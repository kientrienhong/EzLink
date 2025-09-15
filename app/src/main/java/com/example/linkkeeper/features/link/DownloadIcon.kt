package com.example.linkkeeper.features.link

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.linkkeeper.R
import com.example.linkkeeper.features.common.ApiResult

@Composable
fun <T> DownloadIcon(
    apiResult: ApiResult<T>,
    onDownloadResourceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier.clickable { onDownloadResourceClick() }) {
        Image(
            painterResource(R.drawable.download),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
        val alignBadgeModifier = Modifier.align(Alignment.BottomEnd)
        when (apiResult) {
            is ApiResult.Success -> Image(
                painterResource(R.drawable.check),
                contentDescription = null,
                alignBadgeModifier
            )
            is ApiResult.Loading -> CircularProgressIndicator(alignBadgeModifier)
            is ApiResult.Error -> Image(
                painterResource(R.drawable.mark),
                contentDescription = null,
                alignBadgeModifier
            )
        }
    }
}