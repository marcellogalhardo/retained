package dev.marcellogalhardo.retained.core

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.os.bundleOf

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
 * @see createRetainedObject
 */
@OptIn(InternalRetainedApi::class)
inline fun <reified T : Any> ComponentActivity.retain(
    key: String = T::class.java.name,
    noinline getDefaultArgs: () -> Bundle = { intent?.extras ?: bundleOf() },
    noinline createRetainedObject: (RetainedEntry) -> T
): Lazy<T> = createRetainedObjectLazy(key, { this }, { this }, getDefaultArgs, createRetainedObject)
