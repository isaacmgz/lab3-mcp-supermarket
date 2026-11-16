package com.udea.chatbot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.DefaultToolDefinition;
import org.springframework.ai.tool.definition.ToolDefinition;

class RecordingToolCallbackTest {

    private final List<ToolCallRecord> calls = new ArrayList<>();

    @Test
    void recordsSuccessfulCallsAndReturnsTheDelegateResult() {
        var callback = new RecordingToolCallback(tool("searchProducts", input -> "[{\"sku\":\"LAC-001\"}]"), calls);

        String result = callback.call("{\"query\":\"leche\"}");

        assertThat(result).isEqualTo("[{\"sku\":\"LAC-001\"}]");
        assertThat(calls).containsExactly(new ToolCallRecord("searchProducts", "{\"query\":\"leche\"}", false));
    }

    @Test
    void recordsFailedCallsAndPropagatesTheError() {
        var callback = new RecordingToolCallback(tool("confirmOrder", input -> {
            throw new IllegalStateException("minimum order");
        }), calls);

        assertThatThrownBy(() -> callback.call("{\"orderId\":1}")).hasMessage("minimum order");
        assertThat(calls).containsExactly(new ToolCallRecord("confirmOrder", "{\"orderId\":1}", true));
    }

    @Test
    void exposesTheDelegateDefinition() {
        var callback = new RecordingToolCallback(tool("getOrder", input -> "{}"), calls);

        assertThat(callback.getToolDefinition().name()).isEqualTo("getOrder");
    }

    private static ToolCallback tool(String name, java.util.function.Function<String, String> behavior) {
        ToolDefinition definition = DefaultToolDefinition.builder()
                .name(name)
                .description(name + " description")
                .inputSchema("{\"type\":\"object\"}")
                .build();
        return new ToolCallback() {
            @Override
            public ToolDefinition getToolDefinition() {
                return definition;
            }

            @Override
            public String call(String toolInput) {
                return behavior.apply(toolInput);
            }
        };
    }
}
