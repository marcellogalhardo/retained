package dev.marcellogalhardo.retained.core

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity

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
 * and access prior to that will result in [IllegalArgumentException].
 *
 * @see createRetainedObjectLazy
 */
@OptIn(InternalRetainedApi::class)
inline fun <reified T : Any> ComponentActivity.retain(
    key: String = T::class.java.name,
    defaultArgs: Bundle? = intent?.extras,
    noinline createRetainedObject: RetainedContext.() -> T
): Lazy<T> = createRetainedObjectLazy(key, { this }, defaultArgs, createRetainedObject)
