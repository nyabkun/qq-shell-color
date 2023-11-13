// 2023. nyabkun  MIT LICENSE

package nyab.util

import java.nio.charset.Charset
import java.nio.file.Path

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=11] = QOut <-[Ref]- QLogStyle <-[Ref]- QLogStyle.SRC_AND_STACK <-[Call]- QExceptio ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal interface QOut {
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
internal fun QOut.separator(start: String = "\n", end: String = "\n") {
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