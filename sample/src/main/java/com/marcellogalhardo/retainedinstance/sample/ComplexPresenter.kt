package com.marcellogalhardo.retainedinstance.sample

import android.app.Application
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.marcellogalhardo.retainedinstance.RetainedInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren

private const val TAG = "Delegate"

class ComplexPresenter(
    var counter: Int
) : RetainedInstance.Delegate{

    private lateinit var application: Application
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var coroutineScope: CoroutineScope

    override fun init(vararg args: Any) {
        for (arg in args) {
            when (arg) {
                is Application -> application = arg
                is SavedStateHandle -> savedStateHandle = arg
                is CoroutineScope -> coroutineScope = arg
            }
        }
        Log.v(TAG, application.toString())
        Log.v(TAG, savedStateHandle.toString())
        Log.v(TAG, coroutineScope.toString())
    }

    override fun deinit() {
        coroutineScope.coroutineContext.cancelChildren()
    }
}