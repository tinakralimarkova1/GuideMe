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
                    //-----------Tester lessons------////
                    DbLesson(
                        id = 1,
                        name = "Phone – Calling Basics",
                        difficulty = 1
                    ),
                    DbLesson(
                        id = 2,
                        name = "Wi-Fi – Connect to a network",
                        difficulty = 1
                    ),
                    //----------Camera Lessons---------//
                    DbLesson(
                        id = 1001,
                        name =  "Introduction to Camera",
                        difficulty = 1,

                        ),
                    DbLesson(
                        id = 2002,
                        name =  "Taking a Picture",
                        difficulty = 1,

                        ),
                    DbLesson(
                        id = 2003,
                        name =  "Zooming in",
                        difficulty = 2,

                        ),
                    //-----------Phone Lessons---------//
                    DbLesson(
                        id = 2001,
                        name =  "Introduction to Dial Pad",
                        difficulty = 1,

                    ),
                    DbLesson(
                        id = 2002,
                        name =  "Calling Someone",
                        difficulty = 1,

                        ),
                    DbLesson(
                        id = 2003,
                        name =  "Adding a new contact",
                        difficulty = 2,

                        ),
                    //-----------Wifi Lessons---------//
                    DbLesson(
                        id = 3001,
                        name =  "Introduction to Wifi",
                        difficulty = 1,

                        ),
                    DbLesson(
                        id = 3002,
                        name =  "Turning Wifi On and Off",
                        difficulty = 1,

                        ),
                    DbLesson(
                        id = 3003,
                        name =  "Connecting to the Wifi",
                        difficulty = 2,

                        ),
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
                    //-----------Tester lesson--------//
                    DbInstruction(
                        lessonsId = 1,
                        stepNo = 1,
                        text = "This is the call button, it is used to call once a number is entered. Tap it",
                        anchorId = "DialPad.Call",
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 1,
                        stepNo = 2,
                        text = "Enter the number 123.",
                        anchorId = "DialKey2",
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
                    ),

                    //-------Camera Lesson 1001 Instructions-------
                    DbInstruction(
                        lessonsId = 1001,
                        stepNo = 1,
                        text = "Welcome to the camera app. Let's learn about it.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 1001,
                        stepNo = 2,
                        text = "This is the screen that shows what the camera sees.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 1001,
                        stepNo = 3,
                        text = "This is the capture button. Pressing it will take a photo",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 1001,
                        stepNo = 4,
                        text = "This is the photo library button. Pressing it will open your gallery where your photos are stored",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 1001,
                        stepNo = 5,
                        text = "This is the zoom slider. Sliding it up will zoom in",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 1001,
                        stepNo = 6,
                        text = "This is the flash button. Turning on the flash can help you capture photos more clearly in the dark",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 1001,
                        stepNo = 7,
                        text = "Good job! You have now been familiarized with the camera app!",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),


                    //-------Phone Lesson 2001 Instrucitons-------
                    DbInstruction(
                        lessonsId = 2001,
                        stepNo = 1,
                        text = "This is the dial pad screen located in your phone app. Let's learn about it.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 2001,
                        stepNo = 2,
                        text = "This is the dial pad.",
                        anchorId = "DialPad.KeysGrid",
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 2001,
                        stepNo = 3,
                        text = "Each number is button used to enter a phone number. If you press this button, the number 1 will be dialed.",
                        anchorId = "DialPad.key1",
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 2001,
                        stepNo = 4,
                        text = "This is where the numbers you dial will appear",
                        anchorId = "DialPad.NumberField",
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 2001,
                        stepNo = 5,
                        text = "Once you enter a number, pressing the call button will dial the number",
                        anchorId = "DialPad.Call",
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 2001,
                        stepNo = 6,
                        text = "In case you dial the wrong numbers, pressing the backspace deletes the last number dialed",
                        anchorId = "DialPad.Backspace",
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 2001,
                        stepNo = 7,
                        text = "Good job! You have now been familiarized with the dial screen of the phone app!",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),

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

