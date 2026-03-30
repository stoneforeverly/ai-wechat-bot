package com.mybot.backend.service;

import com.mybot.backend.domain.Company;
import com.mybot.backend.repo.CompanyRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService {
  private final CompanyRepository companyRepository;

  public Optional<Company> findByInviteCode(String inviteCode) {
    if (inviteCode == null) return Optional.empty();
    String normalized = inviteCode.trim();
    if (normalized.isEmpty()) return Optional.empty();
    return companyRepository.findByInviteCode(normalized);
  }
}

