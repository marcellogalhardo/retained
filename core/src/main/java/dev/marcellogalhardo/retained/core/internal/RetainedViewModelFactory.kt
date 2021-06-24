package dev.marcellogalhardo.retained.core.internal

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import dev.marcellogalhardo.retained.core.RetainedEntry
import kotlin.reflect.KClass

internal class RetainedViewModelFactory(
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle?,
    val retainedClass: KClass<out Any>,
    val initializer: (RetainedEntry) -> Any
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return RetainedViewModel(key, retainedClass, handle, initializer) as T
    }
}
