# TPS HUD (NeoForge 1.21.1)

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-green.svg)](https://minecraft.net)
[![NeoForge](https://img.shields.io/badge/NeoForge-21.1.213-orange.svg)](https://neoforged.net)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Version](https://img.shields.io/badge/Version-1.0.0-blue.svg)](../../releases)
[![Build](https://github.com/Zivalez/tpshud-neoforge/actions/workflows/build.yml/badge.svg)](https://github.com/Zivalez/tpshud-neoforge/actions/workflows/build.yml)

**Author:** Zivalez  
**Mod ID:** `tpshudneoforge`  
**MC/Loader:** Minecraft 1.21.1 • NeoForge 21.1.213 • Java 21  
**License:** All Rights Reserved

> **Upstream:** This is a NeoForge port inspired by the original Fabric project **tpshud-fabric** by **mooziii**.  
> Source: https://github.com/mooziii/tpshud-fabric • License: MIT.

---

![TPS HUD – default](docs/screenshots/TPSHUD.jpg)

## ✨ Summary

Lightweight client‑side HUD that shows server performance (**TPS & MSPT**) in‑game.

- Works on **any server**: estimates values from vanilla time‑update packets.
- Optionally reads **official tick data** from compatible servers via **custom payload** for higher accuracy.
- Fully configurable via JSON (position, scale, colors, background, show TPS/MSPT).

_No commands. No gameplay changes. Optional server plugin/mod only if you want “official” MSPT/TPS._

> **Bahasa Indonesia (ringkas):** HUD client ringan untuk menampilkan performa server (TPS & MSPT). Jalan di semua server (estimasi dari paket waktu), dan bisa pakai data resmi via custom payload. Konfigurasi lewat JSON (posisi, skala, warna, background, pilih TPS/MSPT).

---

## 🧩 Features

- **HUD Overlay**: simple text with optional background & shadow  
- **TPS / MSPT Toggle**: choose which value to show  
- **Configurable**: `x`, `y`, `scale`, colors (text/value), background, shadow  
- **Server‑Provided Data** (optional): consumes `tpshudneoforge:tps` payload if available  
- **Safe by default**: falls back to client estimation when no server data is present

---

## 📦 Requirements

- **Java 21**
- **Minecraft 1.21.1**
- **NeoForge 21.1.213** (see `gradle.properties`)

---

## 📥 Installation (Players)

1. Download the released JAR (when available) and place it into:  
   ```
   .minecraft/mods
   ```
2. Start the game once to generate config (see below).  
3. (Optional) If your server supports the custom payload, the HUD will automatically switch to server‑provided MSPT/TPS.

> This mod is **client‑side**. Server plugin/mod is optional and only used for higher accuracy.

---

## ⚙️ Configuration

Config file will be created on first run:

```
.minecraft/config/tpshud_v3.json
```

Example:
```json
{
  "enabled": true,
  "showMspt": false,
  "x": 6,
  "y": 6,
  "scale": 1.0,
  "textColor": 16777215,
  "valueTextColor": 65280,
  "shadow": true,
  "background": true
}
```

- `showMspt = false` → show **TPS**  
- `showMspt = true`  → show **MSPT**  
- `textColor` & `valueTextColor` are **decimal RGB** (e.g. white `0xFFFFFF` = `16777215`)

> Position is controlled by `x`/`y` for now. A drag‑and‑drop UI is planned for a later release.

---

## 🌐 Networking (Optional)

The mod supports custom payloads for higher accuracy (server → client):

- **Handshake**: `tpshudneoforge:handshake`  
  _Record_: no fields (marker payload)

- **Tick Rate**: `tpshudneoforge:tps`  
  _Record_:  
  - `double tickRate` — if `convertedFromTps == true` this is **TPS**, if `false` it is **MSPT**  
  - `boolean convertedFromTps`

Client converts to **MSPT**:
- If `convertedFromTps == true` ⇒ `mspt = 1000 / tickRate`  
- If `false` ⇒ `mspt = tickRate`

Without server payloads, the HUD uses client‑side estimation from the vanilla time‑update packet.

---

## 🧪 Development (Build & Run)

```bash
# Build (creates JAR in build/libs)
./gradlew build

# Run development client
./gradlew runClient
```

> On Windows PowerShell use `gradlew.bat`.

**Project layout (key files):**
```
src/main/java/com/zivalez/tpshudneoforge/
├─ core/TpsTracker.java                 # ring buffer mspt/tps + server-provided override
├─ client/hud/TpsOverlay.java           # HUD rendering (RenderGuiLayerEvent.Post)
├─ mixin/ClientPacketListenerMixin.java # hook ClientboundSetTimePacket (client estimation)
├─ config/TpsHudConfig.java             # config model
├─ config/ConfigManager.java            # JSON load/save (FMLPaths.CONFIGDIR)
└─ net/
   ├─ CommonHandshakePayload.java       # marker payload
   ├─ CommonTickRatePayload.java        # TPS/MSPT payload
   └─ NetInit.java                      # payload registrar (common event)
```

---

## 🛠️ Troubleshooting

- **HUD not visible**  
  - Make sure `enabled = true` in `tpshud_v3.json`  
  - Check game log for mod load issues  
  - Ensure it isn’t obscured by other overlays (try moving `x`/`y`)

- **Value shows `--` initially**  
  - Wait for the first time‑update packet (vanilla) or first server payload

- **TPS reads above 20**  
  - The value is displayed with a visual cap at 20 TPS

---

## 🧭 Roadmap

- [ ] Drag‑and‑drop position screen
- [ ] Simple config GUI
- [ ] Dynamic color when TPS drops
- [ ] Extra layouts (compact / bar meter)

---

## 🙏 Upstream & Credits

This NeoForge port is inspired by **tpshud-fabric** by **mooziii** (MIT).  
- Modrinth page (license & versions): https://modrinth.com/plugin/tps-hud  
- CurseForge page (license & versions): https://www.curseforge.com/minecraft/mc-mods/tps-hud-fabric

Thanks to contributors in upstream PRs/issues for features like MSPT display and fixes.

---

## 📝 Changelog

**1.0.0**
- Initial NeoForge release: TPS/MSPT HUD, JSON config, client estimation, optional server payloads.

---

## 📄 License

**All Rights Reserved** for this NeoForge port (see `TEMPLATE_LICENSE.txt`).  
Port acknowledges the upstream MIT‑licensed project by **mooziii** and includes attribution.  
If you prefer, you can switch this port to MIT to match upstream.
