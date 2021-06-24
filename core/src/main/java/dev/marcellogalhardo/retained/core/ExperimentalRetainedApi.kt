package dev.marcellogalhardo.retained.core

/**
 * Marks declarations that are still **experimental** in retained API, which means that the design of the
 * corresponding declarations has open issues which may (or may not) lead to their changes in the future.
 *
 * Roughly speaking, there is a chance that those declarations will be deprecated in the near future or
 * the semantics of their behavior may change in some way that may break some code.
 */
@Retention(value = AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.TYPEALIAS,
    AnnotationTarget.PROPERTY
)
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
public annotation class ExperimentalRetainedApi
