// 2023. nyabkun  MIT LICENSE

package nyab.match

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=15] = String.qMatches() <-[Call]- Path.qFind() <-[Call]- Collection<Path>.qFind()  ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun String.qMatches(matcher: QM): Boolean = matcher.matches(this)

// CallChain[size=5] = QM.not <-[Call]- QM.notExact() <-[Call]- QMMethod.nameNotExact() <-[Call]- qTest() <-[Call]- main()[Root]
private val QM.not: QM
    get() = QMatchNot(this)

// CallChain[size=6] = QMatchNot <-[Call]- QM.not <-[Call]- QM.notExact() <-[Call]- QMMethod.nameNotExact() <-[Call]- qTest() <-[Call]- main()[Root]
private class QMatchNot(val matcher: QM) : QM {
    // CallChain[size=7] = QMatchNot.matches() <-[Propag]- QMatchNot <-[Call]- QM.not <-[Call]- QM.notExact() <-[Call]- QMMethod.nameNotExact() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun matches(text: String): Boolean = !matcher.matches(text)

    // CallChain[size=7] = QMatchNot.toString() <-[Propag]- QMatchNot <-[Call]- QM.not <-[Call]- QM.notExact() <-[Call]- QMMethod.nameNotExact() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun toString(): String {
        return this::class.simpleName.toString() + "(matcher=$matcher)"
    }
}

// CallChain[size=15] = QMatchAny <-[Call]- QM.isAny() <-[Propag]- QM.exact() <-[Call]- qSrcFileAtFr ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal object QMatchAny : QM {
    // CallChain[size=16] = QMatchAny.matches() <-[Propag]- QMatchAny <-[Call]- QM.isAny() <-[Propag]- Q ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun matches(text: String): Boolean {
        return true
    }

    // CallChain[size=16] = QMatchAny.toString() <-[Propag]- QMatchAny <-[Call]- QM.isAny() <-[Propag]-  ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun toString(): String {
        return this::class.simpleName.toString()
    }
}

// CallChain[size=15] = QMatchNone <-[Call]- QM.isNone() <-[Propag]- QM.exact() <-[Call]- qSrcFileAt ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal object QMatchNone : QM {
    // CallChain[size=16] = QMatchNone.matches() <-[Propag]- QMatchNone <-[Call]- QM.isNone() <-[Propag] ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun matches(text: String): Boolean {
        return false
    }

    // CallChain[size=16] = QMatchNone.toString() <-[Propag]- QMatchNone <-[Call]- QM.isNone() <-[Propag ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun toString(): String {
        return this::class.simpleName.toString()
    }
}

// CallChain[size=14] = QM <-[Ref]- QM.exact() <-[Call]- qSrcFileAtFrame() <-[Call]- qSrcFileLinesAt ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal interface QM {
    // CallChain[size=14] = QM.matches() <-[Propag]- QM.exact() <-[Call]- qSrcFileAtFrame() <-[Call]- qS ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun matches(text: String): Boolean

    // CallChain[size=14] = QM.isAny() <-[Propag]- QM.exact() <-[Call]- qSrcFileAtFrame() <-[Call]- qSrc ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun isAny(): Boolean = this == QMatchAny

    // CallChain[size=14] = QM.isNone() <-[Propag]- QM.exact() <-[Call]- qSrcFileAtFrame() <-[Call]- qSr ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun isNone(): Boolean = this == QMatchNone

    companion object {
        // CallChain[size=13] = QM.exact() <-[Call]- qSrcFileAtFrame() <-[Call]- qSrcFileLinesAtFrame() <-[C ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        fun exact(text: String, ignoreCase: Boolean = false): QM = QExactMatch(text, ignoreCase)

        // CallChain[size=4] = QM.notExact() <-[Call]- QMMethod.nameNotExact() <-[Call]- qTest() <-[Call]- main()[Root]
        fun notExact(text: String, ignoreCase: Boolean = false): QM = QExactMatch(text, ignoreCase).not

        // CallChain[size=11] = QM.startsWith() <-[Call]- QMyPath.src_root <-[Call]- qLogStackFrames() <-[Ca ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        fun startsWith(text: String, ignoreCase: Boolean = false): QM = QStartsWithMatch(text, ignoreCase)

        
    }
}

// CallChain[size=14] = QExactMatch <-[Call]- QM.exact() <-[Call]- qSrcFileAtFrame() <-[Call]- qSrcF ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private class QExactMatch(val textExact: String, val ignoreCase: Boolean = false) : QM {
    // CallChain[size=15] = QExactMatch.matches() <-[Propag]- QExactMatch <-[Call]- QM.exact() <-[Call]- ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun matches(text: String): Boolean {
        return text.equals(textExact, ignoreCase)
    }

    // CallChain[size=15] = QExactMatch.toString() <-[Propag]- QExactMatch <-[Call]- QM.exact() <-[Call] ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun toString(): String {
        return this::class.simpleName + "(textExact=$textExact, ignoreCase=$ignoreCase)"
    }
}

// CallChain[size=12] = QStartsWithMatch <-[Call]- QM.startsWith() <-[Call]- QMyPath.src_root <-[Cal ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private class QStartsWithMatch(val textStartsWith: String, val ignoreCase: Boolean = false) : QM {
    // CallChain[size=13] = QStartsWithMatch.matches() <-[Propag]- QStartsWithMatch <-[Call]- QM.startsW ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun matches(text: String): Boolean {
        return text.startsWith(textStartsWith, ignoreCase)
    }

    // CallChain[size=13] = QStartsWithMatch.toString() <-[Propag]- QStartsWithMatch <-[Call]- QM.starts ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun toString(): String {
        return this::class.simpleName + "(textStartsWith=$textStartsWith, ignoreCase=$ignoreCase)"
    }
}