package com.timeskip.ezlink.features.tag

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.timeskip.ezlink.ShareInfoModel
import com.timeskip.ezlink.features.link.navigateToLink
import com.timeskip.ezlink.features.link.navigateToSearchLink
import com.timeskip.ezlink.features.tag.view.TagScreen
import com.timeskip.ezlink.navigation.TagListDestination
import com.timeskip.ezlink.navigation.TagNavigation

fun NavGraphBuilder.tagGraph(
    navController: NavController,
    modifier: Modifier,
    shareInfoModel: ShareInfoModel?,
    paddingValues: PaddingValues,
    onConsumeSharedIntent: () -> Unit,
) {
    navigation<TagNavigation>(TagListDestination) {
        composable<TagListDestination> {
            TagScreen(
                onTagClick = { name ->
                    navController.navigateToLink(
                        name,
                        NavOptions.Builder()
                            .setPopUpTo<TagListDestination>(inclusive = false)
                            .build()
                    )
                },
                onSearchClick = { navController.navigateToSearchLink(it) },
                shareInfoModel = shareInfoModel,
                onConsumeSharedIntent = onConsumeSharedIntent,
                modifier = modifier,
                paddingValues = paddingValues,
            )
        }
    }
}

/**
 * Bring the user back to [TagListDestination] (TagScreen).
 *
 * - If TagListDestination is already on the back stack, we pop back to it.
 * - Otherwise we navigate to it and clear up to the start of the Tag graph.
 */
fun NavController.popUpToTagListOrNavigate(navOptions: NavOptions? = null) {
    // If TagListDestination exists in the back stack, just pop back to it.
    if (this.popBackStack(TagListDestination, inclusive = false)) return

    // Otherwise navigate to it, avoiding duplicates.
    val finalOptions = navOptions ?: NavOptions.Builder()
        .setLaunchSingleTop(true)
        // Clear to the root of the current graph to avoid deep stacks when coming from an external intent.
        .setPopUpTo(this.graph.findStartDestination().id, inclusive = false)
        .build()

    this.navigate(TagListDestination, finalOptions)
}
