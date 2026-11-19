package com.music.bitchord.desktop

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DesktopYouTubeVideoTest {
    private val id = "AbCdEfGh123"

    @Test
    fun acceptsVideoIdsAndSupportedWatchLinks() {
        assertEquals(id, DesktopYouTubeVideo.idFromSearchInput(id))
        assertEquals(id, DesktopYouTubeVideo.idFromSearchInput("https://youtu.be/$id?t=5"))
        assertEquals(id, DesktopYouTubeVideo.idFromSearchInput("https://www.youtube.com/watch?v=$id&list=PL123"))
        assertEquals(id, DesktopYouTubeVideo.idFromSearchInput("http://youtube.com/watch?feature=share&v=$id"))
    }

    @Test
    fun rejectsNonVideoLinksAndMalformedIds() {
        assertEquals(null, DesktopYouTubeVideo.idFromSearchInput("https://example.com/watch?v=$id"))
        assertEquals(null, DesktopYouTubeVideo.idFromSearchInput("https://youtube.com/playlist?list=PL123"))
        assertEquals(null, DesktopYouTubeVideo.idFromSearchInput("too-short"))
        assertFalse(DesktopYouTubeVideo.looksLikeVideoInput("Norah Jones Sunrise"))
        assertTrue(DesktopYouTubeVideo.looksLikeVideoInput("https://youtu.be/"))
    }

    @Test
    fun playerDetailsBecomeAVideoOriginSong() {
        val response = Json.parseToJsonElement(
            """
            {
              "videoDetails": {
                "title": "Live Session",
                "author": "Example Channel",
                "lengthSeconds": "3723",
                "thumbnail": {"thumbnails": [
                  {"url": "https://img.youtube.com/low.jpg"},
                  {"url": "https://img.youtube.com/high.jpg"}
                ]}
              }
            }
            """.trimIndent(),
        ).let { it as kotlinx.serialization.json.JsonObject }

        val song = DesktopYouTubeVideo.songFromPlayerResponse(id, response)

        assertEquals(id, song.videoId)
        assertEquals("Live Session", song.title)
        assertEquals("Example Channel", song.artist)
        assertEquals("1:02:03", song.durationText)
        assertEquals("https://img.youtube.com/high.jpg", song.thumbnailUrl)
        assertTrue(song.isVideo)
        assertTrue(song.isVideoOrigin)
    }
}
