package it.uniparthenope.francescobottino001.labucaasd

inline fun <A, B, R> ifNotNull(a: A?, b: B?, code: ((A, B) -> R)) {
    if (a != null && b != null) {
        code(a, b)
    }
}

inline fun <A, B, R> getIfNotNull(a: A?, b: B?, code: ((A, B) -> R)): R? {
    return if (a != null && b != null) {
        code(a, b)
    } else null
}