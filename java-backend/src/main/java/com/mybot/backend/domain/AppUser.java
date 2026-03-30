package com.mybot.backend.domain;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user", indexes = {
    @Index(name = "idx_user_wx_id", columnList = "wx_id", unique = true),
    @Index(name = "idx_user_company_id", columnList = "company_id")
})
public class AppUser {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "wx_id", length = 100, nullable = false, unique = true)
  private String wxId;

  @Column(name = "company_id")
  private Long companyId;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt = Instant.now();
}

