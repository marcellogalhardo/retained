package dev.marcellogalhardo.retained.core.internal

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistry
import dev.marcellogalhardo.retained.core.RetainedEntry
import kotlin.reflect.KClass

internal class RetainedViewModel(
    override val key: String,
    override val retainedClass: KClass<out Any>,
    override val savedStateHandle: SavedStateHandle,
    createRetainedObject: (RetainedEntry) -> Any,
) : ViewModel(),
    RetainedEntry {
    override val scope get() = viewModelScope

    val lifecycleObservers = mutableSetOf<LifecycleObserver>()

    val retainedInstance = createRetainedObject(this)

    init {
        if (retainedInstance is AutoCloseable) {
            addCloseable(retainedInstance)
        }
        if (retainedInstance is LifecycleObserver) {
            lifecycleObservers += retainedInstance
        }
        if (retainedInstance is SavedStateRegistry.SavedStateProvider) {
            savedStateHandle.setSavedStateProvider(key, retainedInstance)
        }
    }

    public override fun onCleared() {
        super.onCleared()
        if (retainedInstance is AutoCloseable) {
            retainedInstance.close()
        }
        lifecycleObservers.clear()
    }
}
