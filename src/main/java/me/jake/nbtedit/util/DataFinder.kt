package me.jake.nbtedit.util

object DataFinder {
    fun getType(data: String): Any {
        return when {
            isInteger(data) -> data.toInt()
            isBoolean(data) -> data.toBoolean()
            else -> data
        }
    }

    private fun isInteger(data: String): Boolean {
        return try {
            data.toInt()
            true
        } catch (e: NumberFormatException) {
            false
        } catch (e: NullPointerException) {
            false
        }
    }

    private fun isBoolean(data: String): Boolean {
        return data.equals("true", ignoreCase = true) || data.equals("false", ignoreCase = true)
    }
}