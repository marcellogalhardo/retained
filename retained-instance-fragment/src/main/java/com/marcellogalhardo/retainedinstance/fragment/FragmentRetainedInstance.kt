package com.marcellogalhardo.retainedinstance.fragment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelStoreOwner
import com.marcellogalhardo.retainedinstance.RetainedInstance
import com.marcellogalhardo.retainedinstance.RetainedInstanceStore
import com.marcellogalhardo.retainedinstance.RetainedInstanceValueProducer

internal typealias OwnerProducer = () -> ViewModelStoreOwner

/**
 * Returns [RetainedInstanceStore] associated to this [Fragment].
 *
 * Default scope may be overridden with parameter [ownerProducer]:
 *
 * ```
 * class Fragment2 : Fragment() {
 *     val viewModel: FragmentViewModel1 by retainedInstances(
 *         ownerProducer = { requireParentFragment() }
 *     )
 * }
 * ```
 *
 * This property can be accessed only after this [Fragment] is attached i.e., after
 * [Fragment.onAttach], and access prior to that will result in [IllegalArgumentException].
 */
@[MainThread Throws(IllegalArgumentException::class)]
fun Fragment.getRetainedInstanceStore(
    defaultArgs: Bundle? = null,
    ownerProducer: OwnerProducer = { this }
): RetainedInstanceStore {
    val viewModel = viewModels<RetainedInstance>(ownerProducer) {
        SavedStateViewModelFactory(requireActivity().application, this, defaultArgs)
    }.value
    lifecycle.addObserver(viewModel)
    return viewModel
}

/**
 * Returns [RetainedInstanceStore] associated to this [Fragment].
 *
 * This property can be accessed only after this [Fragment] is attached i.e., after
 * [Fragment.onAttach], and access prior to that will result in [IllegalArgumentException].
 */
@[MainThread Throws(IllegalArgumentException::class)]
fun Fragment.getActivityRetainedInstanceStore(
    defaultArgs: Bundle? = null
): RetainedInstanceStore {
    val viewModel = activityViewModels<RetainedInstance> {
        SavedStateViewModelFactory(requireActivity().application, this, defaultArgs)
    }.value
    lifecycle.addObserver(viewModel)
    return viewModel
}

/**
 *
 * Returns a [Lazy] delegate to access the [Fragment]'s [RetainedInstance], if
 * [valueProducer] is specified then [RetainedInstanceValueProducer] returned by it will be used
 * to create value first time.
 *
 * ```
 * class Fragment1 : Fragment() {
 *     val viewModel: FragmentViewModel1 by retainedInstances()
 * }
 * ```
 *
 * Default scope may be overridden with parameter [ownerProducer]:
 *
 * ```
 * class Fragment2 : Fragment() {
 *     val viewModel: FragmentViewModel1 by retainedInstances(
 *         ownerProducer = { requireParentFragment() }
 *     )
 * }
 * ```
 *
 * This property can be accessed only after this [Fragment] is attached i.e., after
 * [Fragment.onAttach], and access prior to that will result in [IllegalArgumentException].
 */
@[MainThread Throws(IllegalArgumentException::class)]
inline fun <reified T : Any> Fragment.retainedInstances(
    defaultArgs: Bundle? = null,
    noinline ownerProducer: OwnerProducer = { this },
    key: Any = T::class,
    noinline valueProducer: RetainedInstanceValueProducer<T> = { T::class.java.newInstance() }
): Lazy<T> = lazy {
    getRetainedInstanceStore(defaultArgs, ownerProducer)
        .getOrPut(key, valueProducer) as T
}

/**
 * Returns a property delegate to access parent [ComponentActivity]'s [RetainedInstance],
 * [valueProducer] is specified then [RetainedInstanceValueProducer] returned by it will be used
 * to create value first time.
 *
 * ```
 * class MyFragment : Fragment() {
 *     val viewModel: ActivityViewModel by activityViewModels()
 * }
 * ```
 *
 * This property can be accessed only after this Fragment is attached i.e., after
 * [Fragment.onAttach], and access prior to that will result in [IllegalArgumentException].
 */
@[MainThread Throws(IllegalArgumentException::class)]
inline fun <reified T : Any> Fragment.activityRetainedInstances(
    defaultArgs: Bundle? = null,
    key: Any = T::class,
    noinline valueProducer: RetainedInstanceValueProducer<T> = { T::class.java.newInstance() }
): Lazy<T> = lazy {
    getActivityRetainedInstanceStore(defaultArgs)
        .getOrPut(key, valueProducer) as T
}