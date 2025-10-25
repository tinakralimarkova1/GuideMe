package com.example.guideme.phone

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView

// Keep these video imports for future implementation (unused for now)
import androidx.camera.video.OutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.VideoRecordEvent
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Recording
// ----

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.graphics.graphicsLayer
import com.example.guideme.tts.TTS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.content.ContextCompat

enum class CameraMode { PHOTO }  // video removed for now
enum class FlashMode { AUTO, ON, OFF }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    // ---- Permissions ----
    val permissions = remember {
        if (Build.VERSION.SDK_INT >= 33)
            arrayOf(android.Manifest.permission.CAMERA)
        else
            arrayOf(android.Manifest.permission.CAMERA)
    }
    var hasPermissions by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        hasPermissions = permissions.all { result[it] == true }
        if (!hasPermissions) TTS.speak("Camera permission is required.")
    }

    LaunchedEffect(Unit) {
        TTS.speak("You are in the camera. Tap the big button to take a photo.")
        launcher.launch(permissions)
    }

    // ---- Camera controller ----
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                LifecycleCameraController.IMAGE_CAPTURE or
                        LifecycleCameraController.IMAGE_ANALYSIS
            )
            cameraSelector = androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
            imageCaptureMode = ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
            // (Keeping video quality config out for now)
        }
    }
    LaunchedEffect(hasPermissions) {
        if (hasPermissions) controller.bindToLifecycle(lifecycleOwner)
    }

    // ---- UI state ----
    val mode = CameraMode.PHOTO
    var flash by remember { mutableStateOf(FlashMode.AUTO) }
    var zoom by remember { mutableFloatStateOf(1.0f) } // 1x..max
    var lastThumb by remember { mutableStateOf<Bitmap?>(null) }

    // Map flash to controller
    LaunchedEffect(flash) {
        controller.imageCaptureFlashMode = when (flash) {
            FlashMode.AUTO -> ImageCapture.FLASH_MODE_AUTO
            FlashMode.ON -> ImageCapture.FLASH_MODE_ON
            FlashMode.OFF -> ImageCapture.FLASH_MODE_OFF
        }
    }

    fun loadLatestImageThumb(context: Context, uri: Uri?): Bitmap? {
        return try {
            uri?.let {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            }
        } catch (_: Exception) { null }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Preview
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    this.controller = controller
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Top bar
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
                    FlashMode.AUTO -> FlashMode.ON
                    FlashMode.ON -> FlashMode.OFF
                    FlashMode.OFF -> FlashMode.AUTO
                }
                TTS.speak("Flash ${flash.name.lowercase(Locale.US)}")
            }) {
                when (flash) {
                    FlashMode.AUTO -> Icon(Icons.Filled.FlashAuto, contentDescription = "Flash Auto")
                    FlashMode.ON -> Icon(Icons.Filled.FlashOn, contentDescription = "Flash On")
                    FlashMode.OFF -> Icon(Icons.Filled.FlashOff, contentDescription = "Flash Off")
                }
            }
        }

        // Right-side: switch + zoom
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FilledTonalIconButton(onClick = {
                controller.cameraSelector =
                    if (controller.cameraSelector == androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA)
                        androidx.camera.core.CameraSelector.DEFAULT_FRONT_CAMERA
                    else
                        androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
                TTS.speak("Switched camera")
            }) {
                Icon(Icons.Filled.Cameraswitch, contentDescription = "Switch camera")
            }

            val cameraInfo = controller.cameraInfo
            val zoomState = cameraInfo?.zoomState?.value
            val maxZoom = zoomState?.maxZoomRatio ?: 4f
            val minZoom = zoomState?.minZoomRatio ?: 1f

            Box(
                modifier = Modifier
                    .height(200.dp)
                    .width(32.dp)
                    .graphicsLayer(rotationZ = -90f)
            ) {
                Slider(
                    value = zoom.coerceIn(minZoom, maxZoom),
                    onValueChange = {
                        zoom = it
                        controller.setZoomRatio(it)
                    },
                    valueRange = minZoom..maxZoom,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Text(String.format(Locale.US, "%.1fx", zoom.coerceIn(minZoom, maxZoom)))
        }

        // Bottom: thumbnail • shutter
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 18.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gallery thumb
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            TTS.speak("This opens your last photo in the gallery on a real phone.")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (lastThumb != null) {
                        Image(
                            bitmap = lastThumb!!.asImageBitmap(),
                            contentDescription = "Last capture",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(Icons.Filled.Photo, contentDescription = null)
                    }
                }

                // Shutter (photo only)
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                        .clickable(enabled = hasPermissions) {
                            if (!hasPermissions) {
                                TTS.speak("Please allow camera permission first.")
                                return@clickable
                            }
                            scope.launch(Dispatchers.IO) {
                                takePhotoWithController(context, controller) { savedUri, ok ->
                                    if (ok) {
                                        TTS.speak("Photo saved")
                                        lastThumb = loadLatestImageThumb(context, savedUri)
                                    } else {
                                        TTS.speak("Failed to save photo")
                                    }
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("●", style = MaterialTheme.typography.titleLarge)
                }

                // Spacer to balance layout
                Spacer(Modifier.size(54.dp))
            }
        }
    }
}

/** Capture a photo via LifecycleCameraController into MediaStore. */
private fun takePhotoWithController(
    context: Context,
    controller: LifecycleCameraController,
    onResult: (Uri?, Boolean) -> Unit
) {
    val resolver = context.contentResolver
    val name = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date()) + ".jpg"
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, name)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/GuideMe")
        }
    }

    // Build OutputFileOptions for MediaStore
    val output = ImageCapture.OutputFileOptions.Builder(
        resolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        values
    ).build()

    val executor = androidx.core.content.ContextCompat.getMainExecutor(context)

    controller.takePicture(
        output,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                onResult(outputFileResults.savedUri, true)
            }
            override fun onError(exception: ImageCaptureException) {
                onResult(null, false)
            }
        }
    )
}

