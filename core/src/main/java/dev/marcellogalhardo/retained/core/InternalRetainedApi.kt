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
@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "This is an internal `dev.marcellogalhardo.retained` API that should not be used from " +
            "outside of `dev.marcellogalhardo.retained`. No compatibility guarantees are provided." +
            " It is recommended to report your use-case of internal API to the maintainers of " +
            "Retained on [GitHub](https://github.com/marcellogalhardo/retained) so stable API could" +
            " be provided instead."
)
annotation class InternalRetainedApi
