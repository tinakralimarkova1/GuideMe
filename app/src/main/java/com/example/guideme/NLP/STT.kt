// class that wraps Android's SpeechRecognizer API

package com.example.guideme.NLP

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

class STT(private val activity: Activity) {

    // instance of Android's built-in SpeechRecognizer
    private var recognizer: SpeechRecognizer? = null
    // emits recognized text strings (both partial and final)
    val results = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    // starts listening to voice input
    fun start(locale: String = "en-US") {

        // stop any ongoing recognizer instance before starting a new one
        stop()

        // attach a listener to receive recognition callbacks
        recognizer = SpeechRecognizer.createSpeechRecognizer(activity).apply {
            setRecognitionListener(object : RecognitionListener {

                // called when final recognition results are ready
                override fun onResults(b: Bundle) {
                    val text = b.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                    text?.let { results.tryEmit(it) }
                }
                // called with interim (partial) speech results as the user is speaking
                override fun onPartialResults(b: Bundle) {
                    val text = b.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                    text?.let { results.tryEmit(it) }
                }

                // called if recognition fails or user cancels
                override fun onError(error: Int) { /* ignore for now */ }

                // detailed state updates
                override fun onReadyForSpeech(p0: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(p0: Float) {}
                override fun onBufferReceived(p0: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onEvent(p0: Int, p1: Bundle?) {}
            })
        }
        // configure the recognition intent, tells Android what kind of speech to capture
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        // start listening using the configured recognizer and intent
        recognizer?.startListening(intent)
    }
    // when done listening
    fun stop() {
        recognizer?.stopListening()
        recognizer?.destroy()
        recognizer = null
    }
}