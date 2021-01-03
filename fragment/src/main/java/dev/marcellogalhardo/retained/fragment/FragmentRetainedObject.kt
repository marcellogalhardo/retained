package dev.marcellogalhardo.retained.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import dev.marcellogalhardo.retained.core.RetainedContext
import dev.marcellogalhardo.retained.core.createRetainedObjectLazy

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
 * [Fragment.onAttach], and access prior to that will result in [IllegalArgumentException].
 *
 * @see createRetainedObjectLazy
 */
inline fun <reified T : Any> Fragment.retain(
    key: String = T::class.java.name,
    defaultArgs: Bundle? = null,
    noinline getFragment: () -> Fragment = { this },
    noinline createRetainedObject: RetainedContext.() -> T
): Lazy<T> = createRetainedObjectLazy(key, getFragment, defaultArgs, createRetainedObject)

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
 * [Fragment.onAttach], and access prior to that will result in [IllegalArgumentException].
 *
 * @see createRetainedObjectLazy
 */
inline fun <reified T : Any> Fragment.retainInActivity(
    key: String = T::class.java.name,
    defaultArgs: Bundle? = null,
    noinline createRetainedObject: RetainedContext.() -> T
): Lazy<T> = createRetainedObjectLazy(key, { requireActivity() }, defaultArgs, createRetainedObject)

/**
 * Returns a [Lazy] delegate to access a retained object by **default** scoped to the parent
 * [LifecycleOwner] (e.g., [Fragment.getParentFragment] if not null, otherwise
 * [Fragment.requireActivity]):
 *
 * ```
 * class MyFragment : Fragment() {
 *     val vm by retainInParent { ViewModel() }
 * }
 * class ViewModel(val name: String = "")
 * ```
 *
 * This property can be accessed only after this [Fragment] is attached i.e., after
 * [Fragment.onAttach], and access prior to that will result in [IllegalArgumentException].
 *
 * @see createRetainedObjectLazy
 */
inline fun <reified T : Any> Fragment.retainInParent(
    key: String = T::class.java.name,
    defaultArgs: Bundle? = null,
    noinline createRetainedObject: RetainedContext.() -> T
): Lazy<T> = createRetainedObjectLazy(key, ::parentLifecycleOwner, defaultArgs, createRetainedObject)

@PublishedApi
internal val Fragment.parentLifecycleOwner: LifecycleOwner
    get() = parentFragment ?: requireActivity()
