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
        null,
        escapeChar = '\\',
        targetNestDepth = 1,
        maskIncludeStartAndEndSequence = false
    )
}

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
val String.noColor: String
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
        internal fun get(code: Int): QShDeco {
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
    Magenta(35),
    // << Root of the CallChain >>
    Cyan(36),
    // << Root of the CallChain >>
    LightGray(37),

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
    LightMagenta(95),
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
        fun random(seed: String, colors: Array<QShColor> = arrayOf(Yellow, Green, Blue, Magenta, Cyan)): QShColor {
            val idx = seed.hashCode().rem(colors.size).absoluteValue
            return colors[idx]
        }

        // << Root of the CallChain >>
        internal fun get(code: Int): QShColor {
            return QShColor.values().find {
                it.code == code
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
val String?.magenta: String
    get() = this?.qColor(QShColor.Magenta) ?: "null".qColor(QShColor.Cyan)

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
    get() = this?.qColor(QShColor.LightMagenta) ?: "null".qColor(QShColor.LightMagenta)

// << Root of the CallChain >>
val String?.light_cyan: String
    get() = this?.qColor(QShColor.LightCyan) ?: "null".qColor(QShColor.LightCyan)

// << Root of the CallChain >>
val String?.white: String
    get() = this?.qColor(QShColor.White) ?: "null".qColor(QShColor.White)


// region Src from Non-Root API Files -- Auto Generated by nyab.conf.QCompactLib.kt
// ================================================================================


// CallChain[size=5] = QMyException <-[Ref]- QE <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
private enum class QMyException {
    // CallChain[size=5] = QMyException.Other <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    Other,

    // CallChain[size=5] = QMyException.Unreachable <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    Unreachable,
    // CallChain[size=5] = QMyException.Unsupported <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    Unsupported,

    // CallChain[size=5] = QMyException.ShouldBeTrue <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeTrue,
    // CallChain[size=5] = QMyException.ShouldBeFalse <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeFalse,
    // CallChain[size=5] = QMyException.ShouldBeNull <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeNull,
    // CallChain[size=5] = QMyException.ShouldNotBeNull <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldNotBeNull,
    // CallChain[size=5] = QMyException.ShouldBeEmpty <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeEmpty,
    // CallChain[size=5] = QMyException.ShouldBeEmptyDir <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeEmptyDir,
    // CallChain[size=5] = QMyException.ShouldNotBeEmpty <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldNotBeEmpty,
    // CallChain[size=5] = QMyException.ShouldBeZero <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeZero,
    // CallChain[size=5] = QMyException.ShouldNotBeZero <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldNotBeZero,
    // CallChain[size=5] = QMyException.ShouldBeOne <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeOne,
    // CallChain[size=5] = QMyException.ShouldNotBeOne <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldNotBeOne,
    // CallChain[size=5] = QMyException.ShouldNotBeNested <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldNotBeNested,
    // CallChain[size=5] = QMyException.ShouldNotBeNumber <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldNotBeNumber,
    // CallChain[size=5] = QMyException.ShouldThrow <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldThrow,
    // CallChain[size=5] = QMyException.ShouldBeEqual <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeEqual,
    // CallChain[size=5] = QMyException.ShouldNotBeEqual <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldNotBeEqual,
    // CallChain[size=5] = QMyException.ShouldContain <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldContain,
    // CallChain[size=5] = QMyException.ShouldBeDirectory <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeDirectory,
    // CallChain[size=5] = QMyException.ShouldBeRegularFile <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeRegularFile,
    // CallChain[size=5] = QMyException.ShouldBeRelativePath <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeRelativePath,
    // CallChain[size=5] = QMyException.ShouldBeAbsolutePath <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeAbsolutePath,
    // CallChain[size=5] = QMyException.ShouldNotContain <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldNotContain,
    // CallChain[size=5] = QMyException.ShouldStartWith <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldStartWith,
    // CallChain[size=5] = QMyException.ShouldEndWith <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldEndWith,
    // CallChain[size=5] = QMyException.ShouldNotStartWith <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldNotStartWith,
    // CallChain[size=5] = QMyException.ShouldNotEndWith <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldNotEndWith,
    // CallChain[size=5] = QMyException.ShouldBeOddNumber <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeOddNumber,
    // CallChain[size=5] = QMyException.ShouldBeEvenNumber <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeEvenNumber,
    // CallChain[size=5] = QMyException.ShouldBeSubDirectory <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeSubDirectory,
    // CallChain[size=5] = QMyException.ShouldNotBeSubDirectory <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldNotBeSubDirectory,

    // CallChain[size=5] = QMyException.InvalidMainArguments <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    InvalidMainArguments,
    // CallChain[size=5] = QMyException.CommandFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    CommandFail,
    // CallChain[size=5] = QMyException.CatchException <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    CatchException,
    // CallChain[size=5] = QMyException.FileNotFound <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    FileNotFound,
    // CallChain[size=5] = QMyException.DirectoryNotFound <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    DirectoryNotFound,
    // CallChain[size=5] = QMyException.QToJavaArgFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    QToJavaArgFail,
    // CallChain[size=5] = QMyException.WinOpenProcessFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    WinOpenProcessFail,
    // CallChain[size=5] = QMyException.WinOpenProcessTokenFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    WinOpenProcessTokenFail,
    // CallChain[size=5] = QMyException.ExecExternalProcessFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ExecExternalProcessFail,
    // CallChain[size=5] = QMyException.CompileFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    CompileFail,
    // CallChain[size=5] = QMyException.EscapedNonSpecial <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    EscapedNonSpecial,
    // CallChain[size=5] = QMyException.EndsWithEscapeChar <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    EndsWithEscapeChar,
    // CallChain[size=5] = QMyException.TooManyBitFlags <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    TooManyBitFlags,
    // CallChain[size=5] = QMyException.FileAlreadyExists <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    FileAlreadyExists,
    // CallChain[size=5] = QMyException.DirectoryAlreadyExists <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    DirectoryAlreadyExists,
    // CallChain[size=5] = QMyException.FetchLinesFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    FetchLinesFail,
    // CallChain[size=5] = QMyException.LineNumberExceedsMaximum <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    LineNumberExceedsMaximum,
    // CallChain[size=5] = QMyException.QTryFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    QTryFail,
    // CallChain[size=5] = QMyException.TrySetAccessibleFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    TrySetAccessibleFail,
    // CallChain[size=5] = QMyException.CreateZipFileFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    CreateZipFileFail,
    // CallChain[size=5] = QMyException.SrcFileNotFound <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    SrcFileNotFound,
    // CallChain[size=5] = QMyException.IllegalArgument <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    IllegalArgument,
    // CallChain[size=5] = QMyException.RegexSearchNotFound <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    RegexSearchNotFound,
    // CallChain[size=5] = QMyException.SplitSizeInvalid <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    SplitSizeInvalid,
    // CallChain[size=5] = QMyException.PrimaryConstructorNotFound <-[Propag]- QMyException.STACK_FRAME_ ...  <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    PrimaryConstructorNotFound,
    // CallChain[size=5] = QMyException.OpenBrowserFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    OpenBrowserFail,
    // CallChain[size=5] = QMyException.FileOpenFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    FileOpenFail,
    // CallChain[size=5] = QMyException.FunctionNotFound <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    FunctionNotFound,
    // CallChain[size=5] = QMyException.PropertyNotFound <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    PropertyNotFound,
    // CallChain[size=5] = QMyException.MethodNotFound <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    MethodNotFound,
    // CallChain[size=5] = QMyException.FieldNotFound <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    FieldNotFound,
    // CallChain[size=5] = QMyException.UrlNotFound <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    UrlNotFound,
    // CallChain[size=5] = QMyException.ConstructorNotFound <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ConstructorNotFound,
    // CallChain[size=5] = QMyException.ImportOffsetFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ImportOffsetFail,

    // CallChain[size=5] = QMyException.SaveImageFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    SaveImageFail,
    // CallChain[size=5] = QMyException.LoadImageFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    LoadImageFail,

    // CallChain[size=5] = QMyException.CycleDetected <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    CycleDetected,

    // CallChain[size=5] = QMyException.ShouldBeSquareMatrix <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeSquareMatrix,
    // CallChain[size=5] = QMyException.ShouldBeInvertibleMatrix <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeInvertibleMatrix,
    // CallChain[size=5] = QMyException.UnsupportedDifferentiation <-[Propag]- QMyException.STACK_FRAME_ ...  <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    UnsupportedDifferentiation,
    // CallChain[size=5] = QMyException.ShouldNotContainImaginaryNumberOtherThanI <-[Propag]- QMyExcepti ...  <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldNotContainImaginaryNumberOtherThanI,
    // CallChain[size=5] = QMyException.DividedByZero <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    DividedByZero,

    // CallChain[size=5] = QMyException.InvalidPhaseTransition <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    InvalidPhaseTransition,

    // CallChain[size=5] = QMyException.ClassNotFound <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ClassNotFound,
    // CallChain[size=5] = QMyException.InstantiationFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    InstantiationFail,
    // CallChain[size=5] = QMyException.GetEnvironmentVariableFail <-[Propag]- QMyException.STACK_FRAME_ ...  <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    GetEnvironmentVariableFail,

    // CallChain[size=5] = QMyException.TestFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    TestFail
    ;

    companion object {
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

// CallChain[size=8] = qDEFAULT_CACHE_IT_EXPIRATION_CHECK_INTERVAL <-[Call]- QCacheMap.QCacheMap() < ... Local() <-[Call]- qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- re <-[Call]- noColor[Root]
private const val qDEFAULT_CACHE_IT_EXPIRATION_CHECK_INTERVAL = 1000L

// CallChain[size=6] = qThreadLocalCache <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- re <-[Call]- noColor[Root]
private val qThreadLocalCache: ThreadLocal<QCacheMap> by lazy {
    ThreadLocal.withInitial {
        QCacheMap(
            qDEFAULT_CACHE_IT_EXPIRATION_CHECK_INTERVAL
        )
    }
}

// CallChain[size=4] = qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- re <-[Call]- noColor[Root]
private fun <K : Any, V : Any> qCacheItOneSecThreadLocal(key: K, block: () -> V): V =
    qCacheItTimedThreadLocal(key, 1000L, block)

// CallChain[size=5] = qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- re <-[Call]- noColor[Root]
private fun <K : Any, V : Any> qCacheItTimedThreadLocal(key: K, duration: Long, block: () -> V): V =
    qThreadLocalCache.get().getOrPut(key) { QCacheEntry(block(), duration, qNow) }.value as V

// CallChain[size=7] = QCacheMap <-[Ref]- qThreadLocalCache <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- re <-[Call]- noColor[Root]
private class QCacheMap(
    val expirationCheckInterval: Long = qDEFAULT_CACHE_IT_EXPIRATION_CHECK_INTERVAL,
    val threadSafe: Boolean = false
) {
    // CallChain[size=7] = QCacheMap.lastCheck <-[Call]- QCacheMap.getOrPut() <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- re <-[Call]- noColor[Root]
    var lastCheck: Long = -1
    // CallChain[size=7] = QCacheMap.lock <-[Call]- QCacheMap.getOrPut() <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- re <-[Call]- noColor[Root]
    val lock: ReentrantLock = ReentrantLock()
    // CallChain[size=7] = QCacheMap.map <-[Call]- QCacheMap.getOrPut() <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- re <-[Call]- noColor[Root]
    val map: MutableMap<Any, QCacheEntry> = mutableMapOf()

    // CallChain[size=7] = QCacheMap.clearExpired() <-[Call]- QCacheMap.getOrPut() <-[Call]- qCacheItTim ... Local() <-[Call]- qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- re <-[Call]- noColor[Root]
    fun clearExpired(): Int = lock.qWithLock(threadSafe) {
        val toRemove = map.filterValues { it.isExpired() }
        toRemove.forEach { map.remove(it.key) }
        return toRemove.count()
    }

    // CallChain[size=6] = QCacheMap.getOrPut() <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- re <-[Call]- noColor[Root]
    fun getOrPut(key: Any, defaultValue: () -> QCacheEntry): QCacheEntry = lock.qWithLock(threadSafe) {
        val now = qNow
        if (now - lastCheck > expirationCheckInterval) {
            lastCheck = now
            clearExpired()
        }

        map.getOrPut(key, defaultValue)
    }
}

// CallChain[size=6] = QCacheEntry <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- re <-[Call]- noColor[Root]
private data class QCacheEntry(val value: Any?, val duration: Long, val creationTime: Long = qNow) {
    // CallChain[size=8] = QCacheEntry.isExpired() <-[Call]- QCacheMap.clearExpired() <-[Call]- QCacheMa ... Local() <-[Call]- qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- re <-[Call]- noColor[Root]
    fun isExpired() = (qNow - creationTime) > duration
}

// CallChain[size=7] = Lock.qWithLock() <-[Call]- QCacheMap.getOrPut() <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- re <-[Call]- noColor[Root]
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

// CallChain[size=4] = RO <-[Ref]- qRe() <-[Call]- re <-[Call]- noColor[Root]
private typealias RO = RegexOption

// CallChain[size=3] = qRe() <-[Call]- re <-[Call]- noColor[Root]
private fun qRe(@Language("RegExp") regex: String, vararg opts: RO): Regex {
    return qCacheItOneSecThreadLocal(regex + opts.contentToString()) {
        Regex(regex, setOf(*opts))
    }
}

// CallChain[size=2] = re <-[Call]- noColor[Root]
// https://youtrack.jetbrains.com/issue/KTIJ-5643
private val @receiver:Language("RegExp") String.re: Regex
    get() = qRe(this)

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

// CallChain[size=3] = String.qIsMultiLine() <-[Call]- String.qIsSingleLine() <-[Call]- String.qColor()[Root]
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

// CallChain[size=7] = QSequenceReader <-[Call]- QBetween.find() <-[Call]- String.qFindBetween() <-[ ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
private class QSequenceReader(text: CharSequence) : QCharReader(text) {
    // CallChain[size=9] = QSequenceReader.sequenceOffset <-[Call]- QSequenceReader.offsetInSequence() < ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    private var sequenceOffset = 0

    // CallChain[size=9] = QSequenceReader.sequence <-[Call]- QSequenceReader.peekCurrentCharInSequence( ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    private var sequence: CharArray? = null

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
    private fun hasNextCharInSequence(): Boolean {
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
    private fun peekCurrentCharInSequence(): Char {
        return sequence!![offsetInSequence()]
    }

    // CallChain[size=8] = QSequenceReader.offsetInSequence() <-[Call]- QSequenceReader.detectSequence() ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    /**
     * 0 to sequence.size - 1
     */
    private fun offsetInSequence(): Int {
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
        // [\n]abc\n --> lineNumber is 1 "First Line"
        // \n[\n] --> lineNumber is 2 "Second Line"

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
    inline fun previousChar(len: Int = 1): Char {
        offset -= len
        return text[offset]
    }

    // CallChain[size=9] = QCharReader.currentChar() <-[Propag]- QCharReader <-[Call]- QSequenceReader < ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    inline fun currentChar(): Char {
        return text[offset]
    }

    // CallChain[size=9] = QCharReader.peekNextChar() <-[Propag]- QCharReader <-[Call]- QSequenceReader  ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    inline fun peekNextChar(): Char {
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

    // CallChain[size=9] = QCharReader.nextString() <-[Propag]- QCharReader <-[Call]- QSequenceReader <- ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    fun nextString(length: Int): String {
        val str = text.substring(offset + 1, (offset + length).coerceAtMost(text.length))
        offset += length
        return str
    }

    // CallChain[size=9] = QCharReader.previousString() <-[Propag]- QCharReader <-[Call]- QSequenceReade ... -[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    fun previousString(length: Int): String {
        val str = text.substring(offset - length, offset)
        offset -= length
        return str
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

// CallChain[size=6] = qNow <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- re <-[Call]- noColor[Root]
private val qNow: Long
    get() = System.currentTimeMillis()


// ================================================================================
// endregion Src from Non-Root API Files -- Auto Generated by nyab.conf.QCompactLib.kt