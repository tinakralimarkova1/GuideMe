package com.example.guideme.phone

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.guideme.R
import com.example.guideme.tts.TTS
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.graphicsLayer
private enum class FlashSim { AUTO, ON, OFF }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen() {
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
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Camera",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            FilledTonalIconButton(onClick = {
                flash = when (flash) {
                    FlashSim.AUTO -> FlashSim.ON
                    FlashSim.ON -> FlashSim.OFF
                    FlashSim.OFF -> FlashSim.AUTO
                }
                TTS.speak("Flash ${flash.name.lowercase()}")
            }) {
                when (flash) {
                    FlashSim.AUTO -> Icon(Icons.Filled.FlashAuto, contentDescription = "Flash Auto")
                    FlashSim.ON -> Icon(Icons.Filled.FlashOn, contentDescription = "Flash On")
                    FlashSim.OFF -> Icon(Icons.Filled.FlashOff, contentDescription = "Flash Off")
                }
            }
        }

        // ===== RIGHT SIDE CONTROLS =====
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            FilledTonalIconButton(onClick = { TTS.speak("Switched camera") }) {
                Icon(Icons.Filled.Cameraswitch, contentDescription = "Switch camera")
            }

            Box(
                modifier = Modifier
                    .height(200.dp)
                    .width(120.dp)
                    .graphicsLayer(rotationZ = -90f)
            ) {
                Slider(
                    value = zoom,
                    onValueChange = { zoom = it },
                    valueRange = 1.0f..5.0f,
                    steps = 3,
                    modifier = Modifier.fillMaxSize(),
                    onValueChangeFinished = {
                        TTS.speak(String.format("%.1fx", zoom))
                    }
                )
            }
            Text(String.format("%.1fx", zoom), color = Color.White)
        }

        // ===== BOTTOM CONTROLS =====
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 18.dp, start = 16.dp, end = 16.dp),
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
                            TTS.speak("This opens your last photo in a real camera app.")
                        },
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
                            scope.launch {
                                flashAlpha.snapTo(1f)
                                flashAlpha.animateTo(0f, tween(300))
                                TTS.speak("Photo captured!")
                                hasPhoto = true
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("●", style = MaterialTheme.typography.titleLarge, color = Color.White)
                }

                Spacer(Modifier.size(54.dp))
            }
        }
    }
}
