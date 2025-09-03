package com.example.linkkeeper.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class DestinationItem

@Serializable
class HomeNavigation : DestinationItem()

@Serializable
data object SplashDestination : DestinationItem()

@Serializable
data object TagNavigation : DestinationItem()

@Serializable
data object TagListDestination : DestinationItem()

@Serializable
data object LinkNavigation : DestinationItem()

@Serializable
data class LinkDestination(val tagId: Int, val tagName: String?) : DestinationItem()

@Serializable
data class LinkEditorDestination(
    val tagId: Int,
    val tagName: String?,
    val linkString: String,
    val isEdit: Boolean
) : DestinationItem()