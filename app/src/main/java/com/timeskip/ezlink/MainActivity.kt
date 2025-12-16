package com.timeskip.ezlink

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.timeskip.ezlink.features.common.TagShortcutManager
import com.timeskip.ezlink.ui.theme.LinkKeeperTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen()
        setContent {
            LinkKeeperTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var sharedUrl by remember { mutableStateOf<String?>(null) }
                    var sharedTagName by remember { mutableStateOf<String?>(null) }

                    LifecycleResumeEffect(intent) {
                        val (url, tagName) = handleIntent(intent)
                        sharedUrl = url
                        sharedTagName = tagName
                        Log.d("MainActivity", "Handled intent: intent $intent")

                        Log.d("MainActivity", "Handled intent: url=$url, tagName=$tagName")
                        onPauseOrDispose {
                        }
                    }

                    MainContainer(sharedTagName, sharedUrl, Modifier.padding(innerPadding))
                }
            }
        }
    }

    private fun handleIntent(intent: Intent): Pair<String?, String?> {
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

    private fun handleShareIntent(intent: Intent): Pair<String?, String?> {
        val url = intent.getStringExtra(Intent.EXTRA_TEXT)
        val tagName = intent.getStringExtra("android.intent.extra.shortcut.ID")
        return url to tagName
    }
}
