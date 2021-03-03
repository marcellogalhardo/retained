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
 * This property can be accessed only after this [View] is attached, i.e. after
 * [View.onAttachedToWindow], and access prior to that will result in [IllegalStateException].
 *
 * @see createRetainedObject
 */
@OptIn(InternalRetainedApi::class)
inline fun <reified T : Any> View.retain(
    key: String = id.toString(),
    noinline getViewModelStoreOwner: () -> ViewModelStoreOwner = { viewModelStoreOwnerOrThrow() },
    noinline getSavedStateRegistryOwner: () -> SavedStateRegistryOwner = { findViewTreeSavedStateRegistryOwner()!! },
    noinline getDefaultArgs: () -> Bundle = { findViewTreeLifecycleOwner()!!.defaultArgs },
    noinline createRetainedObject: (RetainedEntry) -> T
): Lazy<T> = createRetainedObjectLazy(key, getViewModelStoreOwner, getSavedStateRegistryOwner, getDefaultArgs, createRetainedObject)

@PublishedApi
internal fun View.viewModelStoreOwnerOrThrow() = findViewTreeViewModelStoreOwner()
    ?: throw IllegalStateException("Your view is not yet attached, and thus its ViewModelStoreOwner is null.")

// TODO: move this to core or a new common module to avoid the repetition
@PublishedApi
internal val LifecycleOwner.defaultArgs: Bundle
    get() = when (this) {
        is Activity -> intent?.extras
        is Fragment -> arguments
        is NavBackStackEntry -> arguments
        else -> bundleOf()
    } ?: bundleOf()
