package com.mybot.backend.api;

import com.mybot.backend.api.dto.ChatRequest;
import com.mybot.backend.api.dto.ChatResponse;
import com.mybot.backend.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ChatController {
  private final ChatService chatService;

  @PostMapping(path = "/chat", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
    String reply = chatService.handle(request.getUserId(), request.getMessage());
    return new ChatResponse(reply);
  }
}

