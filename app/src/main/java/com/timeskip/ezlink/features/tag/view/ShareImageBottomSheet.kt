package com.timeskip.ezlink.features.tag.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.timeskip.ezlink.R
import com.timeskip.ezlink.features.common.ApiResult
import com.timeskip.ezlink.features.common.views.MyInputDropdown

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun <T> ShareImageBottomSheet(
    imageUrl: String,
    listTagName: List<String>,
    result: ApiResult<T>?,
    tagName: String,
    onDismissRequest: () -> Unit,
    onSubmit: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    dropDownEnabled: Boolean = true
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    LaunchedEffect(result) {
        when (result) {
            is ApiResult.Success -> {
                sheetState.hide()
                onDismissRequest()
            }

            is ApiResult.Error,
            is ApiResult.Loading,
            null -> Unit
        }
    }

    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        ShareImageBottomSheetContent(
            imageUrl = imageUrl,
            listTagName = listTagName,
            result = result,
            tagName = tagName,
            dropDownEnabled = dropDownEnabled,
            onDismissRequest = onDismissRequest,
            onSubmit = onSubmit,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun <T> ShareImageBottomSheetContent(
    imageUrl: String,
    listTagName: List<String>,
    result: ApiResult<T>?,
    tagName: String,
    dropDownEnabled: Boolean,
    onDismissRequest: () -> Unit,
    onSubmit: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var tagName by remember { mutableStateOf(tagName) }

    Column(modifier = modifier.padding(vertical = 16.dp, horizontal = 16.dp)) {
        Text("Share image")
        Spacer(modifier = Modifier.height(12.dp))

        // Image preview container
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(MaterialTheme.colorScheme.surface),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            GlideImage(
                model = imageUrl,
                contentDescription = "Shared image",
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentScale = ContentScale.Fit,
                loading = placeholder(R.drawable.picture),
                failure = placeholder(R.drawable.picture)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tag dropdown
        MyInputDropdown(
            options = listTagName,
            value = tagName,
            onChangeValue = { tagName = it },
            enabled = dropDownEnabled,
            modifier = Modifier.fillMaxWidth()
        )

        // Action buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(modifier = Modifier.padding(end = 24.dp), onClick = { onDismissRequest() }) {
                Text("cancel")
            }
            if (result is ApiResult.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            } else {
                Button(onClick = { onSubmit(imageUrl, tagName) }) {
                    Text("Submit")
                }
            }
        }
    }
}

