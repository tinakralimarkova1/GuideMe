package com.example.guideme.phone

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.guideme.R
import com.example.guideme.tts.TTS
import kotlinx.coroutines.launch

private enum class FlashSim { AUTO, ON, OFF }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // permissions — still ask so the preview "feels" real
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) TTS.speak("Camera permission is needed to view the preview.")
    }

    LaunchedEffect(Unit) {
        launcher.launch(android.Manifest.permission.CAMERA)
        TTS.speak("This is the camera. Tap the big button to take a photo.")
    }

    // Fake flash overlay animation
    val flashAlpha = remember { Animatable(0f) }

    // Fake states
    var flash by remember { mutableStateOf(FlashSim.AUTO) }
    var zoom by remember { mutableStateOf(1.0f) }              // 1.0x .. 5.0x (UI only)
    var lastThumb by remember { mutableStateOf(androidx.compose.ui.graphics.ImageBitmap(1, 1)) }
    var hasPhoto by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        // Pretend Preview (no camera feed)
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Flash overlay for "shutter" effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .alpha(flashAlpha.value)
        )

        // Top bar (title + flash toggle — cycles Auto/On/Off)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Camera", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)

            FilledTonalIconButton(onClick = {
                flash = when (flash) {
                    FlashSim.AUTO -> FlashSim.ON
                    FlashSim.ON   -> FlashSim.OFF
                    FlashSim.OFF  -> FlashSim.AUTO
                }
                TTS.speak("Flash ${flash.name.lowercase()}")
            }) {
                when (flash) {
                    FlashSim.AUTO -> Icon(Icons.Filled.FlashAuto, contentDescription = "Flash Auto")
                    FlashSim.ON   -> Icon(Icons.Filled.FlashOn,   contentDescription = "Flash On")
                    FlashSim.OFF  -> Icon(Icons.Filled.FlashOff,  contentDescription = "Flash Off")
                }
            }
        }

        // Right-side controls: fake camera switch + vertical zoom slider
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Fake flip camera (UI + TTS only)
            FilledTonalIconButton(onClick = { TTS.speak("Switched camera") }) {
                Icon(Icons.Filled.Cameraswitch, contentDescription = "Switch camera")
            }

            // Vertical slider (UI only): rotate the container
            Box(
                modifier = Modifier
                    .height(200.dp)
                    .width(120.dp)
                    .graphicsLayer(rotationZ = -90f)
            ) {
                Slider(
                    value = zoom,
                    onValueChange = {
                        zoom = it
                    },
                    valueRange = 1.0f..5.0f,
                    steps = 3, // ticks between 1–5
                    modifier = Modifier.fillMaxSize(),
                    onValueChangeFinished = {
                        TTS.speak(String.format("%.1fx", zoom))
                    }
                )
            }
            Text(String.format("%.1fx", zoom))
        }

        // Bottom controls: thumbnail • shutter
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
                // Gallery thumbnail
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { TTS.speak("This opens your last photo in a real camera app.") },
                    contentAlignment = Alignment.Center
                ) {
                    if (hasPhoto) {
                        Image(
                            bitmap = lastThumb,
                            contentDescription = "Last capture",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(Icons.Filled.Photo, contentDescription = null)
                    }
                }

                // Capture button — fake shutter
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                        .clickable {
                            scope.launch {
                                // Flash animation
                                flashAlpha.snapTo(1f)
                                flashAlpha.animateTo(0f, tween(300))
                                TTS.speak("Photo captured!")

                                // Fake thumbnail from drawable
                                lastThumb = BitmapFactory
                                    .decodeResource(context.resources, R.drawable.ash_tree___geograph_org_uk___590710)
                                    .asImageBitmap()
                                hasPhoto = true
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("●", style = MaterialTheme.typography.titleLarge)
                }

                Spacer(Modifier.size(54.dp))
            }
        }
    }
}
