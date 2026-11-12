package com.pks.downloader

/** Turns raw yt-dlp errors into a short, helpful message. */
object ErrorHints {
    private const val BLOCKED_MSG =
        "YouTube is blocking this internet connection. " +
            "Turn on a VPN (e.g. Cloudflare 1.1.1.1 WARP) or switch to mobile data, then try again."

    private fun isBlocked(raw: String): Boolean {
        val t = raw.lowercase()
        return "429" in t ||
            "too many requests" in t ||
            "http error 400" in t ||
            "precondition check failed" in t ||
            "sign in to confirm" in t
    }

    /** For errors while reading a link. */
    fun friendly(raw: String): String =
        if (isBlocked(raw)) BLOCKED_MSG else "Could not read this link: ${raw.take(160)}"

    /** For errors while downloading. */
    fun friendlyDownload(raw: String): String =
        if (isBlocked(raw)) BLOCKED_MSG else "Download failed: ${raw.take(160)}"
}
