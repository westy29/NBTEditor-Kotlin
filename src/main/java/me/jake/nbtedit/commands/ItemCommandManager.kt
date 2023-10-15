package me.jake.nbtedit.commands

import io.github.bananapuncher714.nbteditor.NBTEditor
import me.jake.nbtedit.util.ChatUtils
import me.jake.nbtedit.util.DataFinder
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

@SuppressWarnings("NullableProblems")
class ItemCommandManager : CommandExecutor, TabCompleter {

    private val commands: ArrayList<SubCommand> = ArrayList()

    init {
        commands.add(ItemView())
        commands.add(ItemSet())
        commands.add(ItemCheck())
    }

    abstract class SubCommand {
        abstract fun getName(): String
        abstract fun getDescription(): String
        abstract fun getSyntax(): String
        abstract fun getPermission(): String
        abstract fun execute(player: Player, vararg args: String)
    }

    class ItemView : SubCommand() {
        override fun getName(): String = "view"
        override fun getDescription(): String = "View the value of specified NBT tag."
        override fun getSyntax(): String = "/nbtitem view (tag)"
        override fun getPermission(): String = "nbtedit.admin"

        override fun execute(player: Player, vararg args: String) {
            val item = player.inventory.itemInMainHand
            if (item.type == Material.AIR) {
                ChatUtils.tell(player, "You must have an item in your hand.")
                return
            }
            try {
                val tag = args[1]
                val value: Any = when {
                    NBTEditor.contains(item, tag) -> {
                        val sVal = NBTEditor.getString(item, tag)
                        val iVal = NBTEditor.getInt(item, tag)
                        val bVal = NBTEditor.getBoolean(item, tag)
                        sVal ?: if (iVal == 0) bVal else iVal
                    }

                    else -> {
                        ChatUtils.tell(player, "The item doesn't contain tag: &c$tag")
                        return
                    }
                }
                ChatUtils.tell(player, "The value of '&a$tag&f' is '&a$value&f'.")
            } catch (indexException: IndexOutOfBoundsException) {
                ChatUtils.tell(player, "Please use correct Syntax.")
                ChatUtils.tell(player, getSyntax())
            } catch (e: Exception) {
                ChatUtils.tell(player, "An error occurred.")
            }
        }
    }

    class ItemSet : SubCommand() {
        override fun getName(): String = "set"
        override fun getDescription(): String = "Set the NBT tag of an Item"
        override fun getSyntax(): String = "/nbtitem set (tag) (value)"
        override fun getPermission(): String = "nbtedit.admin"

        override fun execute(player: Player, vararg args: String) {
            val item = player.inventory.itemInMainHand
            if (item.type == Material.AIR) {
                ChatUtils.tell(player, "You must have an item in your hand.")
                return
            }
            try {
                val tag = args[1]
                val valText = args[2]
                val type = DataFinder.getType(valText)
                player.inventory.setItemInMainHand(NBTEditor.set(item, type, tag))
                ChatUtils.tell(player, "Set '&a$tag&f' to '&a$type&f'.")
            } catch (indexException: IndexOutOfBoundsException) {
                ChatUtils.tell(player, "Please use correct Syntax.")
                ChatUtils.tell(player, getSyntax())
            } catch (e: Exception) {
                ChatUtils.tell(player, "An error occurred")
            }
        }
    }

    class ItemCheck : SubCommand() {
        override fun getName(): String = "check"
        override fun getDescription(): String = "Check an Item's NBT Tags."
        override fun getSyntax(): String = "/nbtitem check (tag)"
        override fun getPermission(): String = "nbtedit.admin"

        override fun execute(player: Player, vararg args: String) {
            val item: ItemStack = player.inventory.itemInMainHand
            if (item.type == Material.AIR) {
                ChatUtils.tell(player, "You must have an item in your hand.")
                return
            }
            var name: String = item.type.name
            if (item.itemMeta?.hasDisplayName() == true) {
                name = item.itemMeta?.displayName.toString()
            }
            try {
                val tag = args[1]
                val hasTag: Boolean = NBTEditor.contains(item, tag)
                val response = if (hasTag) "has" else "does not have"
                ChatUtils.tell(player, "$name&f $response the tag: &a$tag")
            } catch (var8: IndexOutOfBoundsException) {
                ChatUtils.tell(player, "Please use correct Syntax.")
                ChatUtils.tell(player, getSyntax())
            } catch (var9: Exception) {
                ChatUtils.tell(player, "An error occurred.")
            }
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            return true
        }

        if (args.isNotEmpty()) {
            val commandName = args[0].lowercase(Locale.getDefault())
            val matchingCommand = commands.find { it.getName() == commandName }

            if (matchingCommand != null) {
                if (sender.hasPermission(matchingCommand.getPermission())) {
                    matchingCommand.execute(sender, *args)
                } else {
                    ChatUtils.tell(sender, "You don't have permission.")
                }
                return true
            }
        }

        ChatUtils.tell(sender, "Invalid Syntax.")
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<String>
    ): MutableList<String> {
        val tabComplete = mutableListOf<String>()

        when (args.size) {
            1 -> {
                val arg1 = args[0].lowercase(Locale.getDefault())
                tabComplete.addAll(commands.filter { it.getName().startsWith(arg1) }.map { it.getName() })
            }

            2 -> {
                val arg2 = args[1].lowercase(Locale.getDefault())
                if (sender is Player) {
                    tabComplete.addAll(NBTEditor.getKeys(sender.inventory.itemInMainHand)
                        .filter { it.lowercase(Locale.getDefault()).startsWith(arg2) })
                }
            }
        }

        return tabComplete
    }
}