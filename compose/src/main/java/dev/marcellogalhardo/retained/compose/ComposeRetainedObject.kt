package dev.marcellogalhardo.retained.compose

import android.app.Activity
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.AmbientLifecycleOwner
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavBackStackEntry
import dev.marcellogalhardo.retained.core.RetainedContext
import dev.marcellogalhardo.retained.core.retainObject

@Composable
inline fun <reified T : Any> retain(
    key: String = T::class.java.name,
    owner: LifecycleOwner = AmbientLifecycleOwner.current,
    defaultArgs: Bundle? = getDefaultArgs(owner),
    noinline createRetainedObject: RetainedContext.() -> T
): T {
    return retainObject(key, owner, defaultArgs, createRetainedObject)
}

@PublishedApi
internal fun getDefaultArgs(owner: LifecycleOwner): Bundle? {
    return when (owner) {
        is Activity -> owner.intent?.extras
        is Fragment -> owner.arguments
        is NavBackStackEntry -> owner.arguments
        else -> bundleOf()
    }
}
