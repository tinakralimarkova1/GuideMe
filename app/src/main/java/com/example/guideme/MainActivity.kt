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
import com.example.guideme.lessons.DatabaseSeeder
import com.example.guideme.NLP.IntentLessonRecommender
import com.example.guideme.lessons.DbMissingLesson
import kotlinx.coroutines.launch

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


        val lessonsRepo: LessonsRepository = RoomLessonsRepository(
            instructionDao = db.instructionDao(),
            completionDao = db.completionDao()
        )

        // --- end Room setup ---


        // Seed instructions the first time (very simple check)
        lifecycleScope.launch {
            // Seed Lessons table
            DatabaseSeeder.seed(db)
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
                            customerDao = db.customerDao(),
                            lessonDao = db.lessonDao(),
                            missingLessonDao = db.missingLessonDao()

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
    onLogout: () -> Unit = {},
    lessonDao: com.example.guideme.lessons.LessonDao? = null,
    missingLessonDao: com.example.guideme.lessons.MissingLessonDao? = null
    userEmail: String,
    onLogout: () -> Unit = {}
) {
    Column(
        modifier = modifier
    ) {

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
                    },
                    onLogoutClick = {
                        TTS.speak("Logging out.")
                        onLogout()
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
                if (lessonDao == null || missingLessonDao == null) {
                    // Preview / fallback behavior
                    SearchMenu(
                        modifier = modifier.fillMaxSize().padding(24.dp),
                        onVoiceSearch = { TTS.speak("Voice search coming soon.") },
                        onTextSearch  = { TTS.speak("Text search coming soon.") }
                    )
                } else {
                    // coroutine scope tied to this Composable
                    val scope = rememberCoroutineScope()

                    // 1. load lesson titles from Room once
                    var lessonTitles by remember { mutableStateOf<List<String>>(emptyList()) }
                    LaunchedEffect(Unit) {
                        // Query Room off the main thread
                        lessonTitles = lessonDao.getAllLessons().map { it.name }
                    }
                    // 2. create recommender when titles are available
                    val recommender = remember(lessonTitles) {
                        com.example.guideme.NLP.IntentLessonRecommender(lessonTitles)
                    }

                    // 3. simple text input dialog state
                    var showTextDialog by remember { mutableStateOf(false) }
                    var typedQuery by remember { mutableStateOf("") }

                    // 4. handle a user query: run recommender, suggest lesson if found, if not found apologize and log to DB
                    fun handleUserQuery(query: String) {
                        val suggestion: String? = recommender.recommendLesson(query)

                        if (suggestion != null) {
                            // voice feedback
                            TTS.speak("I found a lesson: $suggestion.")
                            // if want to start the lesson right away: find its id then navigate
                            scope.launch {
                                val match = lessonDao.getAllLessons().firstOrNull { it.name == suggestion }
                                if (match != null) {
                                    // placeholder as we only have this lesson
                                    currentScreen = "lesson_phone"
                                }
                            }
                        } else {
                            TTS.speak("Sorry, we currently don't have a lesson on this.")
                            // persist the missing query to design it later
                            scope.launch {
                                missingLessonDao.insertMissingLesson(
                                    com.example.guideme.lessons.DbMissingLesson(queryText = query)
                                )
                            }
                        }
                    }

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

                    // 5. minimal dialog for typed input
                    if (showTextDialog) {
                        AlertDialog(
                            onDismissRequest = { showTextDialog = false },
                            title = { Text("Type your question") },
                            text = {
                                OutlinedTextField(
                                    value = typedQuery,
                                    onValueChange = { typedQuery = it },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    showTextDialog = false
                                    if (typedQuery.isNotBlank()) handleUserQuery(typedQuery)
                                    typedQuery = ""
                                }) { Text("OK") }
                            },
                            dismissButton = {
                                TextButton(onClick = { showTextDialog = false; typedQuery = "" }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }
                }

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
                    userEmail = userEmail,
                    onExit = {
                        // navigate back to your lessons menu
                        currentScreen = "main"
                    }
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
        onLessonsClick: () -> Unit,
        onLogoutClick: () -> Unit,
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
                Button(
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(130.dp),
                    shape = RoundedCornerShape(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainButtonColor,
                        contentColor = MainButtonContentColor
                    )
                ) {
                    Text("Logout", style = MaterialTheme.typography.bodyMedium)
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
        onStartPhoneLesson: () -> Unit,
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
        customerDao: CustomerDao,
        lessonDao: com.example.guideme.lessons.LessonDao? = null,
        missingLessonDao: com.example.guideme.lessons.MissingLessonDao? = null
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
                                "Click learn to go to the lessons menu, or click search to find a specific lesson."
                    )
                }
            )
        } else {
            // Show your existing main app once logged in
            MainScreen(
                modifier = modifier,
                lessonsRepo = lessonsRepo,
                userEmail = currentUser!!.email,   // 👈 add this
                onLogout = { currentUser = null },
                lessonDao = lessonDao,
                missingLessonDao = missingLessonDao
            )
        }
    }



    /* -------------------- Previews -------------------- */

    @Preview(showBackground = true, name = "Welcome")
    @Composable
    fun PreviewWelcome() {
        GuideMeTheme {
            WelcomeScreen(onSearchClick = {}, onLessonsClick = {}, onLogoutClick = {})
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



