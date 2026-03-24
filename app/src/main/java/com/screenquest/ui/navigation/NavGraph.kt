package com.screenquest.ui.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.screenquest.ui.dashboard.DashboardScreen
import com.screenquest.ui.profile.ProfileScreen
import com.screenquest.ui.quests.QuestsScreen
import com.screenquest.ui.stats.StatsScreen

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Quests : Screen("quests")
    object Stats : Screen("stats")
    object Profile : Screen("profile")
}

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) { DashboardScreen() }
        composable(Screen.Quests.route) { QuestsScreen() }
        composable(Screen.Stats.route) { StatsScreen() }
        composable(Screen.Profile.route) { ProfileScreen() }
    }
}