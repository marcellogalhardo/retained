package dev.marcellogalhardo.retained.core.internal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.marcellogalhardo.retained.core.OnClearedListener
import dev.marcellogalhardo.retained.core.RetainedEntry
import kotlinx.coroutines.CoroutineScope
import kotlin.reflect.KClass

internal class RetainedViewModel(
    override val key: String,
    override val retainedClass: KClass<out Any>,
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
