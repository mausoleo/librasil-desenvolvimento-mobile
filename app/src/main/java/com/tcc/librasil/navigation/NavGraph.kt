package com.tcc.librasil.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tcc.librasil.ui.screens.SplashScreen
import com.tcc.librasil.ui.screens.MainScreen
import com.tcc.librasil.ui.screens.InfoScreen
import com.tcc.librasil.ui.screens.GestureCaptureScreen
import com.tcc.librasil.ui.screens.TextToLibrasScreen
import com.tcc.librasil.ui.screens.LibraryScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Main : Screen("main")
    object Info : Screen("info")
    object GestureCapture : Screen("gesture_capture")
    object TextToLibras : Screen("text_to_libras")
    object Library : Screen("library")
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        
        composable(Screen.Main.route) {
            MainScreen(navController = navController)
        }
        
        composable(Screen.Info.route) {
            InfoScreen(navController = navController)
        }
        
        composable(Screen.GestureCapture.route) {
            GestureCaptureScreen(navController = navController)
        }
        
        composable(Screen.TextToLibras.route) {
            TextToLibrasScreen(navController = navController)
        }
        
        composable(Screen.Library.route) {
            LibraryScreen(navController = navController)
        }
    }
}
