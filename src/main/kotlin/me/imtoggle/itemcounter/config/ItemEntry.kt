package me.imtoggle.itemcounter.config

import cc.polyfrost.oneconfig.config.annotations.Exclude

data class ItemEntry(var itemInfo: String = "", @Exclude var id: Int = -1, var enabled: Boolean = true, var ignoreMetaData: Boolean = false)