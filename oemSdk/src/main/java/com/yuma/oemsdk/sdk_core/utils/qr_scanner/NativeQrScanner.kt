package com.yumaoem.core.utils.qr_scanner

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Size
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.yuma.oemsdk.sdk_core.utils.qr_scanner.BarcodeAnalyzer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


/**
 * Android actual implementation of [NativeQrScanner].
 *
 * Uses CameraX for camera preview and lifecycle management, with Google ML Kit
 * Barcode Scanning for QR code detection.
 *
 * Key design decisions:
 * - Permission: Checks and requests CAMERA permission before initialising CameraX.
 *   Shows a styled ModalBottomSheet on denial, matching the app's error bottom sheet design.
 *   If permanently denied, directs user to App Settings.
 * - Resolution: 1920×1080 for optimal small QR code detection at distance.
 * - Backpressure: STRATEGY_KEEP_ONLY_LATEST — always analyze the freshest frame.
 * - Auto-zoom: ML Kit's ZoomSuggestionOptions — when a barcode is detected but
 *   too far to decode, the camera automatically zooms in.
 * - Debounce: 100ms minimal cooldown to prevent duplicate rapid-fire callbacks.
 * - Lifecycle: binds/unbinds camera with LocalLifecycleOwner for proper cleanup.
 * - Torch: controlled via Camera.cameraControl.enableTorch().
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun NativeQrScanner(
    modifier: Modifier,
    flashlightOn: Boolean,
    onQrCodeScanned: (String) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var wasPermissionDenied by remember { mutableStateOf(false) }

    var isPermanentlyDenied by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            wasPermissionDenied = true
            isPermanentlyDenied = activity?.let {
                !ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.CAMERA)
            } ?: false
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(modifier = modifier) {
        if (hasCameraPermission) {
            CameraPreviewContent(
                modifier = Modifier.fillMaxSize(),
                flashlightOn = flashlightOn,
                onQrCodeScanned = onQrCodeScanned,
                onFailure = onFailure
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            )
        }
    }

    // ── Bottom sheet shown after permission denial ──
    if (wasPermissionDenied && !hasCameraPermission) {
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
            confirmValueChange = { it != SheetValue.Hidden }
        )

        ModalBottomSheet(
            dragHandle = null,
            onDismissRequest = { /* non-dismissible */ },
            sheetState = sheetState
        ) {
            BackHandler(enabled = true) {}

            if (isPermanentlyDenied) {
                // Permanently denied → direct to App Settings
                CameraPermissionSettingsContent(
                    onOpenSettingsClicked = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }
                )
            } else {
                // Temporarily denied → allow retry
                CameraPermissionDeniedContent(
                    onRetry = {
                        wasPermissionDenied = false
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                )
            }
        }
    }
}

/**
 * Bottom sheet content for temporary camera permission denial.
 * Matches the [ErrorBottomSheetContent] design from feature-home.
 */
@Composable
private fun CameraPermissionDeniedContent(
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(top = 24.dp, bottom = 38.dp, start = 20.dp, end = 20.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Camera permission required",
            textAlign = TextAlign.Center,
            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
            color = Color(0xFF1A1A1A)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Camera access is needed to scan QR codes",
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            color = Color(0xFF717171),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(28.dp))
        androidx.compose.material3.Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(38.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1A1A1A)
            )
        ) {
            Text(
                text = "Grant Permission",
                modifier = Modifier.padding(vertical = 6.dp),
                style = androidx.compose.material3.MaterialTheme.typography.labelLarge,
                color = Color.White
            )
        }
    }
}

/**
 * Bottom sheet content for permanent camera permission denial.
 * Directs the user to open App Settings.
 */
@Composable
private fun CameraPermissionSettingsContent(
    onOpenSettingsClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(top = 24.dp, bottom = 38.dp, start = 20.dp, end = 20.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Settings,
            contentDescription = "Camera permission denied",
            modifier = Modifier.size(48.dp),
            tint = Color(0xFFE53935)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Camera permission denied",
            textAlign = TextAlign.Center,
            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
            color = Color(0xFF1A1A1A)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Camera permission was permanently denied.\nPlease enable it from App Settings to scan QR codes.",
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            color = Color(0xFF717171),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(28.dp))
        androidx.compose.material3.Button(
            onClick = onOpenSettingsClicked,
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(38.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1A1A1A)
            )
        ) {
            Text(
                text = "Open Settings",
                modifier = Modifier.padding(vertical = 6.dp),
                style = androidx.compose.material3.MaterialTheme.typography.labelLarge,
                color = Color.White
            )
        }
    }
}

/**
 * Internal composable that sets up CameraX preview + ML Kit barcode analysis.
 * Only called after CAMERA permission has been confirmed.
 */
@Composable
private fun CameraPreviewContent(
    modifier: Modifier,
    flashlightOn: Boolean,
    onQrCodeScanned: (String) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var lastScanTime by remember { mutableLongStateOf(0L) }

    val previewView = remember {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.PERFORMANCE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val analysisExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    var camera by remember { mutableStateOf<Camera?>(null) }

    LaunchedEffect(flashlightOn) {
        camera?.cameraControl?.enableTorch(flashlightOn)
    }

    DisposableEffect(lifecycleOwner) {
        val analyzer = BarcodeAnalyzer(
            onBarcodeDetected = { value ->
                val now = System.currentTimeMillis()
                if (now - lastScanTime > 300L) {
                    lastScanTime = now
                    onQrCodeScanned(value)
                }
            },
            onFailure = onFailure,
            onZoomRequested = { suggestedZoomRatio ->
                camera?.let {
                    it.cameraControl.setZoomRatio(suggestedZoomRatio)
                    true
                } ?: false
            },
            maxZoomRatio = 8f
        )

        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder()
                    .build()
                    .also { it.surfaceProvider = previewView.surfaceProvider }

                val resolutionSelector = ResolutionSelector.Builder()
                    .setResolutionStrategy(
                        ResolutionStrategy(
                            Size(1920, 1080),
                            ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
                        )
                    )
                    .build()

                val imageAnalysis = ImageAnalysis.Builder()
                    .setResolutionSelector(resolutionSelector)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { it.setAnalyzer(analysisExecutor, analyzer) }

                cameraProvider.unbindAll()

                camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalysis
                )

                camera?.cameraControl?.enableTorch(flashlightOn)
            } catch (e: Exception) {
                onFailure(e)
            }
        }, ContextCompat.getMainExecutor(context))

        onDispose {
            analyzer.close()
            try {
                val cameraProvider = cameraProviderFuture.get()
                cameraProvider.unbindAll()
            } catch (_: Exception) { }
            analysisExecutor.shutdown()
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    )
}

