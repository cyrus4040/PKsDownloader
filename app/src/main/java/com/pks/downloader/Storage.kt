package com.pks.downloader

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.documentfile.provider.DocumentFile
import java.io.File

data class Saved(val uri: String, val size: Long, val name: String)

object Storage {
    const val FOLDER = "PK Downloader"

    fun mimeFor(ext: String): String = when (ext.lowercase()) {
        "mp3" -> "audio/mpeg"
        "m4a" -> "audio/mp4"
        "webm" -> "video/webm"
        "mkv" -> "video/x-matroska"
        else -> "video/mp4"
    }

    fun locationLabel(ctx: Context): String {
        val t = Prefs.treeUri ?: return "Downloads/$FOLDER (default)"
        return runCatching { DocumentFile.fromTreeUri(ctx, Uri.parse(t))?.name }.getOrNull()
            ?.let { "Custom folder: $it" } ?: "Custom folder"
    }

    /** Saves to the folder the user picked, or to Downloads/PK Downloader. */
    fun save(ctx: Context, file: File): Saved? {
        Prefs.treeUri?.let { t ->
            saveToTree(ctx, Uri.parse(t), file)?.let { return it }
        }
        return saveToDownloads(ctx, file)
    }

    private fun saveToTree(ctx: Context, tree: Uri, file: File): Saved? = try {
        val dir = DocumentFile.fromTreeUri(ctx, tree)
        if (dir == null || !dir.canWrite()) null else {
            val doc = dir.createFile(mimeFor(file.extension), file.nameWithoutExtension)
            if (doc == null) null else {
                ctx.contentResolver.openOutputStream(doc.uri)?.use { out ->
                    file.inputStream().use { it.copyTo(out) }
                }
                Saved(doc.uri.toString(), file.length(), file.name)
            }
        }
    } catch (e: Exception) {
        null
    }

    private fun saveToDownloads(ctx: Context, file: File): Saved? {
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, file.name)
            put(MediaStore.Downloads.MIME_TYPE, mimeFor(file.extension))
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/" + FOLDER)
            put(MediaStore.Downloads.IS_PENDING, 1)
        }
        val resolver = ctx.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values) ?: return null
        return try {
            resolver.openOutputStream(uri)?.use { out -> file.inputStream().use { it.copyTo(out) } }
            values.clear()
            values.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
            Saved(uri.toString(), file.length(), file.name)
        } catch (e: Exception) {
            resolver.delete(uri, null, null)
            null
        }
    }
}
