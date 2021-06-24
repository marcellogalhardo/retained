package dev.marcellogalhardo.retained.fragment

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import dev.marcellogalhardo.retained.core.GetDefaultArgs
import dev.marcellogalhardo.retained.core.InternalRetainedApi
import dev.marcellogalhardo.retained.core.Retained
import dev.marcellogalhardo.retained.core.RetainedEntry
import dev.marcellogalhardo.retained.core.retain

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
 * @see initializer
 */
@OptIn(InternalRetainedApi::class)
public inline fun <reified T : Any> Fragment.retain(
    key: String = T::class.java.name,
    noinline getDefaultArgs: GetDefaultArgs? = null,
    noinline getFragment: () -> Fragment = { this },
    noinline initializer: (RetainedEntry) -> T
): Retained<T> = retain(key, T::class, getFragment, getFragment, getDefaultArgs ?: { arguments ?: bundleOf() }, initializer)

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
 * @see initializer
 */
@OptIn(InternalRetainedApi::class)
public inline fun <reified T : Any> Fragment.retainInActivity(
    key: String = T::class.java.name,
    noinline getDefaultArgs: () -> Bundle = { activity?.intent?.extras ?: bundleOf() },
    noinline initializer: (RetainedEntry) -> T
): Retained<T> = retain(key, T::class, ::requireActivity, ::requireActivity, getDefaultArgs, initializer)

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
 * [Fragment.onAttach], and access prior to that will result in [IllegalStateException].
 *
 * @see initializer
 */
@OptIn(InternalRetainedApi::class)
public inline fun <reified T : Any> Fragment.retainInParent(
    key: String = T::class.java.name,
    noinline getDefaultArgs: () -> Bundle = ::parentDefaultArgs,
    noinline initializer: (RetainedEntry) -> T
): Retained<T> = retain(key, T::class, ::parentViewModelStoreOwner, ::parentSavedStateRegistryOwner, getDefaultArgs, initializer)

@PublishedApi
internal val Fragment.parentDefaultArgs: Bundle
    get() = parentFragment?.arguments ?: activity?.intent?.extras ?: bundleOf()

@PublishedApi
internal val Fragment.parentSavedStateRegistryOwner: SavedStateRegistryOwner
    get() = parentFragment ?: requireActivity()

@PublishedApi
internal val Fragment.parentViewModelStoreOwner: ViewModelStoreOwner
    get() = parentFragment ?: requireActivity()
