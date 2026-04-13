package com.vocabmaxxing.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Users : Table("users") {
    val id = varchar("id", 36)  // Firebase UID (28 chars, fits in 36)
    val email = varchar("email", 255).uniqueIndex()
    val xp = integer("xp").default(0)
    val streak = integer("streak").default(0)
    val rpi = double("rpi").default(0.0)
    val lastActiveAt = datetime("last_active_at").nullable()
    val createdAt = datetime("created_at").default(LocalDateTime.now())

    override val primaryKey = PrimaryKey(id)
}

object Words : Table("words") {
    val id = varchar("id", 36)
    val word = varchar("word", 100).uniqueIndex()
    val definition = text("definition")
    val exampleSentence = text("example_sentence")
    val tier = varchar("tier", 20) // Academic, Elite, Professional
    val rarityScore = integer("rarity_score")

    override val primaryKey = PrimaryKey(id)
}

object Attempts : Table("attempts") {
    val id = varchar("id", 36)
    val userId = varchar("user_id", 36).references(Users.id)
    val wordId = varchar("word_id", 36).references(Words.id)
    val sentence = text("sentence")
    val semanticScore = integer("semantic_score").default(0)
    val structuralScore = integer("structural_score").default(0)
    val vocabScore = integer("vocab_score").default(0)
    val grammarScore = integer("grammar_score").default(0)
    val totalScore = integer("total_score").default(0)
    val feedbackText = text("feedback_text").nullable()
    val createdAt = datetime("created_at").default(LocalDateTime.now())

    override val primaryKey = PrimaryKey(id)
}
