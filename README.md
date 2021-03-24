# Retained Instance

A lightweight library built on top of Android Architecture Component ViewModel to simplify how UI Controllers (e.g., Activity, Fragment & Composable) retain instances on Android.

**Motivation:** Retained was a single file that I was copying into all side-projects I make with Kotlin Multiplatform to abstract Android Jetpack's ViewModel. Now it is a library to avoid copy and paste. (:

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
    // For Activity support.
    implementation 'com.github.marcellogalhardo.retained:retained-core:{Tag}'

    // For Fragment support. You don't need to add retained-core - it is included as `api` by default.
    implementation 'com.github.marcellogalhardo.retained:retained-fragment:{Tag}'

    // For Compose support. You don't need to add retained-core - it is included as `api` by default.
    implementation 'com.github.marcellogalhardo.retained:retained-compose:{Tag}'
}
```
(Please replace `{Tag}` with the [latest version numbers](https://github.com/marcellogalhardo/retained/releases): [![](https://jitpack.io/v/marcellogalhardo/retained.svg)](https://jitpack.io/#marcellogalhardo/retained))

## Usage

Create the object that you want to retain instances.

```kotlin
data class ViewModel(var counter: Int = 0)
```

Now retain it on any UIController:

```kotlin
class SampleActivity : AppCompatActivity() {

    private val viewModel: ViewModel by retain { ViewModel(counter = 5) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ...
        Log.v("ViewModel.Counter", viewModel.counter.toString())
        viewModel.counter++
    }
}
```

Rotate the screen and check the instance will be retained across configuration changes.

### Fragments

We support Fragments with same API:

```kotlin
class SampleFragment : Fragment() {

    private val viewModel: ViewModel by retain { ViewModel() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // ...
        Log.v("ViewModel.Counter", viewModel.counter.toString())
        viewModel.counter++
    }
}
```

### Jetpack Compose

Again, same API:

```kotlin
@Composable
fun SampleView() {
    val viewModel = retain { ViewModel() }
    // ...
    Log.v("ViewModel.Counter", viewModel.counter.toString())
    viewModel.counter++
}
```

### Advanced usage

#### Custom parameters from Jetpack's ViewModel

When creating an object you might want to access the `RetainedEntry` to get runtime parameters like `savedStateHandle: SavedStateHandle` or `scope: CoroutineScope` (`viewModelScope`) to assisted inject your retained instance.

```kotlin
@Composable
fun SampleView() {
    val viewModel = retain { entry -> ViewModel(entry.scope) }
    // ...
}
```

#### Listening onCleared calls

When creating an object you might be interest to know when it will be cleared by the Android system. For that, you can set a listener to `entry.onClearedListeners` and any listener will be invoked when the host [ViewModel.onCleared](https://developer.android.com/reference/androidx/lifecycle/ViewModel.html#onCleared()) is invoked.

```kotlin
@Composable
fun SampleView() {
    val viewModel = retain { entry -> 
        entry.onClearedListeners += { println("Invoked when the host 'ViewModel.onCleared' is called") }
        // ...
    }
    // ...
}
```

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
