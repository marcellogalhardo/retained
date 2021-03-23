package dev.marcellogalhardo.retained.core

import androidx.lifecycle.ViewModel

/**
 * Listener added to [RetainedEntry.onClearedListeners].
 */
fun interface OnClearedListener {

    /**
     * Called when [ViewModel.onCleared] is called.
     */
    fun onCleared()
}
