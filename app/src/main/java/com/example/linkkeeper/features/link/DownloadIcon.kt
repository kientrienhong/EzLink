package com.example.linkkeeper.features.link

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.linkkeeper.R
import com.example.linkkeeper.features.common.ApiResult

@Composable
fun <T> DownloadIcon(
    apiResult: ApiResult<T>?,
    onDownloadResourceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier.size(28.dp).clickable { onDownloadResourceClick() }) {
        Image(
            painterResource(R.drawable.download),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        val alignBadgeModifier = Modifier.align(Alignment.BottomEnd)
        when (apiResult) {
            null,
            is ApiResult.Success,
            is ApiResult.Error -> Unit
            is ApiResult.Loading -> CircularProgressIndicator(alignBadgeModifier)
        }
    }
}