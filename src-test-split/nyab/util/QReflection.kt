// 2023. nyabkun  MIT LICENSE

@file:Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE", "FunctionName")

package nyab.util

import java.lang.StackWalker.StackFrame
import java.lang.reflect.Method
import java.nio.charset.Charset
import java.nio.file.Path
import java.util.*
import java.util.stream.Stream
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.declaredMemberExtensionFunctions
import kotlin.reflect.full.isSuperclassOf
import kotlin.reflect.full.memberExtensionFunctions
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.isAccessible
import kotlin.streams.asSequence
import nyab.conf.QE
import nyab.conf.QMyPath
import nyab.conf.qSTACK_FRAME_FILTER
import nyab.match.QM
import nyab.match.QMFunc
import nyab.match.and

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=4] = Method.qName() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
internal fun Method.qName(withParenthesis: Boolean = false): String {
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

// CallChain[size=6] = KClass<*>.qFunctions() <-[Call]- qToStringRegistry <-[Call]- Any.qToString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun KClass<*>.qFunctions(matcher: QMFunc = QMFunc.DeclaredOnly and QMFunc.IncludeExtensionsInClass): List<KFunction<*>> {
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

// CallChain[size=19] = KClass<E>.qEnumValues() <-[Call]- QFlagMut.allEnumValues <-[Propag]- QFlagMu ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun <E : Enum<E>> KClass<E>.qEnumValues(): Array<E> {
    return java.enumConstants as Array<E>
}

// CallChain[size=5] = qThisSrcLineSignature <-[Call]- qTimeIt() <-[Call]- qTestMethods() <-[Call]- qTest() <-[Call]- main()[Root]
internal val qThisSrcLineSignature: String
    get() = qCallerSrcLineSignature()

// CallChain[size=12] = qSrcFileAtFrame() <-[Call]- qSrcFileLinesAtFrame() <-[Call]- qMySrcLinesAtFr ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun qSrcFileAtFrame(frame: StackFrame, srcRoots: List<Path> = QMyPath.src_root, pkgDirHint: String? = null): Path =
    qCacheItOneSec(
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
internal fun qCallerFileName(stackDepth: Int = 0): String {
    return qStackFrame(stackDepth + 2).fileName
}

// CallChain[size=3] = qCallerSrcLineSignature() <-[Call]- String.qColorRandom() <-[Call]- QColorTest.randomColor()[Root]
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

// CallChain[size=9] = qStackFrames() <-[Call]- QException.stackFrames <-[Call]- QException.getStack ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal inline fun qStackFrames(
    stackDepth: Int = 0,
    size: Int = 1,
    noinline filter: (StackFrame) -> Boolean = qSTACK_FRAME_FILTER,
): List<StackFrame> {
    return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk { s: Stream<StackFrame> ->
        s.asSequence().filter(filter).drop(stackDepth).take(size).toList()
    }
}

// CallChain[size=12] = qStackFrame() <-[Call]- qSrcFileLinesAtFrame() <-[Call]- qMySrcLinesAtFrame( ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal inline fun qStackFrame(
    stackDepth: Int = 0,
    noinline filter: (StackFrame) -> Boolean = qSTACK_FRAME_FILTER,
): StackFrame {
    return qStackFrames(stackDepth, 1, filter)[0]
}

// CallChain[size=3] = qStackFrameEntryMethod() <-[Call]- qTest() <-[Call]- main()[Root]
internal fun qStackFrameEntryMethod(filter: (StackFrame) -> Boolean): StackFrame {
    return qStackFrames(0, Int.MAX_VALUE)
        .filter(filter)
        .findLast {
            it.lineNumber > 0
        }.qaNotNull()
}

// CallChain[size=7] = KType.qToClass() <-[Call]- KType.qIsSuperclassOf() <-[Call]- qToStringRegistr ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun KType.qToClass(): KClass<*>? {
    // KType MutableList<String> classifier returns List. I don't know why??

    return if (this.classifier != null && this.classifier is KClass<*>) {
        this.classifier as KClass<*>
    } else {
        null
    }
}

// CallChain[size=6] = KType.qIsSuperclassOf() <-[Call]- qToStringRegistry <-[Call]- Any.qToString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun KType.qIsSuperclassOf(cls: KClass<*>): Boolean {
    return try {
        val thisClass = qToClass()

        if (thisClass?.qualifiedName == "kotlin.Array" && cls.qualifiedName == "kotlin.Array") { // TODO 多分 toStringRegistry 関連で作った
            true
        } else {
            thisClass?.isSuperclassOf(cls) ?: false
        }
    } catch (e: Throwable) {
        // Exception in thread "main" kotlin.reflect.jvm.internal.KotlinReflectionInternalError: Unresolved class: ~
        false
    }
}