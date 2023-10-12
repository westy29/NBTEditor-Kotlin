@file:Suppress("SameParameterValue")

package me.jake.nbtedit.util

import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.regex.Pattern

object ChatUtils {
    fun tell(player: Player, message: String) {
        player.sendMessage(processString("&d&lNBTeditor &7| &f $message"))
    }

    private fun processString(s: String): String {
        return ChatColor.translateAlternateColorCodes('&', translateHexColorCodes("#", "", s))
    }

    private fun translateHexColorCodes(startTag: String, endTag: String, message: String): String {
        val hexPattern = Pattern.compile("$startTag([A-Fa-f0-9]{6})$endTag")
        val matcher = hexPattern.matcher(message)
        val buffer = StringBuffer(message.length + 4 * 8)
        while (matcher.find()) {
            val group = matcher.group(1)
            matcher.appendReplacement(
                buffer, ChatColor.COLOR_CHAR.toString() + "x"
                        + ChatColor.COLOR_CHAR + group[0] + ChatColor.COLOR_CHAR + group[1]
                        + ChatColor.COLOR_CHAR + group[2] + ChatColor.COLOR_CHAR + group[3]
                        + ChatColor.COLOR_CHAR + group[4] + ChatColor.COLOR_CHAR + group[5]
            )
        }
        return matcher.appendTail(buffer).toString()
    }
}