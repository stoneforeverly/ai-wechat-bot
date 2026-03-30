package com.mybot.backend.repo;

import com.mybot.backend.domain.AppUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {
  Optional<AppUser> findByWxId(String wxId);
}

