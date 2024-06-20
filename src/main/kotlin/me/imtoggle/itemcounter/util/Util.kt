package me.imtoggle.itemcounter.util

import cc.polyfrost.oneconfig.renderer.asset.SVG
import cc.polyfrost.oneconfig.utils.InputHandler
import cc.polyfrost.oneconfig.utils.Notifications
import me.imtoggle.itemcounter.ItemCounter
import me.imtoggle.itemcounter.config.ItemEntry
import me.imtoggle.itemcounter.config.MainRenderer
import me.imtoggle.itemcounter.config.ModConfig
import me.imtoggle.itemcounter.element.ItemElement
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

val MINUS = SVG("/assets/minus.svg")

var dragging: ItemElement? = null

val exampleItems = arrayListOf(
    ItemElement(ItemEntry(), ItemStack(Item.getItemById(35), 64)),
    ItemElement(ItemEntry(), ItemStack(Item.getItemById(262), 16)),
    ItemElement(ItemEntry(), ItemStack(Item.getItemById(326), 1)),
    ItemElement(ItemEntry(), ItemStack(Item.getItemById(327), 1)),
    ItemElement(ItemEntry(), ItemStack(Item.getItemById(373), 28, 16421))
)

fun isHovered(inputHandler: InputHandler, x: Float, y: Float, width: Int, height: Int): Boolean {
    return inputHandler.mouseX() in x..x + width && inputHandler.mouseY() in y..y + height
}

fun notify(string: String) {
    Notifications.INSTANCE.send(ItemCounter.NAME, string)
}

fun addItem(id: ArrayList<Int>): Boolean {
    val stack = ItemStack(Item.getItemById(id[0]) ?: return false)
    stack.itemDamage = id[1]
    if (ModConfig.itemInfos.contains(stack.displayName)) {
        notify("You can't add the same item twice")
        return false
    }
    val entry = ItemEntry(itemInfo = "${id[0]} ${id[1]}", id = id[0])
    MainRenderer.elements.add(ItemElement(entry, stack))
    ModConfig.entries.add(entry)
    ModConfig.itemInfos.add(stack.displayName)
    return true
}