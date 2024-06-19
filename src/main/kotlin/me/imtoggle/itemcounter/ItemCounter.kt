package me.imtoggle.itemcounter

import cc.polyfrost.oneconfig.utils.commands.CommandManager
import me.imtoggle.itemcounter.command.ModCommand
import me.imtoggle.itemcounter.config.ModConfig
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent

@Mod(modid = ItemCounter.MODID, name = ItemCounter.NAME, version = ItemCounter.VERSION, modLanguageAdapter = "cc.polyfrost.oneconfig.utils.KotlinLanguageAdapter")
object ItemCounter {
    const val MODID = "@ID@"
    const val NAME = "@NAME@"
    const val VERSION = "@VER@"

    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent) {
        ModConfig
        CommandManager.INSTANCE.registerCommand(ModCommand())
    }
}