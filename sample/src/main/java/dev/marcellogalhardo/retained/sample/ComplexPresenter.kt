package dev.marcellogalhardo.retained.sample

import androidx.lifecycle.SavedStateHandle
import dev.marcellogalhardo.retained.core.OnClearedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren

class ComplexPresenter(
    private val savedStateHandle: SavedStateHandle,
    private val coroutineScope: CoroutineScope,
    var counter: Int,
) : OnClearedListener {

    override fun onCleared() {
        coroutineScope.coroutineContext.cancelChildren()
    }
}
