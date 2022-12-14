package dev.marcellogalhardo.retained.navigation.view

import android.view.View
import androidx.annotation.IdRes
import androidx.navigation.findNavController
import dev.marcellogalhardo.retained.core.ExperimentalRetainedApi
import dev.marcellogalhardo.retained.core.Retained
import dev.marcellogalhardo.retained.core.RetainedEntry
import dev.marcellogalhardo.retained.navigation.retainInNavGraph

/**
 * Returns a [Lazy] delegate to access a retained object scoped to a navigation graph present on the
 * back stack:
 *
 * ```
 * class MyView : View() {
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
@ExperimentalRetainedApi
public inline fun <reified T : Any> View.retainInNavGraph(
    @IdRes navGraphId: Int,
    key: String = id.toString(),
    noinline instantiate: (RetainedEntry) -> T,
): Retained<T> = retainInNavGraph(
    findNavGraph = { findNavController().getBackStackEntry(navGraphId) },
    key = key,
    instantiate = instantiate,
)
