// 2023. nyabkun  MIT LICENSE

@file:Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE", "FunctionName")

package nyab.util

import java.lang.StackWalker.StackFrame
import java.util.*
import java.util.stream.Stream
import kotlin.streams.asSequence
import nyab.conf.qSTACK_FRAME_FILTER

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=2] = qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
internal fun qCallerSrcLineSignature(stackDepth: Int = 0): String {
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

// CallChain[size=4] = qStackFrames() <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
internal inline fun qStackFrames(
    stackDepth: Int = 0,
    size: Int = 1,
    noinline filter: (StackFrame) -> Boolean = qSTACK_FRAME_FILTER,
): List<StackFrame> {
    return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk { s: Stream<StackFrame> ->
        s.asSequence().filter(filter).drop(stackDepth).take(size).toList()
    }
}

// CallChain[size=3] = qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
internal inline fun qStackFrame(
    stackDepth: Int = 0,
    noinline filter: (StackFrame) -> Boolean = qSTACK_FRAME_FILTER,
): StackFrame {
    return qStackFrames(stackDepth, 1, filter)[0]
}