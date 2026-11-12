package com.pks.downloader

/** Turns raw yt-dlp errors into a short, helpful message. */
object ErrorHints {
    fun friendly(raw: String): String {
        val t = raw.lowercase()
        val blocked = "429" in t ||
            "too many requests" in t ||
            "http error 400" in t ||
            "precondition check failed" in t ||
            "sign in to confirm" in t
        return if (blocked) {
            "YouTube is blocking this internet connection. " +
                "Turn on a VPN (e.g. Cloudflare 1.1.1.1 WARP) or switch to mobile data, then try again."
        } else {
            "Could not read this link: ${raw.take(160)}"
        }
    }
}
