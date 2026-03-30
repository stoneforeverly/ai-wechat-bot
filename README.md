# MyBot（MVP）

目标链路：

微信用户 → OpenClaw → **Node Bridge（WebSocket 桥接）** → **Java Backend（业务）** → MySQL（可选 Redis）

## 目录结构

```text
.
├─ java-backend/      # Spring Boot：所有业务逻辑（多租户/绑定/对话）
├─ node-bridge/       # Node.js：仅做 WS↔HTTP 桥接（不写业务逻辑）
└─ sql/
   └─ schema.sql      # MySQL 建表与种子数据
```

## 接口（固定）

Java 后端仅暴露一个业务接口：

- `POST /api/chat`

请求：

```json
{
  "userId": "wxid_123",
  "message": "你好"
}
```

响应（固定字段）：

```json
{
  "reply": "你好"
}
```

## 核心业务规则（MVP）

- **用户自动注册**：首次出现的 `userId` 自动创建用户记录。
- **邀请码绑定公司**：当 `message` 以 `#绑定` 开头时触发绑定逻辑（示例：`#绑定 ABC123`）。
- **未绑定提示**：用户未绑定公司时，任何普通消息返回：`请先发送：#绑定 邀请码`
- **正常对话**：已绑定时，MVP 返回：`你说的是：{message}`
- **多租户隔离**：用户通过 `company_id` 隔离（后续扩展 RAG/知识库/对话上下文都必须带 `company_id`）。

## 快速启动（Windows / PowerShell）

### 1) MySQL 建库建表

1. 创建数据库（示例 `mybot`）
2. 执行 `sql/schema.sql`

```powershell
# 进入 MySQL 客户端后执行（示例）
USE mybot;
SOURCE sql/schema.sql;
```

`schema.sql` 内置了两条公司种子数据：

- `ABC123`（默认公司A）
- `XYZ789`（默认公司B）

### 2) 启动 Java Backend

要求：**JDK 17+**、**Maven**、MySQL 可用。

```powershell
cd java-backend

set MYSQL_HOST=localhost
set MYSQL_PORT=3306
set MYSQL_DB=mybot
set MYSQL_USER=root
set MYSQL_PASSWORD=root

mvn spring-boot:run
```

启动后端口默认：`8080`

### 3) 启动 Node Bridge

要求：**Node.js 18+**

```powershell
cd node-bridge
npm i

set OPENCLAW_WS_URL=ws://localhost:9001
set JAVA_API_BASE_URL=http://localhost:8080
set JAVA_TIMEOUT_MS=5000
set PER_USER_MIN_INTERVAL_MS=1000

npm run dev
```

## 联调自测（不依赖 OpenClaw）

### 绑定公司

```powershell
curl -Method POST http://localhost:8080/api/chat `
  -ContentType "application/json" `
  -Body "{\"userId\":\"wxid_test\",\"message\":\"#绑定 ABC123\"}"
```

### 正常对话

```powershell
curl -Method POST http://localhost:8080/api/chat `
  -ContentType "application/json" `
  -Body "{\"userId\":\"wxid_test\",\"message\":\"你好\"}"
```

## 常见问题

- **Maven 不可用**：本机需要安装 Maven（命令 `mvn` 可用）与 JDK17。
- **JPA 启动报 ddl validate**：请确认已先执行 `sql/schema.sql`，且 `MYSQL_DB` 指向正确数据库。

