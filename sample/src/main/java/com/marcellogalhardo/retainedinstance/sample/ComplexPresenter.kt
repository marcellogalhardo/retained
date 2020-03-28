package com.marcellogalhardo.retainedinstance.sample

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import java.io.Closeable

class ComplexPresenter(
    private val application: Application,
    private val savedStateHandle: SavedStateHandle,
    private val coroutineScope: CoroutineScope,
    var counter: Int
) : Closeable {

    override fun close() {
        coroutineScope.coroutineContext.cancelChildren()
    }
}