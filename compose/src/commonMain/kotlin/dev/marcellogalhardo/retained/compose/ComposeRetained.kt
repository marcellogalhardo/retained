package dev.marcellogalhardo.retained.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import dev.marcellogalhardo.retained.core.InternalRetainedApi
import dev.marcellogalhardo.retained.core.RetainedEntry
import dev.marcellogalhardo.retained.core.retain

/**
 * Returns an existing retained instance of [T] or creates a new one in the scope (ie, [ViewModelStoreOwner]).
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
    owner: ViewModelStoreOwner =
        checkNotNull(LocalViewModelStoreOwner.current) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        },
    key: String = T::class.qualifiedName ?: T::class.simpleName ?: "RetainedInstance",
    noinline instantiate: @DisallowComposableCalls (RetainedEntry) -> T,
): T =
    remember(key1 = key) {
        retain(
            key = key,
            findOwner = { owner },
            instantiate = instantiate,
        ).value
    }
