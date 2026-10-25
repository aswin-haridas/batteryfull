# Battery Full 🔋

Native Android app (Kotlin + Jetpack Compose) that plays a random sound from a folder you pick when your phone is charging and the battery reaches a threshold — so you know when to unplug.

## Features
- Foreground service driven by the system `ACTION_BATTERY_CHANGED` broadcast — no polling
- Threshold from 10–100%, plus a sound folder of your choice
- Re-alerts every 30 minutes while still charging; unplugging resets the cooldown
- Settings persist, and the service keeps them if Android restarts it

## Build
Requirements: JDK 17+ and the Android SDK (API 36).

```bash
./gradlew assembleRelease
```

Output: `app/build/outputs/apk/release/app-release.apk` (minified with R8).

```bash
adb install -r app/build/outputs/apk/release/app-release.apk
```

## License
MIT
