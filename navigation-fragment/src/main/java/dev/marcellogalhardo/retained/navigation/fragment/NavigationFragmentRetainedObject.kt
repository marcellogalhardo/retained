package dev.marcellogalhardo.retained.navigation.fragment

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.marcellogalhardo.retained.core.InternalRetainedApi
import dev.marcellogalhardo.retained.core.Retained
import dev.marcellogalhardo.retained.core.RetainedEntry
import dev.marcellogalhardo.retained.navigation.retainInNavGraph

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
 * @see retain
 */
@OptIn(InternalRetainedApi::class)
public inline fun <reified T : Any> Fragment.retainInNavGraph(
    @IdRes navGraphId: Int,
    key: String = T::class.java.name,
    noinline instantiate: (RetainedEntry) -> T
): Retained<T> {
    val backStackEntry by lazy(LazyThreadSafetyMode.NONE) {
        findNavController().getBackStackEntry(navGraphId)
    }
    return retainInNavGraph(key, { backStackEntry }, instantiate)
}
