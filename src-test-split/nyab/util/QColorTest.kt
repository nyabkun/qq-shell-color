// 2023. nyabkun  MIT LICENSE

package nyab.util

import nyab.test.QTest
import nyab.test.QTestHumanCheckRequired
import nyab.test.qTest
import nyab.test.shouldBe

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

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