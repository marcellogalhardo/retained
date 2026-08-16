package dev.marcellogalhardo.retained.core

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.CoroutineScope
import kotlin.reflect.KClass

/**
 * Representation of a retained object entry hosted in a [androidx.lifecycle.ViewModel].
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
}
