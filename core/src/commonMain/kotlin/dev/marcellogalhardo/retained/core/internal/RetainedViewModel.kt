package dev.marcellogalhardo.retained.core.internal

import androidx.lifecycle.LifecycleObserver
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

    override val closeables: MutableCollection<AutoCloseable> =
        object : LinkedHashSet<AutoCloseable>() {
            override fun add(element: AutoCloseable): Boolean {
                val added = super.add(element)
                if (added) {
                    addCloseable(element)
                }
                return added
            }
        }

    override val lifecycleObservers = mutableSetOf<LifecycleObserver>()

    val retainedInstance = createRetainedObject(this)

    init {
        if (retainedInstance is AutoCloseable) {
            closeables += retainedInstance
        }
        if (retainedInstance is LifecycleObserver) {
            lifecycleObservers += retainedInstance
        }
    }

    public override fun onCleared() {
        super.onCleared()
        closeables.forEach { closeable -> closeable.close() }
        lifecycleObservers.clear()
    }
}
