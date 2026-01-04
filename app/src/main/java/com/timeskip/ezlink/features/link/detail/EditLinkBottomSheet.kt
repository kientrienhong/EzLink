package com.timeskip.ezlink.features.link.detail

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.timeskip.ezlink.features.common.ApiResult
import com.timeskip.ezlink.features.common.ImageStorageHelper
import com.timeskip.ezlink.features.common.views.MyInputDropdown
import com.timeskip.ezlink.features.common.views.MyTextField
import com.timeskip.ezlink.features.link.data.Link

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditLinkBottomSheet(
    listTagName: List<String>,
    result: ApiResult<Boolean>?,
    link: Link,
    onDismissRequest: () -> Unit,
    onSubmit: (Link) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val context = LocalContext.current
    LaunchedEffect(result) {
        when (result) {
            is ApiResult.Success -> {
                sheetState.hide()
                onDismissRequest()
            }

            is ApiResult.Error -> Toast.makeText(
                context,
                result.exception.message,
                Toast.LENGTH_LONG
            ).show()

            is ApiResult.Loading,
            null -> Unit
        }
    }

    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        EditLinkBottomSheetContent(
            link,
            listTagName,
            result,
            onDismissRequest,
            onSubmit
        )
    }
}

@Composable
private fun EditLinkBottomSheetContent(
    link: Link,
    listTagName: List<String>,
    result: ApiResult<Boolean>?,
    onDismissRequest: () -> Unit,
    onSubmit: (Link) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var url by remember { mutableStateOf(link.url) }
    var tagName by remember { mutableStateOf(link.tagName) }
    var description by remember { mutableStateOf(link.description) }
    var title by remember { mutableStateOf(link.title) }
    val labelOfUrl = if (ImageStorageHelper.isLocalStoredImage(context, link.url)) {
        "Path"
    } else {
        "Url"
    }

    Column(modifier = modifier.padding(vertical = 16.dp, horizontal = 16.dp)) {
        Text("Add link")
        Spacer(modifier = Modifier.height(12.dp))
        Text(labelOfUrl, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        MyTextField(
            modifier = Modifier.fillMaxWidth(),
            value = url,
            onChange = { url = it },
            enabled = false,
            shape = MaterialTheme.shapes.small
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text("Title", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        MyTextField(
            modifier = Modifier.fillMaxWidth(),
            value = title,
            onChange = { title = it },
            enabled = true,
            minLines = 3,
            maxLines = 6,
            shape = MaterialTheme.shapes.small
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text("Description", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        MyTextField(
            modifier = Modifier.fillMaxWidth(),
            value = description,
            onChange = { description = it },
            enabled = true,
            minLines = 3,
            maxLines = 6,
            shape = MaterialTheme.shapes.small
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text("Tag", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        MyInputDropdown(
            options = listTagName,
            value = tagName,
            onChangeValue = { tagName = it },
            enabled = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                modifier = Modifier.padding(end = 24.dp),
                onClick = { onDismissRequest() }) {
                Text("cancel")
            }
            if (result is ApiResult.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            } else {
                Button(onClick = {
                    val link = link.copy(
                        title = title,
                        url = url,
                        tagName = tagName,
                        description = description
                    )
                    onSubmit(link)
                }) {
                    Text("Submit")
                }
            }
        }
    }
}