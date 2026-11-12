package com.pks.downloader

import android.app.Application
import android.util.Log
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL

class PkApp : Application() {
    @Volatile var engineReady = false
        private set

    override fun onCreate() {
        super.onCreate()
        Prefs.init(this)
        Downloader.init(this)
        // First launch unpacks python + ffmpeg, so do it off the main thread.
        Thread {
            try {
                YoutubeDL.getInstance().init(this)
                FFmpeg.getInstance().init(this)
            } catch (e: Exception) {
                Log.e("PkApp", "Engine init failed", e)
            }

            // Fetch the latest yt-dlp so sites like YouTube keep working.
            // A failed update (e.g. offline) must not break the app.
            try {
                val status = YoutubeDL.getInstance().updateYoutubeDL(this)
                Log.i("PkApp", "yt-dlp update status: $status")
            } catch (e: Exception) {
                Log.e("PkApp", "yt-dlp update failed", e)
            }

            engineReady = true
        }.start()
    }
}
