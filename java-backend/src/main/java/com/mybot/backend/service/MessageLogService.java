package com.mybot.backend.service;

import com.mybot.backend.domain.MessageLog;
import com.mybot.backend.repo.MessageLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageLogService {
  private final MessageLogRepository messageLogRepository;

  @Transactional
  public void logInbound(Long userId, Long companyId, String msgId, String content) {
    if (userId == null || content == null) return;
    MessageLog log = new MessageLog();
    log.setUserId(userId);
    log.setCompanyId(companyId);
    log.setMsgId(msgId);
    log.setContent(content);
    messageLogRepository.save(log);
  }
}

