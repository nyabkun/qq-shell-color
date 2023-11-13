// 2023. nyabkun  MIT LICENSE

package nyab.util

import java.lang.StackWalker.StackFrame
import java.nio.charset.Charset
import java.nio.file.Path
import nyab.conf.QE
import nyab.conf.QMyLog
import nyab.conf.QMyMark
import nyab.conf.QMyPath

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=13] = qARROW <-[Call]- qArrow() <-[Call]- QLogStyle.qLogArrow() <-[Call]- QLogStyl ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal val qARROW = "===>".light_cyan

// CallChain[size=4] = qIsDebugging <-[Call]- qOkToTest() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
// https://stackoverflow.com/a/28754689/5570400
internal val qIsDebugging by lazy {
    java.lang.management.ManagementFactory.getRuntimeMXBean().inputArguments.toString().indexOf("jdwp") >= 0
}

// CallChain[size=4] = qIsTesting <-[Call]- qOkToTest() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
// https://stackoverflow.com/a/12717377/5570400
internal val qIsTesting by lazy {
    qStackFrames(size = Int.MAX_VALUE).any {
        it.methodName.equals("qTest") ||
                it.className.startsWith("org.junit.") || it.className.startsWith("org.testng.")
    }
}

// CallChain[size=7] = QSrcCut <-[Ref]- QException.QException() <-[Ref]- QE.throwItBrackets() <-[Cal ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal class QSrcCut(
    val fetchRule: QFetchRule = QFetchRule.SINGLE_LINE,
    val cut: (srcLines: String) -> String,
) {
    companion object {
        // CallChain[size=11] = QSrcCut.CUT_PARAM_qLog <-[Call]- QSrcCut.SINGLE_qLog_PARAM <-[Call]- qLogSta ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        private val CUT_PARAM_qLog = { mySrc: String ->
            mySrc.replaceFirst("""(?s)^\s*(\S.+)\.qLog[a-zA-Z]{0,10}.*$""".re, "$1")
        }

        // CallChain[size=10] = QSrcCut.SINGLE_qLog_PARAM <-[Call]- qLogStackFrames() <-[Call]- QException.m ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        val SINGLE_qLog_PARAM = QSrcCut(QFetchRule.SINGLE_LINE, CUT_PARAM_qLog)
        // CallChain[size=7] = QSrcCut.MULTILINE_NOCUT <-[Call]- QException.QException() <-[Ref]- QE.throwIt ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        val MULTILINE_NOCUT = QSrcCut(QFetchRule.SMART_FETCH) { it }
        // CallChain[size=4] = QSrcCut.MULTILINE_INFIX_NOCUT <-[Call]- qThrowIt() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        val MULTILINE_INFIX_NOCUT = QSrcCut(QFetchRule.SMART_FETCH_INFIX) { it }
        // CallChain[size=11] = QSrcCut.NOCUT_JUST_SINGLE_LINE <-[Call]- qMySrcLinesAtFrame() <-[Call]- qLog ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        val NOCUT_JUST_SINGLE_LINE = QSrcCut(QFetchRule.SINGLE_LINE) { it }
        
    }
}

// CallChain[size=10] = QLogStyle <-[Ref]- QLogStyle.SRC_AND_STACK <-[Call]- QException.mySrcAndStac ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal class QLogStyle(
    val stackSize: Int,
    val out: QOut = QMyLog.out,
    val start: String = "----".cyan + "\n",
    val end: String = "\n\n",
    val stackReverseOrder: Boolean = false,
    val template: (msg: String, mySrc: String, now: Long, stackTrace: String) -> String,
) {
    @Suppress("UNUSED_PARAMETER")
    companion object {
        // CallChain[size=10] = QLogStyle.String.clarifySrcRegion() <-[Call]- QLogStyle.SRC_AND_STACK <-[Cal ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        fun String.clarifySrcRegion(onlyIf: QOnlyIfStr = QOnlyIfStr.Multiline): String {
            if (!onlyIf.matches(this))
                return this

            return """${"SRC START ―――――――――――".qColor(QColor.Cyan)}
${this.trim()}
${"SRC END   ―――――――――――".qColor(QColor.Cyan)}"""
        }

        // CallChain[size=11] = QLogStyle.qLogArrow() <-[Call]- QLogStyle.SRC_AND_MSG <-[Call]- qLogStackFra ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
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

        // CallChain[size=9] = QLogStyle.SRC_AND_STACK <-[Call]- QException.mySrcAndStack <-[Call]- QExcepti ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        val SRC_AND_STACK: QLogStyle
            get() = QLogStyle(1) { _, mySrc, _, stackTrace ->
                """
${mySrc.clarifySrcRegion(QOnlyIfStr.Always)}
$stackTrace""".trim()
            }

        // CallChain[size=10] = QLogStyle.SRC_AND_MSG <-[Call]- qLogStackFrames() <-[Call]- QException.mySrc ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        val SRC_AND_MSG: QLogStyle
            get() = QLogStyle(0) { msg, mySrc, _, _ ->
                """
${qLogArrow(mySrc, msg)}
""".trim()
            }

        // CallChain[size=3] = QLogStyle.MSG_AND_STACK <-[Call]- qTest() <-[Call]- main()[Root]
        val MSG_AND_STACK: QLogStyle
            get() = QLogStyle(1, start = "") { msg, _, _, stackTrace ->
                """
$msg
$stackTrace
""".trim()
            }

        // CallChain[size=10] = QLogStyle.S <-[Call]- qLogStackFrames() <-[Call]- QException.mySrcAndStack < ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        val S: QLogStyle
            get() = QLogStyle(1) { msg, mySrc, _, stackTrace ->
                """
${qLogArrow(mySrc, msg)}
$stackTrace
""".trim()
            }

        
    }
}

// CallChain[size=10] = qMySrcLinesAtFrame() <-[Call]- qLogStackFrames() <-[Call]- QException.mySrcA ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun qMySrcLinesAtFrame(
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
        "${QMyMark.warn} Couldn't cut src lines : ${
            qBrackets(
                "FileName",
                frame.fileName,
                "LineNo",
                frame.lineNumber,
                "SrcRoots",
                srcRoots
            )
        }"
    }
}

// CallChain[size=9] = qLogStackFrames() <-[Call]- QException.mySrcAndStack <-[Call]- QException.pri ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun qLogStackFrames(
    frames: List<StackFrame>,
    msg: Any? = "",
    style: QLogStyle = QLogStyle.S,
    srcRoots: List<Path> = QMyPath.src_root,
    srcCharset: Charset = Charsets.UTF_8,
    srcCut: QSrcCut = QSrcCut.SINGLE_qLog_PARAM,
    quiet: Boolean = false,
    noColor: Boolean = false,
): String {

    val sty = if( QMyLog.no_stacktrace_mode ) {
        QLogStyle.SRC_AND_MSG
    } else {
        style
    }

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

    val stackTrace = if (sty.stackReverseOrder) {
        frames.reversed().joinToString("\n") { it.toString() }
    } else {
        frames.joinToString("\n") { it.toString() }
    }

    val output = sty.template(
        msg.qToLogString(), mySrc, qNow, stackTrace
    )

    val text = sty.start + output + sty.end

    val finalTxt = if (noColor) text.noStyle else text

    if (!quiet)
        sty.out.print(finalTxt)

    return if (noColor) output.noStyle else output
}

// CallChain[size=11] = qSrcFileLinesAtFrame() <-[Call]- qMySrcLinesAtFrame() <-[Call]- qLogStackFra ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun qSrcFileLinesAtFrame(
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

// CallChain[size=12] = qArrow() <-[Call]- QLogStyle.qLogArrow() <-[Call]- QLogStyle.SRC_AND_MSG <-[ ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun qArrow(key: Any?, value: Any?): String {
    val keyStr = key.qToLogString()
        .qWithNewLinePrefix(onlyIf = QOnlyIfStr.Multiline)
        .qWithNewLineSuffix(onlyIf = QOnlyIfStr.Always)

    val valStr = value.qToLogString().qWithNewLineSurround(onlyIf = QOnlyIfStr.Multiline)
        .qWithSpacePrefix(numSpace = 2, onlyIf = QOnlyIfStr.SingleLine)

    return "$keyStr$qARROW$valStr"
}

// CallChain[size=3] = qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun qBrackets(vararg keysAndValues: Any?): String {
    return qBracketsColored(fg = null, *keysAndValues)
}

// CallChain[size=5] = qBracketsBlue() <-[Call]- QTestResult.printIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
internal fun qBracketsBlue(vararg keysAndValues: Any?): String {
    return qBracketsColored(QColor.Blue, *keysAndValues)
}

// CallChain[size=6] = qBracketsCyan() <-[Call]- QE.throwItBrackets() <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun qBracketsCyan(vararg keysAndValues: Any?): String {
    return qBracketsColored(QColor.Cyan, *keysAndValues)
}

// CallChain[size=5] = QBracketKV <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private class QBracketKV(val key: String, val value: String, val fg: QColor?) {
    // CallChain[size=5] = QBracketKV.toString() <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun toString(): String {
        val keyStr = if (fg != null) "[$key]".qColor(fg) else "[$key]"

        val valueStr = value.qWithNewLinePrefix(onlyIf = QOnlyIfStr.MultilineOrVeryLongLine)

        return if (valueStr.qStartsWithNewLine()) {
            keyStr + valueStr
        } else {
            "$keyStr  $valueStr"
        }
    }

    companion object {
        
    }
}

// CallChain[size=4] = qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun qBracketsColored(fg: QColor? = null, vararg keysAndValues: Any?): String {
    if (keysAndValues.size % 2 != 0) {
        QE.ShouldBeEvenNumber.throwItBrackets("KeysAndValues.size", keysAndValues.size)
    }

    return keysAndValues.asSequence().withIndex().chunked(2) { (key, value) ->
        QBracketKV(key.value.toString(), value.value.qToLogString(), fg).toString()
    }.toList().qJoinStringNicely()
}