package com.timeskip.ezlink

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.timeskip.ezlink.navigation.AppNavHost

@Composable
fun MainContainer(
    shareInfoModel: ShareInfoModel?,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
    onConsumeSharedIntent: () -> Unit = {},
) {
    val navController = rememberNavController()
    AppNavHost(
        navController = navController,
        shareInfoModel,
        modifier = modifier,
        paddingValues = paddingValues,
        onConsumeSharedIntent = onConsumeSharedIntent,
    )
}

@Preview
@Composable
fun MainContainerPreview() {
    MainContainer(shareInfoModel = null, PaddingValues(2.dp), Modifier)
}