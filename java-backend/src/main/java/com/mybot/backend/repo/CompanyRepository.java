package com.mybot.backend.repo;

import com.mybot.backend.domain.Company;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
  Optional<Company> findByInviteCode(String inviteCode);
}

