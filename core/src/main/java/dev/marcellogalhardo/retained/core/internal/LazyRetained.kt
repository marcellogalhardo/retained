package dev.marcellogalhardo.retained.core.internal

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import dev.marcellogalhardo.retained.core.GetDefaultArgs
import dev.marcellogalhardo.retained.core.Retained
import dev.marcellogalhardo.retained.core.RetainedEntry
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

@PublishedApi
internal class LazyRetained<out T : Any>(
    key: String,
    retainedClass: KClass<T>,
    getViewModelStoreOwner: () -> ViewModelStoreOwner,
    getSavedStateRegistryOwner: () -> SavedStateRegistryOwner,
    getDefaultArgs: GetDefaultArgs? = null,
    initializer: (RetainedEntry) -> T,
) : Retained<T> {

    override val value: T by lazy(LazyThreadSafetyMode.NONE) {
        val factory = RetainedViewModelFactory(
            owner = getSavedStateRegistryOwner(),
            defaultArgs = getDefaultArgs?.invoke(),
            retainedClass = retainedClass,
            initializer = initializer
        )
        val provider = ViewModelProvider(getViewModelStoreOwner(), factory)

        @Suppress("UNCHECKED_CAST")
        return@lazy provider.get(key, RetainedViewModel::class.java).retainedObject as T
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = value
}
