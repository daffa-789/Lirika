package com.music.bitchord.data.lyrics

import kotlin.test.Test
import kotlin.test.assertEquals

class LyricsQueryTest {

    @Test
    fun `recording labels stay on the first ask`() {
        assertEquals("Yellow (Remastered 2011)", "Yellow (Remastered 2011)".forLyricsSearch())
    }

    @Test
    fun `recording labels come off only on the relaxed retry`() {
        assertEquals("Yellow", "Yellow (Remastered 2011)".forLyricsSearchRelaxed())
        assertEquals("Dracula", "Dracula (feat. JENNIE) (Sped Up)".forLyricsSearchRelaxed())
        assertEquals("Song", "Song (Live at Wembley)".forLyricsSearchRelaxed())
    }

    @Test
    fun `instrumental and karaoke versions are never stripped`() {
        assertEquals("Song (Instrumental)", "Song (Instrumental)".forLyricsSearchRelaxed())
        assertEquals("Song (Karaoke)", "Song (Karaoke)".forLyricsSearchRelaxed())
    }
}
