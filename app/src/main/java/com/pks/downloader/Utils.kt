package com.pks.downloader

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

const val FACEBOOK_URL = "https://www.facebook.com/share/1G1iXhbJFM/"
const val INSTAGRAM_URL = "https://instagram.com/p_o_r_o_s"
const val TELEGRAM_URL = "https://t.me/cyrus4040"

fun formatBytes(b: Long): String = when {
    b <= 0L -> "-"
    b < 1024L -> "$b B"
    b < 1024L * 1024 -> "%.0f KB".format(b / 1024.0)
    b < 1024L * 1024 * 1024 -> {
        val mb = b / (1024.0 * 1024.0)
        if (mb >= 100) "%.0f MB".format(mb) else "%.1f MB".format(mb)
    }
    else -> "%.2f GB".format(b / (1024.0 * 1024.0 * 1024.0))
}

fun toast(ctx: Context, msg: String) = Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()

fun openLink(ctx: Context, url: String) {
    try {
        ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    } catch (e: ActivityNotFoundException) {
        toast(ctx, "No app found to open this link")
    }
}

fun openItem(ctx: Context, item: HistoryItem) {
    val uri = item.uri?.let { Uri.parse(it) }
    if (uri == null) { toast(ctx, "File not found"); return }
    val i = Intent(Intent.ACTION_VIEW)
        .setDataAndType(uri, Storage.mimeFor(item.ext.lowercase()))
        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
    try {
        ctx.startActivity(i)
    } catch (e: Exception) {
        toast(ctx, "Can't open this file. It may have been moved or deleted.")
    }
}

fun formatEta(sec: Long): String {
    if (sec <= 0L) return ""
    val h = sec / 3600
    val m = (sec % 3600) / 60
    val s = sec % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
}
