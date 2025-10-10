package com.example.linkkeeper.features.searchlink

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.linkkeeper.features.common.debounce
import com.example.linkkeeper.features.link.data.Link
import com.example.linkkeeper.features.link.data.LinkRepository
import com.example.linkkeeper.features.tag.data.TagRepository
import com.example.linkkeeper.features.tag.view.TagViewItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchLinkViewModel @Inject constructor(
    tagRepository: TagRepository,
    private val linkRepository: LinkRepository
) : ViewModel() {

    private val searchMutableLiveData: MutableLiveData<String> = MutableLiveData("")
    val searchLiveData: LiveData<String> = searchMutableLiveData

    private val listLinkMediatorLiveData: MutableLiveData<List<Link>> = MutableLiveData()
    val listLinkLiveData: LiveData<List<Link>> = listLinkMediatorLiveData

    val allTagListLiveData: LiveData<List<TagViewItem>> =
        tagRepository.getAllTagListLiveData().switchMap {
            val tagList = it.map { tag -> tag.toTagViewItem() }
            MutableLiveData(tagList)
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
                searchLink(search)
            }
            Log.d("SearchLinkViewModel", "Search result for \"$search\": $result")
            listLinkMediatorLiveData.value = result
        }
    }

    init {
        searchLiveData.observeForever(searchObserver)
    }

    fun updateSearch(search: String) {
        searchMutableLiveData.value = search
    }

    private suspend fun searchLink(query: String): List<Link> {
        val searchQuery = sanitizeSearchQuery(query)
        return linkRepository.search(searchQuery)
    }

    private fun sanitizeSearchQuery(query: String?): String {
        if (query == null) {
            return "";
        }
        val queryWithEscapedQuotes = query.replace(Regex.fromLiteral("\""), "\"\"")
        return "*\"$queryWithEscapedQuotes\"*"
    }
}