package com.example

import android.app.Application
import android.os.Build
import android.util.Log
import android.webkit.WebView
import com.example.publish.CanvasPublishHelper
import java.io.File

/**
 * Custom Application class for AetherStudio Elite.
 * Initializes logging, crash reporting guards, WebView cache directories, and publication setup.
 */
class AetherApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "AetherStudio Elite initialized. Build: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")

        // Multi-process data directory isolation for Android P+
        setupWebViewProcessIsolation()

        // Pre-create Chromium WebView Code Cache directories to prevent simple_backend_impl directory creation errors
        ensureWebViewCacheDirectories()

        val pubInfo = CanvasPublishHelper.getPublishingMetadataJSON(this)
        Log.d(TAG, "Publishing info: $pubInfo")
    }

    private fun setupWebViewProcessIsolation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                val processName = getProcessName()
                if (packageName != processName) {
                    WebView.setDataDirectorySuffix(processName)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Could not set WebView data directory suffix", e)
            }
        }
    }

    private fun ensureWebViewCacheDirectories() {
        try {
            val cacheRoot = cacheDir ?: return
            val webViewDir = File(cacheRoot, "WebView")
            val defaultDir = File(webViewDir, "Default")
            val httpCacheDir = File(defaultDir, "HTTP Cache")
            val codeCacheDir = File(httpCacheDir, "Code Cache")
            val jsDir = File(codeCacheDir, "js")
            val wasmDir = File(codeCacheDir, "wasm")

            val allDirs = listOf(webViewDir, defaultDir, httpCacheDir, codeCacheDir, jsDir, wasmDir)
            for (dir in allDirs) {
                if (!dir.exists()) {
                    dir.mkdirs()
                }
                dir.setReadable(true, false)
                dir.setWritable(true, false)
                dir.setExecutable(true, false)
            }
            Log.i(TAG, "Chromium WebView cache directory tree established successfully.")
        } catch (e: Exception) {
            Log.w(TAG, "Failed pre-creating WebView cache directories", e)
        }
    }

    companion object {
        private const val TAG = "AetherApplication"
    }
}

