package com.example.guideme.lessons

// app/src/main/java/com/example/guideme/lessons/DatabaseSeeder.kt

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseSeeder {

    // Call this from MainActivity AFTER you build the db
    suspend fun seed(db: GuideMeDatabase) = withContext(Dispatchers.IO) {

        // --- Seed Lessons table ---
        val lessonDao = db.lessonDao()
        val existingLessons = lessonDao.getAllLessons()
        if (existingLessons.isEmpty()) {
            lessonDao.insertAll(
                listOf(
                    DbLesson(
                        id = 1,
                        name = "Phone – Calling Basics",
                        difficulty = 1
                    ),
                    DbLesson(
                        id = 2,
                        name = "Wi-Fi – Connect to a network",
                        difficulty = 1
                    )
                    // add more later as needed
                )
            )
        }

        // --- Seed Instructions for lesson 1 ---
        val instructionDao = db.instructionDao()
        val existingInstructions = instructionDao.getInstructionsForLesson(lessonId = 1)
        if (existingInstructions.isEmpty()) {
            instructionDao.insertAll(
                listOf(
                    DbInstruction(
                        lessonsId = 1,
                        stepNo = 1,
                        text = "This is the call button, it is used to call once a number is entered. Tap it",
                        anchorId = "DialPad.Call",
                        type = StepType.TapTarget.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 1,
                        stepNo = 2,
                        text = "Enter the number 123.",
                        anchorId = "DialPad.NumberField",
                        type = StepType.EnterText.name,
                        outlineColor = null,
                        expectedText = "123"
                    ),
                    DbInstruction(
                        lessonsId = 1,
                        stepNo = 3,
                        text = "Tap CALL again to confirm.",
                        anchorId = "DialPad.Call",
                        type = StepType.TapTarget.name,
                        outlineColor = null
                    )
                )
            )
        }

        // --- Seed demo customer ---
        val customerDao = db.customerDao()
        val existingCustomer = customerDao.getCustomer("demo")   // 👈 matches your current code
        if (existingCustomer == null) {
            customerDao.insertCustomer(
                DbCustomer(
                    email = "demo",
                    name = "Demo User",
                    password = "password",   // dev-only
                    city = null,
                    street = null,
                    state = null,
                    buildingNumber = null,
                    phoneNum = null,
                    dateOfBirth = null
                )
            )
        }

        // --- Seed prereqs ---
        val preReqDao = db.preReqDao()
        val existingPrereqs = preReqDao.getPrereqsForLesson(lessonId = 2)
        if (existingPrereqs.isEmpty()) {
            preReqDao.insertAll(
                listOf(
                    DbPreReq(
                        lessonId = 2,    // Wi-Fi
                        prereqId = 1,    // Phone
                        priority = 1
                    )
                )
            )
        }
    }
}

