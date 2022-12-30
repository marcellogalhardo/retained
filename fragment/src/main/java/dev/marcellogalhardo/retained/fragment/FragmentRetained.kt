package dev.marcellogalhardo.retained.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import dev.marcellogalhardo.retained.activity.retainInActivity
import dev.marcellogalhardo.retained.core.InternalRetainedApi
import dev.marcellogalhardo.retained.core.Retained
import dev.marcellogalhardo.retained.core.RetainedEntry
import dev.marcellogalhardo.retained.core.retain

/**
 * Returns a [Lazy] delegate to access a retained object by **default** scoped to this [Fragment]:
 *
 * ```
 * class MyFragment : Fragment() {
 *     val vm by retainInFragment(getFragment = { this }) { ViewModel() }
 * }
 * class ViewModel(val name: String = "")
 * ```
 *
 * This property can be accessed only after this [Fragment] is attached i.e., after
 * [Fragment.onAttach], and access prior to that will result in [IllegalStateException].
 *
 * @see retain
 */
@OptIn(InternalRetainedApi::class)
public inline fun <reified T : Any> retainInFragment(
    noinline findFragment: () -> Fragment,
    key: String = T::class.java.name,
    noinline instantiate: (RetainedEntry) -> T,
): Retained<T> = retain(
    key = key,
    findOwner = findFragment,
    instantiate = instantiate,
)

/**
 * Returns a [Lazy] delegate to access a retained object by **default** scoped to this [Fragment]:
 *
 * ```
 * class MyFragment : Fragment() {
 *     val vm by retain { ViewModel() }
 * }
 * class ViewModel(val name: String = "")
 * ```
 *
 * Default scope may be overridden with parameter [getFragment]:
 * ```
 * class MyFragment : Fragment() {
 *     val vm by retain({ parentFragment }) { ViewModel() }
 * }
 * ```
 *
 * This property can be accessed only after this [Fragment] is attached i.e., after
 * [Fragment.onAttach], and access prior to that will result in [IllegalStateException].
 *
 * @see retain
 */
public inline fun <reified T : Any> Fragment.retain(
    key: String = T::class.java.name,
    noinline instantiate: (RetainedEntry) -> T,
): Retained<T> = retainInFragment(
    findFragment = { this },
    key = key,
    instantiate = instantiate,
)

/**
 * Returns a [Lazy] delegate to access a retained object by **default** scoped to this
 * [FragmentActivity]:
 *
 * ```
 * class MyFragment : Fragment() {
 *     val vm by retainInActivity { ViewModel() }
 * }
 * class ViewModel(val name: String = "")
 * ```
 *
 * This property can be accessed only after this [Fragment] is attached i.e., after
 * [Fragment.onAttach], and access prior to that will result in [IllegalStateException].
 *
 * @see retain
 */
public inline fun <reified T : Any> Fragment.retainInActivity(
    key: String = T::class.java.name,
    noinline instantiate: (RetainedEntry) -> T,
): Retained<T> = retainInActivity(
    findActivity = ::requireActivity,
    key = key,
    instantiate = instantiate,
)
