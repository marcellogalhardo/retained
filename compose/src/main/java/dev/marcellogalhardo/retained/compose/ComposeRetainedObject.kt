package dev.marcellogalhardo.retained.compose

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.AmbientLifecycleOwner
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import dev.marcellogalhardo.retained.core.RetainedContext
import dev.marcellogalhardo.retained.core.createRetainedObjectLazy

/**
 * Returns a [Lazy] delegate to access a retained object by **default** scoped to this
 * [Composable] (e.g., [NavBackStackEntry], [Fragment] or [ComponentActivity]).
 *
 * ```
 * @Composable
 * fun MyView() {
 *     val vm by retain { ViewModel() }
 * }
 * class ViewModel(val name: String = "")
 * ```
 *
 * This property can be accessed only after the [LifecycleOwner] is ready to use Jetpack
 * [ViewModel], and access prior to that will result in [IllegalArgumentException].
 *
 * @see createRetainedObjectLazy
 */
@Composable
inline fun <reified T : Any> retain(
    key: String = T::class.java.name,
    owner: LifecycleOwner = AmbientLifecycleOwner.current,
    defaultArgs: Bundle? = getDefaultArgs(owner),
    noinline createRetainedObject: RetainedContext.() -> T
): Lazy<T> = createRetainedObjectLazy(key, { owner }, defaultArgs, createRetainedObject)

@PublishedApi
internal fun getDefaultArgs(owner: LifecycleOwner): Bundle? {
    return when (owner) {
        is ComponentActivity -> owner.intent?.extras
        is Fragment -> owner.arguments
        is NavBackStackEntry -> owner.arguments
        else -> bundleOf()
    }
}
