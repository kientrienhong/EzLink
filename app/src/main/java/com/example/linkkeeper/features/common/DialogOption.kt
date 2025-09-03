package com.example.linkkeeper.features.common

import com.example.linkkeeper.features.tag.data.Tag
import com.example.linkkeeper.features.tag.data.TagRepository

sealed class DialogOption() {
    abstract val title: String
    abstract val onClick: suspend () -> Boolean
}

class DeleteTag(tagRepositoryImpl: TagRepository, tag: Tag) : DialogOption() {
    override val title: String = "Delete tag"
    override val onClick: suspend () -> Boolean = { tagRepositoryImpl.deleteTag(tag) }
}