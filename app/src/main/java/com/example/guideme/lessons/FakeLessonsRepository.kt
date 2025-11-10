package com.example.guideme.lessons

// Simple fake repository used for previews / tests.
// The real app uses RoomLessonsRepository.

class FakeLessonsRepository : LessonsRepository {

    override suspend fun getLessonInstructions(
        appName: String,
        lessonId: Int
    ): List<Instruction> {
        // Hard-coded demo instructions, same idea as before.
        return when (appName) {
            "Phone" -> listOf(
                Instruction(
                    stepNo = 1,
                    text = "Tap the blue CALL button.",
                    anchorId = "DialPad.Call",
                    type = StepType.TapTarget,
                    outlineColor = null
                ),
                Instruction(
                    stepNo = 2,
                    text = "Enter the number 123.",
                    anchorId = "DialPad.NumberField",
                    type = StepType.EnterText,
                    outlineColor = null
                ),
                Instruction(
                    stepNo = 3,
                    text = "Tap CALL again to confirm.",
                    anchorId = "DialPad.CallConfirm",
                    type = StepType.TapTarget,
                    outlineColor = null
                )
            )

            "WiFi" -> listOf(
                Instruction(
                    stepNo = 1,
                    text = "Open Wi-Fi settings.",
                    anchorId = "Wifi.Open",
                    type = StepType.TapTarget,
                    outlineColor = null
                ),
                Instruction(
                    stepNo = 2,
                    text = "Select your network.",
                    anchorId = "Wifi.NetworkItem",
                    type = StepType.Select,
                    outlineColor = null
                ),
                Instruction(
                    stepNo = 3,
                    text = "Toggle Wi-Fi on.",
                    anchorId = "Wifi.Toggle",
                    type = StepType.Toggle,
                    outlineColor = null
                )
            )

            else -> emptyList()
        }
    }

    override suspend fun saveCompletion(
        lessonId: Int,
        status: String,
        timeSpentSeconds: Int,
        errorCount: Int,
        attempts: Int,
        customerEmail: String
    ) {
        // No-op for previews; just log so we know it was called.
        println(
            "FakeLessonsRepository.saveCompletion: " +
                    "lesson=$lessonId status=$status time=$timeSpentSeconds " +
                    "errors=$errorCount attempts=$attempts email=$customerEmail"
        )
    }
}
