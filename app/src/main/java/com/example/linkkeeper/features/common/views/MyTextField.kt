package com.example.linkkeeper.features.common.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.linkkeeper.ui.theme.LocalCustomColors
import com.example.linkkeeper.ui.theme.LocalCustomTypography

@Composable
fun MyTextField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    containerColor: Color? = null,
    minLines: Int = 1,
    enable: Boolean = true
) {
    val customColors = LocalCustomColors.current
    val containerColor = containerColor ?: customColors.background
    val customTypography = LocalCustomTypography.current

    Column(modifier.fillMaxWidth()) {
        if (label != null) {
            Text(
                text = label,
                style = customTypography.overline,
                color = customColors.subtext,
                modifier = Modifier.padding(start = 16.dp, bottom = 7.dp)
            )
        }
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = onChange,
            placeholder = placeholder,
            trailingIcon = trailingIcon,
            singleLine = singleLine,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = containerColor,
                unfocusedContainerColor = containerColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(10.dp),
            textStyle = customTypography.body.copy(color = customColors.text),
            minLines = minLines,
            enabled = enable
        )
    }
}