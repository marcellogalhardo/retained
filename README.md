# Retained Instance

A lightweight library built on top of Android Architecture Component ViewModel to simplify how UI Controllers (e.g., Activity or Fragment) retain instances.

Retained Instance was created to leverage ViewModels in a consistent, reliable and composable way - no inheritances, no factories and no parameters like application or saved state handle. Instead, we provide a very composable API based on delegation.

You get what you implement - literally, through interfaces - what allow us to easily support new features like the brand new SavedStateHandle out of the box without modifying any code.

This library API is considered stable and won't changed except by providing support to the latest features of Android's ViewModel.

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

Delegates are a set of interface delegation that we use to communicate your instance with the Android Framework - you plug a "Delegate interface" to your instance and we will delegate the given from the UI Controller to yours instance.

#### Initializable

The `Initializable` interface allows you to receive a callback `initialize()` when the instance is created for the first time - after attachments is added to this instance.

```kotlin
class ViewModel : RetainedInstance.Initializable {

    override fun initialize() {
        print("Instance is initialized.")
    }
}
```

#### Deinitializable

The `Deinitializable` interface allows you to receive a callback `deinitialize()` when the instance is terminated - we use [ViewModel.onCleared](https://developer.android.com/reference/androidx/lifecycle/ViewModel.html#onCleared()) for this callback.

```kotlin
class ViewModel : RetainedInstance.Deinitializable {

    override fun deinitialize() {
        print("Instance is terminated.")
    }
}
```

### Attachables

Sometimes you might need to "attach" other instances provided by the Android Framework to your instances. Following the same concept of the Delegates interface, you can use the `*Attachable` ones.

**All attachables** will be called **before** `initialize()`.

##### ApplicationAttachable

The `ApplicationAttachable` interface allows you to receive a callback `attachApplication(application: Application)` **before** the `initiliaze()` - we attach the [AndroidViewModel.application](https://developer.android.com/reference/androidx/lifecycle/AndroidViewModel.html#getApplication()) for you.

```kotlin
class ViewModel : RetainedInstance.ApplicationAttachable {

    private lateinit var application: Application

    override fun attachApplication(application: Application) {
        this.application = application
    }
}
```

#### SavedStateHandleAttachable

The `SavedStateHandleAttachable` interface allows you to receive a callback `attachSavedStateHandle(savedStateHandle: SavedStateHandle)` **before** the `initiliaze()`, which can be used to save the state of your instance in case of [process death](https://developer.android.com/topic/libraries/architecture/saving-states#use_onsaveinstancestate_as_backup_to_handle_system-initiated_process_death) - we use [SavedStateViewModelFactory](https://developer.android.com/reference/androidx/lifecycle/SavedStateViewModelFactory.html#SavedStateViewModelFactory(android.app.Application,%20androidx.savedstate.SavedStateRegistryOwner,%20android.os.Bundle)) to create a SavedStateHandle and we attach it to your instance for you.


```kotlin
class ViewModel : RetainedInstance.SavedStateHandleAttachable {

    private lateinit var savedStateHandle: SavedStateHandle

    override fun attachSavedStateHandle(savedStateHandle: SavedStateHandle) {
        this.savedStateHandle = savedStateHandle
    }
}
```

#### CoroutineScopeAttachable

The `CoroutineScopeAttachable` interface allows you to receive a callback `attachCoroutineScope(coroutineScope: CoroutineScope)` **before** the `initiliaze()` - we attach the [ViewModel.viewModelScope](https://developer.android.com/topic/libraries/architecture/coroutines#viewmodelscope) for you.

```kotlin
class ViewModel : RetainedInstance.CorourineScopeAttachable {

    private lateinit var coroutineScope: CoroutineScope

    override fun attachCoroutineScope(coroutineScope: CoroutineScope) {
        this.coroutineScope = coroutineScope
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
