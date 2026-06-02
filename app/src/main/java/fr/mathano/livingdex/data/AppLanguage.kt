package fr.mathano.livingdex.data

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.annotation.StringRes
import java.util.Locale

object AppLanguage {
    private const val PREFERENCES_NAME = "livingdex_preferences"
    private const val LANGUAGE_KEY = "language"

    private var preferences: SharedPreferences? = null
    private var appContext: Context? = null
    private var selectedLanguage = androidx.compose.runtime.mutableStateOf("en")

    fun init(context: Context) {
        appContext = context.applicationContext
        preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

        if (!preferencesContainsLanguage()) {
            set(Locale.getDefault().language.toSupportedLanguage())
        } else {
            selectedLanguage.value = preferences?.getString(LANGUAGE_KEY, null) ?: "en"
        }
    }

    fun current(): String =
        selectedLanguage.value

    fun pokeApiLanguage(): String {
        return when (current()) {
            "ja" -> "ja-hrkt"
            "zh-Hans" -> "zh-hans"
            else -> current()
        }
    }

    fun set(language: String) {
        val supportedLanguage = language.toSupportedLanguage()
        selectedLanguage.value = supportedLanguage

        preferences
            ?.edit()
            ?.putString(LANGUAGE_KEY, supportedLanguage)
            ?.apply()
    }

    fun string(
        @StringRes id: Int,
        vararg formatArgs: Any,
    ): String {
        val context = appContext ?: return ""
        return string(context, id, *formatArgs)
    }

    fun string(
        context: Context,
        @StringRes id: Int,
        vararg formatArgs: Any,
    ): String {
        val localizedContext = context.localizedContext(current())
        return if (formatArgs.isEmpty()) {
            localizedContext.getString(id)
        } else {
            localizedContext.getString(id, *formatArgs)
        }
    }

    private fun preferencesContainsLanguage(): Boolean =
        preferences?.contains(LANGUAGE_KEY) == true

    private fun String.toSupportedLanguage(): String {
        return when (this) {
            "fr", "en", "es", "it", "ru", "ja" -> this
            "zh", "zh-Hans", "zh-CN" -> "zh-Hans"
            else -> "en"
        }
    }

    private fun Context.localizedContext(language: String): Context {
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(Locale.forLanguageTag(language))
        return createConfigurationContext(configuration)
    }
}
