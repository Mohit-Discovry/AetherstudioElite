package com.example.publish;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.example.BuildConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * High-performance Java utility for publishing and exporting digital artwork from AetherStudio.
 * Interoperates cleanly with Kotlin coroutines and Android WebView JavascriptInterface.
 */
public final class CanvasPublishHelper {

    private static final String TAG = "CanvasPublishHelper";
    private static final String ALBUM_DIR = "Pictures/AetherStudio";

    private CanvasPublishHelper() {
        // Prevent instantiation
    }

    /**
     * Decodes a base64 DataURL (e.g. data:image/png;base64,iVBORw0KGgo...) into a Bitmap.
     */
    @Nullable
    public static Bitmap decodeBase64ToBitmap(@NonNull String base64Data) {
        try {
            String cleanData = base64Data;
            int commaIndex = cleanData.indexOf(',');
            if (commaIndex != -1) {
                cleanData = cleanData.substring(commaIndex + 1);
            }
            byte[] decodedBytes = Base64.decode(cleanData, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            Log.e(TAG, "Failed to decode base64 bitmap data", e);
            return null;
        }
    }

    /**
     * Saves bitmap artwork directly to Android MediaStore scoped storage (Pictures/AetherStudio).
     * Works seamlessly without legacy storage permissions on Android 10+ (API 29+).
     */
    @Nullable
    public static Uri saveArtworkToGallery(@NonNull Context context, @NonNull Bitmap bitmap, @NonNull String title, @NonNull String format) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String cleanTitle = title.trim().replaceAll("[^a-zA-Z0-9_-]", "_");
        if (cleanTitle.isEmpty()) {
            cleanTitle = "AetherArtwork";
        }
        boolean isJpeg = "jpeg".equalsIgnoreCase(format) || "jpg".equalsIgnoreCase(format);
        String extension = isJpeg ? "jpg" : "png";
        String mimeType = isJpeg ? "image/jpeg" : "image/png";
        String fileName = cleanTitle + "_" + timestamp + "." + extension;

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType);
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, ALBUM_DIR);
            values.put(MediaStore.Images.Media.IS_PENDING, 1);
        }

        ContentResolver resolver = context.getContentResolver();
        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        if (imageUri == null) {
            Log.e(TAG, "Failed to create MediaStore entry for " + fileName);
            return null;
        }

        try (OutputStream out = resolver.openOutputStream(imageUri)) {
            if (out != null) {
                Bitmap.CompressFormat compressFormat = isJpeg ? Bitmap.CompressFormat.JPEG : Bitmap.CompressFormat.PNG;
                int quality = isJpeg ? 92 : 100;
                bitmap.compress(compressFormat, quality, out);
                out.flush();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.clear();
                    values.put(MediaStore.Images.Media.IS_PENDING, 0);
                    resolver.update(imageUri, values, null, null);
                }
                Log.i(TAG, "Successfully published artwork to gallery: " + imageUri);
                return imageUri;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error writing bitmap to MediaStore URI: " + imageUri, e);
            resolver.delete(imageUri, null, null);
        }
        return null;
    }

    /**
     * Creates an Android ACTION_SEND Share Intent with the exported artwork.
     */
    public static boolean shareArtworkIntent(@NonNull Context context, @NonNull Bitmap bitmap, @NonNull String title) {
        try {
            File cacheDir = new File(context.getCacheDir(), "shared_artwork");
            if (!cacheDir.exists() && !cacheDir.mkdirs()) {
                Log.w(TAG, "Could not create cache directory for shared artwork");
            }
            File shareFile = new File(cacheDir, "AetherStudio_" + System.currentTimeMillis() + ".png");
            try (FileOutputStream fos = new FileOutputStream(shareFile)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
            }

            Uri contentUri;
            try {
                contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", shareFile);
            } catch (IllegalArgumentException e) {
                // Fallback to direct file URI if provider not registered
                contentUri = Uri.fromFile(shareFile);
            }

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/png");
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Created and published with AetherStudio Elite ✨");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Intent chooser = Intent.createChooser(shareIntent, "Publish & Share Artwork");
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(chooser);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Failed to trigger share intent", e);
            return false;
        }
    }

    /**
     * Decodes base64 string (with or without data URL header) into raw byte array.
     */
    @Nullable
    public static byte[] decodeBase64ToBytes(@NonNull String base64Data) {
        try {
            String cleanData = base64Data;
            int commaIndex = cleanData.indexOf(',');
            if (commaIndex != -1) {
                cleanData = cleanData.substring(commaIndex + 1);
            }
            return Base64.decode(cleanData, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e(TAG, "Failed to decode base64 data", e);
            return null;
        }
    }

    /**
     * Saves generic document files (SVG, PSD, TIFF, etc.) to device storage.
     * Uses MediaStore.Downloads (or MediaStore.Images for TIFF/SVG) on Android Q+,
     * or standard App external files directory fallback.
     */
    @Nullable
    public static Uri saveDocumentToStorage(
            @NonNull Context context,
            @NonNull byte[] data,
            @NonNull String title,
            @NonNull String mimeType,
            @NonNull String extension) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String cleanTitle = title.trim().replaceAll("[^a-zA-Z0-9_-]", "_");
        if (cleanTitle.isEmpty()) {
            cleanTitle = "AetherProject";
        }
        String cleanExt = extension.startsWith(".") ? extension.substring(1) : extension;
        String fileName = cleanTitle + "_" + timestamp + "." + cleanExt;

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
        values.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000);

        ContentResolver resolver = context.getContentResolver();
        Uri targetCollection;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if ("image/tiff".equalsIgnoreCase(mimeType) || "image/svg+xml".equalsIgnoreCase(mimeType)) {
                targetCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/AetherStudio");
            } else {
                targetCollection = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
                values.put(MediaStore.Downloads.RELATIVE_PATH, "Download/AetherStudio");
            }
            values.put(MediaStore.MediaColumns.IS_PENDING, 1);
        } else {
            targetCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        Uri fileUri = null;
        try {
            fileUri = resolver.insert(targetCollection, values);
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "MediaStore rejected mimeType " + mimeType + ", trying application/octet-stream fallback", e);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "application/octet-stream");
            try {
                fileUri = resolver.insert(targetCollection, values);
            } catch (Exception ex) {
                Log.w(TAG, "Secondary MediaStore insert failed", ex);
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed inserting into MediaStore", e);
        }

        if (fileUri == null) {
            Log.w(TAG, "MediaStore insert unavailable, falling back to local documents directory for: " + fileName);
            try {
                File docsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                if (docsDir == null) {
                    docsDir = new File(context.getFilesDir(), "AetherStudio");
                }
                if (!docsDir.exists() && !docsDir.mkdirs()) {
                    Log.w(TAG, "Could not create target directory for document export");
                }
                File targetFile = new File(docsDir, fileName);
                try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                    fos.write(data);
                    fos.flush();
                }
                Log.i(TAG, "Successfully saved document to local file fallback: " + targetFile.getAbsolutePath());
                return Uri.fromFile(targetFile);
            } catch (Exception e) {
                Log.e(TAG, "Fallback document save failed", e);
                return null;
            }
        }

        try (OutputStream out = resolver.openOutputStream(fileUri)) {
            if (out != null) {
                out.write(data);
                out.flush();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.clear();
                    values.put(MediaStore.MediaColumns.IS_PENDING, 0);
                    resolver.update(fileUri, values, null, null);
                }
                Log.i(TAG, "Successfully published document: " + fileUri);
                return fileUri;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error writing document data to URI: " + fileUri, e);
            resolver.delete(fileUri, null, null);
        }
        return null;
    }

    /**
     * Shares arbitrary document files (SVG, PSD, TIFF) using system ACTION_SEND intent.
     */
    public static boolean shareDocumentIntent(
            @NonNull Context context,
            @NonNull byte[] data,
            @NonNull String title,
            @NonNull String mimeType,
            @NonNull String extension) {
        try {
            File cacheDir = new File(context.getCacheDir(), "shared_artwork");
            if (!cacheDir.exists() && !cacheDir.mkdirs()) {
                Log.w(TAG, "Could not create cache directory for shared document");
            }
            String cleanExt = extension.startsWith(".") ? extension.substring(1) : extension;
            File shareFile = new File(cacheDir, "AetherStudio_" + System.currentTimeMillis() + "." + cleanExt);
            try (FileOutputStream fos = new FileOutputStream(shareFile)) {
                fos.write(data);
                fos.flush();
            }

            Uri contentUri;
            try {
                contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", shareFile);
            } catch (IllegalArgumentException e) {
                contentUri = Uri.fromFile(shareFile);
            }

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType(mimeType);
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Created and published with AetherStudio Elite ✨ (" + cleanExt.toUpperCase() + ")");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Intent chooser = Intent.createChooser(shareIntent, "Share " + cleanExt.toUpperCase() + " Document");
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(chooser);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Failed to trigger document share intent", e);
            return false;
        }
    }

    /**
     * Executes tactile haptic feedback for user interactions and publishing events.
     */
    public static void performHapticFeedback(@NonNull Context context, int type) {
        try {
            Vibrator vibrator;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                VibratorManager manager = (VibratorManager) context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
                vibrator = manager != null ? manager.getDefaultVibrator() : null;
            } else {
                vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            }

            if (vibrator == null || !vibrator.hasVibrator()) {
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int effectId = VibrationEffect.EFFECT_CLICK;
                if (type == 1) { // heavy/success
                    effectId = VibrationEffect.EFFECT_HEAVY_CLICK;
                } else if (type == 2) { // tick
                    effectId = VibrationEffect.EFFECT_TICK;
                }
                vibrator.vibrate(VibrationEffect.createPredefined(effectId));
            } else {
                long duration = (type == 1) ? 35 : 15;
                vibrator.vibrate(duration);
            }
        } catch (Exception ignored) {
            // Haptics are best-effort
        }
    }

    /**
     * Returns JSON-formatted publication and build status for the app.
     */
    @NonNull
    public static String getPublishingMetadataJSON(@NonNull Context context) {
        return "{"
                + "\"applicationId\":\"" + BuildConfig.APPLICATION_ID + "\","
                + "\"versionName\":\"" + BuildConfig.VERSION_NAME + "\","
                + "\"versionCode\":" + BuildConfig.VERSION_CODE + ","
                + "\"buildType\":\"" + BuildConfig.BUILD_TYPE + "\","
                + "\"targetSdk\":36,"
                + "\"platform\":\"Android\","
                + "\"publishChannel\":\"Google Play / Android\","
                + "\"engine\":\"AetherStudio Unified Core (Java + Kotlin)\""
                + "}";
    }
}
