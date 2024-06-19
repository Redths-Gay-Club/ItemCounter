package me.imtoggle.itemcounter.util

import cc.polyfrost.oneconfig.events.EventManager
import cc.polyfrost.oneconfig.events.event.RawMouseEvent
import cc.polyfrost.oneconfig.gui.OneConfigGui
import cc.polyfrost.oneconfig.internal.assets.Colors
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe
import cc.polyfrost.oneconfig.libs.universal.UResolution
import cc.polyfrost.oneconfig.renderer.font.Fonts
import cc.polyfrost.oneconfig.utils.InputHandler
import cc.polyfrost.oneconfig.utils.dsl.*
import me.imtoggle.itemcounter.config.MainRenderer.renderText
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.item.ItemStack
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11

object ItemRenderer {

    data class RenderInfo(val stack: ItemStack, val x: Float, val y: Float)

    private val renderInfos = ArrayList<RenderInfo?>()

    var offsetX = 0f

    var offsetY = 0f

    var shouldCheck = false

    init {
        MinecraftForge.EVENT_BUS.register(this)
        EventManager.INSTANCE.register(this)
    }

    fun drawItem(itemStack: ItemStack, x: Float, y: Float) {
        renderInfos.add(RenderInfo(itemStack, x, y))
    }

    @Subscribe
    private fun onMouse(event: RawMouseEvent) {
        if (event.button == 0 && event.state == 0) {
            if (dragging != null) {
                shouldCheck = true
            }
        }
    }

    @SubscribeEvent
    fun drawAll(e: GuiScreenEvent.DrawScreenEvent.Post) {
        val oneConfigGui = mc.currentScreen as? OneConfigGui ?: return
        val unscaleMC = 1 / UResolution.scaleFactor
        val oneUIScale = OneConfigGui.getScaleFactor() * oneConfigGui.animationScaleFactor
        val rawX = ((UResolution.windowWidth - 800 * oneUIScale) / 2f).toInt()
        val rawY = ((UResolution.windowHeight - 768 * oneUIScale) / 2f).toInt()
        GlStateManager.pushMatrix()
        GL11.glEnable(GL11.GL_SCISSOR_TEST)
        GL11.glScissor(rawX, rawY, (1024 * oneUIScale).toInt(), (696 * oneUIScale).toInt())
        GlStateManager.scale(unscaleMC * oneUIScale, unscaleMC * oneUIScale, 1.0)
        for (info in renderInfos) {
            val (itemStack, x, y) = info ?: continue
            renderItem(x, y, itemStack)
        }
        renderInfos.clear()
        GL11.glDisable(GL11.GL_SCISSOR_TEST)
        val inputHandler = InputHandler()
        nanoVG {
            if (dragging != null) {
                dragging!!.draw(this.instance, inputHandler.mouseX() + offsetX, inputHandler.mouseY() + offsetY, inputHandler)
            }
        }
        if (dragging != null) {
            renderItem(dragging!!.animationX.get() + 56, dragging!!.animationY.get() + 16, dragging!!.itemStack)
        }
        nanoVG {
            if (renderText.isNotEmpty()) {
                val textWidth = getTextWidth(renderText, 14f, Fonts.MEDIUM)
                drawRoundedRect(inputHandler.mouseX(), inputHandler.mouseY() - 46, textWidth + 32, 14f + 32, 10f, Colors.GRAY_800)
                drawText(renderText, inputHandler.mouseX() + 16, inputHandler.mouseY() - 23, Colors.WHITE_90, 14f, Fonts.MEDIUM)
                renderText = ""
            }
        }
        GlStateManager.popMatrix()
    }

    fun renderItem(x: Float, y: Float, itemStack: ItemStack) {
        GlStateManager.pushMatrix()
        GlStateManager.enableRescaleNormal()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        RenderHelper.enableGUIStandardItemLighting()
        val itemRenderer = mc.renderItem
        GlStateManager.translate(x, y, 0f)
        GlStateManager.scale(32 / 16f, 32 / 16f, 1f)
        itemRenderer.renderItemAndEffectIntoGUI(itemStack, 0, 0)
        RenderHelper.disableStandardItemLighting()
        GlStateManager.disableBlend()
        GlStateManager.disableRescaleNormal()
        GlStateManager.enableAlpha()
        GlStateManager.popMatrix()
    }

}