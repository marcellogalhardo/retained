package com.marcellogalhardo.retainedinstance

import android.app.Application
import androidx.lifecycle.*

typealias RetainedInstanceValueProducer<T> = () -> T
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
) : AndroidViewModel(application), RetainedInstanceStore by retainedInstanceStore,
    LifecycleObserver {

    override fun put(key: Any, value: Any): Any? {
        if (value is OnAttachApplicationListener) {
            value.onAttachApplication(getApplication())
        }
        if (value is OnAttachSavedStateHandleListener) {
            value.onAttachSavedStateHandle(savedStateHandle)
        }
        return retainedInstanceStore.put(key, value)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    internal fun onCreate() {
        retainedInstanceStore.forEachInstanceOf(OnLifecycleListener::class.java) {
            it.onCreate()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    internal fun onStart() {
        retainedInstanceStore.forEachInstanceOf(OnLifecycleListener::class.java) {
            it.onStart()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    internal fun onResume() {
        retainedInstanceStore.forEachInstanceOf(OnLifecycleListener::class.java) {
            it.onResume()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    internal fun onPause() {
        retainedInstanceStore.forEachInstanceOf(OnLifecycleListener::class.java) {
            it.onPause()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    internal fun onStop() {
        retainedInstanceStore.forEachInstanceOf(OnLifecycleListener::class.java) {
            it.onStop()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    internal fun onDestroy() {
        retainedInstanceStore.forEachInstanceOf(OnLifecycleListener::class.java) {
            it.onDestroy()
        }
    }

    override fun onCleared() {
        retainedInstanceStore.forEachInstanceOf(OnClearedListener::class.java) {
            it.onCleared()
        }
        super.onCleared()
    }

    interface OnLifecycleListener {
        fun onCreate() {}
        fun onStart() {}
        fun onResume() {}
        fun onPause() {}
        fun onStop() {}
        fun onDestroy() {}
    }

    interface OnClearedListener {
        fun onCleared()
    }

    interface OnAttachSavedStateHandleListener {
        fun onAttachSavedStateHandle(savedStateHandle: SavedStateHandle)
    }

    interface OnAttachApplicationListener {
        fun onAttachApplication(application: Application)
    }
}

private inline fun <T> MutableMap<Any, Any>.forEachInstanceOf(
    classRef: Class<T>,
    block: (T) -> Unit
) = asSequence()
    .filterIsInstance(classRef)
    .forEach(block)