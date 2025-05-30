package com.mlab.knockme.core.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.io.Serializable

/**
 * Created by nalcalag on 09/02/2019.
 *
 * Represents a quintet of values
 *
 * There is no meaning attached to values in this class, it can be used for any purpose.
 * Quintuple exhibits value semantics
 *
 * @param A type of the first value.
 * @param B type of the second value.
 * @param C type of the third value.
 * @param D type of the fourth value.
 * @param E type of the fifth value.
 * @property first First value.
 * @property second Second value.
 * @property third Third value.
 * @property fourth Fourth value.
 * @property fifth Fifth value.
 */
data class Quintuple<out A, out B, out C, out D, out E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
) : Serializable {

    /**
     * Returns string representation of the [Quintuple] including its [first], [second], [third], [fourth] and [fifth] values.
     */
    override fun toString(): String = "($first, $second, $third, $fourth, $fifth)"
}

/**
 * Converts this quintuple into a list.
 */
fun <T> Quintuple<T, T, T, T, T>.toList(): List<T> = listOf(first, second, third, fourth, fifth)

fun <T1, T2, T3, T4, T5, T6, R> combine6(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    transform: suspend (T1, T2, T3, T4, T5, T6) -> R
): Flow<R> = combine(
    combine(flow1, flow2, flow3, flow4, flow5) { t1, t2, t3, t4, t5 ->
        Quintuple(t1, t2, t3, t4, t5)
    },
    flow6
) { (t1, t2, t3, t4, t5), t6 ->
    transform(t1, t2, t3, t4, t5, t6)
}