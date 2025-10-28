package com.zivalez.tpshudneoforge.core;

public final class TpsTracker {
    private static final int WINDOW = 20; // ~1s worth (assuming 20 tps)
    private static long lastPacketNanos = -1L;
    private static int idx = 0, count = 0;
    private static float[] msptWindow = new float[WINDOW];
    private static volatile float serverProvidedMspt = -1f; // negative = not provided

    private TpsTracker() {}

    public static void onWorldTimePacket() {
        long now = System.nanoTime();
        if (lastPacketNanos > 0) {
            long dt = now - lastPacketNanos; // ns
            // Each world-time packet roughly every 1s => MSPT ≈ (dt/1e6)/20
            float mspt = (float)((dt / 1_000_000.0) / 20.0);
            push(mspt);
        }
        lastPacketNanos = now;
    }

    private static void push(float mspt) {
        msptWindow[idx] = mspt;
        idx = (idx + 1) % WINDOW;
        if (count < WINDOW) count++;
    }

    public static void setServerProvidedMspt(float mspt) {
        serverProvidedMspt = mspt;
    }

    public static float getMspt() {
        if (serverProvidedMspt >= 0) return serverProvidedMspt;
        if (count == 0) return Float.NaN;
        float sum = 0f;
        for (int i=0;i<count;i++) sum += msptWindow[i];
        return sum / count;
    }

    public static float getTps() {
        float mspt = getMspt();
        if (Float.isNaN(mspt) || mspt <= 0) return Float.NaN;
        float tps = 1000f / mspt;
        if (tps > 20f) tps = 20f; // cap visually
        return tps;
    }

    public static void reset() {
        lastPacketNanos = -1L;
        idx = 0; count = 0;
        for (int i=0;i<WINDOW;i++) msptWindow[i] = 0f;
        serverProvidedMspt = -1f;
    }
}