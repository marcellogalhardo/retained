package dev.marcellogalhardo.retained.view

import android.app.Activity
import android.content.ContextWrapper
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.navigation.NavBackStackEntry
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.findViewTreeSavedStateRegistryOwner
import dev.marcellogalhardo.retained.core.ExperimentalRetainedApi
import dev.marcellogalhardo.retained.core.InternalRetainedApi
import dev.marcellogalhardo.retained.core.Retained
import dev.marcellogalhardo.retained.core.RetainedEntry
import dev.marcellogalhardo.retained.core.retain

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
 * @see retain
 */
@OptIn(InternalRetainedApi::class)
@ExperimentalRetainedApi
public inline fun <reified T : Any> View.retain(
    key: String = id.toString(),
    noinline findViewModelStoreOwner: () -> ViewModelStoreOwner = { findViewModelStoreOwnerOrThrow() },
    noinline findSavedStateRegistryOwner: () -> SavedStateRegistryOwner = { findViewTreeSavedStateRegistryOwner()!! },
    noinline instantiate: (RetainedEntry) -> T
): Retained<T> = retain(key, findViewModelStoreOwner, findSavedStateRegistryOwner, { findViewTreeLifecycleOwner()!!.defaultArgs }, instantiate)

/**
 * Returns a [Lazy] delegate to access a retained object by **default** scoped to the
 * [FragmentActivity]:
 *
 * ```
 * class MyView(context: Context) : View(context) {
 *     val vm by retainInActivity { ViewModel() }
 * }
 * class ViewModel(val name: String = "")
 * ```
 *
 * This property can be accessed as soon as this [View] is instantiated.
 *
 * @see retain
 */
@OptIn(InternalRetainedApi::class)
@ExperimentalRetainedApi
public inline fun <reified T : Any> View.retainInActivity(
    key: String = id.toString(),
    activity: FragmentActivity = findActivity(),
    noinline instantiate: (RetainedEntry) -> T
): Retained<T> = retain(key, { activity }, { activity }, { activity.intent?.extras }, instantiate)

@PublishedApi
internal fun View.findViewModelStoreOwnerOrThrow(): ViewModelStoreOwner {
    return findViewTreeViewModelStoreOwner()
        ?: error("Your view is not yet attached, and thus its ViewModelStoreOwner is null.")
}

@PublishedApi
internal fun View.findActivity(): FragmentActivity {
    var currentContext = context
    while (currentContext is ContextWrapper) {
        if (currentContext is FragmentActivity) return currentContext
        currentContext = (context as ContextWrapper).baseContext
    }
    error("Your view is not attached to an activity.")
}

@PublishedApi
internal val LifecycleOwner.defaultArgs: Bundle
    get() = when (this) {
        is Activity -> intent?.extras
        is Fragment -> arguments
        is NavBackStackEntry -> arguments
        else -> bundleOf()
    } ?: bundleOf()
