@file:OptIn(InternalRetainedApi::class)

package dev.marcellogalhardo.retained.core.internal

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import dev.marcellogalhardo.retained.core.InternalRetainedApi
import dev.marcellogalhardo.retained.core.Retained
import dev.marcellogalhardo.retained.core.RetainedEntry
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

@PublishedApi
internal class EagerRetained<out T : Any>(
    key: String,
    retainedClass: KClass<T>,
    owner: ViewModelStoreOwner,
    instantiate: (RetainedEntry) -> T,
) : Retained<T> {
    private val factory = RetainedViewModelFactory(retainedClass, instantiate)

    private val provider = ViewModelProvider(owner, factory)

    @Suppress("UNCHECKED_CAST")
    override val value: T =
        run {
            val viewModel = provider.get(key, RetainedViewModel::class)
            if (owner is LifecycleOwner && viewModel.retainedInstance is LifecycleObserver) {
                owner.lifecycle.addObserver(viewModel.retainedInstance as LifecycleObserver)
            }
            viewModel.retainedInstance as T
        }

    override fun getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ): T = value
}
