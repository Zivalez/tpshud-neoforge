package com.zivalez.tpshudneoforge.core;

import com.zivalez.tpshudneoforge.config.ConfigManager;
import com.zivalez.tpshudneoforge.config.TpsHudConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

import java.util.ArrayDeque;
import java.util.Deque;

public final class TpsTracker {
    private static int windowSize = 40;
    private static Deque<Double> samples = new ArrayDeque<>(windowSize);

    private static long lastGameTime = Long.MIN_VALUE;
    private static long lastWallNanos = 0L;

    private static double cachedTps = Double.NaN;

    private TpsTracker() {}

    /** Called from ClientPacketListener mixin when world time packet arrives */
    public static void onWorldTimePacket() {
        sampleNow(true);
    }

    /** Fallback sampling from client tick (when singleplayer or no time packet yet) */
    public static void onClientTickFallback() {
        sampleNow(false);
    }

    private static void sampleNow(boolean fromPacket) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return;
        ClientLevel level = mc.level;
        if (level == null) return;

        // Apply dynamic window size from config
        TpsHudConfig cfg = ConfigManager.get();
        int desired = Math.max(5, Math.min(240, cfg.smoothingWindow));
        if (desired != windowSize) {
            windowSize = desired;
            samples = new ArrayDeque<>(windowSize);
        }

        long gameTime = level.getGameTime();
        long nanos = System.nanoTime();

        if (lastGameTime == Long.MIN_VALUE) {
            lastGameTime = gameTime;
            lastWallNanos = nanos;
            return;
        }

        long dtTicks = gameTime - lastGameTime;
        long dtNanos = nanos - lastWallNanos;
        if (dtTicks <= 0 || dtNanos <= 0) {
            return;
        }

        // Estimate TPS = ticks / seconds
        double seconds = dtNanos / 1_000_000_000.0;
        double tps = dtTicks / seconds;

        if (samples.size() == windowSize) samples.removeFirst();
        samples.addLast(tps);

        // Compute moving average
        double sum = 0.0;
        for (double v : samples) sum += v;
        cachedTps = sum / samples.size();

        lastGameTime = gameTime;
        lastWallNanos = nanos;
    }

    public static float getTps() {
        return Double.isNaN(cachedTps) ? Float.NaN : (float) cachedTps;
    }

    public static float getMspt() {
        if (Double.isNaN(cachedTps)) return Float.NaN;
        if (cachedTps <= 0.0) return Float.POSITIVE_INFINITY;
        return (float) (1000.0 / cachedTps);
    }
}