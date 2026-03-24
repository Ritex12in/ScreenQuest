package com.screenquest.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.screenquest.ui.navigation.NavGraph
import com.screenquest.ui.theme.ScreenQuestTheme
import com.screenquest.ui.components.BottomNavBar
import com.screenquest.ui.main.MainViewModel
import com.screenquest.ui.permission.PermissionScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScreenQuestTheme {
                val state by viewModel.state.collectAsState()
                val navController = rememberNavController()

                when {
                    state.permissionRequired -> {
                        PermissionScreen(
                            onGrantClick = {
                                startActivity(
                                    Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                                )
                            }
                        )
                    }
                    else -> {
                        Scaffold(
                            bottomBar = { BottomNavBar(navController) }
                        ) { padding ->
                            NavGraph(
                                navController = navController,
                                modifier = Modifier.padding(padding)
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Re-check permission and sync every time app comes to foreground
        viewModel.onAppOpen()
    }
}