package com.timeskip.ezlink.features.link.list

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timeskip.ezlink.features.common.ApiResult
import com.timeskip.ezlink.features.common.debounce
import com.timeskip.ezlink.features.common.runBlocking
import com.timeskip.ezlink.features.link.LinkRetryStep
import com.timeskip.ezlink.features.link.LinkUrlHelper
import com.timeskip.ezlink.features.link.data.Link
import com.timeskip.ezlink.features.link.data.LinkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.HttpStatusException
import java.lang.Thread.sleep
import javax.inject.Inject
import kotlin.uuid.ExperimentalUuidApi

@HiltViewModel
class LinkScreenViewModel @Inject constructor(
    private val repository: LinkRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val tagName: String? = savedStateHandle["tagName"]

    private val searchMutableLiveData: MutableLiveData<String> = MutableLiveData("")
    val searchLiveData: LiveData<String> = searchMutableLiveData

    private val tagRetrievingResultMutableLiveData: MutableLiveData<ApiResult<String>> =
        MutableLiveData()
    val tagRetrievingResultLiveData: LiveData<ApiResult<String>> =
        tagRetrievingResultMutableLiveData

    private val linkValidationMutableLiveData: MutableLiveData<ApiResult<Link>?> =
        MutableLiveData()

    private val listLinkMediatorLiveData: MutableLiveData<List<Link>> = MediatorLiveData()
    val linkListLiveData: LiveData<List<Link>> = listLinkMediatorLiveData

    private val localLinkListLiveData: LiveData<List<Link>> =
        repository.getLinkListLiveData(tagName.orEmpty())
    private val deleteLinkMutableLiveData: MutableLiveData<ApiResult<Boolean>> = MutableLiveData()

    val deleteLinkLiveData: LiveData<ApiResult<Boolean>> = deleteLinkMutableLiveData

    private val createLinkMutableLiveData: MediatorLiveData<ApiResult<Boolean>?> =
        MediatorLiveData()
    val createLinkLiveData: LiveData<ApiResult<Boolean>?> = createLinkMutableLiveData

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
                    repository.getAllLink(tagName.orEmpty())
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
        createLinkMutableLiveData.addSource(linkValidationMutableLiveData) { apiResult ->
            when (apiResult) {
                null -> createLinkMutableLiveData.value = null
                is ApiResult.Loading -> createLinkMutableLiveData.value = ApiResult.Loading()
                is ApiResult.Error -> createLinkMutableLiveData.value =
                    ApiResult.Error(apiResult.exception)

                is ApiResult.Success -> {
                    val link = apiResult.data
                    viewModelScope.launch {
                        createLinkMutableLiveData.value = ApiResult.Loading()
                        val result = runBlocking(
                            onBlocking = { repository.insertLink(link) },
                            onSuccess = {
                                if (it) {
                                    ApiResult.Success(it)
                                } else {
                                    ApiResult.Error(Exception("Failed to insert link"))
                                }
                            },
                            onError = { ApiResult.Error(it) }
                        )
                        createLinkMutableLiveData.value = result
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun validateUrlThenForwardCreatingLink(
        url: String,
        delayMillis: Long = 200,
        retryStep: LinkRetryStep? = LinkRetryStep.getInitialStep()
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (linkValidationMutableLiveData.value is ApiResult.Loading &&
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
                            return@launch validateUrlThenForwardCreatingLink(
                                url,
                                delayMillis,
                                retryStep.nextStep
                            )
                        } else {
                            ""
                        }
                    } catch (ex: IllegalArgumentException) {
                        throw ex
                    } catch (_: Exception) {
                        ""
                    }

                    val link = Link(
                        url = linkRetryStepToUrl,
                        tagName = tagName.orEmpty(),
                        iconUrl = iconUrl,
                        title = title,
                        description = ""
                    )
                    ApiResult.Success(link)
                } else {
                    ApiResult.Error(IllegalArgumentException("Invalid URL"))
                }
            } catch (e: IllegalArgumentException) {
                Log.e("LinkScreenViewModel", "Error validating URL: ${e.message}")
                if (!retryStep.isLast()) {
                    return@launch validateUrlThenForwardCreatingLink(
                        url,
                        delayMillis,
                        retryStep.nextStep
                    )
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
                onBlocking = { repository.getTag(tagName.orEmpty()) },
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

    fun deleteLink(link: Link) {
        viewModelScope.launch {
            if (deleteLinkLiveData.value is ApiResult.Loading) {
                return@launch
            }

            deleteLinkMutableLiveData.value = ApiResult.Loading()

            val result = runBlocking(
                onBlocking = { repository.deleteLink(link) },
                onSuccess = { ApiResult.Success(it) },
                onError = { ApiResult.Error(it) }
            )

            deleteLinkMutableLiveData.value = result
        }
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