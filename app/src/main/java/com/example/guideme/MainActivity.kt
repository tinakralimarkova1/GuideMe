package com.example.guideme

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.guideme.NLP.IntentClassifier
import com.example.guideme.NLP.STT
import com.example.guideme.lessons.AccountRow
import com.example.guideme.lessons.AuthScreen
import com.example.guideme.lessons.CompletionDao
import com.example.guideme.lessons.CustomerDao
import com.example.guideme.lessons.DatabaseSeeder
import com.example.guideme.lessons.DbCustomer
import com.example.guideme.lessons.GuideMeDatabase
import com.example.guideme.lessons.LessonDao
import com.example.guideme.lessons.LessonHost
import com.example.guideme.lessons.LessonsRepository
import com.example.guideme.lessons.MissingLessonDao
import com.example.guideme.lessons.RoomLessonsRepository
import com.example.guideme.phone.CameraScreen
import com.example.guideme.phone.PhoneNavHost
import com.example.guideme.tts.TTS
import com.example.guideme.ui.theme.GuideMeTheme
import com.example.guideme.ui.theme.MainBackgroundGradient
import com.example.guideme.ui.theme.MainButtonColor
import com.example.guideme.ui.theme.MainButtonContentColor
import com.example.guideme.ui.theme.Transparent
import com.example.guideme.wifi.WifiNavHost
import kotlinx.coroutines.launch
import me.nikhilchaudhari.library.BuildConfig


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
            completionDao = db.completionDao(),
            defaultButtonDao = db.defaultButtonDao(),
            lessonDao = db.lessonDao()

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
    val context = LocalContext.current
    // SharedPreferences where we remember who is logged in
    val prefs = remember {
        context.getSharedPreferences("guideme_prefs", Context.MODE_PRIVATE)
    }

    // In-memory current user
    var currentUser by remember { mutableStateOf<DbCustomer?>(null) }

    // On first composition, try to restore the saved user
    LaunchedEffect(Unit) {
        val savedEmail = prefs.getString("logged_in_email", null)
        if (!savedEmail.isNullOrBlank()) {
            val user = customerDao.getCustomer(savedEmail)
            currentUser = user
        }
    }

    if (currentUser == null) {
        // Login / Register
        AuthScreen(
            modifier = modifier.fillMaxSize(),
            customerDao = customerDao,
            onAuthSuccess = { customer ->
                currentUser = customer

                // Persist login
                prefs.edit().putString("logged_in_email", customer.email).apply()

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
            onLogout = {
                // ✅ Clear persisted login on explicit logout
                prefs.edit().remove("logged_in_email").apply()
                currentUser = null
            },
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
                    onBack = {
                        TTS.speak("Returning to welcome.")
                        currentScreen = "welcome"
                    }
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
                        onTextSearch = { TTS.speak("Text search coming soon.") },
                        onBack = {TTS.speak("Returning to welcome.")
                            currentScreen = "welcome"}
                    )
                } else {
                    val scope = rememberCoroutineScope()

                    // Load lesson titles once
                    var lessonTitles by remember { mutableStateOf<List<String>>(emptyList()) }
                    LaunchedEffect(Unit) {
                        lessonTitles = lessonDao.getAllLessons().map { it.name }
                    }

                    var showTextDialog by remember { mutableStateOf(false) }
                    var typedQuery by remember { mutableStateOf("") }

                    SearchMenu(
                        modifier = modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        onVoiceSearch = {  },
                        onTextSearch = { TTS.speak("Opening text search.") },
                        onBack = {TTS.speak("Returning to welcome.")
                            currentScreen = "welcome"}
                    )
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
                    },
                    onStartLesson = { nextApp, nextLessonId ->
                        TTS.speak("Starting $nextApp lesson.")
                        selectedApp = nextApp
                        selectedLessonId = nextLessonId
                        currentScreen = "lesson"
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
                            //??

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
           // SkeuomorphicButton("hello",onLogoutClick, width = 80.dp)


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
                onClick = {
                    if (!BuildConfig.DEBUG) {
                        onLogoutClick()
                    } else {
                        // debug build – just ignore to keep Monkey from going to login
                        TTS.speak("Logout is disabled in testing mode.")
                    }
                },
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
    onBack: () -> Unit = {}
    //if need to check ui of apps seperately
//    onOpenCamera: () -> Unit = {},
//    onOpenPhone: () -> Unit = {},
//    onOpenWifi: () -> Unit = {}
) {
    val cameraIds = listOf(1001, 1002, 1003,1004)
    val phoneIds  = listOf(2001, 2002, 2003,2004)
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
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(vertical = 30.dp, horizontal = 15.dp)
                .heightIn(min = 40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MainButtonContentColor)
                .clickable { onBack() }
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MainButtonColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Back",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MainButtonColor,
                    fontSize = 20.sp
                )
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp, bottom = 24.dp)   // leave room for back chip
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)               // ✅ keeps it nice on tablets / big phones
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    text = "Lesson Menu",
                    color = MainButtonContentColor,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
                )

                // CAMERA group header (optional: still keep quick open)
                ExpandableLessonSection(
                    title = "Camera",
                    ids = cameraIds,
                    label = { label(it) },
                ) { id -> onStartLesson("Camera", id) }

                ExpandableLessonSection(
                    title = "Phone",
                    ids = phoneIds,
                    label = { label(it) },
                ) { id -> onStartLesson("Phone", id) }

                ExpandableLessonSection(
                    title = "Wi-Fi",
                    ids = wifiIds,
                    label = { label(it) },
                ) { id -> onStartLesson("WiFi", id) }
            }




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
            .heightIn(min = 48.dp), // issue here ,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MainButtonColor,
            contentColor = MainButtonContentColor
        ),
        contentPadding = PaddingValues(
            horizontal = 20.dp,
            vertical = 10.dp          // let it breathe vertically
        )
    ) {
        Text(text, style = MaterialTheme.typography.bodyLarge,maxLines = 2      )
    }
}

@Composable
private fun ExpandableLessonSection(
    title: String,
    ids: List<Int>,
    label: (Int) -> String,
    onStartLesson: (id: Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .animateContentSize()     // smooth open/close animation
    ) {

        // The clickable group header (button style)
        Button(
            onClick = { expanded = !expanded },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MainButtonColor,
                contentColor = MainButtonContentColor
            ),
            contentPadding = PaddingValues(
                horizontal = 20.dp,
                vertical = 10.dp
            )
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = MaterialTheme.typography.titleLarge,maxLines = 2)
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }
        }

        // Expanded content (your lesson buttons)
        if (expanded) {
            Spacer(Modifier.height(8.dp))

            ids.forEach { id ->
                LessonButton(text = label(id)) {
                    onStartLesson(id)
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}


@Composable
private fun SearchMenu(
    modifier: Modifier = Modifier,
    onVoiceSearch: () -> Unit,
    onTextSearch: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as Activity

    // reuse one speech + one intent classifier per composition
    val speech = remember { STT(activity) }
    val classifier = remember { IntentClassifier(context) }

    var queryText by rememberSaveable { mutableStateOf("") }
    var resultText by rememberSaveable { mutableStateOf<String?>(null) }
    var errorText by rememberSaveable { mutableStateOf<String?>(null) }
    var typingMode by rememberSaveable { mutableStateOf(false) }
    var isClassifying by rememberSaveable { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // for handling permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                speech.start()
            } else {
                errorText = "Microphone permission is required for voice search"
            }
        }
    )
    fun handleVoiceSearchClick() {
        // reset UI state
        errorText = null
        resultText = null
        queryText = ""
        typingMode = false

        // check if permission is already granted
        when (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)){
            PackageManager.PERMISSION_GRANTED -> {
                // permission is already available
                TTS.speak("Say your question after the beep...")
                speech.start()
            }
            else -> {
                // permission has not been granted so we launch the request
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    // collect speech results
    LaunchedEffect(Unit) {
        speech.results.collect { text ->
            queryText = text
            errorText = null
            resultText = "Thinking..."
            isClassifying = true
            scope.launch {
                try {
                    val prediction = classifier.classify(text)
                    if (prediction == null || prediction.confidence < 0.6f) {
                        resultText = null
                        errorText = "Sorry, we do not have a lesson on that yet"
                    } else {
                        // map label to lesson name
                        val lessonName = prediction.label
                        print(lessonName)
                        resultText = "Lesson suggested:\n$lessonName\n"
                    }
                } finally {
                    isClassifying = false
                }
            }
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MainBackgroundGradient)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(vertical = 30.dp, horizontal = 15.dp)
                .heightIn(min = 40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MainButtonContentColor)
                .clickable { onBack() }
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MainButtonColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Back",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MainButtonColor,
                    fontSize = 20.sp
                )
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp, bottom = 24.dp)
                .verticalScroll(rememberScrollState()),     // ✅ scroll if it doesn’t fit
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "Search",
                    color = MainButtonContentColor,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )


                // voice button
                Button(
                    onClick = {
                        // call function that handles permission


                        handleVoiceSearchClick()
                        onVoiceSearch()
                    },
                    modifier = Modifier
                        .fillMaxWidth()

                        .heightIn(min = 100.dp),
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
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            "Say your question",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // text button
                Button(
                    onClick = {
                        errorText = null
                        resultText = null
                        queryText = ""
                        onTextSearch()
                        typingMode = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp),
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

                // typing area (shown after pressing "type your question" button)
                if (typingMode) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()


                    ) {
                        OutlinedTextField(
                            value = queryText,
                            onValueChange = {
                                queryText = it
                                errorText = null
                            },
                            label = { Text("Type here", color = MainButtonContentColor) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MainButtonContentColor,
                                unfocusedBorderColor = MainButtonContentColor,
                                disabledBorderColor = MainButtonContentColor.copy(alpha = 0.5f),
                                errorBorderColor = MaterialTheme.colorScheme.error
                            )

                        )
                        Button(
                            onClick = {
                                val text = queryText.trim()
                                if (text.isNotEmpty() && !isClassifying) { // prevent re-clicks
                                    errorText = null
                                    resultText = null // clear previous card results
                                    isClassifying = true
                                    scope.launch {
                                        try { // use try-finally to guarantee state is reset
                                            val prediction = classifier.classify(text)
                                            if (prediction == null || prediction.confidence < 0.6f) {
                                                resultText = null
                                                errorText =
                                                    "Sorry, we don't have a lesson on that yet"
                                            } else {
                                                val lessonName = prediction.label
                                                resultText = "Suggested lesson:\n$lessonName\n"
                                            }
                                        } finally {
                                            isClassifying = false
                                        }
                                    }
                                }
                            },
                            enabled = !isClassifying,
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 550.dp),
                            shape = RoundedCornerShape(40.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MainButtonColor,
                                contentColor = MainButtonContentColor
                            )
                        ) {
                            Text(if (isClassifying) "Thinking..." else "Search")
                        }
                    }
                }
                // result or error display
                if (!queryText.isBlank()) {
                    Text(
                        text = "You asked: \"$queryText\"",
                        color = MainButtonContentColor,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 30.dp)
                    )
                }
                resultText?.let { msg ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MainButtonColor
                        )
                    ) {
                        Text(
                            text = msg,
                            modifier = Modifier.padding(16.dp),
                            color = MainButtonContentColor,
                            style = MaterialTheme.typography.labelMedium.copy(fontSize = 25.sp)
                        )
                    }
                }
                errorText?.let { msg ->
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 30.dp)
                    )
                }
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewSearchMenu() {
    // Fake activity wrapper so `context as Activity` inside SearchMenu does NOT crash
    val fakeActivity = object : Activity() {}

    CompositionLocalProvider(
        LocalContext provides fakeActivity
    ) {
        MaterialTheme {
            SearchMenu(
                modifier = Modifier.fillMaxSize(),
                onVoiceSearch = {},
                onTextSearch = {},
                onBack = {}

            )
        }
    }
}

