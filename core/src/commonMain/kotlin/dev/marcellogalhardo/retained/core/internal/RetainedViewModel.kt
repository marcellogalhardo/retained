package dev.marcellogalhardo.retained.core.internal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    override val onClearedListeners = mutableSetOf<AutoCloseable>()

    val retainedInstance = createRetainedObject(this)

    init {
        if (retainedInstance is AutoCloseable) {
            onClearedListeners += retainedInstance
        }
    }

    override fun onCleared() {
        super.onCleared()
        onClearedListeners.forEach { closeable -> closeable.close() }
    }
}
