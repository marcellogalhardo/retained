package dev.marcellogalhardo.retained.core

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.CoroutineScope

/**
 * Creates an retained instance scoped by a [LifecycleOwner] - [createRetainedObject] is used to
 * create the instance on the first time.
 *
 * A retained object is always created in association with a [LifecycleOwner] (`Fragment`,
 * `Activity`, or a `@Composable`) and will be retained as long as the scope is alive.
 * E.g., if it is an `Activity`, until it is finished.
 *
 * In other words, this means that a retained object will not be destroyed if its owner is
 * destroyed for a configuration change (e.g., rotation). The new instance of the owner will just
 * re-connected to the existing retained object.
 *
 * ```
 * class MyComponentActivity : ComponentActivity() {
 *     val retainedString by retain { ViewModel() }
 * }
 * class ViewModel(val name: String = "")
 * ```
 *
 * This property can be accessed only after the [LifecycleOwner] is ready to use Jetpack
 * [ViewModel], and access prior to that will result in [IllegalStateException].
 *
 * @param key A String that will be used to identify the retained instance in this scope.
 * @param viewModelStoreOwner The [ViewModelStoreOwner] used to scope the retained instance.
 * @param savedStateRegistryOwner The [SavedStateRegistryOwner] used to restore the retained instance.
 * @param defaultArgs The [Bundle] used to create the [RetainedEntry.savedStateHandle].
 * @param createRetainedObject The factory function that will be used to create the retained object.
 */
@InternalRetainedApi
fun <T : Any> createRetainedObject(
    key: String,
    viewModelStoreOwner: ViewModelStoreOwner,
    savedStateRegistryOwner: SavedStateRegistryOwner,
    defaultArgs: Bundle = bundleOf(),
    createRetainedObject: (RetainedEntry) -> T
): T {
    val factory = RetainedViewModelFactory(
        owner = savedStateRegistryOwner,
        defaultArgs = defaultArgs,
        createRetainedObject = createRetainedObject
    )
    val provider = ViewModelProvider(viewModelStoreOwner, factory)

    @Suppress("UNCHECKED_CAST")
    return provider.get(key, RetainedViewModel::class.java).retainedObject as T
}

/**
 * Returns a [Lazy] delegate to access a single retained instance in a [LifecycleOwner].
 *
 * @see createRetainedObject
 */
@InternalRetainedApi
fun <T : Any> createRetainedObjectLazy(
    key: String,
    getViewModelStoreOwner: () -> ViewModelStoreOwner,
    getSavedStateRegistryOwner: () -> SavedStateRegistryOwner,
    getDefaultArgs: () -> Bundle = { bundleOf() },
    createRetainedObject: (RetainedEntry) -> T
): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {
    createRetainedObject(key, getViewModelStoreOwner(), getSavedStateRegistryOwner(), getDefaultArgs(), createRetainedObject)
}

private class RetainedViewModel(
    override val savedStateHandle: SavedStateHandle,
    createRetainedObject: (RetainedEntry) -> Any,
) : ViewModel(), RetainedEntry {

    override val scope: CoroutineScope get() = viewModelScope

    override val onClearedListeners: MutableCollection<OnClearedListener> = mutableSetOf()

    val retainedObject: Any = createRetainedObject(this)

    init {
        if (retainedObject is OnClearedListener) {
            onClearedListeners += retainedObject
        }
    }

    override fun onCleared() {
        super.onCleared()
        onClearedListeners.forEach { listener -> listener.onCleared() }
    }
}

private class RetainedViewModelFactory(
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle,
    val createRetainedObject: (RetainedEntry) -> Any
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return RetainedViewModel(handle, createRetainedObject) as T
    }
}
