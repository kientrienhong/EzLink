package com.timeskip.ezlink.features.link.list

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
import com.timeskip.ezlink.features.common.LinkValidateUtils
import com.timeskip.ezlink.features.link.data.Link
import com.timeskip.ezlink.features.link.data.LinkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private val createLinkMutableLiveData: MutableLiveData<ApiResult<Link>?> =
        MutableLiveData()
    val createLinkLiveData: LiveData<ApiResult<Link>?> = createLinkMutableLiveData

    private val listLinkMediatorLiveData: MutableLiveData<List<Link>> = MediatorLiveData()
    val linkListLiveData: LiveData<List<Link>> = listLinkMediatorLiveData

    private val localLinkListLiveData: LiveData<List<Link>> =
        repository.getLinkListLiveData(tagName.orEmpty())
    private val deleteLinkMutableLiveData: MutableLiveData<ApiResult<Boolean>> = MutableLiveData()

    val deleteLinkLiveData: LiveData<ApiResult<Boolean>> = deleteLinkMutableLiveData


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
    }

    @OptIn(ExperimentalUuidApi::class)
    fun validateUrlThenCreatingLink(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (createLinkMutableLiveData.value is ApiResult.Loading) {
                return@launch
            }
            createLinkMutableLiveData.postValue(ApiResult.Loading())
            val validationResult = LinkValidateUtils.validateUrl(tagName.orEmpty(), url)
            val result = when (validationResult) {
                is ApiResult.Success -> {
                    val resultInsert = repository.insertLink(validationResult.data)
                    if (resultInsert) {
                        ApiResult.Success(validationResult.data)
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

    private suspend fun searchLink(query: String): List<Link> {
        val searchQuery = sanitizeSearchQuery(query)
        return repository.search(searchQuery)
    }

    fun updateSearch(search: String) {
        searchMutableLiveData.value = search
    }

    fun resetLinkValidationLiveData() {
        createLinkMutableLiveData.value = null
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