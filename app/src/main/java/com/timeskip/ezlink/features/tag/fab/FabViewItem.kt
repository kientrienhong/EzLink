package com.timeskip.ezlink.features.tag.fab

import androidx.annotation.DrawableRes

class FabViewItem(val label: String, @DrawableRes val iconRes: Int, val onClick: () -> Unit)