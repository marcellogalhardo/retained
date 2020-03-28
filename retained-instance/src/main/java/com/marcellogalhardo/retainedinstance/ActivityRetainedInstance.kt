package com.marcellogalhardo.retainedinstance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.annotation.MainThread
import androidx.lifecycle.SavedStateViewModelFactory

/**
 * Returns [RetainedStore] associated to this [ComponentActivity].
 *
 * This property can be accessed only after the Activity is attached to the Application,
 * and access prior to that will result in [IllegalArgumentException].
 */
@[MainThread Throws(IllegalArgumentException::class)]
fun ComponentActivity.getRetainedStore(
    defaultArgs: Bundle? = intent?.extras
): RetainedStore = viewModels<RetainedStore> {
    SavedStateViewModelFactory(application, this, defaultArgs)
}.value

/**
 * Returns a [Lazy] delegate to access the [ComponentActivity]'s [RetainedStore], if
 * [instanceProducer] is specified then [InstanceProducer] returned by it will be used
 * to create value first time.
 *
 * ```
 * class MyComponentActivity : ComponentActivity() {
 *     val viewModel: ActivityViewModel by retainedInstances()
 * }
 * ```
 *
 * This property can be accessed only after the Activity is attached to the Application,
 * and access prior to that will result in [IllegalArgumentException].
 */
@[MainThread Throws(IllegalArgumentException::class)]
inline fun <reified T : Any> ComponentActivity.retainedInstances(
    defaultArgs: Bundle? = intent?.extras,
    key: Any = T::class,
    noinline instanceProducer: InstanceProducer<T> = { T::class.java.newInstance() }
): Lazy<T> = lazy {
    val store = getRetainedStore(defaultArgs)
    store.get(key, instanceProducer)
}