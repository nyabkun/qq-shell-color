// 2023. nyabkun  MIT LICENSE

package nyab.util

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=14] = QIdxRange <-[Ref]- QCharReader.lastDetectionRange() <-[Propag]- QCharReader  ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal class QIdxRange(val start: Int, val endExclusive: Int)

// CallChain[size=12] = QCharReader <-[Call]- QSequenceReader <-[Call]- QBetween.find() <-[Call]- St ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
@Suppress("NOTHING_TO_INLINE")
internal open class QCharReader(val text: CharSequence) {
    // CallChain[size=13] = QCharReader.offset <-[Propag]- QCharReader <-[Call]- QSequenceReader <-[Call ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    var offset = -1
        protected set
    // CallChain[size=13] = QCharReader.mark <-[Propag]- QCharReader <-[Call]- QSequenceReader <-[Call]- ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    private var mark = -1

    // CallChain[size=13] = QCharReader.detectionStartOffset <-[Propag]- QCharReader <-[Call]- QSequence ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    var detectionStartOffset = -1
    // CallChain[size=13] = QCharReader.detectionEndOffset <-[Propag]- QCharReader <-[Call]- QSequenceRe ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    var detectionEndOffset = -1

    // CallChain[size=13] = QCharReader.lastDetectionRange() <-[Propag]- QCharReader <-[Call]- QSequence ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun lastDetectionRange(): QIdxRange {
        return QIdxRange(detectionStartOffset + 1, detectionEndOffset)
    }

    // CallChain[size=13] = QCharReader.lastDetectionLength() <-[Propag]- QCharReader <-[Call]- QSequenc ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun lastDetectionLength(): Int {
        return detectionEndOffset - detectionStartOffset
    }

    // CallChain[size=13] = QCharReader.lastDetectionString() <-[Propag]- QCharReader <-[Call]- QSequenc ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun lastDetectionString(): String {
        return text.substring(detectionStartOffset + 1, detectionEndOffset)
    }

    // CallChain[size=13] = QCharReader.lineNumber() <-[Propag]- QCharReader <-[Call]- QSequenceReader < ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    /**
     * First line is 1.
     * Excluding current offset linebreak.
     */
    fun lineNumber(): Int {
        mark()

        val endOffset = offset

        resetToStart()

        var lineBreakCount = 0

        while(offset < endOffset && !isEOF()) {
            if (detectLineBreak()) {
                offset += lastDetectionLength()
                lineBreakCount++
            } else {
                skip(1)
            }
        }

        resetToMark()

        return lineBreakCount + 1
    }

    // CallChain[size=13] = QCharReader.countCharToEOF() <-[Propag]- QCharReader <-[Call]- QSequenceRead ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    inline fun countCharToEOF(): Int {
        return text.length - offset - 1
    }

    // CallChain[size=13] = QCharReader.readToLineEnd() <-[Propag]- QCharReader <-[Call]- QSequenceReade ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun readToLineEnd(): String {
        val toLineEnd = text.substring(offset + 1, lineEndLastCharIdx())
        offset += toLineEnd.length
        return toLineEnd
    }

    // CallChain[size=13] = QCharReader.skipToLineEnd() <-[Propag]- QCharReader <-[Call]- QSequenceReade ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun skipToLineEnd() {
        offset = lineEndOffset()
    }

    // CallChain[size=13] = QCharReader.skipToLineStart() <-[Propag]- QCharReader <-[Call]- QSequenceRea ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun skipToLineStart() {
        offset = lineStartOffset()
    }

    // CallChain[size=13] = QCharReader.readToLineStart() <-[Propag]- QCharReader <-[Call]- QSequenceRea ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun readToLineStart(): String {
        val toLineStart = text.substring(lineStartFirstCharIdx(), offset + 1)
        offset -= toLineStart.length
        return toLineStart
    }

    // CallChain[size=13] = QCharReader.currentLine() <-[Propag]- QCharReader <-[Call]- QSequenceReader  ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun currentLine(): String {
        return text.substring(lineStartFirstCharIdx(), lineEndLastCharIdx() + 1)
    }

    // CallChain[size=13] = QCharReader.lineStartFirstCharIdx() <-[Propag]- QCharReader <-[Call]- QSeque ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    inline fun lineStartFirstCharIdx(): Int {
        return lineStartOffset() + 1
    }

    // CallChain[size=13] = QCharReader.lineEndLastCharIdx() <-[Propag]- QCharReader <-[Call]- QSequence ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    inline fun lineEndLastCharIdx(): Int {
        return lineEndOffset()
    }

    // CallChain[size=13] = QCharReader.lineStartOffset() <-[Propag]- QCharReader <-[Call]- QSequenceRea ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun lineStartOffset(): Int {
        mark()

        // unread the linebreak if offset is at the linebreak
        if (detectLineBreak()) {
            offset - lastDetectionLength()
        }

        while (offset >= -1) {
            if (detectLineBreak()) {
                val start = offset
                resetToMark()
                return start
            } else {
                unread()
            }
        }

        resetToMark()
        return -1
    }

    // CallChain[size=13] = QCharReader.lineEndOffset() <-[Propag]- QCharReader <-[Call]- QSequenceReade ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    /**
     * next read() => line break or EOF
     */
    fun lineEndOffset(): Int {
        mark()

        if (detectLineBreak()) {
            resetToMark()
            // read linebreak at the current offset
            return if (lastDetectionString() == "\n" && offset != -1 && unread() == '\r') {
                detectionStartOffset - 1 // offset before \r\n
            } else {
                detectionStartOffset // offset before linebreak
            }
        }

        while (!isEOF()) {
            if (detectLineBreak()) {
                resetToMark()
                return detectionStartOffset
            } else {
                read()
            }
        }

        resetToMark()
        return 0
    }

    // CallChain[size=13] = QCharReader.detectLineBreak() <-[Propag]- QCharReader <-[Call]- QSequenceRea ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun detectLineBreak(): Boolean {
        startDetection()

        while (!isEOF()) {
            val ch = read()
            return if (ch == '\n') {
                // \n or \r\n
                endDetection(true)
            } else if (ch == '\r') {
                continue
            } else {
                endDetection(false)
            }
        }

        return endDetection(false)
    }

    // CallChain[size=13] = QCharReader.countIndentSpaces() <-[Propag]- QCharReader <-[Call]- QSequenceR ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun countIndentSpaces(space: Char = ' '): Int {
        mark()

        val toLineStart = readToLineStart()

        return toLineStart.takeWhile { it == space }.count()
    }

    // CallChain[size=13] = QCharReader.isEOF() <-[Propag]- QCharReader <-[Call]- QSequenceReader <-[Cal ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    inline fun isEOF(): Boolean {
        return offset == text.length - 1
    }

    // CallChain[size=13] = QCharReader.peekRead() <-[Propag]- QCharReader <-[Call]- QSequenceReader <-[ ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    inline fun peekRead(): Char {
        return text[offset + 1]
    }

    // CallChain[size=13] = QCharReader.peekUnread() <-[Propag]- QCharReader <-[Call]- QSequenceReader < ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    inline fun peekUnread(): Char {
        return text[offset]
    }

    // CallChain[size=13] = QCharReader.skip() <-[Propag]- QCharReader <-[Call]- QSequenceReader <-[Call ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun skip(n: Int) {
        offset += n
    }

    // CallChain[size=13] = QCharReader.skipToEOF() <-[Propag]- QCharReader <-[Call]- QSequenceReader <- ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun skipToEOF() {
        offset = text.length - 1
    }

    // CallChain[size=13] = QCharReader.read() <-[Propag]- QCharReader <-[Call]- QSequenceReader <-[Call ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    /**
     * Add offset by 1 and read char.
     */
    fun read(): Char {
        return text[++offset]
    }

    // CallChain[size=13] = QCharReader.startDetection() <-[Propag]- QCharReader <-[Call]- QSequenceRead ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    protected inline fun startDetection() {
        detectionStartOffset = offset
    }

    // CallChain[size=13] = QCharReader.endDetection() <-[Propag]- QCharReader <-[Call]- QSequenceReader ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    protected inline fun endDetection(success: Boolean): Boolean {
        detectionEndOffset = offset

        if( !success ) {
            offset = detectionStartOffset
        }

        return success
    }

    // CallChain[size=13] = QCharReader.mark() <-[Propag]- QCharReader <-[Call]- QSequenceReader <-[Call ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    private fun mark() {
        mark = offset
    }

    // CallChain[size=13] = QCharReader.resetToMark() <-[Propag]- QCharReader <-[Call]- QSequenceReader  ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    private fun resetToMark() {
        offset = mark
    }

    // CallChain[size=13] = QCharReader.resetToStart() <-[Propag]- QCharReader <-[Call]- QSequenceReader ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun resetToStart() {
        offset = -1
    }

    // CallChain[size=13] = QCharReader.unread() <-[Propag]- QCharReader <-[Call]- QSequenceReader <-[Ca ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun unread(): Char {
        return text[offset--]
    }

    // CallChain[size=13] = QCharReader.readString() <-[Propag]- QCharReader <-[Call]- QSequenceReader < ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun readString(length: Int): String {
        if( isEOF() )
            return ""

        val str = text.substring(offset + 1, (offset + 1 + length).coerceAtMost(text.length))
        offset += length
        return str
    }

    // CallChain[size=13] = QCharReader.peekReadString() <-[Propag]- QCharReader <-[Call]- QSequenceRead ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun peekReadString(length: Int): String {
        return text.substring(offset + 1, (offset + 1 + length).coerceAtMost(text.length))
    }

    // CallChain[size=13] = QCharReader.peekUnreadString() <-[Propag]- QCharReader <-[Call]- QSequenceRe ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun peekUnreadString(length: Int): String {
        return text.substring(offset + 1 - length, offset + 1)
    }
}