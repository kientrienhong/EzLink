package com.example.linkkeeper.features.link.detail

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.linkkeeper.R
import com.example.linkkeeper.features.common.ApiResult
import com.example.linkkeeper.features.common.views.Header
import com.example.linkkeeper.features.common.views.MyDropdown
import com.example.linkkeeper.features.common.views.MyTextField
import com.example.linkkeeper.features.link.detail.DetailLinkScreenViewModel.ContentHtmlStatus
import com.example.linkkeeper.ui.theme.LocalCustomColors
import com.example.linkkeeper.ui.theme.LocalCustomTypography

@Composable
fun DetailLinkScreen(modifier: Modifier = Modifier, onBackClick: () -> Unit = {}) {
    val customColors = LocalCustomColors.current
    val customTypography = LocalCustomTypography.current
    val viewModel = hiltViewModel<DetailLinkScreenViewModel>()
    val context = LocalContext.current

    val title by viewModel.title.observeAsState("")
    val notes by viewModel.notes.observeAsState("")
    val listAllTags by viewModel.allTagListLiveData.observeAsState(emptyList())
    val listAllTagName = remember(listAllTags) {
        listAllTags.map { it.tag.name }
    }
    val selectedTagName by viewModel.selectedTagName.observeAsState()
    var isEdit by remember { mutableStateOf(false) }
    val hasChanges by viewModel.hasChanges.observeAsState(false)
    val updateResult by viewModel.updateLinkLiveData.observeAsState()
    val hasContentHtml by viewModel.contentStatus.observeAsState(ContentHtmlStatus.NotDownloaded)

    LaunchedEffect(updateResult) {
        if (updateResult is ApiResult.Error) {
            Toast.makeText(context, "Update failed. Please try again!", Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            Log.d("DetailLinkScreen", "DetailLinkScreen Dispose")
        }
    }

    Column(
        modifier
            .background(customColors.background)
            .fillMaxSize()
    ) {
        Header(
            modifier = Modifier,
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
            title = viewModel.link.title,
            rightIcon = {
                if (updateResult is ApiResult.Loading) {
                    CircularProgressIndicator()
                    return@Header
                }

                if (!isEdit) {
                    Icon(
                        painter = painterResource(R.drawable.edit),
                        contentDescription = "Back",
                        tint = customColors.primary,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                isEdit = true
                            }
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.check),
                        contentDescription = "Back",
                        tint = if (hasChanges) {
                            customColors.primary
                        } else {
                            customColors.textPlaceholder
                        },
                        modifier = Modifier
                            .size(14.dp)
                            .clickable {
                                if (!hasChanges) {
                                    return@clickable
                                }
                                viewModel.updateLink()
                            }
                    )
                }
            }
        )
        Column(
            Modifier
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .border(0.5.dp, customColors.border, RoundedCornerShape(16.dp))
                .background(customColors.surface)
                .padding(vertical = 24.dp, horizontal = 16.dp)
        ) {
            MyTextField(
                value = viewModel.link.url,
                onChange = {},
                placeholder = {
                    Text(
                        text = "https://example.com",
                        style = customTypography.body,
                        color = customColors.textPlaceholder
                    )
                },
                label = "URL",
                enable = false,
                modifier = Modifier
                    .fillMaxWidth()
            )
            MyTextField(
                value = title,
                onChange = viewModel::setTitle,
                placeholder = {
                    Text(
                        text = "Link title",
                        style = customTypography.body,
                    )
                },
                label = "TITLE",
                enable = isEdit,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
            MyTextField(
                value = notes,
                onChange = viewModel::setNotes,
                placeholder = {
                    Text(
                        text = "Brief some notes of the link here",
                        style = customTypography.body,
                        color = customColors.textPlaceholder
                    )
                },
                singleLine = false,
                minLines = 3,
                label = "NOTES",
                enable = isEdit,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
            Text(
                text = "CATEGORY",
                style = customTypography.overline,
                color = customColors.subtext,
                modifier = Modifier.padding(start = 16.dp, bottom = 7.dp, top = 16.dp)
            )
            MyDropdown(
                listAllTagName,
                selectedTagName.orEmpty(),
                viewModel::setSelectedTagName,
                enable = isEdit,
            )
        }
        Spacer(Modifier.weight(1f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (hasContentHtml) {
                ContentHtmlStatus.NotDownloaded -> Image(
                    painter = painterResource(R.drawable.download),
                    contentDescription = "Download content",
                    modifier = Modifier
                        .size(50.dp)
                        .clickable { viewModel.downloadContentHtml() }
                )

                ContentHtmlStatus.Available -> Icon(
                    painter = painterResource(R.drawable.delete),
                    contentDescription = "Delete",
                    modifier = Modifier
                        .size(40.dp),
                    tint = customColors.error
                )

                ContentHtmlStatus.Downloading -> CircularProgressIndicator()

                ContentHtmlStatus.NotSupported -> Text("Link is not supported")
            }

            if (hasContentHtml == ContentHtmlStatus.Available) {
                Image(
                    painter = painterResource(R.drawable.preview),
                    contentDescription = "Preview",
                    modifier = Modifier
                        .size(50.dp)
                )
            }
        }
    }
}