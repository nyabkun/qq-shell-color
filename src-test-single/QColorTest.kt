// 2023. nyabkun  MIT LICENSE

@file:Suppress("UNCHECKED_CAST")

import com.sun.nio.file.ExtendedOpenOption
import java.awt.Color
import java.io.LineNumberReader
import java.io.PrintStream
import java.lang.StackWalker.StackFrame
import java.lang.ref.WeakReference
import java.lang.reflect.AccessibleObject
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.OpenOption
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.text.DecimalFormat
import java.time.Duration
import java.util.Locale
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import java.util.stream.Stream
import kotlin.concurrent.withLock
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.io.path.absolutePathString
import kotlin.io.path.bufferedReader
import kotlin.io.path.exists
import kotlin.io.path.fileSize
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.isSymbolicLink
import kotlin.io.path.name
import kotlin.io.path.readText
import kotlin.io.path.reader
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.declaredMemberExtensionFunctions
import kotlin.reflect.full.extensionReceiverParameter
import kotlin.reflect.full.isSuperclassOf
import kotlin.reflect.full.memberExtensionFunctions
import kotlin.reflect.full.memberFunctions
import kotlin.streams.asSequence
import org.intellij.lang.annotations.Language


// qq-shell-color is self-contained single-file library created by nyabkun.
// For this test, the source file is also a single-file, self-contained, and contains a runnable main function.
//  - It can be added to your codebase with a simple copy and paste.
//  - You can modify and redistribute it under the MIT License.
//  - Please add a package declaration if necessary.

// << Root of the CallChain >>
fun main() {
    qTest()
//    qTestHumanCheck()
}

// << Root of the CallChain >>
class QColorTest {
    // << Root of the CallChain >>
    @QTestHumanCheckRequired
    fun listAllColors() {
        val txt = QColor.values().joinToString("\n") {
            "${it.name} => " + "　".qColor(bg = it)
        }

        println(txt)
    }

    // << Root of the CallChain >>
    @QTestHumanCheckRequired
    fun randomColor() {
        println("""random""".qColorRandom())
    }

    // << Root of the CallChain >>
    @QTest
    fun nestRedBlue() {
        """red+blue""".red.blue.qColorAndDecoDebug() shouldBe
                "[Blue][Red]red+blue[End]"
    }

    // << Root of the CallChain >>
    @QTest
    fun fgAndBg() {
        """fg + bg""".qColor(QColor.Red, QColor.LightGray).qColorAndDecoDebug() shouldBe
                """[LightGray_BG][Red]fg + bg[End][End]"""
    }

    // << Root of the CallChain >>
    @QTest
    fun underlineRed() {
        // TODO
//        """underline""".underline.red.qColorAndDecoDebug() shouldBe "[Underline][Red]underline[End][End]"
        """underline""".red.underline.qColorAndDecoDebug() shouldBe "[Underline][Red]underline[End]"
    }

    // << Root of the CallChain >>
    @QTest
    fun underline() {
        """underline""".underline.qColorAndDecoDebug() shouldBe "[Underline]underline[End]"
    }

    // << Root of the CallChain >>
    @QTest
    fun bold() {
        """bold""".bold.qColorAndDecoDebug() shouldBe "[Bold]bold[End]"
    }

    // << Root of the CallChain >>
    @QTest
    fun italic() {
        """italic""".italic.qColorAndDecoDebug() shouldBe "[Italic]italic[End]"
    }

    // << Root of the CallChain >>
    @QTest
    fun colourful() {
        ("c".yellow + "o".blue + "l".red + "o".purple + "u".green + "r".cyan + "f".yellow + "u".blue + "l".red).qColorAndDecoDebug() shouldBe """
            [Yellow]c[End][Blue]o[End][Red]l[End][Purple]o[End][Green]u[End][Cyan]r[End][Yellow]f[End][Blue]u[End][Red]l[End]
        """.trimIndent()
    }

    // << Root of the CallChain >>
    @QTest
    fun background() {
        "GreenBG".qColor(fg = null, bg = QColor.Green, false).qColorAndDecoDebug() shouldBe """
            [Green_BG]GreenBG[End]
        """.trimIndent()
    }

    // << Root of the CallChain >>
    @QTest
    fun foregroundAndBackground() {
        "RedFG_YellowBG".qColor(fg = QColor.Red, bg = QColor.Yellow, false).qColorAndDecoDebug() shouldBe """
            [Yellow_BG][Red]RedFG_YellowBG[End][End]
        """.trimIndent()
    }

    // << Root of the CallChain >>
    @QTest
    fun multiline() {
        "abc\ndef\nhij".qColor(fg = QColor.Red, bg = null).qColorAndDecoDebug() shouldBe """
            [Red]abc[End]
            [Red]def[End]
            [Red]hij[End]
        """.trimIndent()

        "abc\ndef\nhij".qColor(fg = null, bg = QColor.Blue).qColorAndDecoDebug() shouldBe """
            [Blue_BG]abc[End]
            [Blue_BG]def[End]
            [Blue_BG]hij[End]
        """.trimIndent()
    }

    // << Root of the CallChain >>
    @QTest
    fun multiline_fg_bg() {
        "abc\ndef\nhij".qColor(fg = QColor.Red, bg = QColor.Blue).qColorAndDecoDebug() shouldBe """
            [Blue_BG][Red]abc[End][End]
            [Blue_BG][Red]def[End][End]
            [Blue_BG][Red]hij[End][End]
        """.trimIndent()
    }

    // << Root of the CallChain >>
    @QTest
    fun colorTarget() {
        """val color = "green"""".qColorTarget(
            ptn = """val""".toRegex(),
            fg = QColor.Purple
        ).qColorTarget(
            ptn = """".*?"""".toRegex(),
            fg = QColor.Green
        ).qColorAndDecoDebug() shouldBe """
            [Purple]val[End] color = [Green]"green"[End]
        """.trimIndent()
    }

    // << Root of the CallChain >>
    @QTest
    fun noStyle() {
        """val color = "text"""".qColorTarget(
            ptn = """val""".toRegex(),
            fg = QColor.Purple
        ).qColorTarget(
            ptn = """".*?"""".toRegex(),
            fg = QColor.Green
        ).noStyle.qColorAndDecoDebug() shouldBe """val color = "text""""
    }

    // << Root of the CallChain >>
    @QTest
    fun nest() {
        "${"▧".red} Test Start ${"▧".red}\nTestClass".blue.qColorAndDecoDebug() shouldBe """
            [Blue][Red]▧[End][Blue] Test Start [Red]▧[End]
            [Blue]TestClass[End]
        """.trimIndent()
    }

    // << Root of the CallChain >>
    @QTest
    fun nest2() {
        "a${"b".blue}c".red.qColorAndDecoDebug() shouldBe
                "[Red]a[Blue]b[End][Red]c[End]"
    }
}

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


// region Src from Non-Root API Files -- Auto Generated by nyab.conf.QCompactLib.kt
// ================================================================================


// CallChain[size=6] = QMyColorStyle <-[Ref]- QColor.toAWTColor() <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
enum class QMyColorStyle(private val cssHex: (QColor) -> String) {
    // CallChain[size=7] = QMyColorStyle.Dracula <-[Propag]- QMyColorStyle.toAWTColor() <-[Call]- QColor ... ightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
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

    // CallChain[size=7] = QMyColorStyle.OneDarkVivid <-[Propag]- QMyColorStyle.toAWTColor() <-[Call]- Q ... ightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
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

    // CallChain[size=7] = QMyColorStyle.toCSSHex() <-[Call]- QMyColorStyle.toAWTColor() <-[Call]- QColo ... ightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun toCSSHex(color: QColor): String {
        return cssHex(color)
    }

    // CallChain[size=6] = QMyColorStyle.toAWTColor() <-[Call]- QColor.toAWTColor() <-[Propag]- QColor.LightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun toAWTColor(color: QColor): Color {
        return Color(qHexColorToRGBPacked(toCSSHex(color)))
    }
}

// CallChain[size=4] = QMyException <-[Ref]- QE <-[Ref]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private enum class QMyException {


    // --- General

    // CallChain[size=7] = QMyException.UnknownReason <-[Call]- QException.QException() <-[Ref]- QE.thro ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    UnknownReason,
    // CallChain[size=13] = QMyException.Unreachable <-[Call]- qUnreachable() <-[Call]- QFetchRule.SINGL ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    Unreachable,
    // CallChain[size=3] = QMyException.ShouldBeTrue <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    ShouldBeTrue,
    // CallChain[size=3] = QMyException.ShouldBeFalse <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    ShouldBeFalse,
    // CallChain[size=14] = QMyException.ShouldNotBeNull <-[Call]- T.qaNotNull() <-[Call]- qSrcFileAtFra ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    ShouldNotBeNull,
    // CallChain[size=3] = QMyException.ShouldBeEqual <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    ShouldBeEqual,
    // CallChain[size=19] = QMyException.CycleDetected <-[Call]- N.depthFirst() <-[Call]- N.walkDescenda ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    CycleDetected,
    // --- Numbers
    // CallChain[size=6] = QMyException.ShouldNotBeZero <-[Call]- Int.qaNotZero() <-[Call]- CharSequence.qMask() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    ShouldNotBeZero,
    // CallChain[size=5] = QMyException.ShouldBeEvenNumber <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    ShouldBeEvenNumber,

    // CallChain[size=7] = QMyException.IllegalArgument <-[Call]- String.qWithMaxLength() <-[Call]- Stri ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    IllegalArgument,
    // --- File IO
    // CallChain[size=13] = QMyException.FileNotFound <-[Call]- qSrcFileAtFrame() <-[Call]- qSrcFileLine ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    FileNotFound,
    // --- Windows// --- Process// --- Log
    // CallChain[size=13] = QMyException.FetchLinesFail <-[Call]- Path.qFetchLinesAround() <-[Call]- qSr ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    FetchLinesFail,
    // CallChain[size=14] = QMyException.LineNumberExceedsMaximum <-[Call]- Path.qLineAt() <-[Call]- Pat ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    LineNumberExceedsMaximum,
    // --- Reflection
    // CallChain[size=5] = QMyException.TrySetAccessibleFail <-[Call]- AccessibleObject.qTrySetAccessible() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    TrySetAccessibleFail,
    // CallChain[size=6] = QMyException.ConstructorNotFound <-[Call]- Class<T>.qConstructor() <-[Call]- Class<T>.qNewInstance() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    ConstructorNotFound,
    // CallChain[size=5] = QMyException.ClassNotFound <-[Call]- qCallerFileMainClass() <-[Call]- qThisFileMainClass <-[Call]- qTest() <-[Call]- main()[Root]
    ClassNotFound,
    // --- String// --- Math// --- Web// --- Testing
    // CallChain[size=3] = QMyException.TestFail <-[Call]- qTest() <-[Call]- main()[Root]
    TestFail;

    // --- Others

    
}

// CallChain[size=11] = QMyLog <-[Ref]- QLogStyle <-[Ref]- QLogStyle.SRC_AND_STACK <-[Call]- QExcept ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private object QMyLog {
    // CallChain[size=10] = QMyLog.no_stacktrace_mode <-[Call]- qLogStackFrames() <-[Call]- QException.m ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    var no_stacktrace_mode: Boolean = false

    // CallChain[size=11] = QMyLog.out <-[Call]- QLogStyle <-[Ref]- QLogStyle.SRC_AND_STACK <-[Call]- QE ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    /**
     * Default output stream
     */
    val out: QOut = QOut.CONSOLE

    // CallChain[size=4] = QMyLog.no_format <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    var no_format: Boolean = false
}

// CallChain[size=7] = QMyMark <-[Ref]- QException.QException() <-[Ref]- QE.throwItBrackets() <-[Cal ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
@Suppress("MayBeConstant")
private object QMyMark {
    // CallChain[size=4] = QMyMark.test_method <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    val test_method = "☕".yellow
    // CallChain[size=7] = QMyMark.warn <-[Call]- QException.QException() <-[Ref]- QE.throwItBrackets()  ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val warn = "⚠".yellow
    // CallChain[size=3] = QMyMark.test_start <-[Call]- qTest() <-[Call]- main()[Root]
    val test_start = "☘".yellow
    
}

// CallChain[size=10] = QMyPath <-[Ref]- qLogStackFrames() <-[Call]- QException.mySrcAndStack <-[Cal ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private object QMyPath {
    // -- dirs

    // CallChain[size=11] = QMyPath.src <-[Call]- QMyPath.src_root <-[Call]- qLogStackFrames() <-[Call]- ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val src = "src".path
    // CallChain[size=11] = QMyPath.src_java <-[Call]- QMyPath.src_root <-[Call]- qLogStackFrames() <-[C ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val src_java = "src-java".path
    // CallChain[size=11] = QMyPath.src_build <-[Call]- QMyPath.src_root <-[Call]- qLogStackFrames() <-[ ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val src_build = "src-build".path
    // CallChain[size=11] = QMyPath.src_experiment <-[Call]- QMyPath.src_root <-[Call]- qLogStackFrames( ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val src_experiment = "src-experiment".path
    // CallChain[size=11] = QMyPath.src_plugin <-[Call]- QMyPath.src_root <-[Call]- qLogStackFrames() <- ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val src_plugin = "src-plugin".path
    // CallChain[size=11] = QMyPath.src_config <-[Call]- QMyPath.src_root <-[Call]- qLogStackFrames() <- ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val src_config = "src-config".path
    // CallChain[size=11] = QMyPath.src_test <-[Call]- QMyPath.src_root <-[Call]- qLogStackFrames() <-[C ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val src_test = "src-test".path
    // --- dir list
    // CallChain[size=10] = QMyPath.src_root <-[Call]- qLogStackFrames() <-[Call]- QException.mySrcAndSt ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val src_root: List<Path> by lazy {
        val base = listOf(
            src,
            src_test,
            src_experiment,
            src_config,
            src_plugin,
            src_java,
            src_build,
            "src".path,
            "test".path,
            "src/main/kotlin".path,
            "src/test/kotlin".path,
            "src/main/java".path,
            "src/test/java".path,
//            ".".path,
        ).filter { it.exists() }

        val search = Paths.get(".").qListByMatch(type = QFType.Dir, nameMatch = QM.startsWith("src-"), maxDepth = 1)

        (base + search).distinct()
    }

    // -- files
}

// CallChain[size=4] = QMyTest <-[Ref]- qOkToTest() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private object QMyTest {
    // CallChain[size=4] = QMyTest.forceTestMode <-[Call]- qOkToTest() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    const val forceTestMode = true
    
}

// CallChain[size=6] = QMyToString <-[Call]- qToStringRegistry <-[Call]- Any.qToString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private object QMyToString {
    
}

// CallChain[size=3] = QE <-[Ref]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private typealias QE = QMyException

// CallChain[size=7] = qSTACK_FRAME_FILTER <-[Call]- QException.QException() <-[Ref]- QE.throwItBrac ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
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

// CallChain[size=7] = qHexColorToRGBPacked() <-[Call]- QMyColorStyle.toAWTColor() <-[Call]- QColor. ... ightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
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

// CallChain[size=8] = IntArray.qPackToARGBInt() <-[Call]- qHexColorToRGBPacked() <-[Call]- QMyColor ... ightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun IntArray.qPackToARGBInt(): Int {
    return (this[0] shl 24) or (this[1] shl 16) or (this[2] shl 8) or (this[3])
}

// CallChain[size=8] = IntArray.qPackToRGBInt() <-[Call]- qHexColorToRGBPacked() <-[Call]- QMyColorS ... ightGreen <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun IntArray.qPackToRGBInt(): Int {
    return (this[0] shl 16) or (this[1] shl 8) or (this[2])
}

// CallChain[size=15] = String.qMatches() <-[Call]- Path.qFind() <-[Call]- Collection<Path>.qFind()  ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun String.qMatches(matcher: QM): Boolean = matcher.matches(this)

// CallChain[size=5] = QM.not <-[Call]- QM.notExact() <-[Call]- QMMethod.nameNotExact() <-[Call]- qTest() <-[Call]- main()[Root]
private val QM.not: QM
    get() = QMatchNot(this)

// CallChain[size=6] = QMatchNot <-[Call]- QM.not <-[Call]- QM.notExact() <-[Call]- QMMethod.nameNotExact() <-[Call]- qTest() <-[Call]- main()[Root]
private class QMatchNot(val matcher: QM) : QM {
    // CallChain[size=7] = QMatchNot.matches() <-[Propag]- QMatchNot <-[Call]- QM.not <-[Call]- QM.notExact() <-[Call]- QMMethod.nameNotExact() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun matches(text: String): Boolean = !matcher.matches(text)

    // CallChain[size=7] = QMatchNot.toString() <-[Propag]- QMatchNot <-[Call]- QM.not <-[Call]- QM.notExact() <-[Call]- QMMethod.nameNotExact() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun toString(): String {
        return this::class.simpleName.toString() + "(matcher=$matcher)"
    }
}

// CallChain[size=15] = QMatchAny <-[Call]- QM.isAny() <-[Propag]- QM.exact() <-[Call]- qSrcFileAtFr ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private object QMatchAny : QM {
    // CallChain[size=16] = QMatchAny.matches() <-[Propag]- QMatchAny <-[Call]- QM.isAny() <-[Propag]- Q ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun matches(text: String): Boolean {
        return true
    }

    // CallChain[size=16] = QMatchAny.toString() <-[Propag]- QMatchAny <-[Call]- QM.isAny() <-[Propag]-  ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun toString(): String {
        return this::class.simpleName.toString()
    }
}

// CallChain[size=15] = QMatchNone <-[Call]- QM.isNone() <-[Propag]- QM.exact() <-[Call]- qSrcFileAt ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private object QMatchNone : QM {
    // CallChain[size=16] = QMatchNone.matches() <-[Propag]- QMatchNone <-[Call]- QM.isNone() <-[Propag] ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun matches(text: String): Boolean {
        return false
    }

    // CallChain[size=16] = QMatchNone.toString() <-[Propag]- QMatchNone <-[Call]- QM.isNone() <-[Propag ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun toString(): String {
        return this::class.simpleName.toString()
    }
}

// CallChain[size=14] = QM <-[Ref]- QM.exact() <-[Call]- qSrcFileAtFrame() <-[Call]- qSrcFileLinesAt ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private interface QM {
    // CallChain[size=14] = QM.matches() <-[Propag]- QM.exact() <-[Call]- qSrcFileAtFrame() <-[Call]- qS ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun matches(text: String): Boolean

    // CallChain[size=14] = QM.isAny() <-[Propag]- QM.exact() <-[Call]- qSrcFileAtFrame() <-[Call]- qSrc ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun isAny(): Boolean = this == QMatchAny

    // CallChain[size=14] = QM.isNone() <-[Propag]- QM.exact() <-[Call]- qSrcFileAtFrame() <-[Call]- qSr ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun isNone(): Boolean = this == QMatchNone

    companion object {
        // CallChain[size=13] = QM.exact() <-[Call]- qSrcFileAtFrame() <-[Call]- qSrcFileLinesAtFrame() <-[C ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        fun exact(text: String, ignoreCase: Boolean = false): QM = QExactMatch(text, ignoreCase)

        // CallChain[size=4] = QM.notExact() <-[Call]- QMMethod.nameNotExact() <-[Call]- qTest() <-[Call]- main()[Root]
        fun notExact(text: String, ignoreCase: Boolean = false): QM = QExactMatch(text, ignoreCase).not

        // CallChain[size=11] = QM.startsWith() <-[Call]- QMyPath.src_root <-[Call]- qLogStackFrames() <-[Ca ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        fun startsWith(text: String, ignoreCase: Boolean = false): QM = QStartsWithMatch(text, ignoreCase)

        
    }
}

// CallChain[size=14] = QExactMatch <-[Call]- QM.exact() <-[Call]- qSrcFileAtFrame() <-[Call]- qSrcF ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private class QExactMatch(val textExact: String, val ignoreCase: Boolean = false) : QM {
    // CallChain[size=15] = QExactMatch.matches() <-[Propag]- QExactMatch <-[Call]- QM.exact() <-[Call]- ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun matches(text: String): Boolean {
        return text.equals(textExact, ignoreCase)
    }

    // CallChain[size=15] = QExactMatch.toString() <-[Propag]- QExactMatch <-[Call]- QM.exact() <-[Call] ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun toString(): String {
        return this::class.simpleName + "(textExact=$textExact, ignoreCase=$ignoreCase)"
    }
}

// CallChain[size=12] = QStartsWithMatch <-[Call]- QM.startsWith() <-[Call]- QMyPath.src_root <-[Cal ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private class QStartsWithMatch(val textStartsWith: String, val ignoreCase: Boolean = false) : QM {
    // CallChain[size=13] = QStartsWithMatch.matches() <-[Propag]- QStartsWithMatch <-[Call]- QM.startsW ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun matches(text: String): Boolean {
        return text.startsWith(textStartsWith, ignoreCase)
    }

    // CallChain[size=13] = QStartsWithMatch.toString() <-[Propag]- QStartsWithMatch <-[Call]- QM.starts ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun toString(): String {
        return this::class.simpleName + "(textStartsWith=$textStartsWith, ignoreCase=$ignoreCase)"
    }
}

// CallChain[size=7] = qAnd() <-[Call]- QMFunc.and() <-[Call]- qToStringRegistry <-[Call]- Any.qToString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun qAnd(vararg matches: QMFunc): QMFunc = QMatchFuncAnd(*matches)

// CallChain[size=6] = QMFunc.and() <-[Call]- qToStringRegistry <-[Call]- Any.qToString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private infix fun QMFunc.and(match: QMFunc): QMFunc {
    return if (this is QMatchFuncAnd) {
        QMatchFuncAnd(*matchList, match)
    } else {
        qAnd(this, match)
    }
}

// CallChain[size=8] = QMatchFuncAny <-[Call]- QMFunc.isAny() <-[Propag]- QMFunc.IncludeExtensionsIn ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private object QMatchFuncAny : QMFuncA() {
    // CallChain[size=9] = QMatchFuncAny.matches() <-[Propag]- QMatchFuncAny <-[Call]- QMFunc.isAny() <- ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun matches(value: KFunction<*>): Boolean {
        return true
    }
}

// CallChain[size=8] = QMatchFuncNone <-[Call]- QMFunc.isNone() <-[Propag]- QMFunc.IncludeExtensions ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private object QMatchFuncNone : QMFuncA() {
    // CallChain[size=9] = QMatchFuncNone.matches() <-[Propag]- QMatchFuncNone <-[Call]- QMFunc.isNone() ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun matches(value: KFunction<*>): Boolean {
        return false
    }
}

// CallChain[size=7] = QMatchFuncDeclaredOnly <-[Call]- QMFunc.DeclaredOnly <-[Call]- qToStringRegis ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private object QMatchFuncDeclaredOnly : QMFuncA() {
    // CallChain[size=8] = QMatchFuncDeclaredOnly.declaredOnly <-[Propag]- QMatchFuncDeclaredOnly <-[Cal ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override val declaredOnly = true

    // CallChain[size=8] = QMatchFuncDeclaredOnly.matches() <-[Propag]- QMatchFuncDeclaredOnly <-[Call]- ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun matches(value: KFunction<*>): Boolean {
        return true
    }
}

// CallChain[size=7] = QMatchFuncIncludeExtensionsInClass <-[Call]- QMFunc.IncludeExtensionsInClass  ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private object QMatchFuncIncludeExtensionsInClass : QMFuncA() {
    // CallChain[size=8] = QMatchFuncIncludeExtensionsInClass.includeExtensionsInClass <-[Propag]- QMatc ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override val includeExtensionsInClass = true

    // CallChain[size=8] = QMatchFuncIncludeExtensionsInClass.matches() <-[Propag]- QMatchFuncIncludeExt ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun matches(value: KFunction<*>): Boolean {
        return true
    }
}

// CallChain[size=7] = QMatchFuncAnd <-[Ref]- QMFunc.and() <-[Call]- qToStringRegistry <-[Call]- Any.qToString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private class QMatchFuncAnd(vararg match: QMFunc) : QMFuncA() {
    // CallChain[size=7] = QMatchFuncAnd.matchList <-[Call]- QMFunc.and() <-[Call]- qToStringRegistry <- ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val matchList = match

    // CallChain[size=8] = QMatchFuncAnd.declaredOnly <-[Propag]- QMatchFuncAnd.matchList <-[Call]- QMFu ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override val declaredOnly = matchList.any { it.declaredOnly }

    // CallChain[size=8] = QMatchFuncAnd.includeExtensionsInClass <-[Propag]- QMatchFuncAnd.matchList <- ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override val includeExtensionsInClass: Boolean = matchList.any { it.includeExtensionsInClass }

    // CallChain[size=8] = QMatchFuncAnd.matches() <-[Propag]- QMatchFuncAnd.matchList <-[Call]- QMFunc. ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun matches(value: KFunction<*>): Boolean {
        return matchList.all { it.matches(value) }
    }
}

// CallChain[size=9] = QMFuncA <-[Call]- QMatchFuncNone <-[Call]- QMFunc.isNone() <-[Propag]- QMFunc ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private abstract class QMFuncA : QMFunc {
    // CallChain[size=10] = QMFuncA.declaredOnly <-[Propag]- QMFuncA <-[Call]- QMatchFuncNone <-[Call]-  ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override val declaredOnly: Boolean = false
    // CallChain[size=10] = QMFuncA.includeExtensionsInClass <-[Propag]- QMFuncA <-[Call]- QMatchFuncNon ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override val includeExtensionsInClass: Boolean = false
}

// CallChain[size=7] = QMFunc <-[Ref]- QMFunc.IncludeExtensionsInClass <-[Call]- qToStringRegistry < ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private interface QMFunc {
    // CallChain[size=7] = QMFunc.declaredOnly <-[Propag]- QMFunc.IncludeExtensionsInClass <-[Call]- qTo ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val declaredOnly: Boolean

    // CallChain[size=7] = QMFunc.includeExtensionsInClass <-[Propag]- QMFunc.IncludeExtensionsInClass < ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val includeExtensionsInClass: Boolean

    // CallChain[size=7] = QMFunc.matches() <-[Propag]- QMFunc.IncludeExtensionsInClass <-[Call]- qToStr ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun matches(value: KFunction<*>): Boolean

    // CallChain[size=7] = QMFunc.isAny() <-[Propag]- QMFunc.IncludeExtensionsInClass <-[Call]- qToStrin ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun isAny(): Boolean = this == QMatchFuncAny

    // CallChain[size=7] = QMFunc.isNone() <-[Propag]- QMFunc.IncludeExtensionsInClass <-[Call]- qToStri ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun isNone(): Boolean = this == QMatchFuncNone

    companion object {
        // CallChain[size=6] = QMFunc.DeclaredOnly <-[Call]- qToStringRegistry <-[Call]- Any.qToString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        val DeclaredOnly: QMFunc = QMatchFuncDeclaredOnly

        // CallChain[size=6] = QMFunc.IncludeExtensionsInClass <-[Call]- qToStringRegistry <-[Call]- Any.qToString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        // TODO OnlyExtensionsInClass
        val IncludeExtensionsInClass: QMFunc = QMatchFuncIncludeExtensionsInClass

        

        // TODO vararg, nullability, param names, type parameter
        // TODO handle createType() more carefully

        // CallChain[size=6] = QMFunc.nameExact() <-[Call]- qToStringRegistry <-[Call]- Any.qToString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        fun nameExact(text: String, ignoreCase: Boolean = false): QMFunc {
            return QMatchFuncName(QM.exact(text, ignoreCase = ignoreCase))
        }

        
    }
}

// CallChain[size=7] = QMatchFuncName <-[Call]- QMFunc.nameExact() <-[Call]- qToStringRegistry <-[Ca ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private class QMatchFuncName(val nameMatcher: QM) : QMFuncA() {
    // CallChain[size=8] = QMatchFuncName.matches() <-[Propag]- QMatchFuncName <-[Call]- QMFunc.nameExac ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun matches(value: KFunction<*>): Boolean {
        return nameMatcher.matches(value.name)
    }

    // CallChain[size=8] = QMatchFuncName.toString() <-[Propag]- QMatchFuncName <-[Call]- QMFunc.nameExa ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun toString(): String {
        return this::class.simpleName + ":" + nameMatcher.toString()
    }
}

// CallChain[size=4] = QMMethod.not <-[Call]- QMMethod.notAnnotation() <-[Call]- qTest() <-[Call]- main()[Root]
private val QMMethod.not: QMMethod
    get() = QMatchMethodNot(this)

// CallChain[size=4] = qAnd() <-[Call]- QMMethod.and() <-[Call]- qTest() <-[Call]- main()[Root]
private fun qAnd(vararg matches: QMMethod): QMMethod = QMatchMethodAnd(*matches)

// CallChain[size=4] = qOr() <-[Call]- QMMethod.or() <-[Call]- qTest() <-[Call]- main()[Root]
private fun qOr(vararg matches: QMMethod): QMMethod = QMatchMethodOr(*matches)

// CallChain[size=3] = QMMethod.and() <-[Call]- qTest() <-[Call]- main()[Root]
private infix fun QMMethod.and(match: QMMethod): QMMethod {
    return if (this is QMatchMethodAnd) {
        QMatchMethodAnd(*matchList, match)
    } else {
        qAnd(this, match)
    }
}

// CallChain[size=3] = QMMethod.or() <-[Call]- qTest() <-[Call]- main()[Root]
private infix fun QMMethod.or(match: QMMethod): QMMethod {
    return if (this is QMatchMethodOr) {
        QMatchMethodOr(*matchList, match)
    } else {
        qOr(this, match)
    }
}

// CallChain[size=5] = QMatchMethodNot <-[Call]- QMMethod.not <-[Call]- QMMethod.notAnnotation() <-[Call]- qTest() <-[Call]- main()[Root]
private class QMatchMethodNot(val matcher: QMMethod) : QMMethodA() {
    // CallChain[size=6] = QMatchMethodNot.matches() <-[Propag]- QMatchMethodNot <-[Call]- QMMethod.not <-[Call]- QMMethod.notAnnotation() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun matches(value: Method): Boolean = !matcher.matches(value)
}

// CallChain[size=5] = QMatchMethodAny <-[Call]- QMMethod.isAny() <-[Propag]- QMMethod.matches() <-[Call]- qTest() <-[Call]- main()[Root]
private object QMatchMethodAny : QMMethodA() {
    // CallChain[size=6] = QMatchMethodAny.matches() <-[Propag]- QMatchMethodAny <-[Call]- QMMethod.isAny() <-[Propag]- QMMethod.matches() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun matches(value: Method): Boolean {
        return true
    }
}

// CallChain[size=5] = QMatchMethodNone <-[Call]- QMMethod.isNone() <-[Propag]- QMMethod.matches() <-[Call]- qTest() <-[Call]- main()[Root]
private object QMatchMethodNone : QMMethodA() {
    // CallChain[size=6] = QMatchMethodNone.matches() <-[Propag]- QMatchMethodNone <-[Call]- QMMethod.isNone() <-[Propag]- QMMethod.matches() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun matches(value: Method): Boolean {
        return false
    }
}

// CallChain[size=4] = QMatchMethodDeclaredOnly <-[Call]- QMMethod.DeclaredOnly <-[Call]- qTest() <-[Call]- main()[Root]
private object QMatchMethodDeclaredOnly : QMMethodA() {
    // CallChain[size=5] = QMatchMethodDeclaredOnly.declaredOnly <-[Propag]- QMatchMethodDeclaredOnly <-[Call]- QMMethod.DeclaredOnly <-[Call]- qTest() <-[Call]- main()[Root]
    override val declaredOnly = true

    // CallChain[size=5] = QMatchMethodDeclaredOnly.matches() <-[Propag]- QMatchMethodDeclaredOnly <-[Call]- QMMethod.DeclaredOnly <-[Call]- qTest() <-[Call]- main()[Root]
    override fun matches(value: Method): Boolean {
        return true
    }
}

// CallChain[size=4] = QMatchMethodAnd <-[Ref]- QMMethod.and() <-[Call]- qTest() <-[Call]- main()[Root]
private class QMatchMethodAnd(vararg match: QMMethod) : QMMethodA() {
    // CallChain[size=4] = QMatchMethodAnd.matchList <-[Call]- QMMethod.and() <-[Call]- qTest() <-[Call]- main()[Root]
    val matchList = match

    // CallChain[size=5] = QMatchMethodAnd.declaredOnly <-[Propag]- QMatchMethodAnd.matchList <-[Call]- QMMethod.and() <-[Call]- qTest() <-[Call]- main()[Root]
    override val declaredOnly = matchList.any { it.declaredOnly }

    // CallChain[size=5] = QMatchMethodAnd.matches() <-[Propag]- QMatchMethodAnd.matchList <-[Call]- QMMethod.and() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun matches(value: Method): Boolean {
        return matchList.all { it.matches(value) }
    }
}

// CallChain[size=4] = QMatchMethodOr <-[Ref]- QMMethod.or() <-[Call]- qTest() <-[Call]- main()[Root]
private class QMatchMethodOr(vararg match: QMMethod) : QMMethodA() {
    // CallChain[size=4] = QMatchMethodOr.matchList <-[Call]- QMMethod.or() <-[Call]- qTest() <-[Call]- main()[Root]
    val matchList = match

    // CallChain[size=5] = QMatchMethodOr.declaredOnly <-[Propag]- QMatchMethodOr.matchList <-[Call]- QMMethod.or() <-[Call]- qTest() <-[Call]- main()[Root]
    override val declaredOnly = matchList.any { it.declaredOnly }

    // CallChain[size=5] = QMatchMethodOr.matches() <-[Propag]- QMatchMethodOr.matchList <-[Call]- QMMethod.or() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun matches(value: Method): Boolean {
        return matchList.any { it.matches(value) }
    }
}

// CallChain[size=6] = QMMethodA <-[Call]- QMatchMethodNone <-[Call]- QMMethod.isNone() <-[Propag]- QMMethod.matches() <-[Call]- qTest() <-[Call]- main()[Root]
private abstract class QMMethodA : QMMethod {
    // CallChain[size=7] = QMMethodA.declaredOnly <-[Propag]- QMMethodA <-[Call]- QMatchMethodNone <-[Call]- QMMethod.isNone() <-[Propag]- QMMethod.matches() <-[Call]- qTest() <-[Call]- main()[Root]
    override val declaredOnly: Boolean = false
}

// CallChain[size=3] = QMMethod <-[Ref]- qTest() <-[Call]- main()[Root]
private interface QMMethod {
    // CallChain[size=4] = QMMethod.declaredOnly <-[Propag]- QMMethod.matches() <-[Call]- qTest() <-[Call]- main()[Root]
    val declaredOnly: Boolean

    // CallChain[size=3] = QMMethod.matches() <-[Call]- qTest() <-[Call]- main()[Root]
    fun matches(value: Method): Boolean

    // CallChain[size=4] = QMMethod.isAny() <-[Propag]- QMMethod.matches() <-[Call]- qTest() <-[Call]- main()[Root]
    fun isAny(): Boolean = this == QMatchMethodAny

    // CallChain[size=4] = QMMethod.isNone() <-[Propag]- QMMethod.matches() <-[Call]- qTest() <-[Call]- main()[Root]
    fun isNone(): Boolean = this == QMatchMethodNone

    companion object {
        // CallChain[size=3] = QMMethod.DeclaredOnly <-[Call]- qTest() <-[Call]- main()[Root]
        val DeclaredOnly: QMMethod = QMatchMethodDeclaredOnly

        // CallChain[size=3] = QMMethod.NoParams <-[Call]- qTest() <-[Call]- main()[Root]
        val NoParams: QMMethod = QMatchMethodParams(arrayOf())

        // TODO vararg, param names, type parameter

        // CallChain[size=3] = QMMethod.annotation() <-[Call]- qTest() <-[Call]- main()[Root]
        fun <T : Annotation> annotation(annotation: KClass<T>, matcher: (T) -> Boolean = { true }): QMMethod {
            return QMatchMethodAnnotation(annotation.java, matcher)
        }

        // CallChain[size=3] = QMMethod.notAnnotation() <-[Call]- qTest() <-[Call]- main()[Root]
        fun <T : Annotation> notAnnotation(annotation: KClass<T>, matcher: (T) -> Boolean = { true }): QMMethod {
            return QMatchMethodAnnotation(annotation.java, matcher).not
        }

        // CallChain[size=3] = QMMethod.annotation() <-[Call]- qTest() <-[Call]- main()[Root]
        fun annotation(annotationName: String): QMMethod {
            return QMatchMethodAnnotationName(QM.exact(annotationName))
        }

        // CallChain[size=3] = QMMethod.nameNotExact() <-[Call]- qTest() <-[Call]- main()[Root]
        fun nameNotExact(text: String, ignoreCase: Boolean = false): QMMethod {
            return QMatchMethodName(QM.notExact(text, ignoreCase = ignoreCase))
        }

        
    }
}

// CallChain[size=4] = QMatchMethodName <-[Call]- QMMethod.nameNotExact() <-[Call]- qTest() <-[Call]- main()[Root]
private class QMatchMethodName(val nameMatcher: QM) : QMMethodA() {
    // CallChain[size=5] = QMatchMethodName.matches() <-[Propag]- QMatchMethodName <-[Call]- QMMethod.nameNotExact() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun matches(value: Method): Boolean {
        return nameMatcher.matches(value.name)
    }
}

// CallChain[size=4] = QMatchMethodParams <-[Call]- QMMethod.NoParams <-[Call]- qTest() <-[Call]- main()[Root]
private class QMatchMethodParams(val params: Array<Class<*>?>) : QMMethodA() {
    // CallChain[size=5] = QMatchMethodParams.matches() <-[Propag]- QMatchMethodParams <-[Call]- QMMethod.NoParams <-[Call]- qTest() <-[Call]- main()[Root]
    override fun matches(value: Method): Boolean {
        if (value.parameters.size != params.size)
            return false

        if (params.isEmpty())
            return true

        return params.withIndex().all { (idx, actualParam) ->
            // Number is assignable from Integer
            if (actualParam == null) {
                true
            } else {
                // Number is assignable from Integer
                value.parameters[idx].type.qIsAssignableFrom(actualParam)
            }
        }
    }
}

// CallChain[size=4] = QMatchMethodAnnotation <-[Call]- QMMethod.notAnnotation() <-[Call]- qTest() <-[Call]- main()[Root]
private class QMatchMethodAnnotation<T : Annotation>(val annotation: Class<T>, val matcher: (T) -> Boolean) : QMMethodA() {
    // CallChain[size=5] = QMatchMethodAnnotation.matches() <-[Propag]- QMatchMethodAnnotation <-[Call]- QMMethod.notAnnotation() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun matches(value: Method): Boolean {
        val anno = value.getAnnotation(annotation) ?: return false

        return matcher(anno)
    }
}

// CallChain[size=4] = QMatchMethodAnnotationName <-[Call]- QMMethod.annotation() <-[Call]- qTest() <-[Call]- main()[Root]
private class QMatchMethodAnnotationName(val nameMatcher: QM, val declaredAnnotationsOnly: Boolean = false) : QMMethodA() {
    // CallChain[size=5] = QMatchMethodAnnotationName.matches() <-[Propag]- QMatchMethodAnnotationName <-[Call]- QMMethod.annotation() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun matches(value: Method): Boolean {
        val annotations = if (declaredAnnotationsOnly) {
            value.declaredAnnotations
        } else {
            value.annotations
        }

        return annotations.any { anno: Annotation ->
            nameMatcher.matches(anno.annotationClass.java.simpleName)
        }
    }
}

// CallChain[size=2] = QTest <-[Call]- QColorTest.nest2()[Root]
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
private annotation class QTest(val testOnlyThis: Boolean = false)

// CallChain[size=2] = QTestHumanCheckRequired <-[Call]- QColorTest.randomColor()[Root]
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
private annotation class QTestHumanCheckRequired(val testOnlyThis: Boolean = false)

// CallChain[size=3] = QBeforeEach <-[Ref]- qTest() <-[Call]- main()[Root]
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
private annotation class QBeforeEach

// CallChain[size=3] = QAfterEach <-[Ref]- qTest() <-[Call]- main()[Root]
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
private annotation class QAfterEach

// CallChain[size=5] = QTestResultElement <-[Ref]- QTestResult.QTestResult() <-[Call]- QTestResult.numFail <-[Call]- qTest() <-[Call]- main()[Root]
private data class QTestResultElement(val method: Method, val cause: Throwable?) {
    // CallChain[size=4] = QTestResultElement.success <-[Call]- QTestResult.numFail <-[Call]- qTest() <-[Call]- main()[Root]
    val success: Boolean
        get() = cause == null
}

// CallChain[size=5] = List<QTestResultElement>.allTestedMethods <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private val List<QTestResultElement>.allTestedMethods: String
    get() =
        "\n[${"Tested"}]\n".blue +
                this.joinToString("\n") {
                    if (it.success) {
                        it.method.qName().green
                    } else {
                        it.method.qName().light_red
                    }
                }

// CallChain[size=3] = QTestResult <-[Ref]- qTest() <-[Call]- main()[Root]
private class QTestResult(val elements: List<QTestResultElement>, val time: Long) {
    // CallChain[size=5] = QTestResult.targetClasses <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    val targetClasses = elements.map { it.method.declaringClass.canonicalName }

    // CallChain[size=6] = QTestResult.numSuccess <-[Call]- QTestResult.str <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    val numSuccess = elements.count { it.success }
    // CallChain[size=3] = QTestResult.numFail <-[Call]- qTest() <-[Call]- main()[Root]
    val numFail = elements.count { !it.success }

    // CallChain[size=5] = QTestResult.str <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    val str = qBracketsBlue("Result", "$numSuccess / ${numFail + numSuccess}", "Time", time.qFormatDuration())

    // CallChain[size=4] = QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    fun printIt(out: QOut = QOut.CONSOLE) {
        out.separator(start = "")

        if (numFail > 0) {
            out.println("Test Fail ...".red)
            out.println(str)
            out.println(elements.allTestedMethods)

            elements.filter { !it.success }.forEach { ele ->
                out.separator()

                val cause = if (ele.cause != null && ele.cause is QException) {
                    ele.cause
                } else if (ele.cause?.cause != null && ele.cause.cause is QException) {
                    ele.cause.cause
                } else {
                    ele.cause
                }

                val causeStr = if (cause != null && cause is QException) {
                    cause.type.name
                } else if (cause != null) {
                    cause::class.simpleName ?: cause::class.java.simpleName
                } else {
                    "null"
                }

                val msg = qBracketsBlue("Failed", ele.method.name.red, "Cause", causeStr.red)

                val mySrcAndMsg = if (cause != null && cause is QException) {
                    val stackColoringRegex = targetClasses.joinToString("|") { ta ->
                        """(.*($ta|${ta}Kt).*?)\("""
                    }.re

                    val stackStr = stackColoringRegex.replace(cause.mySrcAndStack, "$1".qColor(QColor.Blue) + "(")

                    cause.message + "\n\n" + stackStr
                } else {
                    ""
                }

                if (mySrcAndMsg.isNotEmpty()) {
                    out.println(msg + "\n")
                    out.println(mySrcAndMsg)
                } else if (cause != null) {
                    out.println(msg)
                    out.println("StackTrace >>>>>")
                    out.println(cause.stackTraceToString())
                }
            }
        } else {
            out.println(elements.allTestedMethods)
            out.println()
            out.println(str)
            out.println("${"★".yellow} ${" Success ".green} ${"★".yellow}".green + "\n")
        }
    }
}

// CallChain[size=3] = qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun qTestMethods(
    methodsToTest: List<Method>,
    beforeMethod: List<Method> = emptyList(),
    afterMethod: List<Method> = emptyList(),
    out: QOut = QOut.CONSOLE
): QTestResult {
    val results = mutableListOf<QTestResultElement>()

    val timeItResult = qTimeIt(quiet = true) {
        for (method in methodsToTest) {
            // "⭐"
            out.println(qSeparatorWithLabel("${QMyMark.test_method} " + method.qName(true)))

            method.qTrySetAccessible()

            val cause =
                if (method.qIsInstanceMethod()) {
                    val testInstance = method.declaringClass.qNewInstance(setAccessible = true)

                    try {
                        for (before in beforeMethod) {
                            before.invoke(testInstance)
                        }

                        method.invoke(testInstance)

                        for (after in afterMethod) {
                            after.invoke(testInstance)
                        }
                        null
                    } catch (e: Throwable) {
                        e
                    }
                } else {
                    try {
                        for (before in beforeMethod) {
                            before.invoke(null)
                        }

                        method.invoke(null)

                        for (after in afterMethod) {
                            after.invoke(null)
                        }
                        null
                    } catch (e: Throwable) {
                        e
                    }
                }

            results += if (cause?.cause != null && cause is InvocationTargetException) {
                QTestResultElement(method, cause.cause)
            } else {
                QTestResultElement(method, cause)
            }
        }
    }

    val result = QTestResult(results, timeItResult.time)
    result.printIt()

    return result
}

// CallChain[size=2] = qTest() <-[Call]- main()[Root]
private fun qTest(
    vararg targetClasses: Class<*> = arrayOf(qThisFileMainClass),

    targetMethodFilter: QMMethod =
        (QMMethod.annotation(QTest::class) or QMMethod.annotation("Test")) and
                QMMethod.notAnnotation(QTestHumanCheckRequired::class) and
//                QMMethod.notAnnotation(QIgnore::class) and
                QMMethod.DeclaredOnly and
                QMMethod.NoParams and
                QMMethod.nameNotExact("main"),

    beforeMethodFilter: QMMethod =
        (
                QMMethod.annotation(QBeforeEach::class) or QMMethod.annotation("BeforeTest")
                        or QMMethod.annotation("BeforeEach")
                        or QMMethod.annotation("BeforeMethod")
                )
                and QMMethod.DeclaredOnly and QMMethod.NoParams and QMMethod.nameNotExact(
            "main"
        ),

    afterMethodFilter: QMMethod =
        (
                QMMethod.annotation(QAfterEach::class) or QMMethod.annotation("AfterTest")
                        or QMMethod.annotation("AfterEach")
                        or QMMethod.annotation("AfterMethod")
                ) and QMMethod.DeclaredOnly and QMMethod.NoParams and QMMethod.nameNotExact(
            "main"
        ),

    out: QOut = QOut.CONSOLE,

    throwsException: Boolean = true,
): QTestResult {
    val targets = targetClasses.joinToString(", ") { it.simpleName }

    out.separator()

    val callerFileName = qCallerFileName()

    val methodsToTestImmediately = targetClasses.flatMap { cls ->
        cls.qMethods().filter { method ->
            (QMMethod.DeclaredOnly and (
                    QMMethod.annotation(QTest::class) { it.testOnlyThis } or
                            QMMethod.annotation(QTestHumanCheckRequired::class) { it.testOnlyThis })).matches(method)
        }.sortedBy {
            it.name // TODO sort by line number
        }
    }

    val methodsToTest = methodsToTestImmediately.ifEmpty {
        targetClasses.flatMap {
            it.qMethods().filter { method ->
                targetMethodFilter.matches(method)
            }
        }.sortedBy {
            it.name // TODO sort by line number
        }
    }

    qLogStackFrames(
        // "🚀"
        msg = "${QMyMark.test_start} Test Start ${QMyMark.test_start}\n$targets".light_blue,
        style = QLogStyle.MSG_AND_STACK,
        frames = listOf(
            qStackFrameEntryMethod { frame ->
                frame.fileName == callerFileName
            }
        )
    )

    val before = targetClasses.flatMap {
        it.qMethods().filter { method ->
            beforeMethodFilter.matches(method)
        }
    }

    val after = targetClasses.flatMap {
        it.qMethods().filter { method ->
            afterMethodFilter.matches(method)
        }
    }

    val result = qTestMethods(methodsToTest, before, after)

    if (result.numFail > 0 && throwsException) {
        QE.TestFail.throwIt()
    } else {
        return result
    }
}

// CallChain[size=3] = qFailMsg() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun qFailMsg(msg: String = "it is null"): String {
    val cMsg = "[$msg]".colorIt
    return "${QMyMark.warn} $cMsg"
}

// CallChain[size=3] = qFailMsg() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun qFailMsg(actual: Any?, msg: String = "is not equals to", expected: Any?): String {
    val cMsg = "[$msg]".colorIt
    val actualStr = actual.qToLogString() + " " + "(actual)".light_green
    val expectedStr = expected.qToLogString() + " " + "(expected)".blue
    return "${QMyMark.warn} ${actualStr.qWithNewLineSurround(onlyIf = QOnlyIfStr.Always)}$cMsg${
        expectedStr.qWithNewLinePrefix(onlyIf = QOnlyIfStr.Always)
    }"
}

// CallChain[size=4] = String.colorIt <-[Call]- qFailMsg() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private val String.colorIt: String
    get() = this.light_yellow

// CallChain[size=3] = qThrowIt() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun qThrowIt(msg: String, exception: QE) {
    throw QException(exception, msg, null, stackDepth = 2, srcCut = QSrcCut.MULTILINE_INFIX_NOCUT)
}

// CallChain[size=2] = Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private infix fun Any?.shouldBe(expected: Any?) {
    if (!qOkToTest()) return

    if (expected is Boolean && this is Boolean && this != expected) {
        if (expected) {
            val msg = qFailMsg("it is false")

            qThrowIt(msg, QE.ShouldBeTrue)
        } else {
            val msg = qFailMsg("it is true")

            qThrowIt(msg, QE.ShouldBeFalse)
        }
    }

    // TODO create function without trim().noStyle
    //      => shouldBeExact
    val thisStr = this.qToLogString().trim().noStyle
    val expectedStr = expected.qToLogString().trim().noStyle

    if (thisStr != expectedStr) {
        val msg = qFailMsg(thisStr, "is not equals to", expectedStr)

        val diffIdx =
            if (expectedStr.length < thisStr.length) {
                expectedStr.mapIndexedNotNull { idx, ch ->
                    if (thisStr.length > idx && thisStr[idx] != ch) {
                        idx
                    } else {
                        null
                    }
                }.firstOrNull()
            } else {
                thisStr.mapIndexedNotNull { idx, ch ->
                    if (expectedStr.length > idx && expectedStr[idx] != ch) {
                        idx
                    } else {
                        null
                    }
                }.firstOrNull()
            }

        if (diffIdx != null) {
            val expectedChar = expectedStr[diffIdx]
            val actualChar = thisStr[diffIdx]

            qThrowIt(
                msg + "\n" + qBrackets(
                    "expected char at idx=$diffIdx".blue,
                    expectedChar.toString().blue + (" ( CharCodeHex = " + expectedChar.qToHex() + " )").light_gray,
                    "actual char at idx=$diffIdx".light_green,
                    actualChar.toString().light_green + (" ( CharCodeHex = " + actualChar.qToHex() + " )").light_gray
                ),
                QE.ShouldBeEqual
            )
        } else {
            qThrowIt(msg, QE.ShouldBeEqual)
        }
    }
}

// CallChain[size=3] = qOkToTest() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private inline fun qOkToTest(): Boolean {
    return QMyTest.forceTestMode || qIsTesting || qIsDebugging
}

// CallChain[size=3] = Char.qToHex() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun Char.qToHex(): String = String.format("%02X", code)

// CallChain[size=16] = qDEFAULT_CACHE_IT_EXPIRATION_CHECK_INTERVAL <-[Call]- QCacheMap.QCacheMap()  ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private const val qDEFAULT_CACHE_IT_EXPIRATION_CHECK_INTERVAL = 1000L

// CallChain[size=14] = qCacheThreadLocal <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItOne ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private val qCacheThreadLocal: ThreadLocal<QCacheMap> by lazy {
    ThreadLocal.withInitial {
        QCacheMap(
            qDEFAULT_CACHE_IT_EXPIRATION_CHECK_INTERVAL
        )
    }
}

// CallChain[size=13] = qCacheThreadSafe <-[Call]- qCacheItTimed() <-[Call]- qCacheItOneSec() <-[Cal ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private val qCacheThreadSafe: QCacheMap by lazy { QCacheMap(qDEFAULT_CACHE_IT_EXPIRATION_CHECK_INTERVAL, true) }

// CallChain[size=11] = qCacheItOneSec() <-[Call]- qMySrcLinesAtFrame() <-[Call]- qLogStackFrames()  ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun <K : Any, V : Any?> qCacheItOneSec(key: K, block: () -> V): V = qCacheItTimed(key, 1000L, block)

// CallChain[size=12] = qCacheItTimed() <-[Call]- qCacheItOneSec() <-[Call]- qMySrcLinesAtFrame() <- ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun <K : Any, V : Any?> qCacheItTimed(key: K, duration: Long, block: () -> V): V =
    qCacheThreadSafe.getOrPut(key) { QCacheEntry(block(), duration, qNow) }.value as V

// CallChain[size=12] = qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- @receiver:Language("Re ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun <K : Any, V : Any> qCacheItOneSecThreadLocal(key: K, block: () -> V): V =
    qCacheItTimedThreadLocal(key, 1000L, block)

// CallChain[size=13] = qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThreadLocal() <-[Call]- q ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun <K : Any, V : Any> qCacheItTimedThreadLocal(key: K, duration: Long, block: () -> V): V =
    qCacheThreadLocal.get().getOrPut(key) { QCacheEntry(block(), duration, qNow) }.value as V

// CallChain[size=15] = QCacheMap <-[Ref]- qCacheThreadLocal <-[Call]- qCacheItTimedThreadLocal() <- ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private class QCacheMap(
    val expirationCheckInterval: Long = qDEFAULT_CACHE_IT_EXPIRATION_CHECK_INTERVAL,
    val threadSafe: Boolean = false
) {
    // CallChain[size=15] = QCacheMap.lastCheck <-[Call]- QCacheMap.getOrPut() <-[Call]- qCacheItTimedTh ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    var lastCheck: Long = -1
    // CallChain[size=15] = QCacheMap.lock <-[Call]- QCacheMap.getOrPut() <-[Call]- qCacheItTimedThreadL ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val lock: ReentrantLock = ReentrantLock()
    // CallChain[size=15] = QCacheMap.map <-[Call]- QCacheMap.getOrPut() <-[Call]- qCacheItTimedThreadLo ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val map: MutableMap<Any, QCacheEntry> = mutableMapOf()

    // CallChain[size=15] = QCacheMap.clearExpired() <-[Call]- QCacheMap.getOrPut() <-[Call]- qCacheItTi ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun clearExpired(): Int = lock.qWithLock(threadSafe) {
        val toRemove = map.filterValues { it.isExpired() }
        toRemove.forEach { map.remove(it.key) }
        return toRemove.count()
    }

    // CallChain[size=14] = QCacheMap.getOrPut() <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheIt ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun getOrPut(key: Any, defaultValue: () -> QCacheEntry): QCacheEntry = lock.qWithLock(threadSafe) {
        val now = qNow
        if (now - lastCheck > expirationCheckInterval) {
            lastCheck = now
            clearExpired()
        }

        map.getOrPut(key, defaultValue)
    }
}

// CallChain[size=14] = QCacheEntry <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThr ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private data class QCacheEntry(val value: Any?, val duration: Long, val creationTime: Long = qNow) {
    // CallChain[size=16] = QCacheEntry.isExpired() <-[Call]- QCacheMap.clearExpired() <-[Call]- QCacheM ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun isExpired() = (qNow - creationTime) > duration
}

// CallChain[size=14] = QIdxRange <-[Ref]- QCharReader.lastDetectionRange() <-[Propag]- QCharReader  ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private class QIdxRange(val start: Int, val endExclusive: Int)

// CallChain[size=12] = QCharReader <-[Call]- QSequenceReader <-[Call]- QBetween.find() <-[Call]- St ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
@Suppress("NOTHING_TO_INLINE")
private open class QCharReader(val text: CharSequence) {
    // CallChain[size=13] = QCharReader.offset <-[Propag]- QCharReader <-[Call]- QSequenceReader <-[Call ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    var offset = -1
        protected set
    // CallChain[size=13] = QCharReader.mark <-[Propag]- QCharReader <-[Call]- QSequenceReader <-[Call]- ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    private var mark = -1

    // CallChain[size=13] = QCharReader.detectionStartOffset <-[Propag]- QCharReader <-[Call]- QSequence ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    var detectionStartOffset = -1
    // CallChain[size=13] = QCharReader.detectionEndOffset <-[Propag]- QCharReader <-[Call]- QSequenceRe ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    var detectionEndOffset = -1

    // CallChain[size=13] = QCharReader.lastDetectionRange() <-[Propag]- QCharReader <-[Call]- QSequence ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun lastDetectionRange(): QIdxRange {
        return QIdxRange(detectionStartOffset + 1, detectionEndOffset)
    }

    // CallChain[size=13] = QCharReader.lastDetectionLength() <-[Propag]- QCharReader <-[Call]- QSequenc ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun lastDetectionLength(): Int {
        return detectionEndOffset - detectionStartOffset
    }

    // CallChain[size=13] = QCharReader.lastDetectionString() <-[Propag]- QCharReader <-[Call]- QSequenc ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun lastDetectionString(): String {
        return text.substring(detectionStartOffset + 1, detectionEndOffset)
    }

    // CallChain[size=13] = QCharReader.lineNumber() <-[Propag]- QCharReader <-[Call]- QSequenceReader < ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    /**
     * First line is 1.
     * Excluding current offset linebreak.
     */
    fun lineNumber(): Int {
        mark()

        val endOffset = offset

        resetToStart()

        var lineBreakCount = 0

        while(offset < endOffset && !isEOF()) {
            if (detectLineBreak()) {
                offset += lastDetectionLength()
                lineBreakCount++
            } else {
                skip(1)
            }
        }

        resetToMark()

        return lineBreakCount + 1
    }

    // CallChain[size=13] = QCharReader.countCharToEOF() <-[Propag]- QCharReader <-[Call]- QSequenceRead ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    inline fun countCharToEOF(): Int {
        return text.length - offset - 1
    }

    // CallChain[size=13] = QCharReader.readToLineEnd() <-[Propag]- QCharReader <-[Call]- QSequenceReade ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun readToLineEnd(): String {
        val toLineEnd = text.substring(offset + 1, lineEndLastCharIdx())
        offset += toLineEnd.length
        return toLineEnd
    }

    // CallChain[size=13] = QCharReader.skipToLineEnd() <-[Propag]- QCharReader <-[Call]- QSequenceReade ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun skipToLineEnd() {
        offset = lineEndOffset()
    }

    // CallChain[size=13] = QCharReader.skipToLineStart() <-[Propag]- QCharReader <-[Call]- QSequenceRea ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun skipToLineStart() {
        offset = lineStartOffset()
    }

    // CallChain[size=13] = QCharReader.readToLineStart() <-[Propag]- QCharReader <-[Call]- QSequenceRea ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun readToLineStart(): String {
        val toLineStart = text.substring(lineStartFirstCharIdx(), offset + 1)
        offset -= toLineStart.length
        return toLineStart
    }

    // CallChain[size=13] = QCharReader.currentLine() <-[Propag]- QCharReader <-[Call]- QSequenceReader  ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun currentLine(): String {
        return text.substring(lineStartFirstCharIdx(), lineEndLastCharIdx() + 1)
    }

    // CallChain[size=13] = QCharReader.lineStartFirstCharIdx() <-[Propag]- QCharReader <-[Call]- QSeque ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    inline fun lineStartFirstCharIdx(): Int {
        return lineStartOffset() + 1
    }

    // CallChain[size=13] = QCharReader.lineEndLastCharIdx() <-[Propag]- QCharReader <-[Call]- QSequence ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    inline fun lineEndLastCharIdx(): Int {
        return lineEndOffset()
    }

    // CallChain[size=13] = QCharReader.lineStartOffset() <-[Propag]- QCharReader <-[Call]- QSequenceRea ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun lineStartOffset(): Int {
        mark()

        // unread the linebreak if offset is at the linebreak
        if (detectLineBreak()) {
            offset - lastDetectionLength()
        }

        while (offset >= -1) {
            if (detectLineBreak()) {
                val start = offset
                resetToMark()
                return start
            } else {
                unread()
            }
        }

        resetToMark()
        return -1
    }

    // CallChain[size=13] = QCharReader.lineEndOffset() <-[Propag]- QCharReader <-[Call]- QSequenceReade ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    /**
     * next read() => line break or EOF
     */
    fun lineEndOffset(): Int {
        mark()

        if (detectLineBreak()) {
            resetToMark()
            // read linebreak at the current offset
            return if (lastDetectionString() == "\n" && offset != -1 && unread() == '\r') {
                detectionStartOffset - 1 // offset before \r\n
            } else {
                detectionStartOffset // offset before linebreak
            }
        }

        while (!isEOF()) {
            if (detectLineBreak()) {
                resetToMark()
                return detectionStartOffset
            } else {
                read()
            }
        }

        resetToMark()
        return 0
    }

    // CallChain[size=13] = QCharReader.detectLineBreak() <-[Propag]- QCharReader <-[Call]- QSequenceRea ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun detectLineBreak(): Boolean {
        startDetection()

        while (!isEOF()) {
            val ch = read()
            return if (ch == '\n') {
                // \n or \r\n
                endDetection(true)
            } else if (ch == '\r') {
                continue
            } else {
                endDetection(false)
            }
        }

        return endDetection(false)
    }

    // CallChain[size=13] = QCharReader.countIndentSpaces() <-[Propag]- QCharReader <-[Call]- QSequenceR ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun countIndentSpaces(space: Char = ' '): Int {
        mark()

        val toLineStart = readToLineStart()

        return toLineStart.takeWhile { it == space }.count()
    }

    // CallChain[size=13] = QCharReader.isEOF() <-[Propag]- QCharReader <-[Call]- QSequenceReader <-[Cal ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    inline fun isEOF(): Boolean {
        return offset == text.length - 1
    }

    // CallChain[size=13] = QCharReader.peekRead() <-[Propag]- QCharReader <-[Call]- QSequenceReader <-[ ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    inline fun peekRead(): Char {
        return text[offset + 1]
    }

    // CallChain[size=13] = QCharReader.peekUnread() <-[Propag]- QCharReader <-[Call]- QSequenceReader < ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    inline fun peekUnread(): Char {
        return text[offset]
    }

    // CallChain[size=13] = QCharReader.skip() <-[Propag]- QCharReader <-[Call]- QSequenceReader <-[Call ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun skip(n: Int) {
        offset += n
    }

    // CallChain[size=13] = QCharReader.skipToEOF() <-[Propag]- QCharReader <-[Call]- QSequenceReader <- ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun skipToEOF() {
        offset = text.length - 1
    }

    // CallChain[size=13] = QCharReader.read() <-[Propag]- QCharReader <-[Call]- QSequenceReader <-[Call ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    /**
     * Add offset by 1 and read char.
     */
    fun read(): Char {
        return text[++offset]
    }

    // CallChain[size=13] = QCharReader.startDetection() <-[Propag]- QCharReader <-[Call]- QSequenceRead ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    protected inline fun startDetection() {
        detectionStartOffset = offset
    }

    // CallChain[size=13] = QCharReader.endDetection() <-[Propag]- QCharReader <-[Call]- QSequenceReader ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    protected inline fun endDetection(success: Boolean): Boolean {
        detectionEndOffset = offset

        if( !success ) {
            offset = detectionStartOffset
        }

        return success
    }

    // CallChain[size=13] = QCharReader.mark() <-[Propag]- QCharReader <-[Call]- QSequenceReader <-[Call ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    private fun mark() {
        mark = offset
    }

    // CallChain[size=13] = QCharReader.resetToMark() <-[Propag]- QCharReader <-[Call]- QSequenceReader  ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    private fun resetToMark() {
        offset = mark
    }

    // CallChain[size=13] = QCharReader.resetToStart() <-[Propag]- QCharReader <-[Call]- QSequenceReader ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun resetToStart() {
        offset = -1
    }

    // CallChain[size=13] = QCharReader.unread() <-[Propag]- QCharReader <-[Call]- QSequenceReader <-[Ca ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun unread(): Char {
        return text[offset--]
    }

    // CallChain[size=13] = QCharReader.readString() <-[Propag]- QCharReader <-[Call]- QSequenceReader < ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun readString(length: Int): String {
        if( isEOF() )
            return ""

        val str = text.substring(offset + 1, (offset + 1 + length).coerceAtMost(text.length))
        offset += length
        return str
    }

    // CallChain[size=13] = QCharReader.peekReadString() <-[Propag]- QCharReader <-[Call]- QSequenceRead ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun peekReadString(length: Int): String {
        return text.substring(offset + 1, (offset + 1 + length).coerceAtMost(text.length))
    }

    // CallChain[size=13] = QCharReader.peekUnreadString() <-[Propag]- QCharReader <-[Call]- QSequenceRe ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun peekUnreadString(length: Int): String {
        return text.substring(offset + 1 - length, offset + 1)
    }
}

// CallChain[size=15] = Lock.qWithLock() <-[Call]- QCacheMap.getOrPut() <-[Call]- qCacheItTimedThrea ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private inline fun <T> Lock.qWithLock(threadSafe: Boolean, block: () -> T): T {
    return if (threadSafe) {
        withLock(block)
    } else {
        block()
    }
}

// CallChain[size=13] = QE.throwIt() <-[Call]- qUnreachable() <-[Call]- QFetchRule.SINGLE_LINE <-[Ca ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun QE.throwIt(msg: Any? = "", e: Throwable? = null, stackDepth: Int = 0): Nothing {
    throw QException(
        this,
        if (msg is String && msg.isEmpty()) {
            "No detailed error messages".light_gray
        } else {
            msg.qToLogString()
        },
        e,
        stackDepth = stackDepth + 1
    )
}

// CallChain[size=14] = QE.throwItFile() <-[Call]- LineNumberReader.qFetchLinesAround() <-[Call]- Pa ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun QE.throwItFile(path: Path, e: Throwable? = null, stackDepth: Int = 0): Nothing {
    throw QException(this, qBrackets("File", path.absolutePathString()), e, stackDepth = stackDepth + 1)
}

// CallChain[size=5] = QE.throwItBrackets() <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun QE.throwItBrackets(vararg keysAndValues: Any?, e: Throwable? = null, stackDepth: Int = 0): Nothing {
    throw QException(this, qBracketsCyan(*keysAndValues), e, stackDepth = stackDepth + 1)
}

// CallChain[size=12] = qUnreachable() <-[Call]- QFetchRule.SINGLE_LINE <-[Call]- QSrcCut.QSrcCut()  ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun qUnreachable(msg: Any? = ""): Nothing {
    QE.Unreachable.throwIt(msg)
}

// CallChain[size=6] = QException <-[Call]- QE.throwItBrackets() <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private class QException(
    val type: QE = QE.UnknownReason,
    msg: String = QMyMark.warn,
    e: Throwable? = null,
    val stackDepth: Int = 0,
    stackSize: Int = 20,
    stackFilter: (StackWalker.StackFrame) -> Boolean = qSTACK_FRAME_FILTER,
    private val srcCut: QSrcCut = QSrcCut.MULTILINE_NOCUT,
) : RuntimeException(msg, e) {

    // CallChain[size=7] = QException.printStackTrace() <-[Propag]- QException.QException() <-[Ref]- QE. ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun printStackTrace(s: PrintStream) {
        s.println("\n" + qToString() + "\n" + mySrcAndStack)
    }

    // CallChain[size=8] = QException.stackFrames <-[Call]- QException.getStackTrace() <-[Propag]- QExce ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val stackFrames = qStackFrames(stackDepth + 2, size = stackSize, filter = stackFilter)

    // CallChain[size=8] = QException.mySrcAndStack <-[Call]- QException.printStackTrace() <-[Propag]- Q ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val mySrcAndStack: String by lazy {
        qLogStackFrames(frames = stackFrames, style = QLogStyle.SRC_AND_STACK, srcCut = srcCut, quiet = true)
            .qColorTarget(qRe("""\sshould[a-zA-Z]+"""), QColor.LightYellow)
    }

    // CallChain[size=7] = QException.getStackTrace() <-[Propag]- QException.QException() <-[Ref]- QE.th ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun getStackTrace(): Array<StackTraceElement> {
        return stackFrames.map {
            it.toStackTraceElement()
        }.toTypedArray()
    }

    // CallChain[size=8] = QException.qToString() <-[Call]- QException.toString() <-[Propag]- QException ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun qToString(): String {
        val msg = message

        return if (!msg.isNullOrEmpty() && type.name != message) {
            "${type.name.qCamelCaseToSpaceSeparated().yellow} ${":".light_gray}${
            msg.qWithSpacePrefix(onlyIf = QOnlyIfStr.SingleLine).qWithNewLinePrefix(onlyIf = QOnlyIfStr.Multiline)
            }".trim()
        } else {
            type.name.yellow
        }
    }

    // CallChain[size=7] = QException.toString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun toString(): String {
        return qToString()
//         used by @Test
//        return type.name.yellow
    }
}

// CallChain[size=6] = Boolean.qaTrue() <-[Call]- String.qWithMinAndMaxLength() <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun Boolean.qaTrue(exceptionType: QE = QE.ShouldBeTrue, msg: Any? = "") {
    if (!this) {
        exceptionType.throwIt(stackDepth = 1, msg = msg)
    }
}

// CallChain[size=13] = T.qaNotNull() <-[Call]- qSrcFileAtFrame() <-[Call]- qSrcFileLinesAtFrame() < ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private inline fun <reified T : Any> T?.qaNotNull(exceptionType: QE = QE.ShouldNotBeNull, msg: Any? = "${T::class.simpleName} should not be null"): T {
    if (this != null) {
        return this
    } else {
        exceptionType.throwIt(stackDepth = 1, msg = msg)
    }
}

// CallChain[size=5] = Int.qaNotZero() <-[Call]- CharSequence.qMask() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun Int?.qaNotZero(exceptionType: QE = QE.ShouldNotBeZero, msg: Any? = ""): Int {
    if (this == null) {
        QE.ShouldNotBeNull.throwIt(stackDepth = 1, msg = msg)
    } else if (this == 0) {
        exceptionType.throwIt(stackDepth = 1, msg = msg)
    } else {
        return this
    }
}

// CallChain[size=21] = QExProps <-[Call]- Any.qSetExProp() <-[Call]- N.children <-[Call]- N.depthFi ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
/**
 * Minimal Version of IdentityWeakHashMap.
 */
private object QExProps {
    // CallChain[size=22] = QExProps.map <-[Call]- QExProps.put() <-[Call]- Any.qSetExProp() <-[Call]- N ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val map: MutableMap<WeakKey, HashMap<String, Any?>> = HashMap()

    // CallChain[size=22] = QExProps.removeGarbageCollectedEntries() <-[Call]- QExProps.put() <-[Call]-  ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun removeGarbageCollectedEntries() {
        qMinIntervalRun("QExProps.removeGarbageCollectedEntries", 5000) {
            // This takes large time
            map.keys.removeIf { it.get() == null }
        }
    }

    // CallChain[size=21] = QExProps.get() <-[Call]- Any.qSetExProp() <-[Call]- N.children <-[Call]- N.d ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun get(key: Any): HashMap<String, Any?>? {
        removeGarbageCollectedEntries()

        return map[WeakKey(key)]
    }

    // CallChain[size=21] = QExProps.put() <-[Call]- Any.qSetExProp() <-[Call]- N.children <-[Call]- N.d ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun put(key: Any, value: HashMap<String, Any?>) {
        removeGarbageCollectedEntries()

        map[WeakKey(key)] = value
    }

    // CallChain[size=22] = QExProps.WeakKey <-[Call]- QExProps.put() <-[Call]- Any.qSetExProp() <-[Call ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    class WeakKey(key: Any) : WeakReference<Any>(key) {
        val hash = System.identityHashCode(key)

        override fun equals(other: Any?): Boolean {
            // If equals, hashCode() must be the same value
            // If both reference objects are null, then the keys are not equals
            val thisValue = this.get() ?: return false

            return thisValue === (other as WeakKey).get()
        }

        override fun hashCode() = hash
    }
}

// CallChain[size=20] = Any.qSetExProp() <-[Call]- N.children <-[Call]- N.depthFirstRecursive() <-[C ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun Any.qSetExProp(key: String, value: Any? = true) = synchronized(QExProps) {
    var props = QExProps.get(this)
    if (props == null) {
        props = HashMap(2)
        QExProps.put(this, props)
    }
    props[key] = value
}

// CallChain[size=21] = Any.qGetExPropOrDefault() <-[Call]- Any.qGetExPropOrNull() <-[Call]- N.child ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun <T> Any.qGetExPropOrDefault(key: String, default: T): T = synchronized(QExProps) {
    val props = QExProps.get(this) ?: return default

    return props.getOrDefault(key, default) as T
}

// CallChain[size=20] = Any.qGetExPropOrNull() <-[Call]- N.children <-[Call]- N.depthFirstRecursive( ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
// TODO remove this function, qGetExProp can already returns null
private fun Any.qGetExPropOrNull(key: String): Any? = synchronized(QExProps) {
    return qGetExPropOrDefault(key, null)
}

// CallChain[size=14] = qBUFFER_SIZE <-[Call]- Path.qReader() <-[Call]- Path.qFetchLinesAround() <-[ ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private const val qBUFFER_SIZE = DEFAULT_BUFFER_SIZE

// CallChain[size=14] = QOpenOpt <-[Ref]- Path.qReader() <-[Call]- Path.qFetchLinesAround() <-[Call] ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
// @formatter:off
private enum class QOpenOpt(val opt: OpenOption) : QFlagEnum<QOpenOpt> {
    // CallChain[size=16] = QOpenOpt.TRUNCATE_EXISTING <-[Propag]- QOpenOpt.QOpenOpt() <-[Call]- QFlag<Q ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    TRUNCATE_EXISTING(StandardOpenOption.TRUNCATE_EXISTING),
    // CallChain[size=16] = QOpenOpt.CREATE <-[Propag]- QOpenOpt.QOpenOpt() <-[Call]- QFlag<QOpenOpt>.to ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    CREATE(StandardOpenOption.CREATE),
    // CallChain[size=16] = QOpenOpt.CREATE_NEW <-[Propag]- QOpenOpt.QOpenOpt() <-[Call]- QFlag<QOpenOpt ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    CREATE_NEW(StandardOpenOption.CREATE_NEW),
    // CallChain[size=16] = QOpenOpt.WRITE <-[Propag]- QOpenOpt.QOpenOpt() <-[Call]- QFlag<QOpenOpt>.toO ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    WRITE(StandardOpenOption.WRITE),
    // CallChain[size=16] = QOpenOpt.READ <-[Propag]- QOpenOpt.QOpenOpt() <-[Call]- QFlag<QOpenOpt>.toOp ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    READ(StandardOpenOption.READ),
    // CallChain[size=16] = QOpenOpt.APPEND <-[Propag]- QOpenOpt.QOpenOpt() <-[Call]- QFlag<QOpenOpt>.to ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    APPEND(StandardOpenOption.APPEND),
    // CallChain[size=16] = QOpenOpt.DELETE_ON_CLOSE <-[Propag]- QOpenOpt.QOpenOpt() <-[Call]- QFlag<QOp ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    DELETE_ON_CLOSE(StandardOpenOption.DELETE_ON_CLOSE),
    // CallChain[size=16] = QOpenOpt.DSYNC <-[Propag]- QOpenOpt.QOpenOpt() <-[Call]- QFlag<QOpenOpt>.toO ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    DSYNC(StandardOpenOption.DSYNC),
    // CallChain[size=16] = QOpenOpt.SYNC <-[Propag]- QOpenOpt.QOpenOpt() <-[Call]- QFlag<QOpenOpt>.toOp ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    SYNC(StandardOpenOption.SYNC),
    // CallChain[size=16] = QOpenOpt.SPARSE <-[Propag]- QOpenOpt.QOpenOpt() <-[Call]- QFlag<QOpenOpt>.to ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    SPARSE(StandardOpenOption.SPARSE),
    // CallChain[size=16] = QOpenOpt.EX_DIRECT <-[Propag]- QOpenOpt.QOpenOpt() <-[Call]- QFlag<QOpenOpt> ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    EX_DIRECT(ExtendedOpenOption.DIRECT),
    // CallChain[size=16] = QOpenOpt.EX_NOSHARE_DELETE <-[Propag]- QOpenOpt.QOpenOpt() <-[Call]- QFlag<Q ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    EX_NOSHARE_DELETE(ExtendedOpenOption.NOSHARE_DELETE),
    // CallChain[size=16] = QOpenOpt.EX_NOSHARE_READ <-[Propag]- QOpenOpt.QOpenOpt() <-[Call]- QFlag<QOp ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    EX_NOSHARE_READ(ExtendedOpenOption.NOSHARE_READ),
    // CallChain[size=16] = QOpenOpt.EX_NOSHARE_WRITE <-[Propag]- QOpenOpt.QOpenOpt() <-[Call]- QFlag<QO ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    EX_NOSHARE_WRITE(ExtendedOpenOption.NOSHARE_WRITE),
    // CallChain[size=16] = QOpenOpt.LN_NOFOLLOW_LINKS <-[Propag]- QOpenOpt.QOpenOpt() <-[Call]- QFlag<Q ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    LN_NOFOLLOW_LINKS(LinkOption.NOFOLLOW_LINKS);

    companion object {
        
    }
}

// CallChain[size=14] = QFlag<QOpenOpt>.toOptEnums() <-[Call]- Path.qReader() <-[Call]- Path.qFetchL ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun QFlag<QOpenOpt>.toOptEnums(): Array<OpenOption> {
    return flagTrueValues().map { it.opt }.toTypedArray()
}

// CallChain[size=13] = Path.qLineSeparator() <-[Call]- Path.qFetchLinesAround() <-[Call]- qSrcFileL ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun Path.qLineSeparator(charset: Charset = Charsets.UTF_8): QLineSeparator {
    this.bufferedReader(charset).use { reader ->
        var ch: Char

        while (true) {
            ch = reader.read().toChar()

            if (ch == '\u0000') return QLineSeparator.DEFAULT

            if (ch == '\r') {
                val nextCh = reader.read().toChar()

                if (nextCh == '\u0000') return QLineSeparator.CR

                return if (nextCh == '\n') QLineSeparator.CRLF
                else QLineSeparator.CR
            } else if (ch == '\n') {
                return QLineSeparator.LF
            }
        }
    }
}

// CallChain[size=12] = QFetchEnd <-[Ref]- QFetchRule.SINGLE_LINE <-[Call]- QSrcCut.QSrcCut() <-[Cal ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private enum class QFetchEnd {
    // CallChain[size=13] = QFetchEnd.FETCH_THIS_LINE_AND_GO_TO_NEXT_LINE <-[Propag]- QFetchEnd.END_WITH ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    FETCH_THIS_LINE_AND_GO_TO_NEXT_LINE,
    // CallChain[size=12] = QFetchEnd.END_WITH_THIS_LINE <-[Call]- QFetchRule.SINGLE_LINE <-[Call]- QSrc ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    END_WITH_THIS_LINE,
    // CallChain[size=13] = QFetchEnd.END_WITH_NEXT_LINE <-[Propag]- QFetchEnd.END_WITH_THIS_LINE <-[Cal ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    END_WITH_NEXT_LINE,
    // CallChain[size=13] = QFetchEnd.END_WITH_PREVIOUS_LINE <-[Propag]- QFetchEnd.END_WITH_THIS_LINE <- ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    END_WITH_PREVIOUS_LINE
}

// CallChain[size=12] = QFetchStart <-[Ref]- QFetchRule.SINGLE_LINE <-[Call]- QSrcCut.QSrcCut() <-[C ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private enum class QFetchStart {
    // CallChain[size=12] = QFetchStart.FETCH_THIS_LINE_AND_GO_TO_PREVIOUS_LINE <-[Call]- QFetchRule.SIN ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    FETCH_THIS_LINE_AND_GO_TO_PREVIOUS_LINE,
    // CallChain[size=12] = QFetchStart.START_FROM_THIS_LINE <-[Call]- QFetchRule.SINGLE_LINE <-[Call]-  ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    START_FROM_THIS_LINE,
    // CallChain[size=13] = QFetchStart.START_FROM_NEXT_LINE <-[Propag]- QFetchStart.FETCH_THIS_LINE_AND ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    START_FROM_NEXT_LINE,
    // CallChain[size=13] = QFetchStart.START_FROM_PREVIOUS_LINE <-[Propag]- QFetchStart.FETCH_THIS_LINE ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    START_FROM_PREVIOUS_LINE
}

// CallChain[size=12] = QFetchRuleA <-[Call]- QFetchRule.SINGLE_LINE <-[Call]- QSrcCut.QSrcCut() <-[ ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private abstract class QFetchRuleA(
    override val numLinesBeforeTargetLine: Int = 10,
    override val numLinesAfterTargetLine: Int = 10,
) : QFetchRule

// CallChain[size=11] = QFetchRule <-[Ref]- QSrcCut.QSrcCut() <-[Call]- qLogStackFrames() <-[Call]-  ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private interface QFetchRule {
    // CallChain[size=12] = QFetchRule.numLinesBeforeTargetLine <-[Propag]- QFetchRule.SINGLE_LINE <-[Ca ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val numLinesBeforeTargetLine: Int
    // CallChain[size=12] = QFetchRule.numLinesAfterTargetLine <-[Propag]- QFetchRule.SINGLE_LINE <-[Cal ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val numLinesAfterTargetLine: Int

    // CallChain[size=12] = QFetchRule.fetchStartCheck() <-[Propag]- QFetchRule.SINGLE_LINE <-[Call]- QS ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun fetchStartCheck(
        line: String,
        currentLineNumber: Int,
        targetLine: String,
        targetLineNumber: Int,
        context: MutableSet<String>,
    ): QFetchStart

    // CallChain[size=12] = QFetchRule.fetchEndCheck() <-[Propag]- QFetchRule.SINGLE_LINE <-[Call]- QSrc ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun fetchEndCheck(
        line: String,
        currentLineNumber: Int,
        targetLine: String,
        targetLineNumber: Int,
        context: MutableSet<String>,
    ): QFetchEnd

    companion object {
        // CallChain[size=11] = QFetchRule.SINGLE_LINE <-[Call]- QSrcCut.QSrcCut() <-[Call]- qLogStackFrames ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        val SINGLE_LINE = object : QFetchRuleA(0, 0) {
            override fun fetchStartCheck(
                line: String,
                currentLineNumber: Int,
                targetLine: String,
                targetLineNumber: Int,
                context: MutableSet<String>,
            ): QFetchStart = if (currentLineNumber == targetLineNumber) {
                QFetchStart.START_FROM_THIS_LINE
            } else {
                QFetchStart.FETCH_THIS_LINE_AND_GO_TO_PREVIOUS_LINE
            }

            override fun fetchEndCheck(
                line: String,
                currentLineNumber: Int,
                targetLine: String,
                targetLineNumber: Int,
                context: MutableSet<String>,
            ): QFetchEnd = if (currentLineNumber == targetLineNumber) {
                QFetchEnd.END_WITH_THIS_LINE
            } else {
                qUnreachable()
            }
        }

        // CallChain[size=5] = QFetchRule.SMART_FETCH_INFIX <-[Call]- QSrcCut.MULTILINE_INFIX_NOCUT <-[Call]- qThrowIt() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        val SMART_FETCH_INFIX = object : QFetchRuleA(10, 10) {
            // """               <<< targetLine
            // some text
            // """ shouldBe """
            // some text
            // """

            override fun fetchStartCheck(
                line: String,
                currentLineNumber: Int,
                targetLine: String,
                targetLineNumber: Int,
                context: MutableSet<String>,
            ): QFetchStart {
                return QFetchStart.START_FROM_THIS_LINE
            }

            override fun fetchEndCheck(
                line: String,
                currentLineNumber: Int,
                targetLine: String,
                targetLineNumber: Int,
                context: MutableSet<String>,
            ): QFetchEnd = if (currentLineNumber >= targetLineNumber) {
                val nIndentThis = line.qCountLeftSpace()
                val nIndentTarget = targetLine.qCountLeftSpace()

                if (currentLineNumber == targetLineNumber && line.trimStart()
                        .startsWith("\"\"\"") && line.qCountOccurrence("\"\"\"") == 1
                ) {
                    // """
                    // some text
                    // """.log           <<< targetLine
                    QFetchEnd.FETCH_THIS_LINE_AND_GO_TO_NEXT_LINE
                } else if (line.qEndsWith(""".* should[a-zA-Z]+ ""${'"'}""".re)) { // TODO More accurately
                    QFetchEnd.FETCH_THIS_LINE_AND_GO_TO_NEXT_LINE
                } else if (line.endsWith("{") || line.endsWith("(") || line.endsWith(".")) {
                    QFetchEnd.FETCH_THIS_LINE_AND_GO_TO_NEXT_LINE
                } else if (nIndentThis == nIndentTarget) {
                    QFetchEnd.END_WITH_THIS_LINE
                } else {
                    QFetchEnd.FETCH_THIS_LINE_AND_GO_TO_NEXT_LINE
                }
            } else {
                QFetchEnd.FETCH_THIS_LINE_AND_GO_TO_NEXT_LINE
            }
        }

        // CallChain[size=10] = QFetchRule.SMART_FETCH <-[Call]- qLogStackFrames() <-[Call]- QException.mySr ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        val SMART_FETCH = object : QFetchRuleA(10, 10) {
            override fun fetchStartCheck(
                line: String,
                currentLineNumber: Int,
                targetLine: String,
                targetLineNumber: Int,
                context: MutableSet<String>,
            ): QFetchStart {
                val nIndentThis = line.qCountLeftSpace()
                val nIndentTarget = targetLine.qCountLeftSpace()
                val trimmed = line.trimStart()

                return if (arrayOf(
                        "\"\"\".",
                        "}",
                        ")",
                        ".",
                        ",",
                        "?",
                        "//",
                        "/*",
                        "*"
                    ).any { trimmed.startsWith(it) }
                ) {
                    QFetchStart.FETCH_THIS_LINE_AND_GO_TO_PREVIOUS_LINE
                } else if (nIndentThis <= nIndentTarget) {
                    QFetchStart.START_FROM_THIS_LINE
                } else {
                    QFetchStart.FETCH_THIS_LINE_AND_GO_TO_PREVIOUS_LINE
                }
            }

            override fun fetchEndCheck(
                line: String,
                currentLineNumber: Int,
                targetLine: String,
                targetLineNumber: Int,
                context: MutableSet<String>,
            ): QFetchEnd = if (currentLineNumber >= targetLineNumber) {
                val nIndentThis = line.qCountLeftSpace()
                val nIndentTarget = targetLine.qCountLeftSpace()

                if (currentLineNumber == targetLineNumber && line.trimStart()
                        .startsWith("\"\"\"") && line.qCountOccurrence("\"\"\"") == 1
                ) {
                    // """               <<< targetLine
                    // some text
                    // """ shouldBe """
                    // some text
                    // """

                    // """
                    // some text
                    // """.log           <<< targetLine
                    QFetchEnd.FETCH_THIS_LINE_AND_GO_TO_NEXT_LINE
                } else if (line.qEndsWith(""".* should[a-zA-Z]+ ""${'"'}""".re)) {
                    QFetchEnd.FETCH_THIS_LINE_AND_GO_TO_NEXT_LINE
                } else if (line.endsWith("{") || line.endsWith("(") || line.endsWith(".")) {
                    QFetchEnd.FETCH_THIS_LINE_AND_GO_TO_NEXT_LINE
                } else if (nIndentThis == nIndentTarget) {
                    QFetchEnd.END_WITH_THIS_LINE
                } else {
                    QFetchEnd.FETCH_THIS_LINE_AND_GO_TO_NEXT_LINE
                }
            } else {
                QFetchEnd.FETCH_THIS_LINE_AND_GO_TO_NEXT_LINE
            }
        }
    }
}

// CallChain[size=15] = LineNumberReader.qFetchLinesBetween() <-[Call]- LineNumberReader.qFetchTarge ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun LineNumberReader.qFetchLinesBetween(
    lineNumberStartInclusive: Int,
    lineNumberEndInclusive: Int,
): List<String> {
    var fetching = false
    val lines = mutableListOf<String>()

    while (true) {
        val n = this.lineNumber + 1
        val line = this.readLine() ?: break

        if (n == lineNumberStartInclusive) {
            fetching = true
            lines += line
        } else if (fetching) {
            lines += line

            if (n == lineNumberEndInclusive) {
                break
            }
        }
    }

    return lines
}

// CallChain[size=15] = TargetSurroundingLines <-[Ref]- LineNumberReader.qFetchTargetSurroundingLine ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private class TargetSurroundingLines(
    val targetLineNumber: Int,
    val startLineNumber: Int,
    val endLineNumber: Int,
    val targetLine: String,
    val linesBeforeTargetLine: List<String>,
    val linesAfterTargetLine: List<String>,
) {
    // CallChain[size=14] = TargetSurroundingLines.linesBetween() <-[Call]- LineNumberReader.qFetchLines ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun linesBetween(lineNumberStartInclusive: Int, lineNumberEndInclusive: Int): List<String> {
        val lines = mutableListOf<String>()

        lines += linesBeforeTargetLine
        lines += targetLine
        lines += linesAfterTargetLine

        val startIdx = lineNumberStartInclusive - startLineNumber
        val endIdx = lineNumberEndInclusive - startLineNumber

        return lines.subList(startIdx, endIdx + 1)
    }
}

// CallChain[size=14] = LineNumberReader.qFetchTargetSurroundingLines() <-[Call]- LineNumberReader.q ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun LineNumberReader.qFetchTargetSurroundingLines(
    targetLineNumber: Int,
    numLinesBeforeTargetLine: Int = 10,
    numLinesAfterTargetLine: Int = 10,
): TargetSurroundingLines {
    val start = max(1, targetLineNumber - numLinesBeforeTargetLine)
    val end = targetLineNumber + numLinesAfterTargetLine

    val lines = qFetchLinesBetween(start, end)

    return TargetSurroundingLines(
        targetLineNumber = targetLineNumber,
        startLineNumber = start,
        endLineNumber = end,
        targetLine = lines[targetLineNumber - start],
        linesBeforeTargetLine = lines.subList(0, targetLineNumber - start),
        linesAfterTargetLine = lines.subList(targetLineNumber - start + 1, lines.size)
    )
}

// CallChain[size=13] = LineNumberReader.qFetchLinesAround() <-[Call]- Path.qFetchLinesAround() <-[C ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun LineNumberReader.qFetchLinesAround(
    file: Path,
    targetLineNumber: Int,
    targetLine: String,
    fetchRule: QFetchRule = QFetchRule.SMART_FETCH,
    lineSeparator: QLineSeparator = QLineSeparator.LF,
): String {
    val surroundingLines = qFetchTargetSurroundingLines(
        targetLineNumber,
        fetchRule.numLinesBeforeTargetLine,
        fetchRule.numLinesAfterTargetLine
    )
    val context: MutableSet<String> = mutableSetOf()

    var start: Int = -1
    var end: Int = -1

    val checkStartLines = mutableListOf<String>()
    checkStartLines += surroundingLines.linesBeforeTargetLine
    checkStartLines += targetLine

    val checkEndLines = mutableListOf<String>()
    checkEndLines += targetLine
    checkEndLines += surroundingLines.linesAfterTargetLine

    for ((i, line) in checkStartLines.asReversed().withIndex()) {
        val curLineNumber = targetLineNumber - i

        val check = fetchRule.fetchStartCheck(
            line,
            curLineNumber,
            targetLine,
            targetLineNumber,
            context
        )

        when (check) {
            QFetchStart.START_FROM_PREVIOUS_LINE -> {
                start = curLineNumber - 1
                break
            }

            QFetchStart.START_FROM_THIS_LINE -> {
                start = curLineNumber
                break
            }

            QFetchStart.START_FROM_NEXT_LINE -> {
                start = curLineNumber + 1
                break
            }

            QFetchStart.FETCH_THIS_LINE_AND_GO_TO_PREVIOUS_LINE -> {
            }
        }
    }

    if (start == -1) {
        start = max(0, targetLineNumber - fetchRule.numLinesBeforeTargetLine)
    }

    for ((i, line) in checkEndLines.withIndex()) {
        val curLineNumber = targetLineNumber + i

        val check = fetchRule.fetchEndCheck(
            line,
            curLineNumber,
            targetLine,
            targetLineNumber,
            context
        )

        when (check) {
            QFetchEnd.END_WITH_PREVIOUS_LINE -> {
                end = curLineNumber - 1
                break
            }

            QFetchEnd.END_WITH_THIS_LINE -> {
                end = curLineNumber
                break
            }

            QFetchEnd.END_WITH_NEXT_LINE -> {
                end = curLineNumber + 1
                break
            }

            QFetchEnd.FETCH_THIS_LINE_AND_GO_TO_NEXT_LINE -> {
            }
        }
    }

    if (end == -1) {
        end = targetLineNumber + fetchRule.numLinesAfterTargetLine
    }

    return try {
        surroundingLines.linesBetween(start, end).joinToString(lineSeparator.value)
    } catch (e: Exception) {
        QE.FetchLinesFail.throwItFile(file)
    }
}

// CallChain[size=13] = Path.qReader() <-[Call]- Path.qFetchLinesAround() <-[Call]- qSrcFileLinesAtF ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun Path.qReader(
    charset: Charset = Charsets.UTF_8,
    buffSize: Int = qBUFFER_SIZE,
    opts: QFlag<QOpenOpt> = QFlag.none(),
): LineNumberReader {
    return LineNumberReader(reader(charset, *opts.toOptEnums()), buffSize)
}

// CallChain[size=12] = Path.qFetchLinesAround() <-[Call]- qSrcFileLinesAtFrame() <-[Call]- qMySrcLi ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun Path.qFetchLinesAround(
    lineNumber: Int,
    fetchRule: QFetchRule = QFetchRule.SMART_FETCH,
    charset: Charset = Charsets.UTF_8,
    lineSeparator: QLineSeparator = this.qLineSeparator(charset),
): String {
    val reader = qReader(charset)

    try {
        // TODO optimization
        val targetLine = qLineAt(lineNumber, charset)

        if (fetchRule == QFetchRule.SINGLE_LINE) return targetLine

        val fetchedLines = reader.use {
            it.qFetchLinesAround(this, lineNumber, targetLine, fetchRule, lineSeparator)
        }

        return fetchedLines
    } catch (e: Exception) {
        QE.FetchLinesFail.throwItBrackets("File", this, "LineNumber", lineNumber, e = e)
    }
}

// CallChain[size=13] = Path.qLineAt() <-[Call]- Path.qFetchLinesAround() <-[Call]- qSrcFileLinesAtF ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun Path.qLineAt(
    lineNumber: Int,
    charset: Charset = Charsets.UTF_8,
): String {
    bufferedReader(charset).use { reader ->
        var n = 0
        var line: String? = reader.readLine()

        while (line != null) {
            n++

            if (n == lineNumber) {
                return line
            }

            line = reader.readLine()
        }

        QE.LineNumberExceedsMaximum.throwItBrackets("File", this.absolutePathString(), "TargetLineNumber", lineNumber)
    }
}

// CallChain[size=14] = QFType <-[Ref]- Collection<Path>.qFind() <-[Call]- qSrcFileAtFrame() <-[Call ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private enum class QFType {
    // CallChain[size=18] = QFType.Any <-[Call]- QFType.matches() <-[Call]- Path.qSeq() <-[Call]- Path.q ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    Any,
    // CallChain[size=14] = QFType.File <-[Call]- Collection<Path>.qFind() <-[Call]- qSrcFileAtFrame() < ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    File,
    // CallChain[size=18] = QFType.Dir <-[Call]- QFType.matches() <-[Call]- Path.qSeq() <-[Call]- Path.q ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    Dir,
    // CallChain[size=18] = QFType.SymLink <-[Call]- QFType.matches() <-[Call]- Path.qSeq() <-[Call]- Pa ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    SymLink,
    // CallChain[size=18] = QFType.FileOrDir <-[Call]- QFType.matches() <-[Call]- Path.qSeq() <-[Call]-  ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    FileOrDir;

    // CallChain[size=17] = QFType.matches() <-[Call]- Path.qSeq() <-[Call]- Path.qList() <-[Call]- Path ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun matches(path: Path?, followSymLink: Boolean = true): Boolean {
        if (path == null) return false

        return when (this) {
            Any -> true
            File -> if (followSymLink) path.isRegularFile() else path.isRegularFile(LinkOption.NOFOLLOW_LINKS)
            Dir -> if (followSymLink) path.isDirectory() else path.isDirectory(LinkOption.NOFOLLOW_LINKS)
            FileOrDir -> return if (followSymLink) {
                path.isRegularFile() || path.isDirectory()
            } else {
                path.isRegularFile(LinkOption.NOFOLLOW_LINKS) || path.isDirectory(LinkOption.NOFOLLOW_LINKS)
            }

            SymLink -> return path.isSymbolicLink()
        }
    }
}

// CallChain[size=19] = Path.qFileSizeStr() <-[Call]- QFilePathNode.toTreeNodeString() <-[Propag]- Q ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun Path.qFileSizeStr() = fileSize().qToSizeStr()

// CallChain[size=19] = Path.qDirSizeStr() <-[Call]- QFilePathNode.toTreeNodeString() <-[Propag]- QF ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun Path.qDirSizeStr(
    followSymLink: Boolean = false,
    maxDepth: Int = Int.MAX_VALUE,
    dirFilter: (Path) -> Boolean = { true },
    fileFilter: (Path) -> Boolean = { true }
): String {
    return this.qSeq(
        QFType.Any,
        maxDepth = maxDepth,
        followSymLink = followSymLink,
        dirFilter = dirFilter,
        fileFilter = fileFilter
    ).sumOf {
        try {
            it.fileSize()
        } catch (e: Exception) {
            0
        }
    }.qToSizeStr()
}

// CallChain[size=20] = Long.qToSizeStr() <-[Call]- Path.qDirSizeStr() <-[Call]- QFilePathNode.toTre ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun Long.qToSizeStr(): String {
    val size = this
    if (size <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
    return DecimalFormat("#,##0.#").format(size / 1024.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
}

// CallChain[size=13] = Collection<Path>.qFind() <-[Call]- qSrcFileAtFrame() <-[Call]- qSrcFileLines ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun Collection<Path>.qFind(nameMatcher: QM, type: QFType = QFType.File, maxDepth: Int = 1): Path? {
    for (path in this) {
        val found = path.qFind(nameMatcher, type, maxDepth)
        if (found != null) return found
    }

    return null
}

// CallChain[size=14] = Path.qFind() <-[Call]- Collection<Path>.qFind() <-[Call]- qSrcFileAtFrame()  ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun Path.qFind(nameMatcher: QM, type: QFType = QFType.File, maxDepth: Int = 100): Path? {
    return try {
        qList(type, maxDepth = maxDepth) {
            it.name.qMatches(nameMatcher)
        }.firstOrNull()
    } catch (e: NoSuchElementException) {
        null
    }
}

// CallChain[size=11] = Path.qListByMatch() <-[Call]- QMyPath.src_root <-[Call]- qLogStackFrames() < ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun Path.qListByMatch(
    nameMatch: QM,
    type: QFType = QFType.File,
    maxDepth: Int = 1,
    followSymLink: Boolean = false,
): List<Path> {
    return qList(
        type, maxDepth = maxDepth, followSymLink = followSymLink
    ) {
        it.name.qMatches(nameMatch)
    }
}

// CallChain[size=15] = Path.qList() <-[Call]- Path.qFind() <-[Call]- Collection<Path>.qFind() <-[Ca ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun Path.qList(
    type: QFType = QFType.File,
    maxDepth: Int = 1,
    followSymLink: Boolean = false,
    sortWith: ((Path, Path) -> Int)? = Path::compareTo,
    dirFilter: (Path) -> Boolean = { true },
    fileFilter: (Path) -> Boolean = { true },
    // TODO https://stackoverflow.com/a/66996768/5570400
    // errorContinue: Boolean = true
): List<Path> {
    return qSeq(
        type = type,
        maxDepth = maxDepth,
        followSymLink = followSymLink,
        sortWith = sortWith,
        dirFilter = dirFilter,
        fileFilter = fileFilter,
    ).toList()
}

// CallChain[size=16] = Path.qSeq() <-[Call]- Path.qList() <-[Call]- Path.qFind() <-[Call]- Collecti ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun Path.qSeq(
    type: QFType = QFType.File,
    maxDepth: Int = 1,
    followSymLink: Boolean = false,
    sortWith: ((Path, Path) -> Int)? = Path::compareTo,
    dirFilter: (Path) -> Boolean = { true },
    fileFilter: (Path) -> Boolean = { true },
    // TODO https://stackoverflow.com/a/66996768/5570400
    // errorContinue: Boolean = true
): Sequence<Path> {
    if (!this.isDirectory())
        return emptySequence()

    val seq = QFilePathNode(this).walkDescendants(
        algorithm = QWalkAlgo.BreadthFirst,
        maxDepth = maxDepth,
        includeSelf = false,
        filter = { node ->
            if (node.value.isDirectory()) {
                dirFilter(node.value)
            } else {
                fileFilter(node.value)
            }
        }
    ).map { it.value }.filter {
        type.matches(it, followSymLink)
    }


//    val fvOpt = if (followSymLink) arrayOf(FileVisitOption.FOLLOW_LINKS) else arrayOf()
//    val seq = Files.walk(this, maxDepth, *fvOpt).asSequence().filter {
//        if (it == this) return@filter false
//
//        type.matches(it, followSymLink) && fileFilter(it)
//    }

    return if (sortWith != null) {
        seq.sortedWith(sortWith)
    } else {
        seq
    }
}

// CallChain[size=17] = QFilePathNode <-[Call]- Path.qSeq() <-[Call]- Path.qList() <-[Call]- Path.qF ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private class QFilePathNode(override val value: Path) : QLazyTreeNode<Path> {
    // CallChain[size=18] = QFilePathNode.hasChildNodesToFill() <-[Propag]- QFilePathNode.QFilePathNode( ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun hasChildNodesToFill(): Boolean {
        return value.isDirectory()
    }

    // CallChain[size=18] = QFilePathNode.fillChildNodes() <-[Propag]- QFilePathNode.QFilePathNode() <-[ ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun fillChildNodes(): List<QFilePathNode> {
        return Files.list(value).map { QFilePathNode(it) }.toList()
//        return value.qList(QFType.Any, maxDepth = 1).map {
//            QFilePathNode(it)
//        }
    }

    // CallChain[size=18] = QFilePathNode.toTreeNodeString() <-[Propag]- QFilePathNode.QFilePathNode() < ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun toTreeNodeString(): String {
        return value.name + if (value.isRegularFile()) " ${value.qFileSizeStr().green}" else " " + value.qDirSizeStr().purple
    }
}

// CallChain[size=14] = QFlag <-[Ref]- Path.qReader() <-[Call]- Path.qFetchLinesAround() <-[Call]- q ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
/**
 * Only Enum or QFlagMut can implement this interface.
 */
private sealed interface QFlag<T> where T : QFlag<T>, T : Enum<T> {
    // CallChain[size=16] = QFlag.bits <-[Propag]- QFlag.flagTrueValues() <-[Call]- QFlag<QOpenOpt>.toOp ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val bits: Int

//    fun contains(flags: T): Boolean {
//        return (bits and flags.bits) == flags.bits
//    }

    // CallChain[size=16] = QFlag.notContains() <-[Propag]- QFlag.flagTrueValues() <-[Call]- QFlag<QOpen ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun notContains(flags: QFlag<T>): Boolean {
        return !contains(flags)
    }

    // CallChain[size=16] = QFlag.contains() <-[Propag]- QFlag.flagTrueValues() <-[Call]- QFlag<QOpenOpt ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun contains(flags: QFlag<T>): Boolean {
        return (bits and flags.bits) == flags.bits
    }

    // CallChain[size=16] = QFlag.containsAny() <-[Propag]- QFlag.flagTrueValues() <-[Call]- QFlag<QOpen ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun containsAny(vararg flags: QFlag<T>): Boolean {
        return flags.any { flag -> (bits and flag.bits) == flag.bits }
    }

    // CallChain[size=16] = QFlag.notContainsAny() <-[Propag]- QFlag.flagTrueValues() <-[Call]- QFlag<QO ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun notContainsAny(vararg flags: QFlag<T>): Boolean {
        return !containsAny(*flags)
    }

    // CallChain[size=16] = QFlag.containsAll() <-[Propag]- QFlag.flagTrueValues() <-[Call]- QFlag<QOpen ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun containsAll(vararg flags: QFlag<T>): Boolean {
        return flags.all { flag -> (bits and flag.bits) == flag.bits }
    }

//    fun containsAll(flags: Array<QFlag<T>>): Boolean {
//        return flags.all { flag -> (bits and flag.bits) == flag.bits }
//    }

//    fun matches(flags: T): Boolean {
//        return bits == flags.bits
//    }

    // CallChain[size=16] = QFlag.matches() <-[Propag]- QFlag.flagTrueValues() <-[Call]- QFlag<QOpenOpt> ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun matches(flags: QFlag<T>): Boolean {
        return bits == flags.bits
    }

    // CallChain[size=16] = QFlag.matchesAll() <-[Propag]- QFlag.flagTrueValues() <-[Call]- QFlag<QOpenO ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun matchesAll(vararg flags: QFlag<T>): Boolean {
        return flags.all { flag -> bits == flag.bits }
    }

//    fun matchesAll(flags: Array<QFlag<T>>): Boolean {
//        return flags.all { flag -> bits == flag.bits }
//    }

    // CallChain[size=15] = QFlag.flagTrueValues() <-[Call]- QFlag<QOpenOpt>.toOptEnums() <-[Call]- Path ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun flagTrueValues(): List<T>

    // CallChain[size=16] = QFlag.str() <-[Propag]- QFlag.flagTrueValues() <-[Call]- QFlag<QOpenOpt>.toO ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun str(): String {
        return flagTrueValues().joinToString(", ") { it.name }
    }

    // CallChain[size=16] = QFlag.copy() <-[Propag]- QFlag.flagTrueValues() <-[Call]- QFlag<QOpenOpt>.to ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun copy(): QFlagMut<T>


    // CallChain[size=16] = QFlag.isNone() <-[Propag]- QFlag.flagTrueValues() <-[Call]- QFlag<QOpenOpt>. ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun isNone(): Boolean {
        return bits == 0
    }

    // CallChain[size=16] = QFlag.isNotNone() <-[Propag]- QFlag.flagTrueValues() <-[Call]- QFlag<QOpenOp ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun isNotNone(): Boolean {
        return bits != 0
    }

    companion object {
        // https://discuss.kotlinlang.org/t/reified-generics-on-class-level/16711/2
        // But, can't make constructor private ...

        // CallChain[size=14] = QFlag.none() <-[Call]- Path.qReader() <-[Call]- Path.qFetchLinesAround() <-[ ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        inline fun <reified T> none(): QFlagMut<T> where T : QFlag<T>, T : Enum<T> {
            return QFlagMut<T>(T::class, 0)
        }
    }
}

// CallChain[size=15] = QFlagEnum <-[Ref]- QOpenOpt <-[Ref]- Path.qReader() <-[Call]- Path.qFetchLin ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private interface QFlagEnum<T> : QFlag<T> where T : QFlag<T>, T : Enum<T> {
    // CallChain[size=16] = QFlagEnum.bits <-[Propag]- QFlagEnum <-[Ref]- QOpenOpt <-[Ref]- Path.qReader ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override val bits: Int
        get() = 1 shl (this as T).ordinal

    // CallChain[size=16] = QFlagEnum.flagTrueValues() <-[Propag]- QFlagEnum <-[Ref]- QOpenOpt <-[Ref]-  ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun flagTrueValues(): List<T> = listOf(this) as List<T>

    // CallChain[size=16] = QFlagEnum.copy() <-[Propag]- QFlagEnum <-[Ref]- QOpenOpt <-[Ref]- Path.qRead ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun copy(): QFlagMut<T> {
        return QFlagMut(this::class as KClass<T>, bits)
    }
}

// CallChain[size=17] = QFlagMut <-[Ref]- QFlag.copy() <-[Propag]- QFlag.flagTrueValues() <-[Call]-  ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
/**
 * Mutable bit flag
 */
private open class QFlagMut<T>(val enumClass: KClass<T>, override var bits: Int) : QFlag<T> where T : QFlag<T>, T : Enum<T> {
    // CallChain[size=18] = QFlagMut.allEnumValues <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Propag] ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val allEnumValues: Array<T> by lazy { enumClass.qEnumValues() }

    // CallChain[size=18] = QFlagMut.QFlagMut() <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Propag]- Q ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    constructor(enumClass: KClass<T>, vararg flags: T) : this(
        enumClass,
        bits = if (flags.isEmpty()) 0 else flags.map { it.bits }.reduce { a, b -> a or b }
    )

    // CallChain[size=18] = QFlagMut.add() <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Propag]- QFlag. ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun add(flag: T) {
        add(flag.bits)
    }

    // CallChain[size=18] = QFlagMut.add() <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Propag]- QFlag. ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun add(flagBits: Int) {
        bits = bits or flagBits
    }

//    fun addAll(flags: Array<T>) {
//        for (fl in flags) {
//            add(fl.bits)
//        }
//    }

    // CallChain[size=18] = QFlagMut.addAll() <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Propag]- QFl ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun addAll(vararg flags: T) {
        for(fl in flags) {
            add(fl.bits)
        }
    }

    // CallChain[size=18] = QFlagMut.addAll() <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Propag]- QFl ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun addAll(flags: Iterable<T>) {
        for (fl in flags) {
            add(fl.bits)
        }
    }

    // CallChain[size=18] = QFlagMut.allTrue() <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Propag]- QF ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun allTrue() {
        this.addAll(*allEnumValues)
    }

    // CallChain[size=18] = QFlagMut.allFalse() <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Propag]- Q ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun allFalse() {
        this.bits = 0
    }

    // CallChain[size=18] = QFlagMut.removeAll() <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Propag]-  ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun removeAll(vararg flags: T) {
        for(fl in flags) {
            remove(fl.bits)
        }
    }

//    fun removeAll(flags: Array<T>) {
//        for(fl in flags) {
//            remove(fl.bits)
//        }
//    }

    // CallChain[size=18] = QFlagMut.removeAll() <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Propag]-  ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun removeAll(flags: Iterable<T>) {
        for(fl in flags) {
            remove(fl.bits)
        }
    }

    // CallChain[size=18] = QFlagMut.remove() <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Propag]- QFl ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun remove(flag: T) {
        remove(flag.bits)
    }

    // CallChain[size=18] = QFlagMut.remove() <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Propag]- QFl ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun remove(flagBits: Int) {
        bits = bits and flagBits.inv()
    }

    // CallChain[size=18] = QFlagMut.eq() <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Propag]- QFlag.f ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    infix fun eq(other: QFlag<T>) = this.bits == other.bits

    // CallChain[size=18] = QFlagMut.print() <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Propag]- QFla ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun print(out: QOut = QOut.CONSOLE, end: String = "\n") {
        out.print(str() + end)
    }

    // CallChain[size=18] = QFlagMut.flagTrueValues() <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Prop ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun flagTrueValues(): List<T> =
        allEnumValues.filter { contains(it) }

    // CallChain[size=18] = QFlagMut.copy() <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Propag]- QFlag ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun copy(): QFlagMut<T> {
        return QFlagMut(enumClass, bits)
    }

    // CallChain[size=18] = QFlagMut.toString() <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Propag]- Q ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun toString(): String {
        return flagTrueValues().joinToString(", ")
    }
}

// CallChain[size=7] = QUnit <-[Ref]- Long.qFormatDuration() <-[Call]- QTestResult.str <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private enum class QUnit {
    // CallChain[size=7] = QUnit.Nano <-[Call]- Long.qFormatDuration() <-[Call]- QTestResult.str <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    Nano,
    // CallChain[size=7] = QUnit.Micro <-[Call]- Long.qFormatDuration() <-[Call]- QTestResult.str <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    Micro,
    // CallChain[size=7] = QUnit.Milli <-[Call]- Long.qFormatDuration() <-[Call]- QTestResult.str <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    Milli,
    // CallChain[size=7] = QUnit.Second <-[Call]- Long.qFormatDuration() <-[Call]- QTestResult.str <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    Second,
    // CallChain[size=7] = QUnit.Minute <-[Call]- Long.qFormatDuration() <-[Call]- QTestResult.str <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    Minute,
    // CallChain[size=7] = QUnit.Hour <-[Call]- Long.qFormatDuration() <-[Call]- QTestResult.str <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    Hour,
    // CallChain[size=7] = QUnit.Day <-[Call]- Long.qFormatDuration() <-[Call]- QTestResult.str <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    Day
}

// CallChain[size=6] = Long.qFormatDuration() <-[Call]- QTestResult.str <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun Long.qFormatDuration(unit: QUnit = QUnit.Nano): String {
    return when (unit) {
        QUnit.Milli ->
            Duration.ofMillis(this).qFormat()
        QUnit.Micro ->
            Duration.ofNanos(this * 1000).qFormat()
        QUnit.Nano ->
            Duration.ofNanos(this).qFormat()
        QUnit.Second ->
            Duration.ofSeconds(this).qFormat()
        QUnit.Minute ->
            Duration.ofMinutes(this).qFormat()
        QUnit.Hour ->
            Duration.ofHours(this).qFormat()
        QUnit.Day ->
            Duration.ofDays(this).qFormat()
    }
}

// CallChain[size=8] = Duration.qToMicrosOnlyPart() <-[Call]- Duration.qFormat() <-[Call]- Long.qFor ...  <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun Duration.qToMicrosOnlyPart(): Int {
    return (toNanosPart() % 1_000_000) / 1_000
}

// CallChain[size=8] = Duration.qToNanoOnlyPart() <-[Call]- Duration.qFormat() <-[Call]- Long.qForma ...  <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun Duration.qToNanoOnlyPart(): Int {
    return toNanosPart() % 1_000
}

// CallChain[size=7] = Duration.qFormat() <-[Call]- Long.qFormatDuration() <-[Call]- QTestResult.str <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun Duration.qFormat(detail: Boolean = false): String {
    if(this.isZero) {
        return "0"
    }

    val du = abs()

    val maxUnit: QUnit = du.let {
        when {
            it < Duration.ofNanos(1000) -> QUnit.Nano
            it < Duration.ofMillis(1) -> QUnit.Micro
            it < Duration.ofSeconds(1) -> QUnit.Milli
            it < Duration.ofMinutes(1) -> QUnit.Second
            it < Duration.ofHours(1) -> QUnit.Minute
            it < Duration.ofDays(1) -> QUnit.Hour
            else -> QUnit.Day
        }
    }

    val parts = mutableListOf<String>()
    when (maxUnit) {
        QUnit.Nano -> {
            parts.add(String.format("%3d ns", du.toNanosPart()))
        }
        QUnit.Micro -> {
            parts.add(String.format("%3d μs", du.qToMicrosOnlyPart()))

            if (du.qToMicrosOnlyPart() <= 3 || detail)
                parts.add(String.format("%03d ns", du.qToNanoOnlyPart()))
        }
        QUnit.Milli -> {
            parts.add(String.format("%3d ms", du.toMillisPart()))

            if (du.toMillisPart() <= 3 || detail)
                parts.add(String.format("%03d μs", du.qToMicrosOnlyPart()))
        }
        QUnit.Second -> {
            parts.add(String.format("%2d sec", du.toSecondsPart()))
            parts.add(String.format("%03d ms", du.toMillisPart()))

            if (detail) {
                parts.add(String.format("%03d μs", du.qToMicrosOnlyPart()))
                parts.add(String.format("%03d ns", du.qToNanoOnlyPart()))
            }
        }
        QUnit.Minute -> {
            parts.add(String.format("%2d min", du.toMinutesPart()))
            parts.add(String.format("%02d sec", du.toSecondsPart()))
            if (detail) {
                parts.add(String.format("%03d ms", du.toMillisPart()))
                parts.add(String.format("%03d μs", du.qToMicrosOnlyPart()))
                parts.add(String.format("%03d ns", du.qToNanoOnlyPart()))
            }
        }
        QUnit.Hour -> {
            parts.add(String.format("%2d hour", du.toHoursPart()))
            parts.add(String.format("%02d min", du.toMinutesPart()))
            if (detail) {
                parts.add(String.format("%02d sec", du.toSecondsPart()))
                parts.add(String.format("%03d ms", du.toMillisPart()))
                parts.add(String.format("%03d μs", du.qToMicrosOnlyPart()))
                parts.add(String.format("%03d ns", du.qToNanoOnlyPart()))
            }
        }
        QUnit.Day -> {
            parts.add(String.format("%2d day", du.toDaysPart()))
            parts.add(String.format("%02d hour", du.toHoursPart()))
            if (detail) {
                parts.add(String.format("%02d min", du.toMinutesPart()))
                parts.add(String.format("%02d sec", du.toSecondsPart()))
                parts.add(String.format("%03d ms", du.toMillisPart()))
                parts.add(String.format("%03d μs", du.qToMicrosOnlyPart()))
                parts.add(String.format("%03d ns", du.qToNanoOnlyPart()))
            }
        }
    }

    return parts.joinToString(" ")
}

// CallChain[size=13] = qARROW <-[Call]- qArrow() <-[Call]- QLogStyle.qLogArrow() <-[Call]- QLogStyl ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private val qARROW = "===>".light_cyan

// CallChain[size=4] = qIsDebugging <-[Call]- qOkToTest() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
// https://stackoverflow.com/a/28754689/5570400
private val qIsDebugging by lazy {
    java.lang.management.ManagementFactory.getRuntimeMXBean().inputArguments.toString().indexOf("jdwp") >= 0
}

// CallChain[size=4] = qIsTesting <-[Call]- qOkToTest() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
// https://stackoverflow.com/a/12717377/5570400
private val qIsTesting by lazy {
    qStackFrames(size = Int.MAX_VALUE).any {
        it.methodName.equals("qTest") ||
                it.className.startsWith("org.junit.") || it.className.startsWith("org.testng.")
    }
}

// CallChain[size=7] = QSrcCut <-[Ref]- QException.QException() <-[Ref]- QE.throwItBrackets() <-[Cal ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private class QSrcCut(
    val fetchRule: QFetchRule = QFetchRule.SINGLE_LINE,
    val cut: (srcLines: String) -> String,
) {
    companion object {
        // CallChain[size=11] = QSrcCut.CUT_PARAM_qLog <-[Call]- QSrcCut.SINGLE_qLog_PARAM <-[Call]- qLogSta ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        private val CUT_PARAM_qLog = { mySrc: String ->
            mySrc.replaceFirst("""(?s)^\s*(\S.+)\.qLog[a-zA-Z]{0,10}.*$""".re, "$1")
        }

        // CallChain[size=10] = QSrcCut.SINGLE_qLog_PARAM <-[Call]- qLogStackFrames() <-[Call]- QException.m ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        val SINGLE_qLog_PARAM = QSrcCut(QFetchRule.SINGLE_LINE, CUT_PARAM_qLog)
        // CallChain[size=7] = QSrcCut.MULTILINE_NOCUT <-[Call]- QException.QException() <-[Ref]- QE.throwIt ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        val MULTILINE_NOCUT = QSrcCut(QFetchRule.SMART_FETCH) { it }
        // CallChain[size=4] = QSrcCut.MULTILINE_INFIX_NOCUT <-[Call]- qThrowIt() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        val MULTILINE_INFIX_NOCUT = QSrcCut(QFetchRule.SMART_FETCH_INFIX) { it }
        // CallChain[size=11] = QSrcCut.NOCUT_JUST_SINGLE_LINE <-[Call]- qMySrcLinesAtFrame() <-[Call]- qLog ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        val NOCUT_JUST_SINGLE_LINE = QSrcCut(QFetchRule.SINGLE_LINE) { it }
        
    }
}

// CallChain[size=10] = QLogStyle <-[Ref]- QLogStyle.SRC_AND_STACK <-[Call]- QException.mySrcAndStac ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private class QLogStyle(
    val stackSize: Int,
    val out: QOut = QMyLog.out,
    val start: String = "----".cyan + "\n",
    val end: String = "\n\n",
    val stackReverseOrder: Boolean = false,
    val template: (msg: String, mySrc: String, now: Long, stackTrace: String) -> String,
) {
    @Suppress("UNUSED_PARAMETER")
    companion object {
        // CallChain[size=10] = QLogStyle.String.clarifySrcRegion() <-[Call]- QLogStyle.SRC_AND_STACK <-[Cal ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        fun String.clarifySrcRegion(onlyIf: QOnlyIfStr = QOnlyIfStr.Multiline): String {
            if (!onlyIf.matches(this))
                return this

            return """${"SRC START ―――――――――――".qColor(QColor.Cyan)}
${this.trim()}
${"SRC END   ―――――――――――".qColor(QColor.Cyan)}"""
        }

        // CallChain[size=11] = QLogStyle.qLogArrow() <-[Call]- QLogStyle.SRC_AND_MSG <-[Call]- qLogStackFra ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        fun qLogArrow(mySrc: String, msg: String): String {
            return if (mySrc.startsWith("\"") && mySrc.endsWith("\"") && mySrc.substring(1, mySrc.length - 1)
                    .matches("""[\w\s]+""".re)
            ) {
                // src code is simple string
                "\"".light_green + msg + "\"".light_green
//                ("\"" + msg + "\"").light_green
            } else {
                qArrow(mySrc.clarifySrcRegion(QOnlyIfStr.Multiline), msg)
            }
        }

        // CallChain[size=9] = QLogStyle.SRC_AND_STACK <-[Call]- QException.mySrcAndStack <-[Call]- QExcepti ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        val SRC_AND_STACK: QLogStyle
            get() = QLogStyle(1) { _, mySrc, _, stackTrace ->
                """
${mySrc.clarifySrcRegion(QOnlyIfStr.Always)}
$stackTrace""".trim()
            }

        // CallChain[size=10] = QLogStyle.SRC_AND_MSG <-[Call]- qLogStackFrames() <-[Call]- QException.mySrc ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        val SRC_AND_MSG: QLogStyle
            get() = QLogStyle(0) { msg, mySrc, _, _ ->
                """
${qLogArrow(mySrc, msg)}
""".trim()
            }

        // CallChain[size=3] = QLogStyle.MSG_AND_STACK <-[Call]- qTest() <-[Call]- main()[Root]
        val MSG_AND_STACK: QLogStyle
            get() = QLogStyle(1, start = "") { msg, _, _, stackTrace ->
                """
$msg
$stackTrace
""".trim()
            }

        // CallChain[size=10] = QLogStyle.S <-[Call]- qLogStackFrames() <-[Call]- QException.mySrcAndStack < ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        val S: QLogStyle
            get() = QLogStyle(1) { msg, mySrc, _, stackTrace ->
                """
${qLogArrow(mySrc, msg)}
$stackTrace
""".trim()
            }

        
    }
}

// CallChain[size=10] = qMySrcLinesAtFrame() <-[Call]- qLogStackFrames() <-[Call]- QException.mySrcA ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun qMySrcLinesAtFrame(
    frame: StackFrame,
    srcCut: QSrcCut = QSrcCut.NOCUT_JUST_SINGLE_LINE,
    srcRoots: List<Path> = QMyPath.src_root,
    srcCharset: Charset = Charsets.UTF_8,
): String {
    return try {
        val src = qCacheItOneSec("${frame.fileName} - ${frame.lineNumber} - ${srcCut.fetchRule.hashCode()}") {
            qSrcFileLinesAtFrame(
                srcRoots = srcRoots, charset = srcCharset, fetchRule = srcCut.fetchRule, frame = frame
            )
        }

        val src2 = srcCut.cut(src).trimIndent()
        src2
    } catch (e: Exception) {
//        e.message
        "${QMyMark.warn} Couldn't cut src lines : ${
            qBrackets(
                "FileName",
                frame.fileName,
                "LineNo",
                frame.lineNumber,
                "SrcRoots",
                srcRoots
            )
        }"
    }
}

// CallChain[size=9] = qLogStackFrames() <-[Call]- QException.mySrcAndStack <-[Call]- QException.pri ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun qLogStackFrames(
    frames: List<StackFrame>,
    msg: Any? = "",
    style: QLogStyle = QLogStyle.S,
    srcRoots: List<Path> = QMyPath.src_root,
    srcCharset: Charset = Charsets.UTF_8,
    srcCut: QSrcCut = QSrcCut.SINGLE_qLog_PARAM,
    quiet: Boolean = false,
    noColor: Boolean = false,
): String {

    val sty = if( QMyLog.no_stacktrace_mode ) {
        QLogStyle.SRC_AND_MSG
    } else {
        style
    }

    var mySrc = qMySrcLinesAtFrame(frame = frames[0], srcCut = srcCut, srcRoots = srcRoots, srcCharset = srcCharset)

    if (mySrc.trimStart().startsWith("}.") || mySrc.trimStart().startsWith(").") || mySrc.trimStart()
            .startsWith("\"\"\"")
    ) {
        // Maybe you want to check multiple lines

        val multilineCut = QSrcCut(QFetchRule.SMART_FETCH, srcCut.cut)

        mySrc = qMySrcLinesAtFrame(
            frame = frames[0], srcCut = multilineCut, srcRoots = srcRoots, srcCharset = srcCharset
        )
    }

    val stackTrace = if (sty.stackReverseOrder) {
        frames.reversed().joinToString("\n") { it.toString() }
    } else {
        frames.joinToString("\n") { it.toString() }
    }

    val output = sty.template(
        msg.qToLogString(), mySrc, qNow, stackTrace
    )

    val text = sty.start + output + sty.end

    val finalTxt = if (noColor) text.noStyle else text

    if (!quiet)
        sty.out.print(finalTxt)

    return if (noColor) output.noStyle else output
}

// CallChain[size=11] = qSrcFileLinesAtFrame() <-[Call]- qMySrcLinesAtFrame() <-[Call]- qLogStackFra ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun qSrcFileLinesAtFrame(
    srcRoots: List<Path> = QMyPath.src_root,
    pkgDirHint: String? = null,
    charset: Charset = Charsets.UTF_8,
    lineSeparator: QLineSeparator = QLineSeparator.LF,
    fetchRule: QFetchRule = QFetchRule.SMART_FETCH,
    frame: StackFrame = qStackFrame(2),
): String {
    val srcFile: Path = qSrcFileAtFrame(frame = frame, srcRoots = srcRoots, pkgDirHint = pkgDirHint)

    return srcFile.qFetchLinesAround(frame.lineNumber, fetchRule, charset, lineSeparator)
}

// CallChain[size=12] = qArrow() <-[Call]- QLogStyle.qLogArrow() <-[Call]- QLogStyle.SRC_AND_MSG <-[ ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun qArrow(key: Any?, value: Any?): String {
    val keyStr = key.qToLogString()
        .qWithNewLinePrefix(onlyIf = QOnlyIfStr.Multiline)
        .qWithNewLineSuffix(onlyIf = QOnlyIfStr.Always)

    val valStr = value.qToLogString().qWithNewLineSurround(onlyIf = QOnlyIfStr.Multiline)
        .qWithSpacePrefix(numSpace = 2, onlyIf = QOnlyIfStr.SingleLine)

    return "$keyStr$qARROW$valStr"
}

// CallChain[size=3] = qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun qBrackets(vararg keysAndValues: Any?): String {
    return qBracketsColored(fg = null, *keysAndValues)
}

// CallChain[size=5] = qBracketsBlue() <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun qBracketsBlue(vararg keysAndValues: Any?): String {
    return qBracketsColored(QColor.Blue, *keysAndValues)
}

// CallChain[size=6] = qBracketsCyan() <-[Call]- QE.throwItBrackets() <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun qBracketsCyan(vararg keysAndValues: Any?): String {
    return qBracketsColored(QColor.Cyan, *keysAndValues)
}

// CallChain[size=5] = QBracketKV <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private class QBracketKV(val key: String, val value: String, val fg: QColor?) {
    // CallChain[size=5] = QBracketKV.toString() <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun toString(): String {
        val keyStr = if (fg != null) "[$key]".qColor(fg) else "[$key]"

        val valueStr = value.qWithNewLinePrefix(onlyIf = QOnlyIfStr.MultilineOrVeryLongLine)

        return if (valueStr.qStartsWithNewLine()) {
            keyStr + valueStr
        } else {
            "$keyStr  $valueStr"
        }
    }

    companion object {
        
    }
}

// CallChain[size=4] = qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun qBracketsColored(fg: QColor? = null, vararg keysAndValues: Any?): String {
    if (keysAndValues.size % 2 != 0) {
        QE.ShouldBeEvenNumber.throwItBrackets("KeysAndValues.size", keysAndValues.size)
    }

    return keysAndValues.asSequence().withIndex().chunked(2) { (key, value) ->
        QBracketKV(key.value.toString(), value.value.qToLogString(), fg).toString()
    }.toList().qJoinStringNicely()
}

// CallChain[size=6] = QLoopPos <-[Ref]- List<String>.qJoinStringNicely() <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private enum class QLoopPos {
    // CallChain[size=6] = QLoopPos.First <-[Call]- List<String>.qJoinStringNicely() <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    First,
    // CallChain[size=6] = QLoopPos.Middle <-[Call]- List<String>.qJoinStringNicely() <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    Middle,
    // CallChain[size=6] = QLoopPos.Last <-[Call]- List<String>.qJoinStringNicely() <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    Last,
    // CallChain[size=6] = QLoopPos.OnlyOneItem <-[Call]- List<String>.qJoinStringNicely() <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    OnlyOneItem;

    
}

// CallChain[size=6] = Collection<T>.qMap() <-[Call]- List<String>.qJoinStringNicely() <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private inline fun <T : Any, U : Any?> Collection<T>.qMap(
    crossinline block: (item: T, idx: Int, pos: QLoopPos, previous: T?, next: T?) -> U
): MutableList<U> {
    val list = mutableListOf<U>()

    var prePre: T? = null
    var pre: T? = null
    for ((i, next) in withIndex()) {
        if (i == 0) {
            pre = next
            continue
        }

        // non-last item
        list += block(
            pre!!, i - 1,
            if (i == 1) {
                QLoopPos.First
            } else {
                QLoopPos.Middle
            }, prePre, next
        )

        prePre = pre
        pre = next
    }

    if (isNotEmpty()) {
        // last item
        list += block(
            pre!!,
            size - 1,
            if (size == 1) {
                QLoopPos.OnlyOneItem
            } else {
                QLoopPos.Last
            },
            prePre,
            null
        )
    }

    return list
}

// CallChain[size=24] = qMinIntervalMap <-[Call]- qMinIntervalRun() <-[Call]- QExProps.removeGarbage ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private val qMinIntervalMap by lazy { QMinIntervalMap(false) }

// CallChain[size=23] = qMinIntervalRun() <-[Call]- QExProps.removeGarbageCollectedEntries() <-[Call ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun <T : Any?> qMinIntervalRun(
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
private class QMinInterval(val key: Any, val minIntervalMillis: Long) {
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

// CallChain[size=11] = QOut <-[Ref]- QLogStyle <-[Ref]- QLogStyle.SRC_AND_STACK <-[Call]- QExceptio ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private interface QOut {
    // CallChain[size=13] = QOut.isAcceptColoredText <-[Propag]- QOut.CONSOLE <-[Call]- QMyLog.out <-[Ca ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val isAcceptColoredText: Boolean

    // CallChain[size=13] = QOut.print() <-[Propag]- QOut.CONSOLE <-[Call]- QMyLog.out <-[Call]- QLogSty ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun print(msg: Any? = "")

    // CallChain[size=13] = QOut.println() <-[Propag]- QOut.CONSOLE <-[Call]- QMyLog.out <-[Call]- QLogS ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun println(msg: Any? = "")

    // CallChain[size=13] = QOut.deleteLast() <-[Propag]- QOut.CONSOLE <-[Call]- QMyLog.out <-[Call]- QL ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun deleteLast(length: Int)

    // CallChain[size=13] = QOut.close() <-[Propag]- QOut.CONSOLE <-[Call]- QMyLog.out <-[Call]- QLogSty ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun close()

    companion object {
        // CallChain[size=12] = QOut.CONSOLE <-[Call]- QMyLog.out <-[Call]- QLogStyle <-[Ref]- QLogStyle.SRC ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        val CONSOLE: QOut = QConsole(true)

        
    }
}

// CallChain[size=3] = QOut.separator() <-[Call]- qTest() <-[Call]- main()[Root]
private fun QOut.separator(start: String = "\n", end: String = "\n") {
    this.println(qSeparator(start = start, end = end))
}

// CallChain[size=13] = QConsole <-[Call]- QOut.CONSOLE <-[Call]- QMyLog.out <-[Call]- QLogStyle <-[ ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private class QConsole(override val isAcceptColoredText: Boolean) : QOut {
    // CallChain[size=14] = QConsole.print() <-[Propag]- QConsole <-[Call]- QOut.CONSOLE <-[Call]- QMyLo ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun print(msg: Any?) {
        if (isAcceptColoredText) {
            kotlin.io.print(msg.toString())
        } else {
            kotlin.io.print(msg.toString().noStyle)
        }
    }

    // CallChain[size=14] = QConsole.println() <-[Propag]- QConsole <-[Call]- QOut.CONSOLE <-[Call]- QMy ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun println(msg: Any?) {
        kotlin.io.println(msg.toString())
    }

    // CallChain[size=14] = QConsole.deleteLast() <-[Propag]- QConsole <-[Call]- QOut.CONSOLE <-[Call]-  ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun deleteLast(length: Int) {
        kotlin.io.print("\b".repeat(length))
    }

    // CallChain[size=14] = QConsole.close() <-[Propag]- QConsole <-[Call]- QOut.CONSOLE <-[Call]- QMyLo ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun close() {
        // Do nothing
    }
}

// CallChain[size=4] = Method.qName() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun Method.qName(withParenthesis: Boolean = false): String {
    val clsName = declaringClass.simpleName

    val name = if (clsName.isNotEmpty()) {
        "$clsName.$name"
    } else {
        name
    }

    return if (withParenthesis) {
        "$name()"
    } else {
        name
    }
}

// CallChain[size=6] = KClass<*>.qFunctions() <-[Call]- qToStringRegistry <-[Call]- Any.qToString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun KClass<*>.qFunctions(matcher: QMFunc = QMFunc.DeclaredOnly and QMFunc.IncludeExtensionsInClass): List<KFunction<*>> {
    val list = mutableListOf<KFunction<*>>()

    var functions = if (matcher.declaredOnly) {
        this.declaredFunctions
    } else {
        this.memberFunctions
    }

    list += functions.filter { matcher.matches(it) }

    if (matcher.includeExtensionsInClass) {
        functions = if (matcher.declaredOnly) {
            this.declaredMemberExtensionFunctions
        } else {
            this.memberExtensionFunctions
        }

        list += functions.filter { matcher.matches(it) }
    }

    return list
}

// CallChain[size=19] = KClass<E>.qEnumValues() <-[Call]- QFlagMut.allEnumValues <-[Propag]- QFlagMu ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun <E : Enum<E>> KClass<E>.qEnumValues(): Array<E> {
    return java.enumConstants as Array<E>
}

// CallChain[size=5] = qThisSrcLineSignature <-[Call]- qTimeIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private val qThisSrcLineSignature: String
    get() = qCallerSrcLineSignature()

// CallChain[size=12] = qSrcFileAtFrame() <-[Call]- qSrcFileLinesAtFrame() <-[Call]- qMySrcLinesAtFr ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun qSrcFileAtFrame(frame: StackFrame, srcRoots: List<Path> = QMyPath.src_root, pkgDirHint: String? = null): Path =
    qCacheItOneSec(
        frame.fileName + frame.lineNumber + srcRoots.map { it }.joinToString() + pkgDirHint
    ) {
        val pkgDir = pkgDirHint ?: frame.declaringClass.packageName.replace(".", "/")

        var srcFile: Path? = null

        for (dir in srcRoots) {
            val root = dir.toAbsolutePath()
            val fileInPkgDir = root.resolve(pkgDir).resolve(frame.fileName)
            if (fileInPkgDir.exists()) {
                srcFile = fileInPkgDir
                break
            } else {
                val fileNoPkgDir = root.resolve(frame.fileName)
                if (fileNoPkgDir.exists()) {
                    srcFile = fileNoPkgDir
                }
            }
        }

        if (srcFile != null)
            return@qCacheItOneSec srcFile

        return@qCacheItOneSec srcRoots.qFind(QM.exact(frame.fileName), maxDepth = 100)
            .qaNotNull(QE.FileNotFound, qBrackets("FileName", frame.fileName, "SrcRoots", srcRoots))
    }

// CallChain[size=3] = qCallerFileName() <-[Call]- qTest() <-[Call]- main()[Root]
private fun qCallerFileName(stackDepth: Int = 0): String {
    return qStackFrame(stackDepth + 2).fileName
}

// CallChain[size=3] = qCallerSrcLineSignature() <-[Call]- String.qColorRandom() <-[Call]- QColorTest.randomColor()[Root]
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

// CallChain[size=9] = qStackFrames() <-[Call]- QException.stackFrames <-[Call]- QException.getStack ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private inline fun qStackFrames(
    stackDepth: Int = 0,
    size: Int = 1,
    noinline filter: (StackFrame) -> Boolean = qSTACK_FRAME_FILTER,
): List<StackFrame> {
    return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk { s: Stream<StackFrame> ->
        s.asSequence().filter(filter).drop(stackDepth).take(size).toList()
    }
}

// CallChain[size=12] = qStackFrame() <-[Call]- qSrcFileLinesAtFrame() <-[Call]- qMySrcLinesAtFrame( ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private inline fun qStackFrame(
    stackDepth: Int = 0,
    noinline filter: (StackFrame) -> Boolean = qSTACK_FRAME_FILTER,
): StackFrame {
    return qStackFrames(stackDepth, 1, filter)[0]
}

// CallChain[size=3] = qStackFrameEntryMethod() <-[Call]- qTest() <-[Call]- main()[Root]
private fun qStackFrameEntryMethod(filter: (StackFrame) -> Boolean): StackFrame {
    return qStackFrames(0, Int.MAX_VALUE)
        .filter(filter)
        .findLast {
            it.lineNumber > 0
        }.qaNotNull()
}

// CallChain[size=7] = KType.qToClass() <-[Call]- KType.qIsSuperclassOf() <-[Call]- qToStringRegistr ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun KType.qToClass(): KClass<*>? {
    // KType MutableList<String> classifier returns List. I don't know why??

    return if (this.classifier != null && this.classifier is KClass<*>) {
        this.classifier as KClass<*>
    } else {
        null
    }
}

// CallChain[size=6] = KType.qIsSuperclassOf() <-[Call]- qToStringRegistry <-[Call]- Any.qToString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun KType.qIsSuperclassOf(cls: KClass<*>): Boolean {
    return try {
        val thisClass = qToClass()

        if (thisClass?.qualifiedName == "kotlin.Array" && cls.qualifiedName == "kotlin.Array") { // TODO 多分 toStringRegistry 関連で作った
            true
        } else {
            thisClass?.isSuperclassOf(cls) ?: false
        }
    } catch (e: Throwable) {
        // Exception in thread "main" kotlin.reflect.jvm.internal.KotlinReflectionInternalError: Unresolved class: ~
        false
    }
}

// CallChain[size=4] = AccessibleObject.qTrySetAccessible() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun AccessibleObject.qTrySetAccessible() {
    try {
        if (!this.trySetAccessible()) {
            QE.TrySetAccessibleFail.throwIt(this)
        }
    } catch (e: SecurityException) {
        QE.TrySetAccessibleFail.throwIt(this, e = e)
    }
}

// CallChain[size=3] = qThisFileMainClass <-[Call]- qTest() <-[Call]- main()[Root]
private val qThisFileMainClass: Class<*>
    get() = qCallerFileMainClass()

// CallChain[size=6] = qThisFilePackageName <-[Call]- Path.qListTopLevelFqClassNamesInThisFile() <-[Call]- qCallerFileMainClass() <-[Call]- qThisFileMainClass <-[Call]- qTest() <-[Call]- main()[Root]
private val qThisFilePackageName: String = qCallerPackageName(0)

// CallChain[size=5] = Path.qListTopLevelFqClassNamesInThisFile() <-[Call]- qCallerFileMainClass() <-[Call]- qThisFileMainClass <-[Call]- qTest() <-[Call]- main()[Root]
private fun Path.qListTopLevelFqClassNamesInThisFile(charset: Charset = Charsets.UTF_8): List<String> {
    val srcCode = this.readText(charset)

    val topLevelSrc = srcCode.qMask(QMask.KOTLIN_STRING).maskedStr
        .qRemoveBetween(
            "{",
            "}",
            "{",
            nestingDepth = 1,
            regionIncludesStartAndEndSequence = false
        )

    val pkgName = qThisFilePackageName

    val pkgNameDot = if (pkgName.isNotEmpty()) {
        "$pkgName."
    } else {
        ""
    }

    val regex = """^((private|internal) +)?class +(\w+).*""".re

    return topLevelSrc.lineSequence().filter {
        it.matches(regex)
    }.map {
        it.replaceFirst(regex, "$3").trim()
    }.map {
        "$pkgNameDot$it"
    }.toList()
}

// CallChain[size=3] = Class<*>.qMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun Class<*>.qMethods(matcher: QMMethod = QMMethod.DeclaredOnly): List<Method> {
    val allMethods = if (matcher.declaredOnly) declaredMethods else methods
    return allMethods.filter { matcher.matches(it) }
}

// CallChain[size=4] = Class<T>.qNewInstance() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun <T : Any> Class<T>.qNewInstance(vararg params: Any, setAccessible: Boolean = false): T {
    val constructor = qConstructor(*params, declaredOnly = false)
    try {
        if( setAccessible )
            constructor.isAccessible = true
    } catch( e: QException ) {

    }
    return constructor.newInstance()
}

// CallChain[size=5] = Class<T>.qConstructor() <-[Call]- Class<T>.qNewInstance() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun <T : Any> Class<T>.qConstructor(vararg params: Any, declaredOnly: Boolean = false): Constructor<T> {
    return if (declaredOnly) {
        this.getDeclaredConstructor(*params.map { it::class.java }.toTypedArray())
            .qaNotNull(QE.ConstructorNotFound)
    } else {
        this.getConstructor(*params.map { it::class.java }.toTypedArray())
            .qaNotNull(QE.ConstructorNotFound)
    }
}

// CallChain[size=7] = Class<*>.qPrimitiveToWrapper() <-[Call]- Class<*>.qIsAssignableFrom() <-[Call ... -[Propag]- QMatchMethodParams <-[Call]- QMMethod.NoParams <-[Call]- qTest() <-[Call]- main()[Root]
private fun Class<*>.qPrimitiveToWrapper(): Class<*> = qJVMPrimitiveToWrapperMap[this] ?: this

// CallChain[size=8] = qJVMPrimitiveToWrapperMap <-[Call]- Class<*>.qPrimitiveToWrapper() <-[Call]-  ... -[Propag]- QMatchMethodParams <-[Call]- QMMethod.NoParams <-[Call]- qTest() <-[Call]- main()[Root]
private val qJVMPrimitiveToWrapperMap by lazy {
    val map = HashMap<Class<*>, Class<*>>()

    map[java.lang.Boolean.TYPE] = java.lang.Boolean::class.java
    map[java.lang.Byte.TYPE] = java.lang.Byte::class.java
    map[java.lang.Character.TYPE] = java.lang.Character::class.java
    map[java.lang.Short.TYPE] = java.lang.Short::class.java
    map[java.lang.Integer.TYPE] = java.lang.Integer::class.java
    map[java.lang.Long.TYPE] = java.lang.Long::class.java
    map[java.lang.Double.TYPE] = java.lang.Double::class.java
    map[java.lang.Float.TYPE] = java.lang.Float::class.java
    map
}

// CallChain[size=6] = Class<*>.qIsAssignableFrom() <-[Call]- QMatchMethodParams.matches() <-[Propag]- QMatchMethodParams <-[Call]- QMMethod.NoParams <-[Call]- qTest() <-[Call]- main()[Root]
private fun Class<*>.qIsAssignableFrom(subclass: Class<*>, autoboxing: Boolean = true): Boolean {
    return if (autoboxing) {
        this.qPrimitiveToWrapper().isAssignableFrom(subclass.qPrimitiveToWrapper())
    } else {
        this.isAssignableFrom(subclass)
    }
}

// CallChain[size=4] = Method.qIsInstanceMethod() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun Method.qIsInstanceMethod(): Boolean {
    return !Modifier.isStatic(this.modifiers)
}

// CallChain[size=7] = qCallerPackageName() <-[Call]- qThisFilePackageName <-[Call]- Path.qListTopLe ... all]- qCallerFileMainClass() <-[Call]- qThisFileMainClass <-[Call]- qTest() <-[Call]- main()[Root]
private fun qCallerPackageName(stackDepth: Int = 0): String {
    return try {
        val frame = qStackFrame(stackDepth + 2)
        frame.declaringClass.packageName
    } catch (e: Exception) {
        ""
    }
}

// CallChain[size=4] = qCallerFileMainClass() <-[Call]- qThisFileMainClass <-[Call]- qTest() <-[Call]- main()[Root]
private fun qCallerFileMainClass(stackDepth: Int = 0): Class<*> {
    val frame = qStackFrame(stackDepth + 2)
    val fileName = frame.fileName.substring(0, frame.fileName.lastIndexOf("."))
    val pkgName = frame.declaringClass.packageName

    val clsName = if (fileName.endsWith(".kt", true)) {
        if (pkgName.isNotEmpty())
            "$pkgName.${fileName}Kt"
        else
            "${fileName}Kt"
    } else {
        if (pkgName.isNotEmpty())
            "$pkgName.$fileName"
        else
            fileName
    }

    val classesNotFound = mutableListOf<String>()

    try {
        return Class.forName(clsName)
    } catch ( e: ClassNotFoundException) {
        classesNotFound.add(clsName)
        val srcFile = qSrcFileAtFrame(frame)
        val classNamesInSrcFile = srcFile.qListTopLevelFqClassNamesInThisFile()

        for(clsInFile in classNamesInSrcFile) {
            try {
                return Class.forName(clsInFile)
            } catch(e: ClassNotFoundException) {
                classesNotFound.add(clsInFile)
                continue
            }
        }
    }

    QE.ClassNotFound.throwItBrackets("Classes", classesNotFound)
}

// CallChain[size=12] = RO <-[Ref]- qRe() <-[Call]- @receiver:Language("RegExp") String.re <-[Call]- ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private typealias RO = RegexOption

// CallChain[size=11] = qRe() <-[Call]- @receiver:Language("RegExp") String.re <-[Call]- String.qCam ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun qRe(@Language("RegExp") regex: String, vararg opts: RO): Regex {
    return qCacheItOneSecThreadLocal(regex + opts.contentToString()) {
        Regex(regex, setOf(*opts))
    }
}

// CallChain[size=10] = @receiver:Language("RegExp") String.re <-[Call]- String.qCamelCaseToSpaceSep ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
// https://youtrack.jetbrains.com/issue/KTIJ-5643
private val @receiver:Language("RegExp") String.re: Regex
    get() = qRe(this)

// CallChain[size=11] = QSequenceReader <-[Call]- QBetween.find() <-[Call]- String.qFindBetween() <- ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private open class QSequenceReader(text: CharSequence) : QCharReader(text) {
    // CallChain[size=12] = QSequenceReader.sequenceOffset <-[Propag]- QSequenceReader.detectSequence()  ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    private var sequenceOffset = 0

    // CallChain[size=12] = QSequenceReader.sequence <-[Propag]- QSequenceReader.detectSequence() <-[Cal ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    private var sequence: CharArray? = null

    // CallChain[size=12] = QSequenceReader.startReadingSequence() <-[Call]- QSequenceReader.detectSeque ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    private fun startReadingSequence(sequence: CharArray): Boolean {
        return if (countCharToEOF() < sequence.size) {
            false
        } else {
            this.sequence = sequence
            sequenceOffset = offset
            true
        }
    }

    // CallChain[size=12] = QSequenceReader.endReadingSequence() <-[Call]- QSequenceReader.detectSequenc ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    private fun endReadingSequence(success: Boolean): Boolean {

        if (!success) {
            offset = sequenceOffset
        }

        sequenceOffset = -1

        return success
    }

    // CallChain[size=12] = QSequenceReader.hasNextCharInSequence() <-[Call]- QSequenceReader.detectSequ ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    private fun hasNextCharInSequence(): Boolean {
        return if (sequence == null) {
            false
        } else {
            (offsetInSequence() < sequence!!.size) && !isEOF()
        }
    }

    // CallChain[size=12] = QSequenceReader.peekCurrentCharInSequence() <-[Call]- QSequenceReader.detect ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    private fun peekCurrentCharInSequence(): Char {
        return sequence!![offsetInSequence()]
    }

    // CallChain[size=12] = QSequenceReader.offsetInSequence() <-[Call]- QSequenceReader.detectSequence( ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    /**
     * 0 to sequence.size - 1
     */
    private fun offsetInSequence(): Int {
        return offset - sequenceOffset
    }
    // CallChain[size=12] = QSequenceReader.detectChar() <-[Propag]- QSequenceReader.detectSequence() <- ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun detectChar(target: Char): Boolean {
        return if( target == peekRead() ) {
            read()
            true
        } else {
            false
        }
    }

    // CallChain[size=12] = QSequenceReader.detectWord() <-[Propag]- QSequenceReader.detectSequence() <- ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun detectWord(): Boolean {
        startDetection()

        var ch = peekRead()
        if( ch.isWhitespace() )
            return endDetection(false)

        while (!isEOF()) {
            ch = read()
            if (ch.isWhitespace()) {
                unread() // remove last whitespace
                return endDetection(true)
            }
        }

        return endDetection(true)
    }

    // CallChain[size=12] = QSequenceReader.detectSpace() <-[Propag]- QSequenceReader.detectSequence() < ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun detectSpace(): Boolean {
        startDetection()

        var ch = peekRead()
        if( !ch.qIsWhitespace(allowLinebreak = false))
            return endDetection(false)

        while (!isEOF()) {
            ch = read()
            if (!ch.qIsWhitespace(allowLinebreak = false)) {
                unread()
                return endDetection(true)
            }
        }

        return endDetection(true)
    }

    // CallChain[size=11] = QSequenceReader.detectSequence() <-[Call]- QBetween.find() <-[Call]- String. ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    /**
     * If sequence is detected, move offset by the length of the sequence.
     * If no sequence is found, offset remains unchanged.
     */
    fun detectSequence(sequence: CharArray, eofAllowed: Boolean = false): Boolean {
        if (!startReadingSequence(sequence)) return false

        while (hasNextCharInSequence()) {
            val seqChar = peekCurrentCharInSequence()
            val ch = read()

            if (ch != seqChar) {
                endReadingSequence(false)
                return eofAllowed && isEOF()
            }
        }

        return if (offsetInSequence() == sequence.size) {
            endReadingSequence(true)
            true
        } else {
            val success = eofAllowed && isEOF()
            endReadingSequence(success)
            success
        }
    }

    // CallChain[size=12] = QSequenceReader.QSequenceBetween <-[Propag]- QSequenceReader.detectSequence( ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    class QSequenceBetween(
        val startSequence: CharArray,
        val endSequence: CharArray,
        val nestStartSequence: CharArray? = null,
        val escapeChar: Char? = null,
        val allowEOFEnd: Boolean = false,
    )

    // CallChain[size=12] = QSequenceReader.detectSequenceBetween() <-[Propag]- QSequenceReader.detectSe ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun detectSequenceBetween(
        seq: QSequenceBetween
    ): Boolean {
        val seqBetweenStartOffset = offset

        if (!detectSequence(seq.startSequence)) {
            return endDetection(false)
        }

        var nNest = 0

        while (!isEOF()) {
            val ch = read()

            if (ch == seq.escapeChar) {
                skip(1)
                continue
            } else {
                unread()

                val nestStartSequenceDetected = if (seq.nestStartSequence != null) {
                    detectSequence(seq.nestStartSequence, seq.allowEOFEnd)
                } else {
                    false
                }

                if (nestStartSequenceDetected) {
                    nNest++
                } else if (detectSequence(seq.endSequence, seq.allowEOFEnd)) {
                    if (nNest == 0) {
                        detectionStartOffset = seqBetweenStartOffset
                        detectionEndOffset = offset

                        return true
                    }

                    nNest--
                } else {
                    skip(1)
                }
            }
        }

        return endDetection(seq.allowEOFEnd)
    }

    // CallChain[size=12] = QSequenceReader.detectSequenceRegex() <-[Propag]- QSequenceReader.detectSequ ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    /**
     * If sequence is detected, move offset by the length of the sequence.
     * If no sequence is found, offset remains unchanged.
     */
    fun detectSequenceRegex(
        sequence: Regex,
        lengthFromCurrentOffset: Int,
        okToRegexMatch: ((QSequenceReader) -> Boolean)? = null,
        eofAllowed: Boolean = false
    ): String? {
        if (okToRegexMatch != null) {
            val curOffset = this.offset

            if (!okToRegexMatch(this)) {
                return null
            }

            this.offset = curOffset
        }

        val nextString = peekReadString(lengthFromCurrentOffset)
        val found = sequence.find(nextString)

        return if (found != null) {
            skip(found.value.length)
            found.value
        } else if (eofAllowed && countCharToEOF() < lengthFromCurrentOffset) {
            val seq = text.substring(offset + 1, text.length)
            skipToEOF()
            seq
        } else {
            null
        }
    }
}

// CallChain[size=11] = String.path <-[Call]- QMyPath.src_root <-[Call]- qLogStackFrames() <-[Call]- ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private val String.path: Path
    get() = Paths.get(this.trim()).toAbsolutePath().normalize()

// CallChain[size=5] = QAlign <-[Ref]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private enum class QAlign {
    // CallChain[size=5] = QAlign.LEFT <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    LEFT,
    // CallChain[size=6] = QAlign.RIGHT <-[Propag]- QAlign.LEFT <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    RIGHT,
    // CallChain[size=6] = QAlign.CENTER <-[Propag]- QAlign.LEFT <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    CENTER
}

// CallChain[size=9] = QLineMatchResult <-[Call]- String.qAlign() <-[Call]- String.qAlignCenter() <- ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private class QLineMatchResult(
    val regex: Regex,
    val text: String,
    val onlyFirstMatch: Boolean = false,
    val groupIdx: QGroupIdx
) {
    // CallChain[size=10] = QLineMatchResult.curText <-[Call]- QLineMatchResult.align() <-[Call]- String ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    var curText: String = text

    // CallChain[size=10] = QLineMatchResult.updateResult() <-[Call]- QLineMatchResult.align() <-[Call]- ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    private fun updateResult(align: QAlign) {
        updateRowResult()
        updateColResult()
        updateColDestPos(align)
    }

    // CallChain[size=11] = QLineMatchResult.rowResults <-[Call]- QLineMatchResult.matchedRange() <-[Cal ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    // line index -> match index
    lateinit var rowResults: List<List<MatchResult>>

    // CallChain[size=12] = QLineMatchResult.colResults <-[Call]- QLineMatchResult.updateColDestPos() <- ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    // col index -> match index
    lateinit var colResults: List<List<MatchResult>>

    // CallChain[size=10] = QLineMatchResult.colDestPos <-[Call]- QLineMatchResult.align() <-[Call]- Str ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    // col index -> max left index / right index of matched region
    lateinit var colDestPos: List<QLeftRight>

    // CallChain[size=11] = QLineMatchResult.updateRowResult() <-[Call]- QLineMatchResult.updateResult() ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    private fun updateRowResult() {
        rowResults = if (onlyFirstMatch) {
            curText.lineSequence().map { regex.find(it, 0) }.map {
                if (it == null) {
                    emptyList()
                } else {
                    listOf(it)
                }
            }.toList()
        } else {
            curText.lineSequence().map { regex.findAll(it, 0).toList() }.toList()
        }
    }

    // CallChain[size=11] = QLineMatchResult.updateColResult() <-[Call]- QLineMatchResult.updateResult() ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    private fun updateColResult() {
        colResults = mutableListOf<List<MatchResult>>().also { list ->
            val maximumRowMatchCount = rowResults.maxOfOrNull { it.size } ?: -1

            for (iColumn in 0 until maximumRowMatchCount) {
                list += rowResults.mapNotNull { result ->
                    result.getOrNull(iColumn)
                }
            }
        }
    }

    // CallChain[size=11] = QLineMatchResult.updateColDestPos() <-[Call]- QLineMatchResult.updateResult( ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    private fun updateColDestPos(align: QAlign) {
        colDestPos = if (align == QAlign.RIGHT) {
            colResults.map { it.qMinOrMaxIndexLR(groupIdx, QMinOrMax.MAX) }
        } else {
            colResults.map { it.qMinOrMaxIndexLR(groupIdx, QMinOrMax.MIN) }
        }
    }

    // CallChain[size=10] = QLineMatchResult.matchedRange() <-[Call]- QLineMatchResult.align() <-[Call]- ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    private fun matchedRange(rowIdx: Int, colIdx: Int): IntRange? {
        val groups = rowResults.getOrNull(rowIdx)?.getOrNull(colIdx)?.groups ?: return null
        return if (groupIdx.idx < groups.size) {
            groups[groupIdx.idx]?.range
        } else {
            null
        }
    }

    // CallChain[size=12] = QLineMatchResult.QMinOrMax <-[Ref]- QLineMatchResult.updateColDestPos() <-[C ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    enum class QMinOrMax {
        MIN, MAX
    }

    // CallChain[size=12] = QLineMatchResult.List<MatchResult>.qMinOrMaxIndexLR() <-[Call]- QLineMatchRe ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    private fun List<MatchResult>.qMinOrMaxIndexLR(
        groupIdx: QGroupIdx,
        minOrMax: QMinOrMax
    ): QLeftRight {
        val leftList = mapNotNull {
            if (groupIdx.idx < it.groups.size) {
                it.groups[groupIdx.idx]?.range?.first
            } else {
                -1
            }
        }

        val left =
            if (minOrMax == QMinOrMax.MIN) {
                leftList.minOrNull() ?: -1
            } else {
                leftList.maxOrNull() ?: -1
            }

        val rightList = mapNotNull {
            if (groupIdx.idx < it.groups.size) {
                it.groups[groupIdx.idx]?.range?.last
            } else {
                -1
            }
        }

        val right =
            if (minOrMax == QMinOrMax.MIN) {
                rightList.minOrNull() ?: -1
            } else {
                rightList.maxOrNull() ?: -1
            }

        return QLeftRight(left, right)
    }

    // CallChain[size=9] = QLineMatchResult.align() <-[Call]- String.qAlign() <-[Call]- String.qAlignCen ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    fun align(align: QAlign = QAlign.RIGHT, keepLength: Boolean = false, oddLengthTuning: QLR): String {
        updateResult(align)

        var colIdx = 0

        while (colIdx < colDestPos.size) {
            val lines = curText.lineSequence().mapIndexed { rowIdx, line ->
                val range = matchedRange(rowIdx, colIdx) ?: return@mapIndexed line

                if (align == QAlign.CENTER) {
                    line.qMoveCenter(range, oddLengthTuning)
                } else {
                    val maxLR = colDestPos[colIdx]

                    val destLeft = when (align) {
                        QAlign.RIGHT -> maxLR.right - range.qSize + 1
                        QAlign.LEFT -> maxLR.left
                        else -> qUnreachable()
                    }

                    if (range.first < destLeft) {
                        line.qMoveRight(range, destLeft, keepLength)
                    } else {
                        line.qMoveLeft(range, destLeft, keepLength)
                    }
                }
            }

            curText = lines.joinToString("\n")

            if (!keepLength && align != QAlign.CENTER) {
                updateResult(align)
            }

            colIdx++
        }

        return curText
    }
}

// CallChain[size=10] = IntRange.qSize <-[Call]- QLineMatchResult.align() <-[Call]- String.qAlign()  ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private val IntRange.qSize: Int
    get() = abs(last - first) + 1

// CallChain[size=8] = String.qIsNumber() <-[Call]- String.qAlignCenter() <-[Call]- String.qWithMinL ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun String.qIsNumber(): Boolean {
    return this.trim().matches("""[\d./eE+-]+""".re)
}

// CallChain[size=7] = String.qAlignCenter() <-[Call]- String.qWithMinLength() <-[Call]- String.qWit ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun String.qAlignCenter(
    vararg places: Regex = arrayOf("(.*)".re),
    onlyFirstMatch: Boolean = true,
    oddLengthTuning: QLR = if (qIsNumber()) QLR.Right else QLR.Left,
    groupIdx: QGroupIdx = QGroupIdx.FIRST
): String {
    return qAlign(QAlign.CENTER, *places, onlyFirstMatch = onlyFirstMatch, oddLengthTuning = oddLengthTuning, groupIdx = groupIdx)
}

// CallChain[size=8] = String.qAlign() <-[Call]- String.qAlignCenter() <-[Call]- String.qWithMinLeng ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun String.qAlign(
    align: QAlign = QAlign.RIGHT,
    vararg places: Regex,
    onlyFirstMatch: Boolean = true,
    keepLength: Boolean = false,
    oddLengthTuning: QLR = qDefaultOddLengthTuning(),
    groupIdx: QGroupIdx = QGroupIdx.ENTIRE_MATCH
): String {
    var text = this
    for (p in places) {
        text = QLineMatchResult(p, text, onlyFirstMatch, groupIdx).align(align, keepLength, oddLengthTuning)
    }

    return text
}

// CallChain[size=11] = QLeftRight <-[Ref]- QLineMatchResult.colDestPos <-[Call]- QLineMatchResult.a ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private data class QLeftRight(val left: Int, val right: Int)

// CallChain[size=8] = QGroupIdx <-[Ref]- String.qAlignCenter() <-[Call]- String.qWithMinLength() <- ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private enum class QGroupIdx(val idx: Int) {
    // CallChain[size=9] = QGroupIdx.ENTIRE_MATCH <-[Call]- String.qAlign() <-[Call]- String.qAlignCente ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    ENTIRE_MATCH(0),
    // CallChain[size=8] = QGroupIdx.FIRST <-[Call]- String.qAlignCenter() <-[Call]- String.qWithMinLeng ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    FIRST(1),
    // CallChain[size=12] = QGroupIdx.SECOND <-[Propag]- QGroupIdx.QGroupIdx() <-[Call]- QLineMatchResul ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    SECOND(2),
    // CallChain[size=12] = QGroupIdx.THIRD <-[Propag]- QGroupIdx.QGroupIdx() <-[Call]- QLineMatchResult ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    THIRD(3),
    // CallChain[size=12] = QGroupIdx.FOURTH <-[Propag]- QGroupIdx.QGroupIdx() <-[Call]- QLineMatchResul ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    FOURTH(4),
    // CallChain[size=12] = QGroupIdx.FIFTH <-[Propag]- QGroupIdx.QGroupIdx() <-[Call]- QLineMatchResult ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    FIFTH(5),
    // CallChain[size=12] = QGroupIdx.SIXTH <-[Propag]- QGroupIdx.QGroupIdx() <-[Call]- QLineMatchResult ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    SIXTH(6),
    // CallChain[size=12] = QGroupIdx.SEVENTH <-[Propag]- QGroupIdx.QGroupIdx() <-[Call]- QLineMatchResu ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    SEVENTH(7),
    // CallChain[size=12] = QGroupIdx.EIGHTH <-[Propag]- QGroupIdx.QGroupIdx() <-[Call]- QLineMatchResul ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    EIGHTH(8),
    // CallChain[size=12] = QGroupIdx.NINTH <-[Propag]- QGroupIdx.QGroupIdx() <-[Call]- QLineMatchResult ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    NINTH(9),
    // CallChain[size=12] = QGroupIdx.TENTH <-[Propag]- QGroupIdx.QGroupIdx() <-[Call]- QLineMatchResult ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    TENTH(10);
}

// CallChain[size=10] = String.qMoveCenter() <-[Call]- QLineMatchResult.align() <-[Call]- String.qAl ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
// always keep length
private fun String.qMoveCenter(range: IntRange, oddLengthTuning: QLR): String {
    val regionText = qSubstring(range) // includes spaces

    val nLeftSpace = regionText.qCountLeftSpace()
    val nRightSpace = regionText.qCountRightSpace()

    val nonSpaceChars = regionText.substring(nLeftSpace, regionText.length - nRightSpace)

    val nLeftSpaceTarget =
        if (oddLengthTuning == QLR.Left || (nLeftSpace + nRightSpace) % 2 == 0) {
            abs(nLeftSpace + nRightSpace) / 2
        } else {
            abs(nLeftSpace + nRightSpace) / 2 + 1
        }
    val nRightSpaceTarget = (nLeftSpace + nRightSpace) - nLeftSpaceTarget

    return replaceRange(range, " ".repeat(nLeftSpaceTarget) + nonSpaceChars + " ".repeat(nRightSpaceTarget))
//    return substring(
//        0, range.first
//    ) + " ".repeat(nLeftSpaceTarget) + nonSpaceChars + " ".repeat(nRightSpaceTarget) + substring(range.last + 1, length)
}

// CallChain[size=10] = String.qMoveLeft() <-[Call]- QLineMatchResult.align() <-[Call]- String.qAlig ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun String.qMoveLeft(range: IntRange, destRangeLeft: Int, keepLength: Boolean): String {
    if (destRangeLeft >= range.first) {
        return this
    }

    if (substring(destRangeLeft, range.first).isNotBlank()) {
        // can't move. already has some contents.
        return this
    }

    val regionText = qSubstring(range)

    val nSpaces = range.first - destRangeLeft
    if (nSpaces <= 0) return this

    // when keepLength is true, add as many spaces to the right as removed
    val rightSpaces = if (keepLength) " ".repeat(nSpaces) else ""

    // cut left spaces
    val first = substring(0, range.first - nSpaces) + regionText

    // add spaces to the right
    return first + rightSpaces + substring(range.last + 1, length)
}

// CallChain[size=10] = String.qMoveRight() <-[Call]- QLineMatchResult.align() <-[Call]- String.qAli ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun String.qMoveRight(range: IntRange, destRangeLeft: Int, keepLength: Boolean): String {
    val regionText = qSubstring(range)

    val nSpaces = destRangeLeft - range.first

    if (nSpaces <= 0) return this

    val spaces = " ".repeat(nSpaces)

    return if (keepLength) {
        if (range.last + 1 > length) return this

        if (range.last + 1 + nSpaces > length) return this

        if (substring(range.last + 1, range.last + 1 + nSpaces).isNotBlank()) return this

        replaceRange(IntRange(range.first, range.last - nSpaces), spaces + regionText)
    } else {
        replaceRange(range, spaces + regionText)
    }
}

// CallChain[size=5] = String.qWithMinAndMaxLength() <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun String.qWithMinAndMaxLength(
    minLength: Int,
    maxLength: Int,
    alignment: QAlign = QAlign.RIGHT,
    oddLengthTuning: QLR = qDefaultOddLengthTuning(),
    endDots: String = "...",
): String {
    (minLength <= maxLength).qaTrue()

    return if (this.lengthWithoutAnsiCode() > maxLength) {
        qWithMaxLength(maxLength, endDots = endDots)
    } else {
        qWithMinLength(minLength, alignment, oddLengthTuning = oddLengthTuning)
    }
}

// CallChain[size=6] = String.qDefaultOddLengthTuning() <-[Call]- String.qWithMinAndMaxLength() <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun String.qDefaultOddLengthTuning(): QLR {
    return if (qIsNumber()) QLR.Right else QLR.Left
}

// CallChain[size=6] = String.lengthWithoutAnsiCode() <-[Call]- String.qWithMinAndMaxLength() <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun String.lengthWithoutAnsiCode(): Int {
    return this.noStyle.length
}

// CallChain[size=7] = String.ansiCodeLength() <-[Call]- String.qWithMinLength() <-[Call]- String.qW ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun String.ansiCodeLength(): Int {
    return this.length - this.noStyle.length
}

// CallChain[size=6] = String.qWithMinLength() <-[Call]- String.qWithMinAndMaxLength() <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun String.qWithMinLength(minLength: Int, alignment: QAlign = QAlign.RIGHT, oddLengthTuning: QLR = qDefaultOddLengthTuning()): String {
    val len = minLength + this.ansiCodeLength()
    
    return when (alignment) {
        QAlign.LEFT -> String.format("%-${len}s", this)
        QAlign.RIGHT -> String.format("%${len}s", this)
        QAlign.CENTER -> String.format("%${len}s", this).qAlignCenter(oddLengthTuning = oddLengthTuning)
    }
}

// CallChain[size=6] = String.qWithMaxLength() <-[Call]- String.qWithMinAndMaxLength() <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun String.qWithMaxLength(maxLength: Int, endDots: String = " ..."): String {
    if (maxLength - endDots.length < 0)
        QE.IllegalArgument.throwIt(maxLength - endDots.length)

    if (length < maxLength)
        return this

    if (endDots.isNotEmpty() && length < endDots.length + 1)
        return this

    return substring(0, length.coerceAtMost(maxLength - endDots.length)) + endDots
}

// CallChain[size=11] = String.qCountOccurrence() <-[Call]- QFetchRule.SMART_FETCH <-[Call]- qLogSta ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun String.qCountOccurrence(word: String): Int {
    return windowed(word.length) {
        if (it == word)
            1
        else
            0
    }.sum()
}

// CallChain[size=7] = QMask <-[Ref]- QMaskBetween <-[Call]- QMask.DOUBLE_QUOTE <-[Call]- QMask.KOTLIN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private interface QMask {
    // CallChain[size=5] = QMask.apply() <-[Propag]- QMask.KOTLIN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun apply(text: String): QMaskResult

    companion object {
        // CallChain[size=5] = QMask.THREE_DOUBLE_QUOTES <-[Call]- QMask.KOTLIN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        val THREE_DOUBLE_QUOTES by lazy {
            QMaskBetween(
                "\"\"\"", "\"\"\"",
                nestStartSequence = null,
                escapeChar = '\\',
                maskIncludeStartAndEndSequence = false,
            )
        }
        // CallChain[size=5] = QMask.DOUBLE_QUOTE <-[Call]- QMask.KOTLIN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        val DOUBLE_QUOTE by lazy {
            QMaskBetween(
                "\"", "\"",
                nestStartSequence = null,
                escapeChar = '\\',
                maskIncludeStartAndEndSequence = false,
            )
        }
        // CallChain[size=4] = QMask.KOTLIN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        val KOTLIN_STRING by lazy {
            QMultiMask(
                THREE_DOUBLE_QUOTES,
                DOUBLE_QUOTE
            )
        }
        // CallChain[size=4] = QMask.PARENS <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        val PARENS by lazy {
            QMaskBetween(
                "(", ")",
                nestStartSequence = "(", escapeChar = '\\'
            )
        }
        // CallChain[size=4] = QMask.INNER_BRACKETS <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        val INNER_BRACKETS by lazy {
            QMaskBetween(
                "[", "]",
                nestStartSequence = "[", escapeChar = '', // shell color
                targetNestDepth = 2,
                maskIncludeStartAndEndSequence = true
            )
        }

        
    }
}

// CallChain[size=5] = QMultiMask <-[Call]- QMask.KOTLIN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private class QMultiMask(vararg mask: QMaskBetween) : QMask {
    // CallChain[size=7] = QMultiMask.masks <-[Call]- QMultiMask.apply() <-[Propag]- QMultiMask <-[Call] ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val masks: Array<QMaskBetween>

    // CallChain[size=6] = QMultiMask.init { <-[Propag]- QMultiMask <-[Call]- QMask.KOTLIN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    init {
        masks = arrayOf(*mask)
    }

    // CallChain[size=6] = QMultiMask.apply() <-[Propag]- QMultiMask <-[Call]- QMask.KOTLIN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun apply(text: String): QMaskResult {
        var result: QMaskResult? = null
        for (mask in masks) {
            result = result?.applyMoreMask(mask) ?: mask.apply(text)
        }

        return result!!
    }
}

// CallChain[size=6] = QMaskBetween <-[Call]- QMask.DOUBLE_QUOTE <-[Call]- QMask.KOTLIN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
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

    // CallChain[size=7] = QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- QMask.D ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun apply(text: String): QMaskResult {
        return applyMore(text, null)
    }

    // CallChain[size=8] = QMaskBetween.applyMore() <-[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetw ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
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

// CallChain[size=11] = QMutRegion <-[Ref]- QRegion.toMutRegion() <-[Propag]- QRegion.contains() <-[ ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private open class QMutRegion(override var start: Int, override var end: Int) : QRegion(start, end) {
    // CallChain[size=12] = QMutRegion.intersectMut() <-[Propag]- QMutRegion <-[Ref]- QRegion.toMutRegio ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun intersectMut(region: QRegion) {
        val start = max(this.start, region.start)
        val end = min(this.end, region.end)

        if (start <= end) {
            this.start = start
            this.end = end
        }
    }

    // CallChain[size=12] = QMutRegion.addOffset() <-[Propag]- QMutRegion <-[Ref]- QRegion.toMutRegion() ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun addOffset(offset: Int) {
        start += offset
        end += offset
    }

    // CallChain[size=12] = QMutRegion.shift() <-[Propag]- QMutRegion <-[Ref]- QRegion.toMutRegion() <-[ ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun shift(length: Int) {
        this.start += length
        this.end += length
    }
}

// CallChain[size=11] = QRegion <-[Ref]- QRegion.intersect() <-[Propag]- QRegion.contains() <-[Call] ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
/**
 * [start] inclusive, [end] exclusive
 */
private open class QRegion(open val start: Int, open val end: Int) {
    // CallChain[size=10] = QRegion.toMutRegion() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween. ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun toMutRegion(): QMutRegion {
        return QMutRegion(start, end)
    }

    // CallChain[size=10] = QRegion.toRange() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.appl ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun toRange(): IntRange {
        return IntRange(start, end + 1)
    }

    // CallChain[size=10] = QRegion.length <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.applyMo ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val length: Int
        get() = end - start

    // CallChain[size=10] = QRegion.intersect() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.ap ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun intersect(region: QRegion): QRegion? {
        val start = max(this.start, region.start)
        val end = min(this.end, region.end)

        return if (start <= end) {
            QRegion(end, start)
        } else {
            null
        }
    }

    // CallChain[size=9] = QRegion.contains() <-[Call]- QMaskBetween.applyMore() <-[Call]- QMaskBetween. ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun contains(idx: Int): Boolean {
        return idx in start until end
    }

    // CallChain[size=10] = QRegion.cut() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.applyMor ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun cut(text: String): String {
        return text.substring(start, end)
    }

    // CallChain[size=10] = QRegion.remove() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.apply ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun remove(text: String): String {
        return text.removeRange(start, end)
    }

    // CallChain[size=10] = QRegion.replace() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.appl ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun replace(text: String, replacement: String): String {
        return text.replaceRange(start, end, replacement)
    }

    // CallChain[size=10] = QRegion.mask() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.applyMo ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun mask(text: String, maskChar: Char = '*'): String {
        return text.replaceRange(this.toRange(), maskChar.toString().repeat(end - start))
    }
}

// CallChain[size=6] = QReplacer <-[Ref]- String.qMaskAndReplace() <-[Call]- QMaskResult.replaceAndUnmask() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private class QReplacer(start: Int, end: Int, val replacement: String) : QMutRegion(start, end)

// CallChain[size=6] = QMaskResult <-[Ref]- QMask.apply() <-[Propag]- QMask.KOTLIN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private class QMaskResult(val maskedStr: String, val orgText: String, val maskChar: Char) {
    // CallChain[size=4] = QMaskResult.replaceAndUnmask() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    /**
     * Apply regex to masked string.
     * Apply replacement to original text.
     */
    fun replaceAndUnmask(ptn: Regex, replacement: String, findAll: Boolean = true): String {
        return orgText.qMaskAndReplace(maskedStr, ptn, replacement, findAll)
    }

    // CallChain[size=7] = QMaskResult.applyMoreMask() <-[Call]- QMultiMask.apply() <-[Propag]- QMultiMa ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun applyMoreMask(mask: QMaskBetween): QMaskResult {
        return mask.applyMore(maskedStr, orgText)
    }

    // CallChain[size=7] = QMaskResult.toString() <-[Propag]- QMaskResult <-[Ref]- QMask.apply() <-[Prop ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun toString(): String {
        val original = orgText.qWithNewLineSurround(onlyIf = QOnlyIfStr.Multiline)
        val masked = maskedStr.replace(maskChar, '*').qWithNewLineSurround(onlyIf = QOnlyIfStr.Multiline)

        return "${QMaskResult::class.simpleName} : $original ${"->".cyan} $masked"
    }
}

// CallChain[size=6] = CharSequence.qMask() <-[Call]- Path.qListTopLevelFqClassNamesInThisFile() <-[Call]- qCallerFileMainClass() <-[Call]- qThisFileMainClass <-[Call]- qTest() <-[Call]- main()[Root]
private fun CharSequence.qMask(mask: QMultiMask): QMaskResult {
    return mask.apply(this.toString())
}

// CallChain[size=4] = CharSequence.qMask() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun CharSequence.qMask(vararg mask: QMask): QMaskResult {
    mask.size.qaNotZero()

    return if (mask.size == 1) {
        mask[0].apply(this.toString())
    } else {
        val masks = mutableListOf<QMaskBetween>()
        for (m in mask) {
            if (m is QMaskBetween) {
                masks += m
            } else if (m is QMultiMask) {
                masks += m.masks
            }
        }

        QMultiMask(*masks.toTypedArray()).apply(this.toString())
    }
}

// CallChain[size=7] = QOnlyWhen <-[Ref]- String.qRemoveBetween() <-[Call]- Path.qListTopLevelFqClas ... all]- qCallerFileMainClass() <-[Call]- qThisFileMainClass <-[Call]- qTest() <-[Call]- main()[Root]
private enum class QOnlyWhen {
    // CallChain[size=8] = QOnlyWhen.FirstOnly <-[Call]- String.qReplaceBetween() <-[Call]- String.qRemo ... all]- qCallerFileMainClass() <-[Call]- qThisFileMainClass <-[Call]- qTest() <-[Call]- main()[Root]
    FirstOnly,
    // CallChain[size=8] = QOnlyWhen.LastOnly <-[Call]- String.qReplaceBetween() <-[Call]- String.qRemov ... all]- qCallerFileMainClass() <-[Call]- qThisFileMainClass <-[Call]- qTest() <-[Call]- main()[Root]
    LastOnly,
    // CallChain[size=7] = QOnlyWhen.All <-[Call]- String.qRemoveBetween() <-[Call]- Path.qListTopLevelF ... all]- qCallerFileMainClass() <-[Call]- qThisFileMainClass <-[Call]- qTest() <-[Call]- main()[Root]
    All
}

// CallChain[size=6] = String.qRemoveBetween() <-[Call]- Path.qListTopLevelFqClassNamesInThisFile() <-[Call]- qCallerFileMainClass() <-[Call]- qThisFileMainClass <-[Call]- qTest() <-[Call]- main()[Root]
private fun String.qRemoveBetween(
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
    onlyWhen: QOnlyWhen = QOnlyWhen.All,
): String {
    return qReplaceBetween(
        startSequence,
        endSequence,
        "",
        nestStartSequence,
        escapeChar,
        allowEOFEnd,
        nestingDepth,
        regionIncludesStartAndEndSequence,
        onlyWhen
    )
}

// CallChain[size=7] = String.qReplaceBetween() <-[Call]- String.qRemoveBetween() <-[Call]- Path.qLi ... all]- qCallerFileMainClass() <-[Call]- qThisFileMainClass <-[Call]- qTest() <-[Call]- main()[Root]
private fun String.qReplaceBetween(
    startSequence: String,
    endSequence: String,
    replacement: String,
    nestStartSequence: String? = if (startSequence != endSequence) {
        startSequence // can have nested structure
    } else {
        null // no nested structure
    },
    escapeChar: Char? = null,
    allowEOFEnd: Boolean = false,
    nestingDepth: Int = 1,
    regionIncludesStartAndEndSequence: Boolean = false,
    onlyWhen: QOnlyWhen = QOnlyWhen.All,
): String {
    val regions = qFindBetween(
        startSequence,
        endSequence,
        nestStartSequence,
        escapeChar,
        allowEOFEnd,
        nestingDepth,
        regionIncludesStartAndEndSequence
    )
    val mutRegions = regions.map { it.toMutRegion() }

    var text = this
    for ((idx, region) in mutRegions.withIndex()) {
        val okToReplace = when (onlyWhen) {
            QOnlyWhen.FirstOnly -> idx == 0
            QOnlyWhen.LastOnly -> idx == mutRegions.size - 1
            QOnlyWhen.All -> true
        }

        if (okToReplace) {
            text = region.replace(text, replacement)

            for (shiftRegion in mutRegions) {
                shiftRegion.shift(replacement.length - region.length)
            }
        }
    }

    return text
}

// CallChain[size=9] = String.qFindBetween() <-[Call]- QMaskBetween.applyMore() <-[Call]- QMaskBetwe ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
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

// CallChain[size=5] = String.qMaskAndReplace() <-[Call]- QMaskResult.replaceAndUnmask() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
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

// CallChain[size=6] = CharSequence.qMultiReplace() <-[Call]- String.qMaskAndReplace() <-[Call]- QMa ... dUnmask() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
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

// CallChain[size=6] = MatchResult.qResolveReplacementGroup() <-[Call]- String.qMaskAndReplace() <-[ ... dUnmask() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
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

// CallChain[size=7] = CharSequence.qReplace() <-[Call]- MatchResult.qResolveReplacementGroup() <-[C ... dUnmask() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun CharSequence.qReplace(oldValue: String, newValue: String, escapeChar: Char): String {
    return replace(Regex("""(?<!\Q$escapeChar\E)\Q$oldValue\E"""), newValue)
}

// CallChain[size=10] = QBetween <-[Call]- String.qFindBetween() <-[Call]- QMaskBetween.applyMore()  ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
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

    // CallChain[size=10] = QBetween.find() <-[Call]- String.qFindBetween() <-[Call]- QMaskBetween.apply ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun find(text: CharSequence): List<QRegion> {
        val reader = QSequenceReader(text)

        val ranges: MutableList<QRegion> = mutableListOf()

        val startChArr = startSequence.toCharArray()
        val nestStartChArr = nestStartSequence?.toCharArray()
        val endChArr = endSequence.toCharArray()

        var nNest = 0

        var startSeqIdxInclusive = -1

        while (!reader.isEOF()) {
            val ch = reader.read()

            if (ch == escapeChar) {
                reader.skip(1)
                continue
            } else {
                reader.unread()

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
                        startSeqIdxInclusive = reader.offset + 1
                    }
                } else if (nNest > 0 && reader.detectSequence(endChArr, allowEOFEnd)) {
                    if (nestingDepth == nNest) {
                        val endSeqIdxInclusive = reader.offset - endChArr.size

                        ranges += if (!regionIncludeStartAndEndSequence) {
                            QRegion(startSeqIdxInclusive, endSeqIdxInclusive + 1)
                        } else {
                            val end = min(endSeqIdxInclusive + endChArr.size + 1, text.length)
                            QRegion(startSeqIdxInclusive - startChArr.size, end)
                        }
                    }

                    nNest--
                } else {
                    reader.skip(1)
                }
            }
        }

        return ranges
    }
}

// CallChain[size=6] = QLR <-[Ref]- String.qWithMinAndMaxLength() <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private enum class QLR {
    // CallChain[size=8] = QLR.Left <-[Call]- String.qAlignCenter() <-[Call]- String.qWithMinLength() <- ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    Left,
    // CallChain[size=8] = QLR.Right <-[Call]- String.qAlignCenter() <-[Call]- String.qWithMinLength() < ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    Right
}

// CallChain[size=4] = qSeparator() <-[Call]- QOut.separator() <-[Call]- qTest() <-[Call]- main()[Root]
private fun qSeparator(
    fg: QColor? = QColor.LightGray,
    bg: QColor? = null,
    char: Char = '⎯',
    length: Int = 80,
    start: String = "\n",
    end: String = "\n",
): String {
    return start + char.toString().repeat(length).qColor(fg, bg) + end
}

// CallChain[size=4] = qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun qSeparatorWithLabel(
    label: String,
    fg: QColor? = QColor.LightGray,
    bg: QColor? = null,
    char: Char = '⎯',
    length: Int = 70,
    start: String = "\n",
    end: String = "\n",
): String {
    return "$start$label  " + char.toString().repeat((length - label.length - 2).coerceAtLeast(0)).qColor(fg, bg)
        .qWithMinAndMaxLength(length, length, alignment = QAlign.LEFT, endDots = "") + end
}

// CallChain[size=6] = QOnlyIfStr <-[Ref]- QBracketKV.toString() <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private enum class QOnlyIfStr(val matches: (String) -> Boolean) {
    // CallChain[size=7] = QOnlyIfStr.Multiline <-[Propag]- QOnlyIfStr.MultilineOrVeryLongLine <-[Call]- ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    Multiline({ it.qIsMultiLine() }),
    // CallChain[size=6] = QOnlyIfStr.MultilineOrVeryLongLine <-[Call]- QBracketKV.toString() <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    MultilineOrVeryLongLine({ it.qIsMultiLine() || it.qIsVeryLongLine() }),
    // CallChain[size=7] = QOnlyIfStr.SingleLine <-[Propag]- QOnlyIfStr.MultilineOrVeryLongLine <-[Call] ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    SingleLine({ it.qIsSingleLine() }),
    // CallChain[size=7] = QOnlyIfStr.Empty <-[Propag]- QOnlyIfStr.MultilineOrVeryLongLine <-[Call]- QBr ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    Empty({ it.isEmpty() }),
    // CallChain[size=7] = QOnlyIfStr.Blank <-[Propag]- QOnlyIfStr.MultilineOrVeryLongLine <-[Call]- QBr ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    Blank({ it.isBlank() }),
    // CallChain[size=7] = QOnlyIfStr.NotEmpty <-[Propag]- QOnlyIfStr.MultilineOrVeryLongLine <-[Call]-  ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    NotEmpty({ it.isNotEmpty() }),
    // CallChain[size=7] = QOnlyIfStr.NotBlank <-[Propag]- QOnlyIfStr.MultilineOrVeryLongLine <-[Call]-  ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    NotBlank({ it.isNotBlank() }),
    // CallChain[size=7] = QOnlyIfStr.Always <-[Propag]- QOnlyIfStr.MultilineOrVeryLongLine <-[Call]- QB ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    Always({ true })
}

// CallChain[size=6] = String.qWithNewLinePrefix() <-[Call]- QBracketKV.toString() <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun String.qWithNewLinePrefix(
    numNewLine: Int = 1,
    onlyIf: QOnlyIfStr = QOnlyIfStr.Multiline,
    lineSeparator: QLineSeparator = QLineSeparator.LF,
): String {
    if (!onlyIf.matches(this)) return this

    val nCount = takeWhile { it == '\n' || it == '\r' }.count()

    return lineSeparator.value.repeat(numNewLine) + substring(nCount)
}

// CallChain[size=13] = String.qWithNewLineSuffix() <-[Call]- qArrow() <-[Call]- QLogStyle.qLogArrow ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun String.qWithNewLineSuffix(numNewLine: Int = 1, onlyIf: QOnlyIfStr = QOnlyIfStr.Multiline): String {
    if (!onlyIf.matches(this)) return this

    val nCount = takeLastWhile { it == '\n' || it == '\r' }.count()

    return substring(0, length - nCount) + "\n".repeat(numNewLine)
}

// CallChain[size=6] = String.qStartsWithNewLine() <-[Call]- QBracketKV.toString() <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun String.qStartsWithNewLine(): Boolean {
    return this.isNotEmpty() && (this[0] == '\n' || this[0] == '\r')
}

// CallChain[size=13] = String.qWithNewLineSurround() <-[Call]- qArrow() <-[Call]- QLogStyle.qLogArr ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun String.qWithNewLineSurround(numNewLine: Int = 1, onlyIf: QOnlyIfStr = QOnlyIfStr.Multiline): String {
    if (!onlyIf.matches(this)) return this

    return qWithNewLinePrefix(numNewLine, QOnlyIfStr.Always).qWithNewLineSuffix(numNewLine, QOnlyIfStr.Always)
}

// CallChain[size=9] = String.qWithSpacePrefix() <-[Call]- QException.qToString() <-[Call]- QExcepti ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun String.qWithSpacePrefix(numSpace: Int = 1, onlyIf: QOnlyIfStr = QOnlyIfStr.SingleLine): String {
    if (!onlyIf.matches(this)) return this

    return " ".repeat(numSpace) + this.trimStart()
}

// CallChain[size=11] = CharSequence.qEndsWith() <-[Call]- QFetchRule.SMART_FETCH <-[Call]- qLogStac ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun CharSequence.qEndsWith(suffix: Regex, length: Int = 100): Boolean {
    return takeLast(min(length, this.length)).matches(suffix)
}

// CallChain[size=6] = CharSequence.qIsMultiLine() <-[Call]- CharSequence.qIsSingleLine() <-[Call]-  ... .qColor() <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun CharSequence.qIsMultiLine(): Boolean {
    return this.contains("\n") || this.contains("\r")
}

// CallChain[size=6] = CharSequence.qIsMultiLineOrVeryLongLine() <-[Call]- List<String>.qJoinStringN ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun CharSequence.qIsMultiLineOrVeryLongLine(threshold: Int = 150): Boolean {
    return this.qIsMultiLine() || this.qIsVeryLongLine(threshold = threshold)
}

// CallChain[size=7] = CharSequence.qIsVeryLongLine() <-[Call]- CharSequence.qIsMultiLineOrVeryLongL ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun CharSequence.qIsVeryLongLine(threshold: Int = 150): Boolean {
    return this.length > threshold
}

// CallChain[size=5] = CharSequence.qIsSingleLine() <-[Call]- String.qColor() <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun CharSequence.qIsSingleLine(): Boolean {
    return !this.qIsMultiLine()
}

// CallChain[size=7] = QLineSeparator <-[Ref]- String.qWithNewLinePrefix() <-[Call]- QBracketKV.toSt ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private enum class QLineSeparator(val value: String) {
    // CallChain[size=7] = QLineSeparator.LF <-[Call]- String.qWithNewLinePrefix() <-[Call]- QBracketKV. ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    LF("\n"),
    // CallChain[size=8] = QLineSeparator.CRLF <-[Propag]- QLineSeparator.QLineSeparator() <-[Call]- Str ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    CRLF("\r\n"),
    // CallChain[size=8] = QLineSeparator.CR <-[Propag]- QLineSeparator.QLineSeparator() <-[Call]- Strin ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    CR("\r");

    companion object {
        // CallChain[size=14] = QLineSeparator.DEFAULT <-[Call]- Path.qLineSeparator() <-[Call]- Path.qFetch ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        val DEFAULT = QLineSeparator.LF
    }
}

// CallChain[size=11] = CharSequence.qSubstring() <-[Call]- String.qMoveLeft() <-[Call]- QLineMatchR ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun CharSequence.qSubstring(rangeBothInclusive: IntRange): String =
    substring(rangeBothInclusive.first, rangeBothInclusive.last + 1)

// CallChain[size=11] = CharSequence.qCountLeftSpace() <-[Call]- QFetchRule.SMART_FETCH <-[Call]- qL ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun CharSequence.qCountLeftSpace(): Int = takeWhile { it == ' ' }.count()

// CallChain[size=11] = CharSequence.qCountRightSpace() <-[Call]- String.qMoveCenter() <-[Call]- QLi ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun CharSequence.qCountRightSpace(): Int = takeLastWhile { it == ' ' }.count()

// CallChain[size=4] = qMASK_LENGTH_LIMIT <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private const val qMASK_LENGTH_LIMIT: Int = 100_000

// CallChain[size=6] = QToString <-[Ref]- qToStringRegistry <-[Call]- Any.qToString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private class QToString(val okToApply: (Any) -> Boolean, val toString: (Any) -> String)

// CallChain[size=5] = qToStringRegistry <-[Call]- Any.qToString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private val qToStringRegistry: MutableList<QToString> by lazy {
    val toStrings =
        QMyToString::class.qFunctions(
            QMFunc.nameExact("qToString") and
//                            QMFunc.returnType(String::class, false) and
//                            QMFunc.NoParams and
                    QMFunc.DeclaredOnly and
                    QMFunc.IncludeExtensionsInClass
        )

    toStrings.map { func ->
        QToString(
            okToApply = { value ->
                func.extensionReceiverParameter?.type?.qIsSuperclassOf(value::class) ?: false
            },
            toString = { value ->
                func.call(QMyToString, value) as String
            }
        )
    }.toMutableList()
}

// CallChain[size=4] = Any.qToString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun Any?.qToString(): String {
    if (this == null)
        return "null".light_gray

    for (r in qToStringRegistry) {
        if (r.okToApply(this)) {
            return r.toString(this)
        }
    }

    return toString()
}

// CallChain[size=3] = Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun Any?.qToLogString(maxLineLength: Int = 80): String {
    if (QMyLog.no_format) {
        return this.toString()
    }

    if (this == null)
        return "null".light_gray

    val str = this.qToString()

    if (str.isBlank())
        return str.qClarifyEmptyOrBlank()

    val isListOrArray =
        this !is CharSequence && (str.startsWith("[") && str.endsWith("]")) || (str.startsWith("{") && str.endsWith("}"))
    val isMultiline = isListOrArray && str.qIsMultiLine()
    val isNestedListOrArray = isListOrArray && str.startsWith("[[")

    val comma = ",".light_gray
    val separator = "----".light_gray

    return if (isNestedListOrArray) { // Nested list always add line breaks for consistent formatting.
        val str2 = str.replaceRange(1, 1, "\n")

        val masked = str2.replaceRange(str.length, str.length, "\n").qMask(
            QMask.INNER_BRACKETS
        )

        masked.replaceAndUnmask(", ".re, "$comma\n").trim()
    } else if (isListOrArray && (maxLineLength < str.length || isMultiline) && str.length < qMASK_LENGTH_LIMIT) { // qMask is slow, needs limit length
        val str2 = str.replaceRange(1, 1, "\n")

        val masked = str2.replaceRange(str.length, str.length, "\n").qMask(
            QMask.PARENS,
            QMask.KOTLIN_STRING
        )

        masked.replaceAndUnmask(", ".re, if (isMultiline) "\n$separator\n" else "$comma\n").trim()
    } else {
        str //.trim()
    }.qClarifyEmptyOrBlank()
}

// CallChain[size=4] = String.qClarifyEmptyOrBlank() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun String.qClarifyEmptyOrBlank(): String {
    return if (this.isEmpty()) {
        "(EMPTY STRING)".qColor(QColor.LightGray)
    } else if (this.isBlank()) {
        if (this == "\n" || this == "\r\n") {
            "$this(LINE BREAK)".qColor(QColor.LightGray)
        } else {
            "$this(BLANK STRING)".qColor(QColor.LightGray)
        }
    } else {
        this
    }
}

// CallChain[size=13] = Char.qIsWhitespace() <-[Call]- QSequenceReader.detectSpace() <-[Propag]- QSe ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun Char.qIsWhitespace(allowLinebreak: Boolean = false): Boolean {
    return if (this.isWhitespace()) {
        allowLinebreak || (this != '\n' && this != '\r')
    } else {
        false
    }
}

// CallChain[size=9] = String.qCamelCaseToSpaceSeparated() <-[Call]- QException.qToString() <-[Call] ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun String.qCamelCaseToSpaceSeparated(toLowerCase: Boolean = false): String {
    return this.replace("([a-z])([A-Z]+)".re, "$1 $2")
        .replace("([A-Z]+)([A-Z][a-z])".re, "$1 $2").apply {
            if( toLowerCase) {
                lowercase(Locale.ENGLISH)
            }
        }
}

// CallChain[size=5] = List<String>.qJoinStringNicely() <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun List<String>.qJoinStringNicely(separatorMultiline: String = "\n", separatorSingleLine: String = "  "): String {
    return qMap { item, idx, pos, previous, next ->
        when (pos) {
            QLoopPos.OnlyOneItem, QLoopPos.Last ->
                return@qMap item
            QLoopPos.First, QLoopPos.Middle -> {
                // Second Item, First Item ends with new line

                if( next!!.qIsMultiLineOrVeryLongLine() || item.qIsMultiLineOrVeryLongLine() ) {
                    item + separatorMultiline
                } else {
                    item + separatorSingleLine
                }
            }

        }
    }.joinToString("")
}

// CallChain[size=14] = qNow <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThreadLoca ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private val qNow: Long
    get() = System.currentTimeMillis()

// CallChain[size=5] = QTimeItResult <-[Ref]- qTimeIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private class QTimeItResult<T>(val label: String, val time: Long, val result: T) {
    // CallChain[size=5] = QTimeItResult.toString() <-[Call]- qTimeIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun toString(): String {
        return qBrackets(label, time.qFormatDuration())
    }
}

// CallChain[size=4] = qTimeIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
@OptIn(ExperimentalContracts::class)
private inline fun <T> qTimeIt(label: String = qThisSrcLineSignature, quiet: Boolean = false, out: QOut? = QOut.CONSOLE, block: () -> T): QTimeItResult<T> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val start = System.nanoTime()

    val blockResult = block()

    val time = System.nanoTime() - start

    val result = QTimeItResult(label, time, blockResult)

    if (!quiet)
        out?.println(result.toString())

    return result
}

// CallChain[size=20] = QLazyTreeNode <-[Ref]- N.children <-[Call]- N.depthFirstRecursive() <-[Call] ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
/**
 * Represents a node in a tree data structure that can fill its child nodes.
 *
 * When the [fillTree] function of the root node is called, it invokes the [fillChildNodes] function
 * of descendant nodes in a breadth-first order to fill the child nodes.
 */
private interface QLazyTreeNode<V : Any?> : IQTreeNode<V> {
    // CallChain[size=20] = QLazyTreeNode.hasChildNodesToFill() <-[Call]- N.children <-[Call]- N.depthFi ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    /**
     * Checks if the node has child nodes that need to be filled.
     */
    fun hasChildNodesToFill(): Boolean

    // CallChain[size=20] = QLazyTreeNode.fillChildNodes() <-[Call]- N.children <-[Call]- N.depthFirstRe ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    /**
     * Fills and returns the child nodes of the current node.
     */
    fun fillChildNodes(): List<QLazyTreeNode<V>>
}

// CallChain[size=18] = IQTreeNode <-[Ref]- N.walkDescendants() <-[Call]- Path.qSeq() <-[Call]- Path ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
/**
 * Represents a node in a tree data structure. [value] can be of any type, but within a single tree,
 * the type of [value] needs to be consistent.
 */
private interface IQTreeNode<V : Any?> {
    // CallChain[size=19] = IQTreeNode.value <-[Propag]- IQTreeNode <-[Ref]- N.walkDescendants() <-[Call ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val value: V

    // CallChain[size=19] = IQTreeNode.toTreeNodeString() <-[Propag]- IQTreeNode <-[Ref]- N.walkDescenda ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun toTreeNodeString(): String {
        return value.toString()
    }
}

// CallChain[size=20] = N.parent <-[Call]- N.depthFrom() <-[Call]- N.depthFirst() <-[Call]- N.walkDe ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private var <N : IQTreeNode<*>> N.parent: N?
    get() =
        this.qGetExPropOrNull("#parent") as N?
    set(value) {
        val oldParent = this.parent
        if (oldParent != null) {
            val oldChildren = oldParent.children
            (oldChildren as MutableList<N>).remove(this)
        }

        if (value != null && !value.children.contains(this)) {
            (value.children as MutableList<N>).add(this)
        }

        this.qSetExProp("#parent", value)
    }

// CallChain[size=19] = N.children <-[Call]- N.depthFirstRecursive() <-[Call]- N.walkDescendants() < ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
/**
 * Obtain the child nodes of this node.
 */
private val <N : IQTreeNode<*>> N.children: List<N>
    get() {
        var result = this.qGetExPropOrNull("#children")

        if (result == null) {
            val newChildren = if (this is QLazyTreeNode<*> && this.hasChildNodesToFill()) {
                val filled = this.fillChildNodes().toMutableList() as List<N>
                filled.forEach {
                    it.qSetExProp("#parent", this)
                }
                filled
            } else {
                mutableListOf<N>()
            }

            this.qSetExProp("#children", newChildren)

            result = newChildren
        }

        return result as List<N>
    }

// CallChain[size=19] = N.depthFrom() <-[Call]- N.depthFirst() <-[Call]- N.walkDescendants() <-[Call ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun <N : IQTreeNode<*>> N.depthFrom(root: IQTreeNode<*>): Int {
    var count = 1
    var parent = this.parent

    while (parent !== root) {
        count++

        if (parent == null) {
            return -1
        }

        parent = parent.parent
    }

    return count
//    return ancestorsList().size
//    return ancestorsSeq().count()
}

// CallChain[size=17] = N.walkDescendants() <-[Call]- Path.qSeq() <-[Call]- Path.qList() <-[Call]- P ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
/**
 * It traverses the descendant nodes in the order specified by the [algorithm].
 * The return value is of type [Sequence].
 */
private fun <N : IQTreeNode<*>> N.walkDescendants(
    algorithm: QWalkAlgo = QWalkAlgo.BreadthFirst,
    maxDepth: Int = Int.MAX_VALUE,
    includeSelf: Boolean = true,
    filter: (N) -> Boolean = { true }
): Sequence<N> {
    return when (algorithm) {
        QWalkAlgo.BreadthFirst -> breadthFirst(maxDepth = maxDepth, includeSelf = includeSelf, filter = filter)
        QWalkAlgo.DepthFirst -> depthFirst(maxDepth = maxDepth, includeSelf = includeSelf, filter = filter)
        QWalkAlgo.DepthFirstRecursive -> depthFirstRecursive(
            maxDepth = maxDepth,
            includeSelf = includeSelf,
            filter = filter
        )
    }
}

// CallChain[size=19] = N.mark() <-[Call]- N.depthFirstRecursive() <-[Call]- N.walkDescendants() <-[ ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun <N : IQTreeNode<*>> N.mark(marked: HashSet<N>) {
    marked += this
}

// CallChain[size=19] = N.isMarked() <-[Call]- N.depthFirstRecursive() <-[Call]- N.walkDescendants() ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun <N : IQTreeNode<*>> N.isMarked(marked: HashSet<N>): Boolean =
    marked.contains(this)

// CallChain[size=18] = N.breadthFirst() <-[Call]- N.walkDescendants() <-[Call]- Path.qSeq() <-[Call ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun <N : IQTreeNode<*>> N.breadthFirst(
    maxDepth: Int = Int.MAX_VALUE,
    includeSelf: Boolean = true,
    filter: (N) -> Boolean = { true }
): Sequence<N> = sequence {
    val check: HashSet<N> = HashSet()

    var depth = 0

    var curNodes = mutableListOf<N>()
    curNodes += this@breadthFirst
    var nextDepthNodes = mutableListOf<N>()

    while (curNodes.isNotEmpty()) {
        for (node in curNodes) {
            if (node.isMarked(check)) {
                // already visited
                continue
            }

            val okToAdd = filter(node)

            if (includeSelf || node !== this@breadthFirst) {
                if (okToAdd)
                    yield(node)
            }

            node.mark(check)

            if (okToAdd)
                nextDepthNodes += node.children
        }

        curNodes = nextDepthNodes
        nextDepthNodes = mutableListOf()

        depth++

        if (depth > maxDepth) {
            break
        }
    }
}

// CallChain[size=18] = N.depthFirstRecursive() <-[Call]- N.walkDescendants() <-[Call]- Path.qSeq()  ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun <N : IQTreeNode<*>> N.depthFirstRecursive(
    maxDepth: Int = Int.MAX_VALUE,
    includeSelf: Boolean = true,
    filter: (N) -> Boolean = { true },
    check: HashSet<N> = HashSet(),
    curDepth: Int = 0
): Sequence<N> = sequence {
    if (curDepth > maxDepth) {
        return@sequence
    }

    val thisNode = this@depthFirstRecursive

    thisNode.mark(check)

    if (includeSelf && filter(thisNode)) {
        yield(thisNode)
    }

    for (node in thisNode.children) {
        if (!filter(node))
            continue

        if (node.isMarked(check)) {
            // already visited
            continue
        }

        node.mark(check)

        // recursive call
        yieldAll(node.depthFirstRecursive(maxDepth, true, filter, check, curDepth + 1))
    }
}

// CallChain[size=18] = N.depthFirst() <-[Call]- N.walkDescendants() <-[Call]- Path.qSeq() <-[Call]- ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun <N : IQTreeNode<*>> N.depthFirst(
    maxDepth: Int = Int.MAX_VALUE,
    raiseExceptionIfCyclic: Boolean = false,
    includeSelf: Boolean = true,
    filter: (N) -> Boolean = { true }
): Sequence<N> = sequence {
    val check: HashSet<N> = HashSet()

    val stack = mutableListOf<N>()

    stack += this@depthFirst

    this@depthFirst.mark(check)

    while (stack.isNotEmpty()) {
        val node = stack.removeAt(stack.size - 1)

        val okToAdd = filter(node)

        if (includeSelf || node !== this@depthFirst) {
            if (okToAdd)
                yield(node)
        }

        for (n in node.children.reversed()) {
            if (n.isMarked(check)) {
                if (raiseExceptionIfCyclic) {
                    QE.CycleDetected.throwIt(n)
                }

                // already visited
                continue
            }

            if (n.depthFrom(this@depthFirst) <= maxDepth) {
                stack += n
                node.mark(check)
            }
        }
    }
}

// CallChain[size=17] = QWalkAlgo <-[Ref]- Path.qSeq() <-[Call]- Path.qList() <-[Call]- Path.qFind() ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private enum class QWalkAlgo {
    // CallChain[size=17] = QWalkAlgo.BreadthFirst <-[Call]- Path.qSeq() <-[Call]- Path.qList() <-[Call] ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    BreadthFirst,
    // CallChain[size=18] = QWalkAlgo.DepthFirst <-[Propag]- QWalkAlgo.BreadthFirst <-[Call]- Path.qSeq( ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    DepthFirst,
    // CallChain[size=18] = QWalkAlgo.DepthFirstRecursive <-[Propag]- QWalkAlgo.BreadthFirst <-[Call]- P ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    DepthFirstRecursive
}


// ================================================================================
// endregion Src from Non-Root API Files -- Auto Generated by nyab.conf.QCompactLib.kt