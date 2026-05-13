package com.yuma.oemsdk.sdk_core.utils.qr_scanner

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.ZoomSuggestionOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.atomic.AtomicBoolean

/**
 * CameraX [ImageAnalysis.Analyzer] that uses Google ML Kit to detect QR codes.
 *
 * Design decisions:
 * - Configured for QR codes only ([Barcode.FORMAT_QR_CODE]) for maximum scanning speed.
 * - [enableAllPotentialBarcodes] enabled so ML Kit returns bounding boxes even for
 *   partially-visible codes — useful for the auto-zoom feature.
 * - Auto-zoom: When ML Kit detects a barcode but can't decode it (too small/far),
 *   it triggers [onZoomRequested] with a suggested zoom ratio. The camera then zooms
 *   in automatically for better readability.
 * - Thread-safe frame throttling via [AtomicBoolean]: skips frames while the previous
 *   analysis is still in-flight, preventing frame queue buildup and lag.
 * - Always closes [ImageProxy] (in addOnCompleteListener) to prevent camera stalls.
 *
 * @param onBarcodeDetected Called with the raw QR code value on ML Kit's internal thread.
 * @param onFailure         Called when ML Kit encounters a processing error.
 * @param onZoomRequested   Called when ML Kit suggests zooming in for a distant barcode.
 *                          Return true if zoom was applied, false otherwise.
 * @param maxZoomRatio      Maximum zoom ratio the camera hardware supports.
 */
internal class BarcodeAnalyzer(
    private val onBarcodeDetected: (String) -> Unit,
    private val onFailure: (Exception) -> Unit,
    onZoomRequested: ((Float) -> Boolean)? = null,
    maxZoomRatio: Float = 8f
) : ImageAnalysis.Analyzer {

    private val isProcessing = AtomicBoolean(false)

    private val scanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .apply {
                if (onZoomRequested != null) {
                    setZoomSuggestionOptions(
                        ZoomSuggestionOptions.Builder(onZoomRequested)
                            .setMaxSupportedZoomRatio(maxZoomRatio)
                            .build()
                    )
                }
            }
            .build()
    )

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        if (!isProcessing.compareAndSet(false, true)) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            isProcessing.set(false)
            imageProxy.close()
            return
        }

        val inputImage = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )

        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                val qrCode = barcodes.firstOrNull { it.rawValue != null }
                qrCode?.rawValue?.let { value ->
                    onBarcodeDetected(value)
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
            .addOnCompleteListener {
                isProcessing.set(false)
                imageProxy.close()
            }
    }

    /**
     * Release the ML Kit scanner resources.
     * Call this when the camera is unbound or the composable is disposed.
     */
    fun close() {
        scanner.close()
    }
}
