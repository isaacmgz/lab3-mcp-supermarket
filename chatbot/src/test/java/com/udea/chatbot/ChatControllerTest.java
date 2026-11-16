package com.udea.chatbot;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ChatService chatService;

    @Test
    void returnsTheAssistantReplyWithTheToolCalls() throws Exception {
        given(chatService.chat("c-1", "quiero leche")).willReturn(new ChatReply(
                "Encontré **Leche entera** a $4.900.",
                List.of(new ToolCallRecord("searchProducts", "{\"query\":\"leche\"}", false))));

        mvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"conversationId\":\"c-1\",\"message\":\"  quiero leche  \"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value("Encontré **Leche entera** a $4.900."))
                .andExpect(jsonPath("$.toolCalls[0].name").value("searchProducts"))
                .andExpect(jsonPath("$.toolCalls[0].failed").value(false));
    }

    @Test
    void rejectsBlankMessages() throws Exception {
        mvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"conversationId\":\"c-1\",\"message\":\"   \"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void mapsAssistantFailuresToBadGateway() throws Exception {
        given(chatService.chat(anyString(), anyString())).willThrow(new IllegalStateException("quota exceeded"));

        mvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"conversationId\":\"c-1\",\"message\":\"hola\"}"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.detail").value("The assistant is temporarily unavailable. Please try again in a moment."));
    }

    @Test
    void listsTheDiscoveredTools() throws Exception {
        given(chatService.tools()).willReturn(List.of(new ToolInfo("searchProducts", "Search active products")));

        mvc.perform(get("/api/tools"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("searchProducts"));
    }

    @Test
    void resetsAConversation() throws Exception {
        mvc.perform(delete("/api/chat/c-1")).andExpect(status().isNoContent());

        verify(chatService).reset("c-1");
    }
}
