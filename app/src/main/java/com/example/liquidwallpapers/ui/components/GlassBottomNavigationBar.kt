package com.example.liquidwallpapers.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.liquidwallpapers.ui.theme.LiquidOrange

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem("home", "Explore", Icons.Rounded.Explore),
    BottomNavItem("categories_tab", "Categories", Icons.Rounded.GridView),
    BottomNavItem("daily_mix", "Daily Mix", Icons.Rounded.PieChart),
    BottomNavItem("premium", "Studio", Icons.Rounded.Brush),
    BottomNavItem("profile", "Profile", Icons.Rounded.Person)
)

@Composable
fun GlassBottomNavigationBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onReselect: (String) -> Unit = {}
) {
    // Breathing effect for the alive navbar
    val infiniteTransition = rememberInfiniteTransition(label = "navbar_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "pulse"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 16.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
            .height(64.dp)
            .glassEffect(RoundedCornerShape(50))
            .background(Color(0x80000000), RoundedCornerShape(50))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        LiquidOrange.copy(alpha = pulseAlpha),
                        Color.White.copy(alpha = 0.05f)
                    )
                ),
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.route
            
            BottomNavItemView(
                item = item,
                isSelected = isSelected,
                onClick = {
                    if (isSelected) onReselect(item.route)
                    else onNavigate(item.route)
                }
            )
        }
    }
}

@Composable
fun BottomNavItemView(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    // Smooth transitions for selection state
    val isSelectedAnim by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f, 
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "selected"
    )
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) Color.Black else Color.White.copy(alpha = 0.5f), 
        animationSpec = tween(300),
        label = "iconCol"
    )
    
    val containerModifier = Modifier
        .size(48.dp)
        .graphicsLayer {
            // Adds a subtle breathing pop when selected
            scaleX = 0.8f + (0.2f * isSelectedAnim)
            scaleY = 0.8f + (0.2f * isSelectedAnim)
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
        .background(LiquidOrange.copy(alpha = isSelectedAnim.coerceIn(0f, 1f)), CircleShape)

    Box(
        modifier = containerModifier,
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null, 
            tint = iconColor,
            modifier = Modifier.size(26.dp)
        )
    }
}
