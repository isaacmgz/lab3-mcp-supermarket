package com.udea.chatbot;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public ChatReply chat(@Valid @RequestBody ChatRequest request) {
        return chatService.chat(request.conversationId(), request.message().trim());
    }

    @DeleteMapping("/chat/{conversationId}")
    public ResponseEntity<Void> reset(@PathVariable String conversationId) {
        chatService.reset(conversationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tools")
    public List<ToolInfo> tools() {
        return chatService.tools();
    }
}
