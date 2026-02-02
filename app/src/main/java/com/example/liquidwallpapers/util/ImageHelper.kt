package com.example.liquidwallpapers.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object ImageHelper {

    // FIX: Renamed to 'saveImageToGallery' to match your DetailScreen
    // FIX: Added 'title' parameter so we can name the file nicely
    fun saveImageToGallery(context: Context, bitmap: Bitmap, title: String) {

        // Clean up the title (remove spaces or weird symbols for the filename)
        val safeName = title.replace("[^a-zA-Z0-9]".toRegex(), "_")
        val filename = "Liquid_${safeName}_${System.currentTimeMillis()}.jpg"

        var fos: OutputStream? = null
        var imageUri: Uri? = null

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ (Scoped Storage)
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/LiquidWallpapers")
                }

                imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                if (imageUri != null) {
                    fos = resolver.openOutputStream(imageUri)
                }
            } else {
                // Older Android Versions
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val appDir = File(imagesDir, "LiquidWallpapers")
                if (!appDir.exists()) appDir.mkdirs()

                val image = File(appDir, filename)
                fos = FileOutputStream(image)
            }

            // Write the image
            fos?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                Toast.makeText(context, "Saved to Gallery!", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error saving image", Toast.LENGTH_SHORT).show()
        }
    }
}