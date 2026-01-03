package com.timeskip.ezlink.features.link.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.timeskip.ezlink.features.common.ApiResult
import com.timeskip.ezlink.features.common.runBlocking
import com.timeskip.ezlink.features.link.data.Link
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LinkSearchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: LinkSearchRepository
) : ViewModel() {
    private val search: String = savedStateHandle["search"] ?: ""
    private val searchMutableLiveData: MutableLiveData<String> = MutableLiveData(search)
    val searchLiveData: LiveData<String> = searchMutableLiveData

    private val deleteLinkMutableLiveData: MutableLiveData<ApiResult<Boolean>> = MutableLiveData()
    val deleteLinkLiveData: LiveData<ApiResult<Boolean>> = deleteLinkMutableLiveData

    private var currentOffset = 0
    private val pageSize = 10
    private var isLoadingPage = false
    private var hasMorePages = true

    private val isLoadingMoreMutableLiveData = MutableLiveData(false)

    private val hasMoreItemsMutableLiveData = MutableLiveData(true)

    // Current limit for pagination (increases as user loads more)
    private val currentLimitMutableLiveData = MutableLiveData(pageSize)

    // Unified LiveData that switches based on search query and limit
    private val unifiedLinksLiveData: LiveData<List<Link>> = currentLimitMutableLiveData.switchMap { limit ->
        searchLiveData.switchMap { searchQuery ->
            if (searchQuery.isBlank()) {
                // Return empty LiveData for blank search
                MutableLiveData(emptyList())
            } else {
                val sanitizedQuery = sanitizeSearchQuery(searchQuery)
                repository.searchLiveData(sanitizedQuery, limit)
            }
        }
    }

    private val listLinkMediatorLiveData: MediatorLiveData<List<Link>> = MediatorLiveData()
    val linkListLiveData: LiveData<List<Link>> = listLinkMediatorLiveData

    init {
        // Observe the unified LiveData
        listLinkMediatorLiveData.addSource(unifiedLinksLiveData) { links ->
            listLinkMediatorLiveData.value = links
            // Update hasMorePages based on result size
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
    }

    fun refreshList() {
        // Reset to first page
        currentOffset = 0
        currentLimitMutableLiveData.value = pageSize
        hasMorePages = true
        hasMoreItemsMutableLiveData.value = true
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
        // Reset pagination when search changes
        currentOffset = 0
        currentLimitMutableLiveData.value = pageSize
        hasMorePages = true
        hasMoreItemsMutableLiveData.value = true
        searchMutableLiveData.value = search
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


    private fun sanitizeSearchQuery(query: String): String {
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