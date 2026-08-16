# Retained Instance

A lightweight library built on top of Android Architecture Component ViewModel to simplify how UI Controllers (e.g., `Activity`, `Fragment` & `NavBackStackEntry`) retain instances on Android.

- Eliminate `ViewModel` inheritance.
- Eliminate `ViewModelProvider.Factory` need.
- Easy access to `ViewModel` scoped properties: `CoroutineScope` (`viewModelScope`), `SavedStateHandle`, and many others.
- Automatic resource & lifecycle management via `AutoCloseable` and `LifecycleObserver`.

**Motivation:** Retained was originally created to share a `ViewModel` in Kotlin Multiplatform projects between Android & iOS with ease.

## Download

```gradle
dependencies {
    // `Activity` support
    implementation 'dev.marcellogalhardo:retained-activity:{Tag}'

    // `Fragment` support, includes `Activity` support
    implementation 'dev.marcellogalhardo:retained-fragment:{Tag}'

    // Navigation support
    implementation 'dev.marcellogalhardo:retained-navigation:{Tag}'    

    // Navigation with Fragment support, includes `Navigation` support
    implementation 'dev.marcellogalhardo:retained-navigation-fragment:{Tag}'
    
    // Compose support
    implementation 'dev.marcellogalhardo:retained-compose:{Tag}'
    
    // View support (experimental)
    implementation 'dev.marcellogalhardo:retained-view:{Tag}'
    implementation 'dev.marcellogalhardo:retained-navigation-view:{Tag}'
}
```

(Please replace `{Tag}` with the [latest version numbers](https://github.com/marcellogalhardo/retained/releases))

## Usage

The following sections demonstrate how to retain instances in activities and fragments. For simplicity, all examples will retain the following class:

```kotlin
class ViewModel(var counter: Int = 0)
```

### Use Retained in Activities and Fragments

```kotlin
// retain an instance in an Activity:
class CounterActivity : AppCompatActivity() {
    private val viewModel: ViewModel by retain { ViewModel() }
}

// retain an instance in a Fragment:
class CounterFragment : Fragment() {
    private val viewModel: ViewModel by retain { ViewModel() }
}

// share an instance between Fragments scoped to the Activity
class CounterFragment : Fragment() {
    private val sharedViewModel: ViewModel by retainInActivity { ViewModel() }
}

// share an instance between Fragments scoped to the NavGraph
class CounterFragment : Fragment() {
    private val viewModel: ViewModel by retainInNavGraph(R.navigation.nav_graph) { ViewModel() }
}
```

### Compose Support

```kotlin
@Composable
fun SampleView() {
    val viewModel = retain { ViewModel() }
    
    val activity: ComponentActivity // find Activity
    val viewModel = retain(owner = activity) { ViewModel() }
    
    val fragment: Fragment // find Fragment
    val viewModel = retain(owner = fragment) { ViewModel() }
    
    val navBackStackEntry: NavBackStackEntry // find NavBackStackEntry
    val viewModel = retain(owner = navBackStackEntry) { ViewModel() }
}
```

### Advanced usage

#### Custom parameters from Jetpack's ViewModel

When retaining an instance, you have access to a `RetainedEntry` which contains all parameters you might need.

```kotlin
@Composable
fun SampleView() {
    val viewModel = retain { entry: RetainedEntry ->
        ViewModel()
    }
    // ...
}
```

The entry exposes a `SavedStateHandle` that can be used to work with the saved state, just like in a regular Android `ViewModel`.

```kotlin
class CounterFragment : Fragment() {
    private val viewModel: ViewModel by retain { entry -> 
        ViewModel(counter = entry.savedStateHandle.get<Int>("count"))
    }
    // ...
}
```

It also exposes a `CoroutineScope` that works just like `viewModelScope` from the Android `ViewModel`.

```kotlin
class Presenter(scope: CoroutineScope) { /* ... */ }

fun SampleFragment() {
    private val presenter: Presenter by retain { entry -> 
        Presenter(scope = entry.scope)
    }
    // ...
}
```

For more details, see `RetainedEntry`.

#### Automatic Resource Management (AutoCloseable)

If the retained instance implements `AutoCloseable`, `retained` automatically closes it when the host `ViewModel` is cleared (`ViewModel.onCleared`):

```kotlin
class ResourcePresenter : AutoCloseable {
    override fun close() {
        // Automatically called when the host ViewModel is cleared
    }
}
```

#### Automatic Lifecycle Management (LifecycleObserver)

If the retained instance implements AndroidX `LifecycleObserver` (or `DefaultLifecycleObserver`), `retained` automatically binds it to the host `LifecycleOwner`:

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

If the retained instance implements `SavedStateRegistry.SavedStateProvider`, `retained` automatically registers it to the host `SavedStateHandle`:

```kotlin
class FormPresenter : SavedStateRegistry.SavedStateProvider {
    override fun saveState(): Bundle = bundleOf(
        "step" to currentStep
    )
}
```

#### View support (experimental)

Besides Activities and Fragments, it's also possible to retain instances in a view. There are a couple of extra modules for that:

```gradle
dependencies {
    implementation 'dev.marcellogalhardo:retained-view:{Tag}'
    implementation 'dev.marcellogalhardo:retained-navigation-view:{Tag}'
}
```

The `retained-view` module exposes `retainInActivity` and `retain`, which will scope the instance to the parent being it an activity or a fragment. The `retained-view-navigation` module exposes `retainInNavGraph` to retain instances scoped to the `NavGraph`.

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
