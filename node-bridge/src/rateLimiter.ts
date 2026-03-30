export class PerUserRateLimiter {
  private readonly minIntervalMs: number;
  private readonly lastTsByUser: Map<string, number>;

  constructor(minIntervalMs: number) {
    this.minIntervalMs = Math.max(0, minIntervalMs);
    this.lastTsByUser = new Map();
  }

  /**
   * Returns true if allowed right now; false if should be rejected/throttled.
   */
  allow(userId: string, nowMs: number = Date.now()): boolean {
    const key = userId ?? "";
    const last = this.lastTsByUser.get(key);
    if (last == null) {
      this.lastTsByUser.set(key, nowMs);
      return true;
    }
    if (nowMs - last < this.minIntervalMs) {
      return false;
    }
    this.lastTsByUser.set(key, nowMs);
    return true;
  }
}

