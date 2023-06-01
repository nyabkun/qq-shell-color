/*
 * Copyright 2023. nyabkun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package nyab.match

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.extensionReceiverParameter
import nyab.util.QFlagEnum

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=7] = qAnd() <-[Call]- QMFunc.and() <-[Call]- qToStringRegistry <-[Call]- Any.qToString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private fun qAnd(vararg matches: QMFunc): QMFunc = QMatchFuncAnd(*matches)

// CallChain[size=6] = QMFunc.and() <-[Call]- qToStringRegistry <-[Call]- Any.qToString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
internal infix fun QMFunc.and(match: QMFunc): QMFunc {
    return if (this is QMatchFuncAnd) {
        QMatchFuncAnd(*matchList, match)
    } else {
        qAnd(this, match)
    }
}

// CallChain[size=8] = QMatchFuncAny <-[Call]- QMFunc.isAny() <-[Propag]- QMFunc.IncludeExtensionsIn ... tring() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private object QMatchFuncAny : QMFuncA() {
    // CallChain[size=9] = QMatchFuncAny.matches() <-[Propag]- QMatchFuncAny <-[Call]- QMFunc.isAny() <- ... tring() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun matches(value: KFunction<*>): Boolean {
        return true
    }
}

// CallChain[size=8] = QMatchFuncNone <-[Call]- QMFunc.isNone() <-[Propag]- QMFunc.IncludeExtensions ... tring() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private object QMatchFuncNone : QMFuncA() {
    // CallChain[size=9] = QMatchFuncNone.matches() <-[Propag]- QMatchFuncNone <-[Call]- QMFunc.isNone() ... tring() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun matches(value: KFunction<*>): Boolean {
        return false
    }
}

// CallChain[size=7] = QMatchFuncDeclaredOnly <-[Call]- QMFunc.DeclaredOnly <-[Call]- qToStringRegis ... tring() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private object QMatchFuncDeclaredOnly : QMFuncA() {
    // CallChain[size=8] = QMatchFuncDeclaredOnly.declaredOnly <-[Propag]- QMatchFuncDeclaredOnly <-[Cal ... tring() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override val declaredOnly = true

    // CallChain[size=8] = QMatchFuncDeclaredOnly.matches() <-[Propag]- QMatchFuncDeclaredOnly <-[Call]- ... tring() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun matches(value: KFunction<*>): Boolean {
        return true
    }
}

// CallChain[size=7] = QMatchFuncIncludeExtensionsInClass <-[Call]- QMFunc.IncludeExtensionsInClass  ... tring() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private object QMatchFuncIncludeExtensionsInClass : QMFuncA() {
    // CallChain[size=8] = QMatchFuncIncludeExtensionsInClass.includeExtensionsInClass <-[Propag]- QMatc ... tring() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override val includeExtensionsInClass = true

    // CallChain[size=8] = QMatchFuncIncludeExtensionsInClass.matches() <-[Propag]- QMatchFuncIncludeExt ... tring() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun matches(value: KFunction<*>): Boolean {
        return true
    }
}

// CallChain[size=7] = QMatchFuncAnd <-[Ref]- QMFunc.and() <-[Call]- qToStringRegistry <-[Call]- Any ... tring() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private class QMatchFuncAnd(vararg match: QMFunc) : QMFuncA() {
    // CallChain[size=7] = QMatchFuncAnd.matchList <-[Call]- QMFunc.and() <-[Call]- qToStringRegistry <- ... tring() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    val matchList = match

    // CallChain[size=8] = QMatchFuncAnd.declaredOnly <-[Propag]- QMatchFuncAnd.matchList <-[Call]- QMFu ... tring() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override val declaredOnly = matchList.any { it.declaredOnly }

    // CallChain[size=8] = QMatchFuncAnd.includeExtensionsInClass <-[Propag]- QMatchFuncAnd.matchList <- ... tring() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override val includeExtensionsInClass: Boolean = matchList.any { it.includeExtensionsInClass }

    // CallChain[size=8] = QMatchFuncAnd.matches() <-[Propag]- QMatchFuncAnd.matchList <-[Call]- QMFunc. ... tring() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun matches(value: KFunction<*>): Boolean {
        return matchList.all { it.matches(value) }
    }
}

// CallChain[size=9] = QMFuncA <-[Call]- QMatchFuncNone <-[Call]- QMFunc.isNone() <-[Propag]- QMFunc ... tring() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private abstract class QMFuncA : QMFunc {
    // CallChain[size=10] = QMFuncA.declaredOnly <-[Propag]- QMFuncA <-[Call]- QMatchFuncNone <-[Call]-  ... tring() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override val declaredOnly: Boolean = false
    // CallChain[size=10] = QMFuncA.includeExtensionsInClass <-[Propag]- QMFuncA <-[Call]- QMatchFuncNon ... tring() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override val includeExtensionsInClass: Boolean = false
}

// CallChain[size=7] = QMFunc <-[Ref]- QMFunc.IncludeExtensionsInClass <-[Call]- qToStringRegistry < ... tring() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
internal interface QMFunc {
    // CallChain[size=7] = QMFunc.declaredOnly <-[Propag]- QMFunc.IncludeExtensionsInClass <-[Call]- qTo ... tring() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    val declaredOnly: Boolean

    // CallChain[size=7] = QMFunc.includeExtensionsInClass <-[Propag]- QMFunc.IncludeExtensionsInClass < ... tring() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    val includeExtensionsInClass: Boolean

    // CallChain[size=7] = QMFunc.matches() <-[Propag]- QMFunc.IncludeExtensionsInClass <-[Call]- qToStr ... tring() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun matches(value: KFunction<*>): Boolean

    // CallChain[size=7] = QMFunc.isAny() <-[Propag]- QMFunc.IncludeExtensionsInClass <-[Call]- qToStrin ... tring() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun isAny(): Boolean = this == QMatchFuncAny

    // CallChain[size=7] = QMFunc.isNone() <-[Propag]- QMFunc.IncludeExtensionsInClass <-[Call]- qToStri ... tring() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun isNone(): Boolean = this == QMatchFuncNone

    companion object {
        // CallChain[size=6] = QMFunc.DeclaredOnly <-[Call]- qToStringRegistry <-[Call]- Any.qToString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
        val DeclaredOnly: QMFunc = QMatchFuncDeclaredOnly

        // CallChain[size=6] = QMFunc.IncludeExtensionsInClass <-[Call]- qToStringRegistry <-[Call]- Any.qToString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
        // TODO OnlyExtensionsInClass
        val IncludeExtensionsInClass: QMFunc = QMatchFuncIncludeExtensionsInClass

        

        // TODO vararg, nullability, param names, type parameter
        // TODO handle createType() more carefully

        // CallChain[size=6] = QMFunc.nameExact() <-[Call]- qToStringRegistry <-[Call]- Any.qToString() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
        fun nameExact(text: String, ignoreCase: Boolean = false): QMFunc {
            return QMatchFuncName(QM.exact(text, ignoreCase = ignoreCase))
        }

        
    }
}

// CallChain[size=7] = QMatchFuncName <-[Call]- QMFunc.nameExact() <-[Call]- qToStringRegistry <-[Ca ... tring() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
private class QMatchFuncName(val nameMatcher: QM) : QMFuncA() {
    // CallChain[size=8] = QMatchFuncName.matches() <-[Propag]- QMatchFuncName <-[Call]- QMFunc.nameExac ... tring() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun matches(value: KFunction<*>): Boolean {
        return nameMatcher.matches(value.name)
    }

    // CallChain[size=8] = QMatchFuncName.toString() <-[Propag]- QMatchFuncName <-[Call]- QMFunc.nameExa ... tring() <-[Call]- Any.qToLogString() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun toString(): String {
        return this::class.simpleName + ":" + nameMatcher.toString()
    }
}