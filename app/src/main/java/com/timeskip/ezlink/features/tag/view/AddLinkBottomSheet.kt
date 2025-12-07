package com.timeskip.ezlink.features.tag.view

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
import com.timeskip.ezlink.features.common.views.MyInputDropdown
import com.timeskip.ezlink.features.common.views.MyTextField
import com.timeskip.ezlink.features.link.data.Link

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLinkBottomSheet(
    listTagName: List<String>,
    result: ApiResult<Link>?,
    onDismissRequest: () -> Unit,
    onSubmit: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState()
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
        AddLinkBottomSheetContent(
            listTagName = listTagName,
            result = result,
            onDismissRequest = onDismissRequest,
            onSubmit = onSubmit,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun AddLinkBottomSheetContent(
    listTagName: List<String>,
    result: ApiResult<Link>?,
    onDismissRequest: () -> Unit,
    onSubmit: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var tagName by remember { mutableStateOf("") }

    Column(modifier = modifier.padding(vertical = 16.dp, horizontal = 16.dp)) {
        Text("Add link")
        Spacer(modifier = Modifier.height(8.dp))
        MyTextField(
            modifier = Modifier.fillMaxWidth(),
            value = name,
            onChange = { name = it },
            shape = MaterialTheme.shapes.small
        )
        Spacer(modifier = Modifier.height(8.dp))
        MyInputDropdown(
            options = listTagName,
            value = tagName,
            onChangeValue = { tagName = it },
            modifier = Modifier.fillMaxWidth()
        )
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
                Button(onClick = { onSubmit(name, tagName) }) {
                    Text("Submit")
                }
            }
        }
    }
}