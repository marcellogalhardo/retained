package com.marcellogalhardo.retainedinstance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.annotation.MainThread
import androidx.lifecycle.SavedStateViewModelFactory

/**
 * Returns [RetainedInstanceStore] associated to this [ComponentActivity].
 *
 * This property can be accessed only after the Activity is attached to the Application,
 * and access prior to that will result in [IllegalArgumentException].
 */
@[MainThread Throws(IllegalArgumentException::class)]
fun ComponentActivity.getRetainedInstanceStore(
    defaultArgs: Bundle? = null
): RetainedInstanceStore {
    val viewModel = viewModels<RetainedInstance> {
        SavedStateViewModelFactory(application, this, defaultArgs)
    }.value
    lifecycle.addObserver(viewModel)
    return viewModel
}

/**
 * Returns a [Lazy] delegate to access the [ComponentActivity]'s [RetainedInstance], if
 * [valueProducer] is specified then [RetainedInstanceValueProducer] returned by it will be used
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
    defaultArgs: Bundle? = null,
    key: Any = T::class,
    noinline valueProducer: RetainedInstanceValueProducer<T> = { T::class.java.newInstance() }
): Lazy<T> = lazy {
    getRetainedInstanceStore(defaultArgs).getOrPut(key, valueProducer) as T
}