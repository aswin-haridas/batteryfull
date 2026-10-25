# CallerInfo 📞📊

Pure native Android Call Analyzer app built with **Kotlin** and **Jetpack Compose** (Material 3).

Analyzes phone call history to calculate how long each contact has called, incoming vs. outgoing distributions, call frequency, and time trends.

---

## 🎨 Design

Designed to match the specification in [`DESIGN.md`](./DESIGN.md):
- **Color Palette**: Electric Lime (`#E7FE55`), Soft Vanilla (`#EFF0A3`), Mint Honeydew (`#CFDECA`), Alice Blue (`#D8DFE9`), Dark Surface (`#212121`), Ghost White Scaffold (`#F6F5FA`).
- **Typography & Layout**: Clean modern hierarchy, flat surface contrast, 24dp rounded cards, 9999dp pill chips.
- **Top App Bar**: Quick switch between Live Data and interactive Demo Data preview.
- **Segmented Time Filters**: *Today*, *This Week*, *This Month*, *This Year*.
- **Composite Progress Card**: Visual breakdown of Outgoing, Incoming, Missed, and Rejected calls.
- **2x2 Metric Grid**: Total talk time, total calls count, connected ratio, and missed calls.
- **Activity Spline Chart**: Canvas-rendered smooth curve tracking call durations and counts across periods.
- **Contact Leaderboard**: Ranked contacts by talk time with percentage of total time, quick breakdown, and tap-to-inspect modal bottom sheet.

---

## 🛠️ Architecture & Tech Stack

- **UI**: 100% Jetpack Compose with Material 3
- **Language**: Kotlin 2.2.20
- **Android Target**: Android 14+ (minSdk 26, compileSdk 36, targetSdk 36)
- **Data Layer**:
  - `CallLogRepository`: Queries `CallLog.Calls` and `ContactsContract.PhoneLookup`
  - `MockCallDataProvider`: Sample data generator for emulators / instant previews
- **Architecture**: MVVM with `AndroidViewModel`, `StateFlow`, and Coroutines

---

## 🚀 Building & Running

### Requirements
- JDK 17 or JDK 21 (e.g., from Android Studio JBR)
- Android SDK (API 34+)

### Compile Debug APK
```bash
./gradlew assembleDebug
```
The output APK is located at:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Compile Release Artifacts
```bash
# Build signed Release APK
./gradlew assembleRelease

# Build Google Play / Galaxy Store App Bundle (AAB)
./gradlew bundleRelease
```
The release output artifacts are located at:
```
app/build/outputs/apk/release/app-release.apk
app/build/outputs/bundle/release/app-release.aab
```

### Install & Grant Permissions via ADB
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Grant permissions directly for development:
adb shell pm grant com.aswin.callerinfo android.permission.READ_CALL_LOG
adb shell pm grant com.aswin.callerinfo android.permission.READ_CONTACTS
```

### Run Tests
```bash
./gradlew testDebugUnitTest
```

---

## 📜 License
MIT
