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
import org.bukkit.inventory.meta.ItemMeta
import java.util.*

@SuppressWarnings("NullableProblems")
class ItemCommandManager : CommandExecutor, TabCompleter {

    private val commands: ArrayList<SubCommand> = ArrayList()

    init {
        commands.add(ItemView())
        commands.add(ItemSet())
        commands.add(ItemCheck())
    }

    class ItemView : SubCommand() {
        override fun getName(): String {
            return "view"
        }

        override fun getDescription(): String {
            return "View the value of specified NBT tag."
        }

        override fun getSyntax(): String {
            return "/nbtitem view (tag)"
        }

        override fun getPermission(): String {
            return "nbtedit.admin"
        }

        override val name: String
            get() = "view"
        override val description: String
            get() = "View the value of specified NBT tag."
        override val syntax: String
            get() = "/nbtitem view (tag)"
        override val permission: String
            get() = "nbtedit.admin"


        override fun execute(player: Player, vararg args: String) {
            val item = player.inventory.itemInMainHand
            if (item.type == Material.AIR) {
                ChatUtils.tell(player, "You must have an item in your hand.")
                return
            }
            try {
                val tag = args[1]
                val value: Any
                if (NBTEditor.contains(item, tag)) {
                    val sVal = NBTEditor.getString(item, tag)
                    val iVal = NBTEditor.getInt(item, tag)
                    val bVal = NBTEditor.getBoolean(item, tag)
                    value = sVal ?: if (iVal == 0) {
                        bVal
                    } else {
                        iVal
                    }
                    ChatUtils.tell(player, "The value of '&a$tag&f' is '&a$value&f'.")
                } else {
                    ChatUtils.tell(player, "The item doesn't contain tag: &c$tag")
                }
            } catch (indexException: IndexOutOfBoundsException) {
                ChatUtils.tell(player, "Please use correct Syntax.")
                ChatUtils.tell(player, getSyntax())
            } catch (e: Exception) {
                ChatUtils.tell(player, "An error occurred.")
            }
        }
    }


    class ItemSet : SubCommand() {
        override fun getName(): String {
            return "set"
        }

        override fun getDescription(): String {
            return "Set the NBT tag of an Item"
        }

        override fun getSyntax(): String {
            return "/nbtitem set (tag) (value)"
        }

        override fun getPermission(): String {
            return "nbtedit.admin"
        }

        override val name: String
            get() = "set"
        override val description: String
            get() = "Set the NBT tag of an Item"
        override val syntax: String
            get() = "/nbtitem set (tag) (value)"
        override val permission: String
            get() = "nbtedit.admin"


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
        override fun getName(): String {
            return "check"
        }

        override fun getDescription(): String {
            return "Check an Item's NBT Tags."
        }

        override fun getSyntax(): String {
            return "/nbtitem check (tag)"
        }

        override fun getPermission(): String {
            return "nbtedit.admin"
        }

        override val name: String
            get() = "check"
        override val description: String
            get() = "Check an Item's NBT Tags."
        override val syntax: String
            get() = "/nbtitem check (tag)"
        override val permission: String
            get() = "nbtedit.admin"

        override fun execute(player: Player, vararg args: String) {
            val item: ItemStack = player.inventory.itemInMainHand
            if (item.type == Material.AIR) {
                ChatUtils.tell(player, "You must have an item in your hand.")
            } else {
                var name: String = item.type.name
                if (Objects.requireNonNull<ItemMeta>(item.itemMeta).hasDisplayName()) {
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
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {

        if (sender is Player) {

            if (args.isNotEmpty()) {
                for (cmd in commands) {
                    if (args[0].lowercase(Locale.getDefault()) == cmd.getName()) {
                        if (sender.hasPermission(cmd.getPermission())) {
                            cmd.execute(sender, *args)
                        } else {
                            ChatUtils.tell(sender, "You don't have permission.")
                        }
                        return true
                    }
                }
            }
            ChatUtils.tell(sender, "Invalid Syntax.")
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): MutableList<String> {

        val tabComplete = ArrayList<String>()

        if (args.size == 1) {
            for (cmds in commands) {
                if (cmds.getName().startsWith(args[0].lowercase(Locale.getDefault()))) tabComplete.add(cmds.getName())
            }
        }

        if (args.size == 2) {
            for (key in NBTEditor.getKeys((sender as Player).inventory.itemInMainHand)) {
                if (key.lowercase(Locale.getDefault()).startsWith(args[1].lowercase(Locale.getDefault()))) tabComplete.add(key)
            }
        }

        return tabComplete
    }
}