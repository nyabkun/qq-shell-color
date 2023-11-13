// 2023. nyabkun  MIT LICENSE

package nyab.match

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.extensionReceiverParameter
import nyab.util.QFlagEnum
import nyab.util.QFlagMut

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=7] = qAnd() <-[Call]- QMFunc.and() <-[Call]- qToStringRegistry <-[Call]- Any.qToString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun qAnd(vararg matches: QMFunc): QMFunc = QMatchFuncAnd(*matches)

// CallChain[size=6] = QMFunc.and() <-[Call]- qToStringRegistry <-[Call]- Any.qToString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal infix fun QMFunc.and(match: QMFunc): QMFunc {
    return if (this is QMatchFuncAnd) {
        QMatchFuncAnd(*matchList, match)
    } else {
        qAnd(this, match)
    }
}

// CallChain[size=8] = QMatchFuncAny <-[Call]- QMFunc.isAny() <-[Propag]- QMFunc.IncludeExtensionsIn ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private object QMatchFuncAny : QMFuncA() {
    // CallChain[size=9] = QMatchFuncAny.matches() <-[Propag]- QMatchFuncAny <-[Call]- QMFunc.isAny() <- ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun matches(value: KFunction<*>): Boolean {
        return true
    }
}

// CallChain[size=8] = QMatchFuncNone <-[Call]- QMFunc.isNone() <-[Propag]- QMFunc.IncludeExtensions ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private object QMatchFuncNone : QMFuncA() {
    // CallChain[size=9] = QMatchFuncNone.matches() <-[Propag]- QMatchFuncNone <-[Call]- QMFunc.isNone() ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun matches(value: KFunction<*>): Boolean {
        return false
    }
}

// CallChain[size=7] = QMatchFuncDeclaredOnly <-[Call]- QMFunc.DeclaredOnly <-[Call]- qToStringRegis ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private object QMatchFuncDeclaredOnly : QMFuncA() {
    // CallChain[size=8] = QMatchFuncDeclaredOnly.declaredOnly <-[Propag]- QMatchFuncDeclaredOnly <-[Cal ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override val declaredOnly = true

    // CallChain[size=8] = QMatchFuncDeclaredOnly.matches() <-[Propag]- QMatchFuncDeclaredOnly <-[Call]- ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun matches(value: KFunction<*>): Boolean {
        return true
    }
}

// CallChain[size=7] = QMatchFuncIncludeExtensionsInClass <-[Call]- QMFunc.IncludeExtensionsInClass  ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private object QMatchFuncIncludeExtensionsInClass : QMFuncA() {
    // CallChain[size=8] = QMatchFuncIncludeExtensionsInClass.includeExtensionsInClass <-[Propag]- QMatc ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override val includeExtensionsInClass = true

    // CallChain[size=8] = QMatchFuncIncludeExtensionsInClass.matches() <-[Propag]- QMatchFuncIncludeExt ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun matches(value: KFunction<*>): Boolean {
        return true
    }
}

// CallChain[size=7] = QMatchFuncAnd <-[Ref]- QMFunc.and() <-[Call]- qToStringRegistry <-[Call]- Any.qToString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private class QMatchFuncAnd(vararg match: QMFunc) : QMFuncA() {
    // CallChain[size=7] = QMatchFuncAnd.matchList <-[Call]- QMFunc.and() <-[Call]- qToStringRegistry <- ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val matchList = match

    // CallChain[size=8] = QMatchFuncAnd.declaredOnly <-[Propag]- QMatchFuncAnd.matchList <-[Call]- QMFu ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override val declaredOnly = matchList.any { it.declaredOnly }

    // CallChain[size=8] = QMatchFuncAnd.includeExtensionsInClass <-[Propag]- QMatchFuncAnd.matchList <- ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override val includeExtensionsInClass: Boolean = matchList.any { it.includeExtensionsInClass }

    // CallChain[size=8] = QMatchFuncAnd.matches() <-[Propag]- QMatchFuncAnd.matchList <-[Call]- QMFunc. ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun matches(value: KFunction<*>): Boolean {
        return matchList.all { it.matches(value) }
    }
}

// CallChain[size=9] = QMFuncA <-[Call]- QMatchFuncNone <-[Call]- QMFunc.isNone() <-[Propag]- QMFunc ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private abstract class QMFuncA : QMFunc {
    // CallChain[size=10] = QMFuncA.declaredOnly <-[Propag]- QMFuncA <-[Call]- QMatchFuncNone <-[Call]-  ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override val declaredOnly: Boolean = false
    // CallChain[size=10] = QMFuncA.includeExtensionsInClass <-[Propag]- QMFuncA <-[Call]- QMatchFuncNon ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override val includeExtensionsInClass: Boolean = false
}

// CallChain[size=7] = QMFunc <-[Ref]- QMFunc.IncludeExtensionsInClass <-[Call]- qToStringRegistry < ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal interface QMFunc {
    // CallChain[size=7] = QMFunc.declaredOnly <-[Propag]- QMFunc.IncludeExtensionsInClass <-[Call]- qTo ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val declaredOnly: Boolean

    // CallChain[size=7] = QMFunc.includeExtensionsInClass <-[Propag]- QMFunc.IncludeExtensionsInClass < ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val includeExtensionsInClass: Boolean

    // CallChain[size=7] = QMFunc.matches() <-[Propag]- QMFunc.IncludeExtensionsInClass <-[Call]- qToStr ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun matches(value: KFunction<*>): Boolean

    // CallChain[size=7] = QMFunc.isAny() <-[Propag]- QMFunc.IncludeExtensionsInClass <-[Call]- qToStrin ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun isAny(): Boolean = this == QMatchFuncAny

    // CallChain[size=7] = QMFunc.isNone() <-[Propag]- QMFunc.IncludeExtensionsInClass <-[Call]- qToStri ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun isNone(): Boolean = this == QMatchFuncNone

    companion object {
        // CallChain[size=6] = QMFunc.DeclaredOnly <-[Call]- qToStringRegistry <-[Call]- Any.qToString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        val DeclaredOnly: QMFunc = QMatchFuncDeclaredOnly

        // CallChain[size=6] = QMFunc.IncludeExtensionsInClass <-[Call]- qToStringRegistry <-[Call]- Any.qToString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        // TODO OnlyExtensionsInClass
        val IncludeExtensionsInClass: QMFunc = QMatchFuncIncludeExtensionsInClass

        

        // TODO vararg, nullability, param names, type parameter
        // TODO handle createType() more carefully

        // CallChain[size=6] = QMFunc.nameExact() <-[Call]- qToStringRegistry <-[Call]- Any.qToString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        fun nameExact(text: String, ignoreCase: Boolean = false): QMFunc {
            return QMatchFuncName(QM.exact(text, ignoreCase = ignoreCase))
        }

        
    }
}

// CallChain[size=7] = QMatchFuncName <-[Call]- QMFunc.nameExact() <-[Call]- qToStringRegistry <-[Ca ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private class QMatchFuncName(val nameMatcher: QM) : QMFuncA() {
    // CallChain[size=8] = QMatchFuncName.matches() <-[Propag]- QMatchFuncName <-[Call]- QMFunc.nameExac ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun matches(value: KFunction<*>): Boolean {
        return nameMatcher.matches(value.name)
    }

    // CallChain[size=8] = QMatchFuncName.toString() <-[Propag]- QMatchFuncName <-[Call]- QMFunc.nameExa ... oString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun toString(): String {
        return this::class.simpleName + ":" + nameMatcher.toString()
    }
}