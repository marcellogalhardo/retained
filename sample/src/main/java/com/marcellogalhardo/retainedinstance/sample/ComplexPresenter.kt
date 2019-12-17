package com.marcellogalhardo.retainedinstance.sample

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.marcellogalhardo.retainedinstance.RetainedInstance

class ComplexPresenter(
    var counter: Int
) : RetainedInstance.OnAttachApplicationListener, RetainedInstance.OnAttachSavedStateHandleListener {

    private lateinit var application: Application

    override fun onAttachApplication(application: Application) {
        this.application = application
    }

    override fun onAttachSavedStateHandle(savedStateHandle: SavedStateHandle) {
        print(savedStateHandle.toString())
    }
}