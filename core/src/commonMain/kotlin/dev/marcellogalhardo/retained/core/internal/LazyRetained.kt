package dev.marcellogalhardo.retained.core.internal

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import dev.marcellogalhardo.retained.core.Retained
import dev.marcellogalhardo.retained.core.RetainedEntry
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

@PublishedApi
internal class LazyRetained<out T : Any>(
    private val key: String,
    private val retainedClass: KClass<T>,
    private val findOwner: () -> ViewModelStoreOwner,
    private val instantiate: (RetainedEntry) -> T,
) : Retained<T> {
    @Suppress("UNCHECKED_CAST")
    override val value: T by lazy(LazyThreadSafetyMode.NONE) {
        val owner = findOwner()
        val factory = RetainedViewModelFactory(retainedClass, instantiate)
        val provider = ViewModelProvider(owner, factory)
        val viewModel = provider[key, RetainedViewModel::class]
        if (owner is LifecycleOwner && viewModel.retainedInstance is LifecycleObserver) {
            owner.lifecycle.addObserver(viewModel.retainedInstance)
        }
        viewModel.retainedInstance as T
    }

    override fun getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ): T = value
}
