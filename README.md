# Retained Instance

A lightweight library built on top of Android Architecture Component ViewModel to simplify how UI Controllers (e.g., `Activity`, `Fragment` & `NavBackStackEntry`) retain instances on Android.

- Eliminate `ViewModel` inheritance.
- Eliminate `SavedStateHandle` complexity.
- Eliminate `ViewModelProvider.Factory` need.
- Easy access to `ViewModel` scoped properties: `CoroutineScope` (`viewModelScope`), `SavedStateHandle`, and many others.
- Enable composition: callbacks can be listened with `OnClearedListener`.

**Motivation:** Retained was originally created to share a `ViewModel` in Kotlin Multiplatform projects between Android & iOS with ease.

## Download

**Step 1.** Add it in your root *build.gradle* at the end of repositories:
```gradle
allprojects {
	repositories {
		mavenCentral()
	}
}
```

**Step 2.** Add the dependency
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
}
```

(Please replace `{Tag}` with the [latest version numbers](https://github.com/marcellogalhardo/retained/releases): [![](https://jitpack.io/v/marcellogalhardo/retained.svg)](https://jitpack.io/#marcellogalhardo/retained))

## Usage

The following sections demonstrate the use of generated binding classes in activities and fragments.

First, declare the class you would like to retain. For simplicity, we will use the following one:

```kotlin
data class ViewModel(var counter: Int = 0)
```

### Use Retained in an Activity

To retain an instance with an `Activity`, do:

```kotlin
class CounterActivity : AppCompatActivity() {

    private val viewModel: ViewModel by retain { ViewModel(counter = 5) }
}
```

### Use Retained in a Fragment

To retain an instance with a `Fragment`, do:

```kotlin
class CounterFragment : Fragment() {

    private val viewModel: ViewModel by retain { ViewModel() }
}
```

#### Share a Retained between fragments

To share a Retained instance between one or more `Fragment`, do:

```kotlin
class CounterFragment : Fragment() {

    private val sharedViewModel: ViewModel by retainInActivity { ViewModel() }
}
```

### Use Retained in a NavGraph

To retain an instance with a `NavGraph`, do:

```kotlin
class CounterFragment : Fragment() {

    private val viewModel: ViewModel by retainInNavGraph(R.navigation.child_graph) { ViewModel() }
}
```

### Use Retained in Compose

To retain an instance in Compose, do:

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

Retained includes support for Kotlin coroutines, `SavedStateHandle`, and more.

#### Custom parameters from Jetpack's ViewModel

When retaining an instance, you have access to a `RetainedEntry` which contains all parameters you might need.

```kotlin
@Composable
fun SampleView() {
    val viewModel = retain { entry -> ViewModel(entry.scope) }
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
