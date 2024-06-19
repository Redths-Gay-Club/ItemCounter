package me.imtoggle.itemcounter.command

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.imtoggle.itemcounter.ItemCounter
import me.imtoggle.itemcounter.config.ModConfig
import me.imtoggle.itemcounter.util.notify
import net.minecraft.item.Item

@Command(value = ItemCounter.MODID)
class ModCommand {

    @SubCommand
    fun add() {
        val stack = mc.thePlayer.heldItem
        if (stack == null) {
            notify("You're not holding an item")
            return
        }
        val id = Item.getIdFromItem(stack.item)
        ModConfig.addItem(arrayListOf(id, stack.metadata))
        ModConfig.save()
    }

}