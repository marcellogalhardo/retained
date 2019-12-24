# Retained Instance

A lightweight library built on top of Android Architecture Component ViewModel to simplify how UI Controllers (e.g., Activity or Fragment) retain instances.

Retained Instance was created to leverage ViewModels in a consistent, reliable and composable way - no inheritances, no factories and no parameters like application or saved state handle.

The Public API is considered stable and won't change except by providing support to the latest features of Android's ViewModel.

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
	implementation 'com.github.marcellogalhardo:retained-instance:{Tag}'
	
	// For Fragments support. You don't need to add retained-instance - we included for you!
	implementation 'com.github.marcellogalhardo:retained-instance-fragment:{Tag}'
}
```
(Please replace `{Tag}` with the [latest version numbers](https://github.com/marcellogalhardo/retained-instance/releases): [![](https://jitpack.io/v/marcellogalhardo/retained-instance.svg)](https://jitpack.io/#marcellogalhardo/retained-instance))

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

    private val viewModel: ViewModel by retainedInstances { ViewModel(counter = 5) }

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

    private val viewModel: ViewModel by retainedInstances() // No-args constructor used.

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // ...
        Log.v("ViewModel.Counter", viewModel.counter.toString())
        viewModel.counter++
    }
}
```

### Delegates

Delegate is an interface that allows you to communicate your instance with the Android Framework - you plug a "Delegate interface" to your instance and we will delegate the given from the UI Controller to yours instance.

The `init(vararg args: Any)` method allows you to receive a callback when an instance is created for the first time with an array of parameters from the Android system.

The following args are available today:
- application: Application
- savedStateHandle: SavedStateHandle
- coroutineScope: CoroutineScope

The `deinit()` method allows you to receive a callback `deinitialize()` when the instance is terminated - we use [ViewModel.onCleared](https://developer.android.com/reference/androidx/lifecycle/ViewModel.html#onCleared()) for this callback.

```kotlin
class ViewModel : RetainedInstance.Delegate {
    private lateinit var application: Application
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var coroutineScope: CoroutineScope

    override fun init(vararg args: Any) {
        for (arg in args) {
            when (arg) {
                is Application -> application = arg
                is SavedStateHandle -> savedStateHandle = arg
                is CoroutineScope -> coroutineScope = arg
            }
        }
    }

    override fun deinit() {
        coroutineScope.coroutineContext.cancelChildren()
    }
}
```

## Dependency Injection integration

Integrate with a good dependency injection framework is as simple as asking an instance to be retained.
For matter of simplicity, the following example will use a generic [javax.inject](https://docs.oracle.com/javaee/6/api/javax/inject) API.

```kotlin
class InjectedActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelProvider: Provider<ViewModel>
     
    private val viewModel: ViewModel by retainedInstances(instanceProvider = viewModelProvider::get)
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
