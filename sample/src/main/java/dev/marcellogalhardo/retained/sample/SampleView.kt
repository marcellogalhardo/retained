package dev.marcellogalhardo.retained.sample

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import dev.marcellogalhardo.retained.view.retain

class SampleView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val presenter: SimplePresenter by retain {
        SimplePresenter()
    }

    // TODO: make this a better sample
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        presenter.counter++
        Log.d("SampleView", "counter = ${presenter.counter}")
    }
}
