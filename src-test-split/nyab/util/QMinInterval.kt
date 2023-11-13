// 2023. nyabkun  MIT LICENSE

package nyab.util

import java.util.concurrent.locks.ReentrantLock

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=24] = qMinIntervalMap <-[Call]- qMinIntervalRun() <-[Call]- QExProps.removeGarbage ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private val qMinIntervalMap by lazy { QMinIntervalMap(false) }

// CallChain[size=23] = qMinIntervalRun() <-[Call]- QExProps.removeGarbageCollectedEntries() <-[Call ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun <T : Any?> qMinIntervalRun(
    key: Any,
    minIntervalMillis: Long = 1000,
    now: Long = System.currentTimeMillis(),
    rememberLastNonExecutedTask: Boolean = false,
    rememberLastResult: Boolean = false,
    task: () -> T
): T? {
    return qMinIntervalMap.runIfOk(key, minIntervalMillis, now, rememberLastNonExecutedTask, rememberLastResult, task)
}

// CallChain[size=25] = QMinInterval <-[Call]- QMinIntervalMap.runIfOk() <-[Call]- qMinIntervalRun() ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal class QMinInterval(val key: Any, val minIntervalMillis: Long) {
    // CallChain[size=26] = QMinInterval.firstRun <-[Call]- QMinInterval.okToRun() <-[Call]- QMinInterva ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    private var firstRun = true
    // CallChain[size=26] = QMinInterval.lastRun <-[Call]- QMinInterval.updateLastRun() <-[Call]- QMinIn ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    private var lastRun = qNow
    // CallChain[size=25] = QMinInterval.lastNonExecutedTask <-[Call]- QMinIntervalMap.runIfOk() <-[Call ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    var lastNonExecutedTask: (() -> Any?)? = null

    // CallChain[size=25] = QMinInterval.okToRun() <-[Call]- QMinIntervalMap.runIfOk() <-[Call]- qMinInt ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun okToRun(now: Long = qNow): Boolean {
        if (firstRun) {
            firstRun = false
            return true
        }

        return now - lastRun >= minIntervalMillis
    }

    // CallChain[size=25] = QMinInterval.updateLastRun() <-[Call]- QMinIntervalMap.runIfOk() <-[Call]- q ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun updateLastRun(now: Long = qNow) {
        lastRun = now
    }
}

// CallChain[size=25] = QMinIntervalMap <-[Call]- qMinIntervalMap <-[Call]- qMinIntervalRun() <-[Cal ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private class QMinIntervalMap(val threadSafe: Boolean) {
    // CallChain[size=25] = QMinIntervalMap.lock <-[Call]- QMinIntervalMap.runIfOk() <-[Call]- qMinInter ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val lock = ReentrantLock()
    // CallChain[size=25] = QMinIntervalMap.map <-[Call]- QMinIntervalMap.runIfOk() <-[Call]- qMinInterv ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val map: MutableMap<Any, QMinInterval> = hashMapOf()
    // CallChain[size=25] = QMinIntervalMap.lastResult <-[Call]- QMinIntervalMap.runIfOk() <-[Call]- qMi ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    var lastResult: Any? = null

    // CallChain[size=24] = QMinIntervalMap.runIfOk() <-[Call]- qMinIntervalRun() <-[Call]- QExProps.rem ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun <T : Any?> runIfOk(
        key: Any,
        minIntervalMillis: Long,
        now: Long = System.currentTimeMillis(),
        rememberLastNonExecutedTask: Boolean = false,
        rememberLastResult: Boolean = false,
        task: () -> T
    ): T? = lock.qWithLock(threadSafe) {
        val qMin = map.getOrPut(key) { QMinInterval(key, minIntervalMillis) }

        if (qMin.okToRun(now)) {
            try {
                if( rememberLastResult ) {
                    lastResult = task()
                    return lastResult as T?
                } else {
                    task()
                }
            } finally {
                qMin.updateLastRun(now)
                qMin.lastNonExecutedTask = null
            }
        } else {
            if( rememberLastNonExecutedTask ) {
                qMin.lastNonExecutedTask = task
            }

            return if( rememberLastResult ) {
                lastResult as T?
            } else {
                null
            }
        }
    }
}