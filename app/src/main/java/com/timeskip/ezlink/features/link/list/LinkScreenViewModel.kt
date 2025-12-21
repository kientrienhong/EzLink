package com.timeskip.ezlink.features.link.list

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timeskip.ezlink.features.common.ApiResult
import com.timeskip.ezlink.features.common.LinkUrlHelper
import com.timeskip.ezlink.features.common.UrlValidateUtils
import com.timeskip.ezlink.features.common.debounce
import com.timeskip.ezlink.features.common.runBlocking
import com.timeskip.ezlink.features.link.data.Link
import com.timeskip.ezlink.features.link.data.LinkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import kotlin.uuid.ExperimentalUuidApi

@HiltViewModel
class LinkScreenViewModel @Inject constructor(
    private val repository: LinkRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val tagName: String? = savedStateHandle["tagName"]

    private val searchMutableLiveData: MutableLiveData<String> = MutableLiveData("")
    val searchLiveData: LiveData<String> = searchMutableLiveData

    private val createLinkMutableLiveData: MutableLiveData<ApiResult<Link>?> = MutableLiveData()
    val createLinkLiveData: LiveData<ApiResult<Link>?> = createLinkMutableLiveData

    private val listLinkMediatorLiveData: MutableLiveData<List<Link>> = MediatorLiveData()
    val linkListLiveData: LiveData<List<Link>> = listLinkMediatorLiveData

    private val deleteLinkMutableLiveData: MutableLiveData<ApiResult<Boolean>> = MutableLiveData()
    val deleteLinkLiveData: LiveData<ApiResult<Boolean>> = deleteLinkMutableLiveData

    private var currentOffset = 0
    private val pageSize = 15
    private var isLoadingPage = false
    private var hasMorePages = true

    private val isLoadingMoreMutableLiveData = MutableLiveData(false)
    val isLoadingMore: LiveData<Boolean> = isLoadingMoreMutableLiveData

    private val hasMoreItemsMutableLiveData = MutableLiveData(true)
    val hasMoreItems: LiveData<Boolean> = hasMoreItemsMutableLiveData

    private val searchObserver: Observer<String> = Observer { query ->
        debounceSearch(query)
    }

    private val debounceSearch = debounce<String>(
        waitMs = 300L,
        coroutineScope = viewModelScope
    ) { search ->
        viewModelScope.launch {
            currentOffset = 0
            hasMorePages = true
            val result = withContext(Dispatchers.IO) {
                if (search.isBlank()) {
                    loadNextPageInternal(reset = true)
                } else {
                    loadNextPageInternal(searchQuery = search, reset = true)
                }
            }
            listLinkMediatorLiveData.value = result
        }
    }

    init {
        searchLiveData.observeForever(searchObserver)
        viewModelScope.launch {
            val links = withContext(Dispatchers.IO) {
                loadNextPageInternal(reset = true)
            }
            listLinkMediatorLiveData.value = links
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun validateUrlThenCreatingLink(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (createLinkMutableLiveData.value is ApiResult.Loading) {
                return@launch
            }
            createLinkMutableLiveData.postValue(ApiResult.Loading())
            val isWebUrl = Patterns.WEB_URL.matcher(url).matches()
            val isFilePath = File(url).exists()
            val validationResult = try {
                when {
                    isWebUrl -> UrlValidateUtils.validateUrl(tagName.orEmpty(), url)
                    isFilePath -> UrlValidateUtils.validateUri(tagName.orEmpty(), url)
                    else -> throw IllegalArgumentException("Invalid Url")
                }
            } catch (e: IllegalArgumentException) {
                val result = ApiResult.Error<Link>(e)
                createLinkMutableLiveData.postValue(result)
                return@launch
            }


            val result = when (validationResult) {
                is ApiResult.Success -> {
                    val link = if(isWebUrl) {
                        val domain = LinkUrlHelper.getDomain(url)
                        validationResult.data.copy(iconUrl = getIconUrl(domain))
                    } else {
                        validationResult.data
                    }
                    val resultInsert = repository.insertLink(link)
                    if (resultInsert) {
                        ApiResult.Success(link)
                    } else {
                        ApiResult.Error(Exception("Failed to insert link"))
                    }
                }
                is ApiResult.Error -> ApiResult.Error(validationResult.exception)
                is ApiResult.Loading -> ApiResult.Loading()
            }

            createLinkMutableLiveData.postValue(result)
        }
    }

    private suspend fun loadNextPageInternal(
        searchQuery: String? = null,
        reset: Boolean = false
    ): List<Link> {
        if (isLoadingPage) return listLinkMediatorLiveData.value.orEmpty()
        if (!hasMorePages && !reset) return listLinkMediatorLiveData.value.orEmpty()

        isLoadingPage = true
        isLoadingMoreMutableLiveData.postValue(true)
        if (reset) {
            currentOffset = 0
            hasMorePages = true
            hasMoreItemsMutableLiveData.postValue(true)
        }

        val links = if (searchQuery.isNullOrBlank()) {
            repository.getLinks(tagName.orEmpty(), currentOffset, pageSize)
        } else {
            val query = sanitizeSearchQuery(searchQuery)
            repository.searchPaged(query, currentOffset, pageSize)
        }

        val currentList = if (reset) emptyList() else listLinkMediatorLiveData.value.orEmpty()
        val newList = currentList + links

        hasMorePages = links.size >= pageSize
        hasMoreItemsMutableLiveData.postValue(hasMorePages)
        if (hasMorePages) {
            currentOffset += links.size
        }

        isLoadingPage = false
        isLoadingMoreMutableLiveData.postValue(false)
        return newList
    }

    fun loadNextPage() {
        viewModelScope.launch {
            val search = searchLiveData.value
            val newList = withContext(Dispatchers.IO) {
                loadNextPageInternal(searchQuery = search, reset = false)
            }
            listLinkMediatorLiveData.value = newList
        }
    }

    fun updateSearch(search: String) {
        searchMutableLiveData.value = search
    }

    fun resetLinkValidationLiveData() {
        createLinkMutableLiveData.value = null
    }

    fun deleteLink(context: Context, link: Link) {
        viewModelScope.launch {
            if (deleteLinkLiveData.value is ApiResult.Loading) {
                return@launch
            }

            deleteLinkMutableLiveData.value = ApiResult.Loading()

            val result = runBlocking(
                onBlocking = { repository.deleteLink(context, link) },
                onSuccess = { ApiResult.Success(it) },
                onError = { ApiResult.Error(it) }
            )

            deleteLinkMutableLiveData.value = result
        }
    }

    private fun sanitizeSearchQuery(query: String?): String {
        if (query == null) {
            return ""
        }
        val queryWithEscapedQuotes = query.replace(Regex.fromLiteral("\""), "\"\"")
        return "*\"$queryWithEscapedQuotes\"*"
    }

    private fun getIconUrl(domain: String): String = "https://logo.clearbit.com/$domain"
}