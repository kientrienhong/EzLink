package com.timeskip.ezlink.features.tag.view

import android.content.Intent
import android.util.Log
import android.util.Patterns

object ShareIntentUtils {
    fun handleIntent(intent: Intent): String? {
        val intent = intent
        val action = intent.action
        val type = intent.type

        return when {
            // Handle shared text (URL)
            action == Intent.ACTION_SEND && type == "text/plain" -> {
                val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                if (sharedText != null) {
                    val extractedUrl = extractUrlFromText(sharedText)
                    if (extractedUrl != null && isValidUrl(extractedUrl)) {
                        extractedUrl
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
            // Handle shared image
            action == Intent.ACTION_SEND && type?.startsWith("image/") == true -> {
                val imageUri =
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(Intent.EXTRA_STREAM, android.net.Uri::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra<android.net.Uri>(Intent.EXTRA_STREAM)
                    }
                Log.d("MainActivity", "Image shared: $imageUri")
                // For now, we don't process images, but this handler can be extended
                null
            }

            else -> null
        }
    }

    /**
     * Extracts a URL from shared text.
     * Handles cases where text might contain multiple lines or additional content.
     */
    private fun extractUrlFromText(text: String): String? {
        val urlPattern = Patterns.WEB_URL
        val matcher = urlPattern.matcher(text)

        return if (matcher.find()) {
            var url = matcher.group()
            // Ensure URL has a scheme
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://$url"
            }
            url
        } else {
            null
        }
    }

    /**
     * Validates if the URL matches the web URL pattern.
     */
    private fun isValidUrl(url: String): Boolean {
        return Patterns.WEB_URL.matcher(url).matches()
    }
}