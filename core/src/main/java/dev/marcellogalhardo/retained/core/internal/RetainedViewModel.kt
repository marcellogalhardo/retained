package dev.marcellogalhardo.retained.core.internal

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.marcellogalhardo.retained.core.OnClearedListener
import dev.marcellogalhardo.retained.core.RetainedEntry
import kotlinx.coroutines.CoroutineScope
import kotlin.reflect.KClass

internal class RetainedViewModel(
    override val key: String,
    override val application: Application,
    override val retainedClass: KClass<out Any>,
    override val savedStateHandle: SavedStateHandle,
    createRetainedObject: (RetainedEntry) -> Any,
) : ViewModel(), RetainedEntry {

    override val scope get() = viewModelScope

    override val onClearedListeners = mutableSetOf<OnClearedListener>()

    val retainedInstance = createRetainedObject(this)

    init {
        if (retainedInstance is OnClearedListener) {
            onClearedListeners += retainedInstance
        }
    }

    override fun onCleared() {
        super.onCleared()
        onClearedListeners.forEach { listener -> listener.onCleared() }
    }
}
