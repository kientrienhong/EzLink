package com.example.linkkeeper.features.link.editor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.linkkeeper.features.common.ApiResult
import com.example.linkkeeper.features.common.runBlocking
import com.example.linkkeeper.features.link.LinkUrlHelper
import com.example.linkkeeper.features.link.data.ContentHtml
import com.example.linkkeeper.features.link.data.Link
import com.example.linkkeeper.features.link.data.LinkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LinkEditorViewModel @Inject constructor(private val repository: LinkRepository) :
    ViewModel() {
    private val linkInsertResultMutableLiveData: MutableLiveData<ApiResult<Boolean>?> =
        MutableLiveData()
    private val linkUpdateResultMutableLiveData: MutableLiveData<ApiResult<Boolean>?> =
        MutableLiveData()
    val resultMediatorLiveData: MediatorLiveData<ApiResult<Boolean>> = MediatorLiveData()

    private val crawlWebResultMutableLiveData: MutableLiveData<ApiResult<Unit>> = MutableLiveData()
    val crawlWebResultLiveData: LiveData<ApiResult<Unit>> = crawlWebResultMutableLiveData

    init {
        resultMediatorLiveData.addSource(linkInsertResultMutableLiveData) {
            resultMediatorLiveData.value = linkInsertResultMutableLiveData.value
        }

        resultMediatorLiveData.addSource(linkUpdateResultMutableLiveData) {
            resultMediatorLiveData.value = linkUpdateResultMutableLiveData.value
        }
    }

    fun insertLink(link: Link) {
        viewModelScope.launch {
            if (linkUpdateResultMutableLiveData.value is ApiResult.Loading) {
                return@launch
            }
            linkInsertResultMutableLiveData.value = ApiResult.Loading()
            val result = runBlocking(
                onBlocking = { repository.insertLink(link) },
                onSuccess = { ApiResult.Success(it) },
                onError = { ApiResult.Error(it) }
            )

            linkInsertResultMutableLiveData.value = result
        }
    }

    fun updateLink(link: Link) {
        viewModelScope.launch {
            if (linkUpdateResultMutableLiveData.value is ApiResult.Loading) {
                return@launch
            }
            linkUpdateResultMutableLiveData.value = ApiResult.Loading()
            val result = runBlocking(
                onBlocking = { repository.updateLink(link) },
                onSuccess = { ApiResult.Success(it) },
                onError = { ApiResult.Error(it) }
            )

            linkUpdateResultMutableLiveData.value = result
        }
    }

    fun crawlContentHtml(linkId: Int, url: String) {
        viewModelScope.launch {
            if (crawlWebResultLiveData.value is ApiResult.Loading) {
                return@launch
            }

            crawlWebResultMutableLiveData.value = ApiResult.Loading()
            val result = runBlocking(
                onBlocking = {
                    val content = LinkUrlHelper.crawlData(url)
                    val contentHtml = ContentHtml(id = null, linkId, content.orEmpty())
                    repository.insertContentHtml(contentHtml)
                },
                onSuccess = { ApiResult.Success(Unit) },
                onError = { ApiResult.Error(it) }
            )
            crawlWebResultMutableLiveData.value = result
        }
    }

    fun reset() {
        linkInsertResultMutableLiveData.value = null
        linkUpdateResultMutableLiveData.value = null
    }
}