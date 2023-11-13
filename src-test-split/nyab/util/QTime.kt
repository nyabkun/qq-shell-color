// 2023. nyabkun  MIT LICENSE

@file:Suppress("NOTHING_TO_INLINE")

package nyab.util

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=14] = qNow <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThreadLoca ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal val qNow: Long
    get() = System.currentTimeMillis()