// 2023. nyabkun  MIT LICENSE

@file:Suppress("NOTHING_TO_INLINE")

package nyab.util

import java.io.PrintStream
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import nyab.conf.QE
import nyab.conf.QMyMark
import nyab.conf.qSTACK_FRAME_FILTER
import nyab.match.QM

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=13] = QE.throwIt() <-[Call]- qUnreachable() <-[Call]- QFetchRule.SINGLE_LINE <-[Ca ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun QE.throwIt(msg: Any? = "", e: Throwable? = null, stackDepth: Int = 0): Nothing {
    throw QException(
        this,
        if (msg is String && msg.isEmpty()) {
            "No detailed error messages".light_gray
        } else {
            msg.qToLogString()
        },
        e,
        stackDepth = stackDepth + 1
    )
}

// CallChain[size=14] = QE.throwItFile() <-[Call]- LineNumberReader.qFetchLinesAround() <-[Call]- Pa ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun QE.throwItFile(path: Path, e: Throwable? = null, stackDepth: Int = 0): Nothing {
    throw QException(this, qBrackets("File", path.absolutePathString()), e, stackDepth = stackDepth + 1)
}

// CallChain[size=5] = QE.throwItBrackets() <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun QE.throwItBrackets(vararg keysAndValues: Any?, e: Throwable? = null, stackDepth: Int = 0): Nothing {
    throw QException(this, qBracketsCyan(*keysAndValues), e, stackDepth = stackDepth + 1)
}

// CallChain[size=12] = qUnreachable() <-[Call]- QFetchRule.SINGLE_LINE <-[Call]- QSrcCut.QSrcCut()  ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun qUnreachable(msg: Any? = ""): Nothing {
    QE.Unreachable.throwIt(msg)
}

// CallChain[size=6] = QException <-[Call]- QE.throwItBrackets() <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal class QException(
    val type: QE = QE.UnknownReason,
    msg: String = QMyMark.warn,
    e: Throwable? = null,
    val stackDepth: Int = 0,
    stackSize: Int = 20,
    stackFilter: (StackWalker.StackFrame) -> Boolean = qSTACK_FRAME_FILTER,
    private val srcCut: QSrcCut = QSrcCut.MULTILINE_NOCUT,
) : RuntimeException(msg, e) {

    // CallChain[size=7] = QException.printStackTrace() <-[Propag]- QException.QException() <-[Ref]- QE. ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun printStackTrace(s: PrintStream) {
        s.println("\n" + qToString() + "\n" + mySrcAndStack)
    }

    // CallChain[size=8] = QException.stackFrames <-[Call]- QException.getStackTrace() <-[Propag]- QExce ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val stackFrames = qStackFrames(stackDepth + 2, size = stackSize, filter = stackFilter)

    // CallChain[size=8] = QException.mySrcAndStack <-[Call]- QException.printStackTrace() <-[Propag]- Q ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val mySrcAndStack: String by lazy {
        qLogStackFrames(frames = stackFrames, style = QLogStyle.SRC_AND_STACK, srcCut = srcCut, quiet = true)
            .qColorTarget(qRe("""\sshould[a-zA-Z]+"""), QColor.LightYellow)
    }

    // CallChain[size=7] = QException.getStackTrace() <-[Propag]- QException.QException() <-[Ref]- QE.th ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun getStackTrace(): Array<StackTraceElement> {
        return stackFrames.map {
            it.toStackTraceElement()
        }.toTypedArray()
    }

    // CallChain[size=8] = QException.qToString() <-[Call]- QException.toString() <-[Propag]- QException ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun qToString(): String {
        val msg = message

        return if (!msg.isNullOrEmpty() && type.name != message) {
            "${type.name.qCamelCaseToSpaceSeparated().yellow} ${":".light_gray}${
            msg.qWithSpacePrefix(onlyIf = QOnlyIfStr.SingleLine).qWithNewLinePrefix(onlyIf = QOnlyIfStr.Multiline)
            }".trim()
        } else {
            type.name.yellow
        }
    }

    // CallChain[size=7] = QException.toString() <-[Propag]- QException.QException() <-[Ref]- QE.throwIt ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun toString(): String {
        return qToString()
//         used by @Test
//        return type.name.yellow
    }
}

// CallChain[size=6] = Boolean.qaTrue() <-[Call]- String.qWithMinAndMaxLength() <-[Call]- qSeparatorWithLabel() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
internal fun Boolean.qaTrue(exceptionType: QE = QE.ShouldBeTrue, msg: Any? = "") {
    if (!this) {
        exceptionType.throwIt(stackDepth = 1, msg = msg)
    }
}

// CallChain[size=13] = T.qaNotNull() <-[Call]- qSrcFileAtFrame() <-[Call]- qSrcFileLinesAtFrame() < ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal inline fun <reified T : Any> T?.qaNotNull(exceptionType: QE = QE.ShouldNotBeNull, msg: Any? = "${T::class.simpleName} should not be null"): T {
    if (this != null) {
        return this
    } else {
        exceptionType.throwIt(stackDepth = 1, msg = msg)
    }
}

// CallChain[size=5] = Int.qaNotZero() <-[Call]- CharSequence.qMask() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun Int?.qaNotZero(exceptionType: QE = QE.ShouldNotBeZero, msg: Any? = ""): Int {
    if (this == null) {
        QE.ShouldNotBeNull.throwIt(stackDepth = 1, msg = msg)
    } else if (this == 0) {
        exceptionType.throwIt(stackDepth = 1, msg = msg)
    } else {
        return this
    }
}