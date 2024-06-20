package me.imtoggle.itemcounter.config

import cc.polyfrost.oneconfig.config.elements.BasicOption
import cc.polyfrost.oneconfig.renderer.font.Fonts
import cc.polyfrost.oneconfig.utils.InputHandler
import cc.polyfrost.oneconfig.utils.dsl.nanoVGHelper
import me.imtoggle.itemcounter.element.ItemElement
import me.imtoggle.itemcounter.util.*
import java.awt.Color
import kotlin.math.ceil

object MainRenderer : BasicOption(null, null, "", "", "General", "", 2) {

    var elements = ArrayList<ItemElement>()

    var addQueue = HashMap<Int, ItemElement>()

    var removeQueue = ArrayList<ItemElement>()

    var renderText = ""

    var elementCount = 0

    val WHITE = Color(255, 255, 255, 100).rgb

    override fun getHeight(): Int {
        return if (elementCount != 0) ceil(elementCount / 4f).toInt() * 96 else 20
    }

    override fun draw(vg: Long, x: Int, y: Int, inputHandler: InputHandler) {
        if (elements.size == 0) {
            nanoVGHelper.drawText(vg, "Do \"/itemcounter add\" to add the item you're holding", x.toFloat(), y.toFloat() + 10, WHITE, 20f, Fonts.BOLD)
        }
        var i = 0
        elementCount = 0
        for (index in 0..elements.size) {
            var renderX = x.toFloat() + 64 + i % 4 * 224
            var renderY = y.toFloat() + i / 4 * 96 + 16
            if (dragging != null && isHovered(inputHandler, renderX - 16, renderY - 16, 192 + 32, 64 + 32)) {
                nanoVGHelper.drawRoundedRect(vg, renderX, renderY, 192f, 64f, WHITE, 10f)
                elementCount++
                i++
                renderX = x.toFloat() + 64 + i % 4 * 224
                renderY = y.toFloat() + i / 4 * 96 + 16
                if (ItemRenderer.shouldCheck) {
                    dragging!!.onAdd(index)
                }
            }
            if (index < elements.size) {
                elements[index].draw(vg, renderX, renderY, inputHandler)
                elementCount++
            }
            i++
        }
        if (addQueue.isNotEmpty()) {
            addQueue.forEach {
                elements.add(it.key, it.value)
            }
            addQueue.clear()
        }
        if (removeQueue.isNotEmpty()) {
            elements.removeAll(removeQueue.toSet())
            removeQueue.clear()
        }
        if (ItemRenderer.shouldCheck) {
            dragging = null
            ItemRenderer.shouldCheck = false
        }
    }
}