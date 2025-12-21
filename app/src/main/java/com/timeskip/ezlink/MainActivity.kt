package com.timeskip.ezlink

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.timeskip.ezlink.features.common.ImageStorageHelper
import com.timeskip.ezlink.features.common.TagShortcutManager
import com.timeskip.ezlink.ui.theme.LinkKeeperTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen()
        setContent {
            val sharedUrl by viewModel.sharedUrlLiveData.observeAsState()
            val sharedTagName by viewModel.sharedTagNameLiveData.observeAsState()

            LinkKeeperTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainContainer(
                        modifier = Modifier.padding(innerPadding),
                        sharedUrl = sharedUrl,
                        sharedTagName = sharedTagName,
                        onConsumeSharedIntent = viewModel::reset
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        lifecycleScope.launch {
            val (sharedUrl, sharedTagName) = handleIntent(intent)
            viewModel.setSharedUrl(sharedUrl)
            viewModel.setSharedTagName(sharedTagName)

        }
    }

    private suspend fun handleIntent(intent: Intent): Pair<String?, String?> {
        return when (intent.action) {
            TagShortcutManager.ACTION_SHORTCUT -> handleDirectShareIntent(intent)
            Intent.ACTION_SEND -> handleShareIntent(intent)
            else -> null to null
        }
    }

    private fun handleDirectShareIntent(intent: Intent): Pair<String?, String?> {
        val tagName = intent.getStringExtra(TagShortcutManager.KEY_TAG_NAME)
        return null to tagName
    }

    private suspend fun handleShareIntent(intent: Intent): Pair<String?, String?> {
        val type = intent.type
        val tagName = intent.getStringExtra("android.intent.extra.shortcut.ID")

        return when {
            type == "text/plain" -> {
                val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                sharedText to tagName
            }

            type?.startsWith("image/") == true -> {
                @Suppress("DEPRECATION")
                val imageUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
                if (imageUri != null) {
                    val cachedImageUri =
                        ImageStorageHelper.compressAndStoreImageInCache(this, imageUri)
                    cachedImageUri to tagName
                } else {
                    null to tagName
                }
            }

            else -> null to tagName
        }
    }
}
