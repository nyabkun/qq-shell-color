// 2023. nyabkun  MIT LICENSE

package nyab.conf

import nyab.util.yellow

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=7] = QMyMark <-[Ref]- QException.QException() <-[Ref]- QE.throwItBrackets() <-[Cal ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
@Suppress("MayBeConstant")
internal object QMyMark {
    // CallChain[size=4] = QMyMark.test_method <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    val test_method = "☕".yellow
    // CallChain[size=7] = QMyMark.warn <-[Call]- QException.QException() <-[Ref]- QE.throwItBrackets()  ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val warn = "⚠".yellow
    // CallChain[size=3] = QMyMark.test_start <-[Call]- qTest() <-[Call]- main()[Root]
    val test_start = "☘".yellow
    
}