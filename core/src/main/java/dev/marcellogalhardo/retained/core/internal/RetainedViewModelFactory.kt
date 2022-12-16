package dev.marcellogalhardo.retained.core.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import dev.marcellogalhardo.retained.core.RetainedEntry
import kotlin.reflect.KClass

internal class RetainedViewModelFactory(
    private val retainedClass: KClass<out Any>,
    private val instantiate: (RetainedEntry) -> Any,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return RetainedViewModel(
            key = requireNotNull(extras[ViewModelProvider.NewInstanceFactory.VIEW_MODEL_KEY]),
            application = requireNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]),
            retainedClass = retainedClass,
            savedStateHandle = extras.createSavedStateHandle(),
            createRetainedObject = instantiate,
        ) as T
    }
}
