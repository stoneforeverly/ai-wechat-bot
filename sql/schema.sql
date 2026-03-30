-- MySQL schema for MyBot
-- Notes:
-- 1) `user` is a reserved-ish keyword in some DBs; use backticks.
-- 2) MVP requires persistence across restart, so all core data is stored in MySQL.

CREATE TABLE IF NOT EXISTS company (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100),
  invite_code VARCHAR(20) NOT NULL UNIQUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `user` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  wx_id VARCHAR(100) NOT NULL UNIQUE,
  company_id BIGINT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_user_company_id (company_id),
  CONSTRAINT fk_user_company
    FOREIGN KEY (company_id) REFERENCES company(id)
    ON UPDATE RESTRICT ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS message_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  msg_id VARCHAR(100),
  user_id BIGINT NOT NULL,
  company_id BIGINT NULL,
  content TEXT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_message_log_msg_id (msg_id),
  INDEX idx_message_log_user_id (user_id),
  INDEX idx_message_log_company_id (company_id),
  CONSTRAINT fk_message_log_user
    FOREIGN KEY (user_id) REFERENCES `user`(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fk_message_log_company
    FOREIGN KEY (company_id) REFERENCES company(id)
    ON UPDATE RESTRICT ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seed companies for quick start (optional)
INSERT INTO company (name, invite_code) VALUES
  ('默认公司A', 'ABC123'),
  ('默认公司B', 'XYZ789')
ON DUPLICATE KEY UPDATE invite_code = invite_code;

