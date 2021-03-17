package dev.marcellogalhardo.retained.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.marcellogalhardo.retained.core.RetainedEntry
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class FragmentRetainedObjectTest {

    @Test
    fun `should retain object when owner is recreated`() {
        launchFragmentInContainer { CounterFragment() }.apply {
            onFragment { fragment ->
                fragment.vm.count += 1
                fragment.vmInActivity.count += 3
            }
            recreate()
            onFragment { fragment ->
                assertThat(fragment.vm.count).isEqualTo(1)
                assertThat(fragment.vmInActivity.count).isEqualTo(3)
                // As this fragment does not contain a 'parentFragment', the parent is the Activity
                assertThat(fragment.vmInParent.count).isEqualTo(fragment.vmInActivity.count)
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

internal class CounterFragment : Fragment() {

    val vm by retain { entry -> CounterViewModel(entry) }
    val vmInParent by retainInParent { entry -> CounterViewModel(entry) }
    val vmInActivity by retainInActivity { entry -> CounterViewModel(entry) }
}
