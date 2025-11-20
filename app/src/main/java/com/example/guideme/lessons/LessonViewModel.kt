package com.example.guideme.lessons

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

sealed class UserEvent {
    data class TapOnAnchor(val anchorId: String): UserEvent()
    data class TextEntered(val text: String): UserEvent()
    data class SelectOption(val optionId: String): UserEvent()
    data class Toggle(val anchorId: String, val on: Boolean): UserEvent()

    object Acknowledge : UserEvent()
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
                feedback = null,
                correctAnchor = steps.getOrNull(0)?.anchorId,

            )
        }
    }

    fun onUserEvent(evt: UserEvent) {
        val s = uiState
        val step = s.steps.getOrNull(s.currentIndex) ?: return



        // --- 1. Handle WRONG EVENT TYPE early ---
        val isEventTypeCorrect = when(step.type) {
            StepType.TapTarget -> evt is UserEvent.TapOnAnchor
            StepType.EnterText -> evt is UserEvent.TextEntered
            StepType.Select -> evt is UserEvent.SelectOption
            StepType.Toggle -> evt is UserEvent.Toggle
            StepType.Acknowledge -> evt is UserEvent.Acknowledge
        }

        if (!isEventTypeCorrect) {
            // user pressed a totally unrelated button (wrong type)
            uiState = s.copy(feedback = ".",
                tappedIncorrectAnchorId = buildWrongTapId(evt)
            )
            return
        }

        // --- 2. Now safe to cast to correct event type ---
        val ok = when (step.type) {

            StepType.TapTarget -> {
                val tap = evt as UserEvent.TapOnAnchor
                tap.anchorId == step.anchorId
            }

            StepType.EnterText -> {
                val entered = evt as UserEvent.TextEntered
                val expected = step.expectedText

                if (!expected.isNullOrBlank()) {
                    when {
                        entered.text.length < expected.length -> {
                            // still typing, no wrong action yet
                            if (s.feedback != null) {
                                uiState = s.copy(feedback = null)
                            }
                            return
                        }
                        entered.text.length == expected.length ->
                            entered.text == expected
                        else ->
                            false
                    }
                } else {
                    entered.text.isNotBlank()
                }
            }

            StepType.Select -> {
                val sel = evt as UserEvent.SelectOption
                sel.optionId == step.anchorId
            }

            StepType.Toggle -> {
                val toggle = evt as UserEvent.Toggle

                // *** THIS IS THE NEW LOGIC ***
                val correctAnchor = toggle.anchorId == step.anchorId
                if (!correctAnchor) {
                    // User toggled the wrong switch
                    false
                } else {
                    // The step has an expected state defined (e.g., "true" or "false")
                    if (!step.expectedText.isNullOrBlank()) {
                        // Check if the toggle's state matches the expected state
                        toggle.on == step.expectedText.toBoolean()
                    } else {
                        // If no expected state, just interacting with the right toggle is enough
                        true
                    }
                }
            }

            StepType.Acknowledge -> true
        }

        // ---- 3. Wrong action (right event type, wrong target) ----
        if (!ok) {
            errorCount++
            uiState = s.copy(feedback = "Try again.",
                    tappedIncorrectAnchorId = buildWrongTapId(evt)
            )

            return
        } else {
            uiState = s.copy(feedback = null)
        }

        // ---- 4. Correct action ----
        val next = s.currentIndex + 1
        val finished = next >= s.steps.size
        val nextAnchor = s.steps.getOrNull((next))

        if (finished && !s.completed) {
            val duration = ((System.currentTimeMillis() - lessonStartTimeMillis) / 1000).toInt()

            viewModelScope.launch {
                repo.saveCompletion(
                    lessonId = s.lessonId,
                    status = "Completed",
                    timeSpentSeconds = duration,
                    errorCount = errorCount,
                    attempts = attempts,
                    customerEmail = userEmail
                )
            }
        }

        uiState = s.copy(
            currentIndex = next,
            completed = finished,
            feedback = null,
            correctAnchor = nextAnchor?.anchorId,
            tappedIncorrectAnchorId = null
        )
    }

    private fun buildWrongTapId(evt: UserEvent): String? {
        val base = when (evt) {
            is UserEvent.TapOnAnchor -> evt.anchorId
            is UserEvent.Toggle -> evt.anchorId
            is UserEvent.SelectOption -> evt.optionId
            else -> null
        }
        // Append a timestamp so each wrong tap is unique
        return base?.let { "$it#${System.currentTimeMillis()}" }
    }


    fun isButtonAllowed(anchorId: String): Boolean {
        val step = uiState.steps.getOrNull(uiState.currentIndex) ?: return false
        return when (step.type) {
            StepType.TapTarget, StepType.Select, StepType.Toggle -> step.anchorId == anchorId
            else -> true // other types (Text, Acknowledge) are always allowed
        }
    }
}
