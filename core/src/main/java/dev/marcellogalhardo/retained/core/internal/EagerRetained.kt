@file:OptIn(InternalRetainedApi::class)

package dev.marcellogalhardo.retained.core.internal

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import dev.marcellogalhardo.retained.core.InternalRetainedApi
import dev.marcellogalhardo.retained.core.Retained
import dev.marcellogalhardo.retained.core.RetainedEntry
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

@PublishedApi
internal class EagerRetained<out T : Any>(
    key: String,
    retainedClass: KClass<T>,
    viewModelStoreOwner: ViewModelStoreOwner,
    savedStateRegistryOwner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null,
    instantiate: (RetainedEntry) -> T,
) : Retained<T> {

    private val factory = RetainedViewModelFactory(
        owner = savedStateRegistryOwner,
        defaultArgs = defaultArgs,
        retainedClass = retainedClass,
        instantiate = instantiate,
    )

    private val provider = ViewModelProvider(viewModelStoreOwner, factory)

    @Suppress("UNCHECKED_CAST")
    override val value: T = provider.get(key, RetainedViewModel::class.java).retainedInstance as T

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = value
}
