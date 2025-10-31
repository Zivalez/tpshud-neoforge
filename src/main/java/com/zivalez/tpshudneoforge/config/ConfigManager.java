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
            INSTANCE = loadOrCreate();
        }
        return INSTANCE;
    }

    public static synchronized void save() {
        try {
            File file = FMLPaths.CONFIGDIR.get().resolve(FILE_NAME).toFile();
            file.getParentFile().mkdirs();
            try (FileWriter fw = new FileWriter(file)) {
                GSON.toJson(INSTANCE != null ? INSTANCE : new TpsHudConfig(), fw);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized void reload() {
        INSTANCE = loadOrCreate();
    }

    private static TpsHudConfig loadOrCreate() {
        try {
            File file = FMLPaths.CONFIGDIR.get().resolve(FILE_NAME).toFile();
            if (!file.exists()) {
                TpsHudConfig def = new TpsHudConfig();
                try (FileWriter fw = new FileWriter(file)) {
                    GSON.toJson(def, fw);
                }
                return def;
            }
            try (FileReader fr = new FileReader(file)) {
                TpsHudConfig cfg = GSON.fromJson(fr, TpsHudConfig.class);
                if (cfg == null) cfg = new TpsHudConfig();
                return cfg;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new TpsHudConfig();
        }
    }
}