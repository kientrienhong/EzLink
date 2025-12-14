package com.timeskip.ezlink.features.link.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.timeskip.ezlink.R
import com.timeskip.ezlink.features.common.ApiResult
import com.timeskip.ezlink.features.contentHtml.ContentHtml

@Composable
fun DownloadIcon(
    contentHtml: ContentHtml?,
    crawlResult: ApiResult<Unit>?,
    downloadContentResource: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        if (contentHtml != null) {
            if (contentHtml.content.isEmpty()) {
                Icon(
                    painterResource(R.drawable.download),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            downloadContentResource()
                        },
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Icon(
                    painterResource(R.drawable.delete),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onDelete() }
                )
            }
        } else {
            when (crawlResult) {
                null -> Icon(
                    painterResource(R.drawable.download),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            downloadContentResource()
                        },
                    tint = MaterialTheme.colorScheme.primary
                )

                is ApiResult.Success -> Icon(
                    painterResource(R.drawable.delete),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onDelete() }
                )

                is ApiResult.Error -> Text("This link does not support preview")
                is ApiResult.Loading -> CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }
        }
    }
}