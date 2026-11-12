PK'S Downloader - Android Studio project

1. Open this folder in Android Studio (Hedgehog or newer, JDK 17).
2. Let Gradle sync. Internet is needed the first time.
3. Run on a phone (arm64 recommended) or Build > Build APK(s).

Things you may want to edit:
- app/src/main/java/com/pks/downloader/Utils.kt  -> FACEBOOK_URL (put your own profile link)
- app/src/main/res/values/strings.xml             -> app name
- app/src/main/res/mipmap-*                       -> launcher icon
- app/build.gradle.kts                            -> youtubedl-android version (bump if sync fails)
