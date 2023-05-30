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

import kotlin.math.min
import nyab.conf.QE

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=6] = QOnlyIfStr <-[Ref]- QMaskResult.toString() <-[Propag]- QMaskResult <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
internal enum class QOnlyIfStr(val matches: (String) -> Boolean) {
    // CallChain[size=6] = QOnlyIfStr.Multiline <-[Call]- QMaskResult.toString() <-[Propag]- QMaskResult <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    Multiline({ it.qIsMultiLine() }),
    // CallChain[size=7] = QOnlyIfStr.SingleLine <-[Propag]- QOnlyIfStr.Multiline <-[Call]- QMaskResult. ... <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    SingleLine({ it.qIsSingleLine() }),
    // CallChain[size=7] = QOnlyIfStr.Empty <-[Propag]- QOnlyIfStr.Multiline <-[Call]- QMaskResult.toStr ... <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    Empty({ it.isEmpty() }),
    // CallChain[size=7] = QOnlyIfStr.Blank <-[Propag]- QOnlyIfStr.Multiline <-[Call]- QMaskResult.toStr ... <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    Blank({ it.isBlank() }),
    // CallChain[size=7] = QOnlyIfStr.NotEmpty <-[Propag]- QOnlyIfStr.Multiline <-[Call]- QMaskResult.to ... <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    NotEmpty({ it.isNotEmpty() }),
    // CallChain[size=7] = QOnlyIfStr.NotBlank <-[Propag]- QOnlyIfStr.Multiline <-[Call]- QMaskResult.to ... <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    NotBlank({ it.isNotBlank() }),
    // CallChain[size=7] = QOnlyIfStr.Always <-[Propag]- QOnlyIfStr.Multiline <-[Call]- QMaskResult.toSt ... <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    Always({ true })
}

// CallChain[size=7] = String.qWithNewLinePrefix() <-[Call]- String.qWithNewLineSurround() <-[Call]- ... <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
internal fun String.qWithNewLinePrefix(
        numNewLine: Int = 1,
        onlyIf: QOnlyIfStr = QOnlyIfStr.Multiline,
        lineSeparator: QLineSeparator = QLineSeparator.LF,
): String {
    if (!onlyIf.matches(this)) return this

    val nCount = takeWhile { it == '\n' || it == '\r' }.count()

    return lineSeparator.value.repeat(numNewLine) + substring(nCount)
}

// CallChain[size=7] = String.qWithNewLineSuffix() <-[Call]- String.qWithNewLineSurround() <-[Call]- ... <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
internal fun String.qWithNewLineSuffix(numNewLine: Int = 1, onlyIf: QOnlyIfStr = QOnlyIfStr.Multiline): String {
    if (!onlyIf.matches(this)) return this

    val nCount = takeLastWhile { it == '\n' || it == '\r' }.count()

    return substring(0, length - nCount) + "\n".repeat(numNewLine)
}

// CallChain[size=6] = String.qWithNewLineSurround() <-[Call]- QMaskResult.toString() <-[Propag]- QM ... <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
internal fun String.qWithNewLineSurround(numNewLine: Int = 1, onlyIf: QOnlyIfStr = QOnlyIfStr.Multiline): String {
    if (!onlyIf.matches(this)) return this

    return qWithNewLinePrefix(numNewLine, QOnlyIfStr.Always).qWithNewLineSuffix(numNewLine, QOnlyIfStr.Always)
}

// CallChain[size=3] = String.qIsMultiLine() <-[Call]- String.qIsSingleLine() <-[Call]- String.qColor()[Root]
internal fun String.qIsMultiLine(): Boolean {
    return this.contains("\n") || this.contains("\r")
}

// CallChain[size=2] = String.qIsSingleLine() <-[Call]- String.qColor()[Root]
internal fun String.qIsSingleLine(): Boolean {
    return !this.qIsMultiLine()
}

// CallChain[size=8] = QLineSeparator <-[Ref]- String.qWithNewLinePrefix() <-[Call]- String.qWithNew ... <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
internal enum class QLineSeparator(val value: String) {
    // CallChain[size=8] = QLineSeparator.LF <-[Call]- String.qWithNewLinePrefix() <-[Call]- String.qWit ... <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    LF("\n"),
    // CallChain[size=9] = QLineSeparator.CRLF <-[Propag]- QLineSeparator.QLineSeparator() <-[Call]- Str ... <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    CRLF("\r\n"),
    // CallChain[size=9] = QLineSeparator.CR <-[Propag]- QLineSeparator.QLineSeparator() <-[Call]- Strin ... <-[Ref]- QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- qMASK_COLORED[Root]
    CR("\r");

    companion object {
        
    }
}