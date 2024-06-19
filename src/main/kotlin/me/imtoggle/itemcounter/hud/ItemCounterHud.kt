package me.imtoggle.itemcounter.hud

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.hud.BasicHud
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import cc.polyfrost.oneconfig.renderer.TextRenderer
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.imtoggle.itemcounter.config.MainRenderer
import me.imtoggle.itemcounter.config.ModConfig
import me.imtoggle.itemcounter.element.ItemElement
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.GlStateManager as GL
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack


class ItemCounterHud : BasicHud(true, 1920f - 400, 1080f - 21) {

    @Slider(
        name = "Item Padding",
        min = 0F,
        max = 10F
    )
    var padding = 5

    @Slider(
        name = "Icon Padding",
        min = 0F,
        max = 10F
    )
    var iconPadding = 5

    @DualOption(
        name = "Type",
        left = "Horizontal",
        right = "Vertical"
    )
    var type = false

    @Switch(
        name = "Reversed"
    )
    var reversed = false

    @DualOption(
        name = "Text Position",
        left = "Left", right = "Right"
    )
    var alignment = true

    @Switch(
        name = "Hide When Zero"
    )
    var hideZero = true

    @Dropdown(name = "Text Type", options = ["No Shadow", "Shadow", "Full Shadow"])
    var textType = 0

    @Color(
        name = "Text Color"
    )
    var textColor = OneColor(255, 255, 255)

    @Transient
    private var actualWidth = 0F

    @Transient
    private var actualHeight = 0F

    @Transient
    private var size = 0

    override fun draw(matrices: UMatrixStack?, x: Float, y: Float, scale: Float, example: Boolean) {
        draw(x, y, scale, example)
    }

    private val shownItems: List<ItemElement>
        get() = arrayListOf<ItemElement>().apply {
            MainRenderer.elements.forEach {
                if (it.itemEntry.enabled) add(it)
            }
            if (reversed) reverse()
        }

    private fun getItemAmount(element: ItemElement): Int {
        val itemList = mc.thePlayer.inventory.mainInventory.toMutableList()
        return itemList.filter {
            it?.item == element.itemStack.item && (element.itemEntry.ignoreMetaData || it?.metadata == element.itemStack.metadata)
        }.sumOf {
            it.stackSize
        }
    }

    private val IInventory.itemStackList: List<ItemStack>
        get() = (0..26).map { index -> getStackInSlot(index) }

    private fun draw(x: Float, y: Float, scale: Float, example: Boolean) {
        val itemAmountMap: Map<ItemElement, Int> = shownItems.associateWith { getItemAmount(it) }
        val iconSize = 16f
        val offset = iconSize + padding
        val longestWidth = itemAmountMap.maxOfOrNull { (_, amount) ->
            mc.fontRendererObj.getStringWidth(amount.toString())
        } ?: 0
        var lastWidth = 0

        size = 0

        GL.pushMatrix()
        GL.scale(scale, scale, 1f)
        GL.translate(x / scale, y / scale, 0f)
        for ((element, amount) in itemAmountMap) {
            if (hideZero && amount == 0 && !example) continue
            val text = amount.toString()
            val textWidth = mc.fontRendererObj.getStringWidth(text)
            val itemY = if (type) size * offset else 0

            val iconX = when (alignment) {
                false -> iconPadding + if (type) longestWidth else lastWidth + textWidth
                true -> lastWidth
            }

            val textX = when (alignment) {
                false -> if (type) longestWidth - textWidth else lastWidth
                true -> iconSize + if (type) iconPadding else lastWidth + iconPadding
            }

            RenderHelper.enableGUIStandardItemLighting()
            mc.renderItem.zLevel = 200f
            try {
                mc.renderItem.renderItemAndEffectIntoGUI(element.itemStack, iconX, itemY.toInt())
                mc.renderItem.renderItemOverlayIntoGUI(mc.fontRendererObj, element.itemStack, 0, 0, "")
                RenderHelper.disableStandardItemLighting()
                TextRenderer.drawScaledString(
                    text,
                    textX.toFloat(),
                    itemY.toFloat() + mc.fontRendererObj.FONT_HEIGHT / 2f,
                    textColor.rgb,
                    TextRenderer.TextType.toType(textType),
                    1f
                )
            } finally {
                mc.renderItem.zLevel = 0f
            }
            size++
            if (!type) lastWidth += offset.toInt() + textWidth + iconPadding
        }
        GL.popMatrix()
        actualWidth = if (type) longestWidth + iconPadding + iconSize else lastWidth.toFloat() - padding
        actualHeight = if (type) size * offset - padding else 16f
    }

    override fun getWidth(scale: Float, example: Boolean): Float = actualWidth * scale

    override fun getHeight(scale: Float, example: Boolean): Float = actualHeight * scale

    override fun shouldShow(): Boolean = super.shouldShow()
            && (!hideZero || (shownItems.maxOfOrNull { getItemAmount(it) } ?: 0) > 0) && ModConfig.entries.isNotEmpty()
}