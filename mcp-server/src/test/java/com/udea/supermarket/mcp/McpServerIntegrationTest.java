package com.udea.supermarket.mcp;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.udea.supermarket.TestcontainersConfiguration;
import com.udea.supermarket.catalog.ProductRepository;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema;
import java.time.Duration;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class McpServerIntegrationTest {

    private static final String LAURA = "3001234567";
    private static final String CARLOS = "3109876543";

    @LocalServerPort
    private int port;

    @Autowired
    private ProductRepository products;

    private final ObjectMapper json = new ObjectMapper();

    private McpSyncClient client;

    @BeforeEach
    void connect() {
        var transport = HttpClientStreamableHttpTransport.builder("http://localhost:" + port).endpoint("/mcp").build();
        client = McpClient.sync(transport).requestTimeout(Duration.ofSeconds(20)).build();
        McpSchema.InitializeResult init = client.initialize();
        assertThat(init.serverInfo().name()).isEqualTo("supermarket-mcp-server");
    }

    @AfterEach
    void disconnect() {
        client.closeGracefully();
    }

    @Test
    void exposesTheSupermarketTools() {
        var names = client.listTools().tools().stream().map(McpSchema.Tool::name).toList();

        assertThat(names).containsExactlyInAnyOrder(
                "listCategories", "searchProducts", "getProduct",
                "findCustomerByPhone", "registerCustomer",
                "createOrder", "addItemToOrder", "removeItemFromOrder", "getOrder",
                "confirmOrder", "cancelOrder", "listCustomerOrders");
    }

    @Test
    void searchesTheCatalogIgnoringAccents() throws Exception {
        var result = call("searchProducts", Map.of("query", "cafe"));

        assertThat(result.isError()).isFalse();
        assertThat(parse(result).findValuesAsText("sku")).containsExactly("BEB-001");
    }

    @Test
    void placesAndCancelsAnOrderAdjustingStock() throws Exception {
        int milkBefore = stockOf("LAC-001");
        long orderId = parse(call("createOrder", Map.of("customerPhone", LAURA))).get("orderId").asLong();

        call("addItemToOrder", Map.of("customerPhone", LAURA, "orderId", orderId, "sku", "LAC-001", "quantity", 3));
        call("addItemToOrder", Map.of("customerPhone", LAURA, "orderId", orderId, "sku", "BEB-001", "quantity", 1));
        JsonNode confirmed = parse(call("confirmOrder", Map.of("customerPhone", LAURA, "orderId", orderId, "paymentMethod", "CARD_ON_DELIVERY")));

        assertThat(confirmed.get("status").asText()).isEqualTo("CONFIRMED");
        assertThat(confirmed.get("subtotalCop").asInt()).isEqualTo(33600);
        assertThat(confirmed.get("deliveryFeeCop").asInt()).isEqualTo(7000);
        assertThat(confirmed.get("totalCop").asInt()).isEqualTo(40600);
        assertThat(stockOf("LAC-001")).isEqualTo(milkBefore - 3);

        JsonNode cancelled = parse(call("cancelOrder", Map.of("customerPhone", LAURA, "orderId", orderId)));

        assertThat(cancelled.get("status").asText()).isEqualTo("CANCELLED");
        assertThat(stockOf("LAC-001")).isEqualTo(milkBefore);
    }

    @Test
    void reportsBusinessRuleViolationsAsToolErrors() {
        var result = call("findCustomerByPhone", Map.of("phone", "3000000000"));

        assertThat(result.isError()).isTrue();
        assertThat(text(result)).contains("No customer registered with phone 3000000000");
    }

    @Test
    void doesNotExposeOrdersOfOtherCustomers() throws Exception {
        long orderId = parse(call("createOrder", Map.of("customerPhone", CARLOS))).get("orderId").asLong();

        var result = call("getOrder", Map.of("customerPhone", LAURA, "orderId", orderId));

        assertThat(result.isError()).isTrue();
        assertThat(text(result)).contains("was not found for customer phone " + LAURA);
    }

    @Test
    void registersNewCustomersWithNormalizedPhones() throws Exception {
        JsonNode customer = parse(call("registerCustomer", Map.of(
                "fullName", "Sofía Ramírez",
                "phone", "+57 315 555 0101",
                "address", "Calle 10 # 43-12",
                "city", "Medellín")));

        assertThat(customer.get("phone").asText()).isEqualTo("3155550101");
        assertThat(call("registerCustomer", Map.of(
                "fullName", "Duplicate",
                "phone", "3155550101",
                "address", "Calle 1",
                "city", "Medellín")).isError()).isTrue();
    }

    private McpSchema.CallToolResult call(String tool, Map<String, Object> arguments) {
        return client.callTool(new McpSchema.CallToolRequest(tool, arguments));
    }

    private JsonNode parse(McpSchema.CallToolResult result) throws Exception {
        assertThat(result.isError()).as(text(result)).isFalse();
        return json.readTree(text(result));
    }

    private static String text(McpSchema.CallToolResult result) {
        return ((McpSchema.TextContent) result.content().getFirst()).text();
    }

    private int stockOf(String sku) {
        return products.findBySku(sku).orElseThrow().getStock();
    }
}
