package com.pks.downloader

import android.app.Application
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class VideoQuality(val label: String, val height: Int) {
    P1080("1080p", 1080), P720("720p", 720), P480("480p", 480), P360("360p", 360)
}

enum class Format(val label: String) {
    VIDEO("Video (MP4)"), AUDIO("Audio (MP3)")
}

data class VideoMeta(
    val title: String,
    val thumbnail: String?,
    val uploader: String?,
    val durationSec: Int
)

class DownloadViewModel(app: Application) : AndroidViewModel(app) {

    var url by mutableStateOf("")
    var meta by mutableStateOf<VideoMeta?>(null)
    var quality by mutableStateOf(Prefs.quality)
    var format by mutableStateOf(Prefs.format)
    var fetching by mutableStateOf(false)

    private var lastClipLink: String? = null

    val downloading: Boolean get() = Downloader.downloading
    var message: String?
        get() = Downloader.message
        set(v) { Downloader.message = v }

    /** Called when the app comes to the foreground and the clipboard holds a link. */
    fun onClipboardLink(link: String) {
        if (!Prefs.clipboard) return
        if (link == lastClipLink) return
        lastClipLink = link
        if (url.isBlank() && !downloading) url = link
    }

    fun fetchInfo() {
        val link = url.trim()
        if (link.isEmpty() || fetching || downloading) return
        fetching = true
        meta = null
        message = null
        viewModelScope.launch {
            try {
                val pk = getApplication<PkApp>()
                var waited = 0
                while (!pk.engineReady && waited < 120) { delay(500); waited++ }
                if (!pk.engineReady) { message = "Engine is not ready yet. Try again in a moment."; return@launch }

                val info = withContext(Dispatchers.IO) {
                    val req = YoutubeDLRequest(link)
                    req.addOption("--no-playlist")
                    YoutubeDL.getInstance().getInfo(req)
                }
                quality = Prefs.quality
                format = Prefs.format
                meta = VideoMeta(
                    title = info.title ?: "Untitled",
                    thumbnail = info.thumbnail,
                    uploader = info.uploader,
                    durationSec = info.duration
                )
            } catch (e: Exception) {
            message = ErrorHints.friendly(e.message ?: "")
            } finally {
                fetching = false
            }
        }
    }

    fun startDownload() {
        val m = meta ?: return
        if (Downloader.downloading) return
        Downloader.downloading = true
        Downloader.progress = 0f
        Downloader.message = null
        Downloader.activeTitle = m.title
        Downloader.activeThumb = m.thumbnail
        val ctx = getApplication<PkApp>()
        val i = Intent(ctx, DownloadService::class.java)
            .putExtra(DownloadService.EXTRA_URL, url.trim())
            .putExtra(DownloadService.EXTRA_TITLE, m.title)
            .putExtra(DownloadService.EXTRA_THUMB, m.thumbnail)
            .putExtra(DownloadService.EXTRA_QUALITY, quality.name)
            .putExtra(DownloadService.EXTRA_FORMAT, format.name)
        ContextCompat.startForegroundService(ctx, i)
    }

    fun cancel() = Downloader.cancel()
}
