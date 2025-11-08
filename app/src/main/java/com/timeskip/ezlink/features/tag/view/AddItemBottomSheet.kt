package com.timeskip.ezlink.features.tag.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.timeskip.ezlink.features.common.ApiResult
import com.timeskip.ezlink.ui.theme.LinkKeeperTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> AddItemBottomSheet(
    title: String,
    stateCreate: ApiResult<T>?,
    onDismissRequest: () -> Unit,
    onSubmitWithEditTextValue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(stateCreate) {
        when (stateCreate) {
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
        TagAddBottomSheetContent(
            title,
            stateCreate,
            onDismissRequest = onDismissRequest,
            onSubmitWithEditTextValue = onSubmitWithEditTextValue
        )
    }
}

@Composable
private fun <T> TagAddBottomSheetContent(
    title: String,
    createState: ApiResult<T>?,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onSubmitWithEditTextValue: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    Column(modifier = modifier.padding(vertical = 16.dp, horizontal = 16.dp)) {
        Text(title)
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = name,
            onValueChange = { name = it },
            label = {}
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
            if (createState is ApiResult.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            } else {
                Button(onClick = { onSubmitWithEditTextValue(name) }) {
                    Text("Submit")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TagAddBottomSheetLoadingStatePreview() {
    LinkKeeperTheme {
        TagAddBottomSheetContent(
            title = "Add tag",
            createState = ApiResult.Loading<Unit>(),
            onDismissRequest = {},
            onSubmitWithEditTextValue = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun TagAddBottomSheetInitialStatePreview() {
    LinkKeeperTheme {
        TagAddBottomSheetContent<Unit>(
            title = "Add tag",
            createState = null,
            onDismissRequest = {},
            onSubmitWithEditTextValue = {})
    }
}