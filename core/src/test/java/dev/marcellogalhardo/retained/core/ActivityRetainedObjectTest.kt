package dev.marcellogalhardo.retained.core

import androidx.lifecycle.Lifecycle
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

    @Test
    fun `should request correct 'key' when creating a retained object`() {
        launchActivity<EmptyActivity>().apply {
            onActivity { sut ->
                val vm by sut.retain { entry -> CounterViewModel(entry) }

                assertThat(vm.entry.key).isEqualTo(CounterViewModel::class.java.name)
            }
        }
    }

    @Test
    fun `should request correct 'classRef' when creating a retained object`() {
        launchActivity<EmptyActivity>().apply {
            onActivity { sut ->
                val vm by sut.retain { entry -> CounterViewModel(entry) }

                assertThat(vm.entry.classRef).isEqualTo(CounterViewModel::class)
            }
        }
    }

    @Test
    fun `should call 'onClearedListeners' when scope is destroyed`() {
        launchActivity<EmptyActivity>().apply {
            var vm: Lazy<CounterViewModel>? = null
            onActivity { sut ->
                vm = sut.retain { entry -> CounterViewModel(entry) }
            }
            assertThat(vm?.value?.isCleared).isFalse()
            moveToState(Lifecycle.State.DESTROYED)
            assertThat(vm?.value?.isCleared).isTrue()
        }
    }
}

internal class CounterViewModel(val entry: RetainedEntry) : OnClearedListener {

    var isCleared: Boolean = false

    var count: Int
        get() = entry.savedStateHandle.get("count") ?: 0
        set(value) {
            entry.savedStateHandle.set("count", value)
        }

    override fun onCleared() {
        isCleared = true
    }
}
