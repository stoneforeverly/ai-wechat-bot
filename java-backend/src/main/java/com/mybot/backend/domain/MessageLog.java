package com.mybot.backend.domain;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "message_log", indexes = {
    @Index(name = "idx_message_log_msg_id", columnList = "msg_id"),
    @Index(name = "idx_message_log_user_id", columnList = "user_id"),
    @Index(name = "idx_message_log_company_id", columnList = "company_id")
})
public class MessageLog {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "msg_id", length = 100)
  private String msgId;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "company_id")
  private Long companyId;

  @Lob
  @Column(name = "content", nullable = false)
  private String content;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt = Instant.now();
}

