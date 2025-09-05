package com.example.linkkeeper.features.link.list

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.linkkeeper.features.common.ApiResult
import com.example.linkkeeper.features.common.debounce
import com.example.linkkeeper.features.common.runBlocking
import com.example.linkkeeper.features.link.LinkRetryStep
import com.example.linkkeeper.features.link.LinkUrlHelper
import com.example.linkkeeper.features.link.data.Link
import com.example.linkkeeper.features.link.data.LinkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.HttpStatusException
import java.lang.Thread.sleep
import javax.inject.Inject

@HiltViewModel
class LinkScreenViewModel @Inject constructor(
    private val repository: LinkRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val tagId: Int = savedStateHandle["tagId"] ?: 0
    private val tagName: String? = savedStateHandle["tagName"]

    private val searchMutableLiveData: MutableLiveData<String> = MutableLiveData("")
    val searchLiveData: LiveData<String> = searchMutableLiveData

    private val tagRetrievingResultMutableLiveData: MutableLiveData<ApiResult<String>> =
        MutableLiveData()
    val tagRetrievingResultLiveData: LiveData<ApiResult<String>> =
        tagRetrievingResultMutableLiveData

    private val linkValidationMutableLiveData: MutableLiveData<ApiResult<Link>?> =
        MutableLiveData()
    val linkValidationLiveData: LiveData<ApiResult<Link>?> = linkValidationMutableLiveData

    private val listLinkMediatorLiveData: MutableLiveData<List<Link>> = MediatorLiveData()
    val linkListLiveData: LiveData<List<Link>> = listLinkMediatorLiveData

    private val localLinkListLiveData: LiveData<List<Link>> = repository.getLinkListLiveData(tagId)

    private val linkListObserver: Observer<List<Link>> = Observer {
        viewModelScope.launch {
            val search = searchLiveData.value
            val result = withContext(Dispatchers.IO) {
                if (search.isNullOrBlank()) {
                    it
                } else {
                    searchLink(search)
                }
            }
            listLinkMediatorLiveData.value = result
        }
    }
    private val searchObserver: Observer<String> = Observer {
        debounceSearch(it)
    }
    private val debounceSearch = debounce<String>(
        waitMs = 300L,
        coroutineScope = viewModelScope
    ) { search ->
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                if (search.isBlank()) {
                    repository.getAllLink(tagId)
                } else {
                    searchLink(search)
                }
            }
            listLinkMediatorLiveData.value = result
        }
    }

    init {
        searchLiveData.observeForever(searchObserver)
        localLinkListLiveData.observeForever(linkListObserver)
    }

    fun validateUrl(
        tagId: Int,
        url: String,
        delayMillis: Long = 500,
        retryStep: LinkRetryStep? = LinkRetryStep.getInitialStep()
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (linkValidationLiveData.value is ApiResult.Loading &&
                retryStep == LinkRetryStep.getInitialStep()
            ) {
                return@launch
            }
            if (retryStep == null) {
                linkValidationMutableLiveData.postValue(ApiResult.Error(IllegalArgumentException("Wrong URL format")))
                return@launch
            }

            val linkRetryStepToUrl = LinkRetryStep.getCurrentUrlFormat(url, retryStep)
            linkValidationMutableLiveData.postValue(ApiResult.Loading())
            val result = try {
                val isValid = Patterns.WEB_URL.matcher(linkRetryStepToUrl).matches()
                if (isValid) {
                    val domain = LinkUrlHelper.getDomain(linkRetryStepToUrl)
                    val iconUrl = getIconUrl(domain)

                    val title = try {
                        LinkUrlHelper.getTitle(linkRetryStepToUrl)
                    } catch (e: HttpStatusException) {
                        if (e.statusCode == 429 && !retryStep.isLast()) {
                            println("429 error for: $url. Waiting for $delayMillis ms before retrying.")
                            sleep(delayMillis)
                            return@launch validateUrl(tagId, url, delayMillis)
                        } else {
                            ""
                        }
                    } catch (ex: IllegalArgumentException) {
                        throw ex
                    } catch (_: Exception) {
                        ""
                    }

                    val crawlData = LinkUrlHelper.crawlData(linkRetryStepToUrl)
                    val link = Link(
                        url = linkRetryStepToUrl,
                        tagId = tagId,
                        iconUrl = iconUrl,
                        title = title,
                        description = "",
                        contentHtml = crawlData.orEmpty()
                    )
                    ApiResult.Success(link)
                } else {
                    ApiResult.Error(IllegalArgumentException("Invalid URL"))
                }
            } catch (e: IllegalArgumentException) {
                Log.e("LinkScreenViewModel", "Error validating URL: ${e.message}", e)
                if (!retryStep.isLast()) {
                    return@launch validateUrl(tagId, url, delayMillis, retryStep.nextStep)
                }
                ApiResult.Error(IllegalArgumentException("Wrong URL format"))
            }
            linkValidationMutableLiveData.postValue(result)
        }
    }

    fun getTagName() {
        if (tagName != null) {
            tagRetrievingResultMutableLiveData.value = ApiResult.Success(tagName)
            return
        }

        viewModelScope.launch {
            if (tagRetrievingResultMutableLiveData.value is ApiResult.Loading) {
                return@launch
            }
            tagRetrievingResultMutableLiveData.value = ApiResult.Loading()
            val result = runBlocking(
                onBlocking = { repository.getTag(tagId) },
                onSuccess = { ApiResult.Success(it.name) },
                onError = { ApiResult.Error(it) }
            )

            tagRetrievingResultMutableLiveData.value = result
        }
    }

    private suspend fun searchLink(query: String): List<Link> {
        val searchQuery = sanitizeSearchQuery(query)
        return repository.search(searchQuery)
    }

    fun updateSearch(search: String) {
        searchMutableLiveData.value = search
    }

    fun resetLinkValidationLiveData() {
        linkValidationMutableLiveData.value = null
    }

    private fun sanitizeSearchQuery(query: String?): String {
        if (query == null) {
            return "";
        }
        val queryWithEscapedQuotes = query.replace(Regex.fromLiteral("\""), "\"\"")
        return "*\"$queryWithEscapedQuotes\"*"
    }

    private fun getIconUrl(domain: String): String = "https://logo.clearbit.com/$domain"
}