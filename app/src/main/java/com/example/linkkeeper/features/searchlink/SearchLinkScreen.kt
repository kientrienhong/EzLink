package com.example.linkkeeper.features.searchlink

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.linkkeeper.R
import com.example.linkkeeper.features.common.views.MyTextField
import com.example.linkkeeper.features.link.list.ListLinkItem
import com.example.linkkeeper.ui.theme.LocalCustomColors
import com.example.linkkeeper.ui.theme.LocalCustomTypography

@Composable
internal fun SearchLinkScreen(modifier: Modifier) {
    val customColors = LocalCustomColors.current
    val customTypography = LocalCustomTypography.current
    val viewModel = hiltViewModel<SearchLinkViewModel>()
    val searchText by viewModel.searchLiveData.observeAsState("")
    val allTagList by viewModel.allTagListLiveData.observeAsState(emptyList())
    val links by viewModel.listLinkLiveData.observeAsState(emptyList())

    Column(
        modifier
            .background(customColors.background)
            .fillMaxSize()
            .padding(16.dp)
    ) {
        MyTextField(
            value = searchText,
            onChange = viewModel::updateSearch,
            placeholder = {
                Text(
                    "Search",
                    style = customTypography.body,
                    color = customColors.textPlaceholder
                )
            },
            trailingIcon = {
                Image(
                    ImageVector.vectorResource(R.drawable.search),
                    contentDescription = "Search",
                    modifier = Modifier.size(44.dp)
                )
            },
            containerColor = customColors.surface
        )
        Box(modifier = Modifier.weight(1f)) {
            ListLinkItem(
                links = links,
                allTagList,
                onNavigate = {},
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
            )
        }
    }
}