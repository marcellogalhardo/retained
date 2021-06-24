package dev.marcellogalhardo.retained.core.internal

import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import dev.marcellogalhardo.retained.core.FindDefaultArgs
import dev.marcellogalhardo.retained.core.Retained
import dev.marcellogalhardo.retained.core.RetainedEntry
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

@PublishedApi
internal class LazyRetained<out T : Any>(
    key: String,
    retainedClass: KClass<T>,
    findViewModelStoreOwner: () -> ViewModelStoreOwner,
    findSavedStateRegistryOwner: () -> SavedStateRegistryOwner,
    findDefaultArgs: FindDefaultArgs? = null,
    instantiate: (RetainedEntry) -> T,
) : Retained<T> {

    override val value: T by lazy(LazyThreadSafetyMode.NONE) {
        val retained = EagerRetained(
            key = key,
            retainedClass = retainedClass,
            viewModelStoreOwner = findViewModelStoreOwner(),
            savedStateRegistryOwner = findSavedStateRegistryOwner(),
            defaultArgs = findDefaultArgs?.invoke(),
            instantiate = instantiate
        )
        return@lazy retained.value
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = value
}
