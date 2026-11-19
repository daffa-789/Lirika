package com.music.bitchord.data.lyrics

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class YouTubeTranscriptLyricsTest {
    @Test
    fun parsesAndOrdersNonemptyTimedCaptionCues() {
        val response = Json.parseToJsonElement(
            """
            {
              "transcript": {"cueGroups": [
                {"transcriptCueRenderer": {"startOffsetMs": "3000", "cue": {"simpleText": "♪"}}},
                {"transcriptCueRenderer": {"startOffsetMs": "2000", "cue": {"simpleText": " second "}}},
                {"transcriptCueRenderer": {"startOffsetMs": "1000", "cue": {"runs": [{"text": "first"}]}}}
              ]}
            }
            """.trimIndent(),
        ).let { it as kotlinx.serialization.json.JsonObject }

        assertEquals(
            listOf(LyricLine(1_000, "first"), LyricLine(2_000, "second")),
            parseTranscriptLines(response),
        )
    }
}
