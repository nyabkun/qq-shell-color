// 2023. nyabkun  MIT LICENSE

package nyab.conf

import nyab.util.QOut

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=11] = QMyLog <-[Ref]- QLogStyle <-[Ref]- QLogStyle.SRC_AND_STACK <-[Call]- QExcept ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal object QMyLog {
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