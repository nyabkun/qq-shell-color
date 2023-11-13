// 2023. nyabkun  MIT LICENSE

package nyab.util

import java.awt.Color
import kotlin.math.absoluteValue
import nyab.conf.QMyColorStyle

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=6] = qBG_JUMP <-[Call]- QColor.getBG() <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private const val qBG_JUMP = 10

// CallChain[size=6] = qSTART <-[Call]- QColor.bg <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private const val qSTART = "\u001B["

// CallChain[size=7] = qEND <-[Call]- String.qApplyEscapeLine() <-[Call]- String.qApplyEscapeLine()  ... .qColor() <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private const val qEND = "${qSTART}0m"

// CallChain[size=7] = String.qApplyEscapeNestable() <-[Call]- String.qApplyEscapeLine() <-[Call]- S ... .qColor() <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun String.qApplyEscapeNestable(start: String): String {
    val lastEnd = this.endsWith(qEND)

    return if (lastEnd) {
        start + this.substring(0, this.length - 1).replace(qEND, qEND + start) + this[this.length - 1]
    } else {
        start + this.replace(qEND, qEND + start) + qEND
    }
}

// CallChain[size=4] = String.qColor() <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
fun String.qColor(fg: QColor? = null, bg: QColor? = null, nestable: Boolean = this.contains(qSTART)): String {
    return if (this.qIsSingleLine()) {
        this.qApplyEscapeLine(fg, bg, nestable)
    } else {
        lineSequence().map { line ->
            line.qApplyEscapeLine(fg, bg, nestable)
        }.joinToString("\n")
    }
}

// CallChain[size=3] = String.qDeco() <-[Call]- String.italic <-[Call]- QColorTest.italic()[Root]
fun String.qDeco(deco: QDeco): String {
    return if (this.qIsSingleLine()) {
        this.qApplyEscapeLine(arrayOf(deco.start), nestable = this.contains(qSTART))
    } else {
        lineSequence().map { line ->
            line.qApplyEscapeLine(arrayOf(deco.start), nestable = this.contains(qSTART))
        }.joinToString("\n")
    }
}

// CallChain[size=5] = String.qApplyEscapeLine() <-[Call]- String.qColor() <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun String.qApplyEscapeLine(fg: QColor?, bg: QColor?, nestable: Boolean): String {
    return this.qApplyEscapeLine(
        listOfNotNull(fg?.fg, bg?.bg).toTypedArray(),
        nestable
    )
}

// CallChain[size=6] = String.qApplyEscapeLine() <-[Call]- String.qApplyEscapeLine() <-[Call]- String.qColor() <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
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

// CallChain[size=3] = QDeco <-[Ref]- String.qColorAndDecoDebug() <-[Call]- QColorTest.nest2()[Root]
enum class QDeco(val code: Int) {
    // CallChain[size=4] = QDeco.Bold <-[Propag]- QDeco.start <-[Call]- String.qColorAndDecoDebug() <-[Call]- QColorTest.nest2()[Root]
    // https://en.wikipedia.org/wiki/ANSI_escape_code
    Bold(1),
    // CallChain[size=4] = QDeco.Italic <-[Propag]- QDeco.start <-[Call]- String.qColorAndDecoDebug() <-[Call]- QColorTest.nest2()[Root]
    Italic(3),
    // CallChain[size=4] = QDeco.Underline <-[Propag]- QDeco.start <-[Call]- String.qColorAndDecoDebug() <-[Call]- QColorTest.nest2()[Root]
    Underline(4);

    // CallChain[size=3] = QDeco.start <-[Call]- String.qColorAndDecoDebug() <-[Call]- QColorTest.nest2()[Root]
    val start: String = "$qSTART${code}m"

    companion object {
        
    }
}

// CallChain[size=4] = QColor <-[Ref]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
enum class QColor(val code: Int) {
    // CallChain[size=5] = QColor.Black <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    Black(30),
    // CallChain[size=5] = QColor.Red <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    Red(31),
    // CallChain[size=5] = QColor.Green <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    Green(32),
    // CallChain[size=5] = QColor.Yellow <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    Yellow(33),
    // CallChain[size=5] = QColor.Blue <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    Blue(34),
    // CallChain[size=5] = QColor.Purple <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    Purple(35),
    // CallChain[size=5] = QColor.Cyan <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    Cyan(36),
    // CallChain[size=5] = QColor.LightGray <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    LightGray(37),

    // CallChain[size=5] = QColor.DefaultFG <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    DefaultFG(39),
    // CallChain[size=5] = QColor.DefaultBG <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    DefaultBG(49),

    // CallChain[size=5] = QColor.DarkGray <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    DarkGray(90),
    // CallChain[size=5] = QColor.LightRed <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    LightRed(91),
    // CallChain[size=4] = QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    LightGreen(92),
    // CallChain[size=5] = QColor.LightYellow <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    LightYellow(93),
    // CallChain[size=5] = QColor.LightBlue <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    LightBlue(94),
    // CallChain[size=5] = QColor.LightPurple <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    LightPurple(95),
    // CallChain[size=5] = QColor.LightCyan <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    LightCyan(96),
    // CallChain[size=5] = QColor.White <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    White(97),

    // CallChain[size=5] = QColor.CurrentLine <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    CurrentLine(201),
    // CallChain[size=5] = QColor.Selection <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    Selection(202),
    // CallChain[size=5] = QColor.Caret <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    Caret(203),
    // CallChain[size=5] = QColor.ErrorBG <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    ErrorBG(204);

    // CallChain[size=5] = QColor.fg <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val fg: String = "$qSTART${code}m"

    // CallChain[size=5] = QColor.bg <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val bg: String = "$qSTART${code + qBG_JUMP}m"

    // CallChain[size=5] = QColor.isExtraStyle() <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun isExtraStyle(): Boolean {
        return code >= 200
    }

    // CallChain[size=5] = QColor.toCSSHex() <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun toCSSHex(style: QMyColorStyle): String {
        return style.toCSSHex(this)
    }

    // CallChain[size=5] = QColor.toAWTColor() <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun toAWTColor(style: QMyColorStyle): Color {
        return style.toAWTColor(this)
    }

    companion object {
        // CallChain[size=5] = QColor.Transparent <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        val Transparent = Color(0, 0, 0, 0)

        // CallChain[size=5] = QColor.random() <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        fun random(seed: String, colors: Array<QColor> = arrayOf(Yellow, Green, Blue, Purple, Cyan)): QColor {
            val idx = seed.hashCode().rem(colors.size).absoluteValue
            return colors[idx]
        }

        // CallChain[size=5] = QColor.getFG() <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        fun getFG(ansiEscapeCode: Int): QColor {
            return QColor.values().find {
                it.code == ansiEscapeCode
            }!!
        }

        // CallChain[size=5] = QColor.getBG() <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        fun getBG(ansiEscapeCode: Int): QColor {
            return getFG(ansiEscapeCode - qBG_JUMP)
        }
    }
}

// CallChain[size=2] = String.qColorAndDecoDebug() <-[Call]- QColorTest.nest2()[Root]
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

// CallChain[size=9] = String.qColorTarget() <-[Call]- QException.mySrcAndStack <-[Call]- QException ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
fun String.qColorTarget(ptn: Regex, fg: QColor? = null, bg: QColor? = null): String {
    return ptn.replace(this, "$0".qColor(fg, bg))
}

// CallChain[size=2] = String.qColorRandom() <-[Call]- QColorTest.randomColor()[Root]
fun String.qColorRandom(seed: String = qCallerSrcLineSignature()): String = this.qColor(QColor.random(seed))

// CallChain[size=2] = String.bold <-[Call]- QColorTest.bold()[Root]
val String?.bold: String
    get() = this?.qDeco(QDeco.Bold) ?: "null".qDeco(QDeco.Bold)

// CallChain[size=2] = String.italic <-[Call]- QColorTest.italic()[Root]
val String?.italic: String
    get() = this?.qDeco(QDeco.Italic) ?: "null".qDeco(QDeco.Italic)

// CallChain[size=2] = String.underline <-[Call]- QColorTest.underline()[Root]
val String?.underline: String
    get() = this?.qDeco(QDeco.Underline) ?: "null".qDeco(QDeco.Underline)

// CallChain[size=2] = String.red <-[Call]- QColorTest.nest2()[Root]
val String?.red: String
    get() = this?.qColor(QColor.Red) ?: "null".qColor(QColor.Red)

// CallChain[size=19] = String.green <-[Call]- QFilePathNode.toTreeNodeString() <-[Propag]- QFilePat ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
val String?.green: String
    get() = this?.qColor(QColor.Green) ?: "null".qColor(QColor.Green)

// CallChain[size=9] = String.yellow <-[Call]- QException.qToString() <-[Call]- QException.toString( ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
val String?.yellow: String
    get() = this?.qColor(QColor.Yellow) ?: "null".qColor(QColor.Yellow)

// CallChain[size=2] = String.blue <-[Call]- QColorTest.nest2()[Root]
val String?.blue: String
    get() = this?.qColor(QColor.Blue) ?: "null".qColor(QColor.Blue)

// CallChain[size=19] = String.purple <-[Call]- QFilePathNode.toTreeNodeString() <-[Propag]- QFilePa ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
val String?.purple: String
    get() = this?.qColor(QColor.Purple) ?: "null".qColor(QColor.Cyan)

// CallChain[size=11] = String.cyan <-[Call]- QLogStyle <-[Ref]- QLogStyle.SRC_AND_STACK <-[Call]- Q ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
val String?.cyan: String
    get() = this?.qColor(QColor.Cyan) ?: "null".qColor(QColor.Cyan)

// CallChain[size=3] = String.light_gray <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
val String?.light_gray: String
    get() = this?.qColor(QColor.LightGray) ?: "null".qColor(QColor.LightGray)

// CallChain[size=6] = String.light_red <-[Call]- List<QTestResultElement>.allTestedMethods <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
val String?.light_red: String
    get() = this?.qColor(QColor.LightRed) ?: "null".qColor(QColor.LightRed)

// CallChain[size=3] = String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
val String?.light_green: String
    get() = this?.qColor(QColor.LightGreen) ?: "null".qColor(QColor.LightGreen)

// CallChain[size=5] = String.light_yellow <-[Call]- String.colorIt <-[Call]- qFailMsg() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
val String?.light_yellow: String
    get() = this?.qColor(QColor.LightYellow) ?: "null".qColor(QColor.LightYellow)

// CallChain[size=3] = String.light_blue <-[Call]- qTest() <-[Call]- main()[Root]
val String?.light_blue: String
    get() = this?.qColor(QColor.LightBlue) ?: "null".qColor(QColor.LightBlue)

// CallChain[size=14] = String.light_cyan <-[Call]- qARROW <-[Call]- qArrow() <-[Call]- QLogStyle.qL ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
val String?.light_cyan: String
    get() = this?.qColor(QColor.LightCyan) ?: "null".qColor(QColor.LightCyan)

// CallChain[size=3] = String.noStyle <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
val String.noStyle: String
    get() {
        return this.replace("""\Q$qSTART\E\d{1,3}m""".re, "")
    }