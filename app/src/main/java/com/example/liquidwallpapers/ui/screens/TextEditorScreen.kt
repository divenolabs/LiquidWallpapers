package com.example.liquidwallpapers.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.FormatAlignLeft
import androidx.compose.material.icons.automirrored.rounded.FormatAlignRight
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.example.liquidwallpapers.R
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.liquidwallpapers.ui.theme.LiquidOrange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max
import kotlin.math.roundToInt

// --- DATA CLASSES ---
enum class EditorTab { STYLE, FONT, TINT }

val DEFAULT_COLORS = listOf(
    Color.White, Color.Black,
    Color(0xFFFF5722), Color(0xFFFFC107), Color(0xFF8BC34A),
    Color(0xFF03A9F4), Color(0xFF3F51B5), Color(0xFF9C27B0),
    Color(0xFFE91E63), Color.Gray
)

@Composable
fun TextEditorScreen(
    navController: NavController,
    imageUrl: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    // -- IMAGE STATE --
    var originalBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var containerWidth by remember { mutableIntStateOf(0) }
    var containerHeight by remember { mutableIntStateOf(0) }

    // -- CONTRAST STATE --
    var isLightImage by remember { mutableStateOf(false) }

    // -- TEXT STATE --
    var text by remember { mutableStateOf("Tap to type") }
    var textColor by remember { mutableStateOf(Color.White) }
    var isBold by remember { mutableStateOf(true) }
    var isItalic by remember { mutableStateOf(false) }
    var hasShadow by remember { mutableStateOf(false) }
    var textSize by remember { mutableFloatStateOf(40f) }

    // --- FONT STATE ---
    var selectedFontName by remember { mutableStateOf("Modern") }

    // Fix: Clean Typeface creation for Preview
    val currentTypeface = remember(selectedFontName, isBold, isItalic) {
        val base = getRealTypeface(context, selectedFontName)
        val style = when {
            isBold && isItalic -> Typeface.BOLD_ITALIC
            isBold -> Typeface.BOLD
            isItalic -> Typeface.ITALIC
            else -> Typeface.NORMAL
        }
        Typeface.create(base, style)
    }
    val currentFontFamily = FontFamily(currentTypeface)

    var textAlign by remember { mutableStateOf(TextAlign.Center) }

    // -- DYNAMIC COLORS --
    var wallpaperColors by remember { mutableStateOf<List<Color>>(emptyList()) }
    var tintColor by remember { mutableStateOf(Color.Transparent) }

    // -- TRANSFORM --
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var scale by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }

    // -- UI FLAGS --
    var isEditingText by remember { mutableStateOf(false) }
    var activeTab by remember { mutableStateOf(EditorTab.STYLE) }
    var showControls by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }

    // -- LOAD IMAGE --
    LaunchedEffect(imageUrl) {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context).data(imageUrl).allowHardware(false).build()
        val result = loader.execute(request)
        if (result is SuccessResult) {
            val bmp = result.drawable.toBitmap()
            originalBitmap = bmp
            withContext(Dispatchers.Default) {
                val p = Palette.from(bmp).generate()
                val extracted = listOfNotNull(
                    p.vibrantSwatch?.rgb?.let { Color(it) }, p.lightVibrantSwatch?.rgb?.let { Color(it) },
                    p.darkVibrantSwatch?.rgb?.let { Color(it) }, p.mutedSwatch?.rgb?.let { Color(it) },
                    p.lightMutedSwatch?.rgb?.let { Color(it) }, p.darkMutedSwatch?.rgb?.let { Color(it) }
                )
                val isLight = ColorUtils.calculateLuminance(p.dominantSwatch?.rgb ?: 0) > 0.5
                withContext(Dispatchers.Main) {
                    wallpaperColors = extracted
                    isLightImage = isLight
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .onGloballyPositioned { coordinates ->
                containerWidth = coordinates.size.width
                containerHeight = coordinates.size.height
            }
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    isEditingText = false
                    showControls = !showControls
                })
            }
    ) {
        // 1. WALLPAPER
        if (originalBitmap != null) {
            Image(bitmap = originalBitmap!!.asImageBitmap(), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        }

        // 2. TINT
        Box(modifier = Modifier.fillMaxSize().background(tintColor))

        // 3. TEXT LAYER
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .graphicsLayer(scaleX = scale, scaleY = scale, rotationZ = rotation),
            contentAlignment = Alignment.Center
        ) {
            val commonStyle = TextStyle(
                color = textColor,
                fontSize = textSize.sp,
                fontFamily = currentFontFamily,
                // We let the Typeface handle the style, so we set these to Normal here
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Normal,
                textAlign = textAlign,
                shadow = if (hasShadow) androidx.compose.ui.graphics.Shadow(Color.Black.copy(alpha = 0.6f), blurRadius = 12f) else null
            )

            if (isEditingText) {
                BasicTextField(
                    value = text,
                    onValueChange = { text = it },
                    textStyle = commonStyle,
                    cursorBrush = SolidColor(LiquidOrange),
                    modifier = Modifier.width(300.dp).background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(16.dp)).padding(20.dp)
                )
            } else {
                Text(
                    text = text,
                    style = commonStyle,
                    modifier = Modifier
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, rotate ->
                                scale *= zoom
                                rotation += rotate
                                offsetX += pan.x
                                offsetY += pan.y
                            }
                        }
                        .pointerInput(Unit) { detectTapGestures(onDoubleTap = { isEditingText = true }) }
                        .padding(20.dp)
                )
            }
        }

        // 4. CONTROLS
        if (showControls && !isEditingText) {
            Row(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                ControlCircle(icon = Icons.Rounded.Close, isLightImage = isLightImage) { navController.popBackStack() }
                ControlCircle(icon = Icons.Rounded.Check, tint = LiquidOrange, isLightImage = isLightImage) {
                    if (originalBitmap != null && !isSaving) {
                        isSaving = true
                        Toast.makeText(context, "Saving...", Toast.LENGTH_SHORT).show()
                        scope.launch {
                            val resultUri = saveEditedImage(
                                context, originalBitmap!!, containerWidth, containerHeight,
                                text, textColor, textSize, isBold, isItalic, hasShadow,
                                selectedFontName, textAlign,
                                tintColor, offsetX, offsetY, scale, rotation, density.density
                            )
                            navController.previousBackStackEntry?.savedStateHandle?.set("edited_image_uri", resultUri.toString())
                            navController.popBackStack()
                        }
                    }
                }
            }

            Column(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().navigationBarsPadding().padding(16.dp)) {
                AdaptiveGlassPanel(isLightImage = isLightImage) {
                    when (activeTab) {
                        EditorTab.STYLE -> StylePanel(
                            textColor, wallpaperColors, { textColor = it },
                            isBold, { isBold = !isBold },
                            isItalic, { isItalic = !isItalic },
                            hasShadow, { hasShadow = !hasShadow },
                            textSize, { textSize = it }, textAlign, { textAlign = it }
                        )
                        EditorTab.FONT -> FontPanel(selectedFontName) { selectedFontName = it }
                        EditorTab.TINT -> TintPanel { tintColor = it }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    TabButton("Style", Icons.Rounded.Palette, activeTab == EditorTab.STYLE, isLightImage) { activeTab = EditorTab.STYLE }
                    TabButton("Fonts", Icons.Rounded.TextFields, activeTab == EditorTab.FONT, isLightImage) { activeTab = EditorTab.FONT }
                    TabButton("Tint", Icons.Rounded.BrightnessMedium, activeTab == EditorTab.TINT, isLightImage) { activeTab = EditorTab.TINT }
                }
            }
        }

        if (isSaving) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LiquidOrange)
            }
        }
    }
}

// --- REAL FONT MAPPING ---
// FIX: Use bundled fonts so OnePlus/ColorOS doesn't override them with system fonts
fun getRealTypeface(context: Context, name: String): Typeface {
    return try {
        when (name) {
            "Classic" -> ResourcesCompat.getFont(context, R.font.classic) ?: Typeface.SERIF
            "Code" -> ResourcesCompat.getFont(context, R.font.code) ?: Typeface.MONOSPACE
            "Handwritten" -> ResourcesCompat.getFont(context, R.font.handwritten) ?: Typeface.create("cursive", Typeface.NORMAL)
            "Marker" -> ResourcesCompat.getFont(context, R.font.marker) ?: Typeface.create("casual", Typeface.NORMAL)
            "Tech" -> ResourcesCompat.getFont(context, R.font.tech) ?: Typeface.create("sans-serif-condensed", Typeface.NORMAL)
            "Heavy" -> ResourcesCompat.getFont(context, R.font.heavy) ?: Typeface.create("sans-serif-black", Typeface.NORMAL)
            "Simple" -> ResourcesCompat.getFont(context, R.font.simple) ?: Typeface.create("sans-serif-light", Typeface.NORMAL)
            else -> Typeface.SANS_SERIF // Modern
        }
    } catch (e: Exception) {
        Typeface.SANS_SERIF
    }
}

// --- SAVING LOGIC (Double Slant FIXED) ---
suspend fun saveEditedImage(
    context: Context, originalBitmap: Bitmap, screenWidth: Int, screenHeight: Int,
    text: String, textColor: Color, textSize: Float,
    isBold: Boolean, isItalic: Boolean, hasShadow: Boolean,
    fontName: String, textAlignArg: TextAlign,
    tintColor: Color, offsetX: Float, offsetY: Float, scale: Float, rotation: Float, density: Float
): Uri {
    return withContext(Dispatchers.IO) {
        val resultBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)

        val scaleX = screenWidth.toFloat() / originalBitmap.width
        val scaleY = screenHeight.toFloat() / originalBitmap.height
        val imageScale = max(scaleX, scaleY)
        val dx = (screenWidth - (originalBitmap.width * imageScale)) / 2f
        val dy = (screenHeight - (originalBitmap.height * imageScale)) / 2f
        val matrix = android.graphics.Matrix().apply { postScale(imageScale, imageScale); postTranslate(dx, dy) }
        canvas.drawBitmap(originalBitmap, matrix, Paint(Paint.FILTER_BITMAP_FLAG))

        if (tintColor != Color.Transparent) canvas.drawColor(tintColor.toArgb())

        if (text.isNotBlank()) {
            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = textColor.toArgb()
                this.textSize = textSize * density

                // 1. Load Base Typeface
                val base = getRealTypeface(context, fontName)

                // 2. Set Typeface with Style Intent
                val style = when {
                    isBold && isItalic -> Typeface.BOLD_ITALIC
                    isBold -> Typeface.BOLD
                    isItalic -> Typeface.ITALIC
                    else -> Typeface.NORMAL
                }
                typeface = Typeface.create(base, style)

                // 3. Fake Bold is SAFE (it just thickens lines)
                if (isBold) isFakeBoldText = true

                // 4. Fake Italic is REMOVED
                // We rely on Typeface.create(..., ITALIC) above.
                // This prevents the "Double Slant" bug.

                textAlign = when (textAlignArg) {
                    TextAlign.Left -> Paint.Align.LEFT
                    TextAlign.Right -> Paint.Align.RIGHT
                    else -> Paint.Align.CENTER
                }
                if (hasShadow) setShadowLayer(12f, 0f, 0f, android.graphics.Color.argb(160, 0, 0, 0))
            }
            canvas.save()
            canvas.translate(screenWidth / 2f + offsetX, screenHeight / 2f + offsetY)
            canvas.scale(scale, scale)
            canvas.rotate(rotation)

            val lines = text.split("\n")
            val maxLineWidth = lines.maxOfOrNull { textPaint.measureText(it) } ?: 0f

            val fontMetrics = textPaint.fontMetrics
            val lineHeight = fontMetrics.descent - fontMetrics.ascent
            val totalHeight = lines.size * lineHeight
            
            // Start drawing from the top boundary of the total text block
            var currentY = -totalHeight / 2f - fontMetrics.ascent

            val startX = when (textAlignArg) {
                TextAlign.Left -> -maxLineWidth / 2f
                TextAlign.Right -> maxLineWidth / 2f
                else -> 0f
            }

            lines.forEach { line ->
                canvas.drawText(line, startX, currentY, textPaint)
                currentY += lineHeight
            }
            canvas.restore()
        }
        val file = File(context.cacheDir, "edited_wallpaper_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out -> resultBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out) }
        Uri.fromFile(file)
    }
}

// --- PANELS ---

@Composable
fun FontPanel(currentFontName: String, onFontSelected: (String) -> Unit) {
    val context = LocalContext.current
    val fonts = listOf(
        "Modern", "Classic", "Code", "Handwritten",
        "Marker", "Tech", "Heavy", "Simple"
    )

    Column {
        Text("Choose Font", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp),
            style = TextStyle(shadow = androidx.compose.ui.graphics.Shadow(Color.Black, blurRadius = 6f)))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(fonts) { name ->
                val isSelected = (name == currentFontName)

                // Use REAL typeface for accurate preview
                val base = getRealTypeface(context, name)
                val previewFont = FontFamily(base)

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) LiquidOrange else Color.White.copy(alpha = 0.1f))
                        .clickable { onFontSelected(name) }
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = name,
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
                        fontFamily = previewFont,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun StylePanel(
    activeColor: Color, wallpaperColors: List<Color>, onColorSelected: (Color) -> Unit,
    isBold: Boolean, onBoldToggle: () -> Unit,
    isItalic: Boolean, onItalicToggle: () -> Unit,
    hasShadow: Boolean, onShadowToggle: () -> Unit,
    textSize: Float, onSizeChange: (Float) -> Unit, textAlign: TextAlign, onAlignChange: (TextAlign) -> Unit
) {
    Column {
        Text("Colors", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp),
            style = TextStyle(shadow = androidx.compose.ui.graphics.Shadow(Color.Black, blurRadius = 6f)))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (wallpaperColors.isNotEmpty()) {
                items(wallpaperColors) { color -> ColorDot(color, activeColor == color) { onColorSelected(color) } }
                item { Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.White.copy(alpha = 0.3f))) }
            }
            items(DEFAULT_COLORS) { color -> ColorDot(color, activeColor == color) { onColorSelected(color) } }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(modifier = Modifier.background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp)).padding(4.dp)) {
                AlignButton(Icons.AutoMirrored.Rounded.FormatAlignLeft, textAlign == TextAlign.Left) { onAlignChange(TextAlign.Left) }
                AlignButton(Icons.Rounded.FormatAlignCenter, textAlign == TextAlign.Center) { onAlignChange(TextAlign.Center) }
                AlignButton(Icons.AutoMirrored.Rounded.FormatAlignRight, textAlign == TextAlign.Right) { onAlignChange(TextAlign.Right) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ToolAction("Shd", Icons.Rounded.Layers, hasShadow, onShadowToggle)
                ToolAction("B", Icons.Rounded.FormatBold, isBold, onBoldToggle)
                ToolAction("I", Icons.Rounded.FormatItalic, isItalic, onItalicToggle)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Size: ${textSize.toInt()}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
            style = TextStyle(shadow = androidx.compose.ui.graphics.Shadow(Color.Black, blurRadius = 6f)))
        Slider(value = textSize, onValueChange = onSizeChange, valueRange = 12f..120f, colors = SliderDefaults.colors(thumbColor = LiquidOrange, activeTrackColor = LiquidOrange, inactiveTrackColor = Color.White.copy(alpha = 0.2f)))
    }
}

@Composable
fun TintPanel(onTintSelected: (Color) -> Unit) {
    val tints = listOf(
        "None" to Color.Transparent,
        "Dim" to Color.Black.copy(alpha = 0.4f),
        "Dark" to Color.Black.copy(alpha = 0.7f),
        "Warm" to Color(0xFFFF5722).copy(alpha = 0.3f),
        "Cool" to Color(0xFF2196F3).copy(alpha = 0.3f),
        "Gold" to Color(0xFFFFC107).copy(alpha = 0.3f)
    )
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(tints) { (name, color) ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.1f))
                    .clickable { onTintSelected(color) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(color).border(1.dp, Color.White, CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(name, color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}

// --- HELPERS ---
@Composable fun AdaptiveGlassPanel(isLightImage: Boolean, content: @Composable () -> Unit) {
    val targetColor = if (isLightImage) Color.Black.copy(alpha = 0.88f) else Color.Black.copy(alpha = 0.65f)
    val borderColor = if (isLightImage) Color.White.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.2f)
    val animatedColor by animateColorAsState(targetColor, tween(500), label = "color")
    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(animatedColor).border(1.dp, borderColor, RoundedCornerShape(24.dp)).padding(16.dp)) { content() }
}
@Composable fun ColorDot(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(color).border(if (isSelected) 3.dp else 1.dp, if (isSelected) LiquidOrange else Color.White.copy(alpha = 0.3f), CircleShape).clickable { onClick() })
}
@Composable fun AlignButton(icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(if (isSelected) Color.White.copy(alpha = 0.2f) else Color.Transparent).clickable { onClick() }, contentAlignment = Alignment.Center) { Icon(icon, null, tint = if (isSelected) LiquidOrange else Color.White, modifier = Modifier.size(20.dp)) }
}
@Composable fun TabButton(text: String, icon: ImageVector, isActive: Boolean, isLightImage: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }.padding(8.dp)) {
        Box {
            // Dark shadow layer for legibility on bright wallpapers
            Icon(icon, null, tint = Color.Black.copy(alpha = 0.7f), modifier = Modifier.offset(1.dp, 1.dp).size(24.dp))
            Icon(icon, null, tint = if (isActive) LiquidOrange else Color.White, modifier = Modifier.size(24.dp))
        }
        Box {
            Text(text, color = Color.Black.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.offset(1.dp, 1.dp))
            Text(text, color = if (isActive) LiquidOrange else Color.White, fontSize = 10.sp, fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium)
        }
    }
}
@Composable fun ToolAction(text: String, icon: ImageVector, isActive: Boolean, onClick: () -> Unit) {
    Button(onClick = onClick, colors = ButtonDefaults.buttonColors(containerColor = if (isActive) LiquidOrange else Color.White.copy(alpha = 0.1f)), shape = RoundedCornerShape(12.dp), modifier = Modifier.height(40.dp), contentPadding = PaddingValues(horizontal = 12.dp)
    ) { Icon(icon, null, modifier = Modifier.size(16.dp)); Spacer(modifier = Modifier.width(4.dp)); Text(text, fontSize = 12.sp) }
}
@Composable fun ControlCircle(icon: ImageVector, tint: Color = Color.White, isLightImage: Boolean, onClick: () -> Unit) {
    val bgColor = if (isLightImage) Color.Black.copy(alpha = 0.7f) else Color.Black.copy(alpha = 0.45f)
    Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(bgColor).border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape).clickable { onClick() }, contentAlignment = Alignment.Center) { Icon(icon, null, tint = tint) }
}
fun Modifier.blur(radius: androidx.compose.ui.unit.Dp) = this.graphicsLayer { if (radius.value > 0f) renderEffect = androidx.compose.ui.graphics.BlurEffect(radius.toPx(), radius.toPx()) }