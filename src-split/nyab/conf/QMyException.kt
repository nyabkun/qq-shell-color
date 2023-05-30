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

// CallChain[size=4] = QE <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
internal typealias QE = QMyException

// CallChain[size=5] = QMyException <-[Ref]- QE <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
internal enum class QMyException {
    // CallChain[size=5] = QMyException.Other <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    Other,

    // CallChain[size=5] = QMyException.Unreachable <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    Unreachable,
    // CallChain[size=5] = QMyException.Unsupported <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    Unsupported,

    // CallChain[size=5] = QMyException.ShouldBeTrue <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeTrue,
    // CallChain[size=5] = QMyException.ShouldBeFalse <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeFalse,
    // CallChain[size=5] = QMyException.ShouldBeNull <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeNull,
    // CallChain[size=5] = QMyException.ShouldNotBeNull <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldNotBeNull,
    // CallChain[size=5] = QMyException.ShouldBeEmpty <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeEmpty,
    // CallChain[size=5] = QMyException.ShouldBeEmptyDir <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeEmptyDir,
    // CallChain[size=5] = QMyException.ShouldNotBeEmpty <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldNotBeEmpty,
    // CallChain[size=5] = QMyException.ShouldBeZero <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeZero,
    // CallChain[size=5] = QMyException.ShouldNotBeZero <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldNotBeZero,
    // CallChain[size=5] = QMyException.ShouldBeOne <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeOne,
    // CallChain[size=5] = QMyException.ShouldNotBeOne <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldNotBeOne,
    // CallChain[size=5] = QMyException.ShouldNotBeNested <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldNotBeNested,
    // CallChain[size=5] = QMyException.ShouldNotBeNumber <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldNotBeNumber,
    // CallChain[size=5] = QMyException.ShouldThrow <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldThrow,
    // CallChain[size=5] = QMyException.ShouldBeEqual <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeEqual,
    // CallChain[size=5] = QMyException.ShouldNotBeEqual <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldNotBeEqual,
    // CallChain[size=5] = QMyException.ShouldContain <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldContain,
    // CallChain[size=5] = QMyException.ShouldBeDirectory <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeDirectory,
    // CallChain[size=5] = QMyException.ShouldBeRegularFile <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeRegularFile,
    // CallChain[size=5] = QMyException.ShouldBeRelativePath <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeRelativePath,
    // CallChain[size=5] = QMyException.ShouldBeAbsolutePath <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeAbsolutePath,
    // CallChain[size=5] = QMyException.ShouldNotContain <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldNotContain,
    // CallChain[size=5] = QMyException.ShouldStartWith <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldStartWith,
    // CallChain[size=5] = QMyException.ShouldEndWith <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldEndWith,
    // CallChain[size=5] = QMyException.ShouldNotStartWith <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldNotStartWith,
    // CallChain[size=5] = QMyException.ShouldNotEndWith <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldNotEndWith,
    // CallChain[size=5] = QMyException.ShouldBeOddNumber <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeOddNumber,
    // CallChain[size=5] = QMyException.ShouldBeEvenNumber <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeEvenNumber,
    // CallChain[size=5] = QMyException.ShouldBeSubDirectory <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeSubDirectory,
    // CallChain[size=5] = QMyException.ShouldNotBeSubDirectory <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldNotBeSubDirectory,

    // CallChain[size=5] = QMyException.InvalidMainArguments <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    InvalidMainArguments,
    // CallChain[size=5] = QMyException.CommandFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    CommandFail,
    // CallChain[size=5] = QMyException.CatchException <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    CatchException,
    // CallChain[size=5] = QMyException.FileNotFound <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    FileNotFound,
    // CallChain[size=5] = QMyException.DirectoryNotFound <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    DirectoryNotFound,
    // CallChain[size=5] = QMyException.QToJavaArgFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    QToJavaArgFail,
    // CallChain[size=5] = QMyException.WinOpenProcessFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    WinOpenProcessFail,
    // CallChain[size=5] = QMyException.WinOpenProcessTokenFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    WinOpenProcessTokenFail,
    // CallChain[size=5] = QMyException.ExecExternalProcessFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ExecExternalProcessFail,
    // CallChain[size=5] = QMyException.CompileFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    CompileFail,
    // CallChain[size=5] = QMyException.EscapedNonSpecial <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    EscapedNonSpecial,
    // CallChain[size=5] = QMyException.EndsWithEscapeChar <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    EndsWithEscapeChar,
    // CallChain[size=5] = QMyException.TooManyBitFlags <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    TooManyBitFlags,
    // CallChain[size=5] = QMyException.FileAlreadyExists <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    FileAlreadyExists,
    // CallChain[size=5] = QMyException.DirectoryAlreadyExists <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    DirectoryAlreadyExists,
    // CallChain[size=5] = QMyException.FetchLinesFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    FetchLinesFail,
    // CallChain[size=5] = QMyException.LineNumberExceedsMaximum <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    LineNumberExceedsMaximum,
    // CallChain[size=5] = QMyException.QTryFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    QTryFail,
    // CallChain[size=5] = QMyException.TrySetAccessibleFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    TrySetAccessibleFail,
    // CallChain[size=5] = QMyException.CreateZipFileFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    CreateZipFileFail,
    // CallChain[size=5] = QMyException.SrcFileNotFound <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    SrcFileNotFound,
    // CallChain[size=5] = QMyException.IllegalArgument <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    IllegalArgument,
    // CallChain[size=5] = QMyException.RegexSearchNotFound <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    RegexSearchNotFound,
    // CallChain[size=5] = QMyException.SplitSizeInvalid <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    SplitSizeInvalid,
    // CallChain[size=5] = QMyException.PrimaryConstructorNotFound <-[Propag]- QMyException.STACK_FRAME_ ...  <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    PrimaryConstructorNotFound,
    // CallChain[size=5] = QMyException.OpenBrowserFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    OpenBrowserFail,
    // CallChain[size=5] = QMyException.FileOpenFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    FileOpenFail,
    // CallChain[size=5] = QMyException.FunctionNotFound <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    FunctionNotFound,
    // CallChain[size=5] = QMyException.PropertyNotFound <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    PropertyNotFound,
    // CallChain[size=5] = QMyException.MethodNotFound <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    MethodNotFound,
    // CallChain[size=5] = QMyException.FieldNotFound <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    FieldNotFound,
    // CallChain[size=5] = QMyException.UrlNotFound <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    UrlNotFound,
    // CallChain[size=5] = QMyException.ConstructorNotFound <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ConstructorNotFound,
    // CallChain[size=5] = QMyException.ImportOffsetFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ImportOffsetFail,

    // CallChain[size=5] = QMyException.SaveImageFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    SaveImageFail,
    // CallChain[size=5] = QMyException.LoadImageFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    LoadImageFail,

    // CallChain[size=5] = QMyException.CycleDetected <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    CycleDetected,

    // CallChain[size=5] = QMyException.ShouldBeSquareMatrix <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeSquareMatrix,
    // CallChain[size=5] = QMyException.ShouldBeInvertibleMatrix <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldBeInvertibleMatrix,
    // CallChain[size=5] = QMyException.UnsupportedDifferentiation <-[Propag]- QMyException.STACK_FRAME_ ...  <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    UnsupportedDifferentiation,
    // CallChain[size=5] = QMyException.ShouldNotContainImaginaryNumberOtherThanI <-[Propag]- QMyExcepti ...  <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ShouldNotContainImaginaryNumberOtherThanI,
    // CallChain[size=5] = QMyException.DividedByZero <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    DividedByZero,

    // CallChain[size=5] = QMyException.InvalidPhaseTransition <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    InvalidPhaseTransition,

    // CallChain[size=5] = QMyException.ClassNotFound <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    ClassNotFound,
    // CallChain[size=5] = QMyException.InstantiationFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    InstantiationFail,
    // CallChain[size=5] = QMyException.GetEnvironmentVariableFail <-[Propag]- QMyException.STACK_FRAME_ ...  <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    GetEnvironmentVariableFail,

    // CallChain[size=5] = QMyException.TestFail <-[Propag]- QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
    TestFail
    ;

    companion object {
        // Required to implement extended functions.

        // CallChain[size=4] = QMyException.STACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
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