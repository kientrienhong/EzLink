package com.timeskip.ezlink.features.link.editor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timeskip.ezlink.features.common.ApiResult
import com.timeskip.ezlink.features.common.ConnectivityObserver
import com.timeskip.ezlink.features.common.runBlocking
import com.timeskip.ezlink.features.contentHtml.ContentHtml
import com.timeskip.ezlink.features.contentHtml.ContentHtmlRepository
import com.timeskip.ezlink.features.common.LinkUrlHelper
import com.timeskip.ezlink.features.link.data.Link
import com.timeskip.ezlink.features.link.data.LinkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class LinkDetailViewModel @Inject constructor(
    private val linkRepository: LinkRepository,
    private val contentHtmlRepository: ContentHtmlRepository,
    connectivityObserver: ConnectivityObserver
) : ViewModel() {
    private val linkUpdateResultMutableLiveData: MutableLiveData<ApiResult<Boolean>?> =
        MutableLiveData()

    val linkUpdateResultLiveData: LiveData<ApiResult<Boolean>?> = linkUpdateResultMutableLiveData

    private val crawlWebResultMutableLiveData: MutableLiveData<ApiResult<Unit>?> = MutableLiveData()
    val crawlWebResultLiveData: LiveData<ApiResult<Unit>?> = crawlWebResultMutableLiveData

    val networkStatusFlow = connectivityObserver.observer()

    fun getContentHtmlLiveData(linkId: Int): LiveData<ContentHtml?> =
        contentHtmlRepository.getContentHtmlLiveData(linkId)

    fun updateLink(link: Link) {
        viewModelScope.launch {
            if (linkUpdateResultMutableLiveData.value is ApiResult.Loading) {
                return@launch
            }
            linkUpdateResultMutableLiveData.value = ApiResult.Loading()
            val result = runBlocking(
                onBlocking = { linkRepository.updateLink(link) },
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
                    val contentHtmlExisted = contentHtmlRepository.getContentHtml(linkId)
                    if (contentHtmlExisted != null && content?.isNotEmpty() == true) {
                        contentHtmlRepository.updateContentHtml(contentHtmlExisted.copy(content = content))
                    } else {
                        contentHtmlRepository.insertContentHtml(contentHtml)
                    }
                },
                onSuccess = { ApiResult.Success(Unit) },
                onError = { ApiResult.Error(it) }
            )
            crawlWebResultMutableLiveData.value = result
        }
    }

    fun refreshContentHtml(linkId: Int, url: String) {
        viewModelScope.launch {
            try {
                val contentHtmlExisted =
                    contentHtmlRepository.getContentHtml(linkId) ?: return@launch
                val content = LinkUrlHelper.crawlData(url)
                if (content?.isEmpty() == null) {
                    return@launch
                }
                contentHtmlRepository.updateContentHtml(contentHtmlExisted.copy(content = content))
            } catch (canceled: CancellationException) {
                throw canceled
            } catch (_: Exception) {
                // ignore this function is fire and forget
            }
        }
    }

    fun deleteContentHtml(linkId: Int) {
        viewModelScope.launch {
            try {
                contentHtmlRepository.deleteContentHtmlByLinkId(linkId)
            } catch (canceled: CancellationException) {
                throw canceled
            } catch (_: Exception) {
                // ignore this function is fire and forget
            }
        }
    }

    fun reset() {
        linkUpdateResultMutableLiveData.value = null
    }

    fun resetCrawlWebResult() {
        crawlWebResultMutableLiveData.value = null
    }
}