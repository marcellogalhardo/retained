package dev.marcellogalhardo.retained.core

import android.os.Bundle
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner

public typealias FindDefaultArgs = () -> Bundle?

public typealias FindParametrizedDefaultArgs<T> = (T) -> Bundle?

public typealias InstantiateRetained<T> = (RetainedEntry) -> T
