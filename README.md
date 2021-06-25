# Retained Instance

A lightweight library built on top of Android Architecture Component ViewModel to simplify how UI Controllers (e.g., `Activity`, `Fragment` & `NavBackStackEntry`) retain instances on Android.

- Eliminate `ViewModel` inheritance.
- Eliminate `ViewModelProvider.Factory` need.
- Easy access to `ViewModel` scoped properties: `CoroutineScope` (`viewModelScope`), `SavedStateHandle`, and many others.
- Enable composition: callbacks can be listened with `OnClearedListener`.

**Motivation:** Retained was originally created to share a `ViewModel` in Kotlin Multiplatform projects between Android & iOS with ease.

## Download

**Step 1.** Add it in your root *build.gradle* at the end of repositories:
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

**Step 2.** Add the dependency
```gradle
dependencies {
    // `Activity` support
    implementation 'com.github.marcellogalhardo.retained:retained-activity:{Tag}'

    // `Fragment` support, includes `Activity` support
    implementation 'com.github.marcellogalhardo.retained:retained-fragment:{Tag}'

    // Navigation support
    implementation 'com.github.marcellogalhardo.retained:retained-navigation:{Tag}'

    // Navigation with Fragment support, includes `Navigation` support
    implementation 'com.github.marcellogalhardo.retained:retained-navigation-fragment:{Tag}'
}
```

(Please replace `{Tag}` with the [latest version numbers](https://github.com/marcellogalhardo/retained/releases): [![](https://jitpack.io/v/marcellogalhardo/retained.svg)](https://jitpack.io/#marcellogalhardo/retained))

## Usage

The following sections demonstrate how to retain instances in activities and fragments. For simplicity, all examples will retain the following class:

```kotlin
data class ViewModel(var counter: Int = 0)
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

### Use Retained in Compose

```kotlin
@Composable
fun SampleView() {
    // Using an Activity
    val activity: ComponentActivity // find Activity
    val viewModel by activity.retain { ViewModel() }

    // Using an Activity
    val fragment: Fragment // find Fragment
    val viewModel by fragment.retain { ViewModel() }

    // Using NavBackStackEntry
    val navBackStackEntry: NavBackStackEntry // find NavBackStackEntry
    val viewModel by navBackStackEntry.retain { ViewModel() }
}
```

### Advanced usage

#### Custom parameters from Jetpack's ViewModel

When retaining an instance, you have access to a `RetainedEntry` which contains all parameters you might need.

```kotlin
@Composable
fun SampleView() {
    val viewModel = retain { entry -> ViewModel(entry.scope) }
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
class Presenter(scope: CoroutineScope) { ... }

fun SampleFragment() {
    private val presenter: Presenter by retain { entry -> 
        Presenter(scope = entry.scope)
    }
    // ...
}
```

For more details, see `RetainedEntry`.

#### Listening `onCleared` calls

When retaining an instance, you can use the `RetainedEntry` to be notified when a retained instance is cleared (`ViewModel.onCleared`).

```kotlin
@Composable
fun SampleView() {
    val viewModel by retain { entry ->
        entry.onClearedListeners += {
            println("Invoked when the host 'ViewModel.onCleared' is called")
        }
    }
    // ...
}
```

As a convenience, if the retained instance implements the `OnClearedListener` interface, it will be automatically added to `onClearedListeners` and notified.

#### View support (Experimental)

Besides Activities and Fragments, it's also possible to retain instances in a view. There are a couple of extra modules for that:

```gradle
dependencies {
    implementation 'com.github.marcellogalhardo.retained:retained-view:{Tag}'
    implementation 'com.github.marcellogalhardo.retained:retained-view-navigation:{Tag}'
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
