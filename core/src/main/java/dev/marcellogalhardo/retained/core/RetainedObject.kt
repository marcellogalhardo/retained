package dev.marcellogalhardo.retained.core

import android.os.Bundle
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DisposableHandle

/**
 * Returns a [Lazy] delegate to access a single retained instance in a [LifecycleOwner].
 * [createRetainedObject] is used to create the instance on the first time.
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
 * [ViewModel], and access prior to that will result in [IllegalArgumentException].
 *
 * @param key A String that will be used to identify the retained instance in this scope.
 * @param getOwner The [LifecycleOwner] used to scope the retained instance.
 * @param defaultArgs The [Bundle] used to create the [RetainedContext].
 * @param createRetainedObject The factory function that will be used to create the retained object.
 */
@InternalRetainedApi
fun <T : Any> createRetainedObjectLazy(
    key: String,
    getOwner: () -> LifecycleOwner,
    defaultArgs: Bundle? = null,
    createRetainedObject: RetainedContext.() -> T
): Lazy<T> = lazy {
    val lifecycleOwner = getOwner()
    val viewModelStoreOwner = lifecycleOwner as ViewModelStoreOwner
    val savedStateRegistryOwner = lifecycleOwner as SavedStateRegistryOwner
    val factory = RetainedViewModelFactory(
        owner = savedStateRegistryOwner,
        defaultArgs = defaultArgs,
        createRetainedObject = createRetainedObject
    )
    val provider = ViewModelProvider(viewModelStoreOwner, factory)

    @Suppress("UNCHECKED_CAST")
    provider.get(key, RetainedViewModel::class.java).retainedObject as T
}

private class RetainedViewModel(
    override val retainedHandle: SavedStateHandle,
    createRetainedObject: RetainedContext.() -> Any,
) : ViewModel(), RetainedContext {

    override val retainedScope: CoroutineScope get() = viewModelScope

    val retainedObject: Any = createRetainedObject()

    override fun onCleared() {
        super.onCleared()
        if (retainedObject is DisposableHandle) retainedObject.dispose()
    }
}

private class RetainedViewModelFactory(
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null,
    val createRetainedObject: RetainedContext.() -> Any
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

interface RetainedContext {
    val retainedScope: CoroutineScope
    val retainedHandle: SavedStateHandle
}
