package dev.marcellogalhardo.retained.fragment

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import dev.marcellogalhardo.retained.core.InternalRetainedApi
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
@OptIn(InternalRetainedApi::class)
inline fun <reified T : Any> Fragment.retain(
    key: String = T::class.java.name,
    noinline getDefaultArgs: () -> Bundle? = { arguments ?: bundleOf() },
    noinline getFragment: () -> Fragment = { this },
    noinline createRetainedObject: RetainedContext.() -> T
): Lazy<T> = createRetainedObjectLazy(key, getFragment, getFragment, getDefaultArgs, createRetainedObject)

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
@OptIn(InternalRetainedApi::class)
inline fun <reified T : Any> Fragment.retainInActivity(
    key: String = T::class.java.name,
    noinline getDefaultArgs: () -> Bundle? = { activity?.intent?.extras ?: bundleOf() },
    noinline createRetainedObject: RetainedContext.() -> T
): Lazy<T> = createRetainedObjectLazy(key, ::requireActivity, ::requireActivity, getDefaultArgs, createRetainedObject)

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
@OptIn(InternalRetainedApi::class)
inline fun <reified T : Any> Fragment.retainInParent(
    key: String = T::class.java.name,
    noinline getDefaultArgs: () -> Bundle? = ::parentDefaultArgs,
    noinline createRetainedObject: RetainedContext.() -> T
): Lazy<T> = createRetainedObjectLazy(key, ::parentViewModelStoreOwner, ::parentSavedStateRegistryOwner, getDefaultArgs, createRetainedObject)

@PublishedApi
internal val Fragment.parentDefaultArgs: Bundle
    get() = parentFragment?.arguments ?: activity?.intent?.extras ?: bundleOf()

@PublishedApi
internal val Fragment.parentSavedStateRegistryOwner: SavedStateRegistryOwner
    get() = parentFragment ?: requireActivity()

@PublishedApi
internal val Fragment.parentViewModelStoreOwner: ViewModelStoreOwner
    get() = parentFragment ?: requireActivity()
