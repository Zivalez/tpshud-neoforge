package com.zivalez.tpshudneoforge.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

import java.util.ArrayDeque;
import java.util.Deque;

public final class TpsTracker {
    private static final int WINDOW = 40;
    private static final Deque<Double> samples = new ArrayDeque<>(WINDOW);

    private static long lastGameTime = Long.MIN_VALUE;
    private static long lastWallNanos = 0L;

    private static double cachedTps = Double.NaN;

    private TpsTracker() {}

    public static void onWorldTimePacket() {
        sampleNow();
    }

    public static void onClientTickFallback() {
        sampleNow();
    }

    private static void sampleNow() {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null) return;

        long gt = level.getGameTime();
        long now = System.nanoTime();

        if (lastGameTime != Long.MIN_VALUE) {
            long dtTicks = gt - lastGameTime;
            long dtNanos = now - lastWallNanos;

            if (dtTicks > 0 && dtNanos > 0) {
                double tps = (dtTicks * 1_000_000_000d) / dtNanos;
                if (tps > 20.0) tps = 20.0;
                pushSample(tps);
            }
        }

        lastGameTime = gt;
        lastWallNanos = now;
    }

    private static void pushSample(double tps) {
        if (samples.size() == WINDOW) samples.removeFirst();
        samples.addLast(tps);

        double sum = 0.0;
        for (double v : samples) sum += v;
        cachedTps = sum / samples.size();
    }

    public static float getTps() {
        return Double.isNaN(cachedTps) ? Float.NaN : (float) cachedTps;
    }

    public static float getMspt() {
        if (Double.isNaN(cachedTps)) return Float.NaN;
        if (cachedTps <= 0.0) return Float.POSITIVE_INFINITY;
        return (float) (1000.0 / cachedTps);
    }

    public static String getFormatted() {
        if (Double.isNaN(cachedTps)) return "--";
        return String.format("%.2f", cachedTps);
    }
}
