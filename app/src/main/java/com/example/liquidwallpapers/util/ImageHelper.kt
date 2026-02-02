package com.example.liquidwallpapers.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object ImageHelper {

    suspend fun saveImageToGallery(context: Context, bitmap: Bitmap, title: String) {
        withContext(Dispatchers.IO) {
            try {
                // Create a unique filename
                val filename = "Liquid_${title.replace(" ", "_").take(10)}_${System.currentTimeMillis()}.jpg"
                var fos: OutputStream? = null

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Android 10+ (Scoped Storage - No Permission needed)
                    val resolver = context.contentResolver
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/LiquidWallpapers")
                        put(MediaStore.MediaColumns.IS_PENDING, 1)
                    }
                    val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    fos = imageUri?.let { resolver.openOutputStream(it) }

                    // Write data
                    fos?.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }

                    // Mark as finished
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    imageUri?.let { resolver.update(it, contentValues, null, null) }
                } else {
                    // Android 9 and below (Legacy method)
                    val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    val appDir = File(imagesDir, "LiquidWallpapers")
                    if (!appDir.exists()) appDir.mkdirs()
                    val image = File(appDir, filename)
                    fos = FileOutputStream(image)
                    fos?.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Saved to Gallery!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}