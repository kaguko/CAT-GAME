package com.example.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.RetroGameScreen
import com.example.ui.screens.SkinScreen
import com.example.ui.screens.ThesisDocsScreen
import com.example.viewmodel.CatGameViewModel

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Game : Screen("game", "Trò Chơi", Icons.Default.Pets)
    object Skins : Screen("skins", "Skin Mèo", Icons.Default.Style)
    object Docs : Screen("docs", "Báo Cáo Đồ Án", Icons.Default.Description)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TurboPurrCatMainApp() {
    val viewModel: CatGameViewModel = viewModel()
    var currentTab by remember { mutableStateOf<Screen>(Screen.Game) }

    val items = listOf(Screen.Game, Screen.Skins, Screen.Docs)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Turbo Purr Cat") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF78350F),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFFFEF3C7)
            ) {
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentTab == screen,
                        onClick = { currentTab = screen },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF78350F),
                            unselectedIconColor = Color(0xFFB45309),
                            selectedTextColor = Color(0xFF78350F),
                            unselectedTextColor = Color(0xFFB45309),
                            indicatorColor = Color(0xFFFDE68A)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            when (currentTab) {
                Screen.Game -> RetroGameScreen(viewModel = viewModel)
                Screen.Skins -> SkinScreen(viewModel = viewModel)
                Screen.Docs -> ThesisDocsScreen()
            }
        }
    }
}
