package com.timeskip.ezlink.features.common.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TransparentTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    textStyle: TextStyle = LocalTextStyle.current,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorText: String = ""
) {
    Column {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            textStyle = textStyle.copy(
                color = if (isError) MaterialTheme.colorScheme.error else textStyle.color
            ),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            enabled = enabled,
            cursorBrush = SolidColor(
                if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            ),
            decorationBox = { innerTextField ->
                Box {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = textStyle,
                            color = if (isError)
                                MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                    innerTextField()
                }
            }
        )
        if (isError && errorText.isNotEmpty()) {
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
            )
        }
    }
}

@Preview
@Composable
private fun PreviewTransparentTextField() {
    TransparentTextField(
        value = "Test\nTest\nTest\nTest",
        onValueChange = {},
        placeholder = "Enter text here",
        modifier = Modifier.fillMaxWidth(),
        singleLine = false,
    )
}

@Preview
@Composable
private fun PreviewTransparentTextFieldError() {
    TransparentTextField(
        value = "",
        onValueChange = {},
        placeholder = "Enter text here",
        modifier = Modifier.fillMaxWidth(),
        singleLine = false,
        isError = true,
        errorText = "This is an error message"
    )
}