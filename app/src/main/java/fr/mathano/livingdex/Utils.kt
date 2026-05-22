package fr.mathano.livingdex

import java.util.logging.Logger

class Utils {
    companion object {
        val logger: Logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
    }

    fun log(): Logger {
        return logger
    }
}


fun String.toDisplayName(): String {
    return split("-").joinToString(" ") { word ->
        word.replaceFirstChar { firstChar ->
            if (firstChar.isLowerCase()) firstChar.titlecase() else firstChar.toString()
        }
    }
}