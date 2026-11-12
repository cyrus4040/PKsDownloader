package com.pks.downloader

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.yausername.youtubedl_android.YoutubeDL
import org.json.JSONArray
import org.json.JSONObject

data class HistoryItem(
    val id: Long,
    val title: String,
    val thumbnail: String?,
    val quality: String,
    val ext: String,
    val sizeBytes: Long,
    val uri: String?
)

/** Shared download state: written by DownloadService, read by the UI. */
object Downloader {
    const val PROCESS_ID = "pk-download"

    var downloading by mutableStateOf(false)
    var progress by mutableStateOf(0f)
    var speed by mutableStateOf("")
    var eta by mutableStateOf("")
    var message by mutableStateOf<String?>(null)
    var activeTitle by mutableStateOf<String?>(null)
    var activeThumb by mutableStateOf<String?>(null)

    var history by mutableStateOf<List<HistoryItem>>(emptyList())
        private set

    private var sp: SharedPreferences? = null

    fun init(ctx: Context) {
        val s = ctx.getSharedPreferences("pk_history", Context.MODE_PRIVATE)
        sp = s
        history = load(s.getString("items", null))
    }

    fun add(item: HistoryItem) {
        history = (listOf(item) + history).take(200)
        save()
    }

    fun clearHistory() {
        history = emptyList()
        save()
    }

    fun cancel() {
        YoutubeDL.getInstance().destroyProcessById(PROCESS_ID)
    }

    private fun save() {
        val arr = JSONArray()
        history.forEach {
            arr.put(JSONObject().apply {
                put("id", it.id); put("title", it.title); put("thumb", it.thumbnail ?: "")
                put("quality", it.quality); put("ext", it.ext); put("size", it.sizeBytes)
                put("uri", it.uri ?: "")
            })
        }
        sp?.edit()?.putString("items", arr.toString())?.apply()
    }

    private fun load(json: String?): List<HistoryItem> {
        if (json.isNullOrEmpty()) return emptyList()
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                HistoryItem(
                    id = o.optLong("id"),
                    title = o.optString("title"),
                    thumbnail = o.optString("thumb").ifEmpty { null },
                    quality = o.optString("quality"),
                    ext = o.optString("ext"),
                    sizeBytes = o.optLong("size"),
                    uri = o.optString("uri").ifEmpty { null }
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
