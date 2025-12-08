package com.timeskip.ezlink.features.link.search

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.timeskip.ezlink.features.common.views.GrayLogoWithTextView
import com.timeskip.ezlink.features.link.data.Link
import com.timeskip.ezlink.features.link.editor.LinkDetailScreen
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
private data class LinkEditorScreenArgs(val link: Link, val isEdit: Boolean) : Parcelable

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun LinkSearchContainer(search: String, popBack: () -> Unit, modifier: Modifier = Modifier) {
    val scaffoldNavigator = rememberListDetailPaneScaffoldNavigator<LinkEditorScreenArgs>(
        isDestinationHistoryAware = true
    )
    val scope = rememberCoroutineScope()

    NavigableListDetailPaneScaffold(
        navigator = scaffoldNavigator,
        listPane = {
            AnimatedPane(modifier = modifier) {
                LinkSearchScreen(
                    search = search,
                    onBack = popBack,
                    onItemClick = { link, isEdit ->
                        scope.launch {
                            scaffoldNavigator.navigateTo(
                                ListDetailPaneScaffoldRole.Detail,
                                LinkEditorScreenArgs(link = link, isEdit = isEdit)
                            )
                        }
                    },
                )
            }
        },
        paneExpansionDragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .background(MaterialTheme.colorScheme.outline)
            )
        },
        detailPane = {
            if (scaffoldNavigator.currentDestination?.pane == ListDetailPaneScaffoldRole.List) {
                GrayLogoWithTextView(Modifier.fillMaxSize(), "Select a link to edit")
                return@NavigableListDetailPaneScaffold
            }

            AnimatedPane(modifier = modifier) {
                val selectedItem =
                    scaffoldNavigator.currentDestination?.contentKey ?: return@AnimatedPane
                val link = selectedItem.link
                LinkDetailScreen(
                    link,
                    popNavigation = {
                        if (scaffoldNavigator.currentDestination?.pane == ListDetailPaneScaffoldRole.Detail) {
                            scope.launch {
                                scaffoldNavigator.navigateBack()
                            }
                        } else {
                            popBack()
                        }
                    }
                )
            }
        },
        defaultBackBehavior = BackNavigationBehavior.PopUntilScaffoldValueChange
    )
}