package com.udea.chatbot;

import java.util.List;

public record ChatReply(String reply, List<ToolCallRecord> toolCalls) {
}
