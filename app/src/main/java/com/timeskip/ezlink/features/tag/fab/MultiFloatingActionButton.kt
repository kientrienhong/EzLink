package com.timeskip.ezlink.features.tag.fab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

sealed class FabButtonState(val image: ImageVector) {
    data object Collapsed : FabButtonState(Icons.Filled.Add)
    data object Expand : FabButtonState(Icons.Filled.Close)

    fun isExpanded() = this == Expand

    fun toggleValue() = if (isExpanded()) {
        Collapsed
    } else {
        Expand
    }
}

@Composable
private fun rememberMultiFabState() =
    remember { mutableStateOf<FabButtonState>(FabButtonState.Collapsed) }

@Composable
fun MultiFloatingActionButton(
    list: List<FabViewItem>,
    modifier: Modifier = Modifier,
    fabState: MutableState<FabButtonState> = rememberMultiFabState(),
    stateChanged: (fabState: FabButtonState) -> Unit = {}
) {
    // Animation for rotating the main FAB icon based on its state (expanded or collapsed)
    val rotation by animateFloatAsState(
        if (fabState.value == FabButtonState.Expand) {
            90f
        } else {
            0f
        },
        label = ""
    )

    Column(
        modifier = modifier.wrapContentSize(),
        horizontalAlignment = Alignment.End
    ) {
        AnimatedVisibility(
            visible = fabState.value.isExpanded(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            LazyColumn(
                modifier = Modifier.wrapContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                items(list.size) { index ->
                    MiniFabItem(list[index])
                }
                item {}
            }
        }

        FloatingActionButton(
            onClick = {
                fabState.value = fabState.value.toggleValue()
                stateChanged(fabState.value)
            },
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = fabState.value.image,
                contentDescription = "",
                modifier = Modifier
                    .rotate(rotation)
                    .size(24.dp),
                tint = Color.White
            )
        }
    }
}