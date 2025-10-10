package com.example.linkkeeper.features.home.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.linkkeeper.features.common.ApiResult
import com.example.linkkeeper.features.common.runBlocking
import com.example.linkkeeper.features.tag.data.Tag
import com.example.linkkeeper.features.tag.data.TagRepository
import com.example.linkkeeper.features.tag.view.TagViewItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val tagRepository: TagRepository) : ViewModel() {
    val allTagListLiveData: LiveData<List<TagViewItem>> =
        tagRepository.getAllTagListLiveData().switchMap {
            val tagList = it.map { tag -> tag.toTagViewItem() }
            MutableLiveData(tagList)
        }
    private val createTagMutableLiveData: MutableLiveData<ApiResult<Boolean>> = MutableLiveData()
    val createTagLiveData: LiveData<ApiResult<Boolean>> = createTagMutableLiveData
    private val deleteTagMutableLiveData: MutableLiveData<ApiResult<Boolean>> = MutableLiveData()
    val deleteTagLiveData: LiveData<ApiResult<Boolean>> = deleteTagMutableLiveData

    fun createTag(name: String) {
        viewModelScope.launch {
            if (createTagLiveData.value is ApiResult.Loading) {
                return@launch
            }
            createTagMutableLiveData.value = ApiResult.Loading()
            createTagMutableLiveData.value = runBlocking(
                onBlocking = {
                    if (name.isBlank()) {
                        throw IllegalArgumentException(MESSAGE_EMPTY_TAG)
                    }

                    val tag = Tag(id = UUID.randomUUID().toString(), name = name)
                    tagRepository.createTag(tag)
                },
                onSuccess = { ApiResult.Success(it) },
                onError = {
                    if (it.message == MESSAGE_EMPTY_TAG) {
                        ApiResult.Error(it)
                    } else {
                        ApiResult.Error(
                            Exception("Failed to create tag! Please try again later.")
                        )
                    }
                }
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

    private companion object {
        const val MESSAGE_EMPTY_TAG = "Tag name cannot be empty"
    }
}