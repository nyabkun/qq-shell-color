/*
 * Copyright 2023. nyabkun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package shcolor

import nyab.util.QShColor
import nyab.util.blue
import nyab.util.cyan
import nyab.util.green
import nyab.util.magenta
import nyab.util.qColor
import nyab.util.qColorTarget
import nyab.util.red
import nyab.util.yellow

fun main() {
    var txt = "c".yellow + "o".blue + "l".red + "o".magenta + "u".green + "r".cyan + "f".yellow + "u".blue + "l".red

    println(txt)

    txt = "you can set background".qColor(fg = QShColor.RED, bg = QShColor.BLUE)

    println(txt)

    txt = """val color = "you can use regex to color targeted text"""".qColorTarget(
        ptn = """val(?!\S)""".toRegex(),
        color = QShColor.MAGENTA
    ).qColorTarget(
        ptn = """".*?"""".toRegex(),
        color = QShColor.GREEN
    )

    println(txt)
}
