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
import net.minecraft.client.renderer.GlStateManager as GL
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
        GL.pushMatrix()
        GL11.glEnable(GL11.GL_SCISSOR_TEST)
        GL11.glScissor(rawX, rawY, (1024 * oneUIScale).toInt(), (696 * oneUIScale).toInt())
        GL.scale(unscaleMC * oneUIScale, unscaleMC * oneUIScale, 1.0)
        for (info in renderInfos) {
            val (itemStack, x, y) = info ?: continue
            renderItem(x, y, itemStack)
        }
        renderInfos.clear()
        GL11.glDisable(GL11.GL_SCISSOR_TEST)
        val inputHandler = InputHandler()
        nanoVG {
            if (dragging != null) {
                translate(inputHandler.mouseX() * (1 - oneUIScale), inputHandler.mouseY() * (1 - oneUIScale))
                scale(oneUIScale, oneUIScale)
                dragging!!.draw(this.instance, inputHandler.mouseX() + offsetX, inputHandler.mouseY() + offsetY, inputHandler)
            }
        }
        if (dragging != null) {
            GL.pushMatrix()
            GL.scale(1 / oneUIScale, 1 / oneUIScale, 1f)
            GL.translate(inputHandler.mouseX() * (1 - oneUIScale), inputHandler.mouseY() * (1 - oneUIScale), 0f)
            GL.scale(oneUIScale, oneUIScale, 1f)
            renderItem(inputHandler.mouseX() + offsetX + 56, inputHandler.mouseY() + offsetY + 16, dragging!!.itemStack)
            GL.popMatrix()
        }
        nanoVG {
            if (renderText.isNotEmpty()) {
                val textWidth = getTextWidth(renderText, 14f, Fonts.MEDIUM)
                translate(inputHandler.mouseX(), inputHandler.mouseY())
                scale(oneUIScale, oneUIScale)
                drawRoundedRect(0,  -46, textWidth + 32, 14f + 32, 10f, Colors.GRAY_800)
                drawText(renderText, 16, -23, Colors.WHITE_90, 14f, Fonts.MEDIUM)
                renderText = ""
            }
        }
        GL.popMatrix()
    }

    fun renderItem(x: Float, y: Float, itemStack: ItemStack) {
        GL.pushMatrix()
        GL.enableRescaleNormal()
        GL.enableBlend()
        GL.tryBlendFuncSeparate(770, 771, 1, 0)
        GL.color(1.0f, 1.0f, 1.0f, 1.0f)
        RenderHelper.enableGUIStandardItemLighting()
        val itemRenderer = mc.renderItem
        GL.translate(x, y, 0f)
        GL.scale(32 / 16f, 32 / 16f, 1f)
        itemRenderer.renderItemAndEffectIntoGUI(itemStack, 0, 0)
        RenderHelper.disableStandardItemLighting()
        GL.disableBlend()
        GL.disableRescaleNormal()
        GL.enableAlpha()
        GL.popMatrix()
    }

}