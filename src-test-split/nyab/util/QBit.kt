// 2023. nyabkun  MIT LICENSE

package nyab.util

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=3] = Char.qToHex() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun Char.qToHex(): String = String.format("%02X", code)