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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.guideme.lessons.*
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
import com.example.guideme.lessons.GuideMeDatabase
import com.example.guideme.lessons.LessonsRepository
import com.example.guideme.lessons.RoomLessonsRepository
import androidx.lifecycle.lifecycleScope
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
import com.example.guideme.NLP.STT
import com.example.guideme.lessons.DbMissingLesson
import kotlinx.coroutines.launch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.TopAppBarDefaults





class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Text-to-Speech
        TTS.init(this) {
            // We speak the welcome message after login in GuideMeRoot
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

        // Seed lessons/instructions (simple check inside seeder)
        lifecycleScope.launch {
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
                            missingLessonDao = db.missingLessonDao(),
                            completionDao = db.completionDao()

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

/* -------------------- Root & Main -------------------- */

@Composable
fun GuideMeRoot(
    modifier: Modifier = Modifier,
    lessonsRepo: LessonsRepository,
    customerDao: CustomerDao,
    lessonDao: LessonDao? = null,
    missingLessonDao: MissingLessonDao? = null,
    completionDao: CompletionDao
) {
    var currentUser by rememberSaveable { mutableStateOf<DbCustomer?>(null) }

    if (currentUser == null) {
        // Login / Register
        AuthScreen(
            modifier = modifier.fillMaxSize(),
            customerDao = customerDao,
            onAuthSuccess = { customer ->
                currentUser = customer
                TTS.speak(
                    "Welcome to Guide Me. " +
                            "Click learn to go to the lessons menu, or click search to find a specific lesson."
                )
            }
        )
    } else {
        // Main app after login
        MainScreen(
            modifier = modifier,
            lessonsRepo = lessonsRepo,
            userEmail = currentUser!!.email,
            onLogout = { currentUser = null },
            lessonDao = lessonDao,
            missingLessonDao = missingLessonDao,
            completionDao = completionDao,
            customerDao = customerDao,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    lessonsRepo: LessonsRepository,
    userEmail: String,
    lessonDao: LessonDao? = null,
    missingLessonDao: MissingLessonDao? = null,
    onLogout: () -> Unit = {},
    customerDao: CustomerDao,
    completionDao: CompletionDao
) {
    Column(modifier = modifier) {
        // Screens: welcome -> main (lessons menu) -> phone/camera/wifi/lesson/search
        var currentScreen by remember { mutableStateOf("welcome") }

        // For launching any lesson
        var selectedApp by remember { mutableStateOf<String?>(null) }
        var selectedLessonId by remember { mutableStateOf<Int?>(null) }

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
                    },
                    onAccountClick = {
                        TTS.speak("Opening your account.")
                        currentScreen = "account"
                    }
                )
            }

            "main" -> {
                LessonsMenu(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    lessonDao = lessonDao,
                    onStartLesson = { appName, lessonId ->
                        TTS.speak("Starting $appName lesson.")
                        selectedApp = appName
                        selectedLessonId = lessonId
                        currentScreen = "lesson"
                    },
//                    onOpenCamera = {
//                        TTS.speak("Opening Camera.")
//                        currentScreen = "camera"
//                    },
//                    onOpenPhone = {
//                        TTS.speak("Opening Phone.")
//                        currentScreen = "phone"
//                    },
//                    onOpenWifi = {
//                        TTS.speak("Opening Wi-Fi.")
//                        currentScreen = "wifi"
//                    }
                )
                BackHandler {
                    TTS.speak("Returning to welcome.")
                    currentScreen = "welcome"
                }
            }

            "search" -> {
                if (lessonDao == null || missingLessonDao == null) {
                    SearchMenu(
                        modifier = modifier.fillMaxSize().padding(24.dp),
                        onVoiceSearch = { TTS.speak("Voice search coming soon.") },
                        onTextSearch = { TTS.speak("Text search coming soon.") }
                    )
                } else {
                    val scope = rememberCoroutineScope()

                    // Load lesson titles once
                    var lessonTitles by remember { mutableStateOf<List<String>>(emptyList()) }
                    LaunchedEffect(Unit) {
                        lessonTitles = lessonDao.getAllLessons().map { it.name }
                    }

                    val recommender = remember(lessonTitles) {
                        com.example.guideme.NLP.IntentLessonRecommender(lessonTitles)
                    }

                    var showTextDialog by remember { mutableStateOf(false) }
                    var typedQuery by remember { mutableStateOf("") }

                    fun handleUserQuery(query: String) {
                        val suggestion: String? = recommender.recommendLesson(query)
                        if (suggestion != null) {
                            TTS.speak("I found a lesson: $suggestion.")
                            scope.launch {
                                val match = lessonDao.getAllLessons().firstOrNull { it.name == suggestion }
                                if (match != null) {
                                    selectedApp = inferAppFromId(match.id)
                                    selectedLessonId = match.id
                                    currentScreen = "lesson"
                                }
                            }
                        } else {
                            TTS.speak("Sorry, we currently don't have a lesson on this.")
                            scope.launch { missingLessonDao.insertMissingLesson(DbMissingLesson(queryText = query)) }
                        }
                    }

                    SearchMenu(
                        modifier = modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        onVoiceSearch = { TTS.speak("Voice search coming soon. Say your question after the beep.") },
                        onTextSearch = { TTS.speak("Opening text search.") }
                    )

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
                                TextButton(onClick = { showTextDialog = false; typedQuery = "" }) { Text("Cancel") }
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

            // Generic lesson route: launches LessonHost with selected app + id
            "lesson" -> {
                val app = selectedApp ?: return
                val lid = selectedLessonId ?: return
                LessonHost(
                    appName = app,
                    lessonId = lid,
                    repo = lessonsRepo,
                    userEmail = userEmail,
                    onExit = {
                        TTS.speak("Returning to lessons menu.")
                        currentScreen = "main"
                    }
                )
                BackHandler {
                    TTS.speak("Returning to lessons menu.")
                    currentScreen = "main"
                }
            }

            "account" -> {
                var user by remember { mutableStateOf<DbCustomer?>(null) }
                var rows by remember { mutableStateOf<List<AccountRow>>(emptyList()) }

                LaunchedEffect(userEmail) {
                    user = customerDao.getCustomer(userEmail)
                    rows = completionDao.getAccountRows(userEmail)
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MainBackgroundGradient)
                ) {
                    Scaffold(
                        containerColor = Color.Transparent, // let gradient show
                        topBar = {
                            TopAppBar(
                                title = { Text("My Account", color = MainButtonContentColor) },
                                navigationIcon = {
                                    TextButton(onClick = {
                                        TTS.speak("Returning to Welcome.")
                                        currentScreen = "welcome"
                                    }) { Text("Back", color = MainButtonContentColor) }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = Color.Transparent,
                                    titleContentColor = MainButtonContentColor,
                                    navigationIconContentColor = MainButtonContentColor
                                )
                            )
                        }
                    ) { padding ->
                        Column(
                            Modifier
                                .padding(padding)
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Header text using the same purple content color
                            Text("Username: ${user?.name ?: "-"}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MainButtonContentColor
                            )
                            Text("Email: $userEmail",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MainButtonContentColor
                            )

                            Divider(Modifier.padding(vertical = 8.dp), color = MainButtonContentColor.copy(alpha = 0.2f))
                            Text("Your Lessons",
                                style = MaterialTheme.typography.titleMedium,
                                color = MainButtonContentColor
                            )

                            // Centered, scrollable list
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(14.dp),
                                contentPadding = PaddingValues(bottom = 28.dp)
                            ) {
                                items(rows) { r ->
                                    Row( // centers each card horizontally
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        ElevatedCard(
                                            modifier = Modifier
                                                .fillMaxWidth(0.92f)     // responsive width
                                                .widthIn(max = 520.dp),  // keep a nice max on tablets
                                            shape = RoundedCornerShape(20.dp)
                                        ) {
                                            Column(
                                                Modifier.padding(16.dp),
                                                verticalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Text(r.lessonName,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    color = MainButtonContentColor
                                                )
                                                if (!r.unmetPrereqs.isNullOrBlank()) {
                                                    Text(
                                                        "Requires: ${r.unmetPrereqs}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.error
                                                    )
                                                }
                                                Text("Status: ${r.status}",      color = MainButtonContentColor)
                                                Text("Time Spent: ${r.timeSpent}", color = MainButtonContentColor)
                                                Text("Errors: ${r.errorCount}",    color = MainButtonContentColor)
                                                Text("Attempts: ${r.attempts}",    color = MainButtonContentColor)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                BackHandler {
                    TTS.speak("Returning to Welcome.")
                    currentScreen = "welcome"
                }
            }


            else -> {}
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
    onAccountClick: () -> Unit,
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

            Button(
                onClick = onAccountClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
                    .height(100.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainButtonColor,
                    contentColor = MainButtonContentColor
                )
            ) {
                Text("My Account", style = MaterialTheme.typography.headlineSmall)
            }

        }
    }
}

/**
 * 9-button Lessons Menu:
 * - Camera: 1001, 1002, 1003
 * - Phone : 2001, 2002, 2003
 * - Wi-Fi : 3001, 3002, 3003
 * Labels are fetched from Room (Lessons.name); fallback "Lesson ####" if DAO is null or not found.
 */
@Composable
private fun LessonsMenu(
    modifier: Modifier = Modifier,
    lessonDao: LessonDao? = null,
    onStartLesson: (appName: String, lessonId: Int) -> Unit,
    //if need to check ui of apps seperately
//    onOpenCamera: () -> Unit = {},
//    onOpenPhone: () -> Unit = {},
//    onOpenWifi: () -> Unit = {}
) {
    val cameraIds = listOf(1001, 1002, 1003)
    val phoneIds  = listOf(2001, 2002, 2003)
    val wifiIds   = listOf(3001, 3002, 3003)

    var lessonNames by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }

    LaunchedEffect(lessonDao) {
        if (lessonDao == null) return@LaunchedEffect
        val ids = cameraIds + phoneIds + wifiIds
        val map = buildMap {
            for (id in ids) {
                val row = lessonDao.getLessonById(id) // ensure this exists in LessonDao
                put(id, row?.name ?: "Lesson $id")
            }
        }
        lessonNames = map
    }

    fun label(id: Int) = lessonNames[id] ?: "Lesson $id"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MainBackgroundGradient)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Lesson Menu",
                color = MainButtonContentColor,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(top = 60.dp, bottom = 12.dp)
            )

            // CAMERA group header (optional: still keep quick open)
            Text(
                text = "Camera",
                color = MainButtonContentColor,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 6.dp)
            )
            cameraIds.forEach { id ->
                LessonButton(label(id)) { onStartLesson("Camera", id) }
            }

            Text(
                text = "Phone",
                color = MainButtonContentColor,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 12.dp)
            )
            phoneIds.forEach { id ->
                LessonButton(label(id)) { onStartLesson("Phone", id) }
            }

            Text(
                text = "Wi-Fi",
                color = MainButtonContentColor,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 12.dp)
            )
            wifiIds.forEach { id ->
                LessonButton(label(id)) { onStartLesson("WiFi", id) }
            }


            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun LessonButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
            .height(48.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MainButtonColor,
            contentColor = MainButtonContentColor
        )
    ) {
        Text(text, style = MaterialTheme.typography.bodyLarge)
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

/* -------------------- Helpers -------------------- */

private fun inferAppFromId(id: Int): String =
    when (id / 1000) {
        1 -> "Camera"
        2 -> "Phone"
        3 -> "WiFi"
        else -> "Phone"
    }

/* -------------------- Previews -------------------- */

@Preview(showBackground = true, name = "Welcome")
@Composable
fun PreviewWelcome() {
    GuideMeTheme {
        WelcomeScreen(onSearchClick = {}, onLessonsClick = {}, onLogoutClick = {}, onAccountClick = {})
    }
}

@Preview(showBackground = true, name = "Lessons Menu")
@Composable
fun PreviewLessonsMenu() {
    GuideMeTheme {
        LessonsMenu(
            lessonDao = null, // previews won't query DB
            onStartLesson = { _, _ -> }
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
