package com.example.guideme.phone

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guideme.R
import com.example.guideme.lessons.anchorId
import com.example.guideme.tts.TTS
import kotlinx.coroutines.launch

private enum class FlashSim { AUTO, ON, OFF }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    onAnchorTapped: (String) -> Unit = {},
    correctAnchor: String? = null,
    tappedIncorrectAnchor: String? = null,
    isAnchorAllowed: (String) -> Boolean = { true },
    defaultStates: Map<String, String> = emptyMap()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // permissions (still requested for realism)
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) TTS.speak("Camera permission is needed to view the preview.")
    }

    LaunchedEffect(Unit) {
        launcher.launch(android.Manifest.permission.CAMERA)
        TTS.speak("This is the camera. Tap the big button to take a photo.")
    }

    val flashAlpha = remember { Animatable(0f) }

    val zoomDefault = defaultStates["Camera.ZoomSlider"]?.toFloatOrNull() ?: 1.0f
    val flashDefault = when (defaultStates["Camera.FlashButton"]?.uppercase()) {
        "ON" -> FlashSim.ON
        "OFF" -> FlashSim.OFF
        "AUTO", null, "" -> FlashSim.AUTO
        else -> FlashSim.AUTO
    }

    var flash by remember { mutableStateOf(FlashSim.AUTO) }
    var zoom by remember { mutableStateOf(1.0f) }
    var lastThumbRes by remember { mutableStateOf(R.drawable.ash_tree___geograph_org_uk___590710) }
    var hasPhoto by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // ===== CAMERA "PREVIEW" BACKGROUND =====
        Image(
            painter = painterResource(id = R.drawable.ash_tree___geograph_org_uk___590710),
            contentDescription = "Simulated camera preview",
            modifier = Modifier
                .fillMaxSize()
                // Add smooth zoom based on the slider
                .graphicsLayer(
                    scaleX = zoom,
                    scaleY = zoom,
                    transformOrigin = androidx.compose.ui.graphics.TransformOrigin.Center
                ),
            contentScale = ContentScale.Crop
        )

        // ===== FLASH OVERLAY =====
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color.White)
//                .alpha(flashAlpha.value)
//        )

        // ===== TOP BAR =====
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, end = 40.dp, start =  20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Camera",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            val flashButtonAnchorId = "Camera.FlashButton"
            FilledTonalIconButton(onClick = {
                onAnchorTapped(flashButtonAnchorId)
                flash = when (flash) {
                    FlashSim.AUTO -> FlashSim.ON
                    FlashSim.ON -> FlashSim.OFF
                    FlashSim.OFF -> FlashSim.AUTO
                }
                TTS.speak("Flash ${flash.name.lowercase()}")
            },
                modifier = Modifier.anchorId("Camera.FlashButton")){
                when (flash) {
                    FlashSim.AUTO -> Icon(Icons.Filled.FlashAuto, contentDescription = "Flash Auto")
                    FlashSim.ON -> Icon(Icons.Filled.FlashOn, contentDescription = "Flash On")
                    FlashSim.OFF -> Icon(Icons.Filled.FlashOff, contentDescription = "Flash Off")
                }
            }
        }
        Box(modifier = Modifier.fillMaxWidth().height(700.dp).anchorId("Camera.Screen"))

        // ===== RIGHT SIDE CONTROLS =====
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width((100.dp))
                .height(300.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            FilledTonalIconButton(
                onClick = {
                    onAnchorTapped("Camera.SwitchCamera")
                    TTS.speak("Switched camera") },
                modifier = Modifier.anchorId("Camera.SwitchCamera")) {
                Icon(Icons.Filled.Cameraswitch, contentDescription = "Switch camera")
            }

            Box(
                modifier = Modifier
                    .height(150.dp)
                    .width(140.dp)
                    .anchorId("Camera.ZoomSlider")

            ) {
                Slider(
                    value = zoom,
                    onValueChange = { zoom = it },
                    valueRange = 1.0f..5.0f,
                    steps = 3,
                    modifier = Modifier
                        .fillMaxSize()
                        .height(100.dp)

                        .graphicsLayer(rotationZ = -90f)
                        .height(60.dp),
                    onValueChangeFinished = {
                        onAnchorTapped("Camera.ZoomValue$zoom")
                        TTS.speak(String.format("%.1fx", zoom))
                    }
                )
            }
            Text(
                String.format("%.1fx", zoom),
                color = Color.White,
                modifier = Modifier.anchorId("Camera.ZoomLabel"))
        }

        // ===== BOTTOM CONTROLS =====
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 148.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Thumbnail
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            onAnchorTapped("Camera.Gallery")
                            TTS.speak("This opens your last photo in a real camera app.")
                        }
                        .anchorId("Camera.Gallery"),
                    contentAlignment = Alignment.Center
                ) {
                    if (hasPhoto) {
                        Image(
                            painter = painterResource(id = lastThumbRes),
                            contentDescription = "Last capture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Filled.Photo, contentDescription = null)
                    }
                }

                // Capture button
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        .clickable {
                            onAnchorTapped("Camera.Capture")
                            scope.launch {
                                flashAlpha.snapTo(1f)
                                flashAlpha.animateTo(0f, tween(300))
                                TTS.speak("Photo captured!")
                                hasPhoto = true
                            }
                        }
                        .anchorId("Camera.Capture"),
                    contentAlignment = Alignment.Center
                ) {
                    Text("●", style = MaterialTheme.typography.titleLarge, color = Color.White)
                }

                Spacer(Modifier.size(54.dp))
            }
        }
    }
}
@Composable
@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Camera Screen Preview"
)
fun CameraScreenPreview() {
    // Optionally wrap it in your app’s theme if you have one
    MaterialTheme {
        CameraScreen()
    }
}
