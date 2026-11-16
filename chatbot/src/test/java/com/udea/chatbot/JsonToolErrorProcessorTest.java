package com.udea.chatbot;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.definition.DefaultToolDefinition;
import org.springframework.ai.tool.execution.ToolExecutionException;

class JsonToolErrorProcessorTest {

    private final ObjectMapper mapper = new ObjectMapper();
    private final JsonToolErrorProcessor processor = new JsonToolErrorProcessor(mapper);

    @Test
    void turnsMcpErrorResultsIntoJsonWithTheServerMessage() throws Exception {
        var cause = new IllegalStateException("Error calling tool: [TextContent[annotations=null, text=No customer registered with phone 3205550199. Register the customer first., meta=null]]");

        JsonNode result = mapper.readTree(processor.process(exception(cause)));

        assertThat(result.get("error").asText()).isEqualTo("No customer registered with phone 3205550199. Register the customer first.");
    }

    @Test
    void keepsPlainMessagesUnchanged() throws Exception {
        JsonNode result = mapper.readTree(processor.process(exception(new IllegalStateException("Connection refused"))));

        assertThat(result.get("error").asText()).isEqualTo("Connection refused");
    }

    private static ToolExecutionException exception(Throwable cause) {
        var definition = DefaultToolDefinition.builder().name("findCustomerByPhone").description("d").inputSchema("{}").build();
        return new ToolExecutionException(definition, cause);
    }
}
