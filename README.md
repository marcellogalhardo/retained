# Retained Instance

A lightweight library built on top of Android Architecture Component ViewModel to simplify how UI Controllers (e.g., Activity, Fragment & Composable) retain instances.

Retained was a single file that I started to copy into all little apps I make with Kotlin Multiplatform with the intention to abstract away Android Jetpack's ViewModel - now it's a library.

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
	implementation 'com.github.marcellogalhardo.retained:core:{Tag}'

    // For Fragment support. You don't need to add retained-instance - it is included as `api` by default.
    implementation 'com.github.marcellogalhardo.retained:fragment:{Tag}'
	
	// For Compose support. You don't need to add retained-instance - it is included as `api` by default.
	implementation 'com.github.marcellogalhardo.retained:compose:{Tag}'
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

#### Disposing your instance

If your retained instance implements `DisposableHandle`, the `dispose` will be invoked when the bounded UI Controller gets terminated - in other words, when [ViewModel.onCleared](https://developer.android.com/reference/androidx/lifecycle/ViewModel.html#onCleared()) is invoked.

```kotlin
class ViewModel(
    private val coroutineScope: CoroutineScope
) : DisposableHandle {

    override fun dispose() {
        coroutineScope.cancel()
    }
}
```

#### Custom parameters from Jetpack's ViewModel

When creating an object you can access the internal `RetainedContext` to get runtime parameters like `retainedHandle: SavedStateHandle` or `retainedScope: CoroutineScope` (`viewModelScope`) to your retained instance.

```kotlin
class MyFragment : Fragment() {

    private val viewModel: ViewModel by retain {
        ViewModel(retainedScope)
    }
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
