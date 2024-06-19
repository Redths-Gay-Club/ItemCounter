package me.imtoggle.itemcounter.element

import cc.polyfrost.oneconfig.config.elements.BasicOption
import cc.polyfrost.oneconfig.gui.animations.Animation
import cc.polyfrost.oneconfig.gui.animations.ColorAnimation
import cc.polyfrost.oneconfig.gui.animations.EaseInOutQuad
import cc.polyfrost.oneconfig.internal.assets.Colors
import cc.polyfrost.oneconfig.internal.assets.SVGs
import cc.polyfrost.oneconfig.utils.InputHandler
import cc.polyfrost.oneconfig.utils.color.ColorPalette
import cc.polyfrost.oneconfig.utils.dsl.*
import me.imtoggle.itemcounter.config.MainRenderer
import java.lang.reflect.Field

class SimpleCheckBox(field: Field, parent: Any, name: String): BasicOption(field, parent, name, "", "", "", 1) {

    private val colorAnimation = ColorAnimation(ColorPalette.SECONDARY)
    private var alphaAnimation: Animation = EaseInOutQuad(0, 1f, 0f, get() as Boolean)

    private fun colorWithAlpha(color: Int): Int {
        val alpha = (alphaAnimation.get() * 255f).toInt()
        val rgb = color and 0xFFFFFF
        val shiftedAlpha = alpha shl 24
        return rgb or shiftedAlpha
    }

    override fun getHeight(): Int = 0

    override fun draw(vg: Long, x: Int, y: Int, inputHandler: InputHandler) {
        val hover = inputHandler.isAreaHovered(x.toFloat(), y + 4f, 24f, 24f)
        val clicked = inputHandler.isClicked && hover
        val pressed = inputHandler.isMouseDown && hover
        var toggled = get() as Boolean
        if (clicked) {
            toggled = !toggled
            alphaAnimation = EaseInOutQuad(100, 1f, 0f, toggled)
            set(toggled)
        }

        val draw = VG(vg)

        draw.drawRoundedRect(
            x, y + 4, 24, 24,
            radius = 6,
            color = colorAnimation.getColor(hover, pressed)
        )
        draw.drawHollowRoundedRect(
            x, y + 4, 23.5f, 23.5f,
            radius = 6,
            color = Colors.GRAY_300,
            thickness = 1
        )
        draw.drawRoundedRect(
            x, y + 4, 24, 24,
            radius = 6,
            color = colorWithAlpha(Colors.PRIMARY_500)
        )
        draw.drawSVG(
            svg = SVGs.CHECKBOX_TICK,
            x, y + 4, 24, 24,
            color = colorWithAlpha(0xFFFFFF)
        )

        if (toggled && hover) draw.drawHollowRoundedRect(
            x - 1, y + 3, 24, 24,
            radius = 6,
            color = Colors.PRIMARY_600,
            thickness = 2
        )

        if (hover && name.isNotEmpty()) {
            MainRenderer.renderText = name
        }
    }

}