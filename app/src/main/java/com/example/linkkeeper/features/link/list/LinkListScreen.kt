package com.example.linkkeeper.features.link.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.linkkeeper.R
import com.example.linkkeeper.features.common.linkdialog.LinkModificationDialog
import com.example.linkkeeper.features.common.views.Header
import com.example.linkkeeper.features.link.data.Link
import com.example.linkkeeper.ui.theme.LocalCustomColors
import com.example.linkkeeper.ui.theme.LocalCustomTypography

@Composable
fun LinkListScreen(
    modifier: Modifier = Modifier,
    navigateToDetailLink: (Link) -> Unit,
    onBackClick: () -> Unit = {},
) {
    val customColors = LocalCustomColors.current
    val viewModel = hiltViewModel<LinkScreenViewModel>()
    val links by viewModel.linkListLiveData.observeAsState(emptyList())
    var showAddLinksDialog by remember { mutableStateOf(false) }
    val allTagListLiveData by viewModel.allTagListLiveData.observeAsState(emptyList())

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(customColors.background)
    ) {
        Header(
            viewModel.tagName,
            leftIcon = {
                Icon(
                    painter = painterResource(R.drawable.back_arrow),
                    contentDescription = "Back",
                    tint = customColors.primary,
                    modifier = Modifier
                        .size(14.dp)
                        .clickable { onBackClick() }
                )
            },
            rightIcon = {
                Icon(
                    painter = painterResource(R.drawable.ellipsis_circle),
                    contentDescription = "Header action",
                    tint = customColors.primary,
                    modifier = Modifier
                        .size(width = 26.dp, height = 22.dp)
                        .clickable { }
                )
            }
        )
        Box(modifier = Modifier.weight(1f)) {
            ListLinkItem(
                links = links,
                allTagListLiveData,
                onNavigate = navigateToDetailLink,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }
        BottomNavigationBar(
            numberOfLink = links.size,
            onAddLinksClick = { showAddLinksDialog = true }
        )
        if (showAddLinksDialog) {
            LinkModificationDialog(
                allTagListLiveData,
                onDismiss = { showAddLinksDialog = false },
                tagId = viewModel.tagId
            )
        }
    }
}

@Composable
private fun BottomNavigationBar(
    numberOfLink: Int,
    onAddLinksClick: () -> Unit
) {
    val textLink = if (numberOfLink > 1) "links" else "link"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(50.dp)) { }
        Text(
            "$numberOfLink $textLink",
            style = LocalCustomTypography.current.caption,
            color = LocalCustomColors.current.subtext
        )
        Image(
            painter = painterResource(R.drawable.link_add),
            contentDescription = "Add Links",
            modifier = Modifier
                .size(50.dp)
                .clickable { onAddLinksClick() }
        )
    }
}