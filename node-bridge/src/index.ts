import axios from "axios";
import WebSocket from "ws";
import { loadConfig } from "./config.js";
import { logError, logInfo, logWarn } from "./logger.js";
import { PerUserRateLimiter } from "./rateLimiter.js";
import type { JavaChatRequest, JavaChatResponse, OpenClawInboundMessage, OpenClawOutboundReply } from "./types.js";

const cfg = loadConfig();
const limiter = new PerUserRateLimiter(cfg.perUserMinIntervalMs);

function safeJsonParse(raw: WebSocket.RawData): unknown | null {
  try {
    const text = typeof raw === "string" ? raw : raw.toString("utf8");
    return JSON.parse(text);
  } catch {
    return null;
  }
}

function isInboundMessage(x: any): x is OpenClawInboundMessage {
  return (
    x &&
    x.type === "message" &&
    typeof x.msgId === "string" &&
    typeof x.from_user_id === "string" &&
    typeof x.content === "string"
  );
}

async function callJava(userId: string, message: string): Promise<string> {
  const url = `${cfg.javaApiBaseUrl.replace(/\/+$/, "")}/api/chat`;
  const body: JavaChatRequest = { userId, message };

  const resp = await axios.post<JavaChatResponse>(url, body, {
    timeout: cfg.javaTimeoutMs,
    headers: { "Content-Type": "application/json" }
  });

  const reply = resp.data?.reply;
  if (typeof reply !== "string" || reply.length === 0) {
    return "系统繁忙，请稍后再试";
  }
  return reply;
}

function buildReply(inMsg: OpenClawInboundMessage, content: string): OpenClawOutboundReply {
  return {
    type: "reply",
    msgId: inMsg.msgId,
    to_user_id: inMsg.from_user_id,
    content
  };
}

function start() {
  let ws: WebSocket | null = null;
  let stopped = false;
  let reconnectAttempt = 0;

  const connect = () => {
    if (stopped) return;

    const attempt = reconnectAttempt;
    const url = cfg.openclawWsUrl;
    logInfo("connecting ws", { url, attempt });

    ws = new WebSocket(url);

    ws.on("open", () => {
      reconnectAttempt = 0;
      logInfo("ws open", { url });
    });

    ws.on("message", async (raw) => {
      const parsed = safeJsonParse(raw);
      if (!parsed) return; // JSON 解析失败 → 忽略
      if (!isInboundMessage(parsed)) return;

      const inMsg = parsed;
      const userId = inMsg.from_user_id;
      const message = inMsg.content;

      // Node 日志要求：msgId, userId, message
      logInfo("inbound", { msgId: inMsg.msgId, userId, message });

      // 限流：1秒/用户（超限直接兜底回复）
      if (!limiter.allow(userId)) {
        const out = buildReply(inMsg, "请求过于频繁，请稍后再试");
        try {
          ws?.send(JSON.stringify(out));
        } catch {
          // ignore
        }
        return;
      }

      let replyText = "系统繁忙，请稍后再试";
      try {
        replyText = await callJava(userId, message);
      } catch (err: any) {
        logWarn("java call failed", { msgId: inMsg.msgId, userId, error: String(err?.message ?? err) });
      }

      const out = buildReply(inMsg, replyText);
      try {
        ws?.send(JSON.stringify(out));
      } catch (err: any) {
        logWarn("ws send failed", { msgId: inMsg.msgId, userId, error: String(err?.message ?? err) });
      }
    });

    ws.on("error", (err) => {
      logWarn("ws error", { error: String((err as any)?.message ?? err) });
    });

    ws.on("close", (code, reason) => {
      logWarn("ws closed", { code, reason: reason?.toString?.() ?? String(reason) });
      ws = null;
      if (stopped) return;

      reconnectAttempt += 1;
      const base = cfg.reconnectBaseDelayMs;
      const max = cfg.reconnectMaxDelayMs;
      const delay = Math.min(max, base * Math.pow(2, Math.min(reconnectAttempt, 6)));
      setTimeout(connect, delay);
    });
  };

  connect();

  process.on("SIGINT", () => {
    stopped = true;
    try {
      ws?.close();
    } catch {
      // ignore
    }
    logInfo("stopped");
    process.exit(0);
  });
}

start();

