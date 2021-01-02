package com.marcellogalhardo.retained.core

import android.os.Bundle
import androidx.activity.ComponentActivity

inline fun <reified T : Any> ComponentActivity.retain(
    key: String = T::class.java.name,
    defaultArgs: Bundle? = intent?.extras,
    noinline createRetainedObject: RetainedContext.() -> T
): Lazy<T> = lazy { retain(key, owner = this, defaultArgs, createRetainedObject) }
