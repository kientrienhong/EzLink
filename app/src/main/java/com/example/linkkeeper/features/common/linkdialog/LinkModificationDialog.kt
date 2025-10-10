package com.example.linkkeeper.features.common.linkdialog

import android.view.Gravity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.linkkeeper.features.common.ApiResult
import com.example.linkkeeper.features.common.views.MyDropdown
import com.example.linkkeeper.features.common.views.MyTextField
import com.example.linkkeeper.features.link.data.Link
import com.example.linkkeeper.features.tag.view.TagViewItem
import com.example.linkkeeper.ui.theme.LocalCustomColors
import com.example.linkkeeper.ui.theme.LocalCustomTypography
import java.util.UUID

@Composable
internal fun LinkModificationDialog(
    listAllTags: List<TagViewItem>,
    link: Link? = null,
    onDismiss: () -> Unit = {},
    tagId: String? = null
) {
    val customColors = LocalCustomColors.current
    val customTypography = LocalCustomTypography.current
    var url by remember { mutableStateOf(link?.url.orEmpty()) }
    var title by remember { mutableStateOf(link?.title.orEmpty()) }
    var notes by remember { mutableStateOf(link?.description.orEmpty()) }
    val listAllTagName = remember(listAllTags) {
        listAllTags.map { it.tag.name }
    }
    var selectedTagName by remember {
        val tagId = tagId ?: link?.tagId
        val currentTagName = listAllTags.firstOrNull { it.tag.id == tagId }?.tag?.name
        mutableStateOf(currentTagName.orEmpty())
    }
    val viewModel = hiltViewModel<LinkModifyViewModel>()
    val result by viewModel.resultMediatorLiveData.observeAsState()
    val (titleDialog, buttonConfirmText) = if (link == null) {
        "Add new link" to "Add"
    } else {
        "Edit link" to "Save"
    }
    val context = LocalContext.current

    LaunchedEffect(result) {
        when (val localResult = result) {
            is ApiResult.Success -> {
                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                onDismiss()
            }

            is ApiResult.Error ->
                Toast.makeText(context, localResult.exception.message, Toast.LENGTH_SHORT).show()

            else -> Unit
        }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.reset() }
    }

    fun createLink() {
        val tagId = listAllTags.firstOrNull { it.tag.name == selectedTagName }?.tag?.id ?: return
        val linkWithoutIconUrl = Link(
            id = UUID.randomUUID().toString(),
            tagId = tagId,
            tagName = selectedTagName,
            url = url,
            title = title,
            iconUrl = null,
            description = notes,
            dateTimeCreated = System.currentTimeMillis()
        )
        viewModel.insertLink(linkWithoutIconUrl)
    }

    fun updateLink() {
        if (link == null) return
        val tagId = listAllTags.firstOrNull { it.tag.name == selectedTagName }?.tag?.id
            ?: return
        val updatedLink = link.copy(
            url = url,
            tagId = tagId,
            title = title,
            description = notes
        )
        viewModel.updateLink(updatedLink)
    }

    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        val dialogWindowProvider = LocalView.current.parent as DialogWindowProvider
        dialogWindowProvider.window.setGravity(Gravity.TOP)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 120.dp, start = 32.dp, end = 32.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                Modifier
                    .background(customColors.surface)
                    .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 20.dp)
            ) {
                Text(
                    titleDialog,
                    style = customTypography.title,
                    color = customColors.text
                )
                MyTextField(
                    value = url,
                    onChange = { url = it },
                    placeholder = {
                        Text(
                            "https://example.com",
                            style = customTypography.body,
                            color = customColors.textPlaceholder
                        )
                    },
                    label = "URL",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
                MyTextField(
                    value = title,
                    onChange = { title = it },
                    placeholder = {
                        Text(
                            "Link title",
                            style = customTypography.body,
                            color = customColors.textPlaceholder
                        )
                    },
                    label = "TITLE",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
                MyTextField(
                    value = notes,
                    onChange = { notes = it },
                    placeholder = {
                        Text(
                            "Brief some notes of the link here",
                            style = customTypography.body,
                            color = customColors.textPlaceholder
                        )
                    },
                    singleLine = false,
                    minLines = 3,
                    label = "NOTES",
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
                    selectedTagName,
                    { selectedTagName = it },
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 44.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(modifier = Modifier.padding(end = 8.dp), onClick = { onDismiss() }) {
                        Text(
                            "Cancel",
                            style = customTypography.buttonText.copy(fontWeight = FontWeight.Normal),
                            color = customColors.primary
                        )
                    }
                    TextButton(onClick = {
                        if (link == null) {
                            createLink()
                        } else {
                            updateLink()
                        }
                    }) {
                        when (result) {
                            is ApiResult.Loading -> CircularProgressIndicator()
                            is ApiResult.Success,
                            is ApiResult.Error,
                            null -> Text(
                                buttonConfirmText,
                                style = customTypography.buttonText,
                                color = customColors.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
