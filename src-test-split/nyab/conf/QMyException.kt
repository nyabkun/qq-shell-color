/*
 * Copyright 2023. nyabkun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package nyab.conf

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=3] = QE <-[Ref]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
internal typealias QE = QMyException

// CallChain[size=4] = QMyException <-[Ref]- QE <-[Ref]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
internal enum class QMyException {
    // CallChain[size=4] = QMyException.Other <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    Other,

    // CallChain[size=4] = QMyException.Unreachable <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    Unreachable,
    // CallChain[size=4] = QMyException.Unsupported <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    Unsupported,

    // CallChain[size=3] = QMyException.ShouldBeTrue <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ShouldBeTrue,
    // CallChain[size=3] = QMyException.ShouldBeFalse <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ShouldBeFalse,
    // CallChain[size=4] = QMyException.ShouldBeNull <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ShouldBeNull,
    // CallChain[size=4] = QMyException.ShouldNotBeNull <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ShouldNotBeNull,
    // CallChain[size=4] = QMyException.ShouldBeEmpty <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ShouldBeEmpty,
    // CallChain[size=4] = QMyException.ShouldBeEmptyDir <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ShouldBeEmptyDir,
    // CallChain[size=4] = QMyException.ShouldNotBeEmpty <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ShouldNotBeEmpty,
    // CallChain[size=4] = QMyException.ShouldBeZero <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ShouldBeZero,
    // CallChain[size=4] = QMyException.ShouldNotBeZero <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ShouldNotBeZero,
    // CallChain[size=4] = QMyException.ShouldBeOne <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ShouldBeOne,
    // CallChain[size=4] = QMyException.ShouldNotBeOne <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ShouldNotBeOne,
    // CallChain[size=4] = QMyException.ShouldNotBeNested <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ShouldNotBeNested,
    // CallChain[size=4] = QMyException.ShouldNotBeNumber <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ShouldNotBeNumber,
    // CallChain[size=4] = QMyException.ShouldThrow <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ShouldThrow,
    // CallChain[size=3] = QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ShouldBeEqual,
    // CallChain[size=4] = QMyException.ShouldNotBeEqual <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ShouldNotBeEqual,
    // CallChain[size=4] = QMyException.ShouldContain <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ShouldContain,
    // CallChain[size=4] = QMyException.ShouldBeDirectory <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ShouldBeDirectory,
    // CallChain[size=4] = QMyException.ShouldBeRegularFile <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ShouldBeRegularFile,
    // CallChain[size=4] = QMyException.ShouldBeRelativePath <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ShouldBeRelativePath,
    // CallChain[size=4] = QMyException.ShouldBeAbsolutePath <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ShouldBeAbsolutePath,
    // CallChain[size=4] = QMyException.ShouldNotContain <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ShouldNotContain,
    // CallChain[size=4] = QMyException.ShouldStartWith <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ShouldStartWith,
    // CallChain[size=4] = QMyException.ShouldEndWith <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ShouldEndWith,
    // CallChain[size=4] = QMyException.ShouldNotStartWith <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ShouldNotStartWith,
    // CallChain[size=4] = QMyException.ShouldNotEndWith <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ShouldNotEndWith,
    // CallChain[size=4] = QMyException.ShouldBeOddNumber <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ShouldBeOddNumber,
    // CallChain[size=4] = QMyException.ShouldBeEvenNumber <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ShouldBeEvenNumber,
    // CallChain[size=4] = QMyException.ShouldBeSubDirectory <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ShouldBeSubDirectory,
    // CallChain[size=4] = QMyException.ShouldNotBeSubDirectory <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ShouldNotBeSubDirectory,

    // CallChain[size=4] = QMyException.InvalidMainArguments <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    InvalidMainArguments,
    // CallChain[size=4] = QMyException.CommandFail <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    CommandFail,
    // CallChain[size=4] = QMyException.CatchException <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    CatchException,
    // CallChain[size=4] = QMyException.FileNotFound <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    FileNotFound,
    // CallChain[size=4] = QMyException.DirectoryNotFound <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    DirectoryNotFound,
    // CallChain[size=4] = QMyException.QToJavaArgFail <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    QToJavaArgFail,
    // CallChain[size=4] = QMyException.WinOpenProcessFail <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    WinOpenProcessFail,
    // CallChain[size=4] = QMyException.WinOpenProcessTokenFail <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    WinOpenProcessTokenFail,
    // CallChain[size=4] = QMyException.ExecExternalProcessFail <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ExecExternalProcessFail,
    // CallChain[size=4] = QMyException.CompileFail <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    CompileFail,
    // CallChain[size=4] = QMyException.EscapedNonSpecial <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    EscapedNonSpecial,
    // CallChain[size=4] = QMyException.EndsWithEscapeChar <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    EndsWithEscapeChar,
    // CallChain[size=4] = QMyException.TooManyBitFlags <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    TooManyBitFlags,
    // CallChain[size=4] = QMyException.FileAlreadyExists <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    FileAlreadyExists,
    // CallChain[size=4] = QMyException.DirectoryAlreadyExists <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    DirectoryAlreadyExists,
    // CallChain[size=4] = QMyException.FetchLinesFail <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    FetchLinesFail,
    // CallChain[size=4] = QMyException.LineNumberExceedsMaximum <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    LineNumberExceedsMaximum,
    // CallChain[size=4] = QMyException.QTryFail <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    QTryFail,
    // CallChain[size=4] = QMyException.TrySetAccessibleFail <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    TrySetAccessibleFail,
    // CallChain[size=4] = QMyException.CreateZipFileFail <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    CreateZipFileFail,
    // CallChain[size=4] = QMyException.SrcFileNotFound <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    SrcFileNotFound,
    // CallChain[size=4] = QMyException.IllegalArgument <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    IllegalArgument,
    // CallChain[size=4] = QMyException.RegexSearchNotFound <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    RegexSearchNotFound,
    // CallChain[size=4] = QMyException.SplitSizeInvalid <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    SplitSizeInvalid,
    // CallChain[size=4] = QMyException.PrimaryConstructorNotFound <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    PrimaryConstructorNotFound,
    // CallChain[size=4] = QMyException.OpenBrowserFail <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    OpenBrowserFail,
    // CallChain[size=4] = QMyException.FileOpenFail <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    FileOpenFail,
    // CallChain[size=4] = QMyException.FunctionNotFound <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    FunctionNotFound,
    // CallChain[size=4] = QMyException.PropertyNotFound <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    PropertyNotFound,
    // CallChain[size=4] = QMyException.MethodNotFound <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    MethodNotFound,
    // CallChain[size=4] = QMyException.FieldNotFound <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    FieldNotFound,
    // CallChain[size=4] = QMyException.UrlNotFound <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    UrlNotFound,
    // CallChain[size=4] = QMyException.ConstructorNotFound <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ConstructorNotFound,
    // CallChain[size=4] = QMyException.ImportOffsetFail <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ImportOffsetFail,

    // CallChain[size=4] = QMyException.SaveImageFail <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    SaveImageFail,
    // CallChain[size=4] = QMyException.LoadImageFail <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    LoadImageFail,

    // CallChain[size=4] = QMyException.CycleDetected <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    CycleDetected,

    // CallChain[size=4] = QMyException.ShouldBeSquareMatrix <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ShouldBeSquareMatrix,
    // CallChain[size=4] = QMyException.ShouldBeInvertibleMatrix <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ShouldBeInvertibleMatrix,
    // CallChain[size=4] = QMyException.UnsupportedDifferentiation <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    UnsupportedDifferentiation,
    // CallChain[size=4] = QMyException.ShouldNotContainImaginaryNumberOtherThanI <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ShouldNotContainImaginaryNumberOtherThanI,
    // CallChain[size=4] = QMyException.DividedByZero <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    DividedByZero,

    // CallChain[size=4] = QMyException.InvalidPhaseTransition <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    InvalidPhaseTransition,

    // CallChain[size=4] = QMyException.ClassNotFound <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    ClassNotFound,
    // CallChain[size=4] = QMyException.InstantiationFail <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    InstantiationFail,
    // CallChain[size=4] = QMyException.GetEnvironmentVariableFail <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    GetEnvironmentVariableFail,

    // CallChain[size=4] = QMyException.TestFail <-[Propag]- QMyException.ShouldBeEqual <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    TestFail
    ;

    companion object {
        // Required to implement extended functions.

        // CallChain[size=6] = QMyException.STACK_FRAME_FILTER <-[Call]- QException.QException() <-[Ref]- QE ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
        val STACK_FRAME_FILTER: (StackWalker.StackFrame) -> Boolean = {
            !it.className.startsWith("org.gradle") &&
                !it.className.startsWith("org.testng") &&
                !it.className.startsWith("worker.org.gradle") &&
                !it.methodName.endsWith("\$default") && it.fileName != null &&
//            && !it.className.startsWith(QException::class.qualifiedName!!)
//            && it.methodName != "invokeSuspend"
                it.declaringClass != null
//            && it.declaringClass.canonicalName != null
//            && !it.declaringClass.canonicalName.startsWith("kotlin.coroutines.jvm.internal.")
//            && !it.declaringClass.canonicalName.startsWith("kotlinx.coroutines")
        }

        
    }
}