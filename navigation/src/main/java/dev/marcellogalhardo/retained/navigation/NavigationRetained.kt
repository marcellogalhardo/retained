package dev.marcellogalhardo.retained.navigation

import androidx.annotation.IdRes
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import dev.marcellogalhardo.retained.core.InstantiateRetained
import dev.marcellogalhardo.retained.core.InternalRetainedApi
import dev.marcellogalhardo.retained.core.Retained
import dev.marcellogalhardo.retained.core.RetainedEntry
import dev.marcellogalhardo.retained.core.retain

/**
 * Returns a [Retained] delegate to access a retained object scoped to a navigation graph present on the
 * back stack:
 *
 * This property can be accessed only after the navigation graph is on the [NavController] back
 * stack, and access prior to that will result in an [IllegalStateException].
 *
 * @param navGraphId ID of a navigation graph that exists on the [NavController] back stack.
 * @see retain
 */
@OptIn(InternalRetainedApi::class)
public inline fun <reified T : Any> retainInNavGraph(
    key: String = T::class.java.name,
    crossinline findNavGraph: () -> NavBackStackEntry,
    noinline instantiate: (RetainedEntry) -> T,
): Retained<T> {
    return retain(key, { findNavGraph() }, { owner -> owner.arguments }, instantiate)
}

/**
 * Returns a [Retained] delegate to access a retained object scoped to a navigation graph present on the
 * back stack:
 *
 * This property can be accessed only after the navigation graph is on the [NavController] back
 * stack, and access prior to that will result in an [IllegalStateException].
 *
 * @param navGraphId ID of a navigation graph that exists on the [NavController] back stack.
 * @see retain
 */
@OptIn(InternalRetainedApi::class)
public inline fun <reified T : Any> NavBackStackEntry.retain(
    key: String = T::class.java.name,
    noinline instantiate: (RetainedEntry) -> T,
): Retained<T> {
    return retain(key, { this }, { owner -> owner.arguments }, instantiate)
}
