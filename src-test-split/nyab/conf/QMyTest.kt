// 2023. nyabkun  MIT LICENSE

package nyab.conf

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=4] = QMyTest <-[Ref]- qOkToTest() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal object QMyTest {
    // CallChain[size=4] = QMyTest.forceTestMode <-[Call]- qOkToTest() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    const val forceTestMode = true
    
}