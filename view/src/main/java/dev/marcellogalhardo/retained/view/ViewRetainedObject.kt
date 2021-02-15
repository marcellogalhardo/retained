package dev.marcellogalhardo.retained.view

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.navigation.NavBackStackEntry
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.findViewTreeSavedStateRegistryOwner
import dev.marcellogalhardo.retained.core.InternalRetainedApi
import dev.marcellogalhardo.retained.core.RetainedEntry
import dev.marcellogalhardo.retained.core.createRetainedObjectLazy

/**
 * Returns a [Lazy] delegate to access a retained object by **default** scoped to this
 * [View] (e.g., [NavBackStackEntry], [Fragment] or [Activity]).
 *
 * ```
 * class MyView(context: Context) : View(context) {
 *     val vm by retain { ViewModel() }
 * }
 * class ViewModel(val name: String = "")
 * ```
 *
 * This property can be accessed only after the [LifecycleOwner] is ready to use Jetpack
 * [ViewModel], and access prior to that will result in [IllegalArgumentException]. TODO: right now it actually throws a NPE
 *
 * @see createRetainedObject
 */
@OptIn(InternalRetainedApi::class)
inline fun <reified T : Any> View.retain(
    key: String = id.toString(),
    noinline getViewModelStoreOwner: () -> ViewModelStoreOwner = { findViewTreeViewModelStoreOwner()!! },
    noinline getSavedStateRegistryOwner: () -> SavedStateRegistryOwner = { findViewTreeSavedStateRegistryOwner()!! },
    noinline getDefaultArgs: () -> Bundle = { findViewTreeLifecycleOwner()!!.defaultArgs },
    noinline createRetainedObject: (RetainedEntry) -> T
): Lazy<T> = createRetainedObjectLazy(key, getViewModelStoreOwner, getSavedStateRegistryOwner, getDefaultArgs, createRetainedObject)

// TODO: move this to core or a new common module to avoid the repetition
@PublishedApi
internal val LifecycleOwner.defaultArgs: Bundle
    get() = when (this) {
        is Activity -> intent?.extras
        is Fragment -> arguments
        is NavBackStackEntry -> arguments
        else -> bundleOf()
    } ?: bundleOf()
