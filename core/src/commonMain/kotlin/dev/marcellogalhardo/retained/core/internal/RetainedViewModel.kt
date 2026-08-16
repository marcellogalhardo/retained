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
    override val coroutineScope get() = viewModelScope

    val retainedInstance = createRetainedObject(this)

    init {
        if (retainedInstance is AutoCloseable) {
            addCloseable(retainedInstance)
        }
    }

    public override fun onCleared() {
        super.onCleared()
        if (retainedInstance is AutoCloseable) {
            retainedInstance.close()
        }
    }
}
