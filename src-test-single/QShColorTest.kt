/*
 * Copyright 2023. nyabkun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

@file:Suppress("UNCHECKED_CAST")

import com.sun.nio.file.ExtendedOpenOption
import java.io.LineNumberReader
import java.io.PrintStream
import java.lang.StackWalker.StackFrame
import java.lang.reflect.AccessibleObject
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.nio.charset.Charset
import java.nio.file.FileVisitOption
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.OpenOption
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.Duration
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import java.util.stream.Stream
import kotlin.concurrent.withLock
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.io.path.absolutePathString
import kotlin.io.path.bufferedReader
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.isSymbolicLink
import kotlin.io.path.name
import kotlin.io.path.reader
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.declaredMemberExtensionFunctions
import kotlin.reflect.full.extensionReceiverParameter
import kotlin.reflect.full.isSuperclassOf
import kotlin.reflect.full.memberExtensionFunctions
import kotlin.reflect.full.memberFunctions
import kotlin.streams.asSequence
import org.intellij.lang.annotations.Language


// qq-shell-color is self-contained single-file library created by nyabkun.
// For this test, the source file is also a single-file, self-contained, and contains a runnable main function.
//  - It can be added to your codebase with a simple copy and paste.
//  - You can modify and redistribute it under the MIT License.
//  - Please add a package declaration if necessary.

// << Root of the CallChain >>
fun main() {
    qTest()
//    qTestHumanCheck()
}

// << Root of the CallChain >>
class QShColorTest {

    // << Root of the CallChain >>
    @QTest
    fun nestRedBlue() {
        """red+blue""".red.blue.qColorAndDecoDebug() shouldBe
                "[Blue][Red]red+blue[End]"
    }

    // << Root of the CallChain >>
    @QTest
    fun fgAndBg() {
        """fg + bg""".qColor(QShColor.Red, QShColor.LightGray).qColorAndDecoDebug() shouldBe
                """[LightGray_BG][Red]fg + bg[End][End]"""
    }

    // << Root of the CallChain >>
    @QTest
    fun underlineRed() {
        // TODO
//        """underline""".underline.red.qColorAndDecoDebug() shouldBe "[Underline][Red]underline[End][End]"
        """underline""".red.underline.qColorAndDecoDebug() shouldBe "[Underline][Red]underline[End]"
    }

    // << Root of the CallChain >>
    @QTest
    fun underline() {
        """underline""".underline.qColorAndDecoDebug() shouldBe "[Underline]underline[End]"
    }

    // << Root of the CallChain >>
    @QTest
    fun bold() {
        """bold""".bold.qColorAndDecoDebug() shouldBe "[Bold]bold[End]"
    }

    // << Root of the CallChain >>
    @QTest
    fun italic() {
        """italic""".italic.qColorAndDecoDebug() shouldBe "[Italic]italic[End]"
    }

    // << Root of the CallChain >>
    @QTest
    fun colourful() {
        ("c".yellow + "o".blue + "l".red + "o".magenta + "u".green + "r".cyan + "f".yellow + "u".blue + "l".red).qColorAndDecoDebug() shouldBe """
            [Yellow]c[End][Blue]o[End][Red]l[End][Magenta]o[End][Green]u[End][Cyan]r[End][Yellow]f[End][Blue]u[End][Red]l[End]
        """.trimIndent()
    }

    // << Root of the CallChain >>
    @QTest
    fun background() {
        "GreenBG".qColor(fg = null, bg = QShColor.Green, false).qColorAndDecoDebug() shouldBe """
            [Green_BG]GreenBG[End]
        """.trimIndent()
    }

    // << Root of the CallChain >>
    @QTest
    fun foregroundAndBackground() {
        "RedFG_YellowBG".qColor(fg = QShColor.Red, bg = QShColor.Yellow, false).qColorAndDecoDebug() shouldBe """
            [Yellow_BG][Red]RedFG_YellowBG[End][End]
        """.trimIndent()
    }

    // << Root of the CallChain >>
    @QTest
    fun multiline() {
        "abc\ndef\nhij".qColor(fg = QShColor.Red, bg = null).qColorAndDecoDebug() shouldBe """
            [Red]abc[End]
            [Red]def[End]
            [Red]hij[End]
        """.trimIndent()

        "abc\ndef\nhij".qColor(fg = null, bg = QShColor.Blue).qColorAndDecoDebug() shouldBe """
            [Blue_BG]abc[End]
            [Blue_BG]def[End]
            [Blue_BG]hij[End]
        """.trimIndent()
    }

    // << Root of the CallChain >>
    @QTest
    fun multiline_fg_bg() {
        "abc\ndef\nhij".qColor(fg = QShColor.Red, bg = QShColor.Blue).qColorAndDecoDebug() shouldBe """
            [Blue_BG][Red]abc[End][End]
            [Blue_BG][Red]def[End][End]
            [Blue_BG][Red]hij[End][End]
        """.trimIndent()
    }

    // << Root of the CallChain >>
    @QTest
    fun colorTarget() {
        """val color = "green"""".qColorTarget(
            ptn = """val""".toRegex(),
            fg = QShColor.Magenta
        ).qColorTarget(
            ptn = """".*?"""".toRegex(),
            fg = QShColor.Green
        ).qColorAndDecoDebug() shouldBe """
            [Magenta]val[End] color = [Green]"green"[End]
        """.trimIndent()
    }

    // << Root of the CallChain >>
    @QTest
    fun noColor() {
        """val color = "text"""".qColorTarget(
            ptn = """val""".toRegex(),
            fg = QShColor.Magenta
        ).qColorTarget(
            ptn = """".*?"""".toRegex(),
            fg = QShColor.Green
        ).noColor.qColorAndDecoDebug() shouldBe """val color = "text""""
    }

    // << Root of the CallChain >>
    @QTest
    fun nest() {
        "${"▧".red} Test Start ${"▧".red}\nTestClass".blue.qColorAndDecoDebug() shouldBe """
            [Blue][Red]▧[End][Blue] Test Start [Red]▧[End]
            [Blue]TestClass[End]
        """.trimIndent()
    }

    // << Root of the CallChain >>
    @QTest
    fun nest2() {
        "a${"b".blue}c".red.qColorAndDecoDebug() shouldBe
                "[Red]a[Blue]b[End][Red]c[End]"
    }
}


// region Src from Non-Root API Files -- Auto Generated by nyab.conf.QCompactLib.kt
// ================================================================================


// CallChain[size=4] = QMyException <-[Ref]- QE <-[Ref]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private enum class QMyException {
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

// CallChain[size=10] = QMyLog <-[Ref]- QLogStyle <-[Ref]- QLogStyle.SRC_AND_STACK <-[Call]- QExcept ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private object QMyLog {
    // CallChain[size=10] = QMyLog.out <-[Call]- QLogStyle <-[Ref]- QLogStyle.SRC_AND_STACK <-[Call]- QE ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    /**
     * Default output stream
     */
    val out: QOut = QOut.CONSOLE

    // CallChain[size=4] = QMyLog.no_format <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    var no_format: Boolean = false
}

// CallChain[size=6] = QMyMark <-[Ref]- QException.QException() <-[Ref]- QE.throwItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private object QMyMark {
    // CallChain[size=4] = QMyMark.TEST_METHOD <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    val TEST_METHOD = "☕".yellow
    // CallChain[size=6] = QMyMark.WARN <-[Call]- QException.QException() <-[Ref]- QE.throwItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    val WARN = "⚠".yellow
    // CallChain[size=3] = QMyMark.TEST_START <-[Call]- qTest() <-[Call]- main()[Root]
    val TEST_START = "☘".yellow
    
}

// CallChain[size=9] = QMyPath <-[Ref]- qLogStackFrames() <-[Call]- QException.mySrcAndStack <-[Call ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private object QMyPath {
    // -- dirs

    // CallChain[size=10] = QMyPath.src <-[Call]- QMyPath.src_root <-[Call]- qLogStackFrames() <-[Call]- ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    val src = "src".path
    // CallChain[size=10] = QMyPath.src_java <-[Call]- QMyPath.src_root <-[Call]- qLogStackFrames() <-[C ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    val src_java = "src-java".path
    // CallChain[size=10] = QMyPath.src_build <-[Call]- QMyPath.src_root <-[Call]- qLogStackFrames() <-[ ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    val src_build = "src-build".path
    // CallChain[size=10] = QMyPath.src_experiment <-[Call]- QMyPath.src_root <-[Call]- qLogStackFrames( ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    val src_experiment = "src-experiment".path
    // CallChain[size=10] = QMyPath.src_plugin <-[Call]- QMyPath.src_root <-[Call]- qLogStackFrames() <- ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    val src_plugin = "src-plugin".path
    // CallChain[size=10] = QMyPath.src_config <-[Call]- QMyPath.src_root <-[Call]- qLogStackFrames() <- ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    val src_config = "src-config".path
    // CallChain[size=10] = QMyPath.src_test <-[Call]- QMyPath.src_root <-[Call]- qLogStackFrames() <-[C ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    val src_test = "src-test".path
    // --- dir list
    // CallChain[size=9] = QMyPath.src_root <-[Call]- qLogStackFrames() <-[Call]- QException.mySrcAndSta ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    val src_root: List<Path> by lazy {
        val base = listOf(
            src,
            src_test,
            src_experiment,
            src_config,
            src_plugin,
            src_java,
            src_build,
            "src".path,
            "test".path,
            "src/main/kotlin".path,
            "src/test/kotlin".path,
            "src/main/java".path,
            "src/test/java".path,
//            ".".path,
        ).filter { it.exists() }

        val search = Paths.get(".").qListByMatch(type = QFType.Dir, nameMatch = QM.startsWith("src-"), maxDepth = 1)

        (base + search).distinct()
    }

    // -- files
}

// CallChain[size=4] = QMyTest <-[Ref]- qOkToTest() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private object QMyTest {
    // CallChain[size=4] = QMyTest.forceTestMode <-[Call]- qOkToTest() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    const val forceTestMode = true
    
}

// CallChain[size=6] = QMyToString <-[Call]- qToStringRegistry <-[Call]- Any?.qToString() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private object QMyToString {
    
}

// CallChain[size=3] = QE <-[Ref]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private typealias QE = QMyException

// CallChain[size=14] = String.qMatches() <-[Call]- Path.qFind() <-[Call]- Collection<Path>.qFind()  ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun String.qMatches(matcher: QM): Boolean = matcher.matches(this)

// CallChain[size=5] = not <-[Call]- QM.notExact() <-[Call]- QMMethod.nameNotExact() <-[Call]- qTest() <-[Call]- main()[Root]
private val QM.not: QM
    get() = QMatchNot(this)

// CallChain[size=6] = QMatchNot <-[Call]- not <-[Call]- QM.notExact() <-[Call]- QMMethod.nameNotExact() <-[Call]- qTest() <-[Call]- main()[Root]
private class QMatchNot(val matcher: QM) : QM {
    // CallChain[size=7] = QMatchNot.matches() <-[Propag]- QMatchNot <-[Call]- not <-[Call]- QM.notExact() <-[Call]- QMMethod.nameNotExact() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun matches(text: String): Boolean = !matcher.matches(text)

    // CallChain[size=7] = QMatchNot.toString() <-[Propag]- QMatchNot <-[Call]- not <-[Call]- QM.notExact() <-[Call]- QMMethod.nameNotExact() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun toString(): String {
        return this::class.simpleName.toString() + "(matcher=$matcher)"
    }
}

// CallChain[size=14] = QMatchAny <-[Call]- QM.isAny() <-[Propag]- QM.exact() <-[Call]- qSrcFileAtFr ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private object QMatchAny : QM {
    // CallChain[size=15] = QMatchAny.matches() <-[Propag]- QMatchAny <-[Call]- QM.isAny() <-[Propag]- Q ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun matches(text: String): Boolean {
        return true
    }

    // CallChain[size=15] = QMatchAny.toString() <-[Propag]- QMatchAny <-[Call]- QM.isAny() <-[Propag]-  ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun toString(): String {
        return this::class.simpleName.toString()
    }
}

// CallChain[size=14] = QMatchNone <-[Call]- QM.isNone() <-[Propag]- QM.exact() <-[Call]- qSrcFileAt ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private object QMatchNone : QM {
    // CallChain[size=15] = QMatchNone.matches() <-[Propag]- QMatchNone <-[Call]- QM.isNone() <-[Propag] ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun matches(text: String): Boolean {
        return false
    }

    // CallChain[size=15] = QMatchNone.toString() <-[Propag]- QMatchNone <-[Call]- QM.isNone() <-[Propag ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun toString(): String {
        return this::class.simpleName.toString()
    }
}

// CallChain[size=13] = QM <-[Ref]- QM.exact() <-[Call]- qSrcFileAtFrame() <-[Call]- qSrcFileLinesAt ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private interface QM {
    // CallChain[size=13] = QM.matches() <-[Propag]- QM.exact() <-[Call]- qSrcFileAtFrame() <-[Call]- qS ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun matches(text: String): Boolean

    // CallChain[size=13] = QM.isAny() <-[Propag]- QM.exact() <-[Call]- qSrcFileAtFrame() <-[Call]- qSrc ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun isAny(): Boolean = this == QMatchAny

    // CallChain[size=13] = QM.isNone() <-[Propag]- QM.exact() <-[Call]- qSrcFileAtFrame() <-[Call]- qSr ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun isNone(): Boolean = this == QMatchNone

    companion object {
        // CallChain[size=12] = QM.exact() <-[Call]- qSrcFileAtFrame() <-[Call]- qSrcFileLinesAtFrame() <-[C ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
        fun exact(text: String, ignoreCase: Boolean = false): QM = QExactMatch(text, ignoreCase)

        // CallChain[size=4] = QM.notExact() <-[Call]- QMMethod.nameNotExact() <-[Call]- qTest() <-[Call]- main()[Root]
        fun notExact(text: String, ignoreCase: Boolean = false): QM = QExactMatch(text, ignoreCase).not

        // CallChain[size=10] = QM.startsWith() <-[Call]- QMyPath.src_root <-[Call]- qLogStackFrames() <-[Ca ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
        fun startsWith(text: String, ignoreCase: Boolean = false): QM = QStartsWithMatch(text, ignoreCase)

        
    }
}

// CallChain[size=13] = QExactMatch <-[Call]- QM.exact() <-[Call]- qSrcFileAtFrame() <-[Call]- qSrcF ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private class QExactMatch(val textExact: String, val ignoreCase: Boolean = false) : QM {
    // CallChain[size=14] = QExactMatch.matches() <-[Propag]- QExactMatch <-[Call]- QM.exact() <-[Call]- ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun matches(text: String): Boolean {
        return text.equals(textExact, ignoreCase)
    }

    // CallChain[size=14] = QExactMatch.toString() <-[Propag]- QExactMatch <-[Call]- QM.exact() <-[Call] ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun toString(): String {
        return this::class.simpleName + "(textExact=$textExact, ignoreCase=$ignoreCase)"
    }
}

// CallChain[size=11] = QStartsWithMatch <-[Call]- QM.startsWith() <-[Call]- QMyPath.src_root <-[Cal ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private class QStartsWithMatch(val textStartsWith: String, val ignoreCase: Boolean = false) : QM {
    // CallChain[size=12] = QStartsWithMatch.matches() <-[Propag]- QStartsWithMatch <-[Call]- QM.startsW ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun matches(text: String): Boolean {
        return text.startsWith(textStartsWith, ignoreCase)
    }

    // CallChain[size=12] = QStartsWithMatch.toString() <-[Propag]- QStartsWithMatch <-[Call]- QM.starts ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun toString(): String {
        return this::class.simpleName + "(textStartsWith=$textStartsWith, ignoreCase=$ignoreCase)"
    }
}

// CallChain[size=7] = qAnd() <-[Call]- QMFunc.and() <-[Call]- qToStringRegistry <-[Call]- Any?.qToString() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun qAnd(vararg matches: QMFunc): QMFunc = QMatchFuncAnd(*matches)

// CallChain[size=6] = QMFunc.and() <-[Call]- qToStringRegistry <-[Call]- Any?.qToString() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private infix fun QMFunc.and(match: QMFunc): QMFunc {
    return if (this is QMatchFuncAnd) {
        QMatchFuncAnd(*matchList, match)
    } else {
        qAnd(this, match)
    }
}

// CallChain[size=8] = QMatchFuncAny <-[Call]- QMFunc.isAny() <-[Propag]- QMFunc.IncludeExtensionsIn ... ing() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private object QMatchFuncAny : QMFuncA() {
    // CallChain[size=9] = QMatchFuncAny.matches() <-[Propag]- QMatchFuncAny <-[Call]- QMFunc.isAny() <- ... ing() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun matches(value: KFunction<*>): Boolean {
        return true
    }
}

// CallChain[size=8] = QMatchFuncNone <-[Call]- QMFunc.isNone() <-[Propag]- QMFunc.IncludeExtensions ... ing() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private object QMatchFuncNone : QMFuncA() {
    // CallChain[size=9] = QMatchFuncNone.matches() <-[Propag]- QMatchFuncNone <-[Call]- QMFunc.isNone() ... ing() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun matches(value: KFunction<*>): Boolean {
        return false
    }
}

// CallChain[size=7] = QMatchFuncDeclaredOnly <-[Call]- QMFunc.DeclaredOnly <-[Call]- qToStringRegis ... ing() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private object QMatchFuncDeclaredOnly : QMFuncA() {
    // CallChain[size=8] = QMatchFuncDeclaredOnly.declaredOnly <-[Propag]- QMatchFuncDeclaredOnly <-[Cal ... ing() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override val declaredOnly = true

    // CallChain[size=8] = QMatchFuncDeclaredOnly.matches() <-[Propag]- QMatchFuncDeclaredOnly <-[Call]- ... ing() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun matches(value: KFunction<*>): Boolean {
        return true
    }
}

// CallChain[size=7] = QMatchFuncIncludeExtensionsInClass <-[Call]- QMFunc.IncludeExtensionsInClass  ... ing() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private object QMatchFuncIncludeExtensionsInClass : QMFuncA() {
    // CallChain[size=8] = QMatchFuncIncludeExtensionsInClass.includeExtensionsInClass <-[Propag]- QMatc ... ing() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override val includeExtensionsInClass = true

    // CallChain[size=8] = QMatchFuncIncludeExtensionsInClass.matches() <-[Propag]- QMatchFuncIncludeExt ... ing() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun matches(value: KFunction<*>): Boolean {
        return true
    }
}

// CallChain[size=7] = QMatchFuncAnd <-[Ref]- QMFunc.and() <-[Call]- qToStringRegistry <-[Call]- Any ... ing() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private class QMatchFuncAnd(vararg match: QMFunc) : QMFuncA() {
    // CallChain[size=7] = QMatchFuncAnd.matchList <-[Call]- QMFunc.and() <-[Call]- qToStringRegistry <- ... ing() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    val matchList = match

    // CallChain[size=8] = QMatchFuncAnd.declaredOnly <-[Propag]- QMatchFuncAnd.matchList <-[Call]- QMFu ... ing() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override val declaredOnly = matchList.any { it.declaredOnly }

    // CallChain[size=8] = QMatchFuncAnd.includeExtensionsInClass <-[Propag]- QMatchFuncAnd.matchList <- ... ing() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override val includeExtensionsInClass: Boolean = matchList.any { it.includeExtensionsInClass }

    // CallChain[size=8] = QMatchFuncAnd.matches() <-[Propag]- QMatchFuncAnd.matchList <-[Call]- QMFunc. ... ing() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun matches(value: KFunction<*>): Boolean {
        return matchList.all { it.matches(value) }
    }
}

// CallChain[size=9] = QMFuncA <-[Call]- QMatchFuncNone <-[Call]- QMFunc.isNone() <-[Propag]- QMFunc ... ing() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private abstract class QMFuncA : QMFunc {
    // CallChain[size=10] = QMFuncA.declaredOnly <-[Propag]- QMFuncA <-[Call]- QMatchFuncNone <-[Call]-  ... ing() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override val declaredOnly: Boolean = false
    // CallChain[size=10] = QMFuncA.includeExtensionsInClass <-[Propag]- QMFuncA <-[Call]- QMatchFuncNon ... ing() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override val includeExtensionsInClass: Boolean = false
}

// CallChain[size=7] = QMFunc <-[Ref]- QMFunc.IncludeExtensionsInClass <-[Call]- qToStringRegistry < ... ing() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private interface QMFunc {
    // CallChain[size=7] = QMFunc.declaredOnly <-[Propag]- QMFunc.IncludeExtensionsInClass <-[Call]- qTo ... ing() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    val declaredOnly: Boolean

    // CallChain[size=7] = QMFunc.includeExtensionsInClass <-[Propag]- QMFunc.IncludeExtensionsInClass < ... ing() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    val includeExtensionsInClass: Boolean

    // CallChain[size=7] = QMFunc.matches() <-[Propag]- QMFunc.IncludeExtensionsInClass <-[Call]- qToStr ... ing() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun matches(value: KFunction<*>): Boolean

    // CallChain[size=7] = QMFunc.isAny() <-[Propag]- QMFunc.IncludeExtensionsInClass <-[Call]- qToStrin ... ing() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun isAny(): Boolean = this == QMatchFuncAny

    // CallChain[size=7] = QMFunc.isNone() <-[Propag]- QMFunc.IncludeExtensionsInClass <-[Call]- qToStri ... ing() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun isNone(): Boolean = this == QMatchFuncNone

    companion object {
        // CallChain[size=6] = QMFunc.DeclaredOnly <-[Call]- qToStringRegistry <-[Call]- Any?.qToString() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
        val DeclaredOnly: QMFunc = QMatchFuncDeclaredOnly

        // CallChain[size=6] = QMFunc.IncludeExtensionsInClass <-[Call]- qToStringRegistry <-[Call]- Any?.qToString() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
        // TODO OnlyExtensionsInClass
        val IncludeExtensionsInClass: QMFunc = QMatchFuncIncludeExtensionsInClass

        

        // TODO vararg, nullability, param names, type parameter
        // TODO handle createType() more carefully

        // CallChain[size=6] = QMFunc.nameExact() <-[Call]- qToStringRegistry <-[Call]- Any?.qToString() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
        fun nameExact(text: String, ignoreCase: Boolean = false): QMFunc {
            return QMatchFuncName(QM.exact(text, ignoreCase = ignoreCase))
        }

        
    }
}

// CallChain[size=7] = QMatchFuncName <-[Call]- QMFunc.nameExact() <-[Call]- qToStringRegistry <-[Ca ... ing() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private class QMatchFuncName(val nameMatcher: QM) : QMFuncA() {
    // CallChain[size=8] = QMatchFuncName.matches() <-[Propag]- QMatchFuncName <-[Call]- QMFunc.nameExac ... ing() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun matches(value: KFunction<*>): Boolean {
        return nameMatcher.matches(value.name)
    }

    // CallChain[size=8] = QMatchFuncName.toString() <-[Propag]- QMatchFuncName <-[Call]- QMFunc.nameExa ... ing() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun toString(): String {
        return this::class.simpleName + ":" + nameMatcher.toString()
    }
}

// CallChain[size=4] = not <-[Call]- QMMethod.notAnnotation() <-[Call]- qTest() <-[Call]- main()[Root]
private val QMMethod.not: QMMethod
    get() = QMatchMethodNot(this)

// CallChain[size=4] = qAnd() <-[Call]- QMMethod.and() <-[Call]- qTest() <-[Call]- main()[Root]
private fun qAnd(vararg matches: QMMethod): QMMethod = QMatchMethodAnd(*matches)

// CallChain[size=4] = qOr() <-[Call]- QMMethod.or() <-[Call]- qTest() <-[Call]- main()[Root]
private fun qOr(vararg matches: QMMethod): QMMethod = QMatchMethodOr(*matches)

// CallChain[size=3] = QMMethod.and() <-[Call]- qTest() <-[Call]- main()[Root]
private infix fun QMMethod.and(match: QMMethod): QMMethod {
    return if (this is QMatchMethodAnd) {
        QMatchMethodAnd(*matchList, match)
    } else {
        qAnd(this, match)
    }
}

// CallChain[size=3] = QMMethod.or() <-[Call]- qTest() <-[Call]- main()[Root]
private infix fun QMMethod.or(match: QMMethod): QMMethod {
    return if (this is QMatchMethodOr) {
        QMatchMethodOr(*matchList, match)
    } else {
        qOr(this, match)
    }
}

// CallChain[size=5] = QMatchMethodNot <-[Call]- not <-[Call]- QMMethod.notAnnotation() <-[Call]- qTest() <-[Call]- main()[Root]
private class QMatchMethodNot(val matcher: QMMethod) : QMMethodA() {
    // CallChain[size=6] = QMatchMethodNot.matches() <-[Propag]- QMatchMethodNot <-[Call]- not <-[Call]- QMMethod.notAnnotation() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun matches(value: Method): Boolean = !matcher.matches(value)
}

// CallChain[size=5] = QMatchMethodAny <-[Call]- QMMethod.isAny() <-[Propag]- QMMethod.matches() <-[Call]- qTest() <-[Call]- main()[Root]
private object QMatchMethodAny : QMMethodA() {
    // CallChain[size=6] = QMatchMethodAny.matches() <-[Propag]- QMatchMethodAny <-[Call]- QMMethod.isAny() <-[Propag]- QMMethod.matches() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun matches(value: Method): Boolean {
        return true
    }
}

// CallChain[size=5] = QMatchMethodNone <-[Call]- QMMethod.isNone() <-[Propag]- QMMethod.matches() <-[Call]- qTest() <-[Call]- main()[Root]
private object QMatchMethodNone : QMMethodA() {
    // CallChain[size=6] = QMatchMethodNone.matches() <-[Propag]- QMatchMethodNone <-[Call]- QMMethod.isNone() <-[Propag]- QMMethod.matches() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun matches(value: Method): Boolean {
        return false
    }
}

// CallChain[size=4] = QMatchMethodDeclaredOnly <-[Call]- QMMethod.DeclaredOnly <-[Call]- qTest() <-[Call]- main()[Root]
private object QMatchMethodDeclaredOnly : QMMethodA() {
    // CallChain[size=5] = QMatchMethodDeclaredOnly.declaredOnly <-[Propag]- QMatchMethodDeclaredOnly <-[Call]- QMMethod.DeclaredOnly <-[Call]- qTest() <-[Call]- main()[Root]
    override val declaredOnly = true

    // CallChain[size=5] = QMatchMethodDeclaredOnly.matches() <-[Propag]- QMatchMethodDeclaredOnly <-[Call]- QMMethod.DeclaredOnly <-[Call]- qTest() <-[Call]- main()[Root]
    override fun matches(value: Method): Boolean {
        return true
    }
}

// CallChain[size=4] = QMatchMethodAnd <-[Ref]- QMMethod.and() <-[Call]- qTest() <-[Call]- main()[Root]
private class QMatchMethodAnd(vararg match: QMMethod) : QMMethodA() {
    // CallChain[size=4] = QMatchMethodAnd.matchList <-[Call]- QMMethod.and() <-[Call]- qTest() <-[Call]- main()[Root]
    val matchList = match

    // CallChain[size=5] = QMatchMethodAnd.declaredOnly <-[Propag]- QMatchMethodAnd.matchList <-[Call]- QMMethod.and() <-[Call]- qTest() <-[Call]- main()[Root]
    override val declaredOnly = matchList.any { it.declaredOnly }

    // CallChain[size=5] = QMatchMethodAnd.matches() <-[Propag]- QMatchMethodAnd.matchList <-[Call]- QMMethod.and() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun matches(value: Method): Boolean {
        return matchList.all { it.matches(value) }
    }
}

// CallChain[size=4] = QMatchMethodOr <-[Ref]- QMMethod.or() <-[Call]- qTest() <-[Call]- main()[Root]
private class QMatchMethodOr(vararg match: QMMethod) : QMMethodA() {
    // CallChain[size=4] = QMatchMethodOr.matchList <-[Call]- QMMethod.or() <-[Call]- qTest() <-[Call]- main()[Root]
    val matchList = match

    // CallChain[size=5] = QMatchMethodOr.declaredOnly <-[Propag]- QMatchMethodOr.matchList <-[Call]- QMMethod.or() <-[Call]- qTest() <-[Call]- main()[Root]
    override val declaredOnly = matchList.any { it.declaredOnly }

    // CallChain[size=5] = QMatchMethodOr.matches() <-[Propag]- QMatchMethodOr.matchList <-[Call]- QMMethod.or() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun matches(value: Method): Boolean {
        return matchList.any { it.matches(value) }
    }
}

// CallChain[size=6] = QMMethodA <-[Call]- QMatchMethodNone <-[Call]- QMMethod.isNone() <-[Propag]- QMMethod.matches() <-[Call]- qTest() <-[Call]- main()[Root]
private abstract class QMMethodA : QMMethod {
    // CallChain[size=7] = QMMethodA.declaredOnly <-[Propag]- QMMethodA <-[Call]- QMatchMethodNone <-[Call]- QMMethod.isNone() <-[Propag]- QMMethod.matches() <-[Call]- qTest() <-[Call]- main()[Root]
    override val declaredOnly: Boolean = false
}

// CallChain[size=3] = QMMethod <-[Ref]- qTest() <-[Call]- main()[Root]
private interface QMMethod {
    // CallChain[size=4] = QMMethod.declaredOnly <-[Propag]- QMMethod.matches() <-[Call]- qTest() <-[Call]- main()[Root]
    val declaredOnly: Boolean

    // CallChain[size=3] = QMMethod.matches() <-[Call]- qTest() <-[Call]- main()[Root]
    fun matches(value: Method): Boolean

    // CallChain[size=4] = QMMethod.isAny() <-[Propag]- QMMethod.matches() <-[Call]- qTest() <-[Call]- main()[Root]
    fun isAny(): Boolean = this == QMatchMethodAny

    // CallChain[size=4] = QMMethod.isNone() <-[Propag]- QMMethod.matches() <-[Call]- qTest() <-[Call]- main()[Root]
    fun isNone(): Boolean = this == QMatchMethodNone

    companion object {
        // CallChain[size=3] = QMMethod.DeclaredOnly <-[Call]- qTest() <-[Call]- main()[Root]
        val DeclaredOnly: QMMethod = QMatchMethodDeclaredOnly

        // CallChain[size=3] = QMMethod.NoParams <-[Call]- qTest() <-[Call]- main()[Root]
        val NoParams: QMMethod = QMatchMethodParams(arrayOf())

        // TODO vararg, param names, type parameter

        // CallChain[size=3] = QMMethod.annotation() <-[Call]- qTest() <-[Call]- main()[Root]
        fun <T : Annotation> annotation(annotation: KClass<T>, matcher: (T) -> Boolean = { true }): QMMethod {
            return QMatchMethodAnnotation(annotation.java, matcher)
        }

        // CallChain[size=3] = QMMethod.notAnnotation() <-[Call]- qTest() <-[Call]- main()[Root]
        fun <T : Annotation> notAnnotation(annotation: KClass<T>, matcher: (T) -> Boolean = { true }): QMMethod {
            return QMatchMethodAnnotation(annotation.java, matcher).not
        }

        // CallChain[size=3] = QMMethod.annotation() <-[Call]- qTest() <-[Call]- main()[Root]
        fun annotation(annotationName: String): QMMethod {
            return QMatchMethodAnnotationName(QM.exact(annotationName))
        }

        // CallChain[size=3] = QMMethod.nameNotExact() <-[Call]- qTest() <-[Call]- main()[Root]
        fun nameNotExact(text: String, ignoreCase: Boolean = false): QMMethod {
            return QMatchMethodName(QM.notExact(text, ignoreCase = ignoreCase))
        }

        
    }
}

// CallChain[size=4] = QMatchMethodName <-[Call]- QMMethod.nameNotExact() <-[Call]- qTest() <-[Call]- main()[Root]
private class QMatchMethodName(val nameMatcher: QM) : QMMethodA() {
    // CallChain[size=5] = QMatchMethodName.matches() <-[Propag]- QMatchMethodName <-[Call]- QMMethod.nameNotExact() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun matches(value: Method): Boolean {
        return nameMatcher.matches(value.name)
    }
}

// CallChain[size=4] = QMatchMethodParams <-[Call]- QMMethod.NoParams <-[Call]- qTest() <-[Call]- main()[Root]
private class QMatchMethodParams(val params: Array<Class<*>?>) : QMMethodA() {
    // CallChain[size=5] = QMatchMethodParams.matches() <-[Propag]- QMatchMethodParams <-[Call]- QMMethod.NoParams <-[Call]- qTest() <-[Call]- main()[Root]
    override fun matches(value: Method): Boolean {
        if (value.parameters.size != params.size)
            return false

        if (params.isEmpty())
            return true

        return params.withIndex().all { (idx, actualParam) ->
            // Number is assignable from Integer
            if (actualParam == null) {
                true
            } else {
                // Number is assignable from Integer
                value.parameters[idx].type.qIsAssignableFrom(actualParam)
            }
        }
    }
}

// CallChain[size=4] = QMatchMethodAnnotation <-[Call]- QMMethod.notAnnotation() <-[Call]- qTest() <-[Call]- main()[Root]
private class QMatchMethodAnnotation<T : Annotation>(val annotation: Class<T>, val matcher: (T) -> Boolean) : QMMethodA() {
    // CallChain[size=5] = QMatchMethodAnnotation.matches() <-[Propag]- QMatchMethodAnnotation <-[Call]- QMMethod.notAnnotation() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun matches(value: Method): Boolean {
        val anno = value.getAnnotation(annotation) ?: return false

        return matcher(anno)
    }
}

// CallChain[size=4] = QMatchMethodAnnotationName <-[Call]- QMMethod.annotation() <-[Call]- qTest() <-[Call]- main()[Root]
private class QMatchMethodAnnotationName(val nameMatcher: QM, val declaredAnnotationsOnly: Boolean = false) : QMMethodA() {
    // CallChain[size=5] = QMatchMethodAnnotationName.matches() <-[Propag]- QMatchMethodAnnotationName <-[Call]- QMMethod.annotation() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun matches(value: Method): Boolean {
        val annotations = if (declaredAnnotationsOnly) {
            value.declaredAnnotations
        } else {
            value.annotations
        }

        return annotations.any { anno: Annotation ->
            nameMatcher.matches(anno.annotationClass.java.simpleName)
        }
    }
}

// CallChain[size=2] = QTest <-[Call]- QShColorTest.nest2()[Root]
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
private annotation class QTest(val testOnlyThis: Boolean = false)

// CallChain[size=3] = QTestHumanCheckRequired <-[Ref]- qTest() <-[Call]- main()[Root]
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
private annotation class QTestHumanCheckRequired(val testOnlyThis: Boolean = false)

// CallChain[size=3] = QBeforeEach <-[Ref]- qTest() <-[Call]- main()[Root]
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
private annotation class QBeforeEach

// CallChain[size=3] = QAfterEach <-[Ref]- qTest() <-[Call]- main()[Root]
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
private annotation class QAfterEach

// CallChain[size=5] = QTestResultElement <-[Ref]- QTestResult.QTestResult() <-[Call]- QTestResult.numFail <-[Call]- qTest() <-[Call]- main()[Root]
private data class QTestResultElement(val method: Method, val cause: Throwable?) {
    // CallChain[size=4] = QTestResultElement.success <-[Call]- QTestResult.numFail <-[Call]- qTest() <-[Call]- main()[Root]
    val success: Boolean
        get() = cause == null
}

// CallChain[size=5] = allTestedMethods <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private val List<QTestResultElement>.allTestedMethods: String
    get() =
        "\n[${"Tested".light_blue}]\n" +
                this.joinToString("\n") {
                    if (it.success) {
                        it.method.qName().green
                    } else {
                        it.method.qName().light_red
                    }
                }

// CallChain[size=3] = QTestResult <-[Ref]- qTest() <-[Call]- main()[Root]
private class QTestResult(val elements: List<QTestResultElement>, val time: Long) {
    // CallChain[size=5] = QTestResult.targetClasses <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    val targetClasses = elements.map { it.method.declaringClass.canonicalName }

    // CallChain[size=6] = QTestResult.numSuccess <-[Call]- QTestResult.str <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    val numSuccess = elements.count { it.success }
    // CallChain[size=3] = QTestResult.numFail <-[Call]- qTest() <-[Call]- main()[Root]
    val numFail = elements.count { !it.success }

    // CallChain[size=5] = QTestResult.str <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    val str = qBrackets("Result".blue, "$numSuccess / ${numFail + numSuccess}", "Time".blue, time.qFormatDuration())

    // CallChain[size=4] = QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    fun printIt(out: QOut = QOut.CONSOLE) {
        out.separator(start = "")

        if (numFail > 0) {
            out.println("Fail ...".red)
            out.println(str)
            out.println(elements.allTestedMethods)

            elements.filter { !it.success }.forEach { ele ->
                out.separator()

                val cause = if (ele.cause != null && ele.cause is QException) {
                    ele.cause
                } else if (ele.cause?.cause != null && ele.cause.cause is QException) {
                    ele.cause.cause
                } else {
                    ele.cause
                }

                val causeStr = if (cause != null && cause is QException) {
                    cause.type.name
                } else if (cause != null) {
                    cause::class.simpleName ?: cause::class.java.simpleName
                } else {
                    "null"
                }

                val msg = qBrackets("Failed".light_blue, ele.method.name.red, "Cause".light_blue, causeStr.red)

                val mySrcAndMsg = if (cause != null && cause is QException) {
                    val stackColoringRegex = targetClasses.joinToString("|") { ta ->
                        """(.*($ta|${ta}Kt).*?)\("""
                    }.re

                    val stackStr = stackColoringRegex.replace(cause.mySrcAndStack, "$1".qColor(QShColor.Blue) + "(")

                    cause.message + "\n\n" + stackStr
                } else {
                    ""
                }

                if (mySrcAndMsg.isNotEmpty()) {
                    out.println(msg + "\n")
                    out.println(mySrcAndMsg)
                } else if (cause != null) {
                    out.println(msg)
                    out.println("StackTrace >>>>>")
                    out.println(cause.stackTraceToString())
                }
            }
        } else {
            out.println("${"✨".yellow} ${" Success ".green} ${"✨".yellow}".green + "\n")
            out.println(str)
            out.println(elements.allTestedMethods)
        }
    }
}

// CallChain[size=3] = qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun qTestMethods(
    methodsToTest: List<Method>,
    beforeMethod: List<Method> = emptyList(),
    afterMethod: List<Method> = emptyList(),
    out: QOut = QOut.CONSOLE
): QTestResult {
    val results = mutableListOf<QTestResultElement>()

    val timeItResult = qTimeIt(quiet = true) {
        for (method in methodsToTest) {
            // "⭐"
            out.println(qSeparatorWithLabel("${QMyMark.TEST_METHOD} " + method.qName(true)))

            method.qTrySetAccessible()

            val cause =
                if (method.qIsInstanceMethod()) {
                    val testInstance = method.declaringClass.qNewInstance()

                    try {
                        for (before in beforeMethod) {
                            before.invoke(testInstance)
                        }

                        method.invoke(testInstance)

                        for (after in afterMethod) {
                            after.invoke(testInstance)
                        }
                        null
                    } catch (e: Throwable) {
                        e
                    }
                } else {
                    try {
                        for (before in beforeMethod) {
                            before.invoke(null)
                        }

                        method.invoke(null)

                        for (after in afterMethod) {
                            after.invoke(null)
                        }
                        null
                    } catch (e: Throwable) {
                        e
                    }
                }

            results += if (cause?.cause != null && cause is InvocationTargetException) {
                QTestResultElement(method, cause.cause)
            } else {
                QTestResultElement(method, cause)
            }
        }
    }

    val result = QTestResult(results, timeItResult.time)
    result.printIt()

    return result
}

// CallChain[size=2] = qTest() <-[Call]- main()[Root]
private fun qTest(
    vararg targetClasses: Class<*> = arrayOf(qThisFileMainClass),

    targetMethodFilter: QMMethod =
        (QMMethod.annotation(QTest::class) or QMMethod.annotation("Test")) and
                QMMethod.notAnnotation(QTestHumanCheckRequired::class) and
//                QMMethod.notAnnotation(QIgnore::class) and
                QMMethod.DeclaredOnly and
                QMMethod.NoParams and
                QMMethod.nameNotExact("main"),

    beforeMethodFilter: QMMethod =
        (
                QMMethod.annotation(QBeforeEach::class) or QMMethod.annotation("BeforeTest")
                        or QMMethod.annotation("BeforeEach")
                        or QMMethod.annotation("BeforeMethod")
                )
                and QMMethod.DeclaredOnly and QMMethod.NoParams and QMMethod.nameNotExact(
            "main"
        ),

    afterMethodFilter: QMMethod =
        (
                QMMethod.annotation(QAfterEach::class) or QMMethod.annotation("AfterTest")
                        or QMMethod.annotation("AfterEach")
                        or QMMethod.annotation("AfterMethod")
                ) and QMMethod.DeclaredOnly and QMMethod.NoParams and QMMethod.nameNotExact(
            "main"
        ),

    out: QOut = QOut.CONSOLE,

    throwsException: Boolean = true,
): QTestResult {
    val targets = targetClasses.joinToString(", ") { it.simpleName }

    out.separator()

    val callerFileName = qCallerFileName()

    val methodsToTestImmediately = targetClasses.flatMap { cls ->
        cls.qMethods().filter { method ->
            (QMMethod.DeclaredOnly and (
                    QMMethod.annotation(QTest::class) { it.testOnlyThis } or
                            QMMethod.annotation(QTestHumanCheckRequired::class) { it.testOnlyThis })).matches(method)
        }.sortedBy {
            it.name // TODO sort by line number
        }
    }

    val methodsToTest = methodsToTestImmediately.ifEmpty {
        targetClasses.flatMap {
            it.qMethods().filter { method ->
                targetMethodFilter.matches(method)
            }
        }.sortedBy {
            it.name // TODO sort by line number
        }
    }

    qLogStackFrames(
        // "🚀"
        msg = "${QMyMark.TEST_START} Test Start ${QMyMark.TEST_START}\n$targets".light_blue,
        style = QLogStyle.MSG_AND_STACK,
        frames = listOf(
            qStackFrameEntryMethod { frame ->
                frame.fileName == callerFileName
            }
        )
    )

    val before = targetClasses.flatMap {
        it.qMethods().filter { method ->
            beforeMethodFilter.matches(method)
        }
    }

    val after = targetClasses.flatMap {
        it.qMethods().filter { method ->
            afterMethodFilter.matches(method)
        }
    }

    val result = qTestMethods(methodsToTest, before, after)

    if (result.numFail > 0 && throwsException) {
        QE.TestFail.throwIt()
    } else {
        return result
    }
}

// CallChain[size=3] = qFailMsg() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun qFailMsg(msg: String = "it is null"): String {
    val cMsg = "[$msg]".colorIt
    return "${QMyMark.WARN} $cMsg"
}

// CallChain[size=3] = qFailMsg() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun qFailMsg(actual: Any?, msg: String = "is not equals to", expected: Any?): String {
    val cMsg = "[$msg]".colorIt
    val actualStr = actual.qToLogString() + " " + "(actual)".light_green
    val expectedStr = expected.qToLogString() + " " + "(expected)".blue
    return "${QMyMark.WARN} ${actualStr.qWithNewLineSurround(onlyIf = QOnlyIfStr.Always)}$cMsg${
        expectedStr.qWithNewLinePrefix(onlyIf = QOnlyIfStr.Always)
    }"
}

// CallChain[size=4] = colorIt <-[Call]- qFailMsg() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private val String.colorIt: String
    get() = this.light_yellow

// CallChain[size=3] = qThrowIt() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun qThrowIt(msg: String, exception: QE) {
    throw QException(exception, msg, null, stackDepth = 2, srcCut = QSrcCut.MULTILINE_INFIX_NOCUT)
}

// CallChain[size=2] = Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private infix fun Any?.shouldBe(expected: Any?) {
    if (!qOkToTest()) return

    if (expected is Boolean && this is Boolean && this != expected) {
        if (expected) {
            val msg = qFailMsg("it is false")

            qThrowIt(msg, QE.ShouldBeTrue)
        } else {
            val msg = qFailMsg("it is true")

            qThrowIt(msg, QE.ShouldBeFalse)
        }
    }

    val thisStr = this.qToLogString()
    val expectedStr = expected.qToLogString()

    if (thisStr.trim().noColor != expectedStr.trim().noColor) {
        val msg = qFailMsg(thisStr, "is not equals to", expectedStr)

        val diffIdx =
            if (expectedStr.length < thisStr.length) {
                expectedStr.mapIndexedNotNull { idx, ch ->
                    if (thisStr.length > idx && thisStr[idx] != ch) {
                        idx
                    } else {
                        null
                    }
                }.firstOrNull()
            } else {
                thisStr.mapIndexedNotNull { idx, ch ->
                    if (expectedStr.length > idx && expectedStr[idx] != ch) {
                        idx
                    } else {
                        null
                    }
                }.firstOrNull()
            }

        if (diffIdx != null) {
            val expectedChar = expectedStr[diffIdx]
            val actualChar = thisStr[diffIdx]

            qThrowIt(
                msg + "\n" + qBrackets(
                    "expected char at idx=$diffIdx".blue,
                    expectedChar.toString().blue + (" ( CharCodeHex = " + expectedChar.qToHex() + " )").light_gray,
                    "actual char at idx=$diffIdx".light_green,
                    actualChar.toString().light_green + (" ( CharCodeHex = " + actualChar.qToHex() + " )").light_gray
                ),
                QE.ShouldBeEqual
            )
        } else {
            qThrowIt(msg, QE.ShouldBeEqual)
        }
    }
}

// CallChain[size=3] = qOkToTest() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private inline fun qOkToTest(): Boolean {
    return QMyTest.forceTestMode || qIsTesting || qIsDebugging
}

// CallChain[size=3] = Char.qToHex() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun Char.qToHex(): String = String.format("%02X", code)

// CallChain[size=13] = qDEFAULT_CACHE_IT_EXPIRATION_CHECK_INTERVAL <-[Call]- QCacheMap.QCacheMap()  ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private const val qDEFAULT_CACHE_IT_EXPIRATION_CHECK_INTERVAL = 1000L

// CallChain[size=11] = qThreadLocalCache <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItOne ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private val qThreadLocalCache: ThreadLocal<QCacheMap> by lazy {
    ThreadLocal.withInitial {
        QCacheMap(
            qDEFAULT_CACHE_IT_EXPIRATION_CHECK_INTERVAL
        )
    }
}

// CallChain[size=12] = qCacheThreadSafe <-[Call]- qCacheItTimed() <-[Call]- qCacheItOneSec() <-[Cal ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private val qCacheThreadSafe: QCacheMap by lazy { QCacheMap(qDEFAULT_CACHE_IT_EXPIRATION_CHECK_INTERVAL, true) }

// CallChain[size=10] = qCacheItOneSec() <-[Call]- qMySrcLinesAtFrame() <-[Call]- qLogStackFrames()  ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun <K : Any, V : Any?> qCacheItOneSec(key: K, block: () -> V): V = qCacheItTimed(key, 1000L, block)

// CallChain[size=11] = qCacheItTimed() <-[Call]- qCacheItOneSec() <-[Call]- qMySrcLinesAtFrame() <- ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun <K : Any, V : Any?> qCacheItTimed(key: K, duration: Long, block: () -> V): V =
    qCacheThreadSafe.getOrPut(key) { QCacheEntry(block(), duration, qNow) }.value as V

// CallChain[size=9] = qCacheItOneSecThreadLocal() <-[Call]- qRe() <-[Call]- QException.mySrcAndStac ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun <K : Any, V : Any> qCacheItOneSecThreadLocal(key: K, block: () -> V): V =
    qCacheItTimedThreadLocal(key, 1000L, block)

// CallChain[size=10] = qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThreadLocal() <-[Call]- q ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun <K : Any, V : Any> qCacheItTimedThreadLocal(key: K, duration: Long, block: () -> V): V =
    qThreadLocalCache.get().getOrPut(key) { QCacheEntry(block(), duration, qNow) }.value as V

// CallChain[size=12] = QCacheMap <-[Ref]- qThreadLocalCache <-[Call]- qCacheItTimedThreadLocal() <- ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private class QCacheMap(
    val expirationCheckInterval: Long = qDEFAULT_CACHE_IT_EXPIRATION_CHECK_INTERVAL,
    val threadSafe: Boolean = false
) {
    // CallChain[size=12] = QCacheMap.lastCheck <-[Call]- QCacheMap.getOrPut() <-[Call]- qCacheItTimedTh ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    var lastCheck: Long = -1
    // CallChain[size=12] = QCacheMap.lock <-[Call]- QCacheMap.getOrPut() <-[Call]- qCacheItTimedThreadL ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    val lock: ReentrantLock = ReentrantLock()
    // CallChain[size=12] = QCacheMap.map <-[Call]- QCacheMap.getOrPut() <-[Call]- qCacheItTimedThreadLo ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    val map: MutableMap<Any, QCacheEntry> = mutableMapOf()

    // CallChain[size=12] = QCacheMap.clearExpired() <-[Call]- QCacheMap.getOrPut() <-[Call]- qCacheItTi ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun clearExpired(): Int = lock.qWithLock(threadSafe) {
        val toRemove = map.filterValues { it.isExpired() }
        toRemove.forEach { map.remove(it.key) }
        return toRemove.count()
    }

    // CallChain[size=11] = QCacheMap.getOrPut() <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheIt ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun getOrPut(key: Any, defaultValue: () -> QCacheEntry): QCacheEntry = lock.qWithLock(threadSafe) {
        val now = qNow
        if (now - lastCheck > expirationCheckInterval) {
            lastCheck = now
            clearExpired()
        }

        map.getOrPut(key, defaultValue)
    }
}

// CallChain[size=11] = QCacheEntry <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThr ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private data class QCacheEntry(val value: Any?, val duration: Long, val creationTime: Long = qNow) {
    // CallChain[size=13] = QCacheEntry.isExpired() <-[Call]- QCacheMap.clearExpired() <-[Call]- QCacheM ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun isExpired() = (qNow - creationTime) > duration
}

// CallChain[size=12] = Lock.qWithLock() <-[Call]- QCacheMap.getOrPut() <-[Call]- qCacheItTimedThrea ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private inline fun <T> Lock.qWithLock(threadSafe: Boolean, block: () -> T): T {
    return if (threadSafe) {
        withLock(block)
    } else {
        block()
    }
}

// CallChain[size=12] = QE.throwIt() <-[Call]- qUnreachable() <-[Call]- QFetchRule.SINGLE_LINE <-[Ca ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun QE.throwIt(msg: Any? = "", e: Throwable? = null, stackDepth: Int = 0): Nothing {
    throw QException(
        this,
        if (msg is String && msg.isEmpty()) {
            "No detailed error messages".light_gray
        } else {
            msg.qToLogString()
        },
        e, stackDepth = stackDepth + 1
    )
}

// CallChain[size=13] = QE.throwItFile() <-[Call]- LineNumberReader.qFetchLinesAround() <-[Call]- Pa ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun QE.throwItFile(path: Path, e: Throwable? = null, stackDepth: Int = 0): Nothing {
    throw QException(this, qBrackets("File", path.absolutePathString()), e, stackDepth = stackDepth + 1)
}

// CallChain[size=4] = QE.throwItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun QE.throwItBrackets(vararg keysAndValues: Any?, e: Throwable? = null, stackDepth: Int = 0): Nothing {
    throw QException(this, qBrackets(*keysAndValues), e, stackDepth = stackDepth + 1)
}

// CallChain[size=11] = qUnreachable() <-[Call]- QFetchRule.SINGLE_LINE <-[Call]- QSrcCut.QSrcCut()  ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun qUnreachable(msg: Any? = ""): Nothing {
    QE.Unreachable.throwIt(msg)
}

// CallChain[size=5] = QException <-[Call]- QE.throwItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private class QException(
    val type: QE = QE.Other,
    msg: String = QMyMark.WARN,
    e: Throwable? = null,
    val stackDepth: Int = 0,
    stackSize: Int = 20,
    stackFilter: (StackWalker.StackFrame) -> Boolean = QE.STACK_FRAME_FILTER,
    private val srcCut: QSrcCut = QSrcCut.MULTILINE_NOCUT,
) : RuntimeException(msg, e) {

    // CallChain[size=6] = QException.printStackTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun printStackTrace(s: PrintStream) {
        s.println("\n" + qToString() + "\n" + mySrcAndStack)
    }

    // CallChain[size=7] = QException.stackFrames <-[Call]- QException.getStackTrace() <-[Propag]- QExce ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    val stackFrames = qStackFrames(stackDepth + 2, size = stackSize, filter = stackFilter)

    // CallChain[size=7] = QException.mySrcAndStack <-[Call]- QException.printStackTrace() <-[Propag]- Q ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    val mySrcAndStack: String by lazy {
        qLogStackFrames(frames = stackFrames, style = QLogStyle.SRC_AND_STACK, srcCut = srcCut, quiet = true)
            .qColorTarget(qRe("""\sshould[a-zA-Z]+"""), QShColor.LightYellow)
    }

    // CallChain[size=6] = QException.getStackTrace() <-[Propag]- QException.QException() <-[Ref]- QE.throwItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun getStackTrace(): Array<StackTraceElement> {
        return stackFrames.map {
            it.toStackTraceElement()
        }.toTypedArray()
    }

    // CallChain[size=7] = QException.qToString() <-[Call]- QException.toString() <-[Propag]- QException ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun qToString(): String {
        val msg = message

        return if (!msg.isNullOrEmpty() && type.name != message) {
            "${type.name.yellow} ${":".yellow}${
            msg.qWithSpacePrefix(onlyIf = QOnlyIfStr.SingleLine).qWithNewLinePrefix(onlyIf = QOnlyIfStr.Multiline)
            }".trim()
        } else {
            type.name.yellow
        }
    }

    // CallChain[size=6] = QException.toString() <-[Propag]- QException.QException() <-[Ref]- QE.throwItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun toString(): String {
        return qToString()
//         used by @Test
//        return type.name.yellow
    }
}

// CallChain[size=6] = Boolean.qaTrue() <-[Call]- String.qWithMinAndMaxLength() <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun Boolean.qaTrue(exceptionType: QE = QE.ShouldBeTrue, msg: Any? = "") {
    if (!this) {
        exceptionType.throwIt(stackDepth = 1, msg = msg)
    }
}

// CallChain[size=12] = T?.qaNotNull() <-[Call]- qSrcFileAtFrame() <-[Call]- qSrcFileLinesAtFrame()  ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun <T : Any> T?.qaNotNull(exceptionType: QE = QE.ShouldNotBeNull, msg: Any? = ""): T {
    if (this != null) {
        return this
    } else {
        exceptionType.throwIt(stackDepth = 1, msg = msg)
    }
}

// CallChain[size=5] = Int?.qaNotZero() <-[Call]- CharSequence.qMask() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun Int?.qaNotZero(exceptionType: QE = QE.ShouldNotBeZero, msg: Any? = ""): Int {
    if (this == null) {
        QE.ShouldNotBeNull.throwIt(stackDepth = 1, msg = msg)
    } else if (this == 0) {
        exceptionType.throwIt(stackDepth = 1, msg = msg)
    } else {
        return this
    }
}

// CallChain[size=13] = qBUFFER_SIZE <-[Call]- Path.qReader() <-[Call]- Path.qFetchLinesAround() <-[ ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private const val qBUFFER_SIZE = DEFAULT_BUFFER_SIZE

// CallChain[size=13] = QOpenOpt <-[Ref]- Path.qReader() <-[Call]- Path.qFetchLinesAround() <-[Call] ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
// @formatter:off
private enum class QOpenOpt(val opt: OpenOption) : QFlagEnum<QOpenOpt> {
    // CallChain[size=15] = QOpenOpt.TRUNCATE_EXISTING <-[Propag]- QOpenOpt.QOpenOpt() <-[Call]- QFlag<Q ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    TRUNCATE_EXISTING(StandardOpenOption.TRUNCATE_EXISTING),
    // CallChain[size=15] = QOpenOpt.CREATE <-[Propag]- QOpenOpt.QOpenOpt() <-[Call]- QFlag<QOpenOpt>.to ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    CREATE(StandardOpenOption.CREATE),
    // CallChain[size=15] = QOpenOpt.CREATE_NEW <-[Propag]- QOpenOpt.QOpenOpt() <-[Call]- QFlag<QOpenOpt ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    CREATE_NEW(StandardOpenOption.CREATE_NEW),
    // CallChain[size=15] = QOpenOpt.WRITE <-[Propag]- QOpenOpt.QOpenOpt() <-[Call]- QFlag<QOpenOpt>.toO ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    WRITE(StandardOpenOption.WRITE),
    // CallChain[size=15] = QOpenOpt.READ <-[Propag]- QOpenOpt.QOpenOpt() <-[Call]- QFlag<QOpenOpt>.toOp ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    READ(StandardOpenOption.READ),
    // CallChain[size=15] = QOpenOpt.APPEND <-[Propag]- QOpenOpt.QOpenOpt() <-[Call]- QFlag<QOpenOpt>.to ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    APPEND(StandardOpenOption.APPEND),
    // CallChain[size=15] = QOpenOpt.DELETE_ON_CLOSE <-[Propag]- QOpenOpt.QOpenOpt() <-[Call]- QFlag<QOp ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    DELETE_ON_CLOSE(StandardOpenOption.DELETE_ON_CLOSE),
    // CallChain[size=15] = QOpenOpt.DSYNC <-[Propag]- QOpenOpt.QOpenOpt() <-[Call]- QFlag<QOpenOpt>.toO ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    DSYNC(StandardOpenOption.DSYNC),
    // CallChain[size=15] = QOpenOpt.SYNC <-[Propag]- QOpenOpt.QOpenOpt() <-[Call]- QFlag<QOpenOpt>.toOp ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    SYNC(StandardOpenOption.SYNC),
    // CallChain[size=15] = QOpenOpt.SPARSE <-[Propag]- QOpenOpt.QOpenOpt() <-[Call]- QFlag<QOpenOpt>.to ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    SPARSE(StandardOpenOption.SPARSE),
    // CallChain[size=15] = QOpenOpt.EX_DIRECT <-[Propag]- QOpenOpt.QOpenOpt() <-[Call]- QFlag<QOpenOpt> ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    EX_DIRECT(ExtendedOpenOption.DIRECT),
    // CallChain[size=15] = QOpenOpt.EX_NOSHARE_DELETE <-[Propag]- QOpenOpt.QOpenOpt() <-[Call]- QFlag<Q ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    EX_NOSHARE_DELETE(ExtendedOpenOption.NOSHARE_DELETE),
    // CallChain[size=15] = QOpenOpt.EX_NOSHARE_READ <-[Propag]- QOpenOpt.QOpenOpt() <-[Call]- QFlag<QOp ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    EX_NOSHARE_READ(ExtendedOpenOption.NOSHARE_READ),
    // CallChain[size=15] = QOpenOpt.EX_NOSHARE_WRITE <-[Propag]- QOpenOpt.QOpenOpt() <-[Call]- QFlag<QO ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    EX_NOSHARE_WRITE(ExtendedOpenOption.NOSHARE_WRITE),
    // CallChain[size=15] = QOpenOpt.LN_NOFOLLOW_LINKS <-[Propag]- QOpenOpt.QOpenOpt() <-[Call]- QFlag<Q ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    LN_NOFOLLOW_LINKS(LinkOption.NOFOLLOW_LINKS);

    companion object {
        
    }
}

// CallChain[size=13] = QFlag<QOpenOpt>.toOptEnums() <-[Call]- Path.qReader() <-[Call]- Path.qFetchL ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun QFlag<QOpenOpt>.toOptEnums(): Array<OpenOption> {
    return toEnumValues().map { it.opt }.toTypedArray()
}

// CallChain[size=12] = Path.qLineSeparator() <-[Call]- Path.qFetchLinesAround() <-[Call]- qSrcFileL ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun Path.qLineSeparator(charset: Charset = Charsets.UTF_8): QLineSeparator {
    this.bufferedReader(charset).use { reader ->
        var ch: Char

        while (true) {
            ch = reader.read().toChar()

            if (ch == '\u0000') return QLineSeparator.DEFAULT

            if (ch == '\r') {
                val nextCh = reader.read().toChar()

                if (nextCh == '\u0000') return QLineSeparator.CR

                return if (nextCh == '\n') QLineSeparator.CRLF
                else QLineSeparator.CR
            } else if (ch == '\n') {
                return QLineSeparator.LF
            }
        }
    }
}

// CallChain[size=11] = QFetchEnd <-[Ref]- QFetchRule.SINGLE_LINE <-[Call]- QSrcCut.QSrcCut() <-[Cal ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private enum class QFetchEnd {
    // CallChain[size=12] = QFetchEnd.FETCH_THIS_LINE_AND_GO_TO_NEXT_LINE <-[Propag]- QFetchEnd.END_WITH ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    FETCH_THIS_LINE_AND_GO_TO_NEXT_LINE,
    // CallChain[size=11] = QFetchEnd.END_WITH_THIS_LINE <-[Call]- QFetchRule.SINGLE_LINE <-[Call]- QSrc ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    END_WITH_THIS_LINE,
    // CallChain[size=12] = QFetchEnd.END_WITH_NEXT_LINE <-[Propag]- QFetchEnd.END_WITH_THIS_LINE <-[Cal ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    END_WITH_NEXT_LINE,
    // CallChain[size=12] = QFetchEnd.END_WITH_PREVIOUS_LINE <-[Propag]- QFetchEnd.END_WITH_THIS_LINE <- ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    END_WITH_PREVIOUS_LINE
}

// CallChain[size=11] = QFetchStart <-[Ref]- QFetchRule.SINGLE_LINE <-[Call]- QSrcCut.QSrcCut() <-[C ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private enum class QFetchStart {
    // CallChain[size=11] = QFetchStart.FETCH_THIS_LINE_AND_GO_TO_PREVIOUS_LINE <-[Call]- QFetchRule.SIN ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    FETCH_THIS_LINE_AND_GO_TO_PREVIOUS_LINE,
    // CallChain[size=11] = QFetchStart.START_FROM_THIS_LINE <-[Call]- QFetchRule.SINGLE_LINE <-[Call]-  ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    START_FROM_THIS_LINE,
    // CallChain[size=12] = QFetchStart.START_FROM_NEXT_LINE <-[Propag]- QFetchStart.FETCH_THIS_LINE_AND ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    START_FROM_NEXT_LINE,
    // CallChain[size=12] = QFetchStart.START_FROM_PREVIOUS_LINE <-[Propag]- QFetchStart.FETCH_THIS_LINE ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    START_FROM_PREVIOUS_LINE
}

// CallChain[size=11] = QFetchRuleA <-[Call]- QFetchRule.SINGLE_LINE <-[Call]- QSrcCut.QSrcCut() <-[ ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private abstract class QFetchRuleA(
    override val numLinesBeforeTargetLine: Int = 10,
    override val numLinesAfterTargetLine: Int = 10,
) : QFetchRule

// CallChain[size=10] = QFetchRule <-[Ref]- QSrcCut.QSrcCut() <-[Call]- qLogStackFrames() <-[Call]-  ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private interface QFetchRule {
    // CallChain[size=11] = QFetchRule.numLinesBeforeTargetLine <-[Propag]- QFetchRule.SINGLE_LINE <-[Ca ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    val numLinesBeforeTargetLine: Int
    // CallChain[size=11] = QFetchRule.numLinesAfterTargetLine <-[Propag]- QFetchRule.SINGLE_LINE <-[Cal ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    val numLinesAfterTargetLine: Int

    // CallChain[size=11] = QFetchRule.fetchStartCheck() <-[Propag]- QFetchRule.SINGLE_LINE <-[Call]- QS ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun fetchStartCheck(
        line: String,
        currentLineNumber: Int,
        targetLine: String,
        targetLineNumber: Int,
        context: MutableSet<String>,
    ): QFetchStart

    // CallChain[size=11] = QFetchRule.fetchEndCheck() <-[Propag]- QFetchRule.SINGLE_LINE <-[Call]- QSrc ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun fetchEndCheck(
        line: String,
        currentLineNumber: Int,
        targetLine: String,
        targetLineNumber: Int,
        context: MutableSet<String>,
    ): QFetchEnd

    companion object {
        // CallChain[size=10] = QFetchRule.SINGLE_LINE <-[Call]- QSrcCut.QSrcCut() <-[Call]- qLogStackFrames ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
        val SINGLE_LINE = object : QFetchRuleA(0, 0) {
            override fun fetchStartCheck(
                line: String,
                currentLineNumber: Int,
                targetLine: String,
                targetLineNumber: Int,
                context: MutableSet<String>,
            ): QFetchStart = if (currentLineNumber == targetLineNumber) {
                QFetchStart.START_FROM_THIS_LINE
            } else {
                QFetchStart.FETCH_THIS_LINE_AND_GO_TO_PREVIOUS_LINE
            }

            override fun fetchEndCheck(
                line: String,
                currentLineNumber: Int,
                targetLine: String,
                targetLineNumber: Int,
                context: MutableSet<String>,
            ): QFetchEnd = if (currentLineNumber == targetLineNumber) {
                QFetchEnd.END_WITH_THIS_LINE
            } else {
                qUnreachable()
            }
        }

        // CallChain[size=5] = QFetchRule.SMART_FETCH_INFIX <-[Call]- QSrcCut.MULTILINE_INFIX_NOCUT <-[Call]- qThrowIt() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
        val SMART_FETCH_INFIX = object : QFetchRuleA(10, 10) {
            // """               <<< targetLine
            // some text
            // """ shouldBe """
            // some text
            // """

            override fun fetchStartCheck(
                line: String,
                currentLineNumber: Int,
                targetLine: String,
                targetLineNumber: Int,
                context: MutableSet<String>,
            ): QFetchStart {
                return QFetchStart.START_FROM_THIS_LINE
            }

            override fun fetchEndCheck(
                line: String,
                currentLineNumber: Int,
                targetLine: String,
                targetLineNumber: Int,
                context: MutableSet<String>,
            ): QFetchEnd = if (currentLineNumber >= targetLineNumber) {
                val nIndentThis = line.qCountLeftSpace()
                val nIndentTarget = targetLine.qCountLeftSpace()

                if (currentLineNumber == targetLineNumber && line.trimStart()
                        .startsWith("\"\"\"") && line.qCountOccurrence("\"\"\"") == 1
                ) {
                    // """
                    // some text
                    // """.log           <<< targetLine
                    QFetchEnd.FETCH_THIS_LINE_AND_GO_TO_NEXT_LINE
                } else if (line.qEndsWith(""".* should[a-zA-Z]+ ""${'"'}""".re)) { // TODO More accurately
                    QFetchEnd.FETCH_THIS_LINE_AND_GO_TO_NEXT_LINE
                } else if (line.endsWith("{") || line.endsWith("(") || line.endsWith(".")) {
                    QFetchEnd.FETCH_THIS_LINE_AND_GO_TO_NEXT_LINE
                } else if (nIndentThis == nIndentTarget) {
                    QFetchEnd.END_WITH_THIS_LINE
                } else {
                    QFetchEnd.FETCH_THIS_LINE_AND_GO_TO_NEXT_LINE
                }
            } else {
                QFetchEnd.FETCH_THIS_LINE_AND_GO_TO_NEXT_LINE
            }
        }

        // CallChain[size=9] = QFetchRule.SMART_FETCH <-[Call]- qLogStackFrames() <-[Call]- QException.mySrc ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
        val SMART_FETCH = object : QFetchRuleA(10, 10) {
            override fun fetchStartCheck(
                line: String,
                currentLineNumber: Int,
                targetLine: String,
                targetLineNumber: Int,
                context: MutableSet<String>,
            ): QFetchStart {
                val nIndentThis = line.qCountLeftSpace()
                val nIndentTarget = targetLine.qCountLeftSpace()
                val trimmed = line.trimStart()

                return if (arrayOf(
                        "\"\"\".",
                        "}",
                        ")",
                        ".",
                        ",",
                        "?",
                        "//",
                        "/*",
                        "*"
                    ).any { trimmed.startsWith(it) }
                ) {
                    QFetchStart.FETCH_THIS_LINE_AND_GO_TO_PREVIOUS_LINE
                } else if (nIndentThis <= nIndentTarget) {
                    QFetchStart.START_FROM_THIS_LINE
                } else {
                    QFetchStart.FETCH_THIS_LINE_AND_GO_TO_PREVIOUS_LINE
                }
            }

            override fun fetchEndCheck(
                line: String,
                currentLineNumber: Int,
                targetLine: String,
                targetLineNumber: Int,
                context: MutableSet<String>,
            ): QFetchEnd = if (currentLineNumber >= targetLineNumber) {
                val nIndentThis = line.qCountLeftSpace()
                val nIndentTarget = targetLine.qCountLeftSpace()

                if (currentLineNumber == targetLineNumber && line.trimStart()
                        .startsWith("\"\"\"") && line.qCountOccurrence("\"\"\"") == 1
                ) {
                    // """               <<< targetLine
                    // some text
                    // """ shouldBe """
                    // some text
                    // """

                    // """
                    // some text
                    // """.log           <<< targetLine
                    QFetchEnd.FETCH_THIS_LINE_AND_GO_TO_NEXT_LINE
                } else if (line.qEndsWith(""".* should[a-zA-Z]+ ""${'"'}""".re)) {
                    QFetchEnd.FETCH_THIS_LINE_AND_GO_TO_NEXT_LINE
                } else if (line.endsWith("{") || line.endsWith("(") || line.endsWith(".")) {
                    QFetchEnd.FETCH_THIS_LINE_AND_GO_TO_NEXT_LINE
                } else if (nIndentThis == nIndentTarget) {
                    QFetchEnd.END_WITH_THIS_LINE
                } else {
                    QFetchEnd.FETCH_THIS_LINE_AND_GO_TO_NEXT_LINE
                }
            } else {
                QFetchEnd.FETCH_THIS_LINE_AND_GO_TO_NEXT_LINE
            }
        }
    }
}

// CallChain[size=14] = LineNumberReader.qFetchLinesBetween() <-[Call]- LineNumberReader.qFetchTarge ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun LineNumberReader.qFetchLinesBetween(
    lineNumberStartInclusive: Int,
    lineNumberEndInclusive: Int,
): List<String> {
    var fetching = false
    val lines = mutableListOf<String>()

    while (true) {
        val n = this.lineNumber + 1
        val line = this.readLine() ?: break

        if (n == lineNumberStartInclusive) {
            fetching = true
            lines += line
        } else if (fetching) {
            lines += line

            if (n == lineNumberEndInclusive) {
                break
            }
        }
    }

    return lines
}

// CallChain[size=14] = TargetSurroundingLines <-[Ref]- LineNumberReader.qFetchTargetSurroundingLine ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private class TargetSurroundingLines(
    val targetLineNumber: Int,
    val startLineNumber: Int,
    val endLineNumber: Int,
    val targetLine: String,
    val linesBeforeTargetLine: List<String>,
    val linesAfterTargetLine: List<String>,
) {
    // CallChain[size=13] = TargetSurroundingLines.linesBetween() <-[Call]- LineNumberReader.qFetchLines ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun linesBetween(lineNumberStartInclusive: Int, lineNumberEndInclusive: Int): List<String> {
        val lines = mutableListOf<String>()

        lines += linesBeforeTargetLine
        lines += targetLine
        lines += linesAfterTargetLine

        val startIdx = lineNumberStartInclusive - startLineNumber
        val endIdx = lineNumberEndInclusive - startLineNumber

        return lines.subList(startIdx, endIdx + 1)
    }
}

// CallChain[size=13] = LineNumberReader.qFetchTargetSurroundingLines() <-[Call]- LineNumberReader.q ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun LineNumberReader.qFetchTargetSurroundingLines(
    targetLineNumber: Int,
    numLinesBeforeTargetLine: Int = 10,
    numLinesAfterTargetLine: Int = 10,
): TargetSurroundingLines {
    val start = max(1, targetLineNumber - numLinesBeforeTargetLine)
    val end = targetLineNumber + numLinesAfterTargetLine

    val lines = qFetchLinesBetween(start, end)

    return TargetSurroundingLines(
        targetLineNumber = targetLineNumber,
        startLineNumber = start,
        endLineNumber = end,
        targetLine = lines[targetLineNumber - start],
        linesBeforeTargetLine = lines.subList(0, targetLineNumber - start),
        linesAfterTargetLine = lines.subList(targetLineNumber - start + 1, lines.size)
    )
}

// CallChain[size=12] = LineNumberReader.qFetchLinesAround() <-[Call]- Path.qFetchLinesAround() <-[C ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun LineNumberReader.qFetchLinesAround(
    file: Path,
    targetLineNumber: Int,
    targetLine: String,
    fetchRule: QFetchRule = QFetchRule.SMART_FETCH,
    lineSeparator: QLineSeparator = QLineSeparator.LF,
): String {
    val surroundingLines = qFetchTargetSurroundingLines(
        targetLineNumber,
        fetchRule.numLinesBeforeTargetLine,
        fetchRule.numLinesAfterTargetLine
    )
    val context: MutableSet<String> = mutableSetOf()

    var start: Int = -1
    var end: Int = -1

    val checkStartLines = mutableListOf<String>()
    checkStartLines += surroundingLines.linesBeforeTargetLine
    checkStartLines += targetLine

    val checkEndLines = mutableListOf<String>()
    checkEndLines += targetLine
    checkEndLines += surroundingLines.linesAfterTargetLine

    for ((i, line) in checkStartLines.asReversed().withIndex()) {
        val curLineNumber = targetLineNumber - i

        val check = fetchRule.fetchStartCheck(
            line,
            curLineNumber,
            targetLine,
            targetLineNumber,
            context
        )

        when (check) {
            QFetchStart.START_FROM_PREVIOUS_LINE -> {
                start = curLineNumber - 1
                break
            }

            QFetchStart.START_FROM_THIS_LINE -> {
                start = curLineNumber
                break
            }

            QFetchStart.START_FROM_NEXT_LINE -> {
                start = curLineNumber + 1
                break
            }

            QFetchStart.FETCH_THIS_LINE_AND_GO_TO_PREVIOUS_LINE -> {
            }
        }
    }

    if (start == -1) {
        start = max(0, targetLineNumber - fetchRule.numLinesBeforeTargetLine)
    }

    for ((i, line) in checkEndLines.withIndex()) {
        val curLineNumber = targetLineNumber + i

        val check = fetchRule.fetchEndCheck(
            line,
            curLineNumber,
            targetLine,
            targetLineNumber,
            context
        )

        when (check) {
            QFetchEnd.END_WITH_PREVIOUS_LINE -> {
                end = curLineNumber - 1
                break
            }

            QFetchEnd.END_WITH_THIS_LINE -> {
                end = curLineNumber
                break
            }

            QFetchEnd.END_WITH_NEXT_LINE -> {
                end = curLineNumber + 1
                break
            }

            QFetchEnd.FETCH_THIS_LINE_AND_GO_TO_NEXT_LINE -> {
            }
        }
    }

    if (end == -1) {
        end = targetLineNumber + fetchRule.numLinesAfterTargetLine
    }

    return try {
        surroundingLines.linesBetween(start, end).joinToString(lineSeparator.value)
    } catch (e: Exception) {
        QE.FetchLinesFail.throwItFile(file)
    }
}

// CallChain[size=12] = Path.qReader() <-[Call]- Path.qFetchLinesAround() <-[Call]- qSrcFileLinesAtF ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun Path.qReader(
    charset: Charset = Charsets.UTF_8,
    buffSize: Int = qBUFFER_SIZE,
    opts: QFlag<QOpenOpt> = QFlag.none(),
): LineNumberReader {
    return LineNumberReader(reader(charset, *opts.toOptEnums()), buffSize)
}

// CallChain[size=11] = Path.qFetchLinesAround() <-[Call]- qSrcFileLinesAtFrame() <-[Call]- qMySrcLi ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun Path.qFetchLinesAround(
    lineNumber: Int,
    fetchRule: QFetchRule = QFetchRule.SMART_FETCH,
    charset: Charset = Charsets.UTF_8,
    lineSeparator: QLineSeparator = this.qLineSeparator(charset),
): String {
    val reader = qReader(charset)

    try {
        // TODO optimization
        val targetLine = qLineAt(lineNumber, charset)

        if (fetchRule == QFetchRule.SINGLE_LINE) return targetLine

        val fetchedLines = reader.use {
            it.qFetchLinesAround(this, lineNumber, targetLine, fetchRule, lineSeparator)
        }

        return fetchedLines
    } catch (e: Exception) {
        QE.FetchLinesFail.throwItBrackets("File", this, "LineNumber", lineNumber, e = e)
    }
}

// CallChain[size=12] = Path.qLineAt() <-[Call]- Path.qFetchLinesAround() <-[Call]- qSrcFileLinesAtF ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun Path.qLineAt(
    lineNumber: Int,
    charset: Charset = Charsets.UTF_8,
): String {
    bufferedReader(charset).use { reader ->
        var n = 0
        var line: String? = reader.readLine()

        while (line != null) {
            n++

            if (n == lineNumber) {
                return line
            }

            line = reader.readLine()
        }

        QE.LineNumberExceedsMaximum.throwItBrackets("File", this.absolutePathString(), "TargetLineNumber", lineNumber)
    }
}

// CallChain[size=13] = QFType <-[Ref]- Collection<Path>.qFind() <-[Call]- qSrcFileAtFrame() <-[Call ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private enum class QFType {
    // CallChain[size=17] = QFType.Any <-[Call]- QFType.matches() <-[Call]- Path.qSeq() <-[Call]- Path.q ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    Any,
    // CallChain[size=13] = QFType.File <-[Call]- Collection<Path>.qFind() <-[Call]- qSrcFileAtFrame() < ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    File,
    // CallChain[size=17] = QFType.Dir <-[Call]- QFType.matches() <-[Call]- Path.qSeq() <-[Call]- Path.q ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    Dir,
    // CallChain[size=17] = QFType.SymLink <-[Call]- QFType.matches() <-[Call]- Path.qSeq() <-[Call]- Pa ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    SymLink,
    // CallChain[size=17] = QFType.FileOrDir <-[Call]- QFType.matches() <-[Call]- Path.qSeq() <-[Call]-  ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    FileOrDir;

    // CallChain[size=16] = QFType.matches() <-[Call]- Path.qSeq() <-[Call]- Path.qList() <-[Call]- Path ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun matches(path: Path?, followSymLink: Boolean = true): Boolean {
        if (path == null) return false

        return when (this) {
            Any -> true
            File -> if (followSymLink) path.isRegularFile() else path.isRegularFile(LinkOption.NOFOLLOW_LINKS)
            Dir -> if (followSymLink) path.isDirectory() else path.isDirectory(LinkOption.NOFOLLOW_LINKS)
            FileOrDir -> return if (followSymLink) {
                path.isRegularFile() || path.isDirectory()
            } else {
                path.isRegularFile(LinkOption.NOFOLLOW_LINKS) || path.isDirectory(LinkOption.NOFOLLOW_LINKS)
            }

            SymLink -> return path.isSymbolicLink()
        }
    }
}

// CallChain[size=12] = Collection<Path>.qFind() <-[Call]- qSrcFileAtFrame() <-[Call]- qSrcFileLines ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun Collection<Path>.qFind(nameMatcher: QM, type: QFType = QFType.File, maxDepth: Int = 1): Path? {
    for (path in this) {
        val found = path.qFind(nameMatcher, type, maxDepth)
        if (found != null) return found
    }

    return null
}

// CallChain[size=13] = Path.qFind() <-[Call]- Collection<Path>.qFind() <-[Call]- qSrcFileAtFrame()  ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun Path.qFind(nameMatcher: QM, type: QFType = QFType.File, maxDepth: Int = 1): Path? {
    return try {
        qList(type, maxDepth = maxDepth) {
            it.name.qMatches(nameMatcher)
        }.firstOrNull()
    } catch (e: NoSuchElementException) {
        null
    }
}

// CallChain[size=10] = Path.qListByMatch() <-[Call]- QMyPath.src_root <-[Call]- qLogStackFrames() < ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun Path.qListByMatch(
    nameMatch: QM,
    type: QFType = QFType.File,
    maxDepth: Int = 1,
    followSymLink: Boolean = false,
): List<Path> {
    return qList(
        type, maxDepth = maxDepth, followSymLink = followSymLink
    ) {
        it.name.qMatches(nameMatch)
    }
}

// CallChain[size=14] = Path.qList() <-[Call]- Path.qFind() <-[Call]- Collection<Path>.qFind() <-[Ca ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun Path.qList(
    type: QFType = QFType.File,
    maxDepth: Int = 1,
    followSymLink: Boolean = false,
    sortWith: ((Path, Path) -> Int)? = Path::compareTo,
    filter: (Path) -> Boolean = { true },
    // TODO https://stackoverflow.com/a/66996768/5570400
    // errorContinue: Boolean = true
): List<Path> {
    return qSeq(
        type = type,
        maxDepth = maxDepth,
        followSymLink = followSymLink,
        sortWith = sortWith,
        filter = filter
    ).toList()
}

// CallChain[size=15] = Path.qSeq() <-[Call]- Path.qList() <-[Call]- Path.qFind() <-[Call]- Collecti ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun Path.qSeq(
    type: QFType = QFType.File,
    maxDepth: Int = 1,
    followSymLink: Boolean = false,
    sortWith: ((Path, Path) -> Int)? = Path::compareTo,
    filter: (Path) -> Boolean = { true },
    // TODO https://stackoverflow.com/a/66996768/5570400
    // errorContinue: Boolean = true
): Sequence<Path> {
    if (!this.isDirectory())
        return emptySequence()

    val fvOpt = if (followSymLink) arrayOf(FileVisitOption.FOLLOW_LINKS) else arrayOf()

    val seq = Files.walk(this, maxDepth, *fvOpt).asSequence().filter {
        if (it == this) return@filter false

        type.matches(it, followSymLink) && filter(it)
    }

    return if (sortWith != null) {
        seq.sortedWith(sortWith)
    } else {
        seq
    }
}

// CallChain[size=13] = QFlag <-[Ref]- Path.qReader() <-[Call]- Path.qFetchLinesAround() <-[Call]- q ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
/**
 * Only Enum or QFlag can implement this interface.
 */
private sealed interface QFlag<T> where T : QFlag<T>, T : Enum<T> {
    // CallChain[size=15] = QFlag.bits <-[Propag]- QFlag.toEnumValues() <-[Call]- QFlag<QOpenOpt>.toOptE ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    val bits: Int

    // CallChain[size=15] = QFlag.contains() <-[Propag]- QFlag.toEnumValues() <-[Call]- QFlag<QOpenOpt>. ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun contains(flags: QFlag<T>): Boolean {
        return (bits and flags.bits) == flags.bits
    }

    // CallChain[size=14] = QFlag.toEnumValues() <-[Call]- QFlag<QOpenOpt>.toOptEnums() <-[Call]- Path.q ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun toEnumValues(): List<T>

    // CallChain[size=15] = QFlag.str() <-[Propag]- QFlag.toEnumValues() <-[Call]- QFlag<QOpenOpt>.toOpt ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun str(): String {
        return toEnumValues().joinToString(", ") { it.name }
    }

    companion object {
        // https://discuss.kotlinlang.org/t/reified-generics-on-class-level/16711/2
        // But, can't make constructor private ...

        // CallChain[size=13] = QFlag.none() <-[Call]- Path.qReader() <-[Call]- Path.qFetchLinesAround() <-[ ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
        inline fun <reified T>
        none(): QFlag<T> where T : QFlag<T>, T : Enum<T> {
            return QFlagSet<T>(T::class, 0)
        }
    }
}

// CallChain[size=14] = QFlagEnum <-[Ref]- QOpenOpt <-[Ref]- Path.qReader() <-[Call]- Path.qFetchLin ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private interface QFlagEnum<T> : QFlag<T> where T : QFlag<T>, T : Enum<T> {
    // CallChain[size=15] = QFlagEnum.bits <-[Propag]- QFlagEnum <-[Ref]- QOpenOpt <-[Ref]- Path.qReader ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override val bits: Int
        get() = 1 shl (this as T).ordinal
    // CallChain[size=15] = QFlagEnum.toEnumValues() <-[Propag]- QFlagEnum <-[Ref]- QOpenOpt <-[Ref]- Pa ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun toEnumValues(): List<T> = listOf(this) as List<T>
}

// CallChain[size=14] = QFlagSet <-[Call]- QFlag.none() <-[Call]- Path.qReader() <-[Call]- Path.qFet ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
/**
 * Mutable bit flag
 */
private class QFlagSet<T>(val enumClass: KClass<T>, override var bits: Int) : QFlag<T> where T : QFlag<T>, T : Enum<T> {
    // CallChain[size=16] = QFlagSet.enumValues <-[Call]- QFlagSet.toEnumValues() <-[Propag]- QFlagSet < ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    val enumValues: Array<T> by lazy { enumClass.qEnumValues() }

    // CallChain[size=15] = QFlagSet.toEnumValues() <-[Propag]- QFlagSet <-[Call]- QFlag.none() <-[Call] ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun toEnumValues(): List<T> =
        enumValues.filter { contains(it) }
}

// CallChain[size=7] = QUnit <-[Ref]- Long.qFormatDuration() <-[Call]- QTestResult.str <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private enum class QUnit {
    // CallChain[size=7] = QUnit.Nano <-[Call]- Long.qFormatDuration() <-[Call]- QTestResult.str <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    Nano,
    // CallChain[size=7] = QUnit.Micro <-[Call]- Long.qFormatDuration() <-[Call]- QTestResult.str <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    Micro,
    // CallChain[size=7] = QUnit.Milli <-[Call]- Long.qFormatDuration() <-[Call]- QTestResult.str <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    Milli,
    // CallChain[size=7] = QUnit.Second <-[Call]- Long.qFormatDuration() <-[Call]- QTestResult.str <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    Second,
    // CallChain[size=7] = QUnit.Minute <-[Call]- Long.qFormatDuration() <-[Call]- QTestResult.str <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    Minute,
    // CallChain[size=7] = QUnit.Hour <-[Call]- Long.qFormatDuration() <-[Call]- QTestResult.str <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    Hour,
    // CallChain[size=7] = QUnit.Day <-[Call]- Long.qFormatDuration() <-[Call]- QTestResult.str <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    Day
}

// CallChain[size=6] = Long.qFormatDuration() <-[Call]- QTestResult.str <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun Long.qFormatDuration(unit: QUnit = QUnit.Nano): String {
    return when (unit) {
        QUnit.Milli ->
            Duration.ofMillis(this).qFormat()
        QUnit.Micro ->
            Duration.ofNanos(this).qFormat()
        QUnit.Nano ->
            Duration.ofNanos(this).qFormat()
        QUnit.Second ->
            Duration.ofSeconds(this).qFormat()
        QUnit.Minute ->
            Duration.ofMinutes(this).qFormat()
        QUnit.Hour ->
            Duration.ofHours(this).qFormat()
        QUnit.Day ->
            Duration.ofDays(this).qFormat()
    }
}

// CallChain[size=8] = Duration.qToMicrosOnlyPart() <-[Call]- Duration.qFormat() <-[Call]- Long.qFor ...  <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun Duration.qToMicrosOnlyPart(): Int {
    return (toNanosPart() % 1_000_000) / 1_000
}

// CallChain[size=8] = Duration.qToNanoOnlyPart() <-[Call]- Duration.qFormat() <-[Call]- Long.qForma ...  <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun Duration.qToNanoOnlyPart(): Int {
    return toNanosPart() % 1_000
}

// CallChain[size=7] = Duration.qFormat() <-[Call]- Long.qFormatDuration() <-[Call]- QTestResult.str <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun Duration.qFormat(detail: Boolean = false): String {
    val du = abs()

    val maxUnit: QUnit = du.let {
        when {
            it < Duration.ofNanos(1000) -> QUnit.Nano
            it < Duration.ofMillis(1) -> QUnit.Micro
            it < Duration.ofSeconds(1) -> QUnit.Milli
            it < Duration.ofMinutes(1) -> QUnit.Second
            it < Duration.ofHours(1) -> QUnit.Minute
            it < Duration.ofDays(1) -> QUnit.Hour
            else -> QUnit.Day
        }
    }

    val parts = mutableListOf<String>()
    when (maxUnit) {
        QUnit.Nano -> {
            parts.add(String.format("%3d ns", du.toNanosPart()))
        }
        QUnit.Micro -> {
            parts.add(String.format("%3d μs", du.qToMicrosOnlyPart()))

            if (du.qToMicrosOnlyPart() <= 3 || detail)
                parts.add(String.format("%03d ns", du.qToNanoOnlyPart()))
        }
        QUnit.Milli -> {
            parts.add(String.format("%3d ms", du.toMillisPart()))

            if (du.toMillisPart() <= 3 || detail)
                parts.add(String.format("%03d μs", du.qToMicrosOnlyPart()))
        }
        QUnit.Second -> {
            parts.add(String.format("%2d sec", du.toSecondsPart()))
            parts.add(String.format("%03d ms", du.toMillisPart()))

            if (detail) {
                parts.add(String.format("%03d μs", du.qToMicrosOnlyPart()))
                parts.add(String.format("%03d ns", du.qToNanoOnlyPart()))
            }
        }
        QUnit.Minute -> {
            parts.add(String.format("%2d min", du.toMinutesPart()))
            parts.add(String.format("%02d sec", du.toSecondsPart()))
            if (detail) {
                parts.add(String.format("%03d ms", du.toMillisPart()))
                parts.add(String.format("%03d μs", du.qToMicrosOnlyPart()))
                parts.add(String.format("%03d ns", du.qToNanoOnlyPart()))
            }
        }
        QUnit.Hour -> {
            parts.add(String.format("%2d hour", du.toHoursPart()))
            parts.add(String.format("%02d min", du.toMinutesPart()))
            if (detail) {
                parts.add(String.format("%02d sec", du.toSecondsPart()))
                parts.add(String.format("%03d ms", du.toMillisPart()))
                parts.add(String.format("%03d μs", du.qToMicrosOnlyPart()))
                parts.add(String.format("%03d ns", du.qToNanoOnlyPart()))
            }
        }
        QUnit.Day -> {
            parts.add(String.format("%2d day", du.toDaysPart()))
            parts.add(String.format("%02d hour", du.toHoursPart()))
            if (detail) {
                parts.add(String.format("%02d min", du.toMinutesPart()))
                parts.add(String.format("%02d sec", du.toSecondsPart()))
                parts.add(String.format("%03d ms", du.toMillisPart()))
                parts.add(String.format("%03d μs", du.qToMicrosOnlyPart()))
                parts.add(String.format("%03d ns", du.qToNanoOnlyPart()))
            }
        }
    }

    return parts.joinToString(" ")
}

// CallChain[size=12] = qARROW <-[Call]- qArrow() <-[Call]- QLogStyle.qLogArrow() <-[Call]- QLogStyl ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private val qARROW = "===>".light_cyan

// CallChain[size=4] = qIsDebugging <-[Call]- qOkToTest() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
// https://stackoverflow.com/a/28754689/5570400
private val qIsDebugging by lazy {
    java.lang.management.ManagementFactory.getRuntimeMXBean().inputArguments.toString().indexOf("jdwp") >= 0
}

// CallChain[size=4] = qIsTesting <-[Call]- qOkToTest() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
// https://stackoverflow.com/a/12717377/5570400
private val qIsTesting by lazy {
    qStackFrames(size = Int.MAX_VALUE).any {
        it.methodName.equals("qTest") ||
                it.className.startsWith("org.junit.") || it.className.startsWith("org.testng.")
    }
}

// CallChain[size=6] = QSrcCut <-[Ref]- QException.QException() <-[Ref]- QE.throwItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private class QSrcCut(
        val fetchRule: QFetchRule = QFetchRule.SINGLE_LINE,
        val cut: (srcLines: String) -> String,
) {
    companion object {
        // CallChain[size=10] = QSrcCut.CUT_PARAM_qLog <-[Call]- QSrcCut.SINGLE_qLog_PARAM <-[Call]- qLogSta ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
        private val CUT_PARAM_qLog = { mySrc: String ->
            mySrc.replaceFirst("""(?s)^\s*(\S.+)\.qLog[a-zA-Z]{0,10}.*$""".re, "$1")
        }

        // CallChain[size=9] = QSrcCut.SINGLE_qLog_PARAM <-[Call]- qLogStackFrames() <-[Call]- QException.my ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
        val SINGLE_qLog_PARAM = QSrcCut(QFetchRule.SINGLE_LINE, CUT_PARAM_qLog)
        // CallChain[size=6] = QSrcCut.MULTILINE_NOCUT <-[Call]- QException.QException() <-[Ref]- QE.throwItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
        val MULTILINE_NOCUT = QSrcCut(QFetchRule.SMART_FETCH) { it }
        // CallChain[size=4] = QSrcCut.MULTILINE_INFIX_NOCUT <-[Call]- qThrowIt() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
        val MULTILINE_INFIX_NOCUT = QSrcCut(QFetchRule.SMART_FETCH_INFIX) { it }
        // CallChain[size=10] = QSrcCut.NOCUT_JUST_SINGLE_LINE <-[Call]- qMySrcLinesAtFrame() <-[Call]- qLog ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
        val NOCUT_JUST_SINGLE_LINE = QSrcCut(QFetchRule.SINGLE_LINE) { it }
        
    }
}

// CallChain[size=9] = QLogStyle <-[Ref]- QLogStyle.SRC_AND_STACK <-[Call]- QException.mySrcAndStack ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private class QLogStyle(
        val stackSize: Int,
        val out: QOut = QMyLog.out,
        val start: String = "----".cyan + "\n",
        val end: String = "\n\n",
        val stackReverseOrder: Boolean = false,
        val template: (msg: String, mySrc: String, now: Long, stackTrace: String) -> String,
) {
    @Suppress("UNUSED_PARAMETER")
    companion object {
        // CallChain[size=9] = QLogStyle.String.clarifySrcRegion() <-[Call]- QLogStyle.SRC_AND_STACK <-[Call ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
        fun String.clarifySrcRegion(onlyIf: QOnlyIfStr = QOnlyIfStr.Multiline): String {
            if (!onlyIf.matches(this))
                return this

            return """${"SRC START ―――――――――――".qColor(QShColor.Cyan)}
${this.trim()}
${"SRC END   ―――――――――――".qColor(QShColor.Cyan)}"""
        }

        // CallChain[size=10] = QLogStyle.qLogArrow() <-[Call]- QLogStyle.S <-[Call]- qLogStackFrames() <-[C ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
        fun qLogArrow(mySrc: String, msg: String): String {
            return if (mySrc.startsWith("\"") && mySrc.endsWith("\"") && mySrc.substring(1, mySrc.length - 1)
                            .matches("""[\w\s]+""".re)
            ) {
                // src code is simple string
                "\"".light_green + msg + "\"".light_green
//                ("\"" + msg + "\"").light_green
            } else {
                qArrow(mySrc.clarifySrcRegion(QOnlyIfStr.Multiline), msg)
            }
        }

        // CallChain[size=8] = QLogStyle.SRC_AND_STACK <-[Call]- QException.mySrcAndStack <-[Call]- QExcepti ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
        val SRC_AND_STACK: QLogStyle
            get() = QLogStyle(1) { _, mySrc, _, stackTrace ->
                """
${mySrc.clarifySrcRegion(QOnlyIfStr.Always)}
$stackTrace""".trim()
            }

        // CallChain[size=3] = QLogStyle.MSG_AND_STACK <-[Call]- qTest() <-[Call]- main()[Root]
        val MSG_AND_STACK: QLogStyle
            get() = QLogStyle(1, start = "") { msg, _, _, stackTrace ->
                """
$msg
$stackTrace
""".trim()
            }

        // CallChain[size=9] = QLogStyle.S <-[Call]- qLogStackFrames() <-[Call]- QException.mySrcAndStack <- ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
        val S: QLogStyle
            get() = QLogStyle(1) { msg, mySrc, _, stackTrace ->
                """
${qLogArrow(mySrc, msg)}
$stackTrace
""".trim()
            }

        
    }
}

// CallChain[size=9] = qMySrcLinesAtFrame() <-[Call]- qLogStackFrames() <-[Call]- QException.mySrcAn ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun qMySrcLinesAtFrame(
        frame: StackFrame,
        srcCut: QSrcCut = QSrcCut.NOCUT_JUST_SINGLE_LINE,
        srcRoots: List<Path> = QMyPath.src_root,
        srcCharset: Charset = Charsets.UTF_8,
): String {
    return try {
        val src = qCacheItOneSec("${frame.fileName} - ${frame.lineNumber} - ${srcCut.fetchRule.hashCode()}") {
            qSrcFileLinesAtFrame(
                    srcRoots = srcRoots, charset = srcCharset, fetchRule = srcCut.fetchRule, frame = frame
            )
        }

        val src2 = srcCut.cut(src).trimIndent()
        src2
    } catch (e: Exception) {
//        e.message
        "${QMyMark.WARN} Couldn't cut src lines : ${qBrackets("FileName", frame.fileName, "LineNo", frame.lineNumber, "SrcRoots", srcRoots)}"
    }
}

// CallChain[size=8] = qLogStackFrames() <-[Call]- QException.mySrcAndStack <-[Call]- QException.pri ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun qLogStackFrames(
        frames: List<StackFrame>,
        msg: Any? = "",
        style: QLogStyle = QLogStyle.S,
        srcRoots: List<Path> = QMyPath.src_root,
        srcCharset: Charset = Charsets.UTF_8,
        srcCut: QSrcCut = QSrcCut.SINGLE_qLog_PARAM,
        quiet: Boolean = false,
        noColor: Boolean = false,
): String {

    var mySrc = qMySrcLinesAtFrame(frame = frames[0], srcCut = srcCut, srcRoots = srcRoots, srcCharset = srcCharset)

    if (mySrc.trimStart().startsWith("}.") || mySrc.trimStart().startsWith(").") || mySrc.trimStart()
                    .startsWith("\"\"\"")
    ) {
        // Maybe you want to check multiple lines

        val multilineCut = QSrcCut(QFetchRule.SMART_FETCH, srcCut.cut)

        mySrc = qMySrcLinesAtFrame(
                frame = frames[0], srcCut = multilineCut, srcRoots = srcRoots, srcCharset = srcCharset
        )
    }

    val stackTrace = if (style.stackReverseOrder) {
        frames.reversed().joinToString("\n") { it.toString() }
    } else {
        frames.joinToString("\n") { it.toString() }
    }

    val output = style.template(
            msg.qToLogString(), mySrc, qNow, stackTrace
    )

    val text = style.start + output + style.end

    val finalTxt = if (noColor) text.noColor else text

    if (!quiet)
        style.out.print(finalTxt)

    return if (noColor) output.noColor else output
}

// CallChain[size=10] = qSrcFileLinesAtFrame() <-[Call]- qMySrcLinesAtFrame() <-[Call]- qLogStackFra ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun qSrcFileLinesAtFrame(
        srcRoots: List<Path> = QMyPath.src_root,
        pkgDirHint: String? = null,
        charset: Charset = Charsets.UTF_8,
        lineSeparator: QLineSeparator = QLineSeparator.LF,
        fetchRule: QFetchRule = QFetchRule.SMART_FETCH,
        frame: StackFrame = qStackFrame(2),
): String {
    val srcFile: Path = qSrcFileAtFrame(frame = frame, srcRoots = srcRoots, pkgDirHint = pkgDirHint)

    return srcFile.qFetchLinesAround(frame.lineNumber, fetchRule, charset, lineSeparator)
}

// CallChain[size=11] = qArrow() <-[Call]- QLogStyle.qLogArrow() <-[Call]- QLogStyle.S <-[Call]- qLo ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun qArrow(key: Any?, value: Any?): String {
    val keyStr = key.qToLogString()
            .qWithNewLinePrefix(onlyIf = QOnlyIfStr.Multiline)
            .qWithNewLineSuffix(onlyIf = QOnlyIfStr.Always)

    val valStr = value.qToLogString().qWithNewLineSurround(onlyIf = QOnlyIfStr.Multiline)
            .qWithSpacePrefix(numSpace = 2, onlyIf = QOnlyIfStr.SingleLine)

    return "$keyStr$qARROW$valStr"
}

// CallChain[size=4] = String.qBracketEnd() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
/**
 * ```
 * [key1]  value1   [key2]  value2
 * ```
 */
private fun String.qBracketEnd(value: Any?): String {
    val valStr =
            value.qToLogString().qWithSpacePrefix(2, onlyIf = QOnlyIfStr.SingleLine)
                    .qWithNewLineSurround(onlyIf = QOnlyIfStr.Multiline)

    return "[$this]$valStr"
}

// CallChain[size=4] = String.qBracketStartOrMiddle() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
/**
 * ```
 * [key1]  value1   [key2]  value2
 * ```
 */
private fun String.qBracketStartOrMiddle(value: Any?): String {
    val valStr = value.qToLogString().qWithSpacePrefix(2, onlyIf = QOnlyIfStr.SingleLine).qWithSpaceSuffix(3)
            .qWithNewLineSurround(onlyIf = QOnlyIfStr.Multiline)

    return "[$this]$valStr"
}

// CallChain[size=3] = qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun qBrackets(vararg keysAndValues: Any?): String {
    if (keysAndValues.size % 2 != 0) {
        QE.ShouldBeEvenNumber.throwItBrackets("KeysAndValues.size", keysAndValues.size)
    }

    return keysAndValues.asSequence().withIndex().chunked(2) { (key, value) ->
        if (value.index != keysAndValues.size) { // last
            key.value.toString().qBracketStartOrMiddle(value.value)
        } else {
            key.value.toString().qBracketEnd(value.value)
        }
    }.joinToString("")
}

// CallChain[size=10] = QOut <-[Ref]- QLogStyle <-[Ref]- QLogStyle.SRC_AND_STACK <-[Call]- QExceptio ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private interface QOut {
    // CallChain[size=12] = QOut.isAcceptColoredText <-[Propag]- QOut.CONSOLE <-[Call]- QMyLog.out <-[Ca ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    val isAcceptColoredText: Boolean

    // CallChain[size=12] = QOut.print() <-[Propag]- QOut.CONSOLE <-[Call]- QMyLog.out <-[Call]- QLogSty ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun print(msg: Any? = "")

    // CallChain[size=12] = QOut.println() <-[Propag]- QOut.CONSOLE <-[Call]- QMyLog.out <-[Call]- QLogS ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun println(msg: Any? = "")

    // CallChain[size=12] = QOut.close() <-[Propag]- QOut.CONSOLE <-[Call]- QMyLog.out <-[Call]- QLogSty ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun close()

    companion object {
        // CallChain[size=11] = QOut.CONSOLE <-[Call]- QMyLog.out <-[Call]- QLogStyle <-[Ref]- QLogStyle.SRC ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
        val CONSOLE: QOut = QConsole(true)

        
    }
}

// CallChain[size=3] = QOut.separator() <-[Call]- qTest() <-[Call]- main()[Root]
private fun QOut.separator(start: String = "\n", end: String = "\n") {
    this.println(qSeparator(start = start, end = end))
}

// CallChain[size=12] = QConsole <-[Call]- QOut.CONSOLE <-[Call]- QMyLog.out <-[Call]- QLogStyle <-[ ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private class QConsole(override val isAcceptColoredText: Boolean) : QOut {
    // CallChain[size=13] = QConsole.print() <-[Propag]- QConsole <-[Call]- QOut.CONSOLE <-[Call]- QMyLo ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun print(msg: Any?) {
        if (isAcceptColoredText) {
            kotlin.io.print(msg.toString())
        } else {
            kotlin.io.print(msg.toString().noColor)
        }
    }

    // CallChain[size=13] = QConsole.println() <-[Propag]- QConsole <-[Call]- QOut.CONSOLE <-[Call]- QMy ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun println(msg: Any?) {
        kotlin.io.println(msg.toString())
    }

    // CallChain[size=13] = QConsole.close() <-[Propag]- QConsole <-[Call]- QOut.CONSOLE <-[Call]- QMyLo ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun close() {
        // Do nothing
    }
}

// CallChain[size=4] = Method.qName() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun Method.qName(withParenthesis: Boolean = false): String {
    val clsName = declaringClass.simpleName

    val name = if (clsName.isNotEmpty()) {
        "$clsName.$name"
    } else {
        name
    }

    return if (withParenthesis) {
        "$name()"
    } else {
        name
    }
}

// CallChain[size=6] = KClass<*>.qFunctions() <-[Call]- qToStringRegistry <-[Call]- Any?.qToString() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun KClass<*>.qFunctions(matcher: QMFunc = QMFunc.DeclaredOnly and QMFunc.IncludeExtensionsInClass): List<KFunction<*>> {
    val list = mutableListOf<KFunction<*>>()

    var functions = if (matcher.declaredOnly) {
        this.declaredFunctions
    } else {
        this.memberFunctions
    }

    list += functions.filter { matcher.matches(it) }

    if (matcher.includeExtensionsInClass) {
        functions = if (matcher.declaredOnly) {
            this.declaredMemberExtensionFunctions
        } else {
            this.memberExtensionFunctions
        }

        list += functions.filter { matcher.matches(it) }
    }

    return list
}

// CallChain[size=17] = KClass<E>.qEnumValues() <-[Call]- QFlagSet.enumValues <-[Call]- QFlagSet.toE ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun <E : Enum<E>> KClass<E>.qEnumValues(): Array<E> {
    return java.enumConstants as Array<E>
}

// CallChain[size=5] = qThisSrcLineSignature <-[Call]- qTimeIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private val qThisSrcLineSignature: String
    get() = qCallerSrcLineSignature()

// CallChain[size=11] = qSrcFileAtFrame() <-[Call]- qSrcFileLinesAtFrame() <-[Call]- qMySrcLinesAtFr ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun qSrcFileAtFrame(frame: StackFrame, srcRoots: List<Path> = QMyPath.src_root, pkgDirHint: String? = null): Path = qCacheItOneSec(
        frame.fileName + frame.lineNumber + srcRoots.map { it }.joinToString() + pkgDirHint
) {
    val pkgDir = pkgDirHint ?: frame.declaringClass.packageName.replace(".", "/")

    var srcFile: Path? = null

    for (dir in srcRoots) {
        val root = dir.toAbsolutePath()
        val fileInPkgDir = root.resolve(pkgDir).resolve(frame.fileName)
        if (fileInPkgDir.exists()) {
            srcFile = fileInPkgDir
            break
        } else {
            val fileNoPkgDir = root.resolve(frame.fileName)
            if (fileNoPkgDir.exists()) {
                srcFile = fileNoPkgDir
            }
        }
    }

    if (srcFile != null)
        return@qCacheItOneSec srcFile

    return@qCacheItOneSec srcRoots.qFind(QM.exact(frame.fileName), maxDepth = 100)
            .qaNotNull(QE.FileNotFound, qBrackets("FileName", frame.fileName, "SrcRoots", srcRoots))
}

// CallChain[size=3] = qCallerFileName() <-[Call]- qTest() <-[Call]- main()[Root]
private fun qCallerFileName(stackDepth: Int = 0): String {
    return qStackFrame(stackDepth + 2).fileName
}

// CallChain[size=6] = qCallerSrcLineSignature() <-[Call]- qThisSrcLineSignature <-[Call]- qTimeIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun qCallerSrcLineSignature(stackDepth: Int = 0): String {
    val frame = qStackFrame(stackDepth + 2)

    return if (frame.declaringClass?.canonicalName == null) {
        if (frame.fileName != null) {
            if (frame.methodName != "invokeSuspend") {
                frame.fileName + " - " + frame.methodName + " - " + frame.lineNumber
            } else {
                frame.fileName + " - " + frame.lineNumber
            }
        } else {
            frame.methodName + " - " + frame.lineNumber
        }
    } else {
        frame.declaringClass.canonicalName + "." + frame.methodName + " - " + frame.lineNumber
    }
}

// CallChain[size=8] = qStackFrames() <-[Call]- QException.stackFrames <-[Call]- QException.getStack ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private inline fun qStackFrames(
        stackDepth: Int = 0,
        size: Int = 1,
        noinline filter: (StackFrame) -> Boolean = QE.STACK_FRAME_FILTER,
): List<StackFrame> {
    return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk { s: Stream<StackFrame> ->
        s.asSequence().filter(filter).drop(stackDepth).take(size).toList()
    }
}

// CallChain[size=11] = qStackFrame() <-[Call]- qSrcFileLinesAtFrame() <-[Call]- qMySrcLinesAtFrame( ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private inline fun qStackFrame(
        stackDepth: Int = 0,
        noinline filter: (StackFrame) -> Boolean = QE.STACK_FRAME_FILTER,
): StackFrame {
    return qStackFrames(stackDepth, 1, filter)[0]
}

// CallChain[size=3] = qStackFrameEntryMethod() <-[Call]- qTest() <-[Call]- main()[Root]
private fun qStackFrameEntryMethod(filter: (StackFrame) -> Boolean): StackFrame {
    return qStackFrames(0, Int.MAX_VALUE)
            .filter(filter)
            .findLast {
                it.lineNumber > 0
            }.qaNotNull()
}

// CallChain[size=7] = KType.qToClass() <-[Call]- KType.qIsSuperclassOf() <-[Call]- qToStringRegistr ... ing() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun KType.qToClass(): KClass<*>? {
    return if (this.classifier != null && this.classifier is KClass<*>) {
        this.classifier as KClass<*>
    } else {
        null
    }
}

// CallChain[size=6] = KType.qIsSuperclassOf() <-[Call]- qToStringRegistry <-[Call]- Any?.qToString() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun KType.qIsSuperclassOf(cls: KClass<*>): Boolean {
    return try {
        val thisClass = qToClass()

        if (thisClass?.qualifiedName == "kotlin.Array" && cls.qualifiedName == "kotlin.Array") {
            true
        } else {
            thisClass?.isSuperclassOf(cls) ?: false
        }
    } catch (e: Throwable) {
        // Exception in thread "main" kotlin.reflect.jvm.internal.KotlinReflectionInternalError: Unresolved class: ~
        false
    }
}

// CallChain[size=4] = AccessibleObject.qTrySetAccessible() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun AccessibleObject.qTrySetAccessible() {
    try {
        if (!this.trySetAccessible()) {
            QE.TrySetAccessibleFail.throwIt(this)
        }
    } catch (e: SecurityException) {
        QE.TrySetAccessibleFail.throwIt(this, e = e)
    }
}

// CallChain[size=3] = qThisFileMainClass <-[Call]- qTest() <-[Call]- main()[Root]
private val qThisFileMainClass: Class<*>
    get() = qCallerFileMainClass()

// CallChain[size=3] = Class<*>.qMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun Class<*>.qMethods(matcher: QMMethod = QMMethod.DeclaredOnly): List<Method> {
    val allMethods = if (matcher.declaredOnly) declaredMethods else methods
    return allMethods.filter { matcher.matches(it) }
}

// CallChain[size=4] = Class<T>.qNewInstance() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun <T : Any> Class<T>.qNewInstance(vararg params: Any): T {
    val constructor = qConstructor(*params, declaredOnly = false)
    return constructor.newInstance()
}

// CallChain[size=5] = Class<T>.qConstructor() <-[Call]- Class<T>.qNewInstance() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun <T : Any> Class<T>.qConstructor(vararg params: Any, declaredOnly: Boolean = false): Constructor<T> {
    return if (declaredOnly) {
        this.getDeclaredConstructor(*params.map { it::class.java }.toTypedArray())
            .qaNotNull(QE.ConstructorNotFound)
    } else {
        this.getConstructor(*params.map { it::class.java }.toTypedArray())
            .qaNotNull(QE.ConstructorNotFound)
    }
}

// CallChain[size=7] = Class<*>.qPrimitiveToWrapper() <-[Call]- Class<*>.qIsAssignableFrom() <-[Call ... -[Propag]- QMatchMethodParams <-[Call]- QMMethod.NoParams <-[Call]- qTest() <-[Call]- main()[Root]
private fun Class<*>.qPrimitiveToWrapper(): Class<*> = qJVMPrimitiveToWrapperMap[this] ?: this

// CallChain[size=8] = qJVMPrimitiveToWrapperMap <-[Call]- Class<*>.qPrimitiveToWrapper() <-[Call]-  ... -[Propag]- QMatchMethodParams <-[Call]- QMMethod.NoParams <-[Call]- qTest() <-[Call]- main()[Root]
private val qJVMPrimitiveToWrapperMap by lazy {
    val map = HashMap<Class<*>, Class<*>>()

    map[java.lang.Boolean.TYPE] = java.lang.Boolean::class.java
    map[java.lang.Byte.TYPE] = java.lang.Byte::class.java
    map[java.lang.Character.TYPE] = java.lang.Character::class.java
    map[java.lang.Short.TYPE] = java.lang.Short::class.java
    map[java.lang.Integer.TYPE] = java.lang.Integer::class.java
    map[java.lang.Long.TYPE] = java.lang.Long::class.java
    map[java.lang.Double.TYPE] = java.lang.Double::class.java
    map[java.lang.Float.TYPE] = java.lang.Float::class.java
    map
}

// CallChain[size=6] = Class<*>.qIsAssignableFrom() <-[Call]- QMatchMethodParams.matches() <-[Propag]- QMatchMethodParams <-[Call]- QMMethod.NoParams <-[Call]- qTest() <-[Call]- main()[Root]
private fun Class<*>.qIsAssignableFrom(subclass: Class<*>, autoboxing: Boolean = true): Boolean {
    return if (autoboxing) {
        this.qPrimitiveToWrapper().isAssignableFrom(subclass.qPrimitiveToWrapper())
    } else {
        this.isAssignableFrom(subclass)
    }
}

// CallChain[size=4] = Method.qIsInstanceMethod() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun Method.qIsInstanceMethod(): Boolean {
    return !Modifier.isStatic(this.modifiers)
}

// CallChain[size=4] = qCallerFileMainClass() <-[Call]- qThisFileMainClass <-[Call]- qTest() <-[Call]- main()[Root]
private fun qCallerFileMainClass(stackDepth: Int = 0): Class<*> {
    val frame = qStackFrame(stackDepth + 2)
    val fileName = frame.fileName.substring(0, frame.fileName.lastIndexOf("."))
    val pkgName = frame.declaringClass.packageName

    val clsName = if (fileName.endsWith(".kt", true)) {
        if (pkgName.isNotEmpty())
            "$pkgName.${fileName}Kt"
        else
            "${fileName}Kt"
    } else {
        if (pkgName.isNotEmpty())
            "$pkgName.$fileName"
        else
            fileName
    }

    return Class.forName(clsName)
}

// CallChain[size=9] = RO <-[Ref]- qRe() <-[Call]- QException.mySrcAndStack <-[Call]- QException.pri ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private typealias RO = RegexOption

// CallChain[size=8] = qRe() <-[Call]- QException.mySrcAndStack <-[Call]- QException.printStackTrace ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun qRe(@Language("RegExp") regex: String, vararg opts: RO): Regex {
    return qCacheItOneSecThreadLocal(regex + opts.contentToString()) {
        Regex(regex, setOf(*opts))
    }
}

// CallChain[size=10] = re <-[Call]- QFetchRule.SMART_FETCH <-[Call]- qLogStackFrames() <-[Call]- QE ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
// https://youtrack.jetbrains.com/issue/KTIJ-5643
private val @receiver:Language("RegExp") String.re: Regex
    get() = qRe(this)

// CallChain[size=7] = qBG_JUMP <-[Call]- QShColor.bg <-[Call]- String.qApplyEscapeLine() <-[Call]- String.qColor() <-[Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private const val qBG_JUMP = 10

// CallChain[size=5] = qSTART <-[Call]- String.qColor() <-[Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private const val qSTART = "\u001B["

// CallChain[size=7] = qEND <-[Call]- String.qApplyEscapeLine() <-[Call]- String.qApplyEscapeLine()  ... ring.qColor() <-[Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private const val qEND = "${qSTART}0m"

// CallChain[size=7] = String.qApplyEscapeNestable() <-[Call]- String.qApplyEscapeLine() <-[Call]- S ... ring.qColor() <-[Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun String.qApplyEscapeNestable(start: String): String {
    val lastEnd = this.endsWith(qEND)

    return if( lastEnd ) {
            start + this.substring(0, this.length - 1).replace(qEND, qEND + start) + this[this.length - 1]
        } else {
            start + this.replace(qEND, qEND + start) + qEND
        }
}

// CallChain[size=4] = String.qColor() <-[Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun String.qColor(fg: QShColor? = null, bg: QShColor? = null, nestable: Boolean = this.contains(qSTART)): String {
    return if (this.qIsSingleLine()) {
        this.qApplyEscapeLine(fg, bg, nestable)
    } else {
        lineSequence().map { line ->
            line.qApplyEscapeLine(fg, bg, nestable)
        }.joinToString("\n")
    }
}

// CallChain[size=3] = String.qDeco() <-[Call]- italic <-[Call]- QShColorTest.italic()[Root]
private fun String.qDeco(deco: QShDeco): String {
    return if (this.qIsSingleLine()) {
        this.qApplyEscapeLine(arrayOf(deco.start), nestable = this.contains(qSTART))
    } else {
        lineSequence().map { line ->
            line.qApplyEscapeLine(arrayOf(deco.start), nestable = this.contains(qSTART))
        }.joinToString("\n")
    }
}

// CallChain[size=5] = String.qApplyEscapeLine() <-[Call]- String.qColor() <-[Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun String.qApplyEscapeLine(fg: QShColor?, bg: QShColor?, nestable: Boolean): String {
    return this.qApplyEscapeLine(
        listOfNotNull(fg?.fg, bg?.bg).toTypedArray(),
        nestable
    )
}

// CallChain[size=6] = String.qApplyEscapeLine() <-[Call]- String.qApplyEscapeLine() <-[Call]- String.qColor() <-[Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun String.qApplyEscapeLine(
    startSequences: Array<String>,
    nestable: Boolean
): String {
    val nest = nestable && this.contains(qEND)

    var text = this

    for (start in startSequences) {
        text = if (nest) {
            text.qApplyEscapeNestable(start)
        } else {
            "$start$text$qEND"
        }
    }

    return text
}

// CallChain[size=3] = noColor <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private val String.noColor: String
    get() {
        return this.replace("""\Q$qSTART\E\d{1,2}m""".re, "")
    }

// CallChain[size=3] = QShDeco <-[Ref]- String.qColorAndDecoDebug() <-[Call]- QShColorTest.nest2()[Root]
private enum class QShDeco(val code: Int) {
    // CallChain[size=4] = QShDeco.Bold <-[Propag]- QShDeco.start <-[Call]- String.qColorAndDecoDebug() <-[Call]- QShColorTest.nest2()[Root]
    // https://en.wikipedia.org/wiki/ANSI_escape_code
    Bold(1),
    // CallChain[size=4] = QShDeco.Italic <-[Propag]- QShDeco.start <-[Call]- String.qColorAndDecoDebug() <-[Call]- QShColorTest.nest2()[Root]
    Italic(3),
    // CallChain[size=4] = QShDeco.Underline <-[Propag]- QShDeco.start <-[Call]- String.qColorAndDecoDebug() <-[Call]- QShColorTest.nest2()[Root]
    Underline(4);

    // CallChain[size=3] = QShDeco.start <-[Call]- String.qColorAndDecoDebug() <-[Call]- QShColorTest.nest2()[Root]
    val start: String = "$qSTART${code}m"

    companion object {
        
    }
}

// CallChain[size=4] = QShColor <-[Ref]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private enum class QShColor(val code: Int) {
    // CallChain[size=5] = QShColor.Black <-[Propag]- QShColor.LightGreen <-[Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    Black(30),
    // CallChain[size=5] = QShColor.Red <-[Propag]- QShColor.LightGreen <-[Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    Red(31),
    // CallChain[size=5] = QShColor.Green <-[Propag]- QShColor.LightGreen <-[Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    Green(32),
    // CallChain[size=5] = QShColor.Yellow <-[Propag]- QShColor.LightGreen <-[Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    Yellow(33),
    // CallChain[size=5] = QShColor.Blue <-[Propag]- QShColor.LightGreen <-[Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    Blue(34),
    // CallChain[size=5] = QShColor.Magenta <-[Propag]- QShColor.LightGreen <-[Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    Magenta(35),
    // CallChain[size=5] = QShColor.Cyan <-[Propag]- QShColor.LightGreen <-[Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    Cyan(36),
    // CallChain[size=5] = QShColor.LightGray <-[Propag]- QShColor.LightGreen <-[Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    LightGray(37),

    // CallChain[size=5] = QShColor.DarkGray <-[Propag]- QShColor.LightGreen <-[Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    DarkGray(90),
    // CallChain[size=5] = QShColor.LightRed <-[Propag]- QShColor.LightGreen <-[Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    LightRed(91),
    // CallChain[size=4] = QShColor.LightGreen <-[Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    LightGreen(92),
    // CallChain[size=5] = QShColor.LightYellow <-[Propag]- QShColor.LightGreen <-[Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    LightYellow(93),
    // CallChain[size=5] = QShColor.LightBlue <-[Propag]- QShColor.LightGreen <-[Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    LightBlue(94),
    // CallChain[size=5] = QShColor.LightMagenta <-[Propag]- QShColor.LightGreen <-[Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    LightMagenta(95),
    // CallChain[size=5] = QShColor.LightCyan <-[Propag]- QShColor.LightGreen <-[Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    LightCyan(96),
    // CallChain[size=5] = QShColor.White <-[Propag]- QShColor.LightGreen <-[Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    White(97);

    // CallChain[size=6] = QShColor.fg <-[Call]- String.qApplyEscapeLine() <-[Call]- String.qColor() <-[Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    val fg: String = "$qSTART${code}m"

    // CallChain[size=6] = QShColor.bg <-[Call]- String.qApplyEscapeLine() <-[Call]- String.qColor() <-[Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    val bg: String = "$qSTART${code + qBG_JUMP}m"

    companion object {
        
    }
}

// CallChain[size=2] = String.qColorAndDecoDebug() <-[Call]- QShColorTest.nest2()[Root]
private fun String.qColorAndDecoDebug(tagStart: String = "[", tagEnd: String = "]"): String {
    var txt = this
    for (color in QShColor.values()) {
        txt = txt.replace(color.fg, "$tagStart${color.name}$tagEnd", ignoreCase = false)
        txt = txt.replace(color.bg, "$tagStart${color.name}_BG$tagEnd", ignoreCase = false)
    }
    for (deco in QShDeco.values()) {
        txt = txt.replace(deco.start, "$tagStart${deco.name}$tagEnd", ignoreCase = false)
    }

    txt = txt.replace(qEND, "${tagStart}End$tagEnd")

    return txt
}

// CallChain[size=8] = String.qColorTarget() <-[Call]- QException.mySrcAndStack <-[Call]- QException ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun String.qColorTarget(ptn: Regex, fg: QShColor? = null, bg: QShColor? = null): String {
    return ptn.replace(this, "$0".qColor(fg, bg))
}

// CallChain[size=2] = bold <-[Call]- QShColorTest.bold()[Root]
private val String?.bold: String
    get() = this?.qDeco(QShDeco.Bold) ?: "null".qDeco(QShDeco.Bold)

// CallChain[size=2] = italic <-[Call]- QShColorTest.italic()[Root]
private val String?.italic: String
    get() = this?.qDeco(QShDeco.Italic) ?: "null".qDeco(QShDeco.Italic)

// CallChain[size=2] = underline <-[Call]- QShColorTest.underline()[Root]
private val String?.underline: String
    get() = this?.qDeco(QShDeco.Underline) ?: "null".qDeco(QShDeco.Underline)

// CallChain[size=2] = red <-[Call]- QShColorTest.nest2()[Root]
private val String?.red: String
    get() = this?.qColor(QShColor.Red) ?: "null".qColor(QShColor.Red)

// CallChain[size=2] = green <-[Call]- QShColorTest.colourful()[Root]
private val String?.green: String
    get() = this?.qColor(QShColor.Green) ?: "null".qColor(QShColor.Green)

// CallChain[size=8] = yellow <-[Call]- QException.qToString() <-[Call]- QException.toString() <-[Pr ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private val String?.yellow: String
    get() = this?.qColor(QShColor.Yellow) ?: "null".qColor(QShColor.Yellow)

// CallChain[size=2] = blue <-[Call]- QShColorTest.nest2()[Root]
private val String?.blue: String
    get() = this?.qColor(QShColor.Blue) ?: "null".qColor(QShColor.Blue)

// CallChain[size=2] = magenta <-[Call]- QShColorTest.colourful()[Root]
private val String?.magenta: String
    get() = this?.qColor(QShColor.Magenta) ?: "null".qColor(QShColor.Cyan)

// CallChain[size=10] = cyan <-[Call]- QLogStyle <-[Ref]- QLogStyle.SRC_AND_STACK <-[Call]- QExcepti ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private val String?.cyan: String
    get() = this?.qColor(QShColor.Cyan) ?: "null".qColor(QShColor.Cyan)

// CallChain[size=3] = light_gray <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private val String?.light_gray: String
    get() = this?.qColor(QShColor.LightGray) ?: "null".qColor(QShColor.LightGray)

// CallChain[size=6] = light_red <-[Call]- allTestedMethods <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private val String?.light_red: String
    get() = this?.qColor(QShColor.LightRed) ?: "null".qColor(QShColor.LightRed)

// CallChain[size=3] = light_green <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private val String?.light_green: String
    get() = this?.qColor(QShColor.LightGreen) ?: "null".qColor(QShColor.LightGreen)

// CallChain[size=5] = light_yellow <-[Call]- colorIt <-[Call]- qFailMsg() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private val String?.light_yellow: String
    get() = this?.qColor(QShColor.LightYellow) ?: "null".qColor(QShColor.LightYellow)

// CallChain[size=3] = light_blue <-[Call]- qTest() <-[Call]- main()[Root]
private val String?.light_blue: String
    get() = this?.qColor(QShColor.LightBlue) ?: "null".qColor(QShColor.LightBlue)

// CallChain[size=13] = light_cyan <-[Call]- qARROW <-[Call]- qArrow() <-[Call]- QLogStyle.qLogArrow ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private val String?.light_cyan: String
    get() = this?.qColor(QShColor.LightCyan) ?: "null".qColor(QShColor.LightCyan)

// CallChain[size=10] = path <-[Call]- QMyPath.src_root <-[Call]- qLogStackFrames() <-[Call]- QExcep ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private val String.path: Path
    get() = Paths.get(this.trim()).toAbsolutePath().normalize()

// CallChain[size=8] = QLR <-[Ref]- String.qAlignCenter() <-[Call]- String.qWithMinLength() <-[Call] ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private enum class QLR {
    // CallChain[size=8] = QLR.LEFT <-[Call]- String.qAlignCenter() <-[Call]- String.qWithMinLength() <- ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    LEFT,
    // CallChain[size=8] = QLR.RIGHT <-[Call]- String.qAlignCenter() <-[Call]- String.qWithMinLength() < ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    RIGHT
}

// CallChain[size=4] = qSeparator() <-[Call]- QOut.separator() <-[Call]- qTest() <-[Call]- main()[Root]
private fun qSeparator(
    fg: QShColor? = QShColor.LightGray,
    bg: QShColor? = null,
    char: Char = '⎯',
    length: Int = 80,
    start: String = "\n",
    end: String = "\n",
): String {
    return start + char.toString().repeat(length).qColor(fg, bg) + end
}

// CallChain[size=4] = qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun qSeparatorWithLabel(
    label: String,
    fg: QShColor? = QShColor.LightGray,
    bg: QShColor? = null,
    char: Char = '⎯',
    length: Int = 70,
    start: String = "\n",
    end: String = "\n",
): String {
    return start + label + "  " + char.toString().repeat((length - label.length - 2).coerceAtLeast(0)).qColor(fg, bg)
            .qWithMinAndMaxLength(length, length, alignment = QAlign.LEFT, endDots = "") + end
}

// CallChain[size=5] = String.qWithMinAndMaxLength() <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun String.qWithMinAndMaxLength(
        minLength: Int,
        maxLength: Int,
        alignment: QAlign = QAlign.RIGHT,
        endDots: String = "...",
): String {
    (minLength <= maxLength).qaTrue()

    return if (this.length > maxLength) {
        qWithMaxLength(maxLength, endDots = endDots)
    } else {
        qWithMinLength(minLength, alignment)
    }
}

// CallChain[size=6] = String.qWithMinLength() <-[Call]- String.qWithMinAndMaxLength() <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun String.qWithMinLength(minLength: Int, alignment: QAlign = QAlign.RIGHT): String = when (alignment) {
    QAlign.LEFT -> String.format("%-${minLength}s", this)
    QAlign.RIGHT -> String.format("%${minLength}s", this)
    QAlign.CENTER -> String.format("%${minLength}s", this).qAlignCenter()
}

// CallChain[size=6] = String.qWithMaxLength() <-[Call]- String.qWithMinAndMaxLength() <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun String.qWithMaxLength(maxLength: Int, endDots: String = " ..."): String {
    if (maxLength - endDots.length < 0)
        QE.IllegalArgument.throwIt(maxLength - endDots.length)

    if (length < maxLength)
        return this

    if (endDots.isNotEmpty() && length < endDots.length + 1)
        return this

    return substring(0, length.coerceAtMost(maxLength - endDots.length)) + endDots
}

// CallChain[size=5] = QOnlyIfStr <-[Ref]- String.qBracketEnd() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private enum class QOnlyIfStr(val matches: (String) -> Boolean) {
    // CallChain[size=5] = QOnlyIfStr.Multiline <-[Call]- String.qBracketEnd() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    Multiline({ it.qIsMultiLine() }),
    // CallChain[size=5] = QOnlyIfStr.SingleLine <-[Call]- String.qBracketEnd() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    SingleLine({ it.qIsSingleLine() }),
    // CallChain[size=6] = QOnlyIfStr.Empty <-[Propag]- QOnlyIfStr.Multiline <-[Call]- String.qBracketEnd() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    Empty({ it.isEmpty() }),
    // CallChain[size=6] = QOnlyIfStr.Blank <-[Propag]- QOnlyIfStr.Multiline <-[Call]- String.qBracketEnd() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    Blank({ it.isBlank() }),
    // CallChain[size=6] = QOnlyIfStr.NotEmpty <-[Propag]- QOnlyIfStr.Multiline <-[Call]- String.qBracketEnd() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    NotEmpty({ it.isNotEmpty() }),
    // CallChain[size=6] = QOnlyIfStr.NotBlank <-[Propag]- QOnlyIfStr.Multiline <-[Call]- String.qBracketEnd() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    NotBlank({ it.isNotBlank() }),
    // CallChain[size=6] = QOnlyIfStr.Always <-[Propag]- QOnlyIfStr.Multiline <-[Call]- String.qBracketEnd() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    Always({ true })
}

// CallChain[size=6] = String.qWithNewLinePrefix() <-[Call]- String.qWithNewLineSurround() <-[Call]- ... qBracketEnd() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun String.qWithNewLinePrefix(
        numNewLine: Int = 1,
        onlyIf: QOnlyIfStr = QOnlyIfStr.Multiline,
        lineSeparator: QLineSeparator = QLineSeparator.LF,
): String {
    if (!onlyIf.matches(this)) return this

    val nCount = takeWhile { it == '\n' || it == '\r' }.count()

    return lineSeparator.value.repeat(numNewLine) + substring(nCount)
}

// CallChain[size=6] = String.qWithNewLineSuffix() <-[Call]- String.qWithNewLineSurround() <-[Call]- ... qBracketEnd() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun String.qWithNewLineSuffix(numNewLine: Int = 1, onlyIf: QOnlyIfStr = QOnlyIfStr.Multiline): String {
    if (!onlyIf.matches(this)) return this

    val nCount = takeLastWhile { it == '\n' || it == '\r' }.count()

    return substring(0, length - nCount) + "\n".repeat(numNewLine)
}

// CallChain[size=5] = String.qWithNewLineSurround() <-[Call]- String.qBracketEnd() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun String.qWithNewLineSurround(numNewLine: Int = 1, onlyIf: QOnlyIfStr = QOnlyIfStr.Multiline): String {
    if (!onlyIf.matches(this)) return this

    return qWithNewLinePrefix(numNewLine, QOnlyIfStr.Always).qWithNewLineSuffix(numNewLine, QOnlyIfStr.Always)
}

// CallChain[size=5] = String.qWithSpacePrefix() <-[Call]- String.qBracketEnd() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun String.qWithSpacePrefix(numSpace: Int = 1, onlyIf: QOnlyIfStr = QOnlyIfStr.SingleLine): String {
    if (!onlyIf.matches(this)) return this

    return " ".repeat(numSpace) + this.trimStart()
}

// CallChain[size=5] = String.qWithSpaceSuffix() <-[Call]- String.qBracketStartOrMiddle() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun String.qWithSpaceSuffix(numSpace: Int = 1, onlyIf: QOnlyIfStr = QOnlyIfStr.SingleLine): String {
    if (!onlyIf.matches(this)) return this

    return this.trimEnd() + " ".repeat(numSpace)
}

// CallChain[size=10] = CharSequence.qEndsWith() <-[Call]- QFetchRule.SMART_FETCH <-[Call]- qLogStac ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun CharSequence.qEndsWith(suffix: Regex, length: Int = 100): Boolean {
    return takeLast(min(length, this.length)).matches(suffix)
}

// CallChain[size=6] = String.qIsMultiLine() <-[Call]- String.qIsSingleLine() <-[Call]- String.qColor() <-[Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun String.qIsMultiLine(): Boolean {
    return this.contains("\n") || this.contains("\r")
}

// CallChain[size=5] = String.qIsSingleLine() <-[Call]- String.qColor() <-[Call]- light_green <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun String.qIsSingleLine(): Boolean {
    return !this.qIsMultiLine()
}

// CallChain[size=7] = QLineSeparator <-[Ref]- String.qWithNewLinePrefix() <-[Call]- String.qWithNew ... qBracketEnd() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private enum class QLineSeparator(val value: String) {
    // CallChain[size=7] = QLineSeparator.LF <-[Call]- String.qWithNewLinePrefix() <-[Call]- String.qWit ... qBracketEnd() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    LF("\n"),
    // CallChain[size=8] = QLineSeparator.CRLF <-[Propag]- QLineSeparator.QLineSeparator() <-[Call]- Str ... qBracketEnd() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    CRLF("\r\n"),
    // CallChain[size=8] = QLineSeparator.CR <-[Propag]- QLineSeparator.QLineSeparator() <-[Call]- Strin ... qBracketEnd() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    CR("\r");

    companion object {
        // CallChain[size=13] = QLineSeparator.DEFAULT <-[Call]- Path.qLineSeparator() <-[Call]- Path.qFetch ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
        val DEFAULT = QLineSeparator.LF
    }
}

// CallChain[size=11] = String.qSubstring() <-[Call]- String.qMoveLeft() <-[Call]- QLineMatchResult. ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun String.qSubstring(rangeBothInclusive: IntRange): String =
        substring(rangeBothInclusive.first, rangeBothInclusive.last + 1)

// CallChain[size=10] = String.qCountLeftSpace() <-[Call]- QFetchRule.SMART_FETCH <-[Call]- qLogStac ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun String.qCountLeftSpace(): Int = takeWhile { it == ' ' }.count()

// CallChain[size=11] = String.qCountRightSpace() <-[Call]- String.qMoveCenter() <-[Call]- QLineMatc ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun String.qCountRightSpace(): Int = takeLastWhile { it == ' ' }.count()

// CallChain[size=4] = qMASK_LENGTH_LIMIT <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private const val qMASK_LENGTH_LIMIT: Int = 100_000

// CallChain[size=6] = QToString <-[Ref]- qToStringRegistry <-[Call]- Any?.qToString() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private class QToString(val okToApply: (Any) -> Boolean, val toString: (Any) -> String)

// CallChain[size=5] = qToStringRegistry <-[Call]- Any?.qToString() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
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

// CallChain[size=4] = Any?.qToString() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun Any?.qToString(): String {
    if (this == null)
        return "null".light_gray

    for (r in qToStringRegistry) {
        if (r.okToApply(this)) {
            return r.toString(this)
        }
    }

    return toString()
}

// CallChain[size=3] = Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun Any?.qToLogString(maxLineLength: Int = 80): String {
    if (QMyLog.no_format) {
        return this.toString()
    }

    if (this == null)
        return "null".light_gray
    if (this == "")
        return "".qClarifyEmptyOrBlank()

    val str = this.qToString()

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
        str.trim()
    }.qClarifyEmptyOrBlank()
}

// CallChain[size=4] = String.qClarifyEmptyOrBlank() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun String.qClarifyEmptyOrBlank(): String {
    return if (this.isEmpty()) {
        "(EMPTY STRING)".qColor(QShColor.LightGray)
    } else if (this.isBlank()) {
        "$this(BLANK STRING)".qColor(QShColor.LightGray)
    } else {
        this
    }
}

// CallChain[size=5] = QAlign <-[Ref]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private enum class QAlign {
    // CallChain[size=5] = QAlign.LEFT <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    LEFT,
    // CallChain[size=6] = QAlign.RIGHT <-[Propag]- QAlign.LEFT <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    RIGHT,
    // CallChain[size=6] = QAlign.CENTER <-[Propag]- QAlign.LEFT <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    CENTER
}

// CallChain[size=9] = QLineMatchResult <-[Call]- String.qAlign() <-[Call]- String.qAlignCenter() <- ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private class QLineMatchResult(
    val regex: Regex,
    val text: String,
    val onlyFirstMatch: Boolean = false,
    val groupIdx: QGroupIdx
) {
    // CallChain[size=10] = QLineMatchResult.curText <-[Call]- QLineMatchResult.align() <-[Call]- String ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    var curText: String = text

    // CallChain[size=10] = QLineMatchResult.updateResult() <-[Call]- QLineMatchResult.align() <-[Call]- ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    private fun updateResult(align: QAlign) {
        updateRowResult()
        updateColResult()
        updateColDestPos(align)
    }

    // CallChain[size=11] = QLineMatchResult.rowResults <-[Call]- QLineMatchResult.matchedRange() <-[Cal ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    // line index -> match index
    lateinit var rowResults: List<List<MatchResult>>

    // CallChain[size=12] = QLineMatchResult.colResults <-[Call]- QLineMatchResult.updateColDestPos() <- ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    // col index -> match index
    lateinit var colResults: List<List<MatchResult>>

    // CallChain[size=10] = QLineMatchResult.colDestPos <-[Call]- QLineMatchResult.align() <-[Call]- Str ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    // col index -> max left index / right index of matched region
    lateinit var colDestPos: List<QLeftRight>

    // CallChain[size=11] = QLineMatchResult.updateRowResult() <-[Call]- QLineMatchResult.updateResult() ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    private fun updateRowResult() {
        rowResults = if (onlyFirstMatch) {
            curText.lineSequence().map { regex.find(it, 0) }.map {
                if (it == null) {
                    emptyList()
                } else {
                    listOf(it)
                }
            }.toList()
        } else {
            curText.lineSequence().map { regex.findAll(it, 0).toList() }.toList()
        }
    }

    // CallChain[size=11] = QLineMatchResult.updateColResult() <-[Call]- QLineMatchResult.updateResult() ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    private fun updateColResult() {
        colResults = mutableListOf<List<MatchResult>>().also { list ->
            val maximumRowMatchCount = rowResults.maxOfOrNull { it.size } ?: -1

            for (iColumn in 0 until maximumRowMatchCount) {
                list += rowResults.mapNotNull { result ->
                    result.getOrNull(iColumn)
                }
            }
        }
    }

    // CallChain[size=11] = QLineMatchResult.updateColDestPos() <-[Call]- QLineMatchResult.updateResult( ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    private fun updateColDestPos(align: QAlign) {
        colDestPos = if (align == QAlign.RIGHT) {
            colResults.map { it.qMinOrMaxIndexLR(groupIdx, QMinOrMax.MAX) }
        } else {
            colResults.map { it.qMinOrMaxIndexLR(groupIdx, QMinOrMax.MIN) }
        }
    }

    // CallChain[size=10] = QLineMatchResult.matchedRange() <-[Call]- QLineMatchResult.align() <-[Call]- ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    private fun matchedRange(rowIdx: Int, colIdx: Int): IntRange? {
        val groups = rowResults.getOrNull(rowIdx)?.getOrNull(colIdx)?.groups ?: return null
        return if (groupIdx.idx < groups.size) {
            groups[groupIdx.idx]?.range
        } else {
            null
        }
    }

    // CallChain[size=12] = QLineMatchResult.QMinOrMax <-[Ref]- QLineMatchResult.updateColDestPos() <-[C ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    enum class QMinOrMax {
        MIN, MAX
    }

    // CallChain[size=12] = QLineMatchResult.List<MatchResult>.qMinOrMaxIndexLR() <-[Call]- QLineMatchRe ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    private fun List<MatchResult>.qMinOrMaxIndexLR(
        groupIdx: QGroupIdx,
        minOrMax: QMinOrMax
    ): QLeftRight {
        val leftList = mapNotNull {
            if (groupIdx.idx < it.groups.size) {
                it.groups[groupIdx.idx]?.range?.first
            } else {
                -1
            }
        }

        val left =
            if (minOrMax == QMinOrMax.MIN) {
                leftList.minOrNull() ?: -1
            } else {
                leftList.maxOrNull() ?: -1
            }

        val rightList = mapNotNull {
            if (groupIdx.idx < it.groups.size) {
                it.groups[groupIdx.idx]?.range?.last
            } else {
                -1
            }
        }

        val right =
            if (minOrMax == QMinOrMax.MIN) {
                rightList.minOrNull() ?: -1
            } else {
                rightList.maxOrNull() ?: -1
            }

        return QLeftRight(left, right)
    }

    // CallChain[size=9] = QLineMatchResult.align() <-[Call]- String.qAlign() <-[Call]- String.qAlignCen ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    fun align(align: QAlign = QAlign.RIGHT, keepLength: Boolean = false, oddLengthTuning: QLR): String {
        updateResult(align)

        var colIdx = 0

        while (colIdx < colDestPos.size) {
            val lines = curText.lineSequence().mapIndexed { rowIdx, line ->
                val range = matchedRange(rowIdx, colIdx) ?: return@mapIndexed line

                if (align == QAlign.CENTER) {
                    line.qMoveCenter(range, oddLengthTuning)
                } else {
                    val maxLR = colDestPos[colIdx]

                    val destLeft = when (align) {
                        QAlign.RIGHT -> maxLR.right - range.qSize + 1
                        QAlign.LEFT -> maxLR.left
                        else -> qUnreachable()
                    }

                    if (range.first < destLeft) {
                        line.qMoveRight(range, destLeft, keepLength)
                    } else {
                        line.qMoveLeft(range, destLeft, keepLength)
                    }
                }
            }

            curText = lines.joinToString("\n")

            if (!keepLength && align != QAlign.CENTER) {
                updateResult(align)
            }

            colIdx++
        }

        return curText
    }
}

// CallChain[size=10] = qSize <-[Call]- QLineMatchResult.align() <-[Call]- String.qAlign() <-[Call]- ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private val IntRange.qSize: Int
    get() = abs(last - first) + 1

// CallChain[size=8] = String.qIsNumber() <-[Call]- String.qAlignCenter() <-[Call]- String.qWithMinL ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun String.qIsNumber(): Boolean {
    return this.trim().matches("""[\d./eE+-]+""".re)
}

// CallChain[size=7] = String.qAlignCenter() <-[Call]- String.qWithMinLength() <-[Call]- String.qWit ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun String.qAlignCenter(
    vararg places: Regex = arrayOf("(.*)".re),
    onlyFirstMatch: Boolean = true,
    oddLengthTuning: QLR = if (qIsNumber()) QLR.RIGHT else QLR.LEFT,
    groupIdx: QGroupIdx = QGroupIdx.FIRST
): String {
    return qAlign(QAlign.CENTER, *places, onlyFirstMatch = onlyFirstMatch, oddLengthTuning = oddLengthTuning, groupIdx = groupIdx)
}

// CallChain[size=8] = String.qAlign() <-[Call]- String.qAlignCenter() <-[Call]- String.qWithMinLeng ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun String.qAlign(
    align: QAlign = QAlign.RIGHT,
    vararg places: Regex,
    onlyFirstMatch: Boolean = true,
    keepLength: Boolean = false,
    oddLengthTuning: QLR = if (qIsNumber()) QLR.RIGHT else QLR.LEFT,
    groupIdx: QGroupIdx = QGroupIdx.ENTIRE_MATCH
): String {
    var text = this
    for (p in places) {
        text = QLineMatchResult(p, text, onlyFirstMatch, groupIdx).align(align, keepLength, oddLengthTuning)
    }

    return text
}

// CallChain[size=11] = QLeftRight <-[Ref]- QLineMatchResult.colDestPos <-[Call]- QLineMatchResult.a ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private data class QLeftRight(val left: Int, val right: Int)

// CallChain[size=8] = QGroupIdx <-[Ref]- String.qAlignCenter() <-[Call]- String.qWithMinLength() <- ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private enum class QGroupIdx(val idx: Int) {
    // CallChain[size=9] = QGroupIdx.ENTIRE_MATCH <-[Call]- String.qAlign() <-[Call]- String.qAlignCente ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    ENTIRE_MATCH(0),
    // CallChain[size=8] = QGroupIdx.FIRST <-[Call]- String.qAlignCenter() <-[Call]- String.qWithMinLeng ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    FIRST(1),
    // CallChain[size=12] = QGroupIdx.SECOND <-[Propag]- QGroupIdx.QGroupIdx() <-[Call]- QLineMatchResul ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    SECOND(2),
    // CallChain[size=12] = QGroupIdx.THIRD <-[Propag]- QGroupIdx.QGroupIdx() <-[Call]- QLineMatchResult ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    THIRD(3),
    // CallChain[size=12] = QGroupIdx.FOURTH <-[Propag]- QGroupIdx.QGroupIdx() <-[Call]- QLineMatchResul ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    FOURTH(4),
    // CallChain[size=12] = QGroupIdx.FIFTH <-[Propag]- QGroupIdx.QGroupIdx() <-[Call]- QLineMatchResult ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    FIFTH(5),
    // CallChain[size=12] = QGroupIdx.SIXTH <-[Propag]- QGroupIdx.QGroupIdx() <-[Call]- QLineMatchResult ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    SIXTH(6),
    // CallChain[size=12] = QGroupIdx.SEVENTH <-[Propag]- QGroupIdx.QGroupIdx() <-[Call]- QLineMatchResu ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    SEVENTH(7),
    // CallChain[size=12] = QGroupIdx.EIGHTH <-[Propag]- QGroupIdx.QGroupIdx() <-[Call]- QLineMatchResul ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    EIGHTH(8),
    // CallChain[size=12] = QGroupIdx.NINTH <-[Propag]- QGroupIdx.QGroupIdx() <-[Call]- QLineMatchResult ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    NINTH(9),
    // CallChain[size=12] = QGroupIdx.TENTH <-[Propag]- QGroupIdx.QGroupIdx() <-[Call]- QLineMatchResult ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    TENTH(10);
}

// CallChain[size=10] = String.qMoveCenter() <-[Call]- QLineMatchResult.align() <-[Call]- String.qAl ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
// always keep length
private fun String.qMoveCenter(range: IntRange, oddLengthTuning: QLR): String {
    val regionText = qSubstring(range) // includes spaces

    val nLeftSpace = regionText.qCountLeftSpace()
    val nRightSpace = regionText.qCountRightSpace()

    val nonSpaceChars = regionText.substring(nLeftSpace, regionText.length - nRightSpace)

    val nLeftSpaceTarget =
        if (oddLengthTuning == QLR.LEFT || (nLeftSpace + nRightSpace) % 2 == 0) {
            abs(nLeftSpace + nRightSpace) / 2
        } else {
            abs(nLeftSpace + nRightSpace) / 2 + 1
        }
    val nRightSpaceTarget = (nLeftSpace + nRightSpace) - nLeftSpaceTarget

    return replaceRange(range, " ".repeat(nLeftSpaceTarget) + nonSpaceChars + " ".repeat(nRightSpaceTarget))
//    return substring(
//        0, range.first
//    ) + " ".repeat(nLeftSpaceTarget) + nonSpaceChars + " ".repeat(nRightSpaceTarget) + substring(range.last + 1, length)
}

// CallChain[size=10] = String.qMoveLeft() <-[Call]- QLineMatchResult.align() <-[Call]- String.qAlig ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun String.qMoveLeft(range: IntRange, destRangeLeft: Int, keepLength: Boolean): String {
    if (destRangeLeft >= range.first) {
        return this
    }

    if (substring(destRangeLeft, range.first).isNotBlank()) {
        // can't move. already has some contents.
        return this
    }

    val regionText = qSubstring(range)

    val nSpaces = range.first - destRangeLeft
    if (nSpaces <= 0) return this

    // when keepLength is true, add as many spaces to the right as removed
    val rightSpaces = if (keepLength) " ".repeat(nSpaces) else ""

    // cut left spaces
    val first = substring(0, range.first - nSpaces) + regionText

    // add spaces to the right
    return first + rightSpaces + substring(range.last + 1, length)
}

// CallChain[size=10] = String.qMoveRight() <-[Call]- QLineMatchResult.align() <-[Call]- String.qAli ...  <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private fun String.qMoveRight(range: IntRange, destRangeLeft: Int, keepLength: Boolean): String {
    val regionText = qSubstring(range)

    val nSpaces = destRangeLeft - range.first

    if (nSpaces <= 0) return this

    val spaces = " ".repeat(nSpaces)

    return if (keepLength) {
        if (range.last + 1 > length) return this

        if (range.last + 1 + nSpaces > length) return this

        if (substring(range.last + 1, range.last + 1 + nSpaces).isNotBlank()) return this

        replaceRange(IntRange(range.first, range.last - nSpaces), spaces + regionText)
    } else {
        replaceRange(range, spaces + regionText)
    }
}

// CallChain[size=10] = String.qCountOccurrence() <-[Call]- QFetchRule.SMART_FETCH <-[Call]- qLogSta ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun String.qCountOccurrence(word: String): Int {
    return windowed(word.length) {
        if (it == word)
            1
        else
            0
    }.sum()
}

// CallChain[size=7] = QMask <-[Ref]- QMaskBetween <-[Call]- QMask.DOUBLE_QUOTE <-[Call]- QMask.KOTLIN_STRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private interface QMask {
    // CallChain[size=5] = QMask.apply() <-[Propag]- QMask.KOTLIN_STRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun apply(text: String): QMaskResult

    companion object {
        // CallChain[size=5] = QMask.THREE_DOUBLE_QUOTES <-[Call]- QMask.KOTLIN_STRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
        val THREE_DOUBLE_QUOTES by lazy {
            QMaskBetween(
                "\"\"\"", "\"\"\"",
                nestStartSequence = null,
                escapeChar = '\\',
                maskIncludeStartAndEndSequence = false,
            )
        }
        // CallChain[size=5] = QMask.DOUBLE_QUOTE <-[Call]- QMask.KOTLIN_STRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
        val DOUBLE_QUOTE by lazy {
            QMaskBetween(
                "\"", "\"",
                nestStartSequence = null,
                escapeChar = '\\',
                maskIncludeStartAndEndSequence = false,
            )
        }
        // CallChain[size=4] = QMask.KOTLIN_STRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
        val KOTLIN_STRING by lazy {
            QMultiMask(
                THREE_DOUBLE_QUOTES,
                DOUBLE_QUOTE
            )
        }
        // CallChain[size=4] = QMask.PARENS <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
        val PARENS by lazy {
            QMaskBetween(
                "(", ")",
                nestStartSequence = "(", escapeChar = '\\'
            )
        }
        // CallChain[size=4] = QMask.INNER_BRACKETS <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
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

// CallChain[size=5] = QMultiMask <-[Call]- QMask.KOTLIN_STRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private class QMultiMask(vararg mask: QMaskBetween) : QMask {
    // CallChain[size=7] = QMultiMask.masks <-[Call]- QMultiMask.apply() <-[Propag]- QMultiMask <-[Call] ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    val masks: Array<QMaskBetween>

    // CallChain[size=6] = QMultiMask.init { <-[Propag]- QMultiMask <-[Call]- QMask.KOTLIN_STRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    init {
        masks = arrayOf(*mask)
    }

    // CallChain[size=6] = QMultiMask.apply() <-[Propag]- QMultiMask <-[Call]- QMask.KOTLIN_STRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun apply(text: String): QMaskResult {
        var result: QMaskResult? = null
        for (mask in masks) {
            result = result?.applyMoreMask(mask) ?: mask.apply(text)
        }

        return result!!
    }
}

// CallChain[size=6] = QMaskBetween <-[Call]- QMask.DOUBLE_QUOTE <-[Call]- QMask.KOTLIN_STRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private class QMaskBetween(
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

    // CallChain[size=7] = QMaskBetween.apply() <-[Propag]- QMaskBetween.QMaskBetween() <-[Ref]- QMask.D ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun apply(text: String): QMaskResult {
        return applyMore(text, null)
    }

    // CallChain[size=8] = QMaskBetween.applyMore() <-[Call]- QMaskBetween.apply() <-[Propag]- QMaskBetw ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
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

// CallChain[size=11] = QMutRegion <-[Ref]- QRegion.toMutRegion() <-[Propag]- QRegion.contains() <-[ ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private open class QMutRegion(override var start: Int, override var end: Int) : QRegion(start, end) {
    // CallChain[size=12] = QMutRegion.intersectMut() <-[Propag]- QMutRegion <-[Ref]- QRegion.toMutRegio ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun intersectMut(region: QRegion) {
        val start = max(this.start, region.start)
        val end = min(this.end, region.end)

        if (start <= end) {
            this.start = start
            this.end = end
        }
    }

    // CallChain[size=12] = QMutRegion.addOffset() <-[Propag]- QMutRegion <-[Ref]- QRegion.toMutRegion() ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun addOffset(offset: Int) {
        start += offset
        end += offset
    }

    // CallChain[size=12] = QMutRegion.shift() <-[Propag]- QMutRegion <-[Ref]- QRegion.toMutRegion() <-[ ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun shift(length: Int) {
        this.start += length
        this.end += length
    }
}

// CallChain[size=11] = QRegion <-[Ref]- QRegion.intersect() <-[Propag]- QRegion.contains() <-[Call] ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
/**
 * [start] inclusive, [end] exclusive
 */
private open class QRegion(open val start: Int, open val end: Int) {
    // CallChain[size=10] = QRegion.toMutRegion() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween. ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun toMutRegion(): QMutRegion {
        return QMutRegion(start, end)
    }

    // CallChain[size=10] = QRegion.toRange() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.appl ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun toRange(): IntRange {
        return IntRange(start, end + 1)
    }

    // CallChain[size=10] = QRegion.length <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.applyMo ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    val length: Int
        get() = end - start

    // CallChain[size=10] = QRegion.intersect() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.ap ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun intersect(region: QRegion): QRegion? {
        val start = max(this.start, region.start)
        val end = min(this.end, region.end)

        return if (start <= end) {
            QRegion(end, start)
        } else {
            null
        }
    }

    // CallChain[size=9] = QRegion.contains() <-[Call]- QMaskBetween.applyMore() <-[Call]- QMaskBetween. ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun contains(idx: Int): Boolean {
        return idx in start until end
    }

    // CallChain[size=10] = QRegion.cut() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.applyMor ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun cut(text: String): String {
        return text.substring(start, end)
    }

    // CallChain[size=10] = QRegion.remove() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.apply ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun remove(text: String): String {
        return text.removeRange(start, end)
    }

    // CallChain[size=10] = QRegion.replace() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.appl ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun replace(text: String, replacement: String): String {
        return text.replaceRange(start, end, replacement)
    }

    // CallChain[size=10] = QRegion.mask() <-[Propag]- QRegion.contains() <-[Call]- QMaskBetween.applyMo ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun mask(text: String, maskChar: Char = '*'): String {
        return text.replaceRange(this.toRange(), maskChar.toString().repeat(end - start))
    }
}

// CallChain[size=6] = QReplacer <-[Ref]- String.qMaskAndReplace() <-[Call]- QMaskResult.replaceAndUnmask() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private class QReplacer(start: Int, end: Int, val replacement: String) : QMutRegion(start, end)

// CallChain[size=6] = QMaskResult <-[Ref]- QMask.apply() <-[Propag]- QMask.KOTLIN_STRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private class QMaskResult(val maskedStr: String, val orgText: String, val maskChar: Char) {
    // CallChain[size=4] = QMaskResult.replaceAndUnmask() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    /**
     * Apply regex to masked string.
     * Apply replacement to original text.
     */
    fun replaceAndUnmask(ptn: Regex, replacement: String, findAll: Boolean = true): String {
        return orgText.qMaskAndReplace(maskedStr, ptn, replacement, findAll)
    }

    // CallChain[size=7] = QMaskResult.applyMoreMask() <-[Call]- QMultiMask.apply() <-[Propag]- QMultiMa ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun applyMoreMask(mask: QMaskBetween): QMaskResult {
        return mask.applyMore(maskedStr, orgText)
    }

    // CallChain[size=7] = QMaskResult.toString() <-[Propag]- QMaskResult <-[Ref]- QMask.apply() <-[Prop ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun toString(): String {
        val original = orgText.qWithNewLineSurround(onlyIf = QOnlyIfStr.Multiline)
        val masked = maskedStr.replace(maskChar, '*').qWithNewLineSurround(onlyIf = QOnlyIfStr.Multiline)

        return "${QMaskResult::class.simpleName} : $original ${"->".cyan} $masked"
    }
}

// CallChain[size=4] = CharSequence.qMask() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun CharSequence.qMask(vararg mask: QMask): QMaskResult {
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

// CallChain[size=9] = String.qFindBetween() <-[Call]- QMaskBetween.applyMore() <-[Call]- QMaskBetwe ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun String.qFindBetween(
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

// CallChain[size=5] = String.qMaskAndReplace() <-[Call]- QMaskResult.replaceAndUnmask() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
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

// CallChain[size=6] = CharSequence.qMultiReplace() <-[Call]- String.qMaskAndReplace() <-[Call]- QMa ... ask() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
/**
 * currently does not support region overlap
 */
private fun CharSequence.qMultiReplace(replacers: List<QReplacer>): String {
    // TODO Use StringBuilder
    val sb = StringBuilder(this)
    var offset = 0
    for (r in replacers) {
        sb.replace(r.start + offset, r.end + offset, r.replacement)
        offset += r.replacement.length - (r.end - r.start)
    }

    return sb.toString()
}

// CallChain[size=6] = MatchResult.qResolveReplacementGroup() <-[Call]- String.qMaskAndReplace() <-[ ... ask() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun MatchResult.qResolveReplacementGroup(replacement: String, orgText: String): String {
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

// CallChain[size=7] = CharSequence.qReplace() <-[Call]- MatchResult.qResolveReplacementGroup() <-[C ... ask() <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun CharSequence.qReplace(oldValue: String, newValue: String, escapeChar: Char): String {
    return replace(Regex("""(?<!\Q$escapeChar\E)\Q$oldValue\E"""), newValue)
}

// CallChain[size=11] = QSequenceReader <-[Call]- QBetween.find() <-[Call]- String.qFindBetween() <- ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private class QSequenceReader(text: CharSequence) : QCharReader(text) {
    // CallChain[size=13] = QSequenceReader.sequenceOffset <-[Call]- QSequenceReader.offsetInSequence()  ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    private var sequenceOffset = 0

    // CallChain[size=13] = QSequenceReader.sequence <-[Call]- QSequenceReader.peekCurrentCharInSequence ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    private var sequence: CharArray? = null

    // CallChain[size=12] = QSequenceReader.startReadingSequence() <-[Call]- QSequenceReader.detectSeque ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    private fun startReadingSequence(sequence: CharArray): Boolean {
        return if (!hasNextChar(sequence.size)) {
            false
        } else {
            this.sequence = sequence
            sequenceOffset = offset
            true
        }
    }

    // CallChain[size=12] = QSequenceReader.endReadingSequence() <-[Call]- QSequenceReader.detectSequenc ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    private fun endReadingSequence(success: Boolean): Boolean {

        if (!success) {
            offset = sequenceOffset
        }

        sequenceOffset = -1

        return success
    }

    // CallChain[size=12] = QSequenceReader.hasNextCharInSequence() <-[Call]- QSequenceReader.detectSequ ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    private fun hasNextCharInSequence(): Boolean {
        return if (sequence == null) {
            false
        } else {
            (offsetInSequence() < sequence!!.size) &&
                    hasNextChar()
        }
    }

//    inline fun peekNextCharInSequence(): Char {
//        return sequence!![offset - sequenceOffset]
//    }

    // CallChain[size=12] = QSequenceReader.peekCurrentCharInSequence() <-[Call]- QSequenceReader.detect ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    private fun peekCurrentCharInSequence(): Char {
        return sequence!![offsetInSequence()]
    }

    // CallChain[size=12] = QSequenceReader.offsetInSequence() <-[Call]- QSequenceReader.detectSequence( ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    /**
     * 0 to sequence.size - 1
     */
    private fun offsetInSequence(): Int {
        return offset - sequenceOffset
    }

    // CallChain[size=11] = QSequenceReader.detectSequence() <-[Call]- QBetween.find() <-[Call]- String. ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    /**
     * If sequence is detected, move offset by the length of the sequence.
     * If no sequence is found, offset remains unchanged.
     */
    fun detectSequence(sequence: CharArray, eofAllowed: Boolean = false): Boolean {
        if (!startReadingSequence(sequence)) return false

        while (hasNextCharInSequence()) {
            val seqChar = peekCurrentCharInSequence()
            val ch = nextChar()

            if (ch != seqChar) {
                endReadingSequence(false)
                return eofAllowed && isOffsetEOF()
            }
        }

        return if (offsetInSequence() == sequence.size) {
            endReadingSequence(true)
            true
        } else {
            val success = eofAllowed && isOffsetEOF()
            endReadingSequence(success)
            success
        }
    }

    
}

// CallChain[size=12] = QCharReader <-[Call]- QSequenceReader <-[Call]- QBetween.find() <-[Call]- St ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private open class QCharReader(val text: CharSequence) {
    // CallChain[size=13] = QCharReader.offset <-[Propag]- QCharReader <-[Call]- QSequenceReader <-[Call ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    var offset = 0

    // CallChain[size=13] = QCharReader.lineNumber() <-[Propag]- QCharReader <-[Call]- QSequenceReader < ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun lineNumber(): Int {
        // Consider caret to be between the character on the offset and the character preceding it
        //
        // ex. ( [ ] indicate offsets )
        // [\n]abc\n --> lineNumber is 1 "First Line"
        // \n[\n] --> lineNumber is 2 "Second Line"

        var lineBreakCount = 0

        var tmpOffset = offset

        while (tmpOffset >= 0) {
            if (tmpOffset != offset && text[tmpOffset] == '\n') {
                lineBreakCount++
            }

            tmpOffset--
        }

        return lineBreakCount + 1
    }

    // CallChain[size=13] = QCharReader.countIndentSpaces() <-[Propag]- QCharReader <-[Call]- QSequenceR ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun countIndentSpaces(space: Char = ' '): Int {
        var count = 0

        var tmpOffset = offset

        // read backward until previous line break
        while (tmpOffset >= 0) {
            if (text[tmpOffset] == '\n') {
                tmpOffset++
                break
            }

            tmpOffset--
        }

        var ch: Char

        while (true) {
            ch = text[tmpOffset]
            if (ch == space) {
                count++
            } else if (ch == '\n') {
                break
            } else {
                continue
            }

            tmpOffset--

            if (tmpOffset == -1)
                break
        }

        return count
    }

    // CallChain[size=13] = QCharReader.hasNextChar() <-[Propag]- QCharReader <-[Call]- QSequenceReader  ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    inline fun hasNextChar(len: Int = 1): Boolean {
        return offset + len - 1 < text.length
    }

    // CallChain[size=13] = QCharReader.isOffsetEOF() <-[Propag]- QCharReader <-[Call]- QSequenceReader  ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    inline fun isOffsetEOF(): Boolean {
        return offset == text.length - 1
    }

    // CallChain[size=13] = QCharReader.isValidOffset() <-[Propag]- QCharReader <-[Call]- QSequenceReade ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    inline fun isValidOffset(): Boolean {
        return 0 <= offset && offset < text.length
    }

    // CallChain[size=13] = QCharReader.hasPreviousChar() <-[Propag]- QCharReader <-[Call]- QSequenceRea ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    inline fun hasPreviousChar(len: Int = 1): Boolean {
        return 0 < offset - len + 1
    }

    // CallChain[size=13] = QCharReader.previousChar() <-[Propag]- QCharReader <-[Call]- QSequenceReader ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    inline fun previousChar(len: Int = 1): Char {
        offset -= len
        return text[offset]
    }

    // CallChain[size=13] = QCharReader.currentChar() <-[Propag]- QCharReader <-[Call]- QSequenceReader  ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    inline fun currentChar(): Char {
        return text[offset]
    }

    // CallChain[size=13] = QCharReader.peekNextChar() <-[Propag]- QCharReader <-[Call]- QSequenceReader ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    inline fun peekNextChar(): Char {
        return text[offset]
    }

    // CallChain[size=13] = QCharReader.moveOffset() <-[Propag]- QCharReader <-[Call]- QSequenceReader < ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    inline fun moveOffset(plus: Int = 1) {
        offset += plus
    }

    // CallChain[size=13] = QCharReader.nextChar() <-[Propag]- QCharReader <-[Call]- QSequenceReader <-[ ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    /**
     * Read current offset char and add offset by 1.
     */
    inline fun nextChar(): Char {
        return text[offset++]
    }

    // CallChain[size=13] = QCharReader.nextString() <-[Propag]- QCharReader <-[Call]- QSequenceReader < ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun nextString(length: Int): String {
        val str = text.substring(offset + 1, (offset + length).coerceAtMost(text.length))
        offset += length
        return str
    }

    // CallChain[size=13] = QCharReader.previousString() <-[Propag]- QCharReader <-[Call]- QSequenceRead ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun previousString(length: Int): String {
        val str = text.substring(offset - length, offset)
        offset -= length
        return str
    }
}

// CallChain[size=10] = QBetween <-[Call]- String.qFindBetween() <-[Call]- QMaskBetween.applyMore()  ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
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

    // CallChain[size=10] = QBetween.find() <-[Call]- String.qFindBetween() <-[Call]- QMaskBetween.apply ... TRING <-[Call]- Any?.qToLogString() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun find(text: CharSequence): List<QRegion> {
        val reader = QSequenceReader(text)

        val ranges: MutableList<QRegion> = mutableListOf()

        val startChArr = startSequence.toCharArray()
        val nestStartChArr = nestStartSequence?.toCharArray()
        val endChArr = endSequence.toCharArray()

        var nNest = 0

        var startSeqOffset = -1

        while (reader.hasNextChar()) {
            val ch = reader.peekNextChar()

            if (ch == escapeChar) {
                reader.moveOffset(2)
                continue
            } else {

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
                        startSeqOffset = reader.offset
                    }
                } else if (nNest > 0 && reader.detectSequence(endChArr, allowEOFEnd)) {
                    if (nestingDepth == nNest) {
                        val endSeqOffset = reader.offset - endChArr.size // exclusive

                        ranges += if (!regionIncludeStartAndEndSequence) {
                            QRegion(startSeqOffset, endSeqOffset)
                        } else {
                            val end = min(endSeqOffset + endChArr.size, text.length)
                            QRegion(startSeqOffset - startChArr.size, end)
                        }
                    }

                    nNest--
                } else {
                    reader.moveOffset()
                }
            }
        }

        return ranges
    }
}

// CallChain[size=11] = qNow <-[Call]- qCacheItTimedThreadLocal() <-[Call]- qCacheItOneSecThreadLoca ... wItBrackets() <-[Call]- qBrackets() <-[Call]- Any?.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private val qNow: Long
    get() = System.currentTimeMillis()

// CallChain[size=5] = QTimeItResult <-[Ref]- qTimeIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
private class QTimeItResult<T>(val label: String, val time: Long, val result: T) {
    // CallChain[size=5] = QTimeItResult.toString() <-[Call]- qTimeIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
    override fun toString(): String {
        return qBrackets(label, time.qFormatDuration())
    }
}

// CallChain[size=4] = qTimeIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
@OptIn(ExperimentalContracts::class)
private inline fun <T> qTimeIt(label: String = qThisSrcLineSignature, quiet: Boolean = false, out: QOut? = QOut.CONSOLE, block: () -> T): QTimeItResult<T> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val start = System.nanoTime()

    val blockResult = block()

    val time = System.nanoTime() - start

    val result = QTimeItResult(label, time, blockResult)

    if (!quiet)
        out?.println(result.toString())

    return result
}


// ================================================================================
// endregion Src from Non-Root API Files -- Auto Generated by nyab.conf.QCompactLib.kt