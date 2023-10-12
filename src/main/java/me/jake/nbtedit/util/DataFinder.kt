package me.jake.nbtedit.util

object DataFinder {
    fun getType(data: String): Any {
        if (isInteger(data)) return data.toInt()
        return if (isBoolean(data)) data.toBoolean() else data
    }

    private fun isInteger(data: String): Boolean {
        try {
            data.toInt()
        } catch (e: NumberFormatException) {
            return false
        } catch (e: NullPointerException) {
            return false
        }
        return true
    }

    private fun isBoolean(data: String): Boolean {
        return data.equals("true", ignoreCase = true) || data.equals("false", ignoreCase = true)
    }
}