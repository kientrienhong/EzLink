package com.timeskip.ezlink.features.tag

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.timeskip.ezlink.features.link.navigateToLink
import com.timeskip.ezlink.features.link.navigateToSearchLink
import com.timeskip.ezlink.features.tag.view.TagScreen
import com.timeskip.ezlink.navigation.TagListDestination
import com.timeskip.ezlink.navigation.TagNavigation

fun NavGraphBuilder.tagGraph(
    sharedTagName: String?,
    sharedUrl: String?,
    navController: NavController,
    resetSharedData: () -> Unit,
    modifier: Modifier
) {
    navigation<TagNavigation>(TagListDestination(null)) {
        composable<TagListDestination> {
            TagScreen(
                sharedTagName,
                sharedUrl,
                modifier,
                onTagClick = { name ->
                    navController.navigateToLink(
                        name,
                        NavOptions.Builder().setLaunchSingleTop(true).build()
                    )
                },
                onSearchClick = { navController.navigateToSearchLink(it) },
                resetSharedData = resetSharedData
            )
        }
    }
}