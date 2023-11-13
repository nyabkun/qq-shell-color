// 2023. nyabkun  MIT LICENSE

package nyab.conf

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=3] = QE <-[Ref]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal typealias QE = QMyException

// CallChain[size=4] = QMyException <-[Ref]- QE <-[Ref]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal enum class QMyException {


    // --- General

    // CallChain[size=7] = QMyException.UnknownReason <-[Call]- QException.QException() <-[Ref]- QE.thro ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    UnknownReason,
    // CallChain[size=13] = QMyException.Unreachable <-[Call]- qUnreachable() <-[Call]- QFetchRule.SINGL ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    Unreachable,
    // CallChain[size=3] = QMyException.ShouldBeTrue <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    ShouldBeTrue,
    // CallChain[size=3] = QMyException.ShouldBeFalse <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    ShouldBeFalse,
    // CallChain[size=14] = QMyException.ShouldNotBeNull <-[Call]- T.qaNotNull() <-[Call]- qSrcFileAtFra ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    ShouldNotBeNull,
    // CallChain[size=3] = QMyException.ShouldBeEqual <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    ShouldBeEqual,
    // CallChain[size=19] = QMyException.CycleDetected <-[Call]- N.depthFirst() <-[Call]- N.walkDescenda ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    CycleDetected,
    // --- Numbers
    // CallChain[size=6] = QMyException.ShouldNotBeZero <-[Call]- Int.qaNotZero() <-[Call]- CharSequence.qMask() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    ShouldNotBeZero,
    // CallChain[size=5] = QMyException.ShouldBeEvenNumber <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    ShouldBeEvenNumber,

    // CallChain[size=7] = QMyException.IllegalArgument <-[Call]- String.qWithMaxLength() <-[Call]- Stri ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    IllegalArgument,
    // --- File IO
    // CallChain[size=13] = QMyException.FileNotFound <-[Call]- qSrcFileAtFrame() <-[Call]- qSrcFileLine ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    FileNotFound,
    // --- Windows// --- Process// --- Log
    // CallChain[size=13] = QMyException.FetchLinesFail <-[Call]- Path.qFetchLinesAround() <-[Call]- qSr ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    FetchLinesFail,
    // CallChain[size=14] = QMyException.LineNumberExceedsMaximum <-[Call]- Path.qLineAt() <-[Call]- Pat ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    LineNumberExceedsMaximum,
    // --- Reflection
    // CallChain[size=5] = QMyException.TrySetAccessibleFail <-[Call]- AccessibleObject.qTrySetAccessible() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    TrySetAccessibleFail,
    // CallChain[size=6] = QMyException.ConstructorNotFound <-[Call]- Class<T>.qConstructor() <-[Call]- Class<T>.qNewInstance() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    ConstructorNotFound,
    // CallChain[size=5] = QMyException.ClassNotFound <-[Call]- qCallerFileMainClass() <-[Call]- qThisFileMainClass <-[Call]- qTest() <-[Call]- main()[Root]
    ClassNotFound,
    // --- String// --- Math// --- Web// --- Testing
    // CallChain[size=3] = QMyException.TestFail <-[Call]- qTest() <-[Call]- main()[Root]
    TestFail;

    // --- Others

    
}

// CallChain[size=7] = qSTACK_FRAME_FILTER <-[Call]- QException.QException() <-[Ref]- QE.throwItBrac ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal val qSTACK_FRAME_FILTER: (StackWalker.StackFrame) -> Boolean = {
    !it.className.startsWith("org.gradle") &&
            !it.className.startsWith("org.testng") &&
            !it.className.startsWith("worker.org.gradle") &&
            !it.methodName.endsWith("\$default") && it.fileName != null &&
//            !it.className.startsWith("nyab.test")
//            && !it.className.startsWith(QException::class.qualifiedName!!)
//            && it.methodName != "invokeSuspend"
            it.declaringClass != null
            // TODO It's hard to detect the src line of suspend functions
            && it.declaringClass.canonicalName != null
            && !it.declaringClass.canonicalName.startsWith("kotlin.coroutines")
            && !it.declaringClass.canonicalName.startsWith("kotlinx.coroutines")
}