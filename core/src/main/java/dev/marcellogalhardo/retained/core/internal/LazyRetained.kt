package dev.marcellogalhardo.retained.core.internal

import androidx.lifecycle.ViewModelStoreOwner
import dev.marcellogalhardo.retained.core.Retained
import dev.marcellogalhardo.retained.core.RetainedEntry
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

@PublishedApi
internal class LazyRetained<out T : Any>(
    key: String,
    retainedClass: KClass<T>,
    findOwner: () -> ViewModelStoreOwner,
    instantiate: (RetainedEntry) -> T,
) : Retained<T> {

    override val value: T by lazy(LazyThreadSafetyMode.NONE) {
        val retained = EagerRetained(
            key = key,
            retainedClass = retainedClass,
            owner = findOwner(),
            instantiate = instantiate
        )
        return@lazy retained.value
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = value
}
