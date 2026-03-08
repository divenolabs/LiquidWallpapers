package com.example.liquidwallpapers.ui.screens.studio

import android.app.WallpaperManager
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Contrast
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import android.graphics.RenderEffect as AndroidRenderEffect
import android.os.Build as AndroidBuild
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.liquidwallpapers.ui.components.glassEffect
import com.example.liquidwallpapers.ui.theme.LiquidOrange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ─────────────────────────────────────
// MODELS
// ─────────────────────────────────────

enum class StudioTool(val label: String, val icon: ImageVector) {
    VIBES("Vibes", Icons.Rounded.Palette),
    EFFECTS("Effects", Icons.Rounded.Tune)
}

data class VibePreset(val name: String, val colors: List<Color>, val accent: Color)

val vibePresets = listOf(
    VibePreset("Midnight", listOf(Color(0xFF0D1B2A), Color(0xFF1B263B), Color(0xFF415A77), Color(0xFF778DA9)), Color(0xFF778DA9)),
    VibePreset("Cyberpunk", listOf(Color(0xFFFF006E), Color(0xFF8338EC), Color(0xFF3A86FF), Color(0xFFFFBE0B)), Color(0xFFFF006E)),
    VibePreset("Latte", listOf(Color(0xFFC9A87C), Color(0xFF8B6F47), Color(0xFFD4B896), Color(0xFF6B4226)), Color(0xFFC9A87C)),
    VibePreset("Aurora", listOf(Color(0xFF00F5D4), Color(0xFF00BBF9), Color(0xFF9B5DE5), Color(0xFFFEE440)), Color(0xFF00F5D4)),
    VibePreset("Ember", listOf(Color(0xFFFA3B05), Color(0xFFFF6B35), Color(0xFFF7C59F), Color(0xFF2E0E02)), Color(0xFFFA3B05)),
    VibePreset("Forest", listOf(Color(0xFF1B4332), Color(0xFF2D6A4F), Color(0xFF40916C), Color(0xFF95D5B2)), Color(0xFF40916C)),
    VibePreset("Ocean", listOf(Color(0xFF023E8A), Color(0xFF0077B6), Color(0xFF00B4D8), Color(0xFF90E0EF)), Color(0xFF00B4D8)),
    VibePreset("Sunset", listOf(Color(0xFFFF4500), Color(0xFFFF7F50), Color(0xFFFFA07A), Color(0xFFFFDAB9)), Color(0xFFFF7F50)),
    VibePreset("Berry", listOf(Color(0xFF5B0060), Color(0xFF870058), Color(0xFFB30051), Color(0xFFCA485C)), Color(0xFFB30051)),
    VibePreset("Arctic", listOf(Color(0xFFCAF0F8), Color(0xFFADE8F4), Color(0xFF48CAE4), Color(0xFF023E8A)), Color(0xFF48CAE4)),
    VibePreset("Lavender", listOf(Color(0xFFE6E6FA), Color(0xFFD8BFD8), Color(0xFF9370DB), Color(0xFF4B0082)), Color(0xFF9370DB)),
    VibePreset("Volcano", listOf(Color(0xFF1A0000), Color(0xFF660000), Color(0xFFCC3300), Color(0xFFFF9900)), Color(0xFFCC3300)),
    VibePreset("Mint", listOf(Color(0xFF004B23), Color(0xFF006400), Color(0xFF38B000), Color(0xFFCCFF33)), Color(0xFF38B000)),
    VibePreset("Sakura", listOf(Color(0xFFFFB6C1), Color(0xFFFFC0CB), Color(0xFFFF69B4), Color(0xFF8B0052)), Color(0xFFFF69B4)),
    VibePreset("Storm", listOf(Color(0xFF1B1B2F), Color(0xFF162447), Color(0xFF1F4068), Color(0xFFE43F5A)), Color(0xFFE43F5A)),
    VibePreset("Gold", listOf(Color(0xFF3C1518), Color(0xFF69140E), Color(0xFFA44200), Color(0xFFD4A373)), Color(0xFFD4A373)),
    VibePreset("Neon", listOf(Color(0xFF0D0D0D), Color(0xFF39FF14), Color(0xFF00FFFF), Color(0xFFFF00FF)), Color(0xFF39FF14)),
    VibePreset("Coral", listOf(Color(0xFFFF6F61), Color(0xFFFE938C), Color(0xFFEAD2AC), Color(0xFF6B705C)), Color(0xFFFF6F61)),
    VibePreset("Mocha", listOf(Color(0xFF2C1A0E), Color(0xFF5C3D2E), Color(0xFF8B5E3C), Color(0xFFBFA58E)), Color(0xFF8B5E3C)),
    VibePreset("Galaxy", listOf(Color(0xFF0C0032), Color(0xFF190061), Color(0xFF240090), Color(0xFF3500D3)), Color(0xFF3500D3))
)

// ─────────────────────────────────────
// BITMAP GENERATOR
// ─────────────────────────────────────

object WallpaperGenerator {
    fun generate(
        preset: VibePreset, blurIntensity: Float, dimIntensity: Float,
        brightness: Float, contrast: Float,
        width: Int = 1080, height: Int = 2400
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val brightnessMultiplier = 0.7f + brightness * 0.6f
        val ct = 0.5f + contrast * 1.5f
        val br = (brightness - 0.5f) * 100f
        val shift = (128f * (1f - ct) + br) / 255f

        fun adjustColor(c: Color, baseA: Float): Int {
            val r = (c.red * ct + shift).coerceIn(0f, 1f)
            val g = (c.green * ct + shift).coerceIn(0f, 1f)
            val b = (c.blue * ct + shift).coerceIn(0f, 1f)
            return android.graphics.Color.argb((baseA * 255).toInt().coerceIn(0, 255), (r * 255).toInt(), (g * 255).toInt(), (b * 255).toInt())
        }

        canvas.drawColor(adjustColor(Color(0xFF050510), 1f))

        val blobs = listOf(
            Triple(preset.colors.getOrElse(0) { Color.Black }, 0.65f, Pair(0.2f, 0.18f)),
            Triple(preset.colors.getOrElse(1) { Color.Black }, 0.6f,  Pair(0.8f, 0.35f)),
            Triple(preset.colors.getOrElse(2) { Color.Black }, 0.55f, Pair(0.45f, 0.6f)),
            Triple(preset.colors.getOrElse(3) { Color.Black }, 0.5f,  Pair(0.6f, 0.8f))
        )
        val alphas = listOf(0.85f, 0.85f, 0.75f, 0.8f)

        blobs.forEachIndexed { i, (color, rMulti, pos) ->
            val cx = width * pos.first
            val cy = height * pos.second
            val r = (width * rMulti) + (blurIntensity * 4.5f)

            val paint = Paint().apply {
                isAntiAlias = true
                shader = RadialGradient(
                    cx, cy, r.coerceAtLeast(1f),
                    intArrayOf(adjustColor(color, alphas[i] * brightnessMultiplier), android.graphics.Color.TRANSPARENT),
                    floatArrayOf(0f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawCircle(cx, cy, r, paint)
        }

        if (dimIntensity > 0.01f) {
            val dp = Paint().apply { color = android.graphics.Color.argb((dimIntensity * 255).toInt(), 0, 0, 0) }
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), dp)
        }

        return bitmap
    }
}

// ─────────────────────────────────────
// MAIN SCREEN
// ─────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var activeTool by remember { mutableStateOf(StudioTool.VIBES) }
    var activePreset by remember { mutableStateOf(vibePresets[0]) }
    var blurAmount by remember { mutableFloatStateOf(120f) }
    var dimAmount by remember { mutableFloatStateOf(0.15f) }
    var brightness by remember { mutableFloatStateOf(0.5f) }
    var contrast by remember { mutableFloatStateOf(0.5f) }
    var showExportSheet by remember { mutableStateOf(false) }
    var isExporting by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Static liquid canvas — no moving blobs, WYSIWYG preview
        StaticLiquidCanvas(preset = activePreset, blurAmount = blurAmount, dimIntensity = dimAmount, brightness = brightness, contrast = contrast)

        StudioTopBar(onExportClick = { showExportSheet = true })

        GlassControlHub(
            activeTool = activeTool, onToolSelected = { activeTool = it },
            activePreset = activePreset, onPresetSelected = { activePreset = it },
            blurAmount = blurAmount, onBlurChanged = { blurAmount = it },
            dimAmount = dimAmount, onDimChanged = { dimAmount = it },
            brightness = brightness, onBrightnessChanged = { brightness = it },
            contrast = contrast, onContrastChanged = { contrast = it }
        )
    }

    if (showExportSheet) {
        ModalBottomSheet(
            onDismissRequest = { showExportSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color(0xFF1A1A2E),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            ExportSheetContent(
                presetName = activePreset.name, isExporting = isExporting,
                onSetHomeScreen = {
                    isExporting = true
                    scope.launch {
                        try {
                            val bmp = withContext(Dispatchers.Default) { WallpaperGenerator.generate(activePreset, blurAmount, dimAmount, brightness, contrast) }
                            WallpaperManager.getInstance(context).setBitmap(bmp, null, true, WallpaperManager.FLAG_SYSTEM)
                            Toast.makeText(context, "Home screen wallpaper set! ✓", Toast.LENGTH_SHORT).show()
                            bmp.recycle()
                        } catch (e: Exception) { Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_SHORT).show() }
                        isExporting = false; showExportSheet = false
                    }
                },
                onSetLockScreen = {
                    isExporting = true
                    scope.launch {
                        try {
                            val bmp = withContext(Dispatchers.Default) { WallpaperGenerator.generate(activePreset, blurAmount, dimAmount, brightness, contrast) }
                            WallpaperManager.getInstance(context).setBitmap(bmp, null, true, WallpaperManager.FLAG_LOCK)
                            Toast.makeText(context, "Lock screen wallpaper set! ✓", Toast.LENGTH_SHORT).show()
                            bmp.recycle()
                        } catch (e: Exception) { Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_SHORT).show() }
                        isExporting = false; showExportSheet = false
                    }
                },
                onSaveToGallery = {
                    isExporting = true
                    scope.launch {
                        try {
                            val bmp = withContext(Dispatchers.Default) { WallpaperGenerator.generate(activePreset, blurAmount, dimAmount, brightness, contrast) }
                            val fn = "LiquidStudio_${activePreset.name}_${System.currentTimeMillis()}.png"
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                val v = ContentValues().apply {
                                    put(MediaStore.Images.Media.DISPLAY_NAME, fn)
                                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                                    put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/LiquidWallpapers")
                                }
                                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, v)?.let { uri ->
                                    context.contentResolver.openOutputStream(uri)?.use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
                                }
                            } else {
                                @Suppress("DEPRECATION")
                                val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                val f = java.io.File(dir, "LiquidWallpapers/$fn"); f.parentFile?.mkdirs()
                                java.io.FileOutputStream(f).use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
                            }
                            Toast.makeText(context, "Saved to Gallery ✓", Toast.LENGTH_SHORT).show()
                            bmp.recycle()
                        } catch (e: Exception) { Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_SHORT).show() }
                        isExporting = false; showExportSheet = false
                    }
                }
            )
        }
    }
}

// ─────────────────────────────────────
// STATIC CANVAS — No animation, true WYSIWYG
// ─────────────────────────────────────

@Composable
fun StaticLiquidCanvas(preset: VibePreset, blurAmount: Float, dimIntensity: Float, brightness: Float, contrast: Float) {
    val c0 by animateColorAsState(preset.colors.getOrElse(0) { Color.Black }, tween(600), label = "c0")
    val c1 by animateColorAsState(preset.colors.getOrElse(1) { Color.Black }, tween(600), label = "c1")
    val c2 by animateColorAsState(preset.colors.getOrElse(2) { Color.Black }, tween(600), label = "c2")
    val c3 by animateColorAsState(preset.colors.getOrElse(3) { Color.Black }, tween(600), label = "c3")

    val brightnessMultiplier = 0.7f + brightness * 0.6f
    val ct = 0.5f + contrast * 1.5f
    val br = (brightness - 0.5f) * 100f
    val shift = (128f * (1f - ct) + br) / 255f

    fun legacyAdjust(c: Color, baseAmount: Float): Color {
        // Compose colors are 0f to 1f bounds. We convert to 0-255 bounds for accurate Matrix comparison
        var r = c.red * 255f
        var g = c.green * 255f
        var b = c.blue * 255f

        // Apply identical ColorMatrix translation logic, then reconvert to Float bounds for Compose
        r = (r * ct + shift * 255f).coerceIn(0f, 255f) / 255f
        g = (g * ct + shift * 255f).coerceIn(0f, 255f) / 255f
        b = (b * ct + shift * 255f).coerceIn(0f, 255f) / 255f

        return Color(r, g, b, baseAmount.coerceIn(0f, 1f))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(legacyAdjust(Color(0xFF050510), 1f))
    ) {
        // Adjust compose DP multiplier vs actual resolution pixels
        // Compose uses logical pixels, Wallpaper uses actual pixels. We map blurIntensity roughly 1:1 visually
        val expandB = blurAmount * 1.5f 

        Box(modifier = Modifier.fillMaxSize()) {
            // Blob 1
            Box(modifier = Modifier.fillMaxSize().drawBehind {
                val r = size.width * 0.65f + expandB
                val baseA = 0.85f * brightnessMultiplier
                val center = Offset(size.width * 0.2f, size.height * 0.18f)
                drawCircle(Brush.radialGradient(listOf(legacyAdjust(c0, baseA), Color.Transparent), center, r), r, center)
            })
            // Blob 2
            Box(modifier = Modifier.fillMaxSize().drawBehind {
                val r = size.width * 0.6f + expandB
                val baseA = 0.85f * brightnessMultiplier
                val center = Offset(size.width * 0.8f, size.height * 0.35f)
                drawCircle(Brush.radialGradient(listOf(legacyAdjust(c1, baseA), Color.Transparent), center, r), r, center)
            })
            // Blob 3
            Box(modifier = Modifier.fillMaxSize().drawBehind {
                val r = size.width * 0.55f + expandB
                val baseA = 0.75f * brightnessMultiplier
                val center = Offset(size.width * 0.45f, size.height * 0.6f)
                drawCircle(Brush.radialGradient(listOf(legacyAdjust(c2, baseA), Color.Transparent), center, r), r, center)
            })
            // Blob 4
            Box(modifier = Modifier.fillMaxSize().drawBehind {
                val r = size.width * 0.5f + expandB
                val baseA = 0.8f * brightnessMultiplier
                val center = Offset(size.width * 0.6f, size.height * 0.8f)
                drawCircle(Brush.radialGradient(listOf(legacyAdjust(c3, baseA), Color.Transparent), center, r), r, center)
            })
        }

        // Dim overlay
        Box(modifier = Modifier.fillMaxSize().graphicsLayer { alpha = dimIntensity }.background(Color.Black))
    }
}

// ─────────────────────────────────────
// TOP BAR — Clean export, no glow circle
// ─────────────────────────────────────

@Composable
fun StudioTopBar(onExportClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Studio", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text("Create your wallpaper", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
        }
        Box(
            modifier = Modifier
                .glassEffect(RoundedCornerShape(16.dp))
                .clickable(remember { MutableInteractionSource() }, null) { onExportClick() }
                .padding(horizontal = 16.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Save, "Export", tint = LiquidOrange, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Export", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

// ─────────────────────────────────────
// EXPORT SHEET
// ─────────────────────────────────────

@Composable
fun ExportSheetContent(
    presetName: String, isExporting: Boolean,
    onSetHomeScreen: () -> Unit, onSetLockScreen: () -> Unit, onSaveToGallery: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 40.dp)) {
        Text("Export \"$presetName\"", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Rendered at 1080×2400 resolution", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
        Spacer(modifier = Modifier.height(24.dp))
        if (isExporting) {
            Box(Modifier.fillMaxWidth().height(56.dp), contentAlignment = Alignment.Center) {
                Text("Rendering...", color = LiquidOrange, fontWeight = FontWeight.Bold)
            }
        } else {
            ExportOption(Icons.Rounded.Home, "Set as Home Screen", "Apply as your home wallpaper", LiquidOrange, onSetHomeScreen)
            Spacer(modifier = Modifier.height(12.dp))
            ExportOption(Icons.Rounded.Lock, "Set as Lock Screen", "Apply as your lock screen", Color(0xFF00BBF9), onSetLockScreen)
            Spacer(modifier = Modifier.height(12.dp))
            ExportOption(Icons.Rounded.PhotoLibrary, "Save to Gallery", "Save PNG to Pictures/LiquidWallpapers", Color(0xFF00F5D4), onSaveToGallery)
        }
    }
}

@Composable
fun ExportOption(icon: ImageVector, title: String, subtitle: String, accentColor: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Color.White.copy(alpha = 0.06f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
            .clickable(remember { MutableInteractionSource() }, null) { onClick() }.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(accentColor.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = accentColor, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
        }
    }
}

// ─────────────────────────────────────
// CONTROL HUB
// ─────────────────────────────────────

@Composable
fun GlassControlHub(
    activeTool: StudioTool, onToolSelected: (StudioTool) -> Unit,
    activePreset: VibePreset, onPresetSelected: (VibePreset) -> Unit,
    blurAmount: Float, onBlurChanged: (Float) -> Unit,
    dimAmount: Float, onDimChanged: (Float) -> Unit,
    brightness: Float, onBrightnessChanged: (Float) -> Unit,
    contrast: Float, onContrastChanged: (Float) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Column(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding()
                .padding(horizontal = 16.dp).padding(bottom = 120.dp)
                .clip(RoundedCornerShape(32.dp)).background(Color(0x66000000))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(32.dp)).padding(16.dp)
        ) {
            ToolSelector(activeTool, onToolSelected)
            Spacer(modifier = Modifier.height(16.dp))
            when (activeTool) {
                StudioTool.VIBES -> VibesPanel(activePreset, onPresetSelected)
                StudioTool.EFFECTS -> EffectsPanel(blurAmount, onBlurChanged, dimAmount, onDimChanged, brightness, onBrightnessChanged, contrast, onContrastChanged)
            }
        }
    }
}

@Composable
fun ToolSelector(activeTool: StudioTool, onToolSelected: (StudioTool) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(24.dp)).background(Color.White.copy(alpha = 0.06f)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StudioTool.entries.forEach { tool ->
            val isSelected = tool == activeTool
            val bgAlpha by animateFloatAsState(if (isSelected) 1f else 0f, tween(250, easing = FastOutSlowInEasing), label = "tb")
            val textColor by animateColorAsState(if (isSelected) Color.Black else Color.White.copy(alpha = 0.5f), tween(250), label = "tc")
            Box(
                modifier = Modifier.weight(1f).fillMaxHeight().padding(4.dp).clip(RoundedCornerShape(20.dp))
                    .then(if (bgAlpha > 0.01f) Modifier.background(LiquidOrange.copy(alpha = bgAlpha), RoundedCornerShape(20.dp)) else Modifier)
                    .clickable(remember { MutableInteractionSource() }, null) { onToolSelected(tool) },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(tool.icon, null, tint = textColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(tool.label, color = textColor, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun VibesPanel(activePreset: VibePreset, onPresetSelected: (VibePreset) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(horizontal = 4.dp)) {
        items(vibePresets) { preset ->
            val isSelected = preset.name == activePreset.name
            val borderColor by animateColorAsState(if (isSelected) preset.accent else Color.White.copy(alpha = 0.1f), tween(300), label = "vb")
            val scale by animateFloatAsState(if (isSelected) 1.05f else 1f, tween(200), label = "vs")
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer { scaleX = scale; scaleY = scale }
                    .clickable(remember { MutableInteractionSource() }, null) { onPresetSelected(preset) }
            ) {
                Box(Modifier.size(52.dp).clip(CircleShape)
                    .background(Brush.sweepGradient(preset.colors + preset.colors.first()))
                    .border(2.dp, borderColor, CircleShape), contentAlignment = Alignment.Center) {
                    if (isSelected) Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(preset.name, color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
                    fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
            }
        }
    }
}

@Composable
fun EffectsPanel(
    blurAmount: Float, onBlurChanged: (Float) -> Unit,
    dimAmount: Float, onDimChanged: (Float) -> Unit,
    brightness: Float, onBrightnessChanged: (Float) -> Unit,
    contrast: Float, onContrastChanged: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        StudioSlider(
            label = "Blur",
            value = blurAmount,
            onValueChange = onBlurChanged,
            icon = Icons.Rounded.AutoAwesome,
            valueRange = 50f..250f,
            displayMapper = { v -> "${(((v - 50f) / 200f) * 100f).toInt()}%" }
        )
        Spacer(modifier = Modifier.height(6.dp))
        StudioSlider("Dim", dimAmount, onDimChanged, Icons.Rounded.Tune)
        Spacer(modifier = Modifier.height(6.dp))
        StudioSlider("Bright", brightness, onBrightnessChanged, Icons.Rounded.LightMode)
        Spacer(modifier = Modifier.height(6.dp))
        StudioSlider("Contrast", contrast, onContrastChanged, Icons.Rounded.Contrast)
    }
}

@Composable
fun StudioSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    icon: ImageVector,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    displayMapper: ((Float) -> String)? = null
) {
    val displayText = displayMapper?.invoke(value) ?: "${(value * 100).toInt()}%"
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.width(60.dp))
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.weight(1f).height(28.dp),
            colors = SliderDefaults.colors(
                thumbColor = LiquidOrange,
                activeTrackColor = LiquidOrange.copy(alpha = 0.6f),
                inactiveTrackColor = Color.White.copy(alpha = 0.1f)
            )
        )
        Text(displayText, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, modifier = Modifier.width(36.dp))
    }
}
