package com.example.linkkeeper.features.common.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.example.linkkeeper.R
import com.example.linkkeeper.ui.theme.LocalCustomColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDropdown(
    options: List<String>,
    value: String,
    onChangeValue: (String) -> Unit,
    modifier: Modifier = Modifier,
    enable: Boolean = true
) {
    val customColors = LocalCustomColors.current
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (!enable) {
                return@ExposedDropdownMenuBox
            }
            expanded = !expanded
        },
        modifier = modifier
    ) {
        MyTextField(
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true),
            onChange = onChangeValue,
            value = value,
            placeholder = { Text(text = "Favorites") },
            trailingIcon = {
                val (resource, contentDescription) = if (expanded) {
                    R.drawable.combobox_arrow_up to "collapse arrow"
                } else {
                    R.drawable.combobox_arrow_down to "expand arrow"
                }
                Icon(
                    painterResource(resource),
                    contentDescription = contentDescription,
                    modifier = Modifier.size(22.dp),
                    tint = customColors.subtext
                )
            },
            enable = enable
        )

        val filteringOptions = options.filter { it.contains(value, ignoreCase = true) }
        if (filteringOptions.isNotEmpty()) {
            DropdownMenu(
                modifier = Modifier
                    .background(customColors.surface)
                    .exposedDropdownSize(true),
                properties = PopupProperties(focusable = false),
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                filteringOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            onChangeValue(selectionOption)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
    }
}