package com.example.guideme.lessons

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

sealed class UserEvent {
    data class TapOnAnchor(val anchorId: String): UserEvent()
    data class TextEntered(val text: String): UserEvent()
    data class SelectOption(val optionId: String): UserEvent()
    data class Toggle(val anchorId: String, val on: Boolean): UserEvent()
}

class LessonViewModel(
    private val repo: LessonsRepository,
    private val userEmail: String
) : ViewModel() {

    var uiState by mutableStateOf(LessonState())
        private set

    // --- tracking for Completion table ---
    private var lessonStartTimeMillis: Long = 0L
    private var errorCount: Int = 0
    private var attempts: Int = 0
    // -------------------------------------

    fun loadLesson(appName: String, lessonId: Int) {
        // new attempt for this lesson
        lessonStartTimeMillis = System.currentTimeMillis()
        errorCount = 0
        attempts += 1
        if (attempts == 0) attempts = 1   // safety, though it should never be 0 now

        viewModelScope.launch {
            val steps = repo.getLessonInstructions(appName, lessonId)
            uiState = LessonState(
                appName = appName,
                lessonId = lessonId,
                steps = steps,
                currentIndex = 0,
                completed = false,
                feedback = null
            )
        }
    }
    fun onUserEvent(evt: UserEvent) {
        val s = uiState
        val step = s.steps.getOrNull(s.currentIndex) ?: return

        val ok = when (step.type) {
            StepType.TapTarget -> {
                val tap = evt as? UserEvent.TapOnAnchor
                tap?.anchorId == step.anchorId
            }

            StepType.EnterText -> {
                val entered = evt as? UserEvent.TextEntered ?: return
                val expected = step.expectedText

                // If we have an expected string, treat "shorter than expected"
                // as "still typing / deleting" -> clear feedback & don't mark error.
                if (!expected.isNullOrBlank() && entered.text.length < expected.length) {
                    if (s.feedback != null) {
                        uiState = s.copy(feedback = null)
                    }
                    return  // don't advance, don't increment errorCount, don't show "Try again"
                }

                when {
                    // generic non-empty check when there's no specific expectedText
                    expected.isNullOrBlank() -> entered.text.isNotBlank()

                    // only compare when length matches expected
                    entered.text.length == expected.length -> entered.text == expected

                    // longer than expected => wrong attempt
                    else -> false
                }
            }

            StepType.Select -> {
                val sel = evt as? UserEvent.SelectOption
                sel?.optionId == step.anchorId
            }

            StepType.Toggle -> {
                val toggle = evt as? UserEvent.Toggle
                toggle?.anchorId == step.anchorId
                // you could later also check toggle.on if needed
            }
        }

        if (!ok) {
            // count mistakes + show feedback, but do NOT advance
            errorCount++
            uiState = s.copy(feedback = "Try again.")

            return
        }

        val next = s.currentIndex + 1
        val justFinished = next >= s.steps.size

        if (justFinished && !s.completed) {
            val durationSeconds =
                ((System.currentTimeMillis() - lessonStartTimeMillis) / 1000).toInt()

            viewModelScope.launch {
                repo.saveCompletion(
                    lessonId = s.lessonId,
                    status = "Completed",
                    timeSpentSeconds = durationSeconds,
                    errorCount = errorCount,
                    attempts = attempts,
                    customerEmail = userEmail
                )
            }
        }

        uiState = s.copy(
            currentIndex = next,
            completed = justFinished,
            feedback = null            // clear any old error message
        )
    }
}
