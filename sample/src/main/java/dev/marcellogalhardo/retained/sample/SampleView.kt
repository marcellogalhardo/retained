package dev.marcellogalhardo.retained.sample

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import dev.marcellogalhardo.retained.view.retain

class SampleView(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {

    private val presenter: SimplePresenter by retain {
        SimplePresenter()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val hostName = resolveHostName()
        text = "$hostName view: ${presenter.counter}"
        setOnClickListener {
            presenter.counter++
            text = "$hostName view: ${presenter.counter}"
        }
    }

    private fun resolveHostName() =
        if (runCatching { findFragment<Fragment>() }.isSuccess) "Fragment" else "Activity"
}
