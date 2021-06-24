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
import dev.marcellogalhardo.retained.core.GetDefaultArgs
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
 * @see initializer
 */
@OptIn(InternalRetainedApi::class)
public inline fun <reified T : Any> View.retain(
    key: String = id.toString(),
    noinline getViewModelStoreOwner: () -> ViewModelStoreOwner = { findViewModelStoreOwnerOrThrow() },
    noinline getSavedStateRegistryOwner: () -> SavedStateRegistryOwner = { findViewTreeSavedStateRegistryOwner()!! },
    noinline getDefaultArgs: GetDefaultArgs? = null,
    noinline initializer: (RetainedEntry) -> T
): Retained<T> = retain(key, T::class, getViewModelStoreOwner, getSavedStateRegistryOwner, getDefaultArgs ?: { findViewTreeLifecycleOwner()!!.defaultArgs }, initializer)

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
 * @see initializer
 */
@OptIn(InternalRetainedApi::class)
public inline fun <reified T : Any> View.retainInActivity(
    key: String = id.toString(),
    activity: FragmentActivity = findActivity(),
    noinline getDefaultArgs: GetDefaultArgs? = null,
    noinline initializer: (RetainedEntry) -> T
): Retained<T> = retain(key, T::class, { activity }, { activity }, getDefaultArgs ?: { activity.intent?.extras ?: bundleOf() }, initializer)

@PublishedApi
internal fun View.findViewModelStoreOwnerOrThrow(): ViewModelStoreOwner = findViewTreeViewModelStoreOwner()
    ?: throw IllegalStateException("Your view is not yet attached, and thus its ViewModelStoreOwner is null.")

@PublishedApi
internal fun View.findActivity(): FragmentActivity {
    var currentContext = context
    while (currentContext is ContextWrapper) {
        if (currentContext is FragmentActivity) return currentContext
        currentContext = (context as ContextWrapper).baseContext
    }
    throw IllegalStateException("Your view is not attached to an activity.")
}

@PublishedApi
internal val LifecycleOwner.defaultArgs: Bundle
    get() = when (this) {
        is Activity -> intent?.extras
        is Fragment -> arguments
        is NavBackStackEntry -> arguments
        else -> bundleOf()
    } ?: bundleOf()
