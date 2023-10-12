package me.jake.nbtedit.commands

import org.bukkit.entity.Player

abstract class SubCommand {
    internal abstract val name: String?
    internal abstract val description: String?
    internal abstract val syntax: String?
    internal abstract val permission: String?

    abstract fun getName(): String
    abstract fun getDescription(): String
    abstract fun getSyntax(): String
    abstract fun getPermission(): String
    abstract fun execute(player: Player, vararg args: String)
}