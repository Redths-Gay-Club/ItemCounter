package me.imtoggle.itemcounter.element

import cc.polyfrost.oneconfig.gui.animations.*
import cc.polyfrost.oneconfig.gui.elements.BasicButton
import cc.polyfrost.oneconfig.internal.assets.Colors
import cc.polyfrost.oneconfig.utils.InputHandler
import cc.polyfrost.oneconfig.utils.color.ColorPalette
import cc.polyfrost.oneconfig.utils.dsl.nanoVGHelper
import me.imtoggle.itemcounter.config.ItemEntry
import me.imtoggle.itemcounter.config.MainRenderer
import me.imtoggle.itemcounter.config.ModConfig
import me.imtoggle.itemcounter.util.*
import net.minecraft.item.ItemStack
import java.awt.Color

@Suppress("UnstableAPIUsage")
class ItemElement(val itemEntry: ItemEntry, val itemStack: ItemStack) {

    val colorAnimation = ColorAnimation(ColorPalette(Colors.GRAY_600, Colors.GRAY_700, Color(28, 28, 30, 255).rgb))
    val removeButton = BasicButton(32, 32, MINUS, 2, ColorPalette.PRIMARY_DESTRUCTIVE)
    val enableCheckBox = SimpleCheckBox(itemEntry::class.java.declaredFields[2], itemEntry, "Enable")
    val ignoreMetaCheckBox = SimpleCheckBox(itemEntry::class.java.declaredFields[3], itemEntry, "Ignore Metadata Check")
    var animationX: Animation = DummyAnimation(0f)
    var animationY: Animation = DummyAnimation(0f)

    init {
        removeButton.setClickAction {
            onRemove()
        }
    }

    fun draw(vg: Long, x: Float, y: Float, inputHandler: InputHandler) {
        if (animationX is DummyAnimation) {
            animationX = EaseOutQuart(0f, 0f, x, false)
        } else if (x != animationX.end) {
            animationX = EaseOutQuart(400f, animationX.end, x, false)
        }
        if (animationY is DummyAnimation) {
            animationY = EaseOutQuart(0f, 0f, y, false)
        } else if (y != animationY.end) {
            animationY = EaseOutQuart(400f, animationY.end, y, false)
        }
        val currentX = animationX.get()
        val currentY = animationY.get()
        val renderX = currentX + 16
        val renderY = currentY + 16
        val hovered =
            isHovered(inputHandler, currentX, currentY, 192, 64) &&
            !isHovered(inputHandler, renderX, renderY + 4, 24, 24) &&
            !isHovered(inputHandler, renderX + 88, renderY + 4, 24, 24) &&
            !isHovered(inputHandler, renderX + 128, renderY, 32, 32)
        if (hovered && inputHandler.isMouseDown && ModConfig.entries.size > 1 && dragging == null) {
            dragging = this
            onRemove()
            ItemRenderer.offsetX = x - inputHandler.mouseX()
            ItemRenderer.offsetY = y - inputHandler.mouseY()
        }
        nanoVGHelper.drawRoundedRect(vg, currentX, currentY, 192f, 64f, colorAnimation.getColor(hovered, hovered && inputHandler.isMouseDown), 10f)
        enableCheckBox.draw(vg, renderX.toInt(), renderY.toInt(), inputHandler)
        if (this != dragging) ItemRenderer.drawItem(itemStack, renderX + 40, renderY)
        ignoreMetaCheckBox.draw(vg, renderX.toInt() + 88, renderY.toInt(), inputHandler)
        removeButton.draw(vg, renderX + 128, renderY, inputHandler)
        if (isHovered(inputHandler, renderX + 40, renderY, 32, 32)) {
            MainRenderer.renderText = itemStack.displayName
        } else if (isHovered(inputHandler, renderX + 128, renderY, 32, 32)) {
            MainRenderer.renderText = "Remove"
        }
    }

    fun onRemove() {
        ModConfig.entries.remove(itemEntry)
        ModConfig.itemInfos.remove(itemEntry.itemInfo)
        MainRenderer.removeQueue.add(this)
    }

}