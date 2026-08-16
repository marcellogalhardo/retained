# Retained Instance

Retained is a lightweight Kotlin Multiplatform library built on top of AndroidX `ViewModel`. It provides a unified API to retain object instances across Kotlin Multiplatform targets.

- Remove the need for `ViewModel` inheritance.
- Remove the need for `ViewModelProvider.Factory`.
- Provide direct access to `ViewModel` properties: `CoroutineScope` (`viewModelScope`), `SavedStateHandle`, and parameters.
- Automatic resource, lifecycle, and saved state management via `AutoCloseable`, `LifecycleObserver`, and `SavedStateProvider`.

## Download

```gradle
dependencies {
    // Core Kotlin Multiplatform support
    implementation 'dev.marcellogalhardo:retained-core:{Tag}'

    // `Activity` support
    implementation 'dev.marcellogalhardo:retained-activity:{Tag}'

    // `Fragment` support (includes `Activity` support)
    implementation 'dev.marcellogalhardo:retained-fragment:{Tag}'

    // Navigation support
    implementation 'dev.marcellogalhardo:retained-navigation:{Tag}'    

    // Navigation with Fragment support (includes `Navigation` support)
    implementation 'dev.marcellogalhardo:retained-navigation-fragment:{Tag}'
    
    // Compose support (Android, iOS, Desktop)
    implementation 'dev.marcellogalhardo:retained-compose:{Tag}'
    
    // View support (experimental)
    implementation 'dev.marcellogalhardo:retained-view:{Tag}'
    implementation 'dev.marcellogalhardo:retained-navigation-view:{Tag}'
}
```

(Replace `{Tag}` with the [latest release version](https://github.com/marcellogalhardo/retained/releases))

## Usage

This section shows how to retain instances in activities and fragments. All examples use this class:

```kotlin
class ViewModel(var counter: Int = 0)
```

### Use Retained in Activities and Fragments

```kotlin
// Retain an instance in an Activity:
class CounterActivity : AppCompatActivity() {
    private val viewModel: ViewModel by retain { ViewModel() }
}

// Retain an instance in a Fragment:
class CounterFragment : Fragment() {
    private val viewModel: ViewModel by retain { ViewModel() }
}

// Share an instance between Fragments scoped to the Activity
class CounterFragment : Fragment() {
    private val sharedViewModel: ViewModel by retainInActivity { ViewModel() }
}

// Share an instance between Fragments scoped to the NavGraph
class CounterFragment : Fragment() {
    private val viewModel: ViewModel by retainInNavGraph(R.navigation.nav_graph) { ViewModel() }
}
```

### Use Retained in Compose

```kotlin
@Composable
fun CounterScreen() {
    // Scope to LocalViewModelStoreOwner (default)
    val viewModel = retain { ViewModel() }

    // Scope to ComponentActivity (Android)
    val activityViewModel = retainInActivity { ViewModel() }

    // Scope to a specific ViewModelStoreOwner (e.g. NavBackStackEntry)
    val navBackStackEntry: NavBackStackEntry // Find NavBackStackEntry
    val scopedViewModel = retain(owner = navBackStackEntry) { ViewModel() }
}
```

### Advanced Usage

#### Custom Parameters from Jetpack ViewModel

When you retain an instance, `RetainedEntry` provides access to host parameters.

```kotlin
@Composable
fun CounterScreen() {
    val viewModel = retain { entry: RetainedEntry ->
        ViewModel()
    }
    // ...
}
```

`RetainedEntry` provides a `SavedStateHandle` to save and restore state.

```kotlin
class CounterFragment : Fragment() {
    private val viewModel: ViewModel by retain { entry -> 
        ViewModel(counter = entry.savedStateHandle.get<Int>("count"))
    }
    // ...
}
```

`RetainedEntry` provides a `CoroutineScope` that matches `viewModelScope`.

```kotlin
class Presenter(scope: CoroutineScope) { /* ... */ }

class SampleFragment : Fragment() {
    private val presenter: Presenter by retain { entry -> 
        Presenter(scope = entry.scope)
    }
    // ...
}
```

For more details, see `RetainedEntry`.

#### Automatic Resource Management (AutoCloseable)

If a retained instance implements `AutoCloseable`, `retained` automatically closes it when the host `ViewModel` is cleared (`ViewModel.onCleared`).

```kotlin
class ResourcePresenter : AutoCloseable {
    override fun close() {
        // Automatically called when the host ViewModel is cleared
    }
}
```

#### Automatic Lifecycle Management (LifecycleObserver)

If a retained instance implements `LifecycleObserver` (or `DefaultLifecycleObserver`), `retained` automatically binds it to the host `LifecycleOwner`.

```kotlin
class LocationPresenter : DefaultLifecycleObserver {
    override fun onStart(owner: LifecycleOwner) {
        // Automatically called when host starts
    }

    override fun onStop(owner: LifecycleOwner) {
        // Automatically called when host stops
    }
}
```

#### Automatic SavedState Management (SavedStateProvider)

If a retained instance implements `SavedStateRegistry.SavedStateProvider`, `retained` automatically registers it to the host `SavedStateHandle`.

```kotlin
class FormPresenter : SavedStateRegistry.SavedStateProvider {
    override fun saveState(): Bundle = bundleOf(
        "step" to currentStep
    )
}
```

#### View Support (Experimental)

You can also retain instances in a `View`. Use these modules:

```gradle
dependencies {
    implementation 'dev.marcellogalhardo:retained-view:{Tag}'
    implementation 'dev.marcellogalhardo:retained-navigation-view:{Tag}'
}
```

The `retained-view` module provides `retain` and `retainInActivity` to scope instances to an `Activity` or `Fragment`. The `retained-navigation-view` module provides `retainInNavGraph` to scope instances to a `NavGraph`.

License
-------

    Copyright 2019 Marcello Galhardo

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
