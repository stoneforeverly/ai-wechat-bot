package com.mybot.backend.service;

import com.mybot.backend.domain.AppUser;
import com.mybot.backend.domain.Company;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
  private static final String BIND_PREFIX = "#绑定";

  private final UserService userService;
  private final CompanyService companyService;
  private final MessageLogService messageLogService;

  public String handle(String userId, String message) {
    long start = System.nanoTime();
    String trimmedMessage = message == null ? "" : message.trim();
    Long companyIdForLog = null;

    try {
      AppUser user = userService.getOrCreateByWxId(userId);
      companyIdForLog = user.getCompanyId();

      // 记录 inbound（msgId 在 Node 侧，不在 /api/chat 请求字段里，这里留空）
      messageLogService.logInbound(user.getId(), user.getCompanyId(), null, trimmedMessage);

      // 绑定指令：#绑定 ABC123
      if (isBindCommand(trimmedMessage)) {
        String code = extractInviteCode(trimmedMessage);
        if (code == null || code.isBlank()) {
          return "绑定失败：邀请码不能为空。请发送：#绑定 邀请码";
        }

        Company company = companyService.findByInviteCode(code).orElse(null);
        if (company == null) {
          return "绑定失败：邀请码无效。请检查后重试。";
        }

        userService.bindCompany(user, company.getId());
        companyIdForLog = company.getId();
        return "绑定成功：" + safeCompanyName(company) + "（company_id=" + company.getId() + "）";
      }

      // 未绑定提示
      if (user.getCompanyId() == null) {
        return "请先发送：#绑定 邀请码";
      }

      // 正常对话（MVP）
      Long companyId = user.getCompanyId();
      companyIdForLog = companyId;
      return answer(companyId, trimmedMessage);
    } finally {
      long elapsedMs = Duration.ofNanos(System.nanoTime() - start).toMillis();
      log.info("chat userId={} companyId={} latencyMs={}", userId, companyIdForLog, elapsedMs);
    }
  }

  private String answer(Long companyId, String message) {
    // 预留：未来替换为 RAG + Redis 上下文
    // ragService.answer(companyId, message)
    return "你说的是：" + message;
  }

  private boolean isBindCommand(String message) {
    if (message == null) return false;
    String s = message.trim();
    return s.startsWith(BIND_PREFIX);
  }

  private String extractInviteCode(String message) {
    String s = message == null ? "" : message.trim();
    if (!s.startsWith(BIND_PREFIX)) return null;
    String rest = s.substring(BIND_PREFIX.length()).trim();
    if (rest.isEmpty()) return null;
    // rest 可能是 "ABC123" 或 "邀请码 ABC123"，这里只取第一段非空 token
    String[] parts = rest.split("\\s+");
    return parts.length == 0 ? null : parts[0].trim();
  }

  private String safeCompanyName(Company company) {
    if (company == null) return "公司";
    String name = company.getName();
    if (name == null || name.isBlank()) return "公司";
    return name.trim();
  }
}

