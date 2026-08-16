package dev.marcellogalhardo.retained.compose

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dev.marcellogalhardo.retained.core.ExperimentalRetainedApi
import dev.marcellogalhardo.retained.core.RetainedEntry

/**
 * Returns an existing retained instance of [T] scoped to this [ComponentActivity]:
 *
 * ```
 * @Composable
 * fun MyComposable() {
 *     val vm = retainInActivity { ViewModel() }
 * }
 * class ViewModel(val name: String = "")
 * ```
 *
 * @see retain
 */
@ExperimentalRetainedApi
@Composable
public inline fun <reified T : Any> retainInActivity(
    key: String = T::class.qualifiedName ?: T::class.simpleName ?: "RetainedInstance",
    noinline instantiate: @DisallowComposableCalls (RetainedEntry) -> T,
): T {
    val context = LocalContext.current
    return retain(
        key = key,
        owner = remember { context.findActivity() },
        instantiate = instantiate,
    )
}

@PublishedApi
internal tailrec fun Context.findActivity(): ComponentActivity =
    when (this) {
        is ComponentActivity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> error("Your view is not attached to an activity.")
    }
