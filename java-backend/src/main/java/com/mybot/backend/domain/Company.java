package com.mybot.backend.domain;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "company", indexes = {
    @Index(name = "idx_company_invite_code", columnList = "invite_code", unique = true)
})
public class Company {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", length = 100)
  private String name;

  @Column(name = "invite_code", length = 20, nullable = false, unique = true)
  private String inviteCode;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt = Instant.now();
}

