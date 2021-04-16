package dev.marcellogalhardo.retained.compose

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
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
 *     val vm = retain { ViewModel() }
 * }
 * class ViewModel(val name: String = "")
 * ```
 *
 * @see createRetainedObject
 */
@OptIn(InternalRetainedApi::class)
@Composable
inline fun <reified T : Any> retain(
    key: String = T::class.java.name,
    viewModelStoreOwner: ViewModelStoreOwner = requireNotNull(LocalViewModelStoreOwner.current),
    savedStateRegistryOwner: SavedStateRegistryOwner = LocalSavedStateRegistryOwner.current,
    defaultArgs: Bundle = LocalLifecycleOwner.current.defaultArgs,
    noinline createRetainedObject: (RetainedEntry) -> T
): T = createRetainedObject(key, T::class, viewModelStoreOwner, savedStateRegistryOwner, defaultArgs, createRetainedObject)

/**
 * Returns a [Lazy] delegate to access a retained object by **default** scoped to this
 * [ComponentActivity]:
 *
 * ```
 * @Composable
 * fun MyView() {
 *     val vm = retainInActivity { ViewModel() }
 * }
 * class ViewModel(val name: String = "")
 * ```
 *
 * @see createRetainedObject
 */
@OptIn(InternalRetainedApi::class)
@Composable
inline fun <reified T : Any> retainInActivity(
    key: String = T::class.java.name,
    defaultArgs: Bundle = getActivity().intent?.extras ?: bundleOf(),
    noinline createRetainedObject: (RetainedEntry) -> T
): T = retain(key, getActivity(), getActivity(), defaultArgs, createRetainedObject)

@PublishedApi
internal val LifecycleOwner.defaultArgs: Bundle
    get() = when (this) {
        is Activity -> intent?.extras
        is Fragment -> arguments
        is NavBackStackEntry -> arguments
        else -> bundleOf()
    } ?: bundleOf()

@PublishedApi
@Composable
internal fun getActivity(): ComponentActivity = LocalContext.current as ComponentActivity
