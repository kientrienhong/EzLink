package com.timeskip.ezlink.features.common

import android.util.Log
import android.util.Patterns
import com.timeskip.ezlink.features.link.LinkRetryStep
import com.timeskip.ezlink.features.link.data.Link
import org.jsoup.HttpStatusException
import java.io.File
import java.io.IOException
import java.lang.Thread.sleep

object UrlValidateUtils {
    fun isValidFileUri(uri: String): Boolean {
        return try {
            val filePath = if (uri.startsWith("file://")) {
                uri.removePrefix("file://")
            } else {
                uri
            }
            File(filePath).exists()
        } catch (e: Exception) {
            false
        }
    }

    fun validateUri(
        tagName: String,
        uri: String,
    ): ApiResult<Link> = try {
        Log.d("UrlValidateUtils", "Validating file URI: $uri")
        val file = File(uri)
        if (!file.exists() || !file.isFile) {
            throw IllegalArgumentException("Cannot find file at the given URI")
        }

        if (!isImageFile(uri)) {
            throw IllegalArgumentException("We only support image files")
        }

        val link = Link(
            url = uri,
            tagName = tagName,
            iconUrl = uri,
            title = "",
            description = ""
        )
        ApiResult.Success(link)

    } catch (_: IOException) {
        ApiResult.Error(IllegalArgumentException("Cannot find file at the given URI"))
    } catch (illegalArgEx: IllegalArgumentException) {
        ApiResult.Error(illegalArgEx)
    } catch (e: Exception) {
        ApiResult.Error(IllegalArgumentException("Invalid URI: ${e.message}"))
    }

    private fun isImageFile(uri: String): Boolean {
        val imageExtensions = setOf("jpg", "jpeg", "png", "webp", "bmp")
        val extension = uri.substringAfterLast(".").lowercase()
        return extension in imageExtensions
    }

    suspend fun validateUrl(
        tagName: String,
        url: String,
        delayMillis: Long = 200,
        retryStep: LinkRetryStep? = LinkRetryStep.getInitialStep()
    ): ApiResult<Link> {
        if (retryStep == null) {
            return ApiResult.Error(IllegalArgumentException("Invalid URL"))
        }

        val linkRetryStepToUrl = LinkRetryStep.getCurrentUrlFormat(url, retryStep)
        return validateAndCreateLink(tagName, linkRetryStepToUrl, url, delayMillis, retryStep)
    }

    private suspend fun validateAndCreateLink(
        tagName: String,
        urlToValidate: String,
        originalUrl: String,
        delayMillis: Long,
        retryStep: LinkRetryStep?
    ): ApiResult<Link> {
        return try {
            if (!isValidUrl(urlToValidate)) {
                return ApiResult.Error(IllegalArgumentException("Invalid URL"))
            }

            val domain = LinkUrlHelper.getDomain(urlToValidate)
            val iconUrl = getIconUrl(domain)
            val title =
                fetchTitleWithRetry(tagName, urlToValidate, originalUrl, delayMillis, retryStep)

            val link = Link(
                url = urlToValidate,
                tagName = tagName,
                iconUrl = iconUrl,
                title = title,
                description = ""
            )
            ApiResult.Success(link)
        } catch (e: IllegalArgumentException) {
            Log.e("LinkScreenViewModel", "Error validating URL: ${e.message}")
            if (retryStep?.isLast() == false) {
                Log.d("LinkScreenViewModel", "Retrying with next URL format")
                validateUrl(tagName, originalUrl, delayMillis, retryStep.nextStep)
            } else {
                ApiResult.Error(IllegalArgumentException("Wrong URL format"))
            }
        }
    }

    private suspend fun fetchTitleWithRetry(
        tagName: String,
        url: String,
        originalUrl: String,
        delayMillis: Long,
        retryStep: LinkRetryStep?
    ): String = try {
        LinkUrlHelper.getTitle(url)
    } catch (e: HttpStatusException) {
        handleHttpStatusException(e, tagName, originalUrl, delayMillis, retryStep)
    } catch (ex: IllegalArgumentException) {
        throw ex
    } catch (_: Exception) {
        ""
    }

    private suspend fun handleHttpStatusException(
        exception: HttpStatusException,
        tagName: String,
        originalUrl: String,
        delayMillis: Long,
        retryStep: LinkRetryStep?
    ): String {
        if (exception.statusCode == 429 && retryStep?.isLast() == false) {
            Log.d("LinkScreenViewModel", "Rate limited (429). Retrying after ${delayMillis}ms")
            sleep(delayMillis)
            validateUrl(tagName, originalUrl, delayMillis, retryStep.nextStep)
        }
        return ""
    }

    private fun isValidUrl(url: String): Boolean = Patterns.WEB_URL.matcher(url).matches()

    private fun getIconUrl(domain: String): String = "https://logo.clearbit.com/$domain"

}