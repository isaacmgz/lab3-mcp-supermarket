package com.udea.chatbot;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class ChatConfig {

    @Bean
    public ChatMemory chatMemory(@Value("${chatbot.memory.max-messages:40}") int maxMessages) {
        return MessageWindowChatMemory.builder().maxMessages(maxMessages).build();
    }

    @Bean
    public ChatClient chatClient(
            ChatClient.Builder builder,
            ChatMemory chatMemory,
            @Value("classpath:prompts/system.st") Resource systemPrompt) {
        return builder
                .defaultSystem(systemPrompt)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }
}
