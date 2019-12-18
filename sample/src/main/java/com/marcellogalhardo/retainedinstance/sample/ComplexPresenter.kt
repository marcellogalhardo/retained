package com.marcellogalhardo.retainedinstance.sample

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.marcellogalhardo.retainedinstance.RetainedInstance

class ComplexPresenter(
    var counter: Int
) : RetainedInstance.ApplicationAttachable, RetainedInstance.SavedStateHandleAttachable {

    private lateinit var application: Application

    override fun attachApplication(application: Application) {
        this.application = application
    }

    override fun attachSavedStateHandle(savedStateHandle: SavedStateHandle) {
        print(savedStateHandle.toString())
    }
}