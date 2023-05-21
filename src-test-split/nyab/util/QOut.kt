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

import java.nio.charset.Charset
import java.nio.file.Path

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=10] = QOut <-[Ref]- QLogStyle <-[Ref]- QLogStyle.SRC_AND_STACK <-[Call]- QExceptio ... ckets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nestedColor()[Root]
interface QOut {
    // CallChain[size=12] = QOut.isAcceptColoredText <-[Propag]- QOut.CONSOLE <-[Call]- QMyLog.out <-[Ca ... ckets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nestedColor()[Root]
    val isAcceptColoredText: Boolean

    // CallChain[size=12] = QOut.print() <-[Propag]- QOut.CONSOLE <-[Call]- QMyLog.out <-[Call]- QLogSty ... ckets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nestedColor()[Root]
    fun print(msg: Any? = "")

    // CallChain[size=12] = QOut.println() <-[Propag]- QOut.CONSOLE <-[Call]- QMyLog.out <-[Call]- QLogS ... ckets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nestedColor()[Root]
    fun println(msg: Any? = "")

    // CallChain[size=12] = QOut.close() <-[Propag]- QOut.CONSOLE <-[Call]- QMyLog.out <-[Call]- QLogSty ... ckets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nestedColor()[Root]
    fun close()

    companion object {
        // CallChain[size=11] = QOut.CONSOLE <-[Call]- QMyLog.out <-[Call]- QLogStyle <-[Ref]- QLogStyle.SRC ... ckets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nestedColor()[Root]
        val CONSOLE: QOut = QConsole(true)

        
    }
}

// CallChain[size=12] = QConsole <-[Call]- QOut.CONSOLE <-[Call]- QMyLog.out <-[Call]- QLogStyle <-[ ... ckets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nestedColor()[Root]
private class QConsole(override val isAcceptColoredText: Boolean) : QOut {
    // CallChain[size=13] = QConsole.print() <-[Propag]- QConsole <-[Call]- QOut.CONSOLE <-[Call]- QMyLo ... ckets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nestedColor()[Root]
    override fun print(msg: Any?) {
        if (isAcceptColoredText) {
            kotlin.io.print(msg.toString())
        } else {
            kotlin.io.print(msg.toString().noColor)
        }
    }

    // CallChain[size=13] = QConsole.println() <-[Propag]- QConsole <-[Call]- QOut.CONSOLE <-[Call]- QMy ... ckets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nestedColor()[Root]
    override fun println(msg: Any?) {
        kotlin.io.println(msg.toString())
    }

    // CallChain[size=13] = QConsole.close() <-[Propag]- QConsole <-[Call]- QOut.CONSOLE <-[Call]- QMyLo ... ckets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nestedColor()[Root]
    override fun close() {
        // Do nothing
    }
}

// CallChain[size=3] = QOut.separator() <-[Call]- qTest() <-[Call]- main()[Root]
internal fun QOut.separator(start: String = "\n", end: String = "\n") {
    this.println(qSeparator(start = start, end = end))
}