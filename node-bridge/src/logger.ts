export function logInfo(msg: string, meta?: Record<string, unknown>) {
  if (meta) {
    console.log(`[INFO] ${msg}`, meta);
    return;
  }
  console.log(`[INFO] ${msg}`);
}

export function logWarn(msg: string, meta?: Record<string, unknown>) {
  if (meta) {
    console.warn(`[WARN] ${msg}`, meta);
    return;
  }
  console.warn(`[WARN] ${msg}`);
}

export function logError(msg: string, meta?: Record<string, unknown>) {
  if (meta) {
    console.error(`[ERROR] ${msg}`, meta);
    return;
  }
  console.error(`[ERROR] ${msg}`);
}

