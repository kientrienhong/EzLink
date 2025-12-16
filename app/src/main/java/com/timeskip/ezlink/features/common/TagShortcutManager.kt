package com.timeskip.ezlink.features.common

import android.content.Context
import android.content.Intent
import androidx.core.app.Person
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.timeskip.ezlink.MainActivity
import com.timeskip.ezlink.features.tag.data.Tag
import com.timeskip.ezlink.features.tag.view.TagResource

class TagShortcutManager(private val context: Context) {
    fun updateTagShortcuts(tags: List<Tag>) {
        val shortcuts = tags
            .asSequence()
            .sortedByDescending { it.updateAt }
            .take(3)
            .mapIndexed { index, tag -> createTagShortcut(tag, index) }
            .toList()
        ShortcutManagerCompat.removeAllDynamicShortcuts(context)
        ShortcutManagerCompat.addDynamicShortcuts(context, shortcuts)
    }

    private fun createTagShortcut(tag: Tag, index: Int): ShortcutInfoCompat {
        val tagResource = TagResource.fromNameTag(tag.name)
        val iconCompat = IconCompat.createWithResource(context, tagResource.iconRes)

        return ShortcutInfoCompat.Builder(context, tag.name)
            .setShortLabel(tag.name)
            .setLongLabel("Add link to ${tag.name}")
            .setIcon(iconCompat)
            .setCategories(setOf(CATEGORY_TAG_SHORTCUT))
            .setIntent(
                Intent(context, MainActivity::class.java).apply {
                    action = ACTION_SHORTCUT
                    putExtra(KEY_TAG_NAME, tag.name)
                }
            )
            .setLongLived(true)
            .setRank(index)
            .setPerson(Person.Builder().setName(tag.name).build())
            .build()
    }

    companion object {
        private const val CATEGORY_TAG_SHORTCUT = "com.timeskip.ezlink.TAG_SHORTCUT"
        const val ACTION_SHORTCUT = "com.timeskip.ezlink.SHARE_TO_TAG"
        const val KEY_TAG_NAME = "TAG_NAME"
    }
}