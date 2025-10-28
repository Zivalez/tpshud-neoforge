package com.zivalez.tpshudneoforge.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.neoforged.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public final class ConfigManager {
    private static final String FILE_NAME = "tpshud_v3.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static TpsHudConfig INSTANCE = null;

    private ConfigManager() {}

    public static synchronized TpsHudConfig get() {
        if (INSTANCE == null) {
            INSTANCE = load();
        }
        return INSTANCE;
    }

    public static synchronized void save() {
        try {
            File cfgDir = FMLPaths.CONFIGDIR.get().toFile();
            File file = new File(cfgDir, FILE_NAME);
            try (FileWriter fw = new FileWriter(file)) {
                GSON.toJson(INSTANCE == null ? new TpsHudConfig() : INSTANCE, fw);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static TpsHudConfig load() {
        try {
            File cfgDir = FMLPaths.CONFIGDIR.get().toFile();
            File file = new File(cfgDir, FILE_NAME);
            if (!file.exists()) {
                TpsHudConfig def = new TpsHudConfig();
                try (FileWriter fw = new FileWriter(file)) {
                    GSON.toJson(def, fw);
                }
                return def;
            }
            try (FileReader fr = new FileReader(file)) {
                TpsHudConfig cfg = GSON.fromJson(fr, TpsHudConfig.class);
                return cfg != null ? cfg : new TpsHudConfig();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new TpsHudConfig();
        }
    }
}