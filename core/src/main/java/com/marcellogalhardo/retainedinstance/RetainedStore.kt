package com.marcellogalhardo.retainedinstance

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import java.io.Closeable

typealias InstanceProducer<T> = (store: RetainedStore) -> T
typealias RetainedMap = MutableMap<Any, Any>

/**
 * [RetainedStore] is a container for objects that needs to be preserved across configuration
 * changes of UI Controllers (a Fragment or an Activity). It is always created in association with
 * a UI Controller scope and will be retained as long as the scope is alive.
 *
 * In other words, this means that a [RetainedStore] will not be destroyed if its owner get
 * destroyed by a configuration change (e.g. rotation). The new instance of the owner will just
 * re-connected to the existing [RetainedStore].
 *
 * E.g. if it is an Activity, until it is finished.
 **/
@Suppress("unused")
class RetainedStore @JvmOverloads internal constructor(
    val application: Application,
    val savedStateHandle: SavedStateHandle,
    private val dataSet: RetainedMap = mutableMapOf()
) : ViewModel(), RetainedMap by dataSet {

    @Suppress("UNCHECKED_CAST")
    inline fun <T> get(
        key: Any,
        instanceProducer: RetainedStore.() -> Any
    ): T = getOrPut(key) { instanceProducer(this) } as T

    override fun onCleared() {
        dataSet.asSequence()
            .filterIsInstance<Closeable>()
            .forEach {
                it.close()
            }
        super.onCleared()
    }
}