package me.imtoggle.itemcounter.util

import cc.polyfrost.oneconfig.renderer.asset.SVG
import cc.polyfrost.oneconfig.utils.InputHandler
import cc.polyfrost.oneconfig.utils.Notifications
import me.imtoggle.itemcounter.ItemCounter
import me.imtoggle.itemcounter.element.ItemElement

val MINUS = SVG("/assets/minus.svg")

var dragging: ItemElement? = null

fun isHovered(inputHandler: InputHandler, x: Float, y: Float, width: Int, height: Int): Boolean {
    return inputHandler.mouseX() in x..x + width && inputHandler.mouseY() in y..y + height
}

fun notify(string: String) {
    Notifications.INSTANCE.send(ItemCounter.NAME, string)
}