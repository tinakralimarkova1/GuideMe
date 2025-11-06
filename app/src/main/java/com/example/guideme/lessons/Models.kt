package com.example.guideme.lessons

// PURPOSE: stores data classes (data blueprints) needed for lessons.
data class Instruction(
    val stepNo: Int,
    val text: String,
    val anchorId: String?,          // semantic id of target, e.g. "CallButton"
    val type: StepType,             // what user must do
    val outlineColor: Long? = null, // optional color override (ARGB long)
)

enum class StepType { TapTarget, EnterText, Toggle, Select }

data class LessonState(
    val appName: String = "",
    val lessonId: Int = 0,
    val steps: List<Instruction> = emptyList(),
    val currentIndex: Int = 0,
    val completed: Boolean = false
)
