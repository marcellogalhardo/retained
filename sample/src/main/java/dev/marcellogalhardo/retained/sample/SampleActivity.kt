package dev.marcellogalhardo.retained.sample

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import dev.marcellogalhardo.retained.core.retain

class SampleActivity : AppCompatActivity() {

    private val presenter: SimplePresenter by retain {
        SimplePresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)
        findViewById<TextView>(R.id.activityTextView).apply {
            text = "Activity: ${presenter.counter}"
            setOnClickListener {
                presenter.counter++
                text = "Activity: ${presenter.counter}"
            }
        }
    }
}