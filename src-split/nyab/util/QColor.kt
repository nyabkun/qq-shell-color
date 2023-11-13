// 2023. nyabkun  MIT LICENSE

package nyab.util

import java.awt.Color
import kotlin.math.absoluteValue
import nyab.conf.QMyColorStyle

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

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