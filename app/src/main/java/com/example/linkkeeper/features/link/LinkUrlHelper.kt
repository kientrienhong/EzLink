package com.example.linkkeeper.features.link

import org.jsoup.Jsoup
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URI
import java.net.URL


object LinkUrlHelper {
    fun getDomain(url: String): String {
        val uri = URI(url)
        val domain = uri.host ?: getHostWithUrlWithoutProtocol(url)
        return if (domain.startsWith("www.")) {
            domain.substring(4)
        } else {
            domain
        }
    }

    fun getTitle(url: String): String = Jsoup.connect(url).get().title()

    fun crawlData(urlAddress: String): String? = try {
        val sb = StringBuilder()
        val url = URL(urlAddress)
        BufferedReader(InputStreamReader(url.openStream())).use { it ->
            var inputLine: String?
            while ((it.readLine().also { inputLine = it }) != null) {
                sb.append(inputLine)
            }
        }
        sb.toString()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    private fun getHostWithUrlWithoutProtocol(url: String): String {
        val indexOfTheEndOfDomain = url.indexOf("/").takeIf { it != -1 } ?: return url
        return url.substring(0, indexOfTheEndOfDomain)
    }
}