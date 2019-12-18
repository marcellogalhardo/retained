package com.marcellogalhardo.retainedinstance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.annotation.MainThread
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider

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
    return viewModels<RetainedInstance> {
        SavedStateViewModelFactory(application, this, defaultArgs)
    }.value
}

@MainThread
inline fun <reified VM : ViewModel> ComponentActivity.viewModels(
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> {
    val factoryPromise = factoryProducer ?: {
        val application = application ?: throw IllegalArgumentException(
            "ViewModel can be accessed only when Activity is attached"
        )
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }
    return ViewModelLazy(VM::class, { viewModelStore }, factoryPromise)
}

/**
 * Returns a [Lazy] delegate to access the [ComponentActivity]'s [RetainedInstance], if
 * [instanceProvider] is specified then [RetainedInstanceProvider] returned by it will be used
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
    noinline instanceProvider: RetainedInstanceProvider<T> = { T::class.java.newInstance() }
): Lazy<T> = lazy {
    getRetainedInstanceStore(defaultArgs).getOrPut(key, instanceProvider) as T
}