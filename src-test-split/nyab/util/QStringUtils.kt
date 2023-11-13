// 2023. nyabkun  MIT LICENSE

package nyab.util

import java.nio.charset.Charset
import java.util.Locale
import kotlin.math.min
import kotlin.reflect.full.extensionReceiverParameter
import kotlin.reflect.full.isSuperclassOf
import nyab.conf.QE
import nyab.conf.QMyLog
import nyab.conf.QMyToString
import nyab.match.QMFunc
import nyab.match.and
import org.jetbrains.kotlin.utils.addToStdlib.applyIf

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=6] = QLR <-[Ref]- String.qWithMinAndMaxLength() <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
internal enum class QLR {
    // CallChain[size=8] = QLR.Left <-[Call]- String.qAlignCenter() <-[Call]- String.qWithMinLength() <- ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    Left,
    // CallChain[size=8] = QLR.Right <-[Call]- String.qAlignCenter() <-[Call]- String.qWithMinLength() < ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    Right
}

// CallChain[size=4] = qSeparator() <-[Call]- QOut.separator() <-[Call]- qTest() <-[Call]- main()[Root]
internal fun qSeparator(
    fg: QColor? = QColor.LightGray,
    bg: QColor? = null,
    char: Char = '⎯',
    length: Int = 80,
    start: String = "\n",
    end: String = "\n",
): String {
    return start + char.toString().repeat(length).qColor(fg, bg) + end
}

// CallChain[size=4] = qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
internal fun qSeparatorWithLabel(
    label: String,
    fg: QColor? = QColor.LightGray,
    bg: QColor? = null,
    char: Char = '⎯',
    length: Int = 70,
    start: String = "\n",
    end: String = "\n",
): String {
    return "$start$label  " + char.toString().repeat((length - label.length - 2).coerceAtLeast(0)).qColor(fg, bg)
        .qWithMinAndMaxLength(length, length, alignment = QAlign.LEFT, endDots = "") + end
}

// CallChain[size=6] = QOnlyIfStr <-[Ref]- QBracketKV.toString() <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal enum class QOnlyIfStr(val matches: (String) -> Boolean) {
    // CallChain[size=7] = QOnlyIfStr.Multiline <-[Propag]- QOnlyIfStr.MultilineOrVeryLongLine <-[Call]- ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    Multiline({ it.qIsMultiLine() }),
    // CallChain[size=6] = QOnlyIfStr.MultilineOrVeryLongLine <-[Call]- QBracketKV.toString() <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    MultilineOrVeryLongLine({ it.qIsMultiLine() || it.qIsVeryLongLine() }),
    // CallChain[size=7] = QOnlyIfStr.SingleLine <-[Propag]- QOnlyIfStr.MultilineOrVeryLongLine <-[Call] ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    SingleLine({ it.qIsSingleLine() }),
    // CallChain[size=7] = QOnlyIfStr.Empty <-[Propag]- QOnlyIfStr.MultilineOrVeryLongLine <-[Call]- QBr ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    Empty({ it.isEmpty() }),
    // CallChain[size=7] = QOnlyIfStr.Blank <-[Propag]- QOnlyIfStr.MultilineOrVeryLongLine <-[Call]- QBr ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    Blank({ it.isBlank() }),
    // CallChain[size=7] = QOnlyIfStr.NotEmpty <-[Propag]- QOnlyIfStr.MultilineOrVeryLongLine <-[Call]-  ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    NotEmpty({ it.isNotEmpty() }),
    // CallChain[size=7] = QOnlyIfStr.NotBlank <-[Propag]- QOnlyIfStr.MultilineOrVeryLongLine <-[Call]-  ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    NotBlank({ it.isNotBlank() }),
    // CallChain[size=7] = QOnlyIfStr.Always <-[Propag]- QOnlyIfStr.MultilineOrVeryLongLine <-[Call]- QB ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    Always({ true })
}

// CallChain[size=6] = String.qWithNewLinePrefix() <-[Call]- QBracketKV.toString() <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun String.qWithNewLinePrefix(
    numNewLine: Int = 1,
    onlyIf: QOnlyIfStr = QOnlyIfStr.Multiline,
    lineSeparator: QLineSeparator = QLineSeparator.LF,
): String {
    if (!onlyIf.matches(this)) return this

    val nCount = takeWhile { it == '\n' || it == '\r' }.count()

    return lineSeparator.value.repeat(numNewLine) + substring(nCount)
}

// CallChain[size=13] = String.qWithNewLineSuffix() <-[Call]- qArrow() <-[Call]- QLogStyle.qLogArrow ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun String.qWithNewLineSuffix(numNewLine: Int = 1, onlyIf: QOnlyIfStr = QOnlyIfStr.Multiline): String {
    if (!onlyIf.matches(this)) return this

    val nCount = takeLastWhile { it == '\n' || it == '\r' }.count()

    return substring(0, length - nCount) + "\n".repeat(numNewLine)
}

// CallChain[size=6] = String.qStartsWithNewLine() <-[Call]- QBracketKV.toString() <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun String.qStartsWithNewLine(): Boolean {
    return this.isNotEmpty() && (this[0] == '\n' || this[0] == '\r')
}

// CallChain[size=13] = String.qWithNewLineSurround() <-[Call]- qArrow() <-[Call]- QLogStyle.qLogArr ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun String.qWithNewLineSurround(numNewLine: Int = 1, onlyIf: QOnlyIfStr = QOnlyIfStr.Multiline): String {
    if (!onlyIf.matches(this)) return this

    return qWithNewLinePrefix(numNewLine, QOnlyIfStr.Always).qWithNewLineSuffix(numNewLine, QOnlyIfStr.Always)
}

// CallChain[size=9] = String.qWithSpacePrefix() <-[Call]- QException.qToString() <-[Call]- QExcepti ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun String.qWithSpacePrefix(numSpace: Int = 1, onlyIf: QOnlyIfStr = QOnlyIfStr.SingleLine): String {
    if (!onlyIf.matches(this)) return this

    return " ".repeat(numSpace) + this.trimStart()
}

// CallChain[size=11] = CharSequence.qEndsWith() <-[Call]- QFetchRule.SMART_FETCH <-[Call]- qLogStac ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun CharSequence.qEndsWith(suffix: Regex, length: Int = 100): Boolean {
    return takeLast(min(length, this.length)).matches(suffix)
}

// CallChain[size=6] = CharSequence.qIsMultiLine() <-[Call]- CharSequence.qIsSingleLine() <-[Call]-  ... .qColor() <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun CharSequence.qIsMultiLine(): Boolean {
    return this.contains("\n") || this.contains("\r")
}

// CallChain[size=6] = CharSequence.qIsMultiLineOrVeryLongLine() <-[Call]- List<String>.qJoinStringN ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun CharSequence.qIsMultiLineOrVeryLongLine(threshold: Int = 150): Boolean {
    return this.qIsMultiLine() || this.qIsVeryLongLine(threshold = threshold)
}

// CallChain[size=7] = CharSequence.qIsVeryLongLine() <-[Call]- CharSequence.qIsMultiLineOrVeryLongL ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun CharSequence.qIsVeryLongLine(threshold: Int = 150): Boolean {
    return this.length > threshold
}

// CallChain[size=5] = CharSequence.qIsSingleLine() <-[Call]- String.qColor() <-[Call]- String.light_green <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun CharSequence.qIsSingleLine(): Boolean {
    return !this.qIsMultiLine()
}

// CallChain[size=7] = QLineSeparator <-[Ref]- String.qWithNewLinePrefix() <-[Call]- QBracketKV.toSt ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal enum class QLineSeparator(val value: String) {
    // CallChain[size=7] = QLineSeparator.LF <-[Call]- String.qWithNewLinePrefix() <-[Call]- QBracketKV. ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    LF("\n"),
    // CallChain[size=8] = QLineSeparator.CRLF <-[Propag]- QLineSeparator.QLineSeparator() <-[Call]- Str ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    CRLF("\r\n"),
    // CallChain[size=8] = QLineSeparator.CR <-[Propag]- QLineSeparator.QLineSeparator() <-[Call]- Strin ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    CR("\r");

    companion object {
        // CallChain[size=14] = QLineSeparator.DEFAULT <-[Call]- Path.qLineSeparator() <-[Call]- Path.qFetch ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        val DEFAULT = QLineSeparator.LF
    }
}

// CallChain[size=11] = CharSequence.qSubstring() <-[Call]- String.qMoveLeft() <-[Call]- QLineMatchR ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
internal fun CharSequence.qSubstring(rangeBothInclusive: IntRange): String =
    substring(rangeBothInclusive.first, rangeBothInclusive.last + 1)

// CallChain[size=11] = CharSequence.qCountLeftSpace() <-[Call]- QFetchRule.SMART_FETCH <-[Call]- qL ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun CharSequence.qCountLeftSpace(): Int = takeWhile { it == ' ' }.count()

// CallChain[size=11] = CharSequence.qCountRightSpace() <-[Call]- String.qMoveCenter() <-[Call]- QLi ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
internal fun CharSequence.qCountRightSpace(): Int = takeLastWhile { it == ' ' }.count()

// CallChain[size=4] = qMASK_LENGTH_LIMIT <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal const val qMASK_LENGTH_LIMIT: Int = 100_000

// CallChain[size=6] = QToString <-[Ref]- qToStringRegistry <-[Call]- Any.qToString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal class QToString(val okToApply: (Any) -> Boolean, val toString: (Any) -> String)

// CallChain[size=5] = qToStringRegistry <-[Call]- Any.qToString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private val qToStringRegistry: MutableList<QToString> by lazy {
    val toStrings =
        QMyToString::class.qFunctions(
            QMFunc.nameExact("qToString") and
//                            QMFunc.returnType(String::class, false) and
//                            QMFunc.NoParams and
                    QMFunc.DeclaredOnly and
                    QMFunc.IncludeExtensionsInClass
        )

    toStrings.map { func ->
        QToString(
            okToApply = { value ->
                func.extensionReceiverParameter?.type?.qIsSuperclassOf(value::class) ?: false
            },
            toString = { value ->
                func.call(QMyToString, value) as String
            }
        )
    }.toMutableList()
}

// CallChain[size=4] = Any.qToString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun Any?.qToString(): String {
    if (this == null)
        return "null".light_gray

    for (r in qToStringRegistry) {
        if (r.okToApply(this)) {
            return r.toString(this)
        }
    }

    return toString()
}

// CallChain[size=3] = Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun Any?.qToLogString(maxLineLength: Int = 80): String {
    if (QMyLog.no_format) {
        return this.toString()
    }

    if (this == null)
        return "null".light_gray

    val str = this.qToString()

    if (str.isBlank())
        return str.qClarifyEmptyOrBlank()

    val isListOrArray =
        this !is CharSequence && (str.startsWith("[") && str.endsWith("]")) || (str.startsWith("{") && str.endsWith("}"))
    val isMultiline = isListOrArray && str.qIsMultiLine()
    val isNestedListOrArray = isListOrArray && str.startsWith("[[")

    val comma = ",".light_gray
    val separator = "----".light_gray

    return if (isNestedListOrArray) { // Nested list always add line breaks for consistent formatting.
        val str2 = str.replaceRange(1, 1, "\n")

        val masked = str2.replaceRange(str.length, str.length, "\n").qMask(
            QMask.INNER_BRACKETS
        )

        masked.replaceAndUnmask(", ".re, "$comma\n").trim()
    } else if (isListOrArray && (maxLineLength < str.length || isMultiline) && str.length < qMASK_LENGTH_LIMIT) { // qMask is slow, needs limit length
        val str2 = str.replaceRange(1, 1, "\n")

        val masked = str2.replaceRange(str.length, str.length, "\n").qMask(
            QMask.PARENS,
            QMask.KOTLIN_STRING
        )

        masked.replaceAndUnmask(", ".re, if (isMultiline) "\n$separator\n" else "$comma\n").trim()
    } else {
        str //.trim()
    }.qClarifyEmptyOrBlank()
}

// CallChain[size=4] = String.qClarifyEmptyOrBlank() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun String.qClarifyEmptyOrBlank(): String {
    return if (this.isEmpty()) {
        "(EMPTY STRING)".qColor(QColor.LightGray)
    } else if (this.isBlank()) {
        if (this == "\n" || this == "\r\n") {
            "$this(LINE BREAK)".qColor(QColor.LightGray)
        } else {
            "$this(BLANK STRING)".qColor(QColor.LightGray)
        }
    } else {
        this
    }
}

// CallChain[size=13] = Char.qIsWhitespace() <-[Call]- QSequenceReader.detectSpace() <-[Propag]- QSe ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun Char.qIsWhitespace(allowLinebreak: Boolean = false): Boolean {
    return if (this.isWhitespace()) {
        allowLinebreak || (this != '\n' && this != '\r')
    } else {
        false
    }
}

// CallChain[size=9] = String.qCamelCaseToSpaceSeparated() <-[Call]- QException.qToString() <-[Call] ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun String.qCamelCaseToSpaceSeparated(toLowerCase: Boolean = false): String {
    return this.replace("([a-z])([A-Z]+)".re, "$1 $2")
        .replace("([A-Z]+)([A-Z][a-z])".re, "$1 $2").applyIf(toLowerCase) {
            lowercase(Locale.ENGLISH)
        }
}

// CallChain[size=5] = List<String>.qJoinStringNicely() <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun List<String>.qJoinStringNicely(separatorMultiline: String = "\n", separatorSingleLine: String = "  "): String {
    return qMap { item, idx, pos, previous, next ->
        when (pos) {
            QLoopPos.OnlyOneItem, QLoopPos.Last ->
                return@qMap item
            QLoopPos.First, QLoopPos.Middle -> {
                // Second Item, First Item ends with new line

                if( next!!.qIsMultiLineOrVeryLongLine() || item.qIsMultiLineOrVeryLongLine() ) {
                    item + separatorMultiline
                } else {
                    item + separatorSingleLine
                }
            }

        }
    }.joinToString("")
}