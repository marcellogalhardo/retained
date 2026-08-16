package dev.marcellogalhardo.retained.core.internal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.testing.viewModelScenario
import com.google.common.truth.Truth.assertThat
import kotlin.test.Test

internal class RetainedViewModelTest {
    @Test
    fun retainedViewModel_shouldExposeCorrectEntryProperties() {
        val handle = SavedStateHandle(mapOf("key" to "value"))
        val scenario =
            viewModelScenario {
                RetainedViewModel(
                    key = "test_key",
                    retainedClass = String::class,
                    savedStateHandle = handle,
                    createRetainedObject = { "retained_object" },
                )
            }

        scenario.viewModel.let { vm ->
            assertThat(vm.key).isEqualTo("test_key")
            assertThat(vm.retainedClass).isEqualTo(String::class)
            assertThat(vm.savedStateHandle).isEqualTo(handle)
            assertThat(vm.retainedInstance).isEqualTo("retained_object")
        }
    }

    @Test
    fun retainedViewModel_shouldCloseAutoCloseableWhenCleared() {
        var closed = false
        val closeable = AutoCloseable { closed = true }

        val scenario =
            viewModelScenario {
                RetainedViewModel(
                    key = "closeable_key",
                    retainedClass = AutoCloseable::class,
                    savedStateHandle = SavedStateHandle(),
                    createRetainedObject = { closeable },
                )
            }

        assertThat(closed).isFalse()

        scenario.viewModel.onCleared()

        assertThat(closed).isTrue()
    }

    @Test
    fun retainedViewModel_shouldCloseRegisteredCloseablesWhenCleared() {
        var closeableCount = 0

        val scenario =
            viewModelScenario {
                RetainedViewModel(
                    key = "closeables_key",
                    retainedClass = Any::class,
                    savedStateHandle = SavedStateHandle(),
                    createRetainedObject = { entry ->
                        entry.closeables += AutoCloseable { closeableCount++ }
                        entry.closeables += AutoCloseable { closeableCount++ }
                        "custom_object"
                    },
                )
            }

        assertThat(closeableCount).isEqualTo(0)

        scenario.viewModel.onCleared()

        assertThat(closeableCount).isEqualTo(2)
    }
}
