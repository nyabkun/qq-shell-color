// 2023. nyabkun  MIT LICENSE

package nyab.util

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=3] = CharSequence.qIsMultiLine() <-[Call]- CharSequence.qIsSingleLine() <-[Call]- String.qColor()[Root]
internal fun CharSequence.qIsMultiLine(): Boolean {
    return this.contains("\n") || this.contains("\r")
}

// CallChain[size=2] = CharSequence.qIsSingleLine() <-[Call]- String.qColor()[Root]
internal fun CharSequence.qIsSingleLine(): Boolean {
    return !this.qIsMultiLine()
}