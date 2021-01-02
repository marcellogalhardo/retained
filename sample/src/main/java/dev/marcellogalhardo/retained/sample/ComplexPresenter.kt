package dev.marcellogalhardo.retained.sample

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import java.io.Closeable

class ComplexPresenter(
    private val savedStateHandle: SavedStateHandle,
    private val coroutineScope: CoroutineScope,
    var counter: Int
) : Closeable {

    override fun close() {
        coroutineScope.coroutineContext.cancelChildren()
    }
}