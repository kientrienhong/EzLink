package com.timeskip.ezlink

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.timeskip.ezlink.navigation.AppNavHost

@Composable
fun MainContainer(
    modifier: Modifier = Modifier,
    sharedUrl: String? = null,
    sharedTagName: String? = null,
    onConsumeSharedIntent: () -> Unit = {},
) {
    val navController = rememberNavController()
    AppNavHost(
        navController = navController,
        modifier = modifier,
        sharedUrl = sharedUrl,
        sharedTagName = sharedTagName,
        onConsumeSharedIntent = onConsumeSharedIntent,
    )
}

@Preview
@Composable
fun MainContainerPreview() {
    MainContainer(Modifier)
}