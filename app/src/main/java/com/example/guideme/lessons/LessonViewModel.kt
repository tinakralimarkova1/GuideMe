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
    private val repo: LessonsRepository
) : ViewModel() {

    var uiState by mutableStateOf(LessonState())
        private set

    fun loadLesson(appName: String, lessonId: Int) {
        viewModelScope.launch {
            val steps = repo.getLessonInstructions(appName, lessonId)
            uiState = LessonState(appName = appName, lessonId = lessonId, steps = steps)
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

        if (!ok) return

        val next = s.currentIndex + 1
        val done = next >= s.steps.size
        uiState = s.copy(currentIndex = next, completed = done)
    }
}
