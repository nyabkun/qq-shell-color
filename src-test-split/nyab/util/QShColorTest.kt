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

import nyab.test.QTest
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
class QShColorTest {

    // << Root of the CallChain >>
    @QTest
    fun nestRedBlue() {
        """red+blue""".red.blue.qColorAndDecoDebug() shouldBe
                "[Blue][Red]red+blue[End]"
    }

    // << Root of the CallChain >>
    @QTest
    fun fgAndBg() {
        """fg + bg""".qColor(QShColor.Red, QShColor.LightGray).qColorAndDecoDebug() shouldBe
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
        ("c".yellow + "o".blue + "l".red + "o".magenta + "u".green + "r".cyan + "f".yellow + "u".blue + "l".red).qColorAndDecoDebug() shouldBe """
            [Yellow]c[End][Blue]o[End][Red]l[End][Magenta]o[End][Green]u[End][Cyan]r[End][Yellow]f[End][Blue]u[End][Red]l[End]
        """.trimIndent()
    }

    // << Root of the CallChain >>
    @QTest
    fun background() {
        "GreenBG".qColor(fg = null, bg = QShColor.Green, false).qColorAndDecoDebug() shouldBe """
            [Green_BG]GreenBG[End]
        """.trimIndent()
    }

    // << Root of the CallChain >>
    @QTest
    fun foregroundAndBackground() {
        "RedFG_YellowBG".qColor(fg = QShColor.Red, bg = QShColor.Yellow, false).qColorAndDecoDebug() shouldBe """
            [Yellow_BG][Red]RedFG_YellowBG[End][End]
        """.trimIndent()
    }

    // << Root of the CallChain >>
    @QTest
    fun multiline() {
        "abc\ndef\nhij".qColor(fg = QShColor.Red, bg = null).qColorAndDecoDebug() shouldBe """
            [Red]abc[End]
            [Red]def[End]
            [Red]hij[End]
        """.trimIndent()

        "abc\ndef\nhij".qColor(fg = null, bg = QShColor.Blue).qColorAndDecoDebug() shouldBe """
            [Blue_BG]abc[End]
            [Blue_BG]def[End]
            [Blue_BG]hij[End]
        """.trimIndent()
    }

    // << Root of the CallChain >>
    @QTest
    fun multiline_fg_bg() {
        "abc\ndef\nhij".qColor(fg = QShColor.Red, bg = QShColor.Blue).qColorAndDecoDebug() shouldBe """
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
            fg = QShColor.Magenta
        ).qColorTarget(
            ptn = """".*?"""".toRegex(),
            fg = QShColor.Green
        ).qColorAndDecoDebug() shouldBe """
            [Magenta]val[End] color = [Green]"green"[End]
        """.trimIndent()
    }

    // << Root of the CallChain >>
    @QTest
    fun noColor() {
        """val color = "text"""".qColorTarget(
            ptn = """val""".toRegex(),
            fg = QShColor.Magenta
        ).qColorTarget(
            ptn = """".*?"""".toRegex(),
            fg = QShColor.Green
        ).noColor.qColorAndDecoDebug() shouldBe """val color = "text""""
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