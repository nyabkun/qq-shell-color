// 2023. nyabkun  MIT LICENSE

@file:Suppress("UNCHECKED_CAST", "MemberVisibilityCanBePrivate")

package nyab.util

import java.util.concurrent.locks.ReentrantLock

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=8] = qDEFAULT_CACHE_IT_EXPIRATION_CHECK_INTERVAL <-[Call]- QCacheMap.QCacheMap() < ... () <-[Call]- qRe() <-[Call]- @receiver:Language("RegExp") String.re <-[Call]- String.noStyle[Root]
internal const val qDEFAULT_CACHE_IT_EXPIRATION_CHECK_INTERVAL = 1000L

// CallChain[size=6] = qCacheThreadLocal <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItOneS ... () <-[Call]- qRe() <-[Call]- @receiver:Language("RegExp") String.re <-[Call]- String.noStyle[Root]
private val qCacheThreadLocal: ThreadLocal<QCacheMap> by lazy {
    ThreadLocal.withInitial {
        QCacheMap(
            qDEFAULT_CACHE_IT_EXPIRATION_CHECK_INTERVAL
        )
    }
}

// CallChain[size=4] = qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- @receiver:Language("RegExp") String.re <-[Call]- String.noStyle[Root]
internal fun <K : Any, V : Any> qCacheItOneSecThreadLocal(key: K, block: () -> V): V =
    qCacheItTimedThreadLocal(key, 1000L, block)

// CallChain[size=5] = qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- @receiver:Language("RegExp") String.re <-[Call]- String.noStyle[Root]
internal fun <K : Any, V : Any> qCacheItTimedThreadLocal(key: K, duration: Long, block: () -> V): V =
    qCacheThreadLocal.get().getOrPut(key) { QCacheEntry(block(), duration, qNow) }.value as V

// CallChain[size=7] = QCacheMap <-[Ref]- qCacheThreadLocal <-[Call]- qCacheItTimedThreadLocal() <-[ ... () <-[Call]- qRe() <-[Call]- @receiver:Language("RegExp") String.re <-[Call]- String.noStyle[Root]
internal class QCacheMap(
    val expirationCheckInterval: Long = qDEFAULT_CACHE_IT_EXPIRATION_CHECK_INTERVAL,
    val threadSafe: Boolean = false
) {
    // CallChain[size=7] = QCacheMap.lastCheck <-[Call]- QCacheMap.getOrPut() <-[Call]- qCacheItTimedThr ... () <-[Call]- qRe() <-[Call]- @receiver:Language("RegExp") String.re <-[Call]- String.noStyle[Root]
    var lastCheck: Long = -1
    // CallChain[size=7] = QCacheMap.lock <-[Call]- QCacheMap.getOrPut() <-[Call]- qCacheItTimedThreadLo ... () <-[Call]- qRe() <-[Call]- @receiver:Language("RegExp") String.re <-[Call]- String.noStyle[Root]
    val lock: ReentrantLock = ReentrantLock()
    // CallChain[size=7] = QCacheMap.map <-[Call]- QCacheMap.getOrPut() <-[Call]- qCacheItTimedThreadLoc ... () <-[Call]- qRe() <-[Call]- @receiver:Language("RegExp") String.re <-[Call]- String.noStyle[Root]
    val map: MutableMap<Any, QCacheEntry> = mutableMapOf()

    // CallChain[size=7] = QCacheMap.clearExpired() <-[Call]- QCacheMap.getOrPut() <-[Call]- qCacheItTim ... () <-[Call]- qRe() <-[Call]- @receiver:Language("RegExp") String.re <-[Call]- String.noStyle[Root]
    fun clearExpired(): Int = lock.qWithLock(threadSafe) {
        val toRemove = map.filterValues { it.isExpired() }
        toRemove.forEach { map.remove(it.key) }
        return toRemove.count()
    }

    // CallChain[size=6] = QCacheMap.getOrPut() <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItO ... () <-[Call]- qRe() <-[Call]- @receiver:Language("RegExp") String.re <-[Call]- String.noStyle[Root]
    fun getOrPut(key: Any, defaultValue: () -> QCacheEntry): QCacheEntry = lock.qWithLock(threadSafe) {
        val now = qNow
        if (now - lastCheck > expirationCheckInterval) {
            lastCheck = now
            clearExpired()
        }

        map.getOrPut(key, defaultValue)
    }
}

// CallChain[size=6] = QCacheEntry <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThre ... () <-[Call]- qRe() <-[Call]- @receiver:Language("RegExp") String.re <-[Call]- String.noStyle[Root]
internal data class QCacheEntry(val value: Any?, val duration: Long, val creationTime: Long = qNow) {
    // CallChain[size=8] = QCacheEntry.isExpired() <-[Call]- QCacheMap.clearExpired() <-[Call]- QCacheMa ... () <-[Call]- qRe() <-[Call]- @receiver:Language("RegExp") String.re <-[Call]- String.noStyle[Root]
    fun isExpired() = (qNow - creationTime) > duration
}