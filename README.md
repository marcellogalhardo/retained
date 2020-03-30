# Retained Instance

A lightweight library built on top of Android Architecture Component ViewModel to simplify how UI Controllers (e.g., Activity or Fragment) retain instances.

Retained Instance was created to leverage ViewModels in a consistent, reliable and composable way - no inheritances, no factories and no parameters like application or saved state handle.

The Public API is stable and won't change except by providing support to the latest features of Android's ViewModel.

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
	implementation 'com.github.marcellogalhardo.android-retained-instance:core:{Tag}'
	
	// For Fragments support. You don't need to add retained-instance - we included for you!
	implementation 'com.github.marcellogalhardo.android-retained-instance:fragment:{Tag}'
}
```
(Please replace `{Tag}` with the [latest version numbers](https://github.com/marcellogalhardo/android-retained-instance/releases): [![](https://jitpack.io/v/marcellogalhardo/android-retained-instance.svg)](https://jitpack.io/#marcellogalhardo/android-retained-instance))

That's it!

## Usage

First, create the object that you want to retain instances.

```kotlin
data class ViewModel(
    var counter: Int = 0
)
```

Then on your UI Controller ask it to retain the instance.

```kotlin
class SampleActivity : AppCompatActivity() {

    private val viewModel: ViewModel by retainInstance { ViewModel(counter = 5) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ...
        Log.v("ViewModel.Counter", viewModel.counter.toString())
        viewModel.counter++
    }
}
```

Rotate the screen and check the instance will be preserved.

### Fragments

We also provide support for Fragments - same API:

```kotlin
class SampleFragment : Fragment() {

    private val viewModel: ViewModel by retainInstance() // No-args constructor used.

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // ...
        Log.v("ViewModel.Counter", viewModel.counter.toString())
        viewModel.counter++
    }
}
```

### Advanced usage

#### Closing your instance

If your retained instance implements `Closeable`, the `close` will be invoked when the bounded UI Controller gets terminated - in other words, when [ViewModel.onCleared](https://developer.android.com/reference/androidx/lifecycle/ViewModel.html#onCleared()) is invoked.

```kotlin
class ViewModel(
    private val coroutineScope: CoroutineScope
) : Closeable {

    override fun close() {
        coroutineScope.coroutineContext.cancelChildren()
    }
}
```

#### Custom parameters from Android's ViewModel

When creating an object you can access the internal `RetainedStore` to get parameters like `application`, `savedStateHandle` or `viewModelScope` to your retained instance.

```kotlin
class MyFragment : Fragment() {

    private val viewModel: ViewModel by retainInstance { retainedStore ->
        ViewModel(retainedStore.viewModelScope)
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
