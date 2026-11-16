package com.udea.chatbot;

import java.util.List;
import java.util.function.Supplier;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.metadata.ToolMetadata;

public class RecordingToolCallback implements ToolCallback {

    private final ToolCallback delegate;
    private final List<ToolCallRecord> calls;

    public RecordingToolCallback(ToolCallback delegate, List<ToolCallRecord> calls) {
        this.delegate = delegate;
        this.calls = calls;
    }

    @Override
    public ToolDefinition getToolDefinition() {
        return delegate.getToolDefinition();
    }

    @Override
    public ToolMetadata getToolMetadata() {
        return delegate.getToolMetadata();
    }

    @Override
    public String call(String toolInput) {
        return record(toolInput, () -> delegate.call(toolInput));
    }

    @Override
    public String call(String toolInput, ToolContext toolContext) {
        return record(toolInput, () -> delegate.call(toolInput, toolContext));
    }

    private String record(String toolInput, Supplier<String> invocation) {
        String name = getToolDefinition().name();
        try {
            String result = invocation.get();
            calls.add(new ToolCallRecord(name, toolInput, false));
            return result;
        } catch (RuntimeException ex) {
            calls.add(new ToolCallRecord(name, toolInput, true));
            throw ex;
        }
    }
}
