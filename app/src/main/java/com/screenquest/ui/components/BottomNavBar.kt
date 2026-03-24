package com.screenquest.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.screenquest.R
import com.screenquest.ui.navigation.Screen
import com.screenquest.ui.theme.Amber
import com.screenquest.ui.theme.SurfaceVariant
import com.screenquest.ui.theme.TextSecondary

data class NavItem(
    val screen: Screen,
    val label: String,
    val iconRes: Int
)

val navItems = listOf(
    NavItem(Screen.Dashboard, "Home", R.drawable.outline_home_24),
    NavItem(Screen.Quests, "Quests", R.drawable.outline_videogame_asset_24),
    NavItem(Screen.Stats, "Stats", R.drawable.outline_bar_chart_24),
    NavItem(Screen.Profile, "Profile", R.drawable.outline_person_24)
)

@Composable
fun BottomNavBar(navController: NavController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar(containerColor = SurfaceVariant) {
        navItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.screen.route,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(Screen.Dashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(item.iconRes),
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Amber,
                    selectedTextColor = Amber,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = SurfaceVariant
                )
            )
        }
    }
}