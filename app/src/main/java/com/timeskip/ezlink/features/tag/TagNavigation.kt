package com.timeskip.ezlink.features.tag

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.timeskip.ezlink.features.link.navigateToLink
import com.timeskip.ezlink.features.tag.view.TagScreen
import com.timeskip.ezlink.navigation.TagListDestination
import com.timeskip.ezlink.navigation.TagNavigation

fun NavController.navigateToHomeTag(navOptions: NavOptions? = null) {
    this.navigate(route = TagListDestination, navOptions = navOptions)
}

fun NavGraphBuilder.tagGraph(navController: NavController, modifier: Modifier) {
    navigation<TagNavigation>(TagListDestination) {
        composable<TagListDestination> {
            TagScreen(modifier) { name -> navController.navigateToLink(name) }
        }
    }
}