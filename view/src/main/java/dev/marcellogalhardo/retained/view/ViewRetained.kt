package dev.marcellogalhardo.retained.view

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.navigation.NavBackStackEntry
import dev.marcellogalhardo.retained.core.ExperimentalRetainedApi
import dev.marcellogalhardo.retained.core.InternalRetainedApi
import dev.marcellogalhardo.retained.core.Retained
import dev.marcellogalhardo.retained.core.RetainedEntry
import dev.marcellogalhardo.retained.core.retain

/**
 * Returns a [Lazy] delegate to access a retained object by **default** scoped to this [View]:
 *
 * ```
 * class MyView(context: Context) : View(context) {
 *     val vm by retainInView(findInView = { this }) { ViewModel() }
 * }
 * class ViewModel(val name: String = "")
 * ```
 *
 * This property can be accessed only after this [View] is attached i.e., after
 * [View.isAttachedToWindow], and access prior to that will result in [IllegalStateException].
 *
 * @see retain
 */
@OptIn(InternalRetainedApi::class)
public inline fun <reified T : Any> retainInView(
    noinline findView: () -> View,
    key: String = "${findView().id}:${T::class.java.name}",
    noinline instantiate: (RetainedEntry) -> T,
): Retained<T> {
    return retain(
        key = key,
        findOwner = { findView().findViewModelStoreOwnerOrThrow() },
        instantiate = instantiate,
    )
}

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
    key: String = T::class.java.name,
    noinline instantiate: (RetainedEntry) -> T,
): Retained<T> {
    return retainInView(
        findView = { this },
        key = key,
        instantiate = instantiate,
    )
}

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
    key: String = T::class.java.name,
    noinline instantiate: (RetainedEntry) -> T,
): Retained<T> = retain(
    key = key,
    findOwner = { context.findActivity() },
    instantiate = instantiate,
)

@PublishedApi
internal fun View.findViewModelStoreOwnerOrThrow(): ViewModelStoreOwner {
    val owner = checkNotNull(findViewTreeViewModelStoreOwner()) {
        "Your view is not yet attached, and thus its ViewModelStoreOwner is null."
    }
    setViewTreeViewModelStoreOwner(owner)
    return owner
}

@PublishedApi
internal tailrec fun Context.findActivity(): ComponentActivity = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> error("Your view is not attached to an activity.")
}
