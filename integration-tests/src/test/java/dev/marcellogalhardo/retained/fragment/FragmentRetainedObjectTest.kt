package dev.marcellogalhardo.retained.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.marcellogalhardo.retained.core.OnClearedListener
import dev.marcellogalhardo.retained.core.Retained
import dev.marcellogalhardo.retained.core.RetainedEntry
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class FragmentRetainedObjectTest {

    @Test
    fun `should retain object when owner is recreated`() {
        launchFragmentInContainer { CounterFragment() }.run {
            onFragment { sut ->
                val vm by sut.retain { entry -> CounterViewModel(entry) }
                val vmInActivity by sut.retainInActivity { entry -> CounterViewModel(entry) }

                vm.count += 1
                vmInActivity.count += 3
            }
            recreate()
            onFragment { sut ->
                val vm by sut.retain { entry -> CounterViewModel(entry) }
                val vmInActivity by sut.retainInActivity { entry -> CounterViewModel(entry) }

                assertThat(vm.count).isEqualTo(1)
                assertThat(vmInActivity.count).isEqualTo(3)
            }
        }
    }

    @Test
    fun `should request correct 'key' when creating a retained object`() {
        launchFragmentInContainer { CounterFragment() }.run {
            onFragment { sut ->
                val vm by sut.retain { entry -> CounterViewModel(entry) }

                assertThat(vm.entry.key).isEqualTo(CounterViewModel::class.java.name)
            }
        }
    }

    @Test
    fun `should request correct 'classRef' when creating a retained object`() {
        launchFragmentInContainer { CounterFragment() }.run {
            onFragment { sut ->
                val vm by sut.retain { entry -> CounterViewModel(entry) }

                assertThat(vm.entry.retainedClass).isEqualTo(CounterViewModel::class)
            }
        }
    }

    @Test
    fun `should call 'onClearedListeners' when scope is destroyed`() {
        launchFragmentInContainer { CounterFragment() }.run {
            var vm: Retained<CounterViewModel>? = null
            onFragment { sut ->
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

internal class CounterFragment : Fragment()
