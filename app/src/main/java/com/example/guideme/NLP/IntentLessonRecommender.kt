package com.example.guideme.NLP

// ML-style module that takes user text, finds the closest existing lesson and recommends it
// will be replaced by actual ML model

import kotlin.math.min

class IntentLessonRecommender(
    private val availableLessons: List<String>  // list of lesson names or titles
) {

    // returns the lesson name that best matches the user query
    // if no match score is strong enough, returns null
    fun recommendLesson(userQuery: String): String? {
        val normalized = userQuery.trim().lowercase()
        if (normalized.isBlank()) return null

        var bestLesson: String? = null
        var bestScore = 0.0

        // loop to go through every lesson name and compute a match score
        for (lesson in availableLessons) {
            val score = similarity(normalized, lesson.lowercase())
            if (score > bestScore) {
                bestScore = score
                bestLesson = lesson
            }
        }

        // only return closest lesson if match is above threshold
        // 1.0 = texts are almost identical, 0.0 = totally unrelated
        return if (bestScore >= 0.45) bestLesson else null
    }

    // helper to find similarity score between user query and lesson title
    private fun similarity(a: String, b: String): Double {
        val tokensA = a.split(" ")
        val tokensB = b.split(" ")

        // 1. token overlap ratio (do they share the same words?)
        val overlap = tokensA.count { it in tokensB }.toDouble()
        val tokenScore = overlap / min(tokensA.size, tokensB.size).toDouble()

        // 2. edit distance ratio (how many single-character edits would make them identical?)
        val editDist = levenshtein(a, b)
        val editScore = 1.0 - (editDist.toDouble() / maxOf(a.length, b.length))

        // weighted average
        return (0.6 * tokenScore) + (0.4 * editScore)
    }

    // levenshtein edit distance to count single-character edits
    // to make a and b identical
    private fun levenshtein(a: String, b: String): Int {
        val dp = Array(a.length + 1) { IntArray(b.length + 1) }
        for (i in 0..a.length) dp[i][0] = i
        for (j in 0..b.length) dp[0][j] = j

        for (i in 1..a.length) {
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[a.length][b.length]
    }
}

