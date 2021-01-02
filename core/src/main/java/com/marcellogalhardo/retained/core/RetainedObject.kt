package com.marcellogalhardo.retained.core

import android.os.Bundle
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DisposableHandle

fun <T : Any> retainObject(
    key: String,
    owner: LifecycleOwner,
    defaultArgs: Bundle? = null,
    createRetainedObject: RetainedContext.() -> T
): T {
    val viewModelStoreOwner = owner as ViewModelStoreOwner
    val savedStateRegistryOwner = owner as SavedStateRegistryOwner
    val factory = RetainedViewModelFactory(
        owner = savedStateRegistryOwner,
        defaultArgs = defaultArgs,
        createRetainedObject = createRetainedObject
    )
    val provider = ViewModelProvider(viewModelStoreOwner, factory)

    @Suppress("UNCHECKED_CAST")
    return provider.get(key, RetainedViewModel::class.java).retainedObject as T
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
