package com.marcellogalhardo.retained.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.marcellogalhardo.retained.core.RetainedContext
import com.marcellogalhardo.retained.core.retainObject

inline fun <reified T : Any> Fragment.retain(
    key: String = T::class.java.name,
    defaultArgs: Bundle? = null,
    crossinline getFragment: () -> Fragment = { this },
    noinline createRetainedObject: RetainedContext.() -> T
): Lazy<T> = lazy { retainObject(key, getFragment(), defaultArgs, createRetainedObject) }

inline fun <reified T : Any> Fragment.retainInActivity(
    key: String = T::class.java.name,
    defaultArgs: Bundle? = null,
    crossinline getActivity: () -> FragmentActivity = { requireActivity() },
    noinline createRetainedObject: RetainedContext.() -> T
): Lazy<T> = lazy { retainObject(key, getActivity(), defaultArgs, createRetainedObject) }

inline fun <reified T : Any> Fragment.retainInParent(
    key: String = T::class.java.name,
    defaultArgs: Bundle? = null,
    noinline createRetainedObject: RetainedContext.() -> T
): Lazy<T> = lazy { retainObject(key, parentLifecycleOwner, defaultArgs, createRetainedObject) }

@PublishedApi
internal val Fragment.parentLifecycleOwner: LifecycleOwner
    get() = parentFragment ?: requireActivity()
