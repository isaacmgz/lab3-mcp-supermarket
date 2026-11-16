package com.udea.chatbot;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final List<ToolCallbackProvider> toolProviders;

    public ChatService(ChatClient chatClient, ChatMemory chatMemory, List<ToolCallbackProvider> toolProviders) {
        this.chatClient = chatClient;
        this.chatMemory = chatMemory;
        this.toolProviders = toolProviders;
    }

    public ChatReply chat(String conversationId, String message) {
        List<ToolCallRecord> calls = new CopyOnWriteArrayList<>();
        ToolCallback[] tools = mcpTools().stream()
                .map(tool -> new RecordingToolCallback(tool, calls))
                .toArray(ToolCallback[]::new);
        String reply = chatClient.prompt()
                .user(message)
                .toolCallbacks(tools)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
        return new ChatReply(reply, List.copyOf(calls));
    }

    public void reset(String conversationId) {
        chatMemory.clear(conversationId);
    }

    public List<ToolInfo> tools() {
        return mcpTools().stream()
                .map(tool -> new ToolInfo(tool.getToolDefinition().name(), tool.getToolDefinition().description()))
                .sorted(Comparator.comparing(ToolInfo::name))
                .toList();
    }

    private List<ToolCallback> mcpTools() {
        return toolProviders.stream()
                .flatMap(provider -> Arrays.stream(provider.getToolCallbacks()))
                .toList();
    }
}
