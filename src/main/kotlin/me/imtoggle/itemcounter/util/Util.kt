package me.imtoggle.itemcounter.util

import cc.polyfrost.oneconfig.renderer.asset.SVG
import cc.polyfrost.oneconfig.utils.InputHandler
import cc.polyfrost.oneconfig.utils.Notifications
import me.imtoggle.itemcounter.ItemCounter
import me.imtoggle.itemcounter.config.ItemEntry
import me.imtoggle.itemcounter.element.ItemElement
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

val MINUS = SVG("/assets/minus.svg")

var dragging: ItemElement? = null

val exampleItems = arrayListOf(
    ItemElement(ItemEntry(), ItemStack(Item.getItemById(425), 8, 1)),
    ItemElement(ItemEntry(), ItemStack(Item.getItemById(420), 9)),
    ItemElement(ItemEntry(), ItemStack(Item.getItemById(383), 6, 65)),
    ItemElement(ItemEntry(), ItemStack(Item.getItemById(367), 4, 0))
)

fun isHovered(inputHandler: InputHandler, x: Float, y: Float, width: Int, height: Int): Boolean {
    return inputHandler.mouseX() in x..x + width && inputHandler.mouseY() in y..y + height
}

fun notify(string: String) {
    Notifications.INSTANCE.send(ItemCounter.NAME, string)
}