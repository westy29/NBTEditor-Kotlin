package me.jake.nbtedit

import me.jake.nbtedit.commands.ItemCommandManager
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

@Suppress("unused")
class NBTedit : JavaPlugin() {
    override fun onEnable() {
        Objects.requireNonNull(getCommand("nbtedit"))?.setExecutor(ItemCommandManager())
        // setting the executor for a command "nbtedit" in the plugin
    }
}