package dev.marcellogalhardo.retained.compose

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.marcellogalhardo.retained.core.ExperimentalRetainedApi
import dev.marcellogalhardo.retained.core.InternalRetainedApi
import dev.marcellogalhardo.retained.core.RetainedEntry
import dev.marcellogalhardo.retained.core.internal.RetainedViewModel
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
@ExperimentalRetainedApi
@Composable
public inline fun <reified T : Any> retain(
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    },
    key: String = T::class.java.name,
    noinline instantiate: (RetainedEntry) -> T,
): T {
    val viewModel = viewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        key = key,
        initializer = {
            RetainedViewModel(
                key = key,
                retainedClass = T::class,
                savedStateHandle = createSavedStateHandle(),
                createRetainedObject = instantiate,
            )
        },
    )
    return viewModel.retainedInstance as T
}
