package fr.mathano.livingdex.data

import android.content.Context
import android.content.SharedPreferences
import java.util.Locale

object AppLanguage {
    private const val PREFERENCES_NAME = "livingdex_preferences"
    private const val LANGUAGE_KEY = "language"

    private var preferences: SharedPreferences? = null

    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

        if (!preferencesContainsLanguage()) {
            set(Locale.getDefault().language.toSupportedLanguage())
        }
    }

    fun current(): String =
        preferences?.getString(LANGUAGE_KEY, null) ?: Locale.getDefault().language.toSupportedLanguage()

    fun set(language: String) {
        preferences
            ?.edit()
            ?.putString(LANGUAGE_KEY, language)
            ?.apply()
    }

    private fun preferencesContainsLanguage(): Boolean =
        preferences?.contains(LANGUAGE_KEY) == true

    private fun String.toSupportedLanguage(): String {
        return when (this) {
            "fr", "en", "es", "it", "ru", "ja" -> this
            "zh" -> "zh-Hans"
            else -> "en"
        }
    }
}
