package dev.marcellogalhardo.retained.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import dev.marcellogalhardo.retained.fragment.retain

class SampleFragment : Fragment() {

    private val presenter by retain { entry ->
        ComplexPresenter(entry.savedStateHandle, entry.scope, 5)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sample, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.fragmentTextView).apply {
            text = "Fragment: ${presenter.counter}"
            setOnClickListener {
                presenter.counter++
                text = "Fragment: ${presenter.counter}"
            }
        }
    }
}
