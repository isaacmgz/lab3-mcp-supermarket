package com.udea.chatbot;

public record ToolCallRecord(String name, String arguments, boolean failed) {
}
