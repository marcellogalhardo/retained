package dev.marcellogalhardo.retained.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.AmbientLifecycleOwner
import androidx.compose.ui.platform.AmbientSavedStateRegistryOwner
import androidx.compose.ui.platform.AmbientViewModelStoreOwner
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavBackStackEntry
import androidx.savedstate.SavedStateRegistryOwner
import dev.marcellogalhardo.retained.core.InternalRetainedApi
import dev.marcellogalhardo.retained.core.RetainedEntry
import dev.marcellogalhardo.retained.core.createRetainedObjectLazy

/**
 * Returns a [Lazy] delegate to access a retained object by **default** scoped to this
 * [Composable] (e.g., [NavBackStackEntry], [Fragment] or [ComponentActivity]).
 *
 * ```
 * @Composable
 * fun MyView() {
 *     val vm by retain { ViewModel() }
 * }
 * class ViewModel(val name: String = "")
 * ```
 *
 * This property can be accessed only after the [LifecycleOwner] is ready to use Jetpack
 * [ViewModel], and access prior to that will result in [IllegalArgumentException].
 *
 * @see createRetainedObjectLazy
 */
@OptIn(InternalRetainedApi::class)
@Composable
inline fun <reified T : Any> retain(
    key: String = T::class.java.name,
    viewModelStoreOwner: ViewModelStoreOwner = AmbientViewModelStoreOwner.current,
    savedStateRegistryOwner: SavedStateRegistryOwner = AmbientSavedStateRegistryOwner.current,
    noinline getDefaultArgs: () -> Bundle = { getDefaultArgs(AmbientLifecycleOwner.current) },
    noinline createRetainedObject: (RetainedEntry) -> T
): Lazy<T> = createRetainedObjectLazy(key, { viewModelStoreOwner }, { savedStateRegistryOwner }, getDefaultArgs, createRetainedObject)

@PublishedApi
internal fun getDefaultArgs(owner: LifecycleOwner): Bundle {
    return when (owner) {
        is ComponentActivity -> owner.intent?.extras
        is Fragment -> owner.arguments
        is NavBackStackEntry -> owner.arguments
        else -> bundleOf()
    } ?: bundleOf()
}
