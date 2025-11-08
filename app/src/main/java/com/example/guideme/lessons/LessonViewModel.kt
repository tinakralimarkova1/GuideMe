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
        if (attempts == 0) attempts = 1   // just in case

        viewModelScope.launch {
            val steps = repo.getLessonInstructions(appName, lessonId)
            uiState = LessonState(
                appName = appName,
                lessonId = lessonId,
                steps = steps
            )
        }
    }

    fun onUserEvent(evt: UserEvent) {
        val s = uiState
        val step = s.steps.getOrNull(s.currentIndex) ?: return

        val ok = when (step.type) {
            StepType.TapTarget -> (evt as? UserEvent.TapOnAnchor)?.anchorId == step.anchorId
            StepType.EnterText -> (evt as? UserEvent.TextEntered)?.text == "123" // demo rule
            StepType.Select    -> (evt as? UserEvent.SelectOption)?.optionId == step.anchorId
            StepType.Toggle    -> (evt as? UserEvent.Toggle)?.anchorId == step.anchorId
        }

        if (!ok) {
            // count mistakes when user does the wrong thing
            errorCount++
            return
        }

        val next = s.currentIndex + 1
        val justFinished = next >= s.steps.size

        // If we just finished the lesson (and it wasn't already marked completed),
        // record the completion in the database.
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
                    // temp email until you have real login wiring
                    customerEmail = userEmail
                )
            }
        }

        uiState = s.copy(
            currentIndex = next,
            completed = justFinished
        )
    }
}
