package com.example.guideme.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

// Singleton helper for Text-to-Speech
object TTS {
    private var tts: TextToSpeech? = null
    private var ready = false

    // Initialize once per Activity (e.g., in onCreate)
    fun init(context: Context, onReady: (() -> Unit)? = null) {
        if (tts != null) return  // already initialized
        tts = TextToSpeech(context.applicationContext) { status ->
            ready = (status == TextToSpeech.SUCCESS)
            if (ready) {
                tts?.language = Locale.US
                tts?.setSpeechRate(0.9f) // slower for clarity
            }
            onReady?.invoke()
        }
    }

    // Call this to make the app speak
    fun speak(text: String) {
        if (!ready || text.isBlank()) return
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts-${System.currentTimeMillis()}")
    }

    // Free resources (optional, call in onDestroy)
    fun shutdown() {
        tts?.shutdown()
        tts = null
        ready = false
    }
}