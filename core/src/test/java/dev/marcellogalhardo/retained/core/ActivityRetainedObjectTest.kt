package dev.marcellogalhardo.retained.core

import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.marcellogalhardo.retained.core.test.EmptyActivity
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class ActivityRetainedObjectTest {

    @Test
    fun `should retain object when Activity is recreated`() {
        launchActivity<EmptyActivity>().apply {
            onActivity { sut ->
                val vm by sut.retain { entry -> CounterViewModel(entry) }
                vm.count += 1
            }
            recreate()
            onActivity { sut ->
                val vm by sut.retain { entry -> CounterViewModel(entry) }
                assertThat(vm.count).isEqualTo(1)
            }
        }
    }
}

internal class CounterViewModel(val entry: RetainedEntry) {

    var count: Int
        get() = entry.savedStateHandle.get("count") ?: 0
        set(value) {
            entry.savedStateHandle.set("count", value)
        }
}
