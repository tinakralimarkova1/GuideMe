package com.example.guideme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guideme.phone.CameraScreen
import com.example.guideme.phone.PhoneNavHost
import com.example.guideme.tts.TTS
import com.example.guideme.ui.theme.GuideMeTheme
import com.example.guideme.ui.theme.MainBackgroundGradient
import com.example.guideme.ui.theme.MainButtonColor
import com.example.guideme.ui.theme.MainButtonContentColor
import com.example.guideme.ui.theme.Transparent
import com.example.guideme.wifi.WifiNavHost
import com.example.guideme.lessons.LessonHost
import androidx.room.Room
import com.example.guideme.lessons.GuideMeDatabase
import com.example.guideme.lessons.LessonsRepository
import com.example.guideme.lessons.RoomLessonsRepository
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.guideme.lessons.DbInstruction
import com.example.guideme.lessons.StepType
import com.example.guideme.lessons.DbLesson
import com.example.guideme.lessons.DbCustomer
import com.example.guideme.lessons.DbPreReq

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.launch
import com.example.guideme.lessons.CustomerDao
import com.example.guideme.lessons.AuthScreen






class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Text-to-Speech
        TTS.init(this) {
            // We'll speak the welcome message after login in GuideMeRoot
        }

        // --- Room database + repository setup ---
        val db = Room.databaseBuilder(
            applicationContext,
            GuideMeDatabase::class.java,
            "guideme.db"
        )
            .fallbackToDestructiveMigration()   // dev-friendly: wipes DB on schema change
            .build()

        //TODO: fix this, remove fake lesson repo

        val lessonsRepo: LessonsRepository = RoomLessonsRepository(
            instructionDao = db.instructionDao(),
            completionDao = db.completionDao()
        )

        // --- end Room setup ---

        // Seed instructions the first time (very simple check)
        lifecycleScope.launch {
            // Seed Lessons table
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

            // Seed Instructions for lesson 1 if not present (you already had this block;
            // keep it, just make sure it still runs after the DB build).
            val instructionDao = db.instructionDao()
            val existing = instructionDao.getInstructionsForLesson(lessonId = 1)
            if (existing.isEmpty()) {
                instructionDao.insertAll(
                    listOf(
                        DbInstruction(
                            lessonsId = 1,
                            stepNo = 1,
                            text = "HELLOOOOOOOO",
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
                            outlineColor = null
                        ),
                        DbInstruction(
                            lessonsId = 1,
                            stepNo = 3,
                            text = "Tap CALL again to confirm.",
                            anchorId = "DialPad.CallConfirm",
                            type = StepType.TapTarget.name,
                            outlineColor = null
                        )
                    )
                )
            }

            // --- Seed a demo customer matching the email used in LessonViewModel ---
            val customerDao = db.customerDao()
            val existingCustomer = customerDao.getCustomer("demo@guideme.app")
            if (existingCustomer == null) {
                customerDao.insertCustomer(
                    DbCustomer(
                        email = "demo",
                        name = "Demo User",
                        password = "password",   // placeholder; not secure, but fine for local dev
                        city = null,
                        street = null,
                        state = null,
                        buildingNumber = null,
                        phoneNum = null,
                        dateOfBirth = null
                    )
                )
            }

            // --- Seed some prereqs (for future recommendations) ---
            val preReqDao = db.preReqDao()
            val existingPrereqs = preReqDao.getPrereqsForLesson(lessonId = 2)  // e.g. Wi-Fi lesson
            if (existingPrereqs.isEmpty()) {
                preReqDao.insertAll(
                    listOf(
                        // Example: Phone (1) must be done before Wi-Fi (2)
                        DbPreReq(
                            lessonId = 2,    // Wi-Fi
                            prereqId = 1,    // Phone
                            priority = 1
                        )
                    )
                )
            }
        }





        setContent {
            GuideMeTheme {
                // Global gradient behind Scaffold
                Box(Modifier.fillMaxSize().background(MainBackgroundGradient)) {
                    Scaffold(
                        containerColor = Transparent,
                        contentWindowInsets = WindowInsets(0)
                    ) { innerPadding ->
                        GuideMeRoot(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            lessonsRepo = lessonsRepo,
                            customerDao = db.customerDao()
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        TTS.shutdown()
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    lessonsRepo: LessonsRepository,
    userEmail: String,                    // 👈 add this
    onLogout: () -> Unit = {}
) {
    Column(
        modifier = modifier
    ) {
        // Top bar with welcome + logout
        //TODO: fix UI here. Only show when logging in
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Welcome to GuideMe",
                style = MaterialTheme.typography.headlineSmall
            )

            TextButton(onClick = onLogout) {
                Text("Logout")
            }
        }

        // Screens: welcome -> main (lessons menu) -> phone/camera/wifi/lesson_phone/search
        var currentScreen by remember { mutableStateOf("welcome") }

        when (currentScreen) {
            "welcome" -> {
                WelcomeScreen(
                    modifier = modifier.fillMaxSize(),
                    onSearchClick = {
                        TTS.speak("Opening search.")
                        currentScreen = "search"
                    },
                    onLessonsClick = {
                        TTS.speak("Opening lessons menu.")
                        currentScreen = "main"
                    }
                )
            }

            "main" -> {
                LessonsMenu(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    onOpenCamera = {
                        TTS.speak("Opening Camera.")
                        currentScreen = "camera"
                    },
                    onOpenPhone = {
                        TTS.speak("Opening Phone.")
                        currentScreen = "phone"
                    },
                    onOpenWifi = {
                        TTS.speak("Opening Wi-Fi.")
                        currentScreen = "wifi"
                    },
                    onStartPhoneLesson = {
                        TTS.speak("Starting Phone Lesson.")
                        currentScreen = "lesson_phone"
                    }
                )
                BackHandler {
                    TTS.speak("Returning to welcome.")
                    currentScreen = "welcome"
                }
            }

            "search" -> {
                SearchMenu(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    onVoiceSearch = {
                        TTS.speak("Voice search coming soon. Say your question after the beep.")
                    },
                    onTextSearch = {
                        TTS.speak("Opening text search.")
                    }
                )
                BackHandler {
                    TTS.speak("Returning to welcome.")
                    currentScreen = "welcome"
                }
            }

            "phone" -> {
                PhoneNavHost()
                BackHandler {
                    TTS.speak("Returning to lessons menu.")
                    currentScreen = "main"
                }
            }

            "camera" -> {
                CameraScreen()
                BackHandler {
                    TTS.speak("Returning to lessons menu.")
                    currentScreen = "main"
                }
            }

            "wifi" -> {
                WifiNavHost()
                BackHandler {
                    TTS.speak("Returning to lessons menu.")
                    currentScreen = "main"
                }
            }

            "lesson_phone" -> {
                LessonHost(
                    appName = "Phone",
                    lessonId = 1,
                    repo = lessonsRepo,
                    userEmail = userEmail              // 👈 forward it down
                )
                BackHandler {
                    TTS.speak("Returning to lessons menu.")
                    currentScreen = "main"
                }
            }

        }
    }

    }

    /* ------------ UI COMPOSABLES ------------ */

    @Composable
    private fun WelcomeScreen(
        modifier: Modifier = Modifier,
        onSearchClick: () -> Unit,
        onLessonsClick: () -> Unit
    ) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "Welcome to GuideMe",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MainButtonContentColor
                )

                Spacer(Modifier.height(0.dp))

                Button(
                    onClick = onLessonsClick,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(130.dp),
                    shape = RoundedCornerShape(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainButtonColor,
                        contentColor = MainButtonContentColor
                    )
                ) {
                    Text(
                        "Click here to learn",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }

                Button(
                    onClick = onSearchClick,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(130.dp),
                    shape = RoundedCornerShape(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainButtonColor,
                        contentColor = MainButtonContentColor
                    )
                ) {
                    Text(
                        "Click here to search",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    @Composable
    private fun LessonsMenu(
        modifier: Modifier = Modifier,
        onOpenCamera: () -> Unit,
        onOpenPhone: () -> Unit,
        onOpenWifi: () -> Unit,
        onStartPhoneLesson: () -> Unit,         // <-- lifted callback
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MainBackgroundGradient)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Lesson Menu",
                    color = MainButtonContentColor,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .padding(bottom = 40.dp)
                        .padding(top = 60.dp),
                )

                Button(
                    onClick = onOpenCamera,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp)
                        .height(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainButtonColor,
                        contentColor = MainButtonContentColor
                    )
                ) {
                    Text(
                        "Camera",
                        color = MainButtonContentColor,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Button(
                    onClick = onOpenPhone,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp)
                        .height(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainButtonColor,
                        contentColor = MainButtonContentColor
                    )
                ) {
                    Text(
                        "Phone",
                        color = MainButtonContentColor,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Button(
                    onClick = onOpenWifi,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp)
                        .height(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainButtonColor,
                        contentColor = MainButtonContentColor
                    )
                ) {
                    Text(
                        "Wi-Fi",
                        color = MainButtonContentColor,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                // Start Phone Lesson
                Button(
                    onClick = onStartPhoneLesson,  // <-- use the callback
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp)
                        .height(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainButtonColor,
                        contentColor = MainButtonContentColor
                    )
                ) {
                    Text("Phone Lesson 1", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }

    @Composable
    private fun SearchMenu(
        modifier: Modifier = Modifier,
        onVoiceSearch: () -> Unit,
        onTextSearch: () -> Unit
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MainBackgroundGradient)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Search",
                    color = MainButtonContentColor,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(top = 60.dp, bottom = 40.dp)
                )

                Button(
                    onClick = onVoiceSearch,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp)
                        .height(130.dp),
                    shape = RoundedCornerShape(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainButtonColor,
                        contentColor = MainButtonContentColor
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = "Microphone",
                            modifier = Modifier.size(52.dp)
                        )
                        Text(
                            "Say your question",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Button(
                    onClick = onTextSearch,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp)
                        .height(130.dp),
                    shape = RoundedCornerShape(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainButtonColor,
                        contentColor = MainButtonContentColor
                    )
                ) {
                    Text(
                        "Type your question",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    @Composable
    fun GuideMeRoot(
        modifier: Modifier = Modifier,
        lessonsRepo: LessonsRepository,
        customerDao: CustomerDao
    ) {
        // Remember which user is currently logged in
        var currentUser by rememberSaveable { mutableStateOf<DbCustomer?>(null) }

        if (currentUser == null) {
            // Show the login / register screen
            AuthScreen(
                modifier = modifier.fillMaxSize(),
                customerDao = customerDao,
                onAuthSuccess = { customer ->
                    currentUser = customer
                    // Say the welcome message AFTER a successful login
                    TTS.speak(
                        "Welcome to Guide Me. " +
                                "Choose Search to look up how to do something, or go to the Lessons menu."
                    )
                }
            )
        } else {
            // Show your existing main app once logged in
            MainScreen(
                modifier = modifier,
                lessonsRepo = lessonsRepo,
                userEmail = currentUser!!.email,   // 👈 add this
                onLogout = { currentUser = null }
            )

        }
    }


    /* --------- Placeholder so it compiles; replace with your real lesson host later --------- */


    /* -------------------- Previews -------------------- */

    @Preview(showBackground = true, name = "Welcome")
    @Composable
    fun PreviewWelcome() {
        GuideMeTheme {
            WelcomeScreen(onSearchClick = {}, onLessonsClick = {})
        }
    }

    @Preview(showBackground = true, name = "Lessons Menu")
    @Composable
    fun PreviewLessonsMenu() {
        GuideMeTheme {
            LessonsMenu(
                onOpenCamera = {},
                onOpenPhone = {},
                onOpenWifi = {},
                onStartPhoneLesson = {}
            )
        }
    }

    @Preview(showBackground = true, name = "Search Menu")
    @Composable
    fun PreviewSearchMenu() {
        GuideMeTheme {
            SearchMenu(onVoiceSearch = {}, onTextSearch = {})
        }
    }



