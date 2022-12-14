package dev.marcellogalhardo.retained.activity

import android.app.Application
import androidx.activity.ComponentActivity
import dev.marcellogalhardo.retained.core.InternalRetainedApi
import dev.marcellogalhardo.retained.core.Retained
import dev.marcellogalhardo.retained.core.RetainedEntry
import dev.marcellogalhardo.retained.core.retain

/**
 * Returns a [Lazy] delegate to access a retained object by **default** scoped to this
 * [ComponentActivity]:
 *
 * ```
 * class MyActivity : ComponentActivity() {
 *     val vm by retainInActivity(findActivity = { this }) { ViewModel() }
 * }
 * class ViewModel(val name: String = "")
 * ```
 *
 * This property can be accessed only after the [ComponentActivity] is attached to the [Application],
 * and access prior to that will result in [IllegalStateException].
 *
 * @see retain
 */
@OptIn(InternalRetainedApi::class)
public inline fun <reified T : Any> retainInActivity(
    noinline findActivity: () -> ComponentActivity,
    key: String = T::class.java.name,
    noinline instantiate: (RetainedEntry) -> T,
): Retained<T> = retain(
    key = key,
    findViewModelStoreOwner = findActivity,
    instantiate = instantiate,
)

/**
 * Returns a [Lazy] delegate to access a retained object by **default** scoped to this
 * [ComponentActivity]:
 *
 * ```
 * class MyActivity : ComponentActivity() {
 *     val vm by retain { ViewModel() }
 * }
 * class ViewModel(val name: String = "")
 * ```
 *
 * This property can be accessed only after the [ComponentActivity] is attached to the [Application],
 * and access prior to that will result in [IllegalStateException].
 *
 * @see retainInActivity
 */
public inline fun <reified T : Any> ComponentActivity.retain(
    key: String = T::class.java.name,
    noinline instantiate: (RetainedEntry) -> T,
): Retained<T> = retainInActivity(
    findActivity = { this },
    key = key,
    instantiate = instantiate,
)
