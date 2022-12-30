package dev.marcellogalhardo.retained.compose

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import dev.marcellogalhardo.retained.core.ExperimentalRetainedApi
import dev.marcellogalhardo.retained.core.InternalRetainedApi
import dev.marcellogalhardo.retained.core.RetainedEntry
import dev.marcellogalhardo.retained.core.retain

/**
 * Returns an existing retained instance of [T] or creates a new one in the scope (ie, [ComponentActivity]).
 *
 * ```
 * @Composable
 * fun MyComposable() {
 *     val vm = retain { ViewModel() }
 * }
 * class ViewModel(val name: String = "")
 * ```
 *
 * @see retain
 */
@OptIn(InternalRetainedApi::class)
@Composable
public inline fun <reified T : Any> retain(
    owner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    },
    key: String = T::class.java.name,
    noinline instantiate: (RetainedEntry) -> T,
): T = remember(key1 = key) {
    retain(
        key = key,
        findOwner = { owner },
        instantiate = instantiate,
    ).value
}

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
    owner: ViewModelStoreOwner?,
    key: String = T::class.java.name,
    noinline instantiate: (RetainedEntry) -> T,
): T = retain(
    key = key,
    owner = if (owner != null) {
        owner
    } else {
        val context = LocalContext.current
        remember { context.findActivity() }
    },
    instantiate = instantiate,
)

@PublishedApi
internal tailrec fun Context.findActivity(): ComponentActivity = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> error("Your view is not attached to an activity.")
}
