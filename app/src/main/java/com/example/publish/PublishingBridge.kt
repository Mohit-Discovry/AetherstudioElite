package com.example.publish

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import com.example.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Kotlin publishing bridge exposing native Java-backed artwork publishing,
 * media storage, sharing intents, and publication metadata to the WebView.
 */
class PublishingBridge(
    private val context: Context,
    private val webViewProvider: () -> WebView?
) {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val mainHandler = Handler(Looper.getMainLooper())

    @JavascriptInterface
    fun getGeminiApiKey(): String {
        return try {
            val field = BuildConfig::class.java.getField("GEMINI_API_KEY")
            (field.get(null) as? String)?.takeIf { it.isNotBlank() && it != "MY_GEMINI_API_KEY" } ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    @JavascriptInterface
    fun showToast(message: String) {
        mainHandler.post {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    @JavascriptInterface
    fun triggerHaptic(type: Int) {
        CanvasPublishHelper.performHapticFeedback(context, type)
    }

    @JavascriptInterface
    fun getPublishingInfo(): String {
        return CanvasPublishHelper.getPublishingMetadataJSON(context)
    }

    /**
     * Publishes and exports the artwork bitmap to the Android device Gallery / Pictures folder
     * using background thread (Dispatchers.IO) and Java CanvasPublishHelper.
     */
    @JavascriptInterface
    fun publishArtwork(title: String, base64Data: String, format: String) {
        triggerHaptic(1)
        scope.launch {
            val resultUri = withContext(Dispatchers.IO) {
                val bitmap = CanvasPublishHelper.decodeBase64ToBitmap(base64Data) ?: return@withContext null
                CanvasPublishHelper.saveArtworkToGallery(context, bitmap, title, format)
            }

            if (resultUri != null) {
                showToast("✨ Published to Device Gallery (Pictures/AetherStudio)!")
                notifyWebCallback("onArtworkPublishedSuccess", resultUri.toString())
            } else {
                showToast("Publish failed: Could not save image to gallery")
                notifyWebCallback("onArtworkPublishedError", "Storage write error")
            }
        }
    }

    /**
     * Shares the artwork bitmap with external Android apps (Instagram, X, Pinterest, Messages, etc.)
     */
    @JavascriptInterface
    fun shareArtwork(title: String, base64Data: String) {
        triggerHaptic(2)
        scope.launch {
            val success = withContext(Dispatchers.IO) {
                val bitmap = CanvasPublishHelper.decodeBase64ToBitmap(base64Data) ?: return@withContext false
                CanvasPublishHelper.shareArtworkIntent(context, bitmap, title)
            }

            if (!success) {
                showToast("Could not open Android share dialog")
            }
        }
    }

    /**
     * Publishes and exports generic documents (SVG, PSD, TIFF) to the Android device.
     */
    @JavascriptInterface
    fun publishDocument(title: String, base64Data: String, mimeType: String, extension: String) {
        triggerHaptic(1)
        scope.launch {
            val resultUri = withContext(Dispatchers.IO) {
                val data = CanvasPublishHelper.decodeBase64ToBytes(base64Data) ?: return@withContext null
                CanvasPublishHelper.saveDocumentToStorage(context, data, title, mimeType, extension)
            }

            if (resultUri != null) {
                showToast("✨ Published ${extension.uppercase()} to device storage!")
                notifyWebCallback("onArtworkPublishedSuccess", resultUri.toString())
            } else {
                showToast("Publish failed: Could not save $extension document")
                notifyWebCallback("onArtworkPublishedError", "Storage write error")
            }
        }
    }

    /**
     * Shares generic documents (SVG, PSD, TIFF) via Android system share intent.
     */
    @JavascriptInterface
    fun shareDocument(title: String, base64Data: String, mimeType: String, extension: String) {
        triggerHaptic(2)
        scope.launch {
            val success = withContext(Dispatchers.IO) {
                val data = CanvasPublishHelper.decodeBase64ToBytes(base64Data) ?: return@withContext false
                CanvasPublishHelper.shareDocumentIntent(context, data, title, mimeType, extension)
            }

            if (!success) {
                showToast("Could not open Android share dialog for $extension")
            }
        }
    }

    private fun notifyWebCallback(functionName: String, param: String) {
        mainHandler.post {
            val safeParam = param.replace("'", "\\'")
            webViewProvider()?.evaluateJavascript("window.$functionName && window.$functionName('$safeParam');", null)
        }
    }
}
