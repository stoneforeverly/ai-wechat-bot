package com.mybot.backend.service;

import com.mybot.backend.domain.AppUser;
import com.mybot.backend.repo.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;

  public Optional<AppUser> findByWxId(String wxId) {
    if (wxId == null) return Optional.empty();
    String normalized = wxId.trim();
    if (normalized.isEmpty()) return Optional.empty();
    return userRepository.findByWxId(normalized);
  }

  @Transactional
  public AppUser getOrCreateByWxId(String wxId) {
    String normalized = wxId == null ? "" : wxId.trim();
    return userRepository.findByWxId(normalized).orElseGet(() -> {
      AppUser u = new AppUser();
      u.setWxId(normalized);
      return userRepository.save(u);
    });
  }

  @Transactional
  public AppUser bindCompany(AppUser user, long companyId) {
    user.setCompanyId(companyId);
    return userRepository.save(user);
  }
}

