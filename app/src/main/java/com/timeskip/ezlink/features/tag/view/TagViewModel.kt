package com.timeskip.ezlink.features.tag.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.timeskip.ezlink.features.common.ApiResult
import com.timeskip.ezlink.features.common.LinkValidateUtils
import com.timeskip.ezlink.features.common.runBlocking
import com.timeskip.ezlink.features.link.data.Link
import com.timeskip.ezlink.features.link.data.LinkRepository
import com.timeskip.ezlink.features.tag.data.Tag
import com.timeskip.ezlink.features.tag.data.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.uuid.ExperimentalUuidApi

@HiltViewModel
class TagViewModel @Inject constructor(
    private val tagRepository: TagRepository,
    private val linkRepository: LinkRepository
) : ViewModel() {
    val tagListLiveData: LiveData<List<TagViewItem>> = tagRepository.getTagListLiveData().switchMap {
        val tagList = it.map { tag -> tag.toTagViewItem() }
        MutableLiveData(tagList)
    }
    private val initialLoadMutableLiveData: MutableLiveData<ApiResult<Unit>> =
        MutableLiveData<ApiResult<Unit>>()
    val initialLoadLiveData: LiveData<ApiResult<Unit>> = initialLoadMutableLiveData
    private val createTagMutableLiveData: MutableLiveData<ApiResult<Boolean>> = MutableLiveData()
    val createTagLiveData: LiveData<ApiResult<Boolean>> = createTagMutableLiveData
    private val deleteTagMutableLiveData: MutableLiveData<ApiResult<Boolean>> = MutableLiveData()
    val deleteTagLiveData: LiveData<ApiResult<Boolean>> = deleteTagMutableLiveData

    private val createLinkMutableLiveData: MutableLiveData<ApiResult<Link>> = MutableLiveData()
    val createLinkLiveData: LiveData<ApiResult<Link>> = createLinkMutableLiveData

    init {
        getTagList()
    }

    private fun getTagList() {
        viewModelScope.launch {
            if (initialLoadMutableLiveData.value is ApiResult.Loading) {
                return@launch
            }
            initialLoadMutableLiveData.value = ApiResult.Loading()
            initialLoadMutableLiveData.value = runBlocking(
                onBlocking = { tagRepository.getTagList() },
                onSuccess = { ApiResult.Success(Unit) },
                onError = { ApiResult.Error(it) }
            )
        }
    }

    fun createTag(name: String) {
        viewModelScope.launch {
            if (createTagLiveData.value is ApiResult.Loading) {
                return@launch
            }
            createTagMutableLiveData.value = ApiResult.Loading()
            createTagMutableLiveData.value = runBlocking(
                onBlocking = {
                    val tag = Tag(name = name)
                    val existedTag = tagRepository.getTagByName(name)
                    if (existedTag != null) {
                        throw IllegalArgumentException("Tag with name '$name' already exists.")
                    }
                    tagRepository.createTag(tag)
                },
                onSuccess = { ApiResult.Success(it) },
                onError = { ApiResult.Error(it) }
            )
        }
    }

    fun deleteTag(tag: Tag) {
        viewModelScope.launch {
            if (deleteTagLiveData.value is ApiResult.Loading) {
                return@launch
            }
            deleteTagMutableLiveData.value = ApiResult.Loading()
            deleteTagMutableLiveData.value = runBlocking(
                onBlocking = { tagRepository.deleteTag(tag) },
                onSuccess = { ApiResult.Success(it) },
                onError = { ApiResult.Error(it) }
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun createLink( url: String, tagName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (createLinkLiveData.value is ApiResult.Loading) {
                return@launch
            }
            createLinkMutableLiveData.postValue(ApiResult.Loading())
            val validationResult = LinkValidateUtils.validateUrl(tagName, url)
            val result = when (validationResult) {
                is ApiResult.Success -> {
                    val resultInsert = linkRepository.insertLink(validationResult.data)
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

    private fun Tag.toTagViewItem(): TagViewItem {
        val backgroundColor = TagBackgroundColorProvider.getColorFromTag(this)
        return TagViewItem(this, backgroundColor)
    }
}