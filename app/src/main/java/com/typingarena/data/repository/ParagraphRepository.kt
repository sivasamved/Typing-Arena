package com.typingarena.data.repository

import com.typingarena.data.model.Difficulty
import kotlin.random.Random

object ParagraphRepository {

    private val easyParagraphs = listOf(
        "the quick brown fox jumps over the lazy dog every single morning without fail.",
        "simple habits built day after day lead to great success over time.",
        "focus on accuracy first then speed will naturally follow as you practice typing.",
        "smooth water runs deep and steady progress wins the race in the end.",
        "keep typing every day to train your muscle memory and build speed."
    )

    private val mediumParagraphs = listOf(
        "Software development requires persistent learning, curiosity, and attention to subtle logic details.",
        "Modern mobile applications deliver seamless experiences using reactive UI components and background synchronization.",
        "Consistent daily typing practice enhances productivity, reduces cognitive friction, and sharpens mental focus.",
        "Great design is not just what it looks like and feels like. Design is how it works under the hood.",
        "Data structures and algorithms form the bedrock of computer science and scalable system architecture."
    )

    private val hardParagraphs = listOf(
        "System.out.println(\"WPM > 100!\"); // Accuracy: 99.8% | Latency: <15ms -- #TypingMaster_2026",
        "Functional programming promotes immutability, pure functions, and monads like Optional<T> & Result<R>.",
        "Regex pattern match: ^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$ (validates 100% email formats!).",
        "Version 3.14-beta: Fixed memory leak in CoroutineScope(Dispatchers.IO + SupervisorJob()).",
        "Cryptography primitives: SHA-256 hash = 0x5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8;"
    )

    fun getRandomParagraph(difficulty: Difficulty): String {
        val list = when (difficulty) {
            Difficulty.EASY -> easyParagraphs
            Difficulty.MEDIUM -> mediumParagraphs
            Difficulty.HARD -> hardParagraphs
        }
        return list[Random.nextInt(list.size)]
    }

    fun getDailyChallengeParagraph(): String {
        return "Daily Challenge 2026: Fast fingers and flawless precision conquer every typing arena with 100% mastery!"
    }
}
