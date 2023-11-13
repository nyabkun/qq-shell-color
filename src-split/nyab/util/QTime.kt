// 2023. nyabkun  MIT LICENSE

@file:Suppress("NOTHING_TO_INLINE")

package nyab.util

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=6] = qNow <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- @receiver:Language("RegExp") String.re <-[Call]- String.noStyle[Root]
internal val qNow: Long
    get() = System.currentTimeMillis()