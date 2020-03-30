package com.marcellogalhardo.retainedinstance.fragment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import com.marcellogalhardo.retainedinstance.InstanceProducer
import com.marcellogalhardo.retainedinstance.RetainedMap
import com.marcellogalhardo.retainedinstance.RetainedStore

/**
 * Returns [RetainedMap] associated to this [Fragment].
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
fun Fragment.getRetainedStore(
    defaultArgs: Bundle? = null,
    targetFragment: Fragment = this
): RetainedStore = viewModels<RetainedStore>({ targetFragment }) {
    SavedStateViewModelFactory(requireActivity().application, this, defaultArgs)
}.value

/**
 * Returns [RetainedMap] associated to this [Fragment].
 *
 * This property can be accessed only after this [Fragment] is attached i.e., after
 * [Fragment.onAttach], and access prior to that will result in [IllegalArgumentException].
 */
@[MainThread Throws(IllegalArgumentException::class)]
fun Fragment.getActivityRetainedStore(
    defaultArgs: Bundle? = null
): RetainedStore = activityViewModels<RetainedStore> {
    SavedStateViewModelFactory(requireActivity().application, this, defaultArgs)
}.value

/**
 *
 * Returns a [Lazy] delegate to access the [Fragment]'s [RetainedStore], if
 * [instanceProducer] is specified then [InstanceProducer] returned by it will be used
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
inline fun <reified T : Any> Fragment.retainInstance(
    defaultArgs: Bundle? = null,
    noinline targetFragment: () -> Fragment = { this },
    key: Any = T::class,
    noinline instanceProducer: InstanceProducer<T> = { T::class.java.newInstance() }
): Lazy<T> = lazy {
    getRetainedStore(defaultArgs, targetFragment()).get(key, instanceProducer) as T
}

/**
 * Returns a property delegate to access parent [ComponentActivity]'s [RetainedStore],
 * [instanceProducer] is specified then [InstanceProducer] returned by it will be used
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
inline fun <reified T : Any> Fragment.activityRetainInstance(
    defaultArgs: Bundle? = null,
    key: Any = T::class,
    noinline instanceProducer: InstanceProducer<T> = { T::class.java.newInstance() }
): Lazy<T> = lazy {
    getActivityRetainedStore(defaultArgs).get(key, instanceProducer)
}