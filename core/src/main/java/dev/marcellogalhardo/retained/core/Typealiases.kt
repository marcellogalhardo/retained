package dev.marcellogalhardo.retained.core

import android.os.Bundle

public typealias FindDefaultArgs = () -> Bundle?
public typealias FindParametrizedDefaultArgs<T> = (T) -> Bundle?
