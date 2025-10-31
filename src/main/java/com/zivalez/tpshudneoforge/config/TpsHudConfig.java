package com.zivalez.tpshudneoforge.config;

import com.google.gson.annotations.SerializedName;

public class TpsHudConfig {
    public enum Anchor { TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT }
    public enum Format { TPS, MSPT, BOTH }
    public enum Mode { COMPACT, DETAIL }

    public static class Thresholds {
        public double tpsWarn = 18.0;
        public double tpsBad  = 10.0;
        public String tpsGoodColor = "#00FF88";
        public String tpsWarnColor = "#FFCC00";
        public String tpsBadColor  = "#FF5555";

        public double msptWarn = 60.0;
        public double msptBad  = 100.0;
        public String msptGoodColor = "#00FF88";
        public String msptWarnColor = "#FFCC00";
        public String msptBadColor  = "#FF5555";
    }

    // Core
    public boolean enabled = true;
    public Anchor anchor = Anchor.TOP_LEFT;
    public int padding = 6;
    public float scale = 1.0f;
    public int precision = 2;
    public Format format = Format.BOTH;
    public int smoothingWindow = 40;
    public boolean autoHideF3 = true;
    public Mode displayMode = Mode.COMPACT;

    // Visual
    public boolean shadow = true;
    public boolean background = true;
    public int textColor = 0xFFFFFF;
    public int valueTextColor = 0x00FF88;

    // Threshold colors
    public Thresholds thresholds = new Thresholds();

    // Legacy fields (kept for compatibility; not used when anchor/padding are present)
    @SerializedName("x") public Integer legacyX = null;
    @SerializedName("y") public Integer legacyY = null;
    @SerializedName("showMspt") public Boolean legacyShowMspt = null;
}