package com.pks.downloader

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** App settings, saved on the device and observable from Compose. */
object Prefs {
    private var sp: SharedPreferences? = null

    var quality by mutableStateOf(VideoQuality.P1080)
        private set
    var format by mutableStateOf(Format.VIDEO)
        private set
    var theme by mutableStateOf(ThemeMode.DARK)
        private set
    var notifications by mutableStateOf(true)
        private set
    var clipboard by mutableStateOf(true)
        private set
    var treeUri by mutableStateOf<String?>(null)
        private set

    fun init(ctx: Context) {
        val s = ctx.getSharedPreferences("pk_prefs", Context.MODE_PRIVATE)
        sp = s
        quality = runCatching { VideoQuality.valueOf(s.getString("quality", "")!!) }.getOrDefault(VideoQuality.P1080)
        format = runCatching { Format.valueOf(s.getString("format", "")!!) }.getOrDefault(Format.VIDEO)
        theme = runCatching { ThemeMode.valueOf(s.getString("theme", "")!!) }.getOrDefault(ThemeMode.DARK)
        notifications = s.getBoolean("notifications", true)
        clipboard = s.getBoolean("clipboard", true)
        treeUri = s.getString("tree_uri", null)
    }

    @JvmName("applyQuality")
    fun setQuality(v: VideoQuality) { quality = v; sp?.edit()?.putString("quality", v.name)?.apply() }

    @JvmName("applyFormat")
    fun setFormat(v: Format) { format = v; sp?.edit()?.putString("format", v.name)?.apply() }

    @JvmName("applyTheme")
    fun setTheme(v: ThemeMode) { theme = v; sp?.edit()?.putString("theme", v.name)?.apply() }

    @JvmName("applyNotifications")
    fun setNotifications(v: Boolean) { notifications = v; sp?.edit()?.putBoolean("notifications", v)?.apply() }

    @JvmName("applyClipboard")
    fun setClipboard(v: Boolean) { clipboard = v; sp?.edit()?.putBoolean("clipboard", v)?.apply() }

    @JvmName("applyTreeUri")
    fun setTreeUri(v: String?) { treeUri = v; sp?.edit()?.putString("tree_uri", v)?.apply() }
}
