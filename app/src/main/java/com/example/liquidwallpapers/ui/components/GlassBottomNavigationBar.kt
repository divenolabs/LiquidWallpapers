package com.example.liquidwallpapers.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 16.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
            .height(64.dp)
            .background(Color(0x80000000), RoundedCornerShape(50))
            .glassEffect(RoundedCornerShape(50))
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        bottomNavItems.forEach { item ->
            // If we are on detail or text editor screen we probably don't want this bar shown at all.
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
    
    val iconColor = if (isSelected) Color.Black else Color.White.copy(alpha = 0.5f)
    
    // Increased interactive area to 48.dp for better touch targets and perfect centering
    val containerModifier = Modifier
        .size(48.dp)
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
        .then(
            if (isSelected) Modifier.background(LiquidOrange, CircleShape)
            else Modifier
        )

    Box(
        modifier = containerModifier,
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null, // UI implies meaning, no visible title
            tint = iconColor,
            modifier = Modifier.size(26.dp) // Slightly boosted icon size
        )
    }
}
