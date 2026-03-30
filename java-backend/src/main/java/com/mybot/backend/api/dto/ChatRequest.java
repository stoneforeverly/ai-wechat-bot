package com.mybot.backend.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequest {
  @NotBlank
  private String userId;

  @NotBlank
  private String message;
}

