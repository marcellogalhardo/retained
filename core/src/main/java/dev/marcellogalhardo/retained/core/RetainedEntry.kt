package dev.marcellogalhardo.retained.core

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.CoroutineScope
import kotlin.reflect.KClass

/**
 * Representation of an retained object entry in a [androidx.lifecycle.LifecycleOwner] and hosted
 * in a [androidx.lifecycle.ViewModel].
 *
 * If the host is terminated (e.g., pop back stack) [onClearedListeners] will be called.
 */
public interface RetainedEntry {

    /**
     * @see [androidx.lifecycle.AbstractSavedStateViewModelFactory.create]
     */
    public val key: String

    /**
     * @see [androidx.lifecycle.AbstractSavedStateViewModelFactory.create]
     */
    public val retainedClass: KClass<out Any>

    /**
     * @see [androidx.lifecycle.viewModelScope]
     */
    public val scope: CoroutineScope

    /**
     * @see [androidx.lifecycle.AbstractSavedStateViewModelFactory]
     */
    public val savedStateHandle: SavedStateHandle

    /**
     * @see [androidx.lifecycle.ViewModel.onCleared]
     */
    public val onClearedListeners: MutableCollection<OnClearedListener>
}
