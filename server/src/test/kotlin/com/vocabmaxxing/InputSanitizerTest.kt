package com.vocabmaxxing

import com.vocabmaxxing.services.InputSanitizer
import com.vocabmaxxing.services.InputSanitizer.SanitizeResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class InputSanitizerTest {

    private fun clean(raw: String): String {
        val result = InputSanitizer.sanitizeSentence(raw)
        assertIs<SanitizeResult.Ok>(result, "expected Ok for: $raw")
        return result.clean
    }

    private fun rejected(raw: String): String {
        val result = InputSanitizer.sanitizeSentence(raw)
        assertIs<SanitizeResult.Rejected>(result, "expected Rejected for: $raw")
        return result.reason
    }

    @Test
    fun `accepts a normal sentence`() {
        val s = "The cat sat quietly on the warm windowsill."
        assertEquals(s, clean(s))
    }

    @Test
    fun `keeps accented loanwords and punctuation`() {
        val s = "She felt naïve ordering a café au lait — it's fine."
        assertEquals(s, clean(s))
    }

    @Test
    fun `strips zero-width characters`() {
        // zero-width space (U+200B) and zero-width joiner (U+200D) padding
        val result = clean("ubiquitous​ technology‍ is everywhere")
        assertEquals("ubiquitous technology is everywhere", result)
    }

    @Test
    fun `strips BOM and direction overrides`() {
        val result = clean("﻿a respectful‮ sentence about words")
        assertTrue(!result.contains('﻿'))
        assertTrue(!result.contains('‮'))
    }

    @Test
    fun `converts newlines and tabs to spaces without merging words`() {
        assertEquals("hello there friend", clean("hello\nthere\tfriend"))
    }

    @Test
    fun `collapses repeated whitespace`() {
        assertEquals("too many spaces here", clean("too   many     spaces  here"))
    }

    @Test
    fun `rejects input shorter than minimum after cleaning`() {
        // 9 visible chars padded with zero-width chars must still be rejected
        rejected("abc​defgh")
    }

    @Test
    fun `rejects whitespace-only input`() {
        rejected("            ")
    }

    @Test
    fun `rejects input longer than maximum`() {
        rejected("a".repeat(InputSanitizer.MAX_LENGTH + 1))
    }

    @Test
    fun `accepts input at the maximum length`() {
        val s = "a".repeat(InputSanitizer.MAX_LENGTH)
        assertEquals(s, clean(s))
    }
}
