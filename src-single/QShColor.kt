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
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KClass
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
private val qMASK_COLORED by lazy {
    QMaskBetween(
        qSTART,
        qEND,
        qSTART,
        escapeChar = '\\',
        targetNestDepth = 1,
        maskIncludeStartAndEndSequence = false
    )
}

// << Root of the CallChain >>
enum class QBgOrFg {
    // << Root of the CallChain >>
    Foreground,
    // << Root of the CallChain >>
    Background
}

// << Root of the CallChain >>
private fun String.qApplyColorNestable(colorStart: String): String {
    val re = "(?s)(\\Q$qEND\\E)(.+?)(\\Q$qSTART\\E|$)".re
    val replace = "$1$colorStart$2$qEND$3"
    val re2 = "^(?s)(.*?)(\\Q$qSTART\\E)"
    val replace2 = "$colorStart$1$qEND$2"

    return this.qMaskAndReplace(
        qMASK_COLORED,
        re,
        replace
    ).qReplaceFirstIfNonEmptyStringGroup(re2, 1, replace2)
}

// << Root of the CallChain >>
fun String.qColor(fg: QShColor? = null, bg: QShColor? = null, nestable: Boolean = this.contains(qSTART)): String {
    return if (this.qIsSingleLine()) {
        this.qColorLine(fg, bg, nestable)
    } else {
        lineSequence().map { line ->
            line.qColorLine(fg, bg, nestable)
        }.joinToString("\n")
    }
}

// << Root of the CallChain >>
private fun String.qColorLine(
    fg: QShColor? = null,
    bg: QShColor? = null,
    nestable: Boolean = true,
): String {
    val nest = nestable && this.contains(qEND)

    val fgApplied = if (fg != null) {
        val fgStart = fg.fg

        if (nest) {
            this.qApplyColorNestable(fgStart)
        } else {
            "$fgStart$this$qEND"
        }
    } else {
        this
    }

    val bgApplied = if (bg != null) {
        val bgStart = bg.bg

        if (nest) {
            fgApplied.qApplyColorNestable(bgStart)
        } else {
            "$bgStart$fgApplied$qEND"
        }
    } else {
        fgApplied
    }

    return bgApplied
}

// << Root of the CallChain >>
val String.noColor: String
    get() {
        return this.replace("""\Q$qSTART\E\d{1,2}m""".re, "")
    }

// << Root of the CallChain >>
enum class QShColor(val code: Int) {
    // << Root of the CallChain >>
    BLACK(30),
    // << Root of the CallChain >>
    RED(31),
    // << Root of the CallChain >>
    GREEN(32),
    // << Root of the CallChain >>
    YELLOW(33),
    // << Root of the CallChain >>
    BLUE(34),
    // << Root of the CallChain >>
    MAGENTA(35),
    // << Root of the CallChain >>
    CYAN(36),
    // << Root of the CallChain >>
    LIGHT_GRAY(37),

    // << Root of the CallChain >>
    DARK_GRAY(90),
    // << Root of the CallChain >>
    LIGHT_RED(91),
    // << Root of the CallChain >>
    LIGHT_GREEN(92),
    // << Root of the CallChain >>
    LIGHT_YELLOW(93),
    // << Root of the CallChain >>
    LIGHT_BLUE(94),
    // << Root of the CallChain >>
    LIGHT_MAGENTA(95),
    // << Root of the CallChain >>
    LIGHT_CYAN(96),
    // << Root of the CallChain >>
    WHITE(97);

    // << Root of the CallChain >>
    /** ANSI modifier string to apply the color to the text itself */
    val fg: String = "$qSTART${code}m"

    // << Root of the CallChain >>
    /** ANSI modifier string to apply the color the text's background */
    val bg: String = "$qSTART${code + qBG_JUMP}m"

    companion object {
        // << Root of the CallChain >>
        fun random(seed: String, colors: Array<QShColor> = arrayOf(YELLOW, GREEN, BLUE, MAGENTA, CYAN)): QShColor {
            val idx = seed.hashCode().rem(colors.size).absoluteValue
            return colors[idx]
        }
    }
}

// << Root of the CallChain >>
fun String.qColorDebug(tagStart: String = "[", tagEnd: String = "]"): String {
    var txt = this
    for (color in QShColor.values()) {
        txt = txt.replace(color.fg, "$tagStart${color.name}$tagEnd", ignoreCase = false)
        txt = txt.replace(color.bg, "$tagStart${color.name}_BG$tagEnd", ignoreCase = false)
    }

    txt = txt.replace(qEND, "${tagStart}END$tagEnd")

    return txt
}

// << Root of the CallChain >>
fun String.qColorTarget(ptn: Regex, color: QShColor = QShColor.LIGHT_YELLOW): String {
    return ptn.replace(this, "$0".qColor(color))
}

// << Root of the CallChain >>
fun String.qColorRandom(seed: String = qCallerSrcLineSignature()): String = this.qColor(QShColor.random(seed))

// << Root of the CallChain >>
val String?.black: String
    get() = this?.qColor(QShColor.BLACK) ?: "null".qColor(QShColor.BLACK)

// << Root of the CallChain >>
val String?.red: String
    get() = this?.qColor(QShColor.RED) ?: "null".qColor(QShColor.RED)

// << Root of the CallChain >>
val String?.green: String
    get() = this?.qColor(QShColor.GREEN) ?: "null".qColor(QShColor.GREEN)

// << Root of the CallChain >>
val String?.yellow: String
    get() = this?.qColor(QShColor.YELLOW) ?: "null".qColor(QShColor.YELLOW)

// << Root of the CallChain >>
val String?.blue: String
    get() = this?.qColor(QShColor.BLUE) ?: "null".qColor(QShColor.BLUE)

// << Root of the CallChain >>
val String?.magenta: String
    get() = this?.qColor(QShColor.MAGENTA) ?: "null".qColor(QShColor.CYAN)

// << Root of the CallChain >>
val String?.cyan: String
    get() = this?.qColor(QShColor.CYAN) ?: "null".qColor(QShColor.CYAN)

// << Root of the CallChain >>
val String?.light_gray: String
    get() = this?.qColor(QShColor.LIGHT_GRAY) ?: "null".qColor(QShColor.LIGHT_GRAY)

// << Root of the CallChain >>
val String?.dark_gray: String
    get() = this?.qColor(QShColor.DARK_GRAY) ?: "null".qColor(QShColor.DARK_GRAY)

// << Root of the CallChain >>
val String?.light_red: String
    get() = this?.qColor(QShColor.LIGHT_RED) ?: "null".qColor(QShColor.LIGHT_RED)

// << Root of the CallChain >>
val String?.light_green: String
    get() = this?.qColor(QShColor.LIGHT_GREEN) ?: "null".qColor(QShColor.LIGHT_GREEN)

// << Root of the CallChain >>
val String?.light_yellow: String
    get() = this?.qColor(QShColor.LIGHT_YELLOW) ?: "null".qColor(QShColor.LIGHT_YELLOW)

// << Root of the CallChain >>
val String?.light_blue: String
    get() = this?.qColor(QShColor.LIGHT_BLUE) ?: "null".qColor(QShColor.LIGHT_BLUE)

// << Root of the CallChain >>
val String?.light_magenta: String
    get() = this?.qColor(QShColor.LIGHT_MAGENTA) ?: "null".qColor(QShColor.LIGHT_MAGENTA)

// << Root of the CallChain >>
val String?.light_cyan: String
    get() = this?.qColor(QShColor.LIGHT_CYAN) ?: "null".qColor(QShColor.LIGHT_CYAN)

// << Root of the CallChain >>
val String?.white: String
    get() = this?.qColor(QShColor.WHITE) ?: "null".qColor(QShColor.WHITE)


// region Src from Non-Root API Files -- Auto Generated by nyab.conf.QCompactLib.kt
// ================================================================================


// CallChain[size=5] = QMyException <-[Ref]- QE <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
private enum class QMyException {
    ;companion object {
        // Required to implement extended functions.

        // CallChain[size=4] = QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
        val STACK_FRAME_FILTER: (StackWalker.StackFrame) -> Boolean = {
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

        
    }
}

// CallChain[size=4] = QE <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
private typealias QE = QMyException

// CallChain[size=8] = qDEFAULT_CACHE_IT_EXPIRATION_CHECK_INTERVAL <-[Call]- QCacheMap.QCacheMap() < ... <-[Call]- String.qReplaceFirstIfNonEmptyStringGroup() <-[Call]- String.qApplyColorNestable()[Root]
private const val qDEFAULT_CACHE_IT_EXPIRATION_CHECK_INTERVAL = 1000L

// CallChain[size=6] = qThreadLocalCache <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItOneS ... <-[Call]- String.qReplaceFirstIfNonEmptyStringGroup() <-[Call]- String.qApplyColorNestable()[Root]
private val qThreadLocalCache: ThreadLocal<QCacheMap> by lazy {
    ThreadLocal.withInitial {
        QCacheMap(
            qDEFAULT_CACHE_IT_EXPIRATION_CHECK_INTERVAL
        )
    }
}

// CallChain[size=4] = qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- String.qReplaceFirstIfNonEmptyStringGroup() <-[Call]- String.qApplyColorNestable()[Root]
private fun <K : Any, V : Any> qCacheItOneSecThreadLocal(key: K, block: () -> V): V =
    qCacheItTimedThreadLocal(key, 1000L, block)

// CallChain[size=5] = qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- String.qReplaceFirstIfNonEmptyStringGroup() <-[Call]- String.qApplyColorNestable()[Root]
private fun <K : Any, V : Any> qCacheItTimedThreadLocal(key: K, duration: Long, block: () -> V): V =
    qThreadLocalCache.get().getOrPut(key) { QCacheEntry(block(), duration, qNow) }.value as V

// CallChain[size=7] = QCacheMap <-[Ref]- qThreadLocalCache <-[Call]- qCacheItTimedThreadLocal() <-[ ... <-[Call]- String.qReplaceFirstIfNonEmptyStringGroup() <-[Call]- String.qApplyColorNestable()[Root]
private class QCacheMap(
    val expirationCheckInterval: Long = qDEFAULT_CACHE_IT_EXPIRATION_CHECK_INTERVAL,
    val threadSafe: Boolean = false
) {
    // CallChain[size=7] = QCacheMap.lastCheck <-[Call]- QCacheMap.getOrPut() <-[Call]- qCacheItTimedThr ... <-[Call]- String.qReplaceFirstIfNonEmptyStringGroup() <-[Call]- String.qApplyColorNestable()[Root]
    var lastCheck: Long = -1
    // CallChain[size=7] = QCacheMap.lock <-[Call]- QCacheMap.getOrPut() <-[Call]- qCacheItTimedThreadLo ... <-[Call]- String.qReplaceFirstIfNonEmptyStringGroup() <-[Call]- String.qApplyColorNestable()[Root]
    val lock: ReentrantLock = ReentrantLock()
    // CallChain[size=7] = QCacheMap.map <-[Call]- QCacheMap.getOrPut() <-[Call]- qCacheItTimedThreadLoc ... <-[Call]- String.qReplaceFirstIfNonEmptyStringGroup() <-[Call]- String.qApplyColorNestable()[Root]
    val map: MutableMap<Any, QCacheEntry> = mutableMapOf()

    // CallChain[size=7] = QCacheMap.clearExpired() <-[Call]- QCacheMap.getOrPut() <-[Call]- qCacheItTim ... <-[Call]- String.qReplaceFirstIfNonEmptyStringGroup() <-[Call]- String.qApplyColorNestable()[Root]
    fun clearExpired(): Int = lock.qWithLock(threadSafe) {
        val toRemove = map.filterValues { it.isExpired() }
        toRemove.forEach { map.remove(it.key) }
        return toRemove.count()
    }

    // CallChain[size=6] = QCacheMap.getOrPut() <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItO ... <-[Call]- String.qReplaceFirstIfNonEmptyStringGroup() <-[Call]- String.qApplyColorNestable()[Root]
    fun getOrPut(key: Any, defaultValue: () -> QCacheEntry): QCacheEntry = lock.qWithLock(threadSafe) {
        val now = qNow
        if (now - lastCheck > expirationCheckInterval) {
            lastCheck = now
            clearExpired()
        }

        map.getOrPut(key, defaultValue)
    }
}

// CallChain[size=6] = QCacheEntry <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThre ... <-[Call]- String.qReplaceFirstIfNonEmptyStringGroup() <-[Call]- String.qApplyColorNestable()[Root]
private data class QCacheEntry(val value: Any?, val duration: Long, val creationTime: Long = qNow) {
    // CallChain[size=8] = QCacheEntry.isExpired() <-[Call]- QCacheMap.clearExpired() <-[Call]- QCacheMa ... <-[Call]- String.qReplaceFirstIfNonEmptyStringGroup() <-[Call]- String.qApplyColorNestable()[Root]
    fun isExpired() = (qNow - creationTime) > duration
}

// CallChain[size=7] = Lock.qWithLock() <-[Call]- QCacheMap.getOrPut() <-[Call]- qCacheItTimedThread ... <-[Call]- String.qReplaceFirstIfNonEmptyStringGroup() <-[Call]- String.qApplyColorNestable()[Root]
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
        noinline filter: (StackFrame) -> Boolean = QE.STACK_FRAME_FILTER,
): List<StackFrame> {
    return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk { s: Stream<StackFrame> ->
        s.asSequence().filter(filter).drop(stackDepth).take(size).toList()
    }
}

// CallChain[size=3] = qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
private inline fun qStackFrame(
        stackDepth: Int = 0,
        noinline filter: (StackFrame) -> Boolean = QE.STACK_FRAME_FILTER,
): StackFrame {
    return qStackFrames(stackDepth, 1, filter)[0]
}

// CallChain[size=3] = RO <-[Ref]- String.qReplaceFirstIfNonEmptyStringGroup() <-[Call]- String.qApplyColorNestable()[Root]
private typealias RO = RegexOption

// CallChain[size=3] = qRe() <-[Call]- String.qReplaceFirstIfNonEmptyStringGroup() <-[Call]- String.qApplyColorNestable()[Root]
private fun qRe(@Language("RegExp") regex: String, vararg opts: RO): Regex {
    return qCacheItOneSecThreadLocal(regex + opts.contentToString()) {
        Regex(regex, setOf(*opts))
    }
}

// CallChain[size=2] = re <-[Call]- String.qApplyColorNestable()[Root]
// https://youtrack.jetbrains.com/issue/KTIJ-5643
private val @receiver:Language("RegExp") String.re: Regex
    get() = qRe(this)

// CallChain[size=2] = String.qReplaceFirstIfNonEmptyStringGroup() <-[Call]- String.qApplyColorNestable()[Root]
private fun String.qReplaceFirstIfNonEmptyStringGroup(@Language("RegExp") regex: String, nonEmptyGroupIdx: Int, replace: String = "$1", vararg opts: RO): String {
    val re = qRe(regex, *opts)

    return if (re.find(this)?.groups?.get(nonEmptyGroupIdx)?.value?.isNotEmpty() == true) {
        re.replaceFirst(this, replace)
    } else {
        this
    }
}

// CallChain[size=6] = QOnlyIfStr <-[Ref]- QMaskResult.toString() <-[Propag]- QMaskResult <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
private enum class QOnlyIfStr(val matches: (String) -> Boolean) {
    // CallChain[size=6] = QOnlyIfStr.Multiline <-[Call]- QMaskResult.toString() <-[Propag]- QMaskResult <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    Multiline({ it.qIsMultiLine() }),
    // CallChain[size=7] = QOnlyIfStr.SingleLine <-[Propag]- QOnlyIfStr.Multiline <-[Call]- QMaskResult. ... <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    SingleLine({ it.qIsSingleLine() }),
    // CallChain[size=7] = QOnlyIfStr.Empty <-[Propag]- QOnlyIfStr.Multiline <-[Call]- QMaskResult.toStr ... <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    Empty({ it.isEmpty() }),
    // CallChain[size=7] = QOnlyIfStr.Blank <-[Propag]- QOnlyIfStr.Multiline <-[Call]- QMaskResult.toStr ... <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    Blank({ it.isBlank() }),
    // CallChain[size=7] = QOnlyIfStr.NotEmpty <-[Propag]- QOnlyIfStr.Multiline <-[Call]- QMaskResult.to ... <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    NotEmpty({ it.isNotEmpty() }),
    // CallChain[size=7] = QOnlyIfStr.NotBlank <-[Propag]- QOnlyIfStr.Multiline <-[Call]- QMaskResult.to ... <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    NotBlank({ it.isNotBlank() }),
    // CallChain[size=7] = QOnlyIfStr.Always <-[Propag]- QOnlyIfStr.Multiline <-[Call]- QMaskResult.toSt ... <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    Always({ true })
}

// CallChain[size=7] = String.qWithNewLinePrefix() <-[Call]- String.qWithNewLineSurround() <-[Call]- ... <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
private fun String.qWithNewLinePrefix(
        numNewLine: Int = 1,
        onlyIf: QOnlyIfStr = QOnlyIfStr.Multiline,
        lineSeparator: QLineSeparator = QLineSeparator.LF,
): String {
    if (!onlyIf.matches(this)) return this

    val nCount = takeWhile { it == '\n' || it == '\r' }.count()

    return lineSeparator.value.repeat(numNewLine) + substring(nCount)
}

// CallChain[size=7] = String.qWithNewLineSuffix() <-[Call]- String.qWithNewLineSurround() <-[Call]- ... <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
private fun String.qWithNewLineSuffix(numNewLine: Int = 1, onlyIf: QOnlyIfStr = QOnlyIfStr.Multiline): String {
    if (!onlyIf.matches(this)) return this

    val nCount = takeLastWhile { it == '\n' || it == '\r' }.count()

    return substring(0, length - nCount) + "\n".repeat(numNewLine)
}

// CallChain[size=6] = String.qWithNewLineSurround() <-[Call]- QMaskResult.toString() <-[Propag]- QM ... <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
private fun String.qWithNewLineSurround(numNewLine: Int = 1, onlyIf: QOnlyIfStr = QOnlyIfStr.Multiline): String {
    if (!onlyIf.matches(this)) return this

    return qWithNewLinePrefix(numNewLine, QOnlyIfStr.Always).qWithNewLineSuffix(numNewLine, QOnlyIfStr.Always)
}

// CallChain[size=7] = String.qIsMultiLine() <-[Call]- QOnlyIfStr.Multiline <-[Call]- QMaskResult.to ... <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
private fun String.qIsMultiLine(): Boolean {
    return this.contains("\n") || this.contains("\r")
}

// CallChain[size=2] = String.qIsSingleLine() <-[Call]- String.qColor()[Root]
private fun String.qIsSingleLine(): Boolean {
    return !this.qIsMultiLine()
}

// CallChain[size=8] = QLineSeparator <-[Ref]- String.qWithNewLinePrefix() <-[Call]- String.qWithNew ... <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
private enum class QLineSeparator(val value: String) {
    // CallChain[size=8] = QLineSeparator.LF <-[Call]- String.qWithNewLinePrefix() <-[Call]- String.qWit ... <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    LF("\n"),
    // CallChain[size=9] = QLineSeparator.CRLF <-[Propag]- QLineSeparator.QLineSeparator() <-[Call]- Str ... <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    CRLF("\r\n"),
    // CallChain[size=9] = QLineSeparator.CR <-[Propag]- QLineSeparator.QLineSeparator() <-[Call]- Strin ... <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    CR("\r");

    companion object {
        
    }
}

// CallChain[size=3] = QMask <-[Ref]- QMaskBetween <-[Call]- qMASK_COLORED[Root]
private interface QMask {
    // CallChain[size=4] = QMask.apply() <-[Propag]- QMask <-[Ref]- QMaskBetween <-[Call]- qMASK_COLORED[Root]
    fun apply(text: String): QMaskResult

    companion object {
        
    }
}

// CallChain[size=2] = QMaskBetween <-[Call]- qMASK_COLORED[Root]
private class QMaskBetween(
    val startSequence: String,
    val endSequence: String,
    val nestStartSequence: String? = if (startSequence != endSequence) {
        startSequence // can have nested structure
    } else {
        null // no nested structure
    },
    val escapeChar: Char? = null,
    val allowEOFEnd: Boolean = false,
    val targetNestDepth: Int = 1,
    val maskIncludeStartAndEndSequence: Boolean = false,
    val invert: Boolean = false,
    val noMaskChars: CharArray? = null, // charArrayOf('\u0020', '\t', '\n', '\r'),
    // U+E000..U+F8FF BMP (0) Private Use Area
    val maskChar: Char = '\uee31',
) : QMask {

    // CallChain[size=3] = QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    override fun apply(text: String): QMaskResult {
        return applyMore(text, null)
    }

    // CallChain[size=4] = QMaskBetween.applyMore() <-[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    fun applyMore(text: String, orgText: String? = null): QMaskResult {
        val regions = text.qFindBetween(
            startSequence,
            endSequence,
            nestStartSequence,
            escapeChar,
            allowEOFEnd,
            targetNestDepth,
            maskIncludeStartAndEndSequence
        )

        val sb = StringBuilder(text.length)

        val iter = text.iterator()

        var idx = -1

        while (iter.hasNext()) {
            idx++

            var masked = false

            val ch = iter.nextChar()

            if (noMaskChars?.contains(ch) == true) {
                sb.append(ch)
                continue
            }

            for (region in regions) {
                if (idx < region.start) {
                    break
                }

                if (region.contains(idx)) {
                    sb.append(if (!invert) maskChar else ch)
                    masked = true
                    break
                }
            }

            if (!masked) {
                sb.append(if (!invert) ch else maskChar)
            }
        }

        val maskedStr = sb.toString()

        return QMaskResult(maskedStr, orgText ?: text, maskChar)
    }
}

// CallChain[size=7] = QMutRegion <-[Ref]- QRegion.toMutRegion() <-[Propag]- QRegion.contains() <-[C ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
private open class QMutRegion(override var start: Int, override var end: Int) : QRegion(start, end) {
    // CallChain[size=8] = QMutRegion.intersectMut() <-[Propag]- QMutRegion <-[Ref]- QRegion.toMutRegion ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    fun intersectMut(region: QRegion) {
        val start = max(this.start, region.start)
        val end = min(this.end, region.end)

        if (start <= end) {
            this.start = start
            this.end = end
        }
    }

    // CallChain[size=8] = QMutRegion.addOffset() <-[Propag]- QMutRegion <-[Ref]- QRegion.toMutRegion()  ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    fun addOffset(offset: Int) {
        start += offset
        end += offset
    }

    // CallChain[size=8] = QMutRegion.shift() <-[Propag]- QMutRegion <-[Ref]- QRegion.toMutRegion() <-[P ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    fun shift(length: Int) {
        this.start += length
        this.end += length
    }
}

// CallChain[size=7] = QRegion <-[Ref]- QRegion.intersect() <-[Propag]- QRegion.contains() <-[Call]- ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
/**
 * [start] inclusive, [end] exclusive
 */
private open class QRegion(open val start: Int, open val end: Int) {
    // CallChain[size=6] = QRegion.toMutRegion() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.a ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    fun toMutRegion(): QMutRegion {
        return QMutRegion(start, end)
    }

    // CallChain[size=6] = QRegion.toRange() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.apply ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    fun toRange(): IntRange {
        return IntRange(start, end + 1)
    }

    // CallChain[size=6] = QRegion.length <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.applyMor ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    val length: Int
        get() = end - start

    // CallChain[size=6] = QRegion.intersect() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.app ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    fun intersect(region: QRegion): QRegion? {
        val start = max(this.start, region.start)
        val end = min(this.end, region.end)

        return if (start <= end) {
            QRegion(end, start)
        } else {
            null
        }
    }

    // CallChain[size=5] = QRegion.contains() <-[Call]- QMaskBetween.applyMore() <-[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    fun contains(idx: Int): Boolean {
        return idx in start until end
    }

    // CallChain[size=6] = QRegion.cut() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.applyMore() <-[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    fun cut(text: String): String {
        return text.substring(start, end)
    }

    // CallChain[size=6] = QRegion.remove() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.applyM ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    fun remove(text: String): String {
        return text.removeRange(start, end)
    }

    // CallChain[size=6] = QRegion.replace() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.apply ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    fun replace(text: String, replacement: String): String {
        return text.replaceRange(start, end, replacement)
    }

    // CallChain[size=6] = QRegion.mask() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.applyMor ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    fun mask(text: String, maskChar: Char = '*'): String {
        return text.replaceRange(this.toRange(), maskChar.toString().repeat(end - start))
    }
}

// CallChain[size=4] = QReplacer <-[Ref]- String.qMaskAndReplace() <-[Call]- String.qMaskAndReplace() <-[Call]- String.qApplyColorNestable()[Root]
private class QReplacer(start: Int, end: Int, val replacement: String) : QMutRegion(start, end)

// CallChain[size=4] = QMaskResult <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
private class QMaskResult(val maskedStr: String, val orgText: String, val maskChar: Char) {
    // CallChain[size=5] = QMaskResult.toString() <-[Propag]- QMaskResult <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    override fun toString(): String {
        val original = orgText.qWithNewLineSurround(onlyIf = QOnlyIfStr.Multiline)
        val masked = maskedStr.replace(maskChar, '*').qWithNewLineSurround(onlyIf = QOnlyIfStr.Multiline)

        return "${QMaskResult::class.simpleName} : $original ${"->".cyan} $masked"
    }
}

// CallChain[size=5] = String.qFindBetween() <-[Call]- QMaskBetween.applyMore() <-[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
private fun String.qFindBetween(
    startSequence: String,
    endSequence: String,
    nestStartSequence: String? = if (startSequence != endSequence) {
        startSequence // can have nested structure
    } else {
        null // no nested structure
    },
    escapeChar: Char? = null,
    allowEOFEnd: Boolean = false,
    nestingDepth: Int = 1,
    regionIncludesStartAndEndSequence: Boolean = false,
): List<QRegion> {
    val finder = QBetween(
        startSequence,
        endSequence,
        nestStartSequence,
        escapeChar,
        allowEOFEnd,
        nestingDepth,
        regionIncludesStartAndEndSequence
    )

    return finder.find(this)
}

// CallChain[size=3] = String.qMaskAndReplace() <-[Call]- String.qMaskAndReplace() <-[Call]- String.qApplyColorNestable()[Root]
private fun String.qMaskAndReplace(
    maskedStr: String,
    ptn: Regex,
    replacement: String = "$1",
    replaceAll: Boolean = true,
): String {
    // Apply Regex pattern to maskedStr
    val findResults: Sequence<MatchResult> = if (replaceAll) {
        ptn.findAll(maskedStr)
    } else {
        val result = ptn.find(maskedStr)
        if (result == null) {
            emptySequence()
        } else {
            sequenceOf(result)
        }
    }

    val replacers: MutableList<QReplacer> = mutableListOf()

    for (r in findResults) {
        val g = r.qResolveReplacementGroup(replacement, this)
        replacers += QReplacer(
            r.range.first,
            r.range.last + 1,
            g
        )
    }

    // Apply replacements to this String instead of maskedStr
    return qMultiReplace(replacers)
}

// CallChain[size=2] = String.qMaskAndReplace() <-[Call]- String.qApplyColorNestable()[Root]
private fun String.qMaskAndReplace(
    mask: QMask,
    ptn: Regex,
    replacement: String = "$1",
    replaceAll: Boolean = true,
): String {
    val maskResult = mask.apply(this)

    return qMaskAndReplace(maskResult.maskedStr, ptn, replacement, replaceAll)
}

// CallChain[size=4] = CharSequence.qMultiReplace() <-[Call]- String.qMaskAndReplace() <-[Call]- String.qMaskAndReplace() <-[Call]- String.qApplyColorNestable()[Root]
/**
 * currently does not support region overlap
 */
private fun CharSequence.qMultiReplace(replacers: List<QReplacer>): String {
    // TODO Use StringBuilder
    val sb = StringBuilder(this)
    var offset = 0
    for (r in replacers) {
        sb.replace(r.start + offset, r.end + offset, r.replacement)
        offset += r.replacement.length - (r.end - r.start)
    }

    return sb.toString()
}

// CallChain[size=4] = MatchResult.qResolveReplacementGroup() <-[Call]- String.qMaskAndReplace() <-[Call]- String.qMaskAndReplace() <-[Call]- String.qApplyColorNestable()[Root]
private fun MatchResult.qResolveReplacementGroup(replacement: String, orgText: String): String {
    var resolveGroup = replacement

    for ((i, g) in groups.withIndex()) {
        if (g == null) continue

        val gValue = if (g.range.last - g.range.first == 0 || !resolveGroup.contains("$")) {
            ""
        } else {
            orgText.substring(g.range)
        }

        resolveGroup = resolveGroup.qReplace("$$i", gValue, '\\')
    }

    return resolveGroup
}

// CallChain[size=5] = CharSequence.qReplace() <-[Call]- MatchResult.qResolveReplacementGroup() <-[C ... .qMaskAndReplace() <-[Call]- String.qMaskAndReplace() <-[Call]- String.qApplyColorNestable()[Root]
private fun CharSequence.qReplace(oldValue: String, newValue: String, escapeChar: Char): String {
    return replace(Regex("""(?<!\Q$escapeChar\E)\Q$oldValue\E"""), newValue)
}

// CallChain[size=7] = QSequenceReader <-[Call]- QBetween.find() <-[Call]- String.qFindBetween() <-[ ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
private class QSequenceReader(text: CharSequence) : QCharReader(text) {
    // CallChain[size=9] = QSequenceReader.sequenceOffset <-[Call]- QSequenceReader.offsetInSequence() < ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    var sequenceOffset = 0

    // CallChain[size=9] = QSequenceReader.sequence <-[Call]- QSequenceReader.peekCurrentCharInSequence( ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    var sequence: CharArray? = null

    // CallChain[size=8] = QSequenceReader.startReadingSequence() <-[Call]- QSequenceReader.detectSequen ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    private fun startReadingSequence(sequence: CharArray): Boolean {
        return if (!hasNextChar(sequence.size)) {
            false
        } else {
            this.sequence = sequence
            sequenceOffset = offset
            true
        }
    }

    // CallChain[size=8] = QSequenceReader.endReadingSequence() <-[Call]- QSequenceReader.detectSequence ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    private fun endReadingSequence(success: Boolean): Boolean {

        if (!success) {
            offset = sequenceOffset
        }

        sequenceOffset = -1

        return success
    }

    // CallChain[size=8] = QSequenceReader.hasNextCharInSequence() <-[Call]- QSequenceReader.detectSeque ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    fun hasNextCharInSequence(): Boolean {
        return if (sequence == null) {
            false
        } else {
            (offsetInSequence() < sequence!!.size) &&
                hasNextChar()
        }
    }

//    inline fun peekNextCharInSequence(): Char {
//        return sequence!![offset - sequenceOffset]
//    }

    // CallChain[size=8] = QSequenceReader.peekCurrentCharInSequence() <-[Call]- QSequenceReader.detectS ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    fun peekCurrentCharInSequence(): Char {
        return sequence!![offsetInSequence()]
    }

    // CallChain[size=8] = QSequenceReader.offsetInSequence() <-[Call]- QSequenceReader.detectSequence() ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    /**
     * 0 to sequence.size - 1
     */
    fun offsetInSequence(): Int {
        return offset - sequenceOffset
    }

    // CallChain[size=7] = QSequenceReader.detectSequence() <-[Call]- QBetween.find() <-[Call]- String.q ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    /**
     * If sequence is detected, move offset by the length of the sequence.
     * If no sequence is found, offset remains unchanged.
     */
    fun detectSequence(sequence: CharArray, eofAllowed: Boolean = false): Boolean {
        if (!startReadingSequence(sequence)) return false

        while (hasNextCharInSequence()) {
            val seqChar = peekCurrentCharInSequence()
            val ch = nextChar()

            if (ch != seqChar) {
                endReadingSequence(false)
                return eofAllowed && isOffsetEOF()
            }
        }

        return if (offsetInSequence() == sequence.size) {
            endReadingSequence(true)
            true
        } else {
            val success = eofAllowed && isOffsetEOF()
            endReadingSequence(success)
            success
        }
    }
}

// CallChain[size=8] = QCharReader <-[Call]- QSequenceReader <-[Call]- QBetween.find() <-[Call]- Str ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
private open class QCharReader(val text: CharSequence) {
    // CallChain[size=9] = QCharReader.offset <-[Propag]- QCharReader <-[Call]- QSequenceReader <-[Call] ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    var offset = 0

    // CallChain[size=9] = QCharReader.lineNumber() <-[Propag]- QCharReader <-[Call]- QSequenceReader <- ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    fun lineNumber(): Int {
        // Consider caret to be between the character on the offset and the character preceding it
        //
        // ex. ( [ ] indicate offsets )
        // [\n]abc\n --> 1
        // \n[\n] --> 2

        var lineBreakCount = 0

        var tmpOffset = offset

        while (tmpOffset >= 0) {
            if (tmpOffset != offset && text[tmpOffset] == '\n') {
                lineBreakCount++
            }

            tmpOffset--
        }

        return lineBreakCount + 1
    }

    // CallChain[size=9] = QCharReader.countIndentSpaces() <-[Propag]- QCharReader <-[Call]- QSequenceRe ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    fun countIndentSpaces(space: Char = ' '): Int {
        var count = 0

        var tmpOffset = offset

        // read backward until previous line break
        while (tmpOffset >= 0) {
            if (text[tmpOffset] == '\n') {
                tmpOffset++
                break
            }

            tmpOffset--
        }

        var ch: Char

        while (true) {
            ch = text[tmpOffset]
            if (ch == space) {
                count++
            } else if (ch == '\n') {
                break
            } else {
                continue
            }

            tmpOffset--

            if (tmpOffset == -1)
                break
        }

        return count
    }

    // CallChain[size=9] = QCharReader.hasNextChar() <-[Propag]- QCharReader <-[Call]- QSequenceReader < ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    inline fun hasNextChar(len: Int = 1): Boolean {
        return offset + len - 1 < text.length
    }

    // CallChain[size=9] = QCharReader.isOffsetEOF() <-[Propag]- QCharReader <-[Call]- QSequenceReader < ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    inline fun isOffsetEOF(): Boolean {
        return offset == text.length - 1
    }

    // CallChain[size=9] = QCharReader.isValidOffset() <-[Propag]- QCharReader <-[Call]- QSequenceReader ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    inline fun isValidOffset(): Boolean {
        return 0 <= offset && offset < text.length
    }

    // CallChain[size=9] = QCharReader.hasPreviousChar() <-[Propag]- QCharReader <-[Call]- QSequenceRead ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    inline fun hasPreviousChar(len: Int = 1): Boolean {
        return 0 < offset - len + 1
    }

    // CallChain[size=9] = QCharReader.previousChar() <-[Propag]- QCharReader <-[Call]- QSequenceReader  ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    inline fun previousChar(len: Int = 1) {
        offset -= len
    }

    // CallChain[size=9] = QCharReader.currentChar() <-[Propag]- QCharReader <-[Call]- QSequenceReader < ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    inline fun currentChar(): Char {
        return text[offset]
    }

    // CallChain[size=9] = QCharReader.peekNextChar() <-[Propag]- QCharReader <-[Call]- QSequenceReader  ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    fun peekNextChar(): Char {
        return text[offset]
    }

    // CallChain[size=9] = QCharReader.moveOffset() <-[Propag]- QCharReader <-[Call]- QSequenceReader <- ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    inline fun moveOffset(plus: Int = 1) {
        offset += plus
    }

    // CallChain[size=9] = QCharReader.nextChar() <-[Propag]- QCharReader <-[Call]- QSequenceReader <-[C ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    /**
     * Read current offset char and add offset by 1.
     */
    inline fun nextChar(): Char {
        return text[offset++]
    }
}

// CallChain[size=6] = QBetween <-[Call]- String.qFindBetween() <-[Call]- QMaskBetween.applyMore() <-[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
private class QBetween(
    val startSequence: String,
    val endSequence: String,
    val nestStartSequence: String? = if (startSequence != endSequence) {
        startSequence // can have nested structure
    } else {
        null // no nested structure
    },
    val escapeChar: Char? = null,
    val allowEOFEnd: Boolean = false,
    val nestingDepth: Int = 1,
    val regionIncludeStartAndEndSequence: Boolean = false,
) {

    // CallChain[size=6] = QBetween.find() <-[Call]- String.qFindBetween() <-[Call]- QMaskBetween.applyM ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    fun find(text: CharSequence): List<QRegion> {
        val reader = QSequenceReader(text)

        val ranges: MutableList<QRegion> = mutableListOf()

        val startChArr = startSequence.toCharArray()
        val nestStartChArr = nestStartSequence?.toCharArray()
        val endChArr = endSequence.toCharArray()

        var nNest = 0

        var startSeqOffset = -1

        while (reader.hasNextChar()) {
            val ch = reader.peekNextChar()

            if (ch == escapeChar) {
                reader.moveOffset(2)
                continue
            } else {

                val startSequenceDetected = if (nNest == 0) {
                    reader.detectSequence(startChArr, allowEOFEnd)
                } else if (nestStartChArr != null) {
                    reader.detectSequence(nestStartChArr, allowEOFEnd)
                } else {
                    false
                }

                if (startSequenceDetected) {
                    nNest++

                    if (nestingDepth == nNest) {
                        startSeqOffset = reader.offset
                    }
                } else if (nNest > 0 && reader.detectSequence(endChArr, allowEOFEnd)) {
                    if (nestingDepth == nNest) {
                        val endSeqOffset = reader.offset - endChArr.size // exclusive

                        ranges += if (!regionIncludeStartAndEndSequence) {
                            QRegion(startSeqOffset, endSeqOffset)
                        } else {
                            val end = min(endSeqOffset + endChArr.size, text.length)
                            QRegion(startSeqOffset - startChArr.size, end)
                        }
                    }

                    nNest--
                } else {
                    reader.moveOffset()
                }
            }
        }

        return ranges
    }
}

// CallChain[size=6] = qNow <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThreadLocal ... <-[Call]- String.qReplaceFirstIfNonEmptyStringGroup() <-[Call]- String.qApplyColorNestable()[Root]
private val qNow: Long
    get() = System.currentTimeMillis()


// ================================================================================
// endregion Src from Non-Root API Files -- Auto Generated by nyab.conf.QCompactLib.kt