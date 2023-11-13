// 2023. nyabkun  MIT LICENSE

package nyab.util

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=5] = QTimeItResult <-[Ref]- qTimeIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
internal class QTimeItResult<T>(val label: String, val time: Long, val result: T) {
    // CallChain[size=5] = QTimeItResult.toString() <-[Call]- qTimeIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun toString(): String {
        return qBrackets(label, time.qFormatDuration())
    }
}

// CallChain[size=4] = qTimeIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
@OptIn(ExperimentalContracts::class)
internal inline fun <T> qTimeIt(label: String = qThisSrcLineSignature, quiet: Boolean = false, out: QOut? = QOut.CONSOLE, block: () -> T): QTimeItResult<T> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val start = System.nanoTime()

    val blockResult = block()

    val time = System.nanoTime() - start

    val result = QTimeItResult(label, time, blockResult)

    if (!quiet)
        out?.println(result.toString())

    return result
}