package com.example.linkkeeper.features.tag.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.linkkeeper.features.common.ApiResult
import com.example.linkkeeper.features.common.runBlocking
import com.example.linkkeeper.features.tag.data.Tag
import com.example.linkkeeper.features.tag.data.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TagViewModel @Inject constructor(private val repository: TagRepository) : ViewModel() {
    val tagListLiveData: LiveData<List<TagViewItem>> = repository.getTagListLiveData().switchMap {
        val tagList = it.map { tag -> tag.toTagViewItem() }
        MutableLiveData(tagList)
    }
    private val initialLoadMutableLiveData: MutableLiveData<ApiResult<Unit>> =
        MutableLiveData<ApiResult<Unit>>()
    val initialLoadLiveData: LiveData<ApiResult<Unit>> = initialLoadMutableLiveData
    private val createTagMutableLiveData: MutableLiveData<ApiResult<Boolean>> = MutableLiveData()
    val createTagLiveData: LiveData<ApiResult<Boolean>> = createTagMutableLiveData

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
                onBlocking = { repository.getTagList() },
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
                    val tag = Tag(id = null, name = name)
                    repository.createTag(tag)
                },
                onSuccess = { ApiResult.Success(it) },
                onError = { ApiResult.Error(it) }
            )
        }
    }

    private fun Tag.toTagViewItem(): TagViewItem {
        val backgroundColor = TagBackgroundColorProvider.getColorFromTag(this)
        return TagViewItem(this, backgroundColor)
    }
}