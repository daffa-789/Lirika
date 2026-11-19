package com.music.bitchord.desktop

import com.music.bitchord.data.innertube.Innertube
import com.music.bitchord.data.model.Song
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

/** Direct YouTube links accepted in the desktop search box. */
internal object DesktopYouTubeVideo {
    private val idPattern = Regex("""[A-Za-z0-9_-]{11}""")
    private val possibleLinkPattern = Regex(
        """(?i)^https?://(?:www\.)?(?:youtube\.com/watch(?:\?|$)|youtu\.be/)""",
    )

    fun idFromSearchInput(input: String): String? {
        val value = input.trim()
        if (idPattern.matches(value)) return value
        val uri = runCatching { java.net.URI(value) }.getOrNull() ?: return null
        if (uri.scheme?.lowercase() !in setOf("http", "https")) return null
        val host = uri.host?.lowercase() ?: return null
        val pathId = when (host) {
            "youtu.be", "www.youtu.be" -> uri.path.orEmpty().trim('/').substringBefore('/').takeIf(idPattern::matches)
            "youtube.com", "www.youtube.com", "m.youtube.com" -> {
                if (uri.path != "/watch") return null
                uri.rawQuery.orEmpty().split('&').firstNotNullOfOrNull { pair ->
                    val key = pair.substringBefore('=')
                    val candidate = pair.substringAfter('=', "")
                    candidate.takeIf { key == "v" && idPattern.matches(it) }
                }
            }
            else -> null
        }
        return pathId
    }

    fun looksLikeVideoInput(input: String): Boolean =
        idFromSearchInput(input) != null || possibleLinkPattern.containsMatchIn(input.trim())

    suspend fun resolve(videoId: String): Song {
        require(idPattern.matches(videoId)) { "Enter a valid 11-character YouTube video ID." }
        return songFromPlayerResponse(videoId, Innertube.videoPlayerWeb(videoId))
    }

    internal fun songFromPlayerResponse(videoId: String, response: JsonObject): Song {
        require(idPattern.matches(videoId)) { "Enter a valid 11-character YouTube video ID." }
        val details = response["videoDetails"] as? JsonObject
            ?: run {
                val reason = (response["playabilityStatus"] as? JsonObject)?.string("reason")
                error(reason ?: "YouTube did not return details for this video.")
            }
        val title = details.string("title")?.takeIf(String::isNotBlank)
            ?: error("YouTube did not return a title for this video.")
        val author = details.string("author")?.takeIf(String::isNotBlank) ?: "YouTube"
        val seconds = details.string("lengthSeconds")?.toLongOrNull()?.coerceAtLeast(0)
        val thumbnails = (details["thumbnail"] as? JsonObject)?.get("thumbnails") as? JsonArray
        val thumbnail = thumbnails.orEmpty().asSequence()
            .mapNotNull { (it as? JsonObject)?.string("url") }
            .lastOrNull()
            ?: "https://i.ytimg.com/vi/$videoId/hqdefault.jpg"

        return Song(
            videoId = videoId,
            title = title,
            artist = author,
            thumbnailUrl = thumbnail,
            durationText = seconds?.toDurationText(),
            isVideo = true,
            isVideoOrigin = true,
        )
    }

    private fun JsonObject.string(key: String): String? =
        (this[key] as? JsonPrimitive)?.contentOrNull

    private fun Long.toDurationText(): String {
        val hours = this / 3_600
        val minutes = (this % 3_600) / 60
        val seconds = this % 60
        return if (hours > 0) {
            "$hours:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
        } else {
            "$minutes:${seconds.toString().padStart(2, '0')}"
        }
    }
}
