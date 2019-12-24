package com.marcellogalhardo.retainedinstance

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope

typealias RetainedInstanceProvider<T> = () -> T
typealias RetainedInstanceStore = MutableMap<Any, Any>

/**
 * [RetainedInstance] is a container for objects that needs to be preserved across configuration
 * changes of UI Controllers (a Fragment or an Activity). It is always created in association with
 * a UI Controller scope and will be retained as long as the scope is alive.
 *
 * In other words, this means that a [RetainedInstance] will not be destroyed if its owner is
 * destroyed for a configuration change (e.g. rotation). The new instance of the owner will just
 * re-connected to the existing [RetainedInstance].
 *
 * E.g. if it is an Activity, until it is finished.
 **/
class RetainedInstance @JvmOverloads constructor(
    application: Application,
    private val savedStateHandle: SavedStateHandle,
    private val retainedInstanceStore: RetainedInstanceStore = mutableMapOf()
) : AndroidViewModel(application),
    RetainedInstanceStore by retainedInstanceStore {

    override fun put(key: Any, value: Any): Any? {
        if (!retainedInstanceStore.contains(key)) {
            if (value is Delegate) {
                value.init(getApplication(), savedStateHandle, viewModelScope)
            }
        }
        return retainedInstanceStore.put(key, value)
    }

    override fun onCleared() {
        retainedInstanceStore.forEachInstanceOf(Delegate::class.java) {
            it.deinit()
        }
        super.onCleared()
    }

    interface Delegate {
        fun init(vararg args: Any) {}
        fun deinit() {}
    }
}

private inline fun <T> MutableMap<Any, Any>.forEachInstanceOf(
    classRef: Class<T>,
    block: (T) -> Unit
) = asSequence()
    .filterIsInstance(classRef)
    .forEach(block)