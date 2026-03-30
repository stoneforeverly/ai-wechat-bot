export type Config = {
  openclawWsUrl: string;
  javaApiBaseUrl: string;
  javaTimeoutMs: number;
  perUserMinIntervalMs: number;
  reconnectBaseDelayMs: number;
  reconnectMaxDelayMs: number;
};

function mustGetEnv(name: string, fallback?: string): string {
  const v = process.env[name] ?? fallback;
  if (!v || v.trim().length === 0) {
    throw new Error(`Missing env: ${name}`);
  }
  return v.trim();
}

function intEnv(name: string, fallback: number): number {
  const raw = process.env[name];
  if (!raw) return fallback;
  const n = Number.parseInt(raw, 10);
  return Number.isFinite(n) ? n : fallback;
}

export function loadConfig(): Config {
  return {
    openclawWsUrl: mustGetEnv("OPENCLAW_WS_URL", "ws://localhost:9001"),
    javaApiBaseUrl: mustGetEnv("JAVA_API_BASE_URL", "http://localhost:8080"),
    javaTimeoutMs: intEnv("JAVA_TIMEOUT_MS", 5000),
    perUserMinIntervalMs: intEnv("PER_USER_MIN_INTERVAL_MS", 1000),
    reconnectBaseDelayMs: intEnv("RECONNECT_BASE_DELAY_MS", 500),
    reconnectMaxDelayMs: intEnv("RECONNECT_MAX_DELAY_MS", 10_000)
  };
}

