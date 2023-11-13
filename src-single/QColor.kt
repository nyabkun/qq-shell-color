// 2023. nyabkun  MIT LICENSE

@file:Suppress("UNCHECKED_CAST")

import java.awt.Color
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

    return if (lastEnd) {
        start + this.substring(0, this.length - 1).replace(qEND, qEND + start) + this[this.length - 1]
    } else {
        start + this.replace(qEND, qEND + start) + qEND
    }
}

// << Root of the CallChain >>
fun String.qColor(fg: QColor? = null, bg: QColor? = null, nestable: Boolean = this.contains(qSTART)): String {
    return if (this.qIsSingleLine()) {
        this.qApplyEscapeLine(fg, bg, nestable)
    } else {
        lineSequence().map { line ->
            line.qApplyEscapeLine(fg, bg, nestable)
        }.joinToString("\n")
    }
}

// << Root of the CallChain >>
fun String.qDeco(deco: QDeco): String {
    return if (this.qIsSingleLine()) {
        this.qApplyEscapeLine(arrayOf(deco.start), nestable = this.contains(qSTART))
    } else {
        lineSequence().map { line ->
            line.qApplyEscapeLine(arrayOf(deco.start), nestable = this.contains(qSTART))
        }.joinToString("\n")
    }
}

// << Root of the CallChain >>
private fun String.qApplyEscapeLine(fg: QColor?, bg: QColor?, nestable: Boolean): String {
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
enum class QDeco(val code: Int) {
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
        fun get(code: Int): QDeco? {
            return QDeco.values().find {
                it.code == code
            }
        }
    }
}

// << Root of the CallChain >>
enum class QColor(val code: Int) {
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
    White(97),

    // << Root of the CallChain >>
    CurrentLine(201),
    // << Root of the CallChain >>
    Selection(202),
    // << Root of the CallChain >>
    Caret(203),
    // << Root of the CallChain >>
    ErrorBG(204);

    // << Root of the CallChain >>
    val fg: String = "$qSTART${code}m"

    // << Root of the CallChain >>
    val bg: String = "$qSTART${code + qBG_JUMP}m"

    // << Root of the CallChain >>
    fun isExtraStyle(): Boolean {
        return code >= 200
    }

    // << Root of the CallChain >>
    fun toCSSHex(style: QMyColorStyle): String {
        return style.toCSSHex(this)
    }

    // << Root of the CallChain >>
    fun toAWTColor(style: QMyColorStyle): Color {
        return style.toAWTColor(this)
    }

    companion object {
        // << Root of the CallChain >>
        val Transparent = Color(0, 0, 0, 0)

        // << Root of the CallChain >>
        fun random(seed: String, colors: Array<QColor> = arrayOf(Yellow, Green, Blue, Purple, Cyan)): QColor {
            val idx = seed.hashCode().rem(colors.size).absoluteValue
            return colors[idx]
        }

        // << Root of the CallChain >>
        fun getFG(ansiEscapeCode: Int): QColor {
            return QColor.values().find {
                it.code == ansiEscapeCode
            }!!
        }

        // << Root of the CallChain >>
        fun getBG(ansiEscapeCode: Int): QColor {
            return getFG(ansiEscapeCode - qBG_JUMP)
        }
    }
}

// << Root of the CallChain >>
fun String.qColorAndDecoDebug(tagStart: String = "[", tagEnd: String = "]"): String {
    var txt = this
    for (color in QColor.values()) {
        txt = txt.replace(color.fg, "$tagStart${color.name}$tagEnd", ignoreCase = false)
        txt = txt.replace(color.bg, "$tagStart${color.name}_BG$tagEnd", ignoreCase = false)
    }
    for (deco in QDeco.values()) {
        txt = txt.replace(deco.start, "$tagStart${deco.name}$tagEnd", ignoreCase = false)
    }

    txt = txt.replace(qEND, "${tagStart}End$tagEnd")

    return txt
}

// << Root of the CallChain >>
fun String.qColorTarget(ptn: Regex, fg: QColor? = null, bg: QColor? = null): String {
    return ptn.replace(this, "$0".qColor(fg, bg))
}

// << Root of the CallChain >>
fun String.qDecoTarget(ptn: Regex, deco: QDeco): String {
    return ptn.replace(this, "$0".qDeco(deco))
}

// << Root of the CallChain >>
fun String.qColorRandom(seed: String = qCallerSrcLineSignature()): String = this.qColor(QColor.random(seed))

// << Root of the CallChain >>
val String?.bold: String
    get() = this?.qDeco(QDeco.Bold) ?: "null".qDeco(QDeco.Bold)

// << Root of the CallChain >>
val String?.italic: String
    get() = this?.qDeco(QDeco.Italic) ?: "null".qDeco(QDeco.Italic)

// << Root of the CallChain >>
val String?.underline: String
    get() = this?.qDeco(QDeco.Underline) ?: "null".qDeco(QDeco.Underline)

// << Root of the CallChain >>
val String?.black: String
    get() = this?.qColor(QColor.Black) ?: "null".qColor(QColor.Black)

// << Root of the CallChain >>
val String?.red: String
    get() = this?.qColor(QColor.Red) ?: "null".qColor(QColor.Red)

// << Root of the CallChain >>
val String?.green: String
    get() = this?.qColor(QColor.Green) ?: "null".qColor(QColor.Green)

// << Root of the CallChain >>
val String?.yellow: String
    get() = this?.qColor(QColor.Yellow) ?: "null".qColor(QColor.Yellow)

// << Root of the CallChain >>
val String?.blue: String
    get() = this?.qColor(QColor.Blue) ?: "null".qColor(QColor.Blue)

// << Root of the CallChain >>
val String?.purple: String
    get() = this?.qColor(QColor.Purple) ?: "null".qColor(QColor.Cyan)

// << Root of the CallChain >>
val String?.cyan: String
    get() = this?.qColor(QColor.Cyan) ?: "null".qColor(QColor.Cyan)

// << Root of the CallChain >>
val String?.light_gray: String
    get() = this?.qColor(QColor.LightGray) ?: "null".qColor(QColor.LightGray)

// << Root of the CallChain >>
val String?.dark_gray: String
    get() = this?.qColor(QColor.DarkGray) ?: "null".qColor(QColor.DarkGray)

// << Root of the CallChain >>
val String?.light_red: String
    get() = this?.qColor(QColor.LightRed) ?: "null".qColor(QColor.LightRed)

// << Root of the CallChain >>
val String?.light_green: String
    get() = this?.qColor(QColor.LightGreen) ?: "null".qColor(QColor.LightGreen)

// << Root of the CallChain >>
val String?.light_yellow: String
    get() = this?.qColor(QColor.LightYellow) ?: "null".qColor(QColor.LightYellow)

// << Root of the CallChain >>
val String?.light_blue: String
    get() = this?.qColor(QColor.LightBlue) ?: "null".qColor(QColor.LightBlue)

// << Root of the CallChain >>
val String?.light_magenta: String
    get() = this?.qColor(QColor.LightPurple) ?: "null".qColor(QColor.LightPurple)

// << Root of the CallChain >>
val String?.light_cyan: String
    get() = this?.qColor(QColor.LightCyan) ?: "null".qColor(QColor.LightCyan)

// << Root of the CallChain >>
val String?.white: String
    get() = this?.qColor(QColor.White) ?: "null".qColor(QColor.White)

// << Root of the CallChain >>
val String.noStyle: String
    get() {
        return this.replace("""\Q$qSTART\E\d{1,3}m""".re, "")
    }


// region Src from Non-Root API Files -- Auto Generated by nyab.conf.QCompactLib.kt
// ================================================================================


// CallChain[size=2] = QMyColorStyle <-[Ref]- QColor.toAWTColor()[Root]
enum class QMyColorStyle(private val cssHex: (QColor) -> String) {
    // CallChain[size=3] = QMyColorStyle.Dracula <-[Propag]- QMyColorStyle.toAWTColor() <-[Call]- QColor.toAWTColor()[Root]
    Dracula({ color ->
        when(color) {
            QColor.Black -> "#000000"
            QColor.White -> "#ffffff"

            QColor.DefaultBG -> "#282a36"
            QColor.DefaultFG -> "#f8f8f2"

            QColor.Red -> "#ff5555"
            QColor.LightRed -> "#ff4c4c" // Original
            QColor.Green -> "#50fa7b"
            QColor.LightGreen -> "#96faaf" // Original
            QColor.Yellow -> "#f1fa8c"
            QColor.LightYellow -> "#f8fcc7" // Original
            QColor.Blue -> "#3287ee"
            QColor.LightBlue -> "#90bdf0" // Original
            QColor.Purple -> "#bd93f9"
            QColor.LightPurple -> "#d7bdfc"
            QColor.Cyan -> "#8be9fd"
            QColor.LightCyan -> "#c2f4ff" // Original

            QColor.LightGray -> "#c0c0c0"
            QColor.DarkGray -> "#555555"

            QColor.ErrorBG -> "#693c3c" // Original
            QColor.CurrentLine -> "#44475a" // Original
            QColor.Selection -> "#64b9ff" // Original
            QColor.Caret -> "#b1def5" // Original
        }
    }),

    // CallChain[size=3] = QMyColorStyle.OneDarkVivid <-[Propag]- QMyColorStyle.toAWTColor() <-[Call]- QColor.toAWTColor()[Root]
    OneDarkVivid({ color ->
        when(color) {
            QColor.Black -> "#000000"
            QColor.White -> "#ffffff"

            QColor.DefaultBG -> "#282c34"
            QColor.DefaultFG -> "#bbbbbb"

            QColor.Red -> "#ef596f"
            QColor.LightRed -> "#f092a0" // Original
            QColor.Green -> "#89ca78"
            QColor.LightGreen -> "#c6e3bf" // Original
            QColor.Yellow -> "#e5c07b"
            QColor.LightYellow -> "#e6d3b1" // Original
            QColor.Blue -> "#61afef"
            QColor.LightBlue -> "#a3cdf0" // Original
            QColor.Purple -> "#d55fde"
            QColor.LightPurple -> "#db9be0" // Original
            QColor.Cyan -> "#2bbac5"
            QColor.LightCyan -> "#91c5c9" // Original
            QColor.LightGray -> "#646a73"
            QColor.DarkGray -> "#464c55"

//            else -> "#FF0000"
            QColor.ErrorBG -> "#693c3c" // Original
            QColor.CurrentLine -> "#44475a" // Original
            QColor.Selection -> "#64b9ff" // Original 100, 185, 255, 50
            QColor.Caret -> "#319ee5" // Original
        }
    });

    companion object {
        
    }

    // CallChain[size=3] = QMyColorStyle.toCSSHex() <-[Call]- QMyColorStyle.toAWTColor() <-[Call]- QColor.toAWTColor()[Root]
    fun toCSSHex(color: QColor): String {
        return cssHex(color)
    }

    // CallChain[size=2] = QMyColorStyle.toAWTColor() <-[Call]- QColor.toAWTColor()[Root]
    fun toAWTColor(color: QColor): Color {
        return Color(qHexColorToRGBPacked(toCSSHex(color)))
    }
}

// CallChain[size=4] = qSTACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
private val qSTACK_FRAME_FILTER: (StackWalker.StackFrame) -> Boolean = {
    !it.className.startsWith("org.gradle") &&
            !it.className.startsWith("org.testng") &&
            !it.className.startsWith("worker.org.gradle") &&
            !it.methodName.endsWith("\$default") && it.fileName != null &&
//            !it.className.startsWith("nyab.test")
//            && !it.className.startsWith(QException::class.qualifiedName!!)
//            && it.methodName != "invokeSuspend"
            it.declaringClass != null
            // TODO It's hard to detect the src line of suspend functions
            && it.declaringClass.canonicalName != null
            && !it.declaringClass.canonicalName.startsWith("kotlin.coroutines")
            && !it.declaringClass.canonicalName.startsWith("kotlinx.coroutines")
}

// CallChain[size=3] = qHexColorToRGBPacked() <-[Call]- QMyColorStyle.toAWTColor() <-[Call]- QColor.toAWTColor()[Root]
private fun qHexColorToRGBPacked(cssHex: String): Int {
    val hex = if (cssHex.startsWith("#")) {
        cssHex.substring(1)
    } else {
        cssHex
    }

    return when (hex.length) {
        3 -> {
            val hr = hex.substring(0, 1)
            val hg = hex.substring(1, 2)
            val hb = hex.substring(2, 3)
            val r = (hr + hr).toInt(16)
            val g = (hg + hg).toInt(16)
            val b = (hb + hb).toInt(16)

            intArrayOf(r, g, b).qPackToRGBInt()
        }

        6 -> {
            val r = hex.substring(0, 2).toInt(16)
            val g = hex.substring(2, 4).toInt(16)
            val b = hex.substring(4, 6).toInt(16)
            intArrayOf(r, g, b).qPackToRGBInt()
        }

        8 -> {
            val r = hex.substring(0, 2).toInt(16)
            val g = hex.substring(2, 4).toInt(16)
            val b = hex.substring(4, 6).toInt(16)
            val a = hex.substring(6, 8).toInt(16)
            intArrayOf(r, g, b, a).qPackToARGBInt()
        }

        else -> {
            throw IllegalArgumentException(hex)
//            QE.IllegalArgument.throwIt(hex)
        }
    }
}

// CallChain[size=4] = IntArray.qPackToARGBInt() <-[Call]- qHexColorToRGBPacked() <-[Call]- QMyColorStyle.toAWTColor() <-[Call]- QColor.toAWTColor()[Root]
private fun IntArray.qPackToARGBInt(): Int {
    return (this[0] shl 24) or (this[1] shl 16) or (this[2] shl 8) or (this[3])
}

// CallChain[size=4] = IntArray.qPackToRGBInt() <-[Call]- qHexColorToRGBPacked() <-[Call]- QMyColorStyle.toAWTColor() <-[Call]- QColor.toAWTColor()[Root]
private fun IntArray.qPackToRGBInt(): Int {
    return (this[0] shl 16) or (this[1] shl 8) or (this[2])
}

// CallChain[size=8] = qDEFAULT_CACHE_IT_EXPIRATION_CHECK_INTERVAL <-[Call]- QCacheMap.QCacheMap() < ... () <-[Call]- qRe() <-[Call]- @receiver:Language("RegExp") String.re <-[Call]- String.noStyle[Root]
private const val qDEFAULT_CACHE_IT_EXPIRATION_CHECK_INTERVAL = 1000L

// CallChain[size=6] = qCacheThreadLocal <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItOneS ... () <-[Call]- qRe() <-[Call]- @receiver:Language("RegExp") String.re <-[Call]- String.noStyle[Root]
private val qCacheThreadLocal: ThreadLocal<QCacheMap> by lazy {
    ThreadLocal.withInitial {
        QCacheMap(
            qDEFAULT_CACHE_IT_EXPIRATION_CHECK_INTERVAL
        )
    }
}

// CallChain[size=4] = qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- @receiver:Language("RegExp") String.re <-[Call]- String.noStyle[Root]
private fun <K : Any, V : Any> qCacheItOneSecThreadLocal(key: K, block: () -> V): V =
    qCacheItTimedThreadLocal(key, 1000L, block)

// CallChain[size=5] = qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- @receiver:Language("RegExp") String.re <-[Call]- String.noStyle[Root]
private fun <K : Any, V : Any> qCacheItTimedThreadLocal(key: K, duration: Long, block: () -> V): V =
    qCacheThreadLocal.get().getOrPut(key) { QCacheEntry(block(), duration, qNow) }.value as V

// CallChain[size=7] = QCacheMap <-[Ref]- qCacheThreadLocal <-[Call]- qCacheItTimedThreadLocal() <-[ ... () <-[Call]- qRe() <-[Call]- @receiver:Language("RegExp") String.re <-[Call]- String.noStyle[Root]
private class QCacheMap(
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
private data class QCacheEntry(val value: Any?, val duration: Long, val creationTime: Long = qNow) {
    // CallChain[size=8] = QCacheEntry.isExpired() <-[Call]- QCacheMap.clearExpired() <-[Call]- QCacheMa ... () <-[Call]- qRe() <-[Call]- @receiver:Language("RegExp") String.re <-[Call]- String.noStyle[Root]
    fun isExpired() = (qNow - creationTime) > duration
}

// CallChain[size=7] = Lock.qWithLock() <-[Call]- QCacheMap.getOrPut() <-[Call]- qCacheItTimedThread ... () <-[Call]- qRe() <-[Call]- @receiver:Language("RegExp") String.re <-[Call]- String.noStyle[Root]
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

// CallChain[size=4] = RO <-[Ref]- qRe() <-[Call]- @receiver:Language("RegExp") String.re <-[Call]- String.noStyle[Root]
private typealias RO = RegexOption

// CallChain[size=3] = qRe() <-[Call]- @receiver:Language("RegExp") String.re <-[Call]- String.noStyle[Root]
private fun qRe(@Language("RegExp") regex: String, vararg opts: RO): Regex {
    return qCacheItOneSecThreadLocal(regex + opts.contentToString()) {
        Regex(regex, setOf(*opts))
    }
}

// CallChain[size=2] = @receiver:Language("RegExp") String.re <-[Call]- String.noStyle[Root]
// https://youtrack.jetbrains.com/issue/KTIJ-5643
private val @receiver:Language("RegExp") String.re: Regex
    get() = qRe(this)

// CallChain[size=3] = CharSequence.qIsMultiLine() <-[Call]- CharSequence.qIsSingleLine() <-[Call]- String.qColor()[Root]
private fun CharSequence.qIsMultiLine(): Boolean {
    return this.contains("\n") || this.contains("\r")
}

// CallChain[size=2] = CharSequence.qIsSingleLine() <-[Call]- String.qColor()[Root]
private fun CharSequence.qIsSingleLine(): Boolean {
    return !this.qIsMultiLine()
}

// CallChain[size=6] = qNow <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- @receiver:Language("RegExp") String.re <-[Call]- String.noStyle[Root]
private val qNow: Long
    get() = System.currentTimeMillis()


// ================================================================================
// endregion Src from Non-Root API Files -- Auto Generated by nyab.conf.QCompactLib.kt