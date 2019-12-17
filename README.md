# Retained Instance

A lightweight library built on top of Android Architecture Component ViewModel to simplify how UI Controllers (e.g., Activity or Fragment) retain instances.

Retained Instance was created to level up Android's ViewModels in a consistent, reliable
and composable way - no inheritances, no factories and no parameters like application 
or saved state handle. Instead, you get a very composable API based on interfaces.

You get what you implement - literally, through interfaces.

Note that no major changes will be done on this library except by updates to support the latest
features of Android's ViewModel. This library API is stable.

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
data class SimplePresenter(
    var counter: Int = 0
)
```

Then on your UI Controller ask it to retain the instance.

```kotlin
class SampleActivity : AppCompatActivity() {

    private val presenter: SimplePresenter by retainedInstances { SimplePresenter(counter = 5) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Do your logic.
        Log.v("Presenter.Counter", presenter.counter.toString())
        presenter.counter++
    }
}
```

Rotate the screen and check the instance will be preserved.

### Fragments

If you are using Fragment supports, you can do the same with Fragments.
```kotlin
class SampleFragment : Fragment() {

    private val presenter: SimplePresenter by retainedInstances() // No-args constructor used.

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Do your logic.
        Log.v("Presenter.Counter", presenter.counter.toString())
        presenter.counter++
    }
}
```

### Lifecycle-aware retained instances

```kotlin
class LifecycleAwareRetainedInstance(
    var counter: Int = 0
) : RetainedInstance.OnLifecycleListener {

    override fun onCreate() {
        TODO("not implemented")
    }

    override fun onStart() {
        TODO("not implemented")
    }

    override fun onResume() {
        TODO("not implemented")
    }

    override fun onPause() {
        TODO("not implemented")
    }

    override fun onStop() {
        TODO("not implemented")
    }

    override fun onDestroy() {
        TODO("not implemented")
    }
}
```

### ViewModels onCleared-aware retained instances

```kotlin
class OnClearedAwareRetainedInstance(
    var counter: Int = 0
) : RetainedInstance.OnClearedListener {

    override fun onCleared() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
```

### Attaching an Application to a Retained Instance

```kotlin
class ApplicationOwnerRetainedInstance(
    var counter: Int = 0
) : RetainedInstance.OnAttachApplicationListener {

    private lateinit var application: Application

    override fun onAttachApplication(application: Application) {
        this.application = application
    }
}
```

### Attaching a SavedStateHandle to a Retained Instance

```kotlin
class SavedStateOwnerRetainedInstance(
    var counter: Int = 0
) : RetainInstance.OnAttachSavedStateHandleListener {

    private lateinit var savedStateHandle: SavedStateHandle

    override fun onAttachSavedStateHandle(savedStateHandle: SavedStateHandle) {
        this.savedStateHandle = savedStateHandle
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
