package dev.marcellogalhardo.retained.compose

import android.app.Activity
import android.os.Bundle
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
import dev.marcellogalhardo.retained.core.createRetainedObject

/**
 * Returns a [Lazy] delegate to access a retained object by **default** scoped to this
 * [Composable] (e.g., [NavBackStackEntry], [Fragment] or [Activity]).
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
 * @see createRetainedObject
 */
@OptIn(InternalRetainedApi::class)
@Composable
inline fun <reified T : Any> retain(
    key: String = T::class.java.name,
    viewModelStoreOwner: ViewModelStoreOwner = AmbientViewModelStoreOwner.current,
    savedStateRegistryOwner: SavedStateRegistryOwner = AmbientSavedStateRegistryOwner.current,
    defaultArgs: Bundle = AmbientLifecycleOwner.current.defaultArgs,
    noinline createRetainedObject: (RetainedEntry) -> T
): T = createRetainedObject(key, viewModelStoreOwner, savedStateRegistryOwner, defaultArgs, createRetainedObject)

@PublishedApi
internal val LifecycleOwner.defaultArgs: Bundle
    get() = when (this) {
        is Activity -> intent?.extras
        is Fragment -> arguments
        is NavBackStackEntry -> arguments
        else -> bundleOf()
    } ?: bundleOf()
