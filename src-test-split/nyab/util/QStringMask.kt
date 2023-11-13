// 2023. nyabkun  MIT LICENSE

@file:Suppress("MemberVisibilityCanBePrivate")

package nyab.util

import kotlin.math.max
import kotlin.math.min

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=11] = String.qCountOccurrence() <-[Call]- QFetchRule.SMART_FETCH <-[Call]- qLogSta ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun String.qCountOccurrence(word: String): Int {
    return windowed(word.length) {
        if (it == word)
            1
        else
            0
    }.sum()
}

// CallChain[size=7] = QMask <-[Ref]- QMaskBetween <-[Call]- QMask.DOUBLE_QUOTE <-[Call]- QMask.KOTLIN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal interface QMask {
    // CallChain[size=5] = QMask.apply() <-[Propag]- QMask.KOTLIN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun apply(text: String): QMaskResult

    companion object {
        // CallChain[size=5] = QMask.THREE_DOUBLE_QUOTES <-[Call]- QMask.KOTLIN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        val THREE_DOUBLE_QUOTES by lazy {
            QMaskBetween(
                "\"\"\"", "\"\"\"",
                nestStartSequence = null,
                escapeChar = '\\',
                maskIncludeStartAndEndSequence = false,
            )
        }
        // CallChain[size=5] = QMask.DOUBLE_QUOTE <-[Call]- QMask.KOTLIN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        val DOUBLE_QUOTE by lazy {
            QMaskBetween(
                "\"", "\"",
                nestStartSequence = null,
                escapeChar = '\\',
                maskIncludeStartAndEndSequence = false,
            )
        }
        // CallChain[size=4] = QMask.KOTLIN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        val KOTLIN_STRING by lazy {
            QMultiMask(
                THREE_DOUBLE_QUOTES,
                DOUBLE_QUOTE
            )
        }
        // CallChain[size=4] = QMask.PARENS <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        val PARENS by lazy {
            QMaskBetween(
                "(", ")",
                nestStartSequence = "(", escapeChar = '\\'
            )
        }
        // CallChain[size=4] = QMask.INNER_BRACKETS <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        val INNER_BRACKETS by lazy {
            QMaskBetween(
                "[", "]",
                nestStartSequence = "[", escapeChar = '', // shell color
                targetNestDepth = 2,
                maskIncludeStartAndEndSequence = true
            )
        }

        
    }
}

// CallChain[size=5] = QMultiMask <-[Call]- QMask.KOTLIN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal class QMultiMask(vararg mask: QMaskBetween) : QMask {
    // CallChain[size=7] = QMultiMask.masks <-[Call]- QMultiMask.apply() <-[Propag]- QMultiMask <-[Call] ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val masks: Array<QMaskBetween>

    // CallChain[size=6] = QMultiMask.init { <-[Propag]- QMultiMask <-[Call]- QMask.KOTLIN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    init {
        masks = arrayOf(*mask)
    }

    // CallChain[size=6] = QMultiMask.apply() <-[Propag]- QMultiMask <-[Call]- QMask.KOTLIN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun apply(text: String): QMaskResult {
        var result: QMaskResult? = null
        for (mask in masks) {
            result = result?.applyMoreMask(mask) ?: mask.apply(text)
        }

        return result!!
    }
}

// CallChain[size=6] = QMaskBetween <-[Call]- QMask.DOUBLE_QUOTE <-[Call]- QMask.KOTLIN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal class QMaskBetween(
    val startSequence: String,
    val endSequence: String,
    val nestStartSequence: String? = if (startSequence != endSequence) {
        startSequence // can have nested structure
    } else {
        null // no nested structure
    },
    val escapeChar: Char? = null,
    val allowEOFEnd: Boolean = false,
    val targetNestDepth: Int = 1,
    val maskIncludeStartAndEndSequence: Boolean = false,
    val invert: Boolean = false,
    val noMaskChars: CharArray? = null, // charArrayOf('\u0020', '\t', '\n', '\r'),
    // U+E000..U+F8FF BMP (0) Private Use Area
    val maskChar: Char = '\uee31',
) : QMask {

    // CallChain[size=7] = QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- QMask.D ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun apply(text: String): QMaskResult {
        return applyMore(text, null)
    }

    // CallChain[size=8] = QMaskBetween.applyMore() <-[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetw ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun applyMore(text: String, orgText: String? = null): QMaskResult {
        val regions = text.qFindBetween(
            startSequence,
            endSequence,
            nestStartSequence,
            escapeChar,
            allowEOFEnd,
            targetNestDepth,
            maskIncludeStartAndEndSequence
        )

        val sb = StringBuilder(text.length)

        val iter = text.iterator()

        var idx = -1

        while (iter.hasNext()) {
            idx++

            var masked = false

            val ch = iter.nextChar()

            if (noMaskChars?.contains(ch) == true) {
                sb.append(ch)
                continue
            }

            for (region in regions) {
                if (idx < region.start) {
                    break
                }

                if (region.contains(idx)) {
                    sb.append(if (!invert) maskChar else ch)
                    masked = true
                    break
                }
            }

            if (!masked) {
                sb.append(if (!invert) ch else maskChar)
            }
        }

        val maskedStr = sb.toString()

        return QMaskResult(maskedStr, orgText ?: text, maskChar)
    }
}

// CallChain[size=11] = QMutRegion <-[Ref]- QRegion.toMutRegion() <-[Propag]- QRegion.contains() <-[ ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal open class QMutRegion(override var start: Int, override var end: Int) : QRegion(start, end) {
    // CallChain[size=12] = QMutRegion.intersectMut() <-[Propag]- QMutRegion <-[Ref]- QRegion.toMutRegio ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun intersectMut(region: QRegion) {
        val start = max(this.start, region.start)
        val end = min(this.end, region.end)

        if (start <= end) {
            this.start = start
            this.end = end
        }
    }

    // CallChain[size=12] = QMutRegion.addOffset() <-[Propag]- QMutRegion <-[Ref]- QRegion.toMutRegion() ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun addOffset(offset: Int) {
        start += offset
        end += offset
    }

    // CallChain[size=12] = QMutRegion.shift() <-[Propag]- QMutRegion <-[Ref]- QRegion.toMutRegion() <-[ ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun shift(length: Int) {
        this.start += length
        this.end += length
    }
}

// CallChain[size=11] = QRegion <-[Ref]- QRegion.intersect() <-[Propag]- QRegion.contains() <-[Call] ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
/**
 * [start] inclusive, [end] exclusive
 */
internal open class QRegion(open val start: Int, open val end: Int) {
    // CallChain[size=10] = QRegion.toMutRegion() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween. ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun toMutRegion(): QMutRegion {
        return QMutRegion(start, end)
    }

    // CallChain[size=10] = QRegion.toRange() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.appl ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun toRange(): IntRange {
        return IntRange(start, end + 1)
    }

    // CallChain[size=10] = QRegion.length <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.applyMo ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val length: Int
        get() = end - start

    // CallChain[size=10] = QRegion.intersect() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.ap ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun intersect(region: QRegion): QRegion? {
        val start = max(this.start, region.start)
        val end = min(this.end, region.end)

        return if (start <= end) {
            QRegion(end, start)
        } else {
            null
        }
    }

    // CallChain[size=9] = QRegion.contains() <-[Call]- QMaskBetween.applyMore() <-[Call]- QMaskBetween. ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun contains(idx: Int): Boolean {
        return idx in start until end
    }

    // CallChain[size=10] = QRegion.cut() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.applyMor ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun cut(text: String): String {
        return text.substring(start, end)
    }

    // CallChain[size=10] = QRegion.remove() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.apply ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun remove(text: String): String {
        return text.removeRange(start, end)
    }

    // CallChain[size=10] = QRegion.replace() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.appl ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun replace(text: String, replacement: String): String {
        return text.replaceRange(start, end, replacement)
    }

    // CallChain[size=10] = QRegion.mask() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.applyMo ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun mask(text: String, maskChar: Char = '*'): String {
        return text.replaceRange(this.toRange(), maskChar.toString().repeat(end - start))
    }
}

// CallChain[size=6] = QReplacer <-[Ref]- String.qMaskAndReplace() <-[Call]- QMaskResult.replaceAndUnmask() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal class QReplacer(start: Int, end: Int, val replacement: String) : QMutRegion(start, end)

// CallChain[size=6] = QMaskResult <-[Ref]- QMask.apply() <-[Propag]- QMask.KOTLIN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal class QMaskResult(val maskedStr: String, val orgText: String, val maskChar: Char) {
    // CallChain[size=4] = QMaskResult.replaceAndUnmask() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    /**
     * Apply regex to masked string.
     * Apply replacement to original text.
     */
    fun replaceAndUnmask(ptn: Regex, replacement: String, findAll: Boolean = true): String {
        return orgText.qMaskAndReplace(maskedStr, ptn, replacement, findAll)
    }

    // CallChain[size=7] = QMaskResult.applyMoreMask() <-[Call]- QMultiMask.apply() <-[Propag]- QMultiMa ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun applyMoreMask(mask: QMaskBetween): QMaskResult {
        return mask.applyMore(maskedStr, orgText)
    }

    // CallChain[size=7] = QMaskResult.toString() <-[Propag]- QMaskResult <-[Ref]- QMask.apply() <-[Prop ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun toString(): String {
        val original = orgText.qWithNewLineSurround(onlyIf = QOnlyIfStr.Multiline)
        val masked = maskedStr.replace(maskChar, '*').qWithNewLineSurround(onlyIf = QOnlyIfStr.Multiline)

        return "${QMaskResult::class.simpleName} : $original ${"->".cyan} $masked"
    }
}

// CallChain[size=6] = CharSequence.qMask() <-[Call]- Path.qListTopLevelFqClassNamesInThisFile() <-[Call]- qCallerFileMainClass() <-[Call]- qThisFileMainClass <-[Call]- qTest() <-[Call]- main()[Root]
internal fun CharSequence.qMask(mask: QMultiMask): QMaskResult {
    return mask.apply(this.toString())
}

// CallChain[size=4] = CharSequence.qMask() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun CharSequence.qMask(vararg mask: QMask): QMaskResult {
    mask.size.qaNotZero()

    return if (mask.size == 1) {
        mask[0].apply(this.toString())
    } else {
        val masks = mutableListOf<QMaskBetween>()
        for (m in mask) {
            if (m is QMaskBetween) {
                masks += m
            } else if (m is QMultiMask) {
                masks += m.masks
            }
        }

        QMultiMask(*masks.toTypedArray()).apply(this.toString())
    }
}

// CallChain[size=7] = QOnlyWhen <-[Ref]- String.qRemoveBetween() <-[Call]- Path.qListTopLevelFqClas ... all]- qCallerFileMainClass() <-[Call]- qThisFileMainClass <-[Call]- qTest() <-[Call]- main()[Root]
internal enum class QOnlyWhen {
    // CallChain[size=8] = QOnlyWhen.FirstOnly <-[Call]- String.qReplaceBetween() <-[Call]- String.qRemo ... all]- qCallerFileMainClass() <-[Call]- qThisFileMainClass <-[Call]- qTest() <-[Call]- main()[Root]
    FirstOnly,
    // CallChain[size=8] = QOnlyWhen.LastOnly <-[Call]- String.qReplaceBetween() <-[Call]- String.qRemov ... all]- qCallerFileMainClass() <-[Call]- qThisFileMainClass <-[Call]- qTest() <-[Call]- main()[Root]
    LastOnly,
    // CallChain[size=7] = QOnlyWhen.All <-[Call]- String.qRemoveBetween() <-[Call]- Path.qListTopLevelF ... all]- qCallerFileMainClass() <-[Call]- qThisFileMainClass <-[Call]- qTest() <-[Call]- main()[Root]
    All
}

// CallChain[size=6] = String.qRemoveBetween() <-[Call]- Path.qListTopLevelFqClassNamesInThisFile() <-[Call]- qCallerFileMainClass() <-[Call]- qThisFileMainClass <-[Call]- qTest() <-[Call]- main()[Root]
internal fun String.qRemoveBetween(
    startSequence: String,
    endSequence: String,
    nestStartSequence: String? = if (startSequence != endSequence) {
        startSequence // can have nested structure
    } else {
        null // no nested structure
    },
    escapeChar: Char? = null,
    allowEOFEnd: Boolean = false,
    nestingDepth: Int = 1,
    regionIncludesStartAndEndSequence: Boolean = false,
    onlyWhen: QOnlyWhen = QOnlyWhen.All,
): String {
    return qReplaceBetween(
        startSequence,
        endSequence,
        "",
        nestStartSequence,
        escapeChar,
        allowEOFEnd,
        nestingDepth,
        regionIncludesStartAndEndSequence,
        onlyWhen
    )
}

// CallChain[size=7] = String.qReplaceBetween() <-[Call]- String.qRemoveBetween() <-[Call]- Path.qLi ... all]- qCallerFileMainClass() <-[Call]- qThisFileMainClass <-[Call]- qTest() <-[Call]- main()[Root]
internal fun String.qReplaceBetween(
    startSequence: String,
    endSequence: String,
    replacement: String,
    nestStartSequence: String? = if (startSequence != endSequence) {
        startSequence // can have nested structure
    } else {
        null // no nested structure
    },
    escapeChar: Char? = null,
    allowEOFEnd: Boolean = false,
    nestingDepth: Int = 1,
    regionIncludesStartAndEndSequence: Boolean = false,
    onlyWhen: QOnlyWhen = QOnlyWhen.All,
): String {
    val regions = qFindBetween(
        startSequence,
        endSequence,
        nestStartSequence,
        escapeChar,
        allowEOFEnd,
        nestingDepth,
        regionIncludesStartAndEndSequence
    )
    val mutRegions = regions.map { it.toMutRegion() }

    var text = this
    for ((idx, region) in mutRegions.withIndex()) {
        val okToReplace = when (onlyWhen) {
            QOnlyWhen.FirstOnly -> idx == 0
            QOnlyWhen.LastOnly -> idx == mutRegions.size - 1
            QOnlyWhen.All -> true
        }

        if (okToReplace) {
            text = region.replace(text, replacement)

            for (shiftRegion in mutRegions) {
                shiftRegion.shift(replacement.length - region.length)
            }
        }
    }

    return text
}

// CallChain[size=9] = String.qFindBetween() <-[Call]- QMaskBetween.applyMore() <-[Call]- QMaskBetwe ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun String.qFindBetween(
    startSequence: String,
    endSequence: String,
    nestStartSequence: String? = if (startSequence != endSequence) {
        startSequence // can have nested structure
    } else {
        null // no nested structure
    },
    escapeChar: Char? = null,
    allowEOFEnd: Boolean = false,
    nestingDepth: Int = 1,
    regionIncludesStartAndEndSequence: Boolean = false,
): List<QRegion> {
    val finder = QBetween(
        startSequence,
        endSequence,
        nestStartSequence,
        escapeChar,
        allowEOFEnd,
        nestingDepth,
        regionIncludesStartAndEndSequence
    )

    return finder.find(this)
}

// CallChain[size=5] = String.qMaskAndReplace() <-[Call]- QMaskResult.replaceAndUnmask() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun String.qMaskAndReplace(
    maskedStr: String,
    ptn: Regex,
    replacement: String = "$1",
    replaceAll: Boolean = true,
): String {
    // Apply Regex pattern to maskedStr
    val findResults: Sequence<MatchResult> = if (replaceAll) {
        ptn.findAll(maskedStr)
    } else {
        val result = ptn.find(maskedStr)
        if (result == null) {
            emptySequence()
        } else {
            sequenceOf(result)
        }
    }

    val replacers: MutableList<QReplacer> = mutableListOf()

    for (r in findResults) {
        val g = r.qResolveReplacementGroup(replacement, this)
        replacers += QReplacer(
            r.range.first,
            r.range.last + 1,
            g
        )
    }

    // Apply replacements to this String instead of maskedStr
    return qMultiReplace(replacers)
}

// CallChain[size=6] = CharSequence.qMultiReplace() <-[Call]- String.qMaskAndReplace() <-[Call]- QMa ... dUnmask() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
/**
 * currently does not support region overlap
 */
internal fun CharSequence.qMultiReplace(replacers: List<QReplacer>): String {
    // TODO Use StringBuilder
    val sb = StringBuilder(this)
    var offset = 0
    for (r in replacers) {
        sb.replace(r.start + offset, r.end + offset, r.replacement)
        offset += r.replacement.length - (r.end - r.start)
    }

    return sb.toString()
}

// CallChain[size=6] = MatchResult.qResolveReplacementGroup() <-[Call]- String.qMaskAndReplace() <-[ ... dUnmask() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun MatchResult.qResolveReplacementGroup(replacement: String, orgText: String): String {
    var resolveGroup = replacement

    for ((i, g) in groups.withIndex()) {
        if (g == null) continue

        val gValue = if (g.range.last - g.range.first == 0 || !resolveGroup.contains("$")) {
            ""
        } else {
            orgText.substring(g.range)
        }

        resolveGroup = resolveGroup.qReplace("$$i", gValue, '\\')
    }

    return resolveGroup
}

// CallChain[size=7] = CharSequence.qReplace() <-[Call]- MatchResult.qResolveReplacementGroup() <-[C ... dUnmask() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun CharSequence.qReplace(oldValue: String, newValue: String, escapeChar: Char): String {
    return replace(Regex("""(?<!\Q$escapeChar\E)\Q$oldValue\E"""), newValue)
}

// CallChain[size=10] = QBetween <-[Call]- String.qFindBetween() <-[Call]- QMaskBetween.applyMore()  ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private class QBetween(
    val startSequence: String,
    val endSequence: String,
    val nestStartSequence: String? = if (startSequence != endSequence) {
        startSequence // can have nested structure
    } else {
        null // no nested structure
    },
    val escapeChar: Char? = null,
    val allowEOFEnd: Boolean = false,
    val nestingDepth: Int = 1,
    val regionIncludeStartAndEndSequence: Boolean = false,
) {

    // CallChain[size=10] = QBetween.find() <-[Call]- String.qFindBetween() <-[Call]- QMaskBetween.apply ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun find(text: CharSequence): List<QRegion> {
        val reader = QSequenceReader(text)

        val ranges: MutableList<QRegion> = mutableListOf()

        val startChArr = startSequence.toCharArray()
        val nestStartChArr = nestStartSequence?.toCharArray()
        val endChArr = endSequence.toCharArray()

        var nNest = 0

        var startSeqIdxInclusive = -1

        while (!reader.isEOF()) {
            val ch = reader.read()

            if (ch == escapeChar) {
                reader.skip(1)
                continue
            } else {
                reader.unread()

                val startSequenceDetected = if (nNest == 0) {
                    reader.detectSequence(startChArr, allowEOFEnd)
                } else if (nestStartChArr != null) {
                    reader.detectSequence(nestStartChArr, allowEOFEnd)
                } else {
                    false
                }

                if (startSequenceDetected) {
                    nNest++

                    if (nestingDepth == nNest) {
                        startSeqIdxInclusive = reader.offset + 1
                    }
                } else if (nNest > 0 && reader.detectSequence(endChArr, allowEOFEnd)) {
                    if (nestingDepth == nNest) {
                        val endSeqIdxInclusive = reader.offset - endChArr.size

                        ranges += if (!regionIncludeStartAndEndSequence) {
                            QRegion(startSeqIdxInclusive, endSeqIdxInclusive + 1)
                        } else {
                            val end = min(endSeqIdxInclusive + endChArr.size + 1, text.length)
                            QRegion(startSeqIdxInclusive - startChArr.size, end)
                        }
                    }

                    nNest--
                } else {
                    reader.skip(1)
                }
            }
        }

        return ranges
    }
}