package fr.mathano.livingdex.ui.components

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import fr.mathano.livingdex.data.AppLanguage

@Composable
fun livingDexString(
    @StringRes id: Int,
    vararg formatArgs: Any,
): String {
    val context = LocalContext.current
    AppLanguage.current()
    return AppLanguage.string(context, id, *formatArgs)
}
