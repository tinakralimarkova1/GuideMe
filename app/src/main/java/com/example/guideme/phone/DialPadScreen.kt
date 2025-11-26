package com.example.guideme.phone

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.guideme.lessons.anchorId
import com.example.guideme.lessons.flash
import com.example.guideme.tts.TTS
import com.example.guideme.ui.theme.GuideMeTheme
import kotlinx.coroutines.delay

private val InstructionOverlaySpace = 10.dp



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialPadScreen(
    navController: NavController,
    initialNumber: String = "",
    autoDialOnStart: Boolean = false,
    onButtonPressed: (String) -> Unit = {},
    onNumberCommitted: (String) -> Unit = {},
    correctAnchor: String? = null,
    tappedIncorrectAnchor: String? = null,
    isAnchorAllowed: (String) -> Boolean = { true }
) {
    var number by remember { mutableStateOf("") }
    var isCalling by remember { mutableStateOf(false) }

    // Prefill & auto-dial if number provided, else speak intro
    LaunchedEffect(initialNumber, autoDialOnStart) {
        if (initialNumber.isNotBlank()) {
            number = initialNumber

            if (autoDialOnStart) {
                // Coming from Recents/Contacts → auto-dial
                TTS.speak("Phone dialing.")
                isCalling = true
            } else {
                // Coming from a lesson default → just prefilled, no call yet
                TTS.speak("The number $initialNumber is already typed in. Follow the instructions on the screen.")
            }
        } else {
            // No preset at all
            TTS.speak("You are now in the dial pad. You can type numbers and press call.")
        }
    }

    if (isCalling) {
        IncomingCallUI(
            number = number.ifEmpty { "Unknown" },
            onEndCall = {
                TTS.speak("Call ended.")
                number = ""
                isCalling = false
                onButtonPressed("DialPad.EndCall")

            }
        )
    } else {
        Scaffold(

            modifier = Modifier.fillMaxSize(),

//            bottomBar = {
//                Spacer(modifier = Modifier.height(70.dp))
//                Row(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)){
//                    BottomNavBar(navController, "dialpad")
//
//                    // 👇 Reserve an empty band at the bottom for the lesson overlay
//                    Spacer(Modifier.heightIn(min =280.dp))
//                }
//                        },
            contentWindowInsets = WindowInsets(0.dp, bottom = 0.dp)
        ) { padding ->

            val configuration = LocalConfiguration.current
            val screenHeightPx = configuration.screenHeightDp.dp
            val fakeUiSpace = screenHeightPx * 0.80f


            Column(modifier = Modifier.heightIn(max= fakeUiSpace)
                ,
            verticalArrangement = Arrangement.spacedBy(10.dp)) {


                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(

                            top = 74.dp,
                            bottom = 0.dp   // ⬅️ reserve room for the overlay
                        )
                        .padding(horizontal = 20.dp)
                        .weight(1f)


                        ,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = if (number.isEmpty()) "Enter number" else number.chunked(3)
                                .joinToString(" "),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (number.isEmpty()) 0.35f else 1f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .weight(1f)
                                .anchorId("DialPad.NumberField")
                        )
                        IconButton(
                            onClick = {
                                val anchor = "DialPad.Backspace"

                                if (!isAnchorAllowed(anchor)) {
                                    // Wrong time to use backspace → send as wrong tap, no delete
                                    onButtonPressed(anchor)
                                    return@IconButton
                                }

                                if (number.isNotEmpty()) {
                                    number = number.dropLast(1)
                                    onNumberCommitted(number)
                                }
                                // For the "correct" step 2002-5, we still want the event to hit the VM
                                onButtonPressed(anchor)
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .anchorId("DialPad.Backspace")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Backspace,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    Box(modifier = Modifier.weight(7f)) {

                        DialPadKeys(
                            onKey = { key ->
                                if (number.length < 20) {
                                    number += key
                                    TTS.speak(
                                        when (key) {
                                            "*" -> "star"
                                            "#" -> "pound"
                                            else -> key
                                        }
                                    )
                                    onNumberCommitted(number)
                                }
                            },
                            onButtonPressed = onButtonPressed,
                            tappedIncorrectAnchor = tappedIncorrectAnchor,
                            correctAnchor,
                            isAnchorAllowed
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            //.heightIn(min = 42.dp, max = 50.dp)// issue here i think?
                            .flash(tappedIncorrectAnchor, "DialPad.Call")   // ← apply here
                            .clip(RoundedCornerShape(16.dp))                // so the flash matches the button shape
                    ) {
                        Button(
                            onClick = {
                                onButtonPressed("DialPad.Call")
                                if (correctAnchor == "DialPad.Call") {
                                    if (number.isEmpty()) {
                                        TTS.speak("Please enter a number.")
                                    } else {
                                        TTS.speak("Phone dialing.")
                                        isCalling = true
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxSize()                              // button fills the flashing box
                                .anchorId("DialPad.Call"),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Call", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    // 👇 THIS is now the controlled gap between Call and the nav bar


                }
                Row(modifier = Modifier.fillMaxWidth().heightIn(max=80.dp)){
                    BottomNavBar(navController, "dialpad")

                    // 👇 Reserve an empty band at the bottom for the lesson overlay

                }
            }
        }




    }
}

@Composable
private fun IncomingCallUI(number: String, onEndCall: () -> Unit, onButtonPressed: (String) -> Unit = {}, tappedIncorrectAnchor:String? = null) {
    var isConnected by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        TTS.speak("Incoming call from $number.")
        delay(2000)
        TTS.speak("Connected.")
        isConnected = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0B0B)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (isConnected) "Call in progress..." else "Ringing...",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 18.sp
            )
            Text(
                text = number,
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE53935))
                    .clickable {
                        onEndCall()


                    }
                    .anchorId("DialPad.EndCall")
                    .flash(tappedIncorrectAnchor,"DialPad.EndCall"),
                contentAlignment = Alignment.Center
            ) {
                Text("End", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}

@Composable
private fun DialPadKeys(
    onKey: (String) -> Unit,
    onButtonPressed: (String) -> Unit = {},
    tappedIncorrectAnchor: String? = null,
    correctAnchor: String?,
    isAnchorAllowed: (String) -> Boolean   // 👈 new
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("*", "0", "#"),
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp)
            .anchorId("DialPad.KeysGrid")

            //.heightIn(max=600.dp)

            //.heightIn(max=400.dp)
    ) {
        rows.forEach { row ->
            Row(
                //horizontalArrangement = Arrangement.Center,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),

                ) {
                row.forEach { key ->
                    val anchor = "DialPad.key$key"

                    DialKey(
                        label = key,
                        modifier = Modifier

                            .fillMaxHeight(0.8f)   // take ~80% of row height
                            
                            .aspectRatio(1f)       // keep it square → circle after clip
                            .anchorId(anchor)
                            .weight(1f)
                            .flash(tappedIncorrectAnchor, anchor)



                    ) {
                        if (!isAnchorAllowed(anchor)) {
                            // Wrong key for this step → send as wrong tap, no typing
                            onButtonPressed(anchor)
                        } else {
                            // Allowed by VM → do the actual typing behaviour
                            onKey(key)
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun DialKey(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    )
{
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(10.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick(

            ) }

    ) {
        Text(
            text = label,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Dial Pad"
)
@Composable
private fun DialPadPreview() {
    GuideMeTheme {
        val navController = rememberNavController()

        DialPadScreen(
            navController = navController,
            initialNumber = "123 456 7890",
            autoDialOnStart = false,
            onButtonPressed = {},
            onNumberCommitted = {},
            correctAnchor = null,
            tappedIncorrectAnchor = null,
            isAnchorAllowed = { true }
        )
    }
}
