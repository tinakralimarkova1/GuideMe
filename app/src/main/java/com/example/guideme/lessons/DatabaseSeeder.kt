package com.example.guideme.lessons

// app/src/main/java/com/example/guideme/lessons/DatabaseSeeder.kt

import com.example.guideme.util.HashUtils
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
                        id = 1002,
                        name =  "Taking a Picture",
                        difficulty = 1,

                        ),
                    DbLesson(
                        id = 1003,
                        name =  "Zooming in",
                        difficulty = 2,

                        ),
                    DbLesson(
                        id = 1004,
                        name =  "Flipping the camera",
                        difficulty = 2,

                        ),
                    //-----------Camera Practice-------//
                    // 1001
                    DbLesson(
                        id = 10011,
                        name =  "Introduction to Camera",
                        difficulty = 1,

                        ),
                    //1002
                    DbLesson(
                        id = 10021,
                        name =  "Introduction to Camera",
                        difficulty = 1,

                        ),
                    DbLesson(
                        id = 10022,
                        name =  "Introduction to Camera",
                        difficulty = 1,

                        ),
                    DbLesson(
                        id = 10023,
                        name =  "Introduction to Camera",
                        difficulty = 1,

                        ),

                    //1003
                    DbLesson(
                        id = 10031,
                        name =  "Introduction to Camera",
                        difficulty = 1,

                        ),
                    DbLesson(
                        id = 10032,
                        name =  "Introduction to Camera",
                        difficulty = 1,

                        ),
                    DbLesson(
                        id = 10033,
                        name =  "Introduction to Camera",
                        difficulty = 1,

                        ),
                    // 1004
                    DbLesson(
                        id = 10041,
                        name = "Camera – Lesson 1004 Practice I",
                        difficulty = 2,
                    ),
                    DbLesson(
                        id = 10042,
                        name = "Camera – Lesson 1004 Practice II",
                        difficulty = 2,
                    ),
                    DbLesson(
                        id = 10043,
                        name = "Camera – Lesson 1004 Practice III",
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
                    DbLesson(
                        id = 2004,
                        name =  "Calling a saved contact",
                        difficulty = 2,

                        ),

                    //-----------Phone Practice---------//
// 2001 – Intro to Dial Pad (single practice)
                    DbLesson(
                        id = 20011,
                        name = "Introduction to Dial Pad – Practice I",
                        difficulty = 1,
                    ),

// 2002 – Calling Someone (3 practices)
                    DbLesson(
                        id = 20021,
                        name = "Calling Someone – Practice I",
                        difficulty = 1,
                    ),
                    DbLesson(
                        id = 20022,
                        name = "Calling Someone – Practice II",
                        difficulty = 1,
                    ),
                    DbLesson(
                        id = 20023,
                        name = "Calling Someone – Practice III",
                        difficulty = 1,
                    ),

// 2003 – Adding a new contact (3 practices)
                    DbLesson(
                        id = 20031,
                        name = "Adding a new contact – Practice I",
                        difficulty = 2,
                    ),
                    DbLesson(
                        id = 20032,
                        name = "Adding a new contact – Practice II",
                        difficulty = 2,
                    ),
                    DbLesson(
                        id = 20033,
                        name = "Adding a new contact – Practice III",
                        difficulty = 2,
                    ),

// 2004 – Calling a saved contact (3 practices)
                    DbLesson(
                        id = 20041,
                        name = "Calling a saved contact – Practice I",
                        difficulty = 2,
                    ),
                    DbLesson(
                        id = 20042,
                        name = "Calling a saved contact – Practice II",
                        difficulty = 2,
                    ),
                    DbLesson(
                        id = 20043,
                        name = "Calling a saved contact – Practice III",
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

                    DbLesson(
                        id = 3004,
                        name =  "Open vs Secured Wifi networks",
                        difficulty = 2,

                        ),

                    //-----------Wifi Practice---------//
// 3001 – Intro to Wifi (single practice)
                    DbLesson(
                        id = 30011,
                        name = "Introduction to Wifi – Practice I",
                        difficulty = 1,
                    ),

// 3002 – Turning Wifi On and Off (3 practices)
                    DbLesson(
                        id = 30021,
                        name = "Turning Wifi On and Off – Practice I",
                        difficulty = 1,
                    ),
                    DbLesson(
                        id = 30022,
                        name = "Turning Wifi On and Off – Practice II",
                        difficulty = 1,
                    ),
                    DbLesson(
                        id = 30023,
                        name = "Turning Wifi On and Off – Practice III",
                        difficulty = 1,
                    ),

// 3003 – Connecting to Wifi (3 practices)
                    DbLesson(
                        id = 30031,
                        name = "Connecting to the Wifi – Practice I",
                        difficulty = 2,
                    ),
                    DbLesson(
                        id = 30032,
                        name = "Connecting to the Wifi – Practice II",
                        difficulty = 2,
                    ),
                    DbLesson(
                        id = 30033,
                        name = "Connecting to the Wifi – Practice III",
                        difficulty = 2,
                    ),

// 3004 – Open vs Secured networks (3 practices)
                    DbLesson(
                        id = 30041,
                        name = "Open vs Secured Wifi networks – Practice I",
                        difficulty = 2,
                    ),
                    DbLesson(
                        id = 30042,
                        name = "Open vs Secured Wifi networks – Practice II",
                        difficulty = 2,
                    ),
                    DbLesson(
                        id = 30043,
                        name = "Open vs Secured Wifi networks – Practice III",
                        difficulty = 2,
                    ),


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
                        anchorId = "Camera.Screen",
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 1001,
                        stepNo = 3,
                        text = "This is the capture button. Pressing it will take a photo",
                        anchorId = "Camera.Capture",
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 1001,
                        stepNo = 4,
                        text = "This is the photo library button. Pressing it will open your gallery where your photos are stored",
                        anchorId = "Camera.Gallery",
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 1001,
                        stepNo = 5,
                        text = "This is the zoom slider. Sliding it up will zoom in",
                        anchorId = "Camera.ZoomSlider",
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 1001,
                        stepNo = 6,
                        text = "This is the flash button. Turning on the flash can help you capture photos more clearly in the dark",
                        anchorId = "Camera.FlashButton",
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 1001,
                        stepNo = 7,
                        text = "This is the switch camera button. Pressing it will switch between the front and the back camera.",
                        anchorId = "Camera.SwitchCamera",
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 1001,
                        stepNo = 8,
                        text = "Good job! You have now been familiarized with the camera app!",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    //-------Camera Practice Lesson 10011 Instructions-------
                    DbInstruction(
                        lessonsId = 10011,
                        stepNo = 1,
                        text = "Practice time! You will see the names of camera buttons. Tap the matching button on the screen.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor =  0x00000000L
                    ),
                    DbInstruction(
                        lessonsId = 10011,
                        stepNo = 2,
                        text = "Tap the Capture button.",
                        anchorId = "Camera.Capture",
                        type = StepType.TapTarget.name,
                        // Transparent outline so there is no visible highlight
                        outlineColor = 0x00000000L
                    ),
                    DbInstruction(
                        lessonsId = 10011,
                        stepNo = 3,
                        text = "Now tap the Gallery button.",
                        anchorId = "Camera.Gallery",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),
                    DbInstruction(
                        lessonsId = 10011,
                        stepNo = 4,
                        text = "Find and tap the Zoom slider.",
                        anchorId = "Camera.ZoomSlider",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),
                    DbInstruction(
                        lessonsId = 10011,
                        stepNo = 5,
                        text = "Tap the Flash button.",
                        anchorId = "Camera.FlashButton",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),
                    DbInstruction(
                        lessonsId = 10011,
                        stepNo = 6,
                        text = "Tap the Switch Camera button.",
                        anchorId = "Camera.SwitchCamera",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),
                    DbInstruction(
                        lessonsId = 10011,
                        stepNo = 7,
                        text = "Great job! You’ve practiced finding the main camera buttons.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),


                    //-------Camera tester 1002
                    DbInstruction(
                        lessonsId = 1002,
                        stepNo = 1,
                        text = "This lesson will teach you how to take a picture",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 1002,
                        stepNo = 2,
                        text = "This is the pretend camera app. In the real app, instead of the tree, the real environment would show up.",
                        anchorId = "Camera.Screen",
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 1002,
                        stepNo = 3,
                        text = "Your phone has 2 cameras: one in the front and one in the back. The app will use the back camera by default.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 1002,
                        stepNo = 4,
                        text = "This lesson will teach you how to take a photo, later we will learn how to switch cameras.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 1002,
                        stepNo = 5,
                        text = "To take a photo, you need to press this button. It's called the capture button. Press the capture button to take a photo.",
                        anchorId = "Camera.Capture",
                        type = StepType.TapTarget.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 1002,
                        stepNo = 6,
                        text = "Good job! You have taken a photo",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 1002,
                        stepNo = 7,
                        text = "In the real app, you can press this button, the gallery button, to look through the photos you have taken.",
                        anchorId = "Camera.Gallery",
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 1002,
                        stepNo = 8,
                        text = "Tap the gallery button, it won't do anything since we didn't really take a photo. If you did this in the real app it would open the photo gallery.",
                        anchorId = "Camera.Gallery",
                        type = StepType.TapTarget.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 1002,
                        stepNo = 9,
                        text = "Exellent! You now know how to take a photo and how to view your photo gallery.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    //-------Camera Practice Lesson 10021 – Taking a Picture (with highlights)-------
                    DbInstruction(
                        lessonsId = 10021,
                        stepNo = 1,
                        text = "Practice time! Let's review how to take a photo.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 10021,
                        stepNo = 2,
                        text = "Remember, this is the pretend camera app. In the real app, you would see your surroundings here.",
                        anchorId = "Camera.Screen",
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 10021,
                        stepNo = 3,
                        text = "To take a photo, press the capture button. Tap the capture button now.",
                        anchorId = "Camera.Capture",
                        type = StepType.TapTarget.name,
                        outlineColor = null   // default highlight ON
                    ),

                    DbInstruction(
                        lessonsId = 10021,
                        stepNo = 4,
                        text = "Great! You have taken a photo.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 10021,
                        stepNo = 5,
                        text = "In the real app, you can tap the gallery button to see your photos.",
                        anchorId = "Camera.Gallery",
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 10021,
                        stepNo = 6,
                        text = "Tap the gallery button. Here it won't open a real gallery, but in the real app it would show your photos.",
                        anchorId = "Camera.Gallery",
                        type = StepType.TapTarget.name,
                        outlineColor = null   // default highlight ON
                    ),

                    DbInstruction(
                        lessonsId = 10021,
                        stepNo = 7,
                        text = "Well done! You have practiced taking a photo and opening the gallery.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    //-------Camera Practice Lesson 10022 – Taking a Picture (no highlight, guided)-------
                    DbInstruction(
                        lessonsId = 10022,
                        stepNo = 1,
                        text = "Now let's practice again, this time without the on screen hints.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 10022,
                        stepNo = 2,
                        text = "To take a photo, press the capture button. Find and tap the capture button now.",
                        anchorId = "Camera.Capture",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L   // transparent → no visible highlight
                    ),

                    DbInstruction(
                        lessonsId = 10022,
                        stepNo = 3,
                        text = "Great! You have taken a photo.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 10022,
                        stepNo = 4,
                        text = "Next, open the gallery. Tap the gallery button to practice.",
                        anchorId = "Camera.Gallery",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L   // transparent → no visible highlight
                    ),

                    DbInstruction(
                        lessonsId = 10022,
                        stepNo = 5,
                        text = "Nice work! You can now find the capture and gallery buttons without on-screen hints.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    //-------Camera Practice Lesson 10023 – Taking a Picture (test, no help)-------
                    DbInstruction(
                        lessonsId = 10023,
                        stepNo = 1,
                        text = "For this practice, try to use the camera on your own.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 10023,
                        stepNo = 2,
                        text = "Take a photo using the camera.",
                        anchorId = "Camera.Capture",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L   // no highlight, user must remember capture button
                    ),

                    DbInstruction(
                        lessonsId = 10023,
                        stepNo = 3,
                        text = "Now open your photos.",
                        anchorId = "Camera.Gallery",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L   // no highlight, user must remember gallery button
                    ),

                    DbInstruction(
                        lessonsId = 10023,
                        stepNo = 4,
                        text = "Excellent! You have shown that you can take a photo and open the gallery on your own.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),




                    //-------Camera Lesson 1003: Zooming in-------
                    DbInstruction(
                        lessonsId = 1003,
                        stepNo = 1,
                        text = "This lesson will teach you how to zoom in before taking a picture.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 1003,
                        stepNo = 2,
                        text = "Sometimes your subject is far away. Zooming in makes it look closer without you moving.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 1003,
                        stepNo = 3,
                        text = "This is the zoom slider. Sliding it up will zoom in.",
                        anchorId = "Camera.ZoomSlider",
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 1003,
                        stepNo = 4,
                        text = "Slide the zoom slider up to zoom in.",
                        anchorId = "Camera.ZoomSlider",
                        type = StepType.TapTarget.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 1003,
                        stepNo = 5,
                        text = "Great job. Now the picture looks closer because you zoomed in.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 1003,
                        stepNo = 6,
                        text = "Now tap the capture button to take a zoomed-in photo.",
                        anchorId = "Camera.Capture",
                        type = StepType.TapTarget.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 1003,
                        stepNo = 7,
                        text = "Excellent! You have learned how to zoom in and take a picture.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    //-------Camera Practice Lesson 10031 – Zooming in (with highlights)-------
                    DbInstruction(
                        lessonsId = 10031,
                        stepNo = 1,
                        text = "Practice time! Let's review how to zoom in before taking a picture.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 10031,
                        stepNo = 2,
                        text = "This is the zoom slider. Sliding it up makes things look closer.",
                        anchorId = "Camera.ZoomSlider",
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 10031,
                        stepNo = 3,
                        text = "Slide the zoom slider up to zoom in.",
                        anchorId = "Camera.ZoomSlider",
                        type = StepType.TapTarget.name,
                        outlineColor = null // highlight ON
                    ),
                    DbInstruction(
                        lessonsId = 10031,
                        stepNo = 4,
                        text = "Great job. Now the picture looks closer because you zoomed in.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 10031,
                        stepNo = 5,
                        text = "Now tap the capture button to take a zoomed-in photo.",
                        anchorId = "Camera.Capture",
                        type = StepType.TapTarget.name,
                        outlineColor = null // highlight ON
                    ),
                    DbInstruction(
                        lessonsId = 10031,
                        stepNo = 6,
                        text = "Excellent! You have practiced zooming in and taking a picture.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    //-------Camera Practice Lesson 10032 – Zooming in (no highlight, guided)-------
                    DbInstruction(
                        lessonsId = 10032,
                        stepNo = 1,
                        text = "Now let's practice zooming again, this time without the on screen hints.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 10032,
                        stepNo = 2,
                        text = "Find the zoom slider and slide it up to zoom in.",
                        anchorId = "Camera.ZoomSlider",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L // transparent → no visible highlight
                    ),
                    DbInstruction(
                        lessonsId = 10032,
                        stepNo = 3,
                        text = "Great. The picture looks closer when you zoom in.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 10032,
                        stepNo = 4,
                        text = "Now take a zoomed-in photo by pressing the capture button.",
                        anchorId = "Camera.Capture",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),
                    DbInstruction(
                        lessonsId = 10032,
                        stepNo = 5,
                        text = "Nice work! You zoomed in and took a picture without any visual hints.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    //-------Camera Practice Lesson 10033 – Zooming in (test, no help)-------
                    DbInstruction(
                        lessonsId = 10033,
                        stepNo = 1,
                        text = "Now try it on your own.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 10033,
                        stepNo = 2,
                        text = "Zoom in.",
                        anchorId = "Camera.ZoomSlider",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),
                    DbInstruction(
                        lessonsId = 10033,
                        stepNo = 3,
                        text = "Take a photo.",
                        anchorId = "Camera.Capture",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),
                    DbInstruction(
                        lessonsId = 10033,
                        stepNo = 4,
                        text = "Excellent! You zoomed in and took a picture on your own.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),




                    //-------Camera Lesson 1004: Flipping the camera-------
                    DbInstruction(
                        lessonsId = 1004,
                        stepNo = 1,
                        text = "This lesson will teach you how to flip between the front and the back camera.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 1004,
                        stepNo = 2,
                        text = "Your phone has two cameras: the back camera, which points away from you, and the front camera, which points towards you for selfies.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 1004,
                        stepNo = 3,
                        text = "This is the switch camera button. It lets you change between the front and back camera.",
                        anchorId = "Camera.SwitchCamera",
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 1004,
                        stepNo = 4,
                        text = "Tap the switch camera button once to change from the back camera to the front camera.",
                        anchorId = "Camera.SwitchCamera",
                        type = StepType.TapTarget.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 1004,
                        stepNo = 5,
                        text = "Now you are using the front camera. In a real app, you would see yourself on the screen, like taking a selfie.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 1004,
                        stepNo = 6,
                        text = "Let's take a selfie. Press the capture button.",
                        anchorId = "Camera.Capture",
                        type = StepType.TapTarget.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 1004,
                        stepNo = 7,
                        text = "You can switch back to the back camera the same way. Tap the switch camera button again to go back.",
                        anchorId = "Camera.SwitchCamera",
                        type = StepType.TapTarget.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 1004,
                        stepNo = 8,
                        text = "Great job. You are now back on the back camera. You have learned how to flip between the front and back cameras.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 1004,
                        stepNo = 9,
                        text = "Remember: use the back camera for things in front of you, and the front camera when you want to take a picture of yourself.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    //-------Camera Practice Lesson 10041 – Flipping the camera (with highlights)-------
                    DbInstruction(
                        lessonsId = 10041,
                        stepNo = 1,
                        text = "Practice time! Let's review how to flip between the front and back cameras.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 10041,
                        stepNo = 2,
                        text = "This is the switch camera button. It changes between the front and back camera.",
                        anchorId = "Camera.SwitchCamera",
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 10041,
                        stepNo = 3,
                        text = "Tap the switch camera button to go from the back camera to the front camera.",
                        anchorId = "Camera.SwitchCamera",
                        type = StepType.TapTarget.name,
                        outlineColor = null // highlight ON
                    ),
                    DbInstruction(
                        lessonsId = 10041,
                        stepNo = 4,
                        text = "Great. Now you are using the front camera, like when taking a selfie.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 10041,
                        stepNo = 5,
                        text = "Tap the capture button to take a selfie.",
                        anchorId = "Camera.Capture",
                        type = StepType.TapTarget.name,
                        outlineColor = null // highlight ON
                    ),
                    DbInstruction(
                        lessonsId = 10041,
                        stepNo = 6,
                        text = "Now tap the switch camera button again to go back to the back camera.",
                        anchorId = "Camera.SwitchCamera",
                        type = StepType.TapTarget.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 10041,
                        stepNo = 7,
                        text = "Excellent! You have practiced flipping between the front and back cameras and taking a photo.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    //-------Camera Practice Lesson 10042 – Flipping the camera (no highlight, guided)-------
                    DbInstruction(
                        lessonsId = 10042,
                        stepNo = 1,
                        text = "Let's practice flipping cameras again, this time without the on screen hints.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 10042,
                        stepNo = 2,
                        text = "Switch to the front camera by tapping the switch camera button.",
                        anchorId = "Camera.SwitchCamera",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L // no visible highlight
                    ),
                    DbInstruction(
                        lessonsId = 10042,
                        stepNo = 3,
                        text = "Now take a selfie by pressing the capture button.",
                        anchorId = "Camera.Capture",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),
                    DbInstruction(
                        lessonsId = 10042,
                        stepNo = 4,
                        text = "Switch back to the back camera using the same switch camera button.",
                        anchorId = "Camera.SwitchCamera",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),
                    DbInstruction(
                        lessonsId = 10042,
                        stepNo = 5,
                        text = "Nice work! You flipped the camera and took a photo without any on-screen hints.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    //-------Camera Practice Lesson 10043 – Flipping the camera (test, no help)-------
                    DbInstruction(
                        lessonsId = 10043,
                        stepNo = 1,
                        text = "Now try flipping the camera on your own.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 10043,
                        stepNo = 2,
                        text = "Switch to the front camera.",
                        anchorId = "Camera.SwitchCamera",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),
                    DbInstruction(
                        lessonsId = 10043,
                        stepNo = 3,
                        text = "Take a photo.",
                        anchorId = "Camera.Capture",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),
                    DbInstruction(
                        lessonsId = 10043,
                        stepNo = 4,
                        text = "Switch back to the back camera.",
                        anchorId = "Camera.SwitchCamera",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),
                    DbInstruction(
                        lessonsId = 10043,
                        stepNo = 5,
                        text = "Excellent! You flipped the camera and took a picture by yourself.",
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
                    //-------Phone Practice Lesson 20011 Instructions-------
                    DbInstruction(
                        lessonsId = 20011,
                        stepNo = 1,
                        text = "Practice time! You will see the names of phone buttons. Tap the matching button on the screen.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 20011,
                        stepNo = 2,
                        text = "Tap the call button.",
                        anchorId = "DialPad.Call",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),
                    DbInstruction(
                        lessonsId = 20011,
                        stepNo = 3,
                        text = "Tap the backspace button.",
                        anchorId = "DialPad.Backspace",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),
                    DbInstruction(
                        lessonsId = 20011,
                        stepNo = 4,
                        text = "Tap the Key 1 button.",
                        anchorId = "DialPad.key1",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),

                    DbInstruction(
                        lessonsId = 20011,
                        stepNo = 5,
                        text = "Great job! You have practiced finding the main dial pad buttons.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    //-------Phone Lesson 2002 Instructions-------
                    DbInstruction(
                        lessonsId = 2002,
                        stepNo = 1,
                        text = "This lesson will teach you how to call a number.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 2002,
                        stepNo = 2,
                        text = "To call someone, you must type in the number you want to call. We can do this on the dial pad.",
                        anchorId = "DialPad.KeysGrid",
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 2002,
                        stepNo = 3,
                        text = "Tap the corresponding numbers to type them in. Give it a go, type the highlighted number 7.",
                        anchorId = "DialPad.key7",
                        type = StepType.EnterText.name,
                        outlineColor = null,
                        expectedText = "7"

                    ),
                    DbInstruction(
                        lessonsId = 2002,
                        stepNo = 4,
                        text = "The dialed keys appear here",
                        anchorId = "DialPad.NumberField",
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 2002,
                        stepNo = 5,
                        text = "If you make a mistake, you can press the back button to delete the last number you dialed. Give it a go, delete the number 7.",
                        anchorId = "DialPad.Backspace",
                        type = StepType.TapTarget.name,
                        outlineColor = null,
                        //expectedText = ""

                    ),
                    DbInstruction(
                        lessonsId = 2002,
                        stepNo = 6,
                        text = "Good Job! Now let's dial a practice number. We will dial the number 112 345",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 2002,
                        stepNo = 7,
                        text = "The number is 112 345. First type in 1.",
                        anchorId = "DialPad.key1",
                        type = StepType.EnterText.name,
                        outlineColor = null,
                        expectedText = "1"

                    ),
                    DbInstruction(
                        lessonsId = 2002,
                        stepNo = 8,
                        text = "The number is 112 345. Now type in 1 again.",
                        anchorId = "DialPad.key1",
                        type = StepType.EnterText.name,
                        outlineColor = null,
                        expectedText = "11"

                    ),
                    DbInstruction(
                        lessonsId = 2002,
                        stepNo = 9,
                        text = "The number is 112 345. Now type in 2.",
                        anchorId = "DialPad.key2",
                        type = StepType.EnterText.name,
                        outlineColor = null,
                        expectedText = "112"

                    ),
                    DbInstruction(
                        lessonsId = 2002,
                        stepNo = 10,
                        text = "The number is 112 345. Now type in 3.",
                        anchorId = "DialPad.key3",
                        type = StepType.EnterText.name,
                        outlineColor = null,
                        expectedText = "1123"

                    ),
                    DbInstruction(
                        lessonsId = 2002,
                        stepNo = 11,
                        text = "The number is 112 345. Now see if you can type in the rest of the number without the highlight hints! Remember, if you make a mistake, you can press the backspace button to delete the last number.",
                        anchorId = null,
                        type = StepType.EnterText.name,
                        outlineColor = null,
                        expectedText = "112345"

                    ),
                    DbInstruction(
                        lessonsId = 2002,
                        stepNo = 12,
                        text = "Good job! Once the number is typed in you can press the call button to call the number. We will pretend call number 112 345.",
                        anchorId = "DialPad.Call",
                        type = StepType.TapTarget.name,
                        outlineColor = null,

                        ),
                    DbInstruction(
                        lessonsId = 2002,
                        stepNo = 13,
                        text = "This is the call screen. To end a call press the hang up button.",
                        anchorId = "DialPad.EndCall",
                        type = StepType.TapTarget.name,
                        outlineColor = null,


                        ),
                    DbInstruction(
                        lessonsId = 2002,
                        stepNo = 14,
                        text = "Good job! Now you know how to call someone!",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null,


                        ),


                    //-------Phone Practice Lesson 20021 – Calling a number (with highlights)-------
                    DbInstruction(
                        lessonsId = 20021,
                        stepNo = 1,
                        text = "Practice time! Let's review how to call a phone number.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 20021,
                        stepNo = 2,
                        text = "We will call the number 234 567.",
                        anchorId = "DialPad.KeysGrid",
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 20021,
                        stepNo = 3,
                        text = "First, type 2.",
                        anchorId = "DialPad.key2",
                        type = StepType.EnterText.name,
                        outlineColor = null,
                        expectedText = "2"
                    ),
                    DbInstruction(
                        lessonsId = 20021,
                        stepNo = 4,
                        text = "Now type 3. The screen should show 23.",
                        anchorId = "DialPad.key3",
                        type = StepType.EnterText.name,
                        outlineColor = null,
                        expectedText = "23"
                    ),
                    DbInstruction(
                        lessonsId = 20021,
                        stepNo = 5,
                        text = "Now type 4. The screen should show 234.",
                        anchorId = "DialPad.key4",
                        type = StepType.EnterText.name,
                        outlineColor = null,
                        expectedText = "234"
                    ),
                    DbInstruction(
                        lessonsId = 20021,
                        stepNo = 6,
                        text = "Type 5. The screen should show 2345.",
                        anchorId = "DialPad.key5",
                        type = StepType.EnterText.name,
                        outlineColor = null,
                        expectedText = "2345"
                    ),
                    DbInstruction(
                        lessonsId = 20021,
                        stepNo = 7,
                        text = "Type 6. The screen should show 23456.",
                        anchorId = "DialPad.key6",
                        type = StepType.EnterText.name,
                        outlineColor = null,
                        expectedText = "23456"
                    ),
                    DbInstruction(
                        lessonsId = 20021,
                        stepNo = 8,
                        text = "Type 7. The screen should show 234567.",
                        anchorId = "DialPad.key7",
                        type = StepType.EnterText.name,
                        outlineColor = null,
                        expectedText = "234567"
                    ),

                    DbInstruction(
                        lessonsId = 20021,
                        stepNo = 9,
                        text = "Now press the call button to start the call.",
                        anchorId = "DialPad.Call",
                        type = StepType.TapTarget.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 20021,
                        stepNo = 10,
                        text = "Great! You have started a call.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 20021,
                        stepNo = 11,
                        text = "Tap the end call button to hang up.",
                        anchorId = "DialPad.EndCall",
                        type = StepType.TapTarget.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 20021,
                        stepNo = 12,
                        text = "Well done! You have practiced calling a number from start to finish.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    //-------Phone Practice Lesson 20022 – Calling a number (no highlight, guided)-------
                    DbInstruction(
                        lessonsId = 20022,
                        stepNo = 1,
                        text = "Now let's practice calling another number without the on screen hints.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 20022,
                        stepNo = 2,
                        text = "We will call the number 905 221.",
                        anchorId = "DialPad.KeysGrid",
                        type = StepType.Acknowledge.name,
                        outlineColor = 0x00000000L
                    ),

                    DbInstruction(
                        lessonsId = 20022,
                        stepNo = 3,
                        text = "Type 9. The screen should show 9.",
                        anchorId = "DialPad.key9",
                        type = StepType.EnterText.name,
                        outlineColor = 0x00000000L,
                        expectedText = "9"
                    ),
                    DbInstruction(
                        lessonsId = 20022,
                        stepNo = 4,
                        text = "Type 0. The screen should show 90.",
                        anchorId = "DialPad.key0",
                        type = StepType.EnterText.name,
                        outlineColor = 0x00000000L,
                        expectedText = "90"
                    ),
                    DbInstruction(
                        lessonsId = 20022,
                        stepNo = 5,
                        text = "Type 5. The screen should show 905.",
                        anchorId = "DialPad.key5",
                        type = StepType.EnterText.name,
                        outlineColor = 0x00000000L,
                        expectedText = "905"
                    ),
                    DbInstruction(
                        lessonsId = 20022,
                        stepNo = 6,
                        text = "Type 2. The screen should show 9052.",
                        anchorId = "DialPad.key2",
                        type = StepType.EnterText.name,
                        outlineColor = 0x00000000L,
                        expectedText = "9052"
                    ),
                    DbInstruction(
                        lessonsId = 20022,
                        stepNo = 7,
                        text = "Type 2. The screen should show 90522.",
                        anchorId = "DialPad.key2",
                        type = StepType.EnterText.name,
                        outlineColor = 0x00000000L,
                        expectedText = "90522"
                    ),
                    DbInstruction(
                        lessonsId = 20022,
                        stepNo = 8,
                        text = "Type 1. The screen should show 905221.",
                        anchorId = "DialPad.key1",
                        type = StepType.EnterText.name,
                        outlineColor = 0x00000000L,
                        expectedText = "905221"
                    ),

                    DbInstruction(
                        lessonsId = 20022,
                        stepNo = 9,
                        text = "Now press the call button to start the call.",
                        anchorId = "DialPad.Call",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),
                    DbInstruction(
                        lessonsId = 20022,
                        stepNo = 10,
                        text = "Tap the end call button to hang up.",
                        anchorId = "DialPad.EndCall",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),
                    DbInstruction(
                        lessonsId = 20022,
                        stepNo = 11,
                        text = "Nice work! You called a different number without any visual hints.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    //-------Phone Practice Lesson 20023 – Calling a number (test, no help)-------
                    DbInstruction(
                        lessonsId = 20023,
                        stepNo = 1,
                        text = "Now try calling a number more independently.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 20023,
                        stepNo = 2,
                        text = "Call the number 411 999. Type it on the dial pad.",
                        anchorId = null, // any key anchor; highlight hidden anyway
                        type = StepType.EnterText.name,
                        outlineColor = null,
                        expectedText = "411999"
                    ),

                    DbInstruction(
                        lessonsId = 20023,
                        stepNo = 3,
                        text = "Press the call button.",
                        anchorId = "DialPad.Call",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),
                    DbInstruction(
                        lessonsId = 20023,
                        stepNo = 4,
                        text = "End the call.",
                        anchorId = "DialPad.EndCall",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),
                    DbInstruction(
                        lessonsId = 20023,
                        stepNo = 5,
                        text = "Excellent! You called a new number on your own.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),



                    // -------Phone Lesson 2003: Adding a new contact-------
                    DbInstruction(
                        lessonsId = 2003,
                        stepNo = 1,
                        text = "This lesson will show you how to add a new contact so you don’t have to remember phone numbers.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 2003,
                        stepNo = 2,
                        text = "We start on the dial screen. At the bottom you can see three options: Favorites, Recents, and Contacts.",
                        anchorId = "Phone.BottomNav.Contacts",
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 2003,
                        stepNo = 3,
                        text = "To add a contact, you first need to go to your contacts list. Tap the Contacts button at the bottom of the screen.",
                        anchorId = "Phone.BottomNav.Contacts",
                        type = StepType.TapTarget.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 2003,
                        stepNo = 4,
                        text = "This is the contacts screen. Here you will see people whose phone numbers you have already saved.",
                        anchorId = "Contacts.List",
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 2003,
                        stepNo = 5,
                        text = "To add someone new, look for the Add contact button with a plus sign. Tap this button to open the contact form.",
                        anchorId = "Contacts.AddContact",
                        type = StepType.TapTarget.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 2003,
                        stepNo = 6,
                        text = "At the top of the form is a box for the person’s name. This is where you type who the contact is, for example “Daughter” or “Dr. Chen”.",
                        anchorId = "Contacts.Add.Name",
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 2003,
                        stepNo = 7,
                        text = "Let's practice adding a contact. Type in 'John Doe' in the name field",
                        anchorId = "Contacts.Add.Name",
                        type = StepType.EnterText.name,
                        outlineColor = null,
                        expectedText = "John Doe"
                    ),


                    DbInstruction(
                        lessonsId = 2003,
                        stepNo = 8,
                        text = "Below the name box is a box for the phone number. This is where you type the person’s phone number.",
                        anchorId = "Contacts.Add.Phone",
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 2003,
                        stepNo = 9,
                        text = "Now type in '987654' in the phone number field",
                        anchorId = "Contacts.Add.Phone",
                        type = StepType.EnterText.name,
                        outlineColor = null,
                        expectedText = "987654"
                    ),

                    DbInstruction(
                        lessonsId = 2003,
                        stepNo = 10,
                        text = "After you enter the name and phone number, tap the Add button to save the contact. Next time, you can call them directly from your contacts list.",
                        anchorId = "Contacts.Add.AddButton",
                        type = StepType.TapTarget.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 2003,
                        stepNo = 11,
                        text = "Awesome! You have added a new contact John Doe and learned how to add a contact.",
                        anchorId = "Contacts.Add.Name",
                        type = StepType.Acknowledge.name,
                        outlineColor = null,

                    ),
                    //-------Phone Practice Lesson 20031 – Adding a contact (with highlights)-------
                    DbInstruction(
                        lessonsId = 20031,
                        stepNo = 1,
                        text = "Practice time! Let's add a new contact together.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 20031,
                        stepNo = 2,
                        text = "Navigate to the contacts tab.",
                        anchorId = "Phone.BottomNav.Contacts",
                        type = StepType.TapTarget.name,
                        outlineColor = null
                    ),


                    DbInstruction(
                        lessonsId = 20031,
                        stepNo = 3,
                        text = "Tap the Add Contact button.",
                        anchorId = "Contacts.AddContact",
                        type = StepType.TapTarget.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 20031,
                        stepNo = 4,
                        text = "In the name box, type the name Alice.",
                        anchorId = "Contacts.Add.Name",
                        type = StepType.EnterText.name,
                        outlineColor = null,
                        expectedText = "Alice"
                    ),

                    DbInstruction(
                        lessonsId = 20031,
                        stepNo = 5,
                        text = "In the phone number box, type 555 1001.",
                        anchorId = "Contacts.Add.Phone",
                        type = StepType.EnterText.name,
                        outlineColor = null,
                        expectedText = "5551001"
                    ),

                    DbInstruction(
                        lessonsId = 20031,
                        stepNo = 6,
                        text = "Tap the Add button to save Alice as a contact.",
                        anchorId = "Contacts.Add.AddButton",
                        type = StepType.TapTarget.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 20031,
                        stepNo = 7,
                        text = "Great! You added Alice, number 555 1001, to your contacts.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    //-------Phone Practice Lesson 20032 – Adding a contact (no highlight, guided)-------
                    DbInstruction(
                        lessonsId = 20032,
                        stepNo = 1,
                        text = "Now let's add another contact without the on screen hints",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 20032,
                        stepNo = 2,
                        text = "Navigate to the contacts tab.",
                        anchorId = "Phone.BottomNav.Contacts",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),
                    DbInstruction(
                        lessonsId = 20032,
                        stepNo = 3,
                        text = "Tap the Add Contact button.",
                        anchorId = "Contacts.AddContact",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),
                    DbInstruction(
                        lessonsId = 20032,
                        stepNo = 4,
                        text = "In the name box, type the name Bob.",
                        anchorId = "Contacts.Add.Name",
                        type = StepType.EnterText.name,
                        outlineColor = 0x00000000L,
                        expectedText = "Bob"
                    ),
                    DbInstruction(
                        lessonsId = 20032,
                        stepNo = 5,
                        text = "In the phone number box, type 555 2002.",
                        anchorId = "Contacts.Add.Phone",
                        type = StepType.EnterText.name,
                        outlineColor = 0x00000000L,
                        expectedText = "5552002"
                    ),
                    DbInstruction(
                        lessonsId = 20032,
                        stepNo = 6,
                        text = "Tap the Add button to save Bob as a contact.",
                        anchorId = "Contacts.Add.AddButton",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),
                    DbInstruction(
                        lessonsId = 20032,
                        stepNo = 7,
                        text = "Nice work! You added Bob, number 555 2002, to your contacts.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

//-------Phone Practice Lesson 20033 – Adding a contact (test, no highlight)-------
                    DbInstruction(
                        lessonsId = 20033,
                        stepNo = 1,
                        text = "Now try adding a contact more independently.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 20033,
                        stepNo = 2,
                        text = "Navigate to the contacts tab.",
                        anchorId = "Phone.BottomNav.Contacts",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),
                    DbInstruction(
                        lessonsId = 20033,
                        stepNo = 3,
                        text = "Add contact Grandma Li with phone number 555 3030",
                        anchorId = "Contacts.AddContact",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),
                    DbInstruction(
                        lessonsId = 20033,
                        stepNo = 4,
                        text = "Add contact Grandma Li with phone number 555 3030",
                        anchorId = "Contacts.Add.Name",
                        type = StepType.EnterText.name,
                        outlineColor = 0x00000000L,
                        expectedText = "Grandma Li"
                    ),
                    DbInstruction(
                        lessonsId = 20033,
                        stepNo = 5,
                        text = "Add contact Grandma Li with phone number 555 3030",
                        anchorId = "Contacts.Add.Phone",
                        type = StepType.EnterText.name,
                        outlineColor = 0x00000000L,
                        expectedText = "5553030"
                    ),
                    DbInstruction(
                        lessonsId = 20033,
                        stepNo = 6,
                        text = "Add contact Grandma Li with phone number 555 3030",
                        anchorId = "Contacts.Add.AddButton",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),
                    DbInstruction(
                        lessonsId = 20033,
                        stepNo = 7,
                        text = "Excellent! You added Grandma Li, number 555 3030, on your own.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),





                    //------- Phone Lesson 2004 — Calling a Saved Contact -------
                    DbInstruction(
                        lessonsId = 2004,
                        stepNo = 1,
                        text = "In this lesson, you will learn how to call a saved contact.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 2004,
                        stepNo = 2,
                        text = "First, open your saved contacts.",
                        anchorId = "Phone.BottomNav.Contacts",   // Make sure Contacts tab has this anchor
                        type = StepType.TapTarget.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 2004,
                        stepNo = 3,
                        text = "Now tap on the saved contact named 'Alice Johnson'.",
                        anchorId = "Contacts.Contact.Alice Johnson",
                        type = StepType.TapTarget.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 2004,
                        stepNo = 4,
                        text = "You are now calling the saved contact of Alice Johnson. To end the call, tap the hang up button.",
                        anchorId = "DialPad.EndCall",
                        type = StepType.TapTarget.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 2004,
                        stepNo = 5,
                        text = "Good job! You’ve learned how to call a saved contact.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    //------- Phone Practice Lesson 20041 — Calling a Saved Contact (with on-screen hints) -------
                    DbInstruction(
                        lessonsId = 20041,
                        stepNo = 1,
                        text = "Practice time! Let's call a saved contact using the on-screen hints.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 20041,
                        stepNo = 2,
                        text = "First, open your saved contacts.",
                        anchorId = "Phone.BottomNav.Contacts",
                        type = StepType.TapTarget.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 20041,
                        stepNo = 3,
                        text = "Now tap on the saved contact named 'Alice Johnson'.",
                        anchorId = "Contacts.Contact.Alice Johnson",
                        type = StepType.TapTarget.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 20041,
                        stepNo = 4,
                        text = "You are now calling Alice Johnson. To end the call, tap the hang up button.",
                        anchorId = "DialPad.EndCall",
                        type = StepType.TapTarget.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 20041,
                        stepNo = 5,
                        text = "Great job! You called a saved contact using the on-screen hints.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),



//------- Phone Practice Lesson 20042 — Calling a Saved Contact (no on-screen hints, guided) -------
                    DbInstruction(
                        lessonsId = 20042,
                        stepNo = 1,
                        text = "Now let's call a saved contact again, this time with fewer on-screen hints.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 20042,
                        stepNo = 2,
                        text = "Open your saved contacts.",
                        anchorId = "Phone.BottomNav.Contacts",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L   // no visible highlight
                    ),

                    DbInstruction(
                        lessonsId = 20042,
                        stepNo = 3,
                        text = "Tap on the saved contact named 'Bob Smith'.",
                        anchorId = "Contacts.Contact.Bob Smith",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),

                    DbInstruction(
                        lessonsId = 20042,
                        stepNo = 4,
                        text = "You are now calling Bob Smith. Tap the hang up button to end the call.",
                        anchorId = "DialPad.EndCall",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),

                    DbInstruction(
                        lessonsId = 20042,
                        stepNo = 5,
                        text = "Nice work! You called a different saved contact with fewer on-screen hints.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),



//------- Phone Practice Lesson 20043 — Calling a Saved Contact (test, no on-screen hints) -------
                    DbInstruction(
                        lessonsId = 20043,
                        stepNo = 1,
                        text = "Now try calling a saved contact more independently, without on-screen hints.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 20043,
                        stepNo = 2,
                        text = "Open your saved contacts.",
                        anchorId = "Phone.BottomNav.Contacts",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),

                    DbInstruction(
                        lessonsId = 20043,
                        stepNo = 3,
                        text = "Call the contact named 'Alice Johnson' by tapping her name.",
                        anchorId = "Contacts.Contact.Alice Johnson",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),

                    DbInstruction(
                        lessonsId = 20043,
                        stepNo = 4,
                        text = "End the call.",
                        anchorId = "DialPad.EndCall",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),

                    DbInstruction(
                        lessonsId = 20043,
                        stepNo = 5,
                        text = "Excellent! You called a saved contact on your own without on-screen hints.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),






                    //-------Wifi Lesson 3001 Instructions-------
                    DbInstruction(
                        lessonsId = 3001,
                        stepNo = 1,
                        text = "Welcome to the WiFi screen located in your settings app. Let's learn about it.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 3001,
                        stepNo = 2,
                        text = "In order to access the internet, you must be connected to the WiFi.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 3001,
                        stepNo = 3,
                        text = "Many apps need to be connected to Wifi to work. Such apps include Google, What's App, Google Maps, and others",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 3001,
                        stepNo = 4,
                        text = "Here are located all the available WiFi networks in your area.",
                        anchorId = "Wifi.NetworkBox",
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 3001,
                        stepNo = 5,
                        text = "This toggle turns the wifi on and off.",
                        anchorId = "Wifi.WifiToggle",
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 3001,
                        stepNo = 6,
                        text = "This is one of the available networks. By clicking it, you can connect to the network.",
                        anchorId = "Wifi.Network.Campus-Guest",
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 3001,
                        stepNo = 7,
                        text = "This is the name of this network:'Campus-Guest'",
                        anchorId = "Wifi.NetworkName.Campus-Guest",
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 3001,
                        stepNo = 8,
                        text = "These bars show the strength of the Wifi signal. This means how good the connection is.",
                        anchorId = "Wifi.Bars.Campus-Guest",
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 3001,
                        stepNo = 9,
                        text = "Good job! You have now been familiarized with the Wifi settings!",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),

                    //-------Wifi Practice Lesson 30011 Instructions-------
                    DbInstruction(
                        lessonsId = 30011,
                        stepNo = 1,
                        text = "Practice time! You will see the names of WiFi controls. Tap the matching part of the screen.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 30011,
                        stepNo = 2,
                        text = "Tap the WiFi network named 'Campus-Guest'.",
                        anchorId = "Wifi.Network.Campus-Guest",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),

                    DbInstruction(
                        lessonsId = 30011,
                        stepNo = 3,
                        text = "Tap the WiFi toggle button.",
                        anchorId = "Wifi.WifiToggle",
                        type = StepType.Toggle.name,
                        outlineColor = 0x00000000L
                    ),



                    DbInstruction(
                        lessonsId = 30011,
                        stepNo = 4,
                        text = "Nice work! You have practiced the main parts of the WiFi screen.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),


                    //------- tester wifi

                    DbInstruction(
                        lessonsId = 3002,
                        stepNo = 1,
                        text = "In this lesson, you will learn how to turn on the WiFi.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null,


                    ),

                    DbInstruction(
                        lessonsId = 3002,
                        stepNo = 2,
                        text = "Wifi is a wireless connection that allows your phone to access the Internet.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 3002,
                        stepNo = 3,
                        text = "In order to access the internet, you must be connected to the WiFi.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 3002,
                        stepNo = 4,
                        text = "Many apps need to be connected to Wifi to work. Such apps include Google, What's App, Google Maps, and others",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 3002,
                        stepNo = 5,
                        text = "Currently, the Wifi is off. You can tell it is off because no Wifi networks are showing and it says 'off'.",
                        anchorId = "Wifi.OnOffRow",
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 3002,
                        stepNo = 6,
                        text = "In order to turn it on, you must tap this toggle. Give it a try!",
                        anchorId = "Wifi.WifiToggle",
                        type = StepType.Toggle.name,
                        outlineColor = null,
                        expectedText = "true"

                    ),
                    DbInstruction(
                        lessonsId = 3002,
                        stepNo = 7,
                        text = "Good job! You have turned on the Wifi. However, this does not mean you are already connected to the Wifi.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    DbInstruction(
                        lessonsId = 3002,
                        stepNo = 8,
                        text = "You will learn how to connect to the Wifi and gain Internet access in the next lesson.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null

                    ),
                    //------- Wifi Practice Lesson 30021 — Turning WiFi On (with on-screen hints) -------
                    DbInstruction(
                        lessonsId = 30021,
                        stepNo = 1,
                        text = "Practice time! Let's turn the WiFi on using the on-screen hints.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 30021,
                        stepNo = 2,
                        text = "This row shows whether WiFi is on or off.",
                        anchorId = "Wifi.OnOffRow",
                        type = StepType.Acknowledge.name,
                        outlineColor = null,
                    ),
                    DbInstruction(
                        lessonsId = 30021,
                        stepNo = 3,
                        text = "Tap the WiFi toggle to turn WiFi off.",
                        anchorId = "Wifi.WifiToggle",
                        type = StepType.Toggle.name,
                        outlineColor = null,
                        expectedText = "false"
                    ),
                    DbInstruction(
                        lessonsId = 30021,
                        stepNo = 4,
                        text = "Tap the WiFi toggle to turn WiFi on.",
                        anchorId = "Wifi.WifiToggle",
                        type = StepType.Toggle.name,
                        outlineColor = null,
                        expectedText = "true"
                    ),
                    DbInstruction(
                        lessonsId = 30021,
                        stepNo = 5,
                        text = "Great job! WiFi is now on.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),


//------- Wifi Practice Lesson 30022 — Turning WiFi On and Off (no on-screen hints, guided) -------
                    DbInstruction(
                        lessonsId = 30022,
                        stepNo = 1,
                        text = "Now let's turn WiFi on and off with fewer on-screen hints.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 30022,
                        stepNo = 2,
                        text = "Turn WiFi on using the WiFi toggle.",
                        anchorId = "Wifi.WifiToggle",
                        type = StepType.Toggle.name,
                        outlineColor = 0x00000000L,
                        expectedText = "true"
                    ),
                    DbInstruction(
                        lessonsId = 30022,
                        stepNo = 3,
                        text = "Now turn WiFi off using the same toggle.",
                        anchorId = "Wifi.WifiToggle",
                        type = StepType.Toggle.name,
                        outlineColor = 0x00000000L,
                        expectedText = "false"
                    ),
                    DbInstruction(
                        lessonsId = 30022,
                        stepNo = 4,
                        text = "Nice work! You turned WiFi on and off without on-screen hints.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),


//------- Wifi Practice Lesson 30023 — Turning WiFi On and Off (test, no on-screen hints) -------
                    DbInstruction(
                        lessonsId = 30023,
                        stepNo = 1,
                        text = "Now try controlling WiFi on your own, without on-screen hints.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 30023,
                        stepNo = 2,
                        text = "Turn WiFi on.",
                        anchorId = "Wifi.WifiToggle",
                        type = StepType.Toggle.name,
                        outlineColor = 0x00000000L,
                        expectedText = "true"
                    ),
                    DbInstruction(
                        lessonsId = 30023,
                        stepNo = 3,
                        text = "Now turn WiFi off.",
                        anchorId = "Wifi.WifiToggle",
                        type = StepType.Toggle.name,
                        outlineColor = 0x00000000L,
                        expectedText = "false"
                    ),
                    DbInstruction(
                        lessonsId = 30023,
                        stepNo = 4,
                        text = "Excellent! You turned WiFi on and off by yourself.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    //-------WiFi Lesson 3003 Instructions-------
                    // Goal: Connect to Home-5G with password "password"

                    DbInstruction(
                        lessonsId = 3003,
                        stepNo = 1,
                        text = "In this lesson, you'll practice connecting to your home Wi-Fi network called Home-5G.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 3003,
                        stepNo = 2,
                        text = "First, tap the Home-5G network in the list.",
                        anchorId = "Wifi.Network.Home-5G",
                        type = StepType.TapTarget.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 3003,
                        stepNo = 3,
                        text = "Now type the Wi-Fi password in the password box. For practice, enter the word: password.",
                        anchorId = "Wifi.Connect.Password",
                        type = StepType.EnterText.name,
                        outlineColor = null,
                        expectedText = "password"
                    ),

                    DbInstruction(
                        lessonsId = 3003,
                        stepNo = 4,
                        text = "Great! On a real phone, you would tap the Connect button to join Home-5G.",
                        anchorId = "Wifi.Connect.Button",
                        type = StepType.TapTarget.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 3003,
                        stepNo = 5,
                        text = "Nice job! You practiced connecting to a secure Wi-Fi network.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    //------- Wifi Practice Lesson 30031 — Connecting to Home-5G (with on-screen hints) -------
                    DbInstruction(
                        lessonsId = 30031,
                        stepNo = 1,
                        text = "Practice time! Let's connect to your secure WiFi network called Home-5G.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 30031,
                        stepNo = 2,
                        text = "First, tap the Home-5G network in the list.",
                        anchorId = "Wifi.Network.Home-5G",
                        type = StepType.TapTarget.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 30031,
                        stepNo = 3,
                        text = "Now type the WiFi password in the password box. For practice, enter: password.",
                        anchorId = "Wifi.Connect.Password",
                        type = StepType.EnterText.name,
                        outlineColor = null,
                        expectedText = "password"
                    ),
                    DbInstruction(
                        lessonsId = 30031,
                        stepNo = 4,
                        text = "Tap the Connect button to join Home-5G.",
                        anchorId = "Wifi.Connect.Button",
                        type = StepType.TapTarget.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 30031,
                        stepNo = 5,
                        text = "Great job! You connected to a secure WiFi network using the on-screen hints.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),


//------- Wifi Practice Lesson 30032 — Connecting to Home-5G (no on-screen hints, guided) -------
                    DbInstruction(
                        lessonsId = 30032,
                        stepNo = 1,
                        text = "Now let's connect to Home-5G again with fewer on-screen hints.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 30032,
                        stepNo = 2,
                        text = "Tap the Home-5G network.",
                        anchorId = "Wifi.Network.Home-5G",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),
                    DbInstruction(
                        lessonsId = 30032,
                        stepNo = 3,
                        text = "Enter the WiFi password: password.",
                        anchorId = "Wifi.Connect.Password",
                        type = StepType.EnterText.name,
                        outlineColor = 0x00000000L,
                        expectedText = "password"
                    ),
                    DbInstruction(
                        lessonsId = 30032,
                        stepNo = 4,
                        text = "Tap the Connect button.",
                        anchorId = "Wifi.Connect.Button",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),
                    DbInstruction(
                        lessonsId = 30032,
                        stepNo = 5,
                        text = "Nice work! You connected to Home-5G with fewer on-screen hints.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),


//------- Wifi Practice Lesson 30033 — Connecting to Home-5G (test, no on-screen hints) -------
                    DbInstruction(
                        lessonsId = 30033,
                        stepNo = 1,
                        text = "Now try connecting to your secure WiFi network on your own.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),
                    DbInstruction(
                        lessonsId = 30033,
                        stepNo = 2,
                        text = "Connect to Home-5G which has password 'password'.",
                        anchorId = "Wifi.Network.Home-5G",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),
                    DbInstruction(
                        lessonsId = 30033,
                        stepNo = 3,
                        text = "Connect to Home-5G which has password 'password'.",
                        anchorId = "Wifi.Connect.Password",
                        type = StepType.EnterText.name,
                        outlineColor = 0x00000000L,
                        expectedText = "password"
                    ),
                    DbInstruction(
                        lessonsId = 30033,
                        stepNo = 4,
                        text = "Connect to Home-5G which has password 'password'.",
                        anchorId = "Wifi.Connect.Button",
                        type = StepType.TapTarget.name,
                        outlineColor = 0x00000000L
                    ),
                    DbInstruction(
                        lessonsId = 30033,
                        stepNo = 5,
                        text = "Excellent! You connected to your secure WiFi network without on-screen hints.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    //------- Wifi Lesson 3004 — Open vs Secure Networks -------
                    DbInstruction(
                        lessonsId = 3004,
                        stepNo = 1,
                        text = "In this lesson, you will learn the difference between open and secure Wi-Fi networks.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 3004,
                        stepNo = 2,
                        text = "This is your list of available Wi-Fi networks.",
                        anchorId = "Wifi.NetworkBox",
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 3004,
                        stepNo = 3,
                        text = "Some networks are secure. A secure network needs a password, usually for places like your home or office. Home-5G is a secure network.",
                        anchorId = "Wifi.Network.Home-5G",
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 3004,
                        stepNo = 4,
                        text = "Other networks are open. An open network does NOT need a password, and is often used in public places like airports, coffee shops, or guest networks. Campus-Guest is an open network.",
                        anchorId = "Wifi.Network.Campus-Guest",
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 3004,
                        stepNo = 5,
                        text = "Secure networks are usually safer because only people with the password can join. Open networks are more convenient, but you should avoid doing banking or anything very private on them.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 3004,
                        stepNo = 6,
                        text = "Now, let’s practice connecting to an open network. Tap on the network called Campus-Guest.",
                        anchorId = "Wifi.Network.Campus-Guest",
                        type = StepType.TapTarget.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 3004,
                        stepNo = 7,
                        text = "You are now on the connect screen for Campus-Guest. Because this is an open network, it does not need a password.",
                        anchorId = "Wifi.Connect.SSID",
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 3004,
                        stepNo = 8,
                        text = "To connect to this open network, tap the Connect button.",
                        anchorId = "Wifi.Connect.Button",
                        type = StepType.TapTarget.name,
                        outlineColor = null
                    ),

                    DbInstruction(
                        lessonsId = 3004,
                        stepNo = 9,
                        text = "Great job! You learned the difference between open and secure Wi-Fi, and practiced connecting to an open network.",
                        anchorId = null,
                        type = StepType.Acknowledge.name,
                        outlineColor = null
                    ),


                    )
            )
        }

        // --- Seed default Button ---

        val defaultButtonDao = db.defaultButtonDao()
        val existingDefaults = defaultButtonDao.getDefaultsForLesson(lessonId = 3003) // for example
        if (existingDefaults.isEmpty()) {
            defaultButtonDao.insertAll(
                listOf(
                    // Connect to Wi-Fi lesson: Wi-Fi toggle should start ON
                    DbDefaultButton(
                        lessonsId = 3002,
                        buttonName = "Wifi.OnOffButton",
                        state = "false"
                    ),
                    DbDefaultButton(
                        lessonsId = 30022,
                        buttonName = "Wifi.OnOffButton",
                        state = "false"
                    ),
                    DbDefaultButton(
                        lessonsId = 30023,
                        buttonName = "Wifi.OnOffButton",
                        state = "false"
                    ),
                    DbDefaultButton(
                        lessonsId = 2001,
                        buttonName = "DialPad.NumberField",
                        state = "112345"
                    ),
                    DbDefaultButton(
                        lessonsId = 2002,
                        buttonName = "DialPad.NumberField",
                        state = ""
                    ),
                    DbDefaultButton(
                        lessonsId = 1001,
                        buttonName = "Camera.SwitchCamera",
                        state = "BACK"
                    ),
                    DbDefaultButton(
                        lessonsId = 1001,
                        buttonName = "Camera.ZoomSlider",
                        state = "1.0"
                    ),
                    DbDefaultButton(
                        lessonsId = 1001,
                        buttonName = "Camera.FlashButton",
                        state = "OFF"
                    ),
                    DbDefaultButton(
                        lessonsId = 30011,
                        buttonName = "Wifi.OnOffButton",
                        state = "false"
                    )


                    )
            )
        }


        // --- Seed demo customer ---
        val customerDao = db.customerDao()
        val existingCustomer = customerDao.getCustomer("demo")
        if (existingCustomer == null) {
            // generate salt and hash the password
            val salt = HashUtils.generateSalt()
            val hashedPassword = HashUtils.hashPasswordWithSalt("password", salt)

            customerDao.insertCustomer(
                DbCustomer(
                    email = "demo",
                    name = "Demo User",
                    password = hashedPassword,
                    salt = salt
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
                    ),
                    DbPreReq(
                        lessonId = 1002,
                        prereqId = 1001,
                        priority = 1
                    ),
                    DbPreReq(
                        lessonId = 1003,
                        prereqId = 1002,
                        priority = 2
                    ),
                    DbPreReq(
                        lessonId = 1003,
                        prereqId = 1001,
                        priority = 1
                    ),
                    DbPreReq(
                        lessonId = 2002,
                        prereqId = 2001,
                        priority = 1
                    ),
                    DbPreReq(
                        lessonId = 2003,
                        prereqId = 2002,
                        priority = 2
                    ),
                    DbPreReq(
                        lessonId = 2003,
                        prereqId = 2001,
                        priority = 1
                    ),
                    DbPreReq(
                        lessonId = 3002,
                        prereqId = 1001,
                        priority = 1
                    ),
                    DbPreReq(
                        lessonId = 3003,
                        prereqId = 1002,
                        priority = 2
                    ),
                    DbPreReq(
                        lessonId =3003,
                        prereqId = 1001,
                        priority = 1
                    )
                )
            )
        }
    }
}

