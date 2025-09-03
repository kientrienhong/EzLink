package com.example.linkkeeper.features.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.linkkeeper.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(modifier: Modifier, navigateToHomeTag: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1000)
        navigateToHomeTag()
    }

    Box(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Image(
            painterResource(R.drawable.logo),
            contentDescription = null,
            modifier = Modifier.align(Alignment.Center).size(160.dp)
        )
    }
}