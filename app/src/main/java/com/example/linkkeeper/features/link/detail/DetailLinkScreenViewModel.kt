package com.example.linkkeeper.features.link.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.linkkeeper.features.common.ApiResult
import com.example.linkkeeper.features.common.runBlocking
import com.example.linkkeeper.features.contentHtml.ContentHtml
import com.example.linkkeeper.features.contentHtml.ContentHtmlRepository
import com.example.linkkeeper.features.link.LinkUrlHelper
import com.example.linkkeeper.features.link.data.Link
import com.example.linkkeeper.features.link.data.LinkRepository
import com.example.linkkeeper.features.tag.data.TagRepository
import com.example.linkkeeper.features.tag.view.TagViewItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DetailLinkScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    tagRepository: TagRepository,
    private val linkRepository: LinkRepository,
    private val contentHtmlRepository: ContentHtmlRepository
) : ViewModel() {
    val linkJsonString: String =
        savedStateHandle["linkJsonString"] ?: throw IllegalArgumentException("Link is required")

    var link: Link = Json.decodeFromString(Link.serializer(), linkJsonString)
        private set

    private var originalTitle = link.title
    private var originalNotes = link.description
    private var originalSelectedTagId = link.tagId

    val allTagListLiveData: LiveData<List<TagViewItem>> =
        tagRepository.getAllTagListLiveData().switchMap {
            val tagList = it.map { tag -> tag.toTagViewItem() }
            MutableLiveData(tagList)
        }

    private val titleMutableLiveData = MutableLiveData(link.title)
    val title: LiveData<String> = titleMutableLiveData
    private val notesMutableLiveData = MutableLiveData(link.description)
    val notes: LiveData<String> = notesMutableLiveData
    private val selectedTagIdMutableLiveData = MutableLiveData(
        allTagListLiveData.value?.firstOrNull { it.tag.id == link.tagId }?.tag?.name.orEmpty()
    )
    val selectedTagName: LiveData<String> = selectedTagIdMutableLiveData

    private val hasChangesMutableLiveData = MediatorLiveData(false)
    val hasChanges: LiveData<Boolean> = hasChangesMutableLiveData

    private val updateLinkMutableLiveData: MutableLiveData<ApiResult<Boolean>> = MutableLiveData()
    val updateLinkLiveData: LiveData<ApiResult<Boolean>> = updateLinkMutableLiveData

    val contentHtmlLiveData: LiveData<ContentHtml?> =
        contentHtmlRepository.getContentHtmlLiveData(link.id)

    val contentStatus: MediatorLiveData<ContentHtmlStatus> = MediatorLiveData()

    private val insertContentHtmlResultMutableLiveData: MutableLiveData<ApiResult<Unit>> =
        MutableLiveData()
    val insertContentHtmlResultLiveData: LiveData<ApiResult<Unit>> =
        insertContentHtmlResultMutableLiveData

    init {
        hasChangesMutableLiveData.addSource(titleMutableLiveData) { checkForChanges() }
        hasChangesMutableLiveData.addSource(notesMutableLiveData) { checkForChanges() }
        hasChangesMutableLiveData.addSource(selectedTagIdMutableLiveData) { checkForChanges() }

        contentStatus.addSource(contentHtmlLiveData) { contentHtml ->
            val status = when {
                contentHtml == null -> ContentHtmlStatus.NotDownloaded
                contentHtml.isSupported -> ContentHtmlStatus.Available
                !contentHtml.isSupported -> ContentHtmlStatus.NotSupported
                else -> ContentHtmlStatus.NotDownloaded
            }
            contentStatus.value = status
        }

        contentStatus.addSource(insertContentHtmlResultLiveData) {
            if (it is ApiResult.Loading) {
                contentStatus.value = ContentHtmlStatus.Downloading
            } else if (it is ApiResult.Error) {
                contentStatus.value = ContentHtmlStatus.NotDownloaded
            }
        }
    }

    fun setTitle(newTitle: String) {
        titleMutableLiveData.value = newTitle
    }

    fun setNotes(newNotes: String) {
        notesMutableLiveData.value = newNotes
    }

    fun setSelectedTagName(tagName: String) {
        selectedTagIdMutableLiveData.value = tagName
    }

    private fun checkForChanges() {
        val currentTitle = titleMutableLiveData.value ?: ""
        val currentNotes = notesMutableLiveData.value ?: ""
        val currentSelectedTagId = selectedTagIdMutableLiveData.value ?: ""
        val hasChanged = currentTitle != originalTitle ||
                currentNotes != originalNotes ||
                currentSelectedTagId != originalSelectedTagId
        hasChangesMutableLiveData.value = hasChanged
    }

    fun updateLink() {
        if (hasChangesMutableLiveData.value == false) {
            return
        }

        val tagId =
            allTagListLiveData.value?.firstOrNull { it.tag.name == selectedTagName.value }?.tag?.id
                ?: return
        val newLink = link.copy(
            title = title.value.orEmpty(),
            description = notes.value.orEmpty(),
            tagId = tagId
        )
        viewModelScope.launch {
            if (updateLinkMutableLiveData.value is ApiResult.Loading) {
                return@launch
            }
            updateLinkMutableLiveData.value = ApiResult.Loading()
            val result = runBlocking(
                onBlocking = { linkRepository.updateLink(newLink) },
                onSuccess = { ApiResult.Success(it) },
                onError = { ApiResult.Error(it) }
            )
            updateLinkMutableLiveData.value = result
        }

        originalTitle = newLink.title
        originalNotes = newLink.description
        originalSelectedTagId = newLink.tagId
        link = newLink
        hasChangesMutableLiveData.value = false
    }

    fun downloadContentHtml() {
        viewModelScope.launch {
            if (insertContentHtmlResultLiveData.value is ApiResult.Loading) {
                return@launch
            }

            insertContentHtmlResultMutableLiveData.value = ApiResult.Loading()
            val result = runBlocking(
                onBlocking = {
                    val content = LinkUrlHelper.crawlData(link.url)
                    val contentHtml = ContentHtml(
                        UUID.randomUUID().toString(),
                        link.id,
                        content,
                        isSupported = !content.isNullOrBlank()
                    )
                    contentHtmlRepository.insertContentHtml(contentHtml)
                },
                onSuccess = { ApiResult.Success(Unit) },
                onError = { ApiResult.Error(it) }
            )
            insertContentHtmlResultMutableLiveData.value = result
        }
    }

    enum class ContentHtmlStatus {
        NotDownloaded,
        Downloading,
        Available,
        NotSupported
    }
}