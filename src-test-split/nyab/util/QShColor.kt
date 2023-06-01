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

// CallChain[size=6] = qBG_JUMP <-[Call]- QShColor.bg <-[Propag]- QShColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private const val qBG_JUMP = 10

// CallChain[size=6] = qSTART <-[Call]- QShColor.bg <-[Propag]- QShColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private const val qSTART = "\u001B["

// CallChain[size=7] = qEND <-[Call]- String.qApplyEscapeLine() <-[Call]- String.qApplyEscapeLine()  ... Color() <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private const val qEND = "${qSTART}0m"

// CallChain[size=7] = String.qApplyEscapeNestable() <-[Call]- String.qApplyEscapeLine() <-[Call]- S ... Color() <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun String.qApplyEscapeNestable(start: String): String {
    val lastEnd = this.endsWith(qEND)

    return if( lastEnd ) {
            start + this.substring(0, this.length - 1).replace(qEND, qEND + start) + this[this.length - 1]
        } else {
            start + this.replace(qEND, qEND + start) + qEND
        }
}

// CallChain[size=4] = String.qColor() <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
internal fun String.qColor(fg: QShColor? = null, bg: QShColor? = null, nestable: Boolean = this.contains(qSTART)): String {
    return if (this.qIsSingleLine()) {
        this.qApplyEscapeLine(fg, bg, nestable)
    } else {
        lineSequence().map { line ->
            line.qApplyEscapeLine(fg, bg, nestable)
        }.joinToString("\n")
    }
}

// CallChain[size=3] = String.qDeco() <-[Call]- String.italic <-[Call]- QShColorTest.italic()[Root]
internal fun String.qDeco(deco: QShDeco): String {
    return if (this.qIsSingleLine()) {
        this.qApplyEscapeLine(arrayOf(deco.start), nestable = this.contains(qSTART))
    } else {
        lineSequence().map { line ->
            line.qApplyEscapeLine(arrayOf(deco.start), nestable = this.contains(qSTART))
        }.joinToString("\n")
    }
}

// CallChain[size=5] = String.qApplyEscapeLine() <-[Call]- String.qColor() <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun String.qApplyEscapeLine(fg: QShColor?, bg: QShColor?, nestable: Boolean): String {
    return this.qApplyEscapeLine(
        listOfNotNull(fg?.fg, bg?.bg).toTypedArray(),
        nestable
    )
}

// CallChain[size=6] = String.qApplyEscapeLine() <-[Call]- String.qApplyEscapeLine() <-[Call]- String.qColor() <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
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

// CallChain[size=3] = QShDeco <-[Ref]- String.qColorAndDecoDebug() <-[Call]- QShColorTest.nest2()[Root]
internal enum class QShDeco(val code: Int) {
    // CallChain[size=4] = QShDeco.Bold <-[Propag]- QShDeco.start <-[Call]- String.qColorAndDecoDebug() <-[Call]- QShColorTest.nest2()[Root]
    // https://en.wikipedia.org/wiki/ANSI_escape_code
    Bold(1),
    // CallChain[size=4] = QShDeco.Italic <-[Propag]- QShDeco.start <-[Call]- String.qColorAndDecoDebug() <-[Call]- QShColorTest.nest2()[Root]
    Italic(3),
    // CallChain[size=4] = QShDeco.Underline <-[Propag]- QShDeco.start <-[Call]- String.qColorAndDecoDebug() <-[Call]- QShColorTest.nest2()[Root]
    Underline(4);

    // CallChain[size=3] = QShDeco.start <-[Call]- String.qColorAndDecoDebug() <-[Call]- QShColorTest.nest2()[Root]
    val start: String = "$qSTART${code}m"

    companion object {
        
    }
}

// CallChain[size=4] = QShColor <-[Ref]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
internal enum class QShColor(val code: Int) {
    // CallChain[size=5] = QShColor.Black <-[Propag]- QShColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    Black(30),
    // CallChain[size=5] = QShColor.Red <-[Propag]- QShColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    Red(31),
    // CallChain[size=5] = QShColor.Green <-[Propag]- QShColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    Green(32),
    // CallChain[size=5] = QShColor.Yellow <-[Propag]- QShColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    Yellow(33),
    // CallChain[size=5] = QShColor.Blue <-[Propag]- QShColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    Blue(34),
    // CallChain[size=5] = QShColor.Purple <-[Propag]- QShColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    Purple(35),
    // CallChain[size=5] = QShColor.Cyan <-[Propag]- QShColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    Cyan(36),
    // CallChain[size=5] = QShColor.LightGray <-[Propag]- QShColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    LightGray(37),

    // CallChain[size=5] = QShColor.DefaultFG <-[Propag]- QShColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    DefaultFG(39),
    // CallChain[size=5] = QShColor.DefaultBG <-[Propag]- QShColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    DefaultBG(49),

    // CallChain[size=5] = QShColor.DarkGray <-[Propag]- QShColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    DarkGray(90),
    // CallChain[size=5] = QShColor.LightRed <-[Propag]- QShColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    LightRed(91),
    // CallChain[size=4] = QShColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    LightGreen(92),
    // CallChain[size=5] = QShColor.LightYellow <-[Propag]- QShColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    LightYellow(93),
    // CallChain[size=5] = QShColor.LightBlue <-[Propag]- QShColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    LightBlue(94),
    // CallChain[size=5] = QShColor.LightPurple <-[Propag]- QShColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    LightPurple(95),
    // CallChain[size=5] = QShColor.LightCyan <-[Propag]- QShColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    LightCyan(96),
    // CallChain[size=5] = QShColor.White <-[Propag]- QShColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    White(97);

    // CallChain[size=5] = QShColor.fg <-[Propag]- QShColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    val fg: String = "$qSTART${code}m"

    // CallChain[size=5] = QShColor.bg <-[Propag]- QShColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    val bg: String = "$qSTART${code + qBG_JUMP}m"

    companion object {
        // CallChain[size=5] = QShColor.random() <-[Propag]- QShColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
        fun random(seed: String, colors: Array<QShColor> = arrayOf(Yellow, Green, Blue, Purple, Cyan)): QShColor {
            val idx = seed.hashCode().rem(colors.size).absoluteValue
            return colors[idx]
        }

        // CallChain[size=5] = QShColor.get() <-[Propag]- QShColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
        fun get(ansiEscapeCode: Int): QShColor {
            return QShColor.values().find {
                it.code == ansiEscapeCode
            }!!
        }
    }
}

// CallChain[size=2] = String.qColorAndDecoDebug() <-[Call]- QShColorTest.nest2()[Root]
internal fun String.qColorAndDecoDebug(tagStart: String = "[", tagEnd: String = "]"): String {
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

// CallChain[size=8] = String.qColorTarget() <-[Call]- QException.mySrcAndStack <-[Call]- QException ... owItBrackets() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
internal fun String.qColorTarget(ptn: Regex, fg: QShColor? = null, bg: QShColor? = null): String {
    return ptn.replace(this, "$0".qColor(fg, bg))
}

// CallChain[size=2] = String.qColorRandom() <-[Call]- QShColorTest.randomColor()[Root]
internal fun String.qColorRandom(seed: String = qCallerSrcLineSignature()): String = this.qColor(QShColor.random(seed))

// CallChain[size=2] = String.bold <-[Call]- QShColorTest.bold()[Root]
internal val String?.bold: String
    get() = this?.qDeco(QShDeco.Bold) ?: "null".qDeco(QShDeco.Bold)

// CallChain[size=2] = String.italic <-[Call]- QShColorTest.italic()[Root]
internal val String?.italic: String
    get() = this?.qDeco(QShDeco.Italic) ?: "null".qDeco(QShDeco.Italic)

// CallChain[size=2] = String.underline <-[Call]- QShColorTest.underline()[Root]
internal val String?.underline: String
    get() = this?.qDeco(QShDeco.Underline) ?: "null".qDeco(QShDeco.Underline)

// CallChain[size=2] = String.red <-[Call]- QShColorTest.nest2()[Root]
internal val String?.red: String
    get() = this?.qColor(QShColor.Red) ?: "null".qColor(QShColor.Red)

// CallChain[size=2] = String.green <-[Call]- QShColorTest.colourful()[Root]
internal val String?.green: String
    get() = this?.qColor(QShColor.Green) ?: "null".qColor(QShColor.Green)

// CallChain[size=8] = String.yellow <-[Call]- QException.qToString() <-[Call]- QException.toString( ... owItBrackets() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
internal val String?.yellow: String
    get() = this?.qColor(QShColor.Yellow) ?: "null".qColor(QShColor.Yellow)

// CallChain[size=2] = String.blue <-[Call]- QShColorTest.nest2()[Root]
internal val String?.blue: String
    get() = this?.qColor(QShColor.Blue) ?: "null".qColor(QShColor.Blue)

// CallChain[size=2] = String.purple <-[Call]- QShColorTest.colourful()[Root]
internal val String?.purple: String
    get() = this?.qColor(QShColor.Purple) ?: "null".qColor(QShColor.Cyan)

// CallChain[size=10] = String.cyan <-[Call]- QLogStyle <-[Ref]- QLogStyle.SRC_AND_STACK <-[Call]- Q ... owItBrackets() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
internal val String?.cyan: String
    get() = this?.qColor(QShColor.Cyan) ?: "null".qColor(QShColor.Cyan)

// CallChain[size=3] = String.light_gray <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
internal val String?.light_gray: String
    get() = this?.qColor(QShColor.LightGray) ?: "null".qColor(QShColor.LightGray)

// CallChain[size=6] = String.light_red <-[Call]- List<QTestResultElement>.allTestedMethods <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
internal val String?.light_red: String
    get() = this?.qColor(QShColor.LightRed) ?: "null".qColor(QShColor.LightRed)

// CallChain[size=3] = String.light_green <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
internal val String?.light_green: String
    get() = this?.qColor(QShColor.LightGreen) ?: "null".qColor(QShColor.LightGreen)

// CallChain[size=5] = String.light_yellow <-[Call]- String.colorIt <-[Call]- qFailMsg() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
internal val String?.light_yellow: String
    get() = this?.qColor(QShColor.LightYellow) ?: "null".qColor(QShColor.LightYellow)

// CallChain[size=3] = String.light_blue <-[Call]- qTest() <-[Call]- main()[Root]
internal val String?.light_blue: String
    get() = this?.qColor(QShColor.LightBlue) ?: "null".qColor(QShColor.LightBlue)

// CallChain[size=13] = String.light_cyan <-[Call]- qARROW <-[Call]- qArrow() <-[Call]- QLogStyle.qL ... owItBrackets() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
internal val String?.light_cyan: String
    get() = this?.qColor(QShColor.LightCyan) ?: "null".qColor(QShColor.LightCyan)

// CallChain[size=3] = String.noStyle <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
internal val String.noStyle: String
    get() {
        return this.replace("""\Q$qSTART\E\d{1,2}m""".re, "")
    }