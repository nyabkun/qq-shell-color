// 2023. nyabkun  MIT LICENSE

package nyab.conf

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=4] = qSTACK_FRAME_FILTER <-[Call]- qStackFrame() <-[Call]- qCallerSrcLineSignature() <-[Call]- String.qColorRandom()[Root]
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