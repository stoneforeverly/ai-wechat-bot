# Java Backend（Spring Boot）

## 运行环境

- Java：17+
- Maven：3.8+
- MySQL：8.x（建议）
- Redis：可选（MVP 未启用）

## 配置（环境变量）

服务端口：

- `SERVER_PORT`：默认 `8080`

MySQL：

- `MYSQL_HOST`：默认 `localhost`
- `MYSQL_PORT`：默认 `3306`
- `MYSQL_DB`：默认 `mybot`
- `MYSQL_USER`：默认 `root`
- `MYSQL_PASSWORD`：默认 `root`

对应配置见 `src/main/resources/application.yml`。

## 数据库准备

先执行 `../sql/schema.sql` 建表（并写入种子 company 数据）。

注意：本项目 JPA 配置为 `ddl-auto: validate`，数据库不存在表会直接启动失败（符合“服务重启仍可用”的验收要求）。

## API（固定）

### POST `/api/chat`

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

## 业务逻辑（MVP 必须）

### 1) 用户自动注册

当 `userId`（微信 id）在 `user` 表不存在时，自动创建用户：

- `user.wx_id = userId`
- `user.company_id = NULL`

### 2) 邀请码绑定公司

触发条件：`message` 以 `#绑定` 开头。

示例：

- `#绑定 ABC123`

处理：

- 提取邀请码 token（第一段非空字符串）
- 用 `invite_code` 查询 `company`
- 将 `user.company_id` 绑定为 `company.id`
- 返回绑定结果

### 3) 未绑定提示

当 `user.company_id` 为空且不是绑定指令时，返回：

```text
请先发送：#绑定 邀请码
```

### 4) 正常对话（MVP）

当已绑定 `company_id` 时，返回：

```text
你说的是：{message}
```

> 后续可替换为：`ragService.answer(companyId, message)` 并配合 Redis 上下文。

## 多租户（强制）

- 所有业务以 `company_id` 隔离。
- 当前 MVP 以 `user.company_id` 做租户归属，后续扩展（知识库/对话上下文/计费）必须携带 `company_id`。

## 异常处理（统一）

Java 侧任何异常统一返回（HTTP 200，字段不变）：

```json
{
  "reply": "系统繁忙，请稍后再试"
}
```

## 日志

每次请求打印：

- `userId`
- `companyId`（可能为 `null`）
- `latencyMs`

## 启动

```powershell
cd java-backend

set MYSQL_HOST=localhost
set MYSQL_PORT=3306
set MYSQL_DB=mybot
set MYSQL_USER=root
set MYSQL_PASSWORD=root

mvn spring-boot:run
```

## 快速自测（curl）

### 绑定公司

```powershell
curl -Method POST http://localhost:8080/api/chat `
  -ContentType "application/json" `
  -Body "{\"userId\":\"wxid_test\",\"message\":\"#绑定 ABC123\"}"
```

### 未绑定提示

```powershell
curl -Method POST http://localhost:8080/api/chat `
  -ContentType "application/json" `
  -Body "{\"userId\":\"wxid_new\",\"message\":\"你好\"}"
```

### 绑定后对话

```powershell
curl -Method POST http://localhost:8080/api/chat `
  -ContentType "application/json" `
  -Body "{\"userId\":\"wxid_test\",\"message\":\"你好\"}"
```

