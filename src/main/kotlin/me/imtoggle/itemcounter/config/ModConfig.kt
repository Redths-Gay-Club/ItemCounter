package me.imtoggle.itemcounter.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.ConfigUtils
import cc.polyfrost.oneconfig.config.data.*
import cc.polyfrost.oneconfig.config.elements.BasicOption
import cc.polyfrost.oneconfig.config.elements.OptionPage
import me.imtoggle.itemcounter.ItemCounter
import me.imtoggle.itemcounter.element.ItemElement
import me.imtoggle.itemcounter.hud.ItemCounterHud
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import java.lang.reflect.Field

object ModConfig : Config(Mod(ItemCounter.NAME, ModType.HUD, "/assets/ItemCounterDarkTransparent.svg"), "${ItemCounter.MODID}.json") {

    @CustomOption
    var entries = ArrayList<ItemEntry>()

    @Exclude
    var itemInfos = ArrayList<String>()

    @HUD(
        name = "Hud",
        category = "HUD"
    )
    var itemCounterHud = ItemCounterHud()

    init {
        initialize()
    }

    override fun getCustomOption(field: Field?, annotation: CustomOption?, page: OptionPage?, mod: Mod?, migrate: Boolean): BasicOption {
        val option = MainRenderer
        ConfigUtils.getSubCategory(page, "General", "").options.add(option)
        return option
    }

    override fun load() {
        super.load()
        itemInfos.clear()
        MainRenderer.elements.clear()
        for (i in entries) {
            val key = i.itemInfo.split(" ").map { it.toInt() }
            i.id = key[0]
            val stack = ItemStack(Item.getItemById(key[0]) ?: continue)
            stack.itemDamage = key[1]
            itemInfos.add(stack.displayName)
            MainRenderer.elements.add(ItemElement(i, stack))
        }
    }

}