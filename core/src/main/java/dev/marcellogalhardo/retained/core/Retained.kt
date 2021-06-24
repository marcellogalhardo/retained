package dev.marcellogalhardo.retained.core

import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import dev.marcellogalhardo.retained.core.internal.LazyRetained
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass

/**
 * Represents a value retained across configuration changes with lazy initialization.
 *
 * To create an instance of [Retained] use the [retain] function.
 */
public interface Retained<out T : Any> : ReadOnlyProperty<Any?, T> {

    /**
     * Gets the retained with lazy initialization value of the current Retained instance.
     * Once the value was initialized it must not change during the rest of lifetime of this [Retained] instance.
     */
    public val value: T
}

/**
 * Creates an retained instance scoped by a [LifecycleOwner] - [initializer] is used to
 * create the instance on the first time.
 *
 * A retained object is always created in association with a [LifecycleOwner] (`Fragment`,
 * `Activity`, or a `NavBackStackEntry`) and will be retained as long as the scope is alive.
 * E.g., if it is an `Activity`, until it is finished.
 *
 * In other words, this means that a retained object will not be destroyed if its owner is
 * destroyed for a configuration change (e.g., rotation). The new instance of the owner will just
 * re-connected to the existing retained object.
 *
 * ```
 * class MyComponentActivity : ComponentActivity() {
 *     val retainedString by retain { ViewModel() }
 * }
 * class ViewModel(val name: String = "")
 * ```
 *
 * This property can be accessed only after the [LifecycleOwner] is ready to use Jetpack
 * [ViewModel], and access prior to that will result in [IllegalStateException].
 *
 * @param key A String that will be used to identify the retained instance in this scope.
 * @param retainedClass A [KClass] that will be used to identify the retained instance in this scope.
 * @param getViewModelStoreOwner The [ViewModelStoreOwner] used to scope the retained instance.
 * @param getSavedStateRegistryOwner The [SavedStateRegistryOwner] used to restore the retained instance.
 * @param getDefaultArgs The [Bundle] used to create the [RetainedEntry.savedStateHandle].
 * @param initializer The factory function that will be used to create the retained object.
 */
public fun <T : Any> retain(
    key: String,
    retainedClass: KClass<T>,
    getViewModelStoreOwner: () -> ViewModelStoreOwner,
    getSavedStateRegistryOwner: () -> SavedStateRegistryOwner,
    getDefaultArgs: () -> Bundle,
    initializer: (RetainedEntry) -> T,
): Retained<T> {
    return LazyRetained(
        key,
        retainedClass,
        getViewModelStoreOwner,
        getSavedStateRegistryOwner,
        getDefaultArgs,
        initializer
    )
}
