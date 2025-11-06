package com.example.guideme.lessons
// later will be changed to a real repo -> room-backed

import kotlinx.coroutines.delay

class FakeLessonsRepository : LessonsRepository {
    override suspend fun getLessonInstructions(appName: String, lessonId: Int): List<Instruction> {
        // simulate IO latency (optional)
        delay(150)

        return when (appName to lessonId) {
            "Phone" to 1 -> listOf(
                Instruction(1, "Tap the green Call button.", "DialPad.Call", StepType.TapTarget),
                Instruction(2, "Enter the number 123.", "DialPad.NumberField", StepType.EnterText),
                Instruction(3, "Tap Call again to confirm.", "DialPad.CallConfirm", StepType.TapTarget),
            )
            "WiFi" to 1 -> listOf(
                Instruction(1, "Open Wi-Fi settings.", "Wifi.Open", StepType.TapTarget),
                Instruction(2, "Select your network.", "Wifi.NetworkItem", StepType.Select),
                Instruction(3, "Toggle Wi-Fi on.", "Wifi.Toggle", StepType.Toggle),
            )
            else -> emptyList()
        }
    }
}
