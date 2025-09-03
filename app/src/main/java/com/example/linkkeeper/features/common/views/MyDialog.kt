package com.example.linkkeeper.features.common.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.linkkeeper.features.common.DialogOption
import kotlinx.coroutines.launch

@Composable
fun MyDialog(
    title: String,
    dialogOptions: List<DialogOption>,
    onDismissRequest: () -> Unit,
    onResult: (Boolean) -> Unit,
    onError: (Exception) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column {
                dialogOptions.forEach { option ->
                    Text(
                        text = option.title,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                coroutineScope.launch {
                                    try {
                                        val result = option.onClick()
                                        onDismissRequest()
                                        onResult(result)
                                    } catch (e: Exception) {
                                        onError(e)
                                    }
                                }
                            }
                    )
                }
            }
        },
        confirmButton = {}
    )
}