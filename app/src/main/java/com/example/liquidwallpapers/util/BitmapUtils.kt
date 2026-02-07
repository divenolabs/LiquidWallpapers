package com.example.liquidwallpapers.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.core.graphics.createBitmap

object BitmapUtils {

    /**
     * Master function to apply both Blur and Brightness (Dimming)
     * @param brightness: 0f is normal. -0.5f is darker. +0.5f is brighter.
     */
    fun applyEffects(context: Context, source: Bitmap, blurRadius: Float, brightness: Float): Bitmap {
        var output = source

        // 1. Apply Blur if needed
        if (blurRadius > 0f) {
            output = blurBitmap(context, output, blurRadius)
        }

        // 2. Apply Brightness/Dim if needed
        if (brightness != 0f) {
            output = adjustBrightness(output, brightness)
        }

        return output
    }

    private fun adjustBrightness(source: Bitmap, brightness: Float): Bitmap {
        // FIX: Added '?: Bitmap.Config.ARGB_8888' to handle null configs safely
        val safeConfig = source.config ?: Bitmap.Config.ARGB_8888

        // Using KTX createBitmap or standard Bitmap.createBitmap with the safe config
        val output = Bitmap.createBitmap(source.width, source.height, safeConfig)

        val canvas = Canvas(output)
        val paint = Paint()
        val matrix = ColorMatrix()

        // Brightness logic: scales RGB channels.
        // 1f = normal, 0.5f = 50% brightness (dimmed), etc.
        // We convert the offset (-0.8f to 0f) into a scale (0.2f to 1f)
        val scale = 1f + brightness

        matrix.set(floatArrayOf(
            scale, 0f, 0f, 0f, 0f,
            0f, scale, 0f, 0f, 0f,
            0f, 0f, scale, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        ))

        paint.colorFilter = ColorMatrixColorFilter(matrix)
        canvas.drawBitmap(source, 0f, 0f, paint)
        return output
    }

    @Suppress("DEPRECATION")
    fun blurBitmap(context: Context, bitmap: Bitmap, radius: Float): Bitmap {
        if (radius <= 0f) return bitmap

        // RenderScript limit is 25f.
        val safeRadius = radius.coerceIn(0f, 25f)

        try {
            // FIX: Ensure we create a MUTABLE copy with a valid config
            val safeConfig = bitmap.config ?: Bitmap.Config.ARGB_8888
            val outputBitmap = bitmap.copy(safeConfig, true)

            val rs = RenderScript.create(context)
            val input = Allocation.createFromBitmap(rs, bitmap)
            val output = Allocation.createFromBitmap(rs, outputBitmap)
            val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))

            script.setRadius(safeRadius)
            script.setInput(input)
            script.forEach(output)

            output.copyTo(outputBitmap)
            rs.destroy()
            return outputBitmap
        } catch (e: Exception) {
            e.printStackTrace()
            return bitmap // Return original if blur fails
        }
    }
}