package com.timeskip.ezlink.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class DestinationItem

@Serializable
data object TagNavigation : DestinationItem()

@Serializable
data object TagListDestination: DestinationItem()

@Serializable
data class LinkSearchNavigation(val search: String) : DestinationItem()

@Serializable
data class LinkDestination(val tagName: String) : DestinationItem()

@Serializable
data class LinkEditorDestination(
    val tagId: Int,
    val tagName: String?,
    val linkString: String,
    val isEdit: Boolean
) : DestinationItem()