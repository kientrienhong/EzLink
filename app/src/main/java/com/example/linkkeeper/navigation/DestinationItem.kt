package com.example.linkkeeper.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class DestinationItem

@Serializable
data object HomeNavigation : DestinationItem()

@Serializable
data object TagNavigation : DestinationItem()

@Serializable
data object TagListDestination : DestinationItem()

@Serializable
data object SearchNavigation : DestinationItem()

@Serializable
data class LinkDestination(val tagId: String, val tagName: String?) : DestinationItem()
