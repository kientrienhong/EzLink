package com.example.linkkeeper.features.splash

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.linkkeeper.features.home.view.navigateToHomeTag
import com.example.linkkeeper.navigation.SplashDestination

fun NavGraphBuilder.splashGraph(navController: NavController) {
    composable<SplashDestination> {
        SplashScreen(Modifier.fillMaxSize()) {
            navController.popBackStack()
            navController.navigateToHomeTag()
        }
    }
}