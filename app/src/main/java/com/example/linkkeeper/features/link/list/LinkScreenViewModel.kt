package com.example.linkkeeper.features.link.list

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.linkkeeper.features.common.ApiResult
import com.example.linkkeeper.features.common.debounce
import com.example.linkkeeper.features.common.runBlocking
import com.example.linkkeeper.features.link.LinkRetryStep
import com.example.linkkeeper.features.link.LinkUrlHelper
import com.example.linkkeeper.features.link.data.Link
import com.example.linkkeeper.features.link.data.LinkRepository
import com.example.linkkeeper.features.tag.data.TagRepository
import com.example.linkkeeper.features.tag.view.TagViewItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.HttpStatusException
import java.lang.Thread.sleep
import javax.inject.Inject
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@HiltViewModel
class LinkScreenViewModel @Inject constructor(
    tagRepository: TagRepository,
    private val linkRepository: LinkRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val tagId: String =
        savedStateHandle["tagId"] ?: throw IllegalArgumentException("Tag id is required")
    val tagName: String =
        savedStateHandle["tagName"] ?: throw IllegalArgumentException("Tag name is required")

    val allTagListLiveData: LiveData<List<TagViewItem>> =
        tagRepository.getAllTagListLiveData().switchMap {
            val tagList = it.map { tag -> tag.toTagViewItem() }
            MutableLiveData(tagList)
        }

    private val linkValidationMutableLiveData: MutableLiveData<ApiResult<Link>?> =
        MutableLiveData()
    val linkValidationLiveData: LiveData<ApiResult<Link>?> = linkValidationMutableLiveData

    val linkListLiveData: LiveData<List<Link>> = linkRepository.getLinkListLiveData(tagId)
    private val deleteLinkMutableLiveData: MutableLiveData<ApiResult<Boolean>> = MutableLiveData()

    val deleteLinkLiveData: LiveData<ApiResult<Boolean>> = deleteLinkMutableLiveData

    @OptIn(ExperimentalUuidApi::class)
    fun validateUrl(
        tagId: Int,
        url: String,
        delayMillis: Long = 200,
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
                            return@launch validateUrl(tagId, url, delayMillis, retryStep.nextStep)
                        } else {
                            ""
                        }
                    } catch (ex: IllegalArgumentException) {
                        throw ex
                    } catch (_: Exception) {
                        ""
                    }

                    val link = Link(
                        id = Uuid.random().toString(),
                        url = linkRetryStepToUrl,
                        tagId = "tagId",
                        iconUrl = iconUrl,
                        title = title,
                        description = "",
                        dateTimeCreated = System.currentTimeMillis()
                    )
                    ApiResult.Success(link)
                } else {
                    ApiResult.Error(IllegalArgumentException("Invalid URL"))
                }
            } catch (e: IllegalArgumentException) {
                Log.e("LinkScreenViewModel", "Error validating URL: ${e.message}")
                if (!retryStep.isLast()) {
                    return@launch validateUrl(tagId, url, delayMillis, retryStep.nextStep)
                }
                ApiResult.Error(IllegalArgumentException("Wrong URL format"))
            }
            linkValidationMutableLiveData.postValue(result)
        }
    }

    fun deleteLink(link: Link) {
        viewModelScope.launch {
            if (deleteLinkLiveData.value is ApiResult.Loading) {
                return@launch
            }

            deleteLinkMutableLiveData.value = ApiResult.Loading()

            val result = runBlocking(
                onBlocking = { linkRepository.deleteLink(link) },
                onSuccess = { ApiResult.Success(it) },
                onError = { ApiResult.Error(it) }
            )

            deleteLinkMutableLiveData.value = result
        }
    }

    private fun getIconUrl(domain: String): String = "https://logo.clearbit.com/$domain"
}