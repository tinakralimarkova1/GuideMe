package com.example.guideme.NLP

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import org.json.JSONObject
import java.util.Locale
import kotlin.math.max

/**
 * Result of a classification:
 * - label: predicted lesson name
 * - confidence: softmax probability
 */
data class IntentResult(
    val label: String,
    val confidence: Float
)

/**
 * Kotlin wrapper for the TFLite model.
 *
 * Responsibilities:
 * 1. Load TFLite model from assets
 * 2. Load tokenizer (word_index.json)
 * 3. Load labels (labels.json)
 * 4. Preprocess text exactly like Python
 * 5. Run inference using Interpreter
 * 6. Return best prediction + confidence
 */

class IntentClassifier(context: Context) {

    companion object {
        private const val MODEL_PATH = "intent_classifier.tflite"
        private const val WORD_INDEX_PATH = "word_index.json"
        private const val LABELS_PATH = "labels.json"

        // from your notebook
        private const val MAX_LENGTH = 32
        private const val UNKNOWN_TOKEN = "<OOV>"
        private val STOPWORDS = setOf(
            "a","an","the","to","of","and","or","in","on","at","for","with","from","by"
        )
    }

    private val interpreter: Interpreter // TFLite engine
    private val wordIndex: Map<String, Int> // Tokenizer dictionary
    private val labels: List<String> // Label encoder classes
    private val oovIndex: Int // Index for unknown words

    init {

        // Load the TFLite model from assets into memory
        interpreter = Interpreter(loadModelFile(context, MODEL_PATH))
        // Load tokenizer's word index from assets (JSON → Map)
        wordIndex = loadWordIndex(context.assets.open(WORD_INDEX_PATH)
            .bufferedReader().use { it.readText() })

        // Get the integer index of <OOV>
        oovIndex = wordIndex[UNKNOWN_TOKEN] ?: 0

        // Load labels (list of lesson names)
        labels = loadLabels(context.assets.open(LABELS_PATH)
            .bufferedReader().use { it.readText() })
    }

    private fun loadModelFile(context: Context, path: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(path)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            fileDescriptor.startOffset,
            fileDescriptor.declaredLength
        )
    }
    /**
     * Converts word_index.json → Kotlin Map<String, Int>
     */
    private fun loadWordIndex(json: String): Map<String, Int> {
        val obj = JSONObject(json)
        val map = mutableMapOf<String, Int>()
        val it = obj.keys()
        while (it.hasNext()) {
            val k = it.next()
            map[k] = obj.getInt(k)
        }
        return map
    }

    private fun loadLabels(json: String): List<String> {
        // json is ["Taking a picture","Introduction to Camera",...]
        val arr = org.json.JSONArray(json)
        return List(arr.length()) { i -> arr.getString(i) }
    }

    /**
     * Applies the SAME preprocessing as your Python notebook:
     * - lowercase
     * - remove punctuation
     * - remove stopwords
     * - convert tokens → integer IDs
     * - pad/truncate to MAX_LENGTH
     */
    private fun preprocess(text: String): IntArray {
        val lower = text.lowercase(Locale.US)
        val cleaned = lower
            .replace(Regex("[^a-z0-9\\s]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()

        if (cleaned.isBlank()) return IntArray(MAX_LENGTH) { 0 }

        val tokens = cleaned
            .split(" ")
            .filter { it.isNotBlank() && it !in STOPWORDS }

        val seq = IntArray(MAX_LENGTH) { 0 }
        var i = 0
        for (token in tokens) {
            if (i >= MAX_LENGTH) break
            seq[i] = wordIndex[token] ?: oovIndex
            i++
        }
        return seq
    }
    /**
     * Main prediction function:
     * - preprocess text
     * - run TFLite model
     * - pick highest-probability output
     */
    fun classify(text: String): IntentResult? {
        if (text.isBlank()) return null
        val inputSeq = preprocess(text)
        val input = Array<IntArray>(size = 1) { inputSeq }
        val output = Array<FloatArray>(size = 1) { FloatArray(size = labels.size) }

        interpreter.run(input, output)

        val probs = output[0]
        var bestIdx = 0
        var bestScore = Float.NEGATIVE_INFINITY
        for (i in probs.indices) {
            if (probs[i] > bestScore) {
                bestScore = probs[i]
                bestIdx = i
            }
        }

        return IntentResult(
            label = labels[bestIdx],
            confidence = bestScore
        )
    }

    fun close() {
        interpreter.close()
    }
}
