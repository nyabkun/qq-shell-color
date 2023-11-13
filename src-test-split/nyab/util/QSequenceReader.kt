// 2023. nyabkun  MIT LICENSE

package nyab.util

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=11] = QSequenceReader <-[Call]- QBetween.find() <-[Call]- String.qFindBetween() <- ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal open class QSequenceReader(text: CharSequence) : QCharReader(text) {
    // CallChain[size=12] = QSequenceReader.sequenceOffset <-[Propag]- QSequenceReader.detectSequence()  ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    private var sequenceOffset = 0

    // CallChain[size=12] = QSequenceReader.sequence <-[Propag]- QSequenceReader.detectSequence() <-[Cal ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    private var sequence: CharArray? = null

    // CallChain[size=12] = QSequenceReader.startReadingSequence() <-[Call]- QSequenceReader.detectSeque ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    private fun startReadingSequence(sequence: CharArray): Boolean {
        return if (countCharToEOF() < sequence.size) {
            false
        } else {
            this.sequence = sequence
            sequenceOffset = offset
            true
        }
    }

    // CallChain[size=12] = QSequenceReader.endReadingSequence() <-[Call]- QSequenceReader.detectSequenc ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    private fun endReadingSequence(success: Boolean): Boolean {

        if (!success) {
            offset = sequenceOffset
        }

        sequenceOffset = -1

        return success
    }

    // CallChain[size=12] = QSequenceReader.hasNextCharInSequence() <-[Call]- QSequenceReader.detectSequ ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    private fun hasNextCharInSequence(): Boolean {
        return if (sequence == null) {
            false
        } else {
            (offsetInSequence() < sequence!!.size) && !isEOF()
        }
    }

    // CallChain[size=12] = QSequenceReader.peekCurrentCharInSequence() <-[Call]- QSequenceReader.detect ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    private fun peekCurrentCharInSequence(): Char {
        return sequence!![offsetInSequence()]
    }

    // CallChain[size=12] = QSequenceReader.offsetInSequence() <-[Call]- QSequenceReader.detectSequence( ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    /**
     * 0 to sequence.size - 1
     */
    private fun offsetInSequence(): Int {
        return offset - sequenceOffset
    }
    // CallChain[size=12] = QSequenceReader.detectChar() <-[Propag]- QSequenceReader.detectSequence() <- ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun detectChar(target: Char): Boolean {
        return if( target == peekRead() ) {
            read()
            true
        } else {
            false
        }
    }

    // CallChain[size=12] = QSequenceReader.detectWord() <-[Propag]- QSequenceReader.detectSequence() <- ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun detectWord(): Boolean {
        startDetection()

        var ch = peekRead()
        if( ch.isWhitespace() )
            return endDetection(false)

        while (!isEOF()) {
            ch = read()
            if (ch.isWhitespace()) {
                unread() // remove last whitespace
                return endDetection(true)
            }
        }

        return endDetection(true)
    }

    // CallChain[size=12] = QSequenceReader.detectSpace() <-[Propag]- QSequenceReader.detectSequence() < ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun detectSpace(): Boolean {
        startDetection()

        var ch = peekRead()
        if( !ch.qIsWhitespace(allowLinebreak = false))
            return endDetection(false)

        while (!isEOF()) {
            ch = read()
            if (!ch.qIsWhitespace(allowLinebreak = false)) {
                unread()
                return endDetection(true)
            }
        }

        return endDetection(true)
    }

    // CallChain[size=11] = QSequenceReader.detectSequence() <-[Call]- QBetween.find() <-[Call]- String. ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    /**
     * If sequence is detected, move offset by the length of the sequence.
     * If no sequence is found, offset remains unchanged.
     */
    fun detectSequence(sequence: CharArray, eofAllowed: Boolean = false): Boolean {
        if (!startReadingSequence(sequence)) return false

        while (hasNextCharInSequence()) {
            val seqChar = peekCurrentCharInSequence()
            val ch = read()

            if (ch != seqChar) {
                endReadingSequence(false)
                return eofAllowed && isEOF()
            }
        }

        return if (offsetInSequence() == sequence.size) {
            endReadingSequence(true)
            true
        } else {
            val success = eofAllowed && isEOF()
            endReadingSequence(success)
            success
        }
    }

    // CallChain[size=12] = QSequenceReader.QSequenceBetween <-[Propag]- QSequenceReader.detectSequence( ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    class QSequenceBetween(
        val startSequence: CharArray,
        val endSequence: CharArray,
        val nestStartSequence: CharArray? = null,
        val escapeChar: Char? = null,
        val allowEOFEnd: Boolean = false,
    )

    // CallChain[size=12] = QSequenceReader.detectSequenceBetween() <-[Propag]- QSequenceReader.detectSe ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun detectSequenceBetween(
        seq: QSequenceBetween
    ): Boolean {
        val seqBetweenStartOffset = offset

        if (!detectSequence(seq.startSequence)) {
            return endDetection(false)
        }

        var nNest = 0

        while (!isEOF()) {
            val ch = read()

            if (ch == seq.escapeChar) {
                skip(1)
                continue
            } else {
                unread()

                val nestStartSequenceDetected = if (seq.nestStartSequence != null) {
                    detectSequence(seq.nestStartSequence, seq.allowEOFEnd)
                } else {
                    false
                }

                if (nestStartSequenceDetected) {
                    nNest++
                } else if (detectSequence(seq.endSequence, seq.allowEOFEnd)) {
                    if (nNest == 0) {
                        detectionStartOffset = seqBetweenStartOffset
                        detectionEndOffset = offset

                        return true
                    }

                    nNest--
                } else {
                    skip(1)
                }
            }
        }

        return endDetection(seq.allowEOFEnd)
    }

    // CallChain[size=12] = QSequenceReader.detectSequenceRegex() <-[Propag]- QSequenceReader.detectSequ ... IN_STRING <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    /**
     * If sequence is detected, move offset by the length of the sequence.
     * If no sequence is found, offset remains unchanged.
     */
    fun detectSequenceRegex(
        sequence: Regex,
        lengthFromCurrentOffset: Int,
        okToRegexMatch: ((QSequenceReader) -> Boolean)? = null,
        eofAllowed: Boolean = false
    ): String? {
        if (okToRegexMatch != null) {
            val curOffset = this.offset

            if (!okToRegexMatch(this)) {
                return null
            }

            this.offset = curOffset
        }

        val nextString = peekReadString(lengthFromCurrentOffset)
        val found = sequence.find(nextString)

        return if (found != null) {
            skip(found.value.length)
            found.value
        } else if (eofAllowed && countCharToEOF() < lengthFromCurrentOffset) {
            val seq = text.substring(offset + 1, text.length)
            skipToEOF()
            seq
        } else {
            null
        }
    }
}