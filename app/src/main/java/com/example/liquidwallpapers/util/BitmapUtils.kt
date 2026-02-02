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

object BitmapUtils {

    fun applyEffects(context: Context, source: Bitmap, blurRadius: Float, brightness: Float): Bitmap {
        // 1. Create a mutable copy to edit
        var finalBitmap = source.copy(Bitmap.Config.ARGB_8888, true)

        // 2. Apply Blur (Only if radius > 0)
        if (blurRadius > 0f) {
            finalBitmap = blurBitmap(context, finalBitmap, blurRadius)
        }

        // 3. Apply Brightness/Dimming
        // Brightness range: -1.0f (Dark) to 0.0f (Normal)
        if (brightness != 0f) {
            val canvas = Canvas(finalBitmap)
            val paint = Paint()
            val matrix = ColorMatrix()

            // Adjust brightness (Scale RGB, keep Alpha)
            // A brightness of -0.5f means we multiply colors by 0.5 (50% darker)
            val scale = 1f + brightness
            matrix.setScale(scale, scale, scale, 1f)

            paint.colorFilter = ColorMatrixColorFilter(matrix)
            canvas.drawBitmap(finalBitmap, 0f, 0f, paint)
        }

        return finalBitmap
    }

    private fun blurBitmap(context: Context, bitmap: Bitmap, radius: Float): Bitmap {
        return try {
            // Cap radius at 25f (RenderScript limit)
            val safeRadius = radius.coerceIn(0f, 25f)
            if (safeRadius <= 0f) return bitmap

            val rs = RenderScript.create(context)
            val input = Allocation.createFromBitmap(rs, bitmap)
            val output = Allocation.createTyped(rs, input.type)
            val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))

            script.setRadius(safeRadius)
            script.setInput(input)
            script.forEach(output)
            output.copyTo(bitmap)

            rs.destroy()
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            bitmap
        }
    }
}