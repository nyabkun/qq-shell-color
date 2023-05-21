/*
 * Copyright 2023. nyabkun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package nyab.util

import kotlin.math.absoluteValue

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

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