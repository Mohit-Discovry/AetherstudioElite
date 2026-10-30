package com.example

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.example.publish.PublishingBridge
import com.example.ui.theme.MyApplicationTheme

/**
 * Main Activity hosting the Jetpack Compose surface and AetherStudio Elite WebView.
 * Integrates Java and Kotlin publishing engines with zero-latency hardware acceleration.
 */
class MainActivity : ComponentActivity() {

  private var webViewInstance: WebView? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    onBackPressedDispatcher.addCallback(
      this,
      object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
          val webView = webViewInstance
          if (webView != null) {
            webView.evaluateJavascript("window.handleAndroidHardwareBack && window.handleAndroidHardwareBack();") { result ->
              // If webview handled it, it returns true, otherwise check canGoBack
              if (result != "\"handled\"" && result != "true") {
                if (webView.canGoBack()) {
                  webView.goBack()
                } else {
                  isEnabled = false
                  onBackPressedDispatcher.onBackPressed()
                }
              }
            }
          } else {
            isEnabled = false
            onBackPressedDispatcher.onBackPressed()
          }
        }
      }
    )

    setContent {
      MyApplicationTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = Color(0xFF0A0B10)
        ) {
          StudioWebView()
        }
      }
    }
  }

  private fun ensureCacheDirectories(context: android.content.Context) {
    try {
      val cacheRoot = context.cacheDir ?: return
      val webViewDir = java.io.File(cacheRoot, "WebView")
      val defaultDir = java.io.File(webViewDir, "Default")
      val httpCacheDir = java.io.File(defaultDir, "HTTP Cache")
      val codeCacheDir = java.io.File(httpCacheDir, "Code Cache")
      val jsDir = java.io.File(codeCacheDir, "js")
      val wasmDir = java.io.File(codeCacheDir, "wasm")

      val allDirs = listOf(webViewDir, defaultDir, httpCacheDir, codeCacheDir, jsDir, wasmDir)
      for (dir in allDirs) {
        if (!dir.exists()) {
          dir.mkdirs()
        }
        dir.setReadable(true, false)
        dir.setWritable(true, false)
        dir.setExecutable(true, false)
      }
    } catch (e: Exception) {
      android.util.Log.w("MainActivity", "Failed pre-creating WebView cache directories", e)
    }
  }

  @SuppressLint("SetJavaScriptEnabled")
  @Composable
  fun StudioWebView() {
    AndroidView(
      modifier = Modifier.fillMaxSize(),
      factory = { ctx ->
        ensureCacheDirectories(ctx)
        WebView(ctx).apply {
          webViewInstance = this
          layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
          )
          setLayerType(View.LAYER_TYPE_HARDWARE, null)
          setBackgroundColor(android.graphics.Color.parseColor("#0A0B10"))
          settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            useWideViewPort = true
            loadWithOverviewMode = true
            displayZoomControls = false
            setSupportZoom(false)
            cacheMode = WebSettings.LOAD_DEFAULT
          }
          webViewClient = WebViewClient()
          webChromeClient = WebChromeClient()

          // Register PublishingBridge (Java + Kotlin hybrid publisher)
          val bridge = PublishingBridge(ctx) { webViewInstance }
          addJavascriptInterface(bridge, "AndroidBridge")
          addJavascriptInterface(bridge, "PublishingBridge")

          loadUrl("file:///android_asset/index.html")
        }
      }
    )
  }

  override fun onDestroy() {
    webViewInstance?.destroy()
    webViewInstance = null
    super.onDestroy()
  }
}
