package com.example.linkkeeper.features.link.detail

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.linkkeeper.features.link.data.Link
import com.example.linkkeeper.navigation.DetailLinkDestination
import kotlinx.serialization.json.Json

fun NavController.navigateToDetailLink(link: Link, navOptions: NavOptions? = null) {
    val string = Json.encodeToString(Link.serializer(), link)
    this.navigate(DetailLinkDestination(string), navOptions = navOptions)
}

fun NavGraphBuilder.detailLinkGraph(navController: NavController, modifier: Modifier) {
    composable<DetailLinkDestination> {
        DetailLinkScreen(modifier, navController::popBackStack)
    }
}
