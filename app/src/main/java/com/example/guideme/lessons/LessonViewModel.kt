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
            val defaults = repo.getDefaultButtonStates(lessonId)
            uiState = LessonState(
                appName = appName,
                lessonId = lessonId,
                steps = steps,
                currentIndex = 0,
                completed = false,
                feedback = null,
                correctAnchor = steps.getOrNull(0)?.anchorId,
                defaultButtonStates = defaults

            )
        }
    }

    fun onUserEvent(evt: UserEvent) {
        val s = uiState
        val step = s.steps.getOrNull(s.currentIndex) ?: return

        if (step.type == StepType.Acknowledge && evt !is UserEvent.Acknowledge) {
            // Do not set feedback, do not increment errors; just ignore.
            return
        }

        // --- 1. Handle WRONG EVENT TYPE early ---
        val isEventTypeCorrect = when(step.type) {
            StepType.TapTarget -> evt is UserEvent.TapOnAnchor
            StepType.EnterText -> evt is UserEvent.TextEntered
            StepType.Select -> evt is UserEvent.SelectOption
            StepType.Toggle -> evt is UserEvent.Toggle
            StepType.Acknowledge -> evt is UserEvent.Acknowledge
        }

        if (!isEventTypeCorrect) {
            errorCount++
            
            uiState = s.copy(
                feedback = "Try again.",
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
        val nextAnchor = s.steps.getOrNull(next)

        var duration: Int? = null

        if (finished && !s.completed) {
            duration = ((System.currentTimeMillis() - lessonStartTimeMillis) / 1000).toInt()
            val timeValue = duration

            viewModelScope.launch {
                // Save completion record
                repo.saveCompletion(
                    lessonId = s.lessonId,
                    status = "Completed",
                    timeSpentSeconds = timeValue ?: 0,
                    errorCount = errorCount,
                    attempts = attempts,
                    customerEmail = userEmail
                )

                // Compute recommendations and push into state
                val recLessons = computeRecommendations(s.lessonId)
                uiState = uiState.copy(
                    recommendedLessons = recLessons
                )
            }
        }

        uiState = s.copy(
            currentIndex = next,
            completed = finished,
            feedback = null,
            correctAnchor = nextAnchor?.anchorId,
            tappedIncorrectAnchorId = null,
            timeSpentSeconds = duration ?: s.timeSpentSeconds,
            errorCount = errorCount,
            attempts = attempts
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
            StepType.TapTarget,
            StepType.Select,
            StepType.Toggle -> {
                // Only the specific target is allowed
                step.anchorId == anchorId
            }

            StepType.EnterText -> {
                // Typing steps (like lesson 2002)
                when {
                    // Always allow backspace during typing steps
                    anchorId == "DialPad.Backspace" -> true

                    // Free-typing step (lesson 2002 step 11 has anchorId = null) :contentReference[oaicite:2]{index=2}
                    step.anchorId == null -> anchorId.startsWith("DialPad.key")

                    // Guided typing steps (7–10): only the highlighted key is allowed
                    else -> anchorId == step.anchorId
                }
            }

            StepType.Acknowledge -> {
                // User is just reading / pressing OK, don't block fake UI
                false
            }
        }
    }
    fun clearFeedback() {
        val s = uiState
        if (s.feedback != null || s.tappedIncorrectAnchorId != null) {
            uiState = s.copy(
                feedback = null,
                tappedIncorrectAnchorId = null
            )
        }
    }

     //Recommend 3 lessons based on current and what user has finished
     private suspend fun computeRecommendations(currentLessonId: Int): List<RecommendedLesson> {
         val buckets = listOf(1000, 2000, 3000)          // 1xxx, 2xxx, 3xxx groups
         val base = (currentLessonId / 1000) * 1000      // e.g. 2000
         val index = currentLessonId % 1000              // e.g. 1, 2, 3
         val email = userEmail

         val recIds = mutableListOf<Int>()

         // 1) Next lesson in same app (if any)
         if (index > 0) {
             recIds.add(base + index + 1)
         }

         // 2) For each other bucket, intro (…1) or …2 if intro is done
         for (bucket in buckets) {
             if (bucket == base) continue

             val introId = bucket + 1          // 1001, 2001, 3001
             val introDone = repo.hasCompletedLesson(introId, email)
             val candidate = if (!introDone) introId else introId + 1

             if (!recIds.contains(candidate)) {
                 recIds.add(candidate)
             }
         }

         // Map to names + app names
         val recLessons = recIds.take(3).mapNotNull { id ->
             val db = repo.getLessonById(id) ?: return@mapNotNull null
             val appName = when (id / 1000) {
                 1 -> "Camera"
                 2 -> "Phone"
                 3 -> "WiFi"
                 else -> "Phone"
             }
             RecommendedLesson(
                 id = id,
                 appName = appName,
                 name = db.name
             )
         }

         return recLessons
     }


}
