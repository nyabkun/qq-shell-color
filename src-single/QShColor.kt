/*
 * Copyright 2023. nyabkun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

@file:Suppress("UNCHECKED_CAST")

import java.lang.StackWalker.StackFrame
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import java.util.stream.Stream
import kotlin.concurrent.withLock
import kotlin.math.absoluteValue
import kotlin.streams.asSequence
import org.intellij.lang.annotations.Language


// qq-shell-color is self-contained single-file library created by nyabkun.
//  - It can be added to your codebase with a simple copy and paste.
//  - You can modify and redistribute it under the MIT License.
//  - Please add a package declaration if necessary.

// << Root of the CallChain >>
private const val qBG_JUMP = 10

// << Root of the CallChain >>
private const val qSTART = "\u001B["

// << Root of the CallChain >>
private const val qEND = "${qSTART}0m"

// << Root of the CallChain >>
private fun String.qApplyEscapeNestable(start: String): String {
    val lastEnd = this.endsWith(qEND)

    return if( lastEnd ) {
            start + this.substring(0, this.length - 1).replace(qEND, qEND + start) + this[this.length - 1]
        } else {
            start + this.replace(qEND, qEND + start) + qEND
        }
}

// << Root of the CallChain >>
fun String.qColor(fg: QShColor? = null, bg: QShColor? = null, nestable: Boolean = this.contains(qSTART)): String {
    return if (this.qIsSingleLine()) {
        this.qApplyEscapeLine(fg, bg, nestable)
    } else {
        lineSequence().map { line ->
            line.qApplyEscapeLine(fg, bg, nestable)
        }.joinToString("\n")
    }
}

// << Root of the CallChain >>
fun String.qDeco(deco: QShDeco): String {
    return if (this.qIsSingleLine()) {
        this.qApplyEscapeLine(arrayOf(deco.start), nestable = this.contains(qSTART))
    } else {
        lineSequence().map { line ->
            line.qApplyEscapeLine(arrayOf(deco.start), nestable = this.contains(qSTART))
        }.joinToString("\n")
    }
}

// << Root of the CallChain >>
private fun String.qApplyEscapeLine(fg: QShColor?, bg: QShColor?, nestable: Boolean): String {
    return this.qApplyEscapeLine(
        listOfNotNull(fg?.fg, bg?.bg).toTypedArray(),
        nestable
    )
}

// << Root of the CallChain >>
private fun String.qApplyEscapeLine(
    startSequences: Array<String>,
    nestable: Boolean
): String {
    val nest = nestable && this.contains(qEND)

    var text = this

    for (start in startSequences) {
        text = if (nest) {
            text.qApplyEscapeNestable(start)
        } else {
            "$start$text$qEND"
        }
    }

    return text
}

// << Root of the CallChain >>
val String.noStyle: String
    get() {
        return this.replace("""\Q$qSTART\E\d{1,2}m""".re, "")
    }

// << Root of the CallChain >>
enum class QShDeco(val code: Int) {
    // << Root of the CallChain >>
    // https://en.wikipedia.org/wiki/ANSI_escape_code
    Bold(1),
    // << Root of the CallChain >>
    Italic(3),
    // << Root of the CallChain >>
    Underline(4);

    // << Root of the CallChain >>
    val start: String = "$qSTART${code}m"

    companion object {
        // << Root of the CallChain >>
        fun get(code: Int): QShDeco {
            return QShDeco.values().find {
                it.code == code
            }!!
        }
    }
}

// << Root of the CallChain >>
enum class QShColor(val code: Int) {
    // << Root of the CallChain >>
    Black(30),
    // << Root of the CallChain >>
    Red(31),
    // << Root of the CallChain >>
    Green(32),
    // << Root of the CallChain >>
    Yellow(33),
    // << Root of the CallChain >>
    Blue(34),
    // << Root of the CallChain >>
    Purple(35),
    // << Root of the CallChain >>
    Cyan(36),
    // << Root of the CallChain >>
    LightGray(37),

    // << Root of the CallChain >>
    DefaultFG(39),
    // << Root of the CallChain >>
    DefaultBG(49),

    // << Root of the CallChain >>
    DarkGray(90),
    // << Root of the CallChain >>
    LightRed(91),
    // << Root of the CallChain >>
    LightGreen(92),
    // << Root of the CallChain >>
    LightYellow(93),
    // << Root of the CallChain >>
    LightBlue(94),
    // << Root of the CallChain >>
    LightPurple(95),
    // << Root of the CallChain >>
    LightCyan(96),
    // << Root of the CallChain >>
    White(97);

    // << Root of the CallChain >>
    val fg: String = "$qSTART${code}m"

    // << Root of the CallChain >>
    val bg: String = "$qSTART${code + qBG_JUMP}m"

    companion object {
        // << Root of the CallChain >>
        fun random(seed: String, colors: Array<QShColor> = arrayOf(Yellow, Green, Blue, Purple, Cyan)): QShColor {
            val idx = seed.hashCode().rem(colors.size).absoluteValue
            return colors[idx]
        }

        // << Root of the CallChain >>
        fun get(ansiEscapeCode: Int): QShColor {
            return QShColor.values().find {
                it.code == ansiEscapeCode
            }!!
        }
    }
}

// << Root of the CallChain >>
fun String.qColorAndDecoDebug(tagStart: String = "[", tagEnd: String = "]"): String {
    var txt = this
    for (color in QShColor.values()) {
        txt = txt.replace(color.fg, "$tagStart${color.name}$tagEnd", ignoreCase = false)
        txt = txt.replace(color.bg, "$tagStart${color.name}_BG$tagEnd", ignoreCase = false)
    }
    for (deco in QShDeco.values()) {
        txt = txt.replace(deco.start, "$tagStart${deco.name}$tagEnd", ignoreCase = false)
    }

    txt = txt.replace(qEND, "${tagStart}End$tagEnd")

    return txt
}

// << Root of the CallChain >>
fun String.qColorTarget(ptn: Regex, fg: QShColor? = null, bg: QShColor? = null): String {
    return ptn.replace(this, "$0".qColor(fg, bg))
}

// << Root of the CallChain >>
fun String.qDecoTarget(ptn: Regex, deco: QShDeco): String {
    return ptn.replace(this, "$0".qDeco(deco))
}

// << Root of the CallChain >>
fun String.qColorRandom(seed: String = qCallerSrcLineSignature()): String = this.qColor(QShColor.random(seed))

// << Root of the CallChain >>
val String?.bold: String
    get() = this?.qDeco(QShDeco.Bold) ?: "null".qDeco(QShDeco.Bold)

// << Root of the CallChain >>
val String?.italic: String
    get() = this?.qDeco(QShDeco.Italic) ?: "null".qDeco(QShDeco.Italic)

// << Root of the CallChain >>
val String?.underline: String
    get() = this?.qDeco(QShDeco.Underline) ?: "null".qDeco(QShDeco.Underline)

// << Root of the CallChain >>
val String?.black: String
    get() = this?.qColor(QShColor.Black) ?: "null".qColor(QShColor.Black)

// << Root of the CallChain >>
val String?.red: String
    get() = this?.qColor(QShColor.Red) ?: "null".qColor(QShColor.Red)

// << Root of the CallChain >>
val String?.green: String
    get() = this?.qColor(QShColor.Green) ?: "null".qColor(QShColor.Green)

// << Root of the CallChain >>
val String?.yellow: String
    get() = this?.qColor(QShColor.Yellow) ?: "null".qColor(QShColor.Yellow)

// << Root of the CallChain >>
val String?.blue: String
    get() = this?.qColor(QShColor.Blue) ?: "null".qColor(QShColor.Blue)

// << Root of the CallChain >>
val String?.purple: String
    get() = this?.qColor(QShColor.Purple) ?: "null".qColor(QShColor.Cyan)

// << Root of the CallChain >>
val String?.cyan: String
    get() = this?.qColor(QShColor.Cyan) ?: "null".qColor(QShColor.Cyan)

// << Root of the CallChain >>
val String?.light_gray: String
    get() = this?.qColor(QShColor.LightGray) ?: "null".qColor(QShColor.LightGray)

// << Root of the CallChain >>
val String?.dark_gray: String
    get() = this?.qColor(QShColor.DarkGray) ?: "null".qColor(QShColor.DarkGray)

// << Root of the CallChain >>
val String?.light_red: String
    get() = this?.qColor(QShColor.LightRed) ?: "null".qColor(QShColor.LightRed)

// << Root of the CallChain >>
val String?.light_green: String
    get() = this?.qColor(QShColor.LightGreen) ?: "null".qColor(QShColor.LightGreen)

// << Root of the CallChain >>
val String?.light_yellow: String
    get() = this?.qColor(QShColor.LightYellow) ?: "null".qColor(QShColor.LightYellow)

// << Root of the CallChain >>
val String?.light_blue: String
    get() = this?.qColor(QShColor.LightBlue) ?: "null".qColor(QShColor.LightBlue)

// << Root of the CallChain >>
val String?.light_magenta: String
    get() = this?.qColor(QShColor.LightPurple) ?: "null".qColor(QShColor.LightPurple)

// << Root of the CallChain >>
val String?.light_cyan: String
    get() = this?.qColor(QShColor.LightCyan) ?: "null".qColor(QShColor.LightCyan)

// << Root of the CallChain >>
val String?.white: String
    get() = this?.qColor(QShColor.White) ?: "null".qColor(QShColor.White)


// region Src from Non-Root API Files -- Auto Generated by nyab.conf.QCompactLib.kt
// ================================================================================


// CallChain[size=4] = qSTACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
private val qSTACK_FRAME_FILTER: (StackWalker.StackFrame) -> Boolean = {
    !it.className.startsWith("org.gradle") &&
            !it.className.startsWith("org.testng") &&
            !it.className.startsWith("worker.org.gradle") &&
            !it.methodName.endsWith("\$default") && it.fileName != null &&
//            && !it.className.startsWith(QException::class.qualifiedName!!)
//            && it.methodName != "invokeSuspend"
            it.declaringClass != null
//            && it.declaringClass.canonicalName != null
//            && !it.declaringClass.canonicalName.startsWith("kotlin.coroutines.jvm.internal.")
//            && !it.declaringClass.canonicalName.startsWith("kotlinx.coroutines")
}

// CallChain[size=8] = qDEFAULT_CACHE_IT_EXPIRATION_CHECK_INTERVAL <-[Call]- QCacheMap.QCacheMap() < ... Local() <-[Call]- qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- re <-[Call]- noStyle[Root]
private const val qDEFAULT_CACHE_IT_EXPIRATION_CHECK_INTERVAL = 1000L

// CallChain[size=6] = qThreadLocalCache <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- re <-[Call]- noStyle[Root]
private val qThreadLocalCache: ThreadLocal<QCacheMap> by lazy {
    ThreadLocal.withInitial {
        QCacheMap(
            qDEFAULT_CACHE_IT_EXPIRATION_CHECK_INTERVAL
        )
    }
}

// CallChain[size=4] = qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- re <-[Call]- noStyle[Root]
private fun <K : Any, V : Any> qCacheItOneSecThreadLocal(key: K, block: () -> V): V =
    qCacheItTimedThreadLocal(key, 1000L, block)

// CallChain[size=5] = qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- re <-[Call]- noStyle[Root]
private fun <K : Any, V : Any> qCacheItTimedThreadLocal(key: K, duration: Long, block: () -> V): V =
    qThreadLocalCache.get().getOrPut(key) { QCacheEntry(block(), duration, qNow) }.value as V

// CallChain[size=7] = QCacheMap <-[Ref]- qThreadLocalCache <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- re <-[Call]- noStyle[Root]
private class QCacheMap(
    val expirationCheckInterval: Long = qDEFAULT_CACHE_IT_EXPIRATION_CHECK_INTERVAL,
    val threadSafe: Boolean = false
) {
    // CallChain[size=7] = QCacheMap.lastCheck <-[Call]- QCacheMap.getOrPut() <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- re <-[Call]- noStyle[Root]
    var lastCheck: Long = -1
    // CallChain[size=7] = QCacheMap.lock <-[Call]- QCacheMap.getOrPut() <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- re <-[Call]- noStyle[Root]
    val lock: ReentrantLock = ReentrantLock()
    // CallChain[size=7] = QCacheMap.map <-[Call]- QCacheMap.getOrPut() <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- re <-[Call]- noStyle[Root]
    val map: MutableMap<Any, QCacheEntry> = mutableMapOf()

    // CallChain[size=7] = QCacheMap.clearExpired() <-[Call]- QCacheMap.getOrPut() <-[Call]- qCacheItTim ... Local() <-[Call]- qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- re <-[Call]- noStyle[Root]
    fun clearExpired(): Int = lock.qWithLock(threadSafe) {
        val toRemove = map.filterValues { it.isExpired() }
        toRemove.forEach { map.remove(it.key) }
        return toRemove.count()
    }

    // CallChain[size=6] = QCacheMap.getOrPut() <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- re <-[Call]- noStyle[Root]
    fun getOrPut(key: Any, defaultValue: () -> QCacheEntry): QCacheEntry = lock.qWithLock(threadSafe) {
        val now = qNow
        if (now - lastCheck > expirationCheckInterval) {
            lastCheck = now
            clearExpired()
        }

        map.getOrPut(key, defaultValue)
    }
}

// CallChain[size=6] = QCacheEntry <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- re <-[Call]- noStyle[Root]
private data class QCacheEntry(val value: Any?, val duration: Long, val creationTime: Long = qNow) {
    // CallChain[size=8] = QCacheEntry.isExpired() <-[Call]- QCacheMap.clearExpired() <-[Call]- QCacheMa ... Local() <-[Call]- qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- re <-[Call]- noStyle[Root]
    fun isExpired() = (qNow - creationTime) > duration
}

// CallChain[size=7] = Lock.qWithLock() <-[Call]- QCacheMap.getOrPut() <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- re <-[Call]- noStyle[Root]
private inline fun <T> Lock.qWithLock(threadSafe: Boolean, block: () -> T): T {
    return if (threadSafe) {
        withLock(block)
    } else {
        block()
    }
}

// CallChain[size=2] = qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
private fun qCallerSrcLineSignature(stackDepth: Int = 0): String {
    val frame = qStackFrame(stackDepth + 2)

    return if (frame.declaringClass?.canonicalName == null) {
        if (frame.fileName != null) {
            if (frame.methodName != "invokeSuspend") {
                frame.fileName + " - " + frame.methodName + " - " + frame.lineNumber
            } else {
                frame.fileName + " - " + frame.lineNumber
            }
        } else {
            frame.methodName + " - " + frame.lineNumber
        }
    } else {
        frame.declaringClass.canonicalName + "." + frame.methodName + " - " + frame.lineNumber
    }
}

// CallChain[size=4] = qStackFrames() <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
private inline fun qStackFrames(
        stackDepth: Int = 0,
        size: Int = 1,
        noinline filter: (StackFrame) -> Boolean = qSTACK_FRAME_FILTER,
): List<StackFrame> {
    return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk { s: Stream<StackFrame> ->
        s.asSequence().filter(filter).drop(stackDepth).take(size).toList()
    }
}

// CallChain[size=3] = qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
private inline fun qStackFrame(
        stackDepth: Int = 0,
        noinline filter: (StackFrame) -> Boolean = qSTACK_FRAME_FILTER,
): StackFrame {
    return qStackFrames(stackDepth, 1, filter)[0]
}

// CallChain[size=4] = RO <-[Ref]- qRe() <-[Call]- re <-[Call]- noStyle[Root]
private typealias RO = RegexOption

// CallChain[size=3] = qRe() <-[Call]- re <-[Call]- noStyle[Root]
private fun qRe(@Language("RegExp") regex: String, vararg opts: RO): Regex {
    return qCacheItOneSecThreadLocal(regex + opts.contentToString()) {
        Regex(regex, setOf(*opts))
    }
}

// CallChain[size=2] = re <-[Call]- noStyle[Root]
// https://youtrack.jetbrains.com/issue/KTIJ-5643
private val @receiver:Language("RegExp") String.re: Regex
    get() = qRe(this)

// CallChain[size=3] = String.qIsMultiLine() <-[Call]- String.qIsSingleLine() <-[Call]- String.qColor()[Root]
private fun String.qIsMultiLine(): Boolean {
    return this.contains("\n") || this.contains("\r")
}

// CallChain[size=2] = String.qIsSingleLine() <-[Call]- String.qColor()[Root]
private fun String.qIsSingleLine(): Boolean {
    return !this.qIsMultiLine()
}

// CallChain[size=6] = qNow <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- re <-[Call]- noStyle[Root]
private val qNow: Long
    get() = System.currentTimeMillis()


// ================================================================================
// endregion Src from Non-Root API Files -- Auto Generated by nyab.conf.QCompactLib.kt