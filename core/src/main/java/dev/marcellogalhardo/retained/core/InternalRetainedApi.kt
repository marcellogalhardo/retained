package dev.marcellogalhardo.retained.core

/**
 * Marks declarations that are **internal** in Retained API, which means that should not be used
 * outside of `dev.marcellogalhardo.retained`, because their signatures and semantics will change
 * between future releases without any warnings and without providing any migration aids.
 */
@Retention(value = AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.TYPEALIAS,
    AnnotationTarget.PROPERTY
)
@RequiresOptIn(level = RequiresOptIn.Level.ERROR)
public annotation class InternalRetainedApi
