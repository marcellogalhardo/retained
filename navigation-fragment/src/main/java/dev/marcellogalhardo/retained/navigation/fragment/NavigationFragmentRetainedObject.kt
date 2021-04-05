package dev.marcellogalhardo.retained.navigation.fragment

import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.marcellogalhardo.retained.core.GetDefaultArgs
import dev.marcellogalhardo.retained.core.InternalRetainedApi
import dev.marcellogalhardo.retained.core.RetainedEntry
import dev.marcellogalhardo.retained.core.createRetainedObjectLazy

/**
 * Returns a [Lazy] delegate to access a retained object scoped to a navigation graph present on the
 * back stack:
 *
 * ```
 * class MyFragment : Fragment() {
 *     val vm by retainInNavGraph(R.navigation.main) { ViewModel() }
 * }
 * ```
 *
 * This property can be accessed only after the navigation graph is on the [NavController] back
 * stack, and access prior to that will result in an [IllegalStateException].
 *
 * @param navGraphId ID of a navigation graph that exists on the [NavController] back stack.
 * @see createRetainedObject
 */
@OptIn(InternalRetainedApi::class)
inline fun <reified T : Any> Fragment.retainInNavGraph(
    @IdRes navGraphId: Int,
    key: String = T::class.java.name,
    noinline getDefaultArgs: GetDefaultArgs? = null,
    noinline createRetainedObject: (RetainedEntry) -> T
): Lazy<T> {
    val backStackEntry by lazy(LazyThreadSafetyMode.NONE) { findNavController().getBackStackEntry(navGraphId) }
    return createRetainedObjectLazy(key, T::class, { backStackEntry }, { backStackEntry }, getDefaultArgs ?: { backStackEntry.arguments ?: bundleOf() }, createRetainedObject)
}
