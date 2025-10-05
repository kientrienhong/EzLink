package com.example.linkkeeper.features.home.view

import android.view.Gravity
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.example.linkkeeper.features.common.ApiResult
import com.example.linkkeeper.features.common.views.MyTextField
import com.example.linkkeeper.ui.theme.LocalCustomColors
import com.example.linkkeeper.ui.theme.LocalCustomTypography

@Composable
internal fun AddTagDialog(
    addTagResult: ApiResult<Boolean>?,
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit = {}
) {
    val customColors = LocalCustomColors.current
    val customTypography = LocalCustomTypography.current
    var tagText by remember { mutableStateOf("") }

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
                    "Add tag",
                    style = customTypography.title,
                    color = customColors.text
                )
                MyTextField(
                    value = tagText,
                    onChange = { tagText = it },
                    placeholder = {
                        Text(
                            "Place your tag here...",
                            style = customTypography.body,
                            color = customColors.subtext
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
                if (addTagResult is ApiResult.Error) {
                    Text(
                        text = addTagResult.exception.message ?: "An error occurred",
                        style = customTypography.body,
                        textAlign = TextAlign.Center,
                        color = customColors.error,
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                    )
                }
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
                    TextButton(onClick = { onCreate(tagText) }) {
                        when (addTagResult) {
                            is ApiResult.Loading -> CircularProgressIndicator()
                            is ApiResult.Success,
                            is ApiResult.Error,
                            null -> Text(
                                "Add",
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

@Preview
@Composable
fun AddTagDialogPreview() {
    AddTagDialog(null, onDismiss = {})
}