package com.example.guideme.lessons

// PURPOSE: stores data classes (data blueprints) needed for lessons.
data class Instruction(
    val stepNo: Int,
    val text: String,
    val anchorId: String?,          // semantic id of target, e.g. "CallButton"
    val type: StepType,             // what user must do
    val outlineColor: Long? = null, // optional color override (ARGB long)
    val expectedText: String? = null // used for EnterText steps
)

enum class StepType { TapTarget, EnterText, Toggle, Select, Acknowledge }

data class RecommendedLesson(
    val id: Int,
    val appName: String,
    val name: String
)

data class LessonState(
    val appName: String = "",
    val lessonId: Int = 0,
    val steps: List<Instruction> = emptyList(),
    val currentIndex: Int = 0,
    val completed: Boolean = false,
    val feedback: String? = null,
    val correctAnchor: String? = null,
    val tappedIncorrectAnchorId: String? = null,
    val defaultButtonStates: Map<String, String> = emptyMap(),
    val timeSpentSeconds: Int? = null,
    val errorCount: Int = 0,
    val attempts: Int = 0,
    val recommendedLessons: List<RecommendedLesson> = emptyList()

)
