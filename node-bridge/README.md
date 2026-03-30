# Node Bridge（TypeScript WebSocket ↔ HTTP）

职责（强约束）：

- 连接 OpenClaw WebSocket
- 接收消息（JSON）
- 调用 Java `POST /api/chat`
- 把 Java 返回的 `reply` 回写到 WS
- 仅做桥接：**不写任何业务逻辑**

## 运行环境

- Node.js：18+

## 协议（严格）

### OpenClaw → Node（输入）

```json
{
  "type": "message",
  "msgId": "string",
  "from_user_id": "string",
  "content": "string"
}
```

### Node → Java（HTTP）

`POST /api/chat`

```json
{
  "userId": "wxid_xxx",
  "message": "用户输入"
}
```

### Java → Node（HTTP 响应）

```json
{
  "reply": "你好"
}
```

### Node → OpenClaw（输出）

```json
{
  "type": "reply",
  "msgId": "string",
  "to_user_id": "string",
  "content": "回复内容"
}
```

## 非功能要求（已实现）

- **超时控制**：调用 Java 超时默认 5 秒（`JAVA_TIMEOUT_MS`）
- **限流**：每用户最小间隔默认 1 秒（`PER_USER_MIN_INTERVAL_MS`）
- **自动重连**：WS 断开后指数退避重连（上限 `RECONNECT_MAX_DELAY_MS`）
- **异常兜底**：
  - JSON 解析失败：忽略
  - Java 调用失败：回复 `系统繁忙，请稍后再试`
- **日志**：记录 `msgId, userId, message`

## 配置（环境变量）

```powershell
set OPENCLAW_WS_URL=ws://localhost:9001
set JAVA_API_BASE_URL=http://localhost:8080

set JAVA_TIMEOUT_MS=5000
set PER_USER_MIN_INTERVAL_MS=1000

set RECONNECT_BASE_DELAY_MS=500
set RECONNECT_MAX_DELAY_MS=10000
```

## 启动

```powershell
cd node-bridge
npm i
npm run dev
```

## 构建与生产运行

```powershell
npm run build
npm start
```

