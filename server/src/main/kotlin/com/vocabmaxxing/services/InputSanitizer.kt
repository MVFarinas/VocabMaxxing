package com.vocabmaxxing.services

import java.text.Normalizer

/**
 * Server-side sanitization and validation for the user-submitted sentence.
 *
 * This is the authoritative input gate. The Android client mirrors the
 * invisible-character stripping for instant feedback, but every submission is
 * re-checked here because the client cannot be trusted.
 *
 * Intentionally NOT a character allowlist: legitimate English sentences contain
 * apostrophes, hyphens, curly quotes, em-dashes and accented loanwords
 * (café, naïve). Instead we normalize the text and remove only the characters
 * that have no business in a typed sentence (control + invisible/format chars).
 */
object InputSanitizer {

    const val MIN_LENGTH = 10
    const val MAX_LENGTH = 500

    sealed interface SanitizeResult {
        data class Ok(val clean: String) : SanitizeResult
        data class Rejected(val reason: String) : SanitizeResult
    }

    private val WHITESPACE = Regex("\\s")
    // Control chars (Cc) and format chars (Cf: zero-width space/joiner, BOM,
    // RTL/LTR overrides, etc.), EXCLUDING the standard whitespace controls which
    // are turned into spaces first (so newlines/tabs become word separators
    // rather than being deleted and merging adjacent words).
    private val INVISIBLE = Regex("[\\p{Cc}\\p{Cf}&&[^\\t\\n\\u000B\\f\\r ]]")
    private val SPACE_RUN = Regex(" +")

    fun sanitizeSentence(raw: String): SanitizeResult {
        // 1. Canonical Unicode form so visually identical strings normalize
        //    consistently (e.g. composed vs decomposed accents).
        val normalized = Normalizer.normalize(raw, Normalizer.Form.NFC)

        // 2. Turn every whitespace char (newline, tab, CR, ...) into a plain space
        //    BEFORE stripping invisibles, so word boundaries are preserved.
        val spaced = WHITESPACE.replace(normalized, " ")

        // 3. Remove remaining control + invisible/format characters (zero-width,
        //    BOM, direction overrides, NUL, etc.).
        val withoutInvisible = INVISIBLE.replace(spaced, "")

        // 4. Collapse runs of spaces and trim the ends.
        val clean = SPACE_RUN.replace(withoutInvisible, " ").trim()

        // 5. Length is enforced AFTER cleaning so padding with invisible chars
        //    can't sneak past the bounds.
        if (clean.length < MIN_LENGTH) {
            return SanitizeResult.Rejected("Sentence must be at least $MIN_LENGTH characters.")
        }
        if (clean.length > MAX_LENGTH) {
            return SanitizeResult.Rejected("Sentence must be at most $MAX_LENGTH characters.")
        }

        return SanitizeResult.Ok(clean)
    }
}
