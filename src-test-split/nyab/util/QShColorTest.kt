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
    fun colourful() {
        ("c".yellow + "o".blue + "l".red + "o".magenta + "u".green + "r".cyan + "f".yellow + "u".blue + "l".red).qColorDebug() shouldBe """
            [YELLOW]c[END][BLUE]o[END][RED]l[END][MAGENTA]o[END][GREEN]u[END][CYAN]r[END][YELLOW]f[END][BLUE]u[END][RED]l[END]
        """.trimIndent()
    }

    // << Root of the CallChain >>
    @QTest
    fun background() {
        "GreenBG".qColor(fg = null, bg = QShColor.GREEN, false).qColorDebug() shouldBe """
            [GREEN_BG]GreenBG[END]
        """.trimIndent()
    }

    // << Root of the CallChain >>
    @QTest
    fun foregroundAndBackground() {
        "RedFG_YellowBG".qColor(fg = QShColor.RED, bg = QShColor.YELLOW, false).qColorDebug() shouldBe """
            [YELLOW_BG][RED]RedFG_YellowBG[END][END]
        """.trimIndent()
    }

    // << Root of the CallChain >>
    @QTest
    fun multiline() {
        "abc\ndef\nhij".qColor(fg = QShColor.RED, bg = null).qColorDebug() shouldBe """
            [RED]abc[END]
            [RED]def[END]
            [RED]hij[END]
        """.trimIndent()

        "abc\ndef\nhij".qColor(fg = null, bg = QShColor.BLUE).qColorDebug() shouldBe """
            [BLUE_BG]abc[END]
            [BLUE_BG]def[END]
            [BLUE_BG]hij[END]
        """.trimIndent()
    }

    // << Root of the CallChain >>
    @QTest
    fun multiline_fg_bg() {
        "abc\ndef\nhij".qColor(fg = QShColor.RED, bg = QShColor.BLUE).qColorDebug() shouldBe """
            [BLUE_BG][RED]abc[END][END]
            [BLUE_BG][RED]def[END][END]
            [BLUE_BG][RED]hij[END][END]
        """.trimIndent()
    }

    // << Root of the CallChain >>
    @QTest
    fun colorTarget() {
        """val color = "green"""".qColorTarget(
            ptn = """val""".toRegex(),
            color = QShColor.MAGENTA
        ).qColorTarget(
            ptn = """".*?"""".toRegex(),
            color = QShColor.GREEN
        ).qColorDebug() shouldBe """
            [MAGENTA]val[END] color = [GREEN]"green"[END]
        """.trimIndent()
    }

    // << Root of the CallChain >>
    @QTest
    fun noColor() {
        """val color = "text"""".qColorTarget(
            ptn = """val""".toRegex(),
            color = QShColor.MAGENTA
        ).qColorTarget(
            ptn = """".*?"""".toRegex(),
            color = QShColor.GREEN
        ).noColor.qColorDebug() shouldBe """val color = "text""""
    }

    // << Root of the CallChain >>
    @QTest
    fun nestedColor() {
        "${"🚀".red} Test Start ${"🚀".red}\nTestClass".blue.qColorDebug() shouldBe """
            [RED]🚀[END][BLUE] Test Start [END][RED]🚀[END]
            [BLUE]TestClass[END]
        """.trimIndent()
    }
}