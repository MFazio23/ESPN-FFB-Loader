package dev.mfazio.espnffb.extensions

fun <K> Map<K, Double>.getOrZero(key: K): Int = this.getOrDefault(key, 0.0).toInt()

fun Double.toTwoDigits() = "%.2f".format(this)

fun <T> Collection<T>.printEach() = this.forEach(::println)