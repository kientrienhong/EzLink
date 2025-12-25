package com.timeskip.ezlink

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.timeskip.ezlink.navigation.AppNavHost

@Composable
fun MainContainer(
    shareInfoModel: ShareInfoModel?,
    modifier: Modifier = Modifier,
    onConsumeSharedIntent: () -> Unit = {},
) {
    val navController = rememberNavController()
    AppNavHost(
        navController = navController,
        shareInfoModel,
        modifier = modifier,
        onConsumeSharedIntent = onConsumeSharedIntent,
    )
}

@Preview
@Composable
fun MainContainerPreview() {
    MainContainer(shareInfoModel = null, Modifier)
}