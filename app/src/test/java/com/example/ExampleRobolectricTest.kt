package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.publish.CanvasPublishHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.nio.charset.StandardCharsets

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("AetherStudio Elite", appName)
  }

  @Test
  fun `verify publishing metadata contains package and targetSdk`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val metadataJson = CanvasPublishHelper.getPublishingMetadataJSON(context)
    assertTrue(metadataJson.contains("targetSdk"))
    assertTrue(metadataJson.contains("applicationId"))
  }

  @Test
  fun `decodeBase64ToBytes handles raw and data URL formats`() {
    val testString = "Hello AetherStudio SVG PSD TIFF"
    val rawBytes = testString.toByteArray(StandardCharsets.UTF_8)
    val base64 = android.util.Base64.encodeToString(rawBytes, android.util.Base64.NO_WRAP)
    
    // Plain base64
    val decoded1 = CanvasPublishHelper.decodeBase64ToBytes(base64)
    assertNotNull(decoded1)
    assertEquals(testString, String(decoded1!!, StandardCharsets.UTF_8))

    // Data URL format with mime type
    val dataUrl = "data:image/svg+xml;base64,$base64"
    val decoded2 = CanvasPublishHelper.decodeBase64ToBytes(dataUrl)
    assertNotNull(decoded2)
    assertEquals(testString, String(decoded2!!, StandardCharsets.UTF_8))
  }

  @Test
  fun `saveDocumentToStorage creates MediaStore URI for SVG PSD and TIFF`() {
    val context = ApplicationProvider.getApplicationContext<Context>()

    // SVG
    val svgData = "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"100\" height=\"100\"><circle cx=\"50\" cy=\"50\" r=\"40\"/></svg>".toByteArray(StandardCharsets.UTF_8)
    val svgUri = CanvasPublishHelper.saveDocumentToStorage(context, svgData, "TestVector", "image/svg+xml", "svg")
    assertNotNull(svgUri)

    // PSD
    val psdData = byteArrayOf(0x38, 0x42, 0x50, 0x53, 0x00, 0x01) // 8BPS header
    val psdUri = CanvasPublishHelper.saveDocumentToStorage(context, psdData, "TestLayers", "image/vnd.adobe.photoshop", "psd")
    assertNotNull(psdUri)

    // TIFF
    val tiffData = byteArrayOf(0x49, 0x49, 0x2A, 0x00) // II* Little-Endian header
    val tiffUri = CanvasPublishHelper.saveDocumentToStorage(context, tiffData, "TestPrint", "image/tiff", "tiff")
    assertNotNull(tiffUri)
  }

  @Test
  fun `shareDocumentIntent executes safely for document formats`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val sampleData = "AetherStudio Artwork".toByteArray(StandardCharsets.UTF_8)
    
    val result = CanvasPublishHelper.shareDocumentIntent(
      context,
      sampleData,
      "Artwork Share",
      "image/svg+xml",
      "svg"
    )
    assertTrue(result)
  }
}

