package com.marcellogalhardo.retainedinstance.sample

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.marcellogalhardo.retainedinstance.retainInstance

class SampleActivity : AppCompatActivity() {

    private val presenter: SimplePresenter by retainInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)
        Log.v("Presenter.Counter", presenter.counter.toString())
        presenter.counter++
        findViewById<TextView>(R.id.activityTextView).apply {
            text = "Activity: ${presenter.counter}"
            setOnClickListener {
                presenter.counter++
                text = "Activity: ${presenter.counter}"
            }
        }
    }
}