package com.timeskip.ezlink.features.link.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timeskip.ezlink.features.common.ApiResult
import com.timeskip.ezlink.features.common.debounce
import com.timeskip.ezlink.features.common.runBlocking
import com.timeskip.ezlink.features.link.data.Link
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LinkSearchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: LinkSearchRepository
) : ViewModel() {
    private val search: String = savedStateHandle["search"] ?: ""
    private val searchMutableLiveData: MutableLiveData<String> = MutableLiveData(search)
    val searchLiveData: LiveData<String> = searchMutableLiveData
    private val searchObserver: Observer<String> = Observer {
        debounceSearch(it)
    }
    private val deleteLinkMutableLiveData: MutableLiveData<ApiResult<Boolean>> = MutableLiveData()

    val deleteLinkLiveData: LiveData<ApiResult<Boolean>> = deleteLinkMutableLiveData

    private val listLinkMutableLiveData: MutableLiveData<List<Link>> =
        MutableLiveData()
    val linkListLiveData: LiveData<List<Link>> = listLinkMutableLiveData

    private val debounceSearch = debounce(
        waitMs = 300L,
        coroutineScope = viewModelScope,
        ::onSearch
    )

    init {
        searchLiveData.observeForever(searchObserver)
    }

    fun updateSearch(search: String) {
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

    private fun onSearch(search: String) {
        Log.d("LinkSearchViewModel", "onSearch")

        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                Log.d("LinkSearchViewModel", "Searching for: $search")
                if (search.isNotEmpty()) {
                    return@withContext searchLink(search)
                }

                return@withContext emptyList()
            }
            Log.d("LinkSearchViewModel", "result for: $result")

            listLinkMutableLiveData.value = result
        }
    }

    private suspend fun searchLink(query: String): List<Link> {
        val searchQuery = sanitizeSearchQuery(query)
        return repository.search(searchQuery)
    }

    private fun sanitizeSearchQuery(query: String): String {
        val queryWithEscapedQuotes = query.replace(Regex.fromLiteral("\""), "\"\"")
        return "*\"$queryWithEscapedQuotes\"*"
    }
}