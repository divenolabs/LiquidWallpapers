package com.example.liquidwallpapers.ui.screens.details

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.liquidwallpapers.data.model.Wallpaper
import com.example.liquidwallpapers.ui.theme.DeepBlue
import com.example.liquidwallpapers.ui.theme.LiquidOrange
import com.example.liquidwallpapers.util.BitmapUtils
import com.example.liquidwallpapers.util.ImageHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun DetailScreen(
    wallpaper: Wallpaper,
    onBack: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Database Status
    LaunchedEffect(wallpaper.id) {
        viewModel.checkFavoriteStatus(wallpaper.id)
    }
    val isFavorite by viewModel.isFavorite.collectAsState()

    // Image State
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    // --- EDITOR STATE ---
    var isEditing by remember { mutableStateOf(false) }
    var blurValue by remember { mutableFloatStateOf(0f) }   // 0f to 25f
    var dimValue by remember { mutableFloatStateOf(0f) }    // 0f to 0.8f

    // UI Animations
    var controlsVisible by remember { mutableStateOf(false) }

    // Zoom/Pan State
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidth = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var minScale by remember { mutableFloatStateOf(1f) }

    // Load Image
    LaunchedEffect(wallpaper.url) {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(wallpaper.url)
            .allowHardware(false)
            .build()
        val result = loader.execute(request)
        if (result is SuccessResult) {
            bitmap = result.drawable.toBitmap()
            delay(300)
            controlsVisible = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlue)
    ) {
        if (bitmap == null) {
            CircularProgressIndicator(
                color = LiquidOrange,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            val imageBitmap = bitmap!!

            LaunchedEffect(imageBitmap) {
                val scaleX = screenWidth / imageBitmap.width
                val scaleY = screenHeight / imageBitmap.height
                minScale = max(scaleX, scaleY)
                scale = minScale
                val scaledWidth = imageBitmap.width * minScale
                val scaledHeight = imageBitmap.height * minScale
                offsetX = (screenWidth - scaledWidth) / 2f
                offsetY = (screenHeight - scaledHeight) / 2f
            }

            // --- CANVAS (Handles Zoom + Live Preview of Dim/Blur) ---
            val previewDimMatrix = remember(dimValue) {
                val m = ColorMatrix()
                val scaleV = 1f - dimValue
                m.setToScale(scaleV, scaleV, scaleV, 1f)
                m
            }

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (Build.VERSION.SDK_INT >= 31 && blurValue > 0) {
                            Modifier.blur(blurValue.dp)
                        } else Modifier
                    )
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            val newScale = (scale * zoom).coerceIn(minScale, minScale * 5f)
                            val currentWidth = imageBitmap.width * newScale
                            val currentHeight = imageBitmap.height * newScale
                            val minX = screenWidth - currentWidth
                            val maxX = 0f
                            val minY = screenHeight - currentHeight
                            val maxY = 0f
                            val newX = (offsetX + pan.x).coerceIn(minX, maxX)
                            val newY = (offsetY + pan.y).coerceIn(minY, maxY)
                            scale = newScale
                            offsetX = newX
                            offsetY = newY
                            controlsVisible = false
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { controlsVisible = !controlsVisible })
                    }
            ) {
                withTransform({
                    translate(left = offsetX, top = offsetY)
                    scale(scaleX = scale, scaleY = scale, pivot = Offset.Zero)
                }) {
                    drawImage(
                        image = imageBitmap.asImageBitmap(),
                        colorFilter = ColorFilter.colorMatrix(previewDimMatrix)
                    )
                }
            }
        }

        // --- TOP CONTROLS ---
        AnimatedVisibility(
            visible = controlsVisible && !isEditing, // Hide top bar when editing
            enter = slideInVertically(tween(500)) { -it },
            exit = slideOutVertically(tween(500)) { -it },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .highContrastGlass(CircleShape)
                        .bounceClick { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back", tint = Color.White, modifier = Modifier.size(24.dp))
                }

                Spacer(modifier = Modifier.weight(1f))

                // Favorite Button
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .highContrastGlass(CircleShape)
                        .bounceClick { viewModel.toggleFavorite(wallpaper) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFFF4040) else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // EDIT Button
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .highContrastGlass(CircleShape)
                        .bounceClick { isEditing = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Edit, "Edit", tint = Color.White, modifier = Modifier.size(24.dp))
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Download Button
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .highContrastGlass(CircleShape)
                        .bounceClick {
                            if (bitmap != null) {
                                // 1. TRACK DOWNLOAD (Required by Unsplash)
                                viewModel.trackDownload(wallpaper)

                                scope.launch {
                                    val finalBm = withContext(Dispatchers.Default) {
                                        BitmapUtils.applyEffects(context, bitmap!!, blurValue, -dimValue)
                                    }
                                    ImageHelper.saveImageToGallery(context, finalBm, wallpaper.title)
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Download, "Download", tint = Color.White, modifier = Modifier.size(24.dp))
                }
            }
        }

        // --- BOTTOM DOCK (Normal Mode) ---
        AnimatedVisibility(
            visible = controlsVisible && !isEditing,
            enter = slideInVertically(tween(500)) { it },
            exit = slideOutVertically(tween(500)) { it },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = 32.dp, start = 24.dp, end = 24.dp)
            ) {
                // --- ATTRIBUTION TEXT (Required by Unsplash) ---
                // "Photo by [Name] on Unsplash"
                val annotatedString = buildAnnotatedString {
                    append("Photo by ")
                    pushStringAnnotation(tag = "user", annotation = wallpaper.photographerUrl)
                    withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline, fontWeight = FontWeight.Bold)) {
                        append(wallpaper.photographer)
                    }
                    pop()
                    append(" on ")
                    pushStringAnnotation(tag = "unsplash", annotation = "https://unsplash.com/?utm_source=LiquidWallpapers&utm_medium=referral")
                    withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline, fontWeight = FontWeight.Bold)) {
                        append("Unsplash")
                    }
                    pop()
                }

                Text(
                    text = annotatedString,
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .clickable {
                            // Handle click logic manually for now, or just open the user profile
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(wallpaper.photographerUrl + "?utm_source=LiquidWallpapers&utm_medium=referral"))
                            context.startActivity(intent)
                        }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .highContrastGlass(RoundedCornerShape(24.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f).padding(end = 8.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Preview & adjust",
                                color = Color.White.copy(alpha = 0.8f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = "Set Your Wallpaper",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        Button(
                            onClick = { if (bitmap != null) showDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(0.dp),
                            shape = CircleShape,
                            modifier = Modifier
                                .height(48.dp)
                                .bounceClick { if (bitmap != null) showDialog = true }
                                .background(
                                    brush = Brush.horizontalGradient(colors = listOf(LiquidOrange, Color(0xFFB02A02))),
                                    shape = CircleShape
                                )
                        ) {
                            if (isProcessing) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 20.dp)) {
                                    Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Apply", color = Color.White, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- EDITOR CONTROLS (Editing Mode) ---
        AnimatedVisibility(
            visible = isEditing,
            enter = slideInVertically(tween(500)) { it },
            exit = slideOutVertically(tween(500)) { it },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .navigationBarsPadding()
                    .padding(24.dp)
            ) {
                Column {
                    // Title + Done Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Edit Wallpaper", color = Color.White, style = MaterialTheme.typography.titleMedium)
                        IconButton(onClick = { isEditing = false }) {
                            Icon(Icons.Rounded.Close, "Done", tint = LiquidOrange)
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // DIM Slider
                    Text("Dim / Brightness", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelMedium)
                    Slider(
                        value = dimValue,
                        onValueChange = { dimValue = it },
                        valueRange = 0f..0.8f, // Max 80% dim
                        colors = SliderDefaults.colors(
                            thumbColor = LiquidOrange,
                            activeTrackColor = LiquidOrange
                        )
                    )

                    Spacer(Modifier.height(8.dp))

                    // BLUR Slider
                    Text("Blur", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelMedium)
                    Slider(
                        value = blurValue,
                        onValueChange = { blurValue = it },
                        valueRange = 0f..25f, // Max RenderScript radius
                        colors = SliderDefaults.colors(
                            thumbColor = LiquidOrange,
                            activeTrackColor = LiquidOrange
                        )
                    )
                }
            }
        }

        // --- WALLPAPER OPTIONS DIALOG ---
        if (showDialog && bitmap != null) {
            WallpaperOptionDialog(
                onDismiss = { showDialog = false },
                onOptionSelected = { flag ->
                    showDialog = false
                    isProcessing = true

                    // 1. TRACK DOWNLOAD (Required by Unsplash)
                    viewModel.trackDownload(wallpaper)

                    scope.launch {
                        // 2. Process Bitmap
                        val processedBitmap = withContext(Dispatchers.Default) {
                            BitmapUtils.applyEffects(context, bitmap!!, blurValue, -dimValue)
                        }

                        // 3. Set Wallpaper
                        cropAndSetWallpaper(
                            context = context,
                            originalBitmap = processedBitmap,
                            screenWidth = screenWidth,
                            screenHeight = screenHeight,
                            offsetX = offsetX,
                            offsetY = offsetY,
                            scale = scale,
                            flag = flag
                        )
                        isProcessing = false
                    }
                }
            )
        }
    }
}

// --- HELPER FUNCTIONS ---

suspend fun cropAndSetWallpaper(
    context: Context,
    originalBitmap: Bitmap,
    screenWidth: Float,
    screenHeight: Float,
    offsetX: Float,
    offsetY: Float,
    scale: Float,
    flag: Int
) {
    withContext(Dispatchers.IO) {
        try {
            val visibleX = (-offsetX / scale).roundToInt().coerceIn(0, originalBitmap.width)
            val visibleY = (-offsetY / scale).roundToInt().coerceIn(0, originalBitmap.height)
            val visibleWidth = (screenWidth / scale).roundToInt()
            val visibleHeight = (screenHeight / scale).roundToInt()
            val cropWidth = visibleWidth.coerceAtMost(originalBitmap.width - visibleX)
            val cropHeight = visibleHeight.coerceAtMost(originalBitmap.height - visibleY)

            if (cropWidth > 0 && cropHeight > 0) {
                val croppedBitmap = Bitmap.createBitmap(originalBitmap, visibleX, visibleY, cropWidth, cropHeight)
                val wallpaperManager = WallpaperManager.getInstance(context)
                wallpaperManager.setBitmap(croppedBitmap, null, true, flag)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Wallpaper Updated!", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun WallpaperOptionDialog(onDismiss: () -> Unit, onOptionSelected: (Int) -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .highContrastGlass(RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Rounded.Wallpaper, null, tint = LiquidOrange, modifier = Modifier.size(32.dp))
                Spacer(Modifier.height(16.dp))
                Text("Set Wallpaper", style = MaterialTheme.typography.titleLarge, color = Color.White)
                Text("Choose where to apply", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.7f))
                Spacer(Modifier.height(24.dp))
                GlassOptionButton("Home Screen") { onOptionSelected(WallpaperManager.FLAG_SYSTEM) }
                Spacer(Modifier.height(8.dp))
                GlassOptionButton("Lock Screen") { onOptionSelected(WallpaperManager.FLAG_LOCK) }
                Spacer(Modifier.height(8.dp))
                GlassOptionButton("Both Screens") { onOptionSelected(WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK) }
                Spacer(Modifier.height(16.dp))
                TextButton(onClick = onDismiss) { Text("Cancel", color = Color.White.copy(alpha = 0.6f)) }
            }
        }
    }
}

@Composable
fun GlassOptionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().height(50.dp)
            .bounceClick { onClick() }
    ) {
        Text(text, color = Color.White)
    }
}

fun Modifier.highContrastGlass(shape: androidx.compose.ui.graphics.Shape) = this
    .clip(shape)
    .background(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Black.copy(alpha = 0.4f),
                Color.Black.copy(alpha = 0.75f)
            )
        )
    )
    .border(
        width = 1.dp,
        brush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.3f),
                Color.Transparent,
                Color.White.copy(alpha = 0.1f)
            )
        ),
        shape = shape
    )

fun Modifier.bounceClick(scaleDown: Float = 0.95f, onClick: () -> Unit) = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "bounce"
    )
    this
        .graphicsLayer { scaleX = scale; scaleY = scale }
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = { isPressed = true; tryAwaitRelease(); isPressed = false },
                onTap = { onClick() }
            )
        }
}