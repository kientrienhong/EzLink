package com.timeskip.ezlink.features.link.list

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.timeskip.ezlink.features.common.ApiResult
import com.timeskip.ezlink.features.common.LinkUrlHelper
import com.timeskip.ezlink.features.common.UrlValidateUtils
import com.timeskip.ezlink.features.common.runBlocking
import com.timeskip.ezlink.features.link.data.Link
import com.timeskip.ezlink.features.link.data.LinkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    // Current limit for pagination (increases as user loads more)
    private val currentLimitMutableLiveData = MutableLiveData(pageSize)

    // Unified LiveData that switches based on search query and limit
    private val unifiedLinksLiveData: LiveData<List<Link>> = currentLimitMutableLiveData.switchMap { limit ->
        searchLiveData.switchMap { searchQuery ->
            val sanitizedQuery = if (searchQuery.isBlank()) "" else sanitizeSearchQuery(searchQuery)
            repository.getLinksLiveData(tagName.orEmpty(), sanitizedQuery, limit)
        }
    }

    private val listLinkMediatorLiveData: MediatorLiveData<List<Link>> = MediatorLiveData()
    val linkListLiveData: LiveData<List<Link>> = listLinkMediatorLiveData

    init {
        // Observe the unified LiveData
        listLinkMediatorLiveData.addSource(unifiedLinksLiveData) { links ->
            listLinkMediatorLiveData.value = links
            hasMorePages = links.size >= (currentLimitMutableLiveData.value ?: pageSize)
            hasMoreItemsMutableLiveData.value = hasMorePages
            isLoadingMoreMutableLiveData.value = false
        }

        // Auto-refresh when delete succeeds
        listLinkMediatorLiveData.addSource(deleteLinkLiveData) {
            if (deleteLinkLiveData.value is ApiResult.Success) {
                refreshList()
            }
        }

        // Auto-refresh when create succeeds
        listLinkMediatorLiveData.addSource(createLinkLiveData) {
            if (createLinkLiveData.value is ApiResult.Success) {
                refreshList()
            }
        }
    }

    fun refreshList() {
        // Reset to first page
        currentOffset = 0
        currentLimitMutableLiveData.value = pageSize
        hasMorePages = true
        hasMoreItemsMutableLiveData.value = true
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

            val result = try {
                when (validationResult) {
                    is ApiResult.Success -> {
                        val link = if (isWebUrl) {
                            validationResult.data.copy(iconUrl = LinkUrlHelper.getIconUrl(url))
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
            } catch (e: SQLiteConstraintException) {
                if (e.message?.contains("FOREIGN KEY constraint failed") == true) {
                    ApiResult.Error(IllegalArgumentException("Tag name is invalid"))
                } else {
                    ApiResult.Error(IllegalArgumentException("Failed to create link! Please try again."))
                }
            }

            createLinkMutableLiveData.postValue(result)
        }
    }

    fun loadNextPage() {
        if (isLoadingPage || !hasMorePages) return

        isLoadingPage = true
        isLoadingMoreMutableLiveData.value = true

        // Increase the limit to load more items
        val currentLimit = currentLimitMutableLiveData.value ?: pageSize
        currentLimitMutableLiveData.value = currentLimit + pageSize

        isLoadingPage = false
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
        if (query == null || query.isBlank()) {
            return ""
        }
        // Split query into words and add prefix wildcard to each word
        // This allows: "A" -> "A*" matches "AI", "Android"
        //              "and dev" -> "and* dev*" matches "android development"
        val words = query.trim().split("\\s+".toRegex())
        return words.joinToString(" ") { word ->
            // Escape special FTS characters
            val escaped = word.replace("\"", "\"\"")
            // Add prefix wildcard for partial matching
            "$escaped*"
        }
    }
}