// 2023. nyabkun  MIT LICENSE

package nyab.util

import java.util.concurrent.locks.Lock
import kotlin.concurrent.withLock

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=15] = Lock.qWithLock() <-[Call]- QCacheMap.getOrPut() <-[Call]- qCacheItTimedThrea ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal inline fun <T> Lock.qWithLock(threadSafe: Boolean, block: () -> T): T {
    return if (threadSafe) {
        withLock(block)
    } else {
        block()
    }
}