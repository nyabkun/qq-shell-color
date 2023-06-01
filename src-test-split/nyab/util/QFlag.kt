/*
 * Copyright 2023. nyabkun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

@file:Suppress("SpellCheckingInspection", "UNCHECKED_CAST")

package nyab.util

import kotlin.reflect.KClass

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=13] = QFlag <-[Ref]- Path.qReader() <-[Call]- Path.qFetchLinesAround() <-[Call]- q ... owItBrackets() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
/**
 * Only Enum or QFlag can implement this interface.
 */
internal sealed interface QFlag<T> where T : QFlag<T>, T : Enum<T> {
    // CallChain[size=15] = QFlag.bits <-[Propag]- QFlag.toEnumValues() <-[Call]- QFlag<QOpenOpt>.toOptE ... owItBrackets() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    val bits: Int

    // CallChain[size=15] = QFlag.contains() <-[Propag]- QFlag.toEnumValues() <-[Call]- QFlag<QOpenOpt>. ... owItBrackets() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun contains(flags: QFlag<T>): Boolean {
        return (bits and flags.bits) == flags.bits
    }

    // CallChain[size=14] = QFlag.toEnumValues() <-[Call]- QFlag<QOpenOpt>.toOptEnums() <-[Call]- Path.q ... owItBrackets() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun toEnumValues(): List<T>

    // CallChain[size=15] = QFlag.str() <-[Propag]- QFlag.toEnumValues() <-[Call]- QFlag<QOpenOpt>.toOpt ... owItBrackets() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    fun str(): String {
        return toEnumValues().joinToString(", ") { it.name }
    }

    companion object {
        // https://discuss.kotlinlang.org/t/reified-generics-on-class-level/16711/2
        // But, can't make constructor private ...

        // CallChain[size=13] = QFlag.none() <-[Call]- Path.qReader() <-[Call]- Path.qFetchLinesAround() <-[ ... owItBrackets() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
        inline fun <reified T>
        none(): QFlag<T> where T : QFlag<T>, T : Enum<T> {
            return QFlagSet<T>(T::class, 0)
        }
    }
}

// CallChain[size=14] = QFlagEnum <-[Ref]- QOpenOpt <-[Ref]- Path.qReader() <-[Call]- Path.qFetchLin ... owItBrackets() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
internal interface QFlagEnum<T> : QFlag<T> where T : QFlag<T>, T : Enum<T> {
    // CallChain[size=15] = QFlagEnum.bits <-[Propag]- QFlagEnum <-[Ref]- QOpenOpt <-[Ref]- Path.qReader ... owItBrackets() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override val bits: Int
        get() = 1 shl (this as T).ordinal
    // CallChain[size=15] = QFlagEnum.toEnumValues() <-[Propag]- QFlagEnum <-[Ref]- QOpenOpt <-[Ref]- Pa ... owItBrackets() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun toEnumValues(): List<T> = listOf(this) as List<T>
}

// CallChain[size=14] = QFlagSet <-[Call]- QFlag.none() <-[Call]- Path.qReader() <-[Call]- Path.qFet ... owItBrackets() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
/**
 * Mutable bit flag
 */
internal class QFlagSet<T>(val enumClass: KClass<T>, override var bits: Int) : QFlag<T> where T : QFlag<T>, T : Enum<T> {
    // CallChain[size=16] = QFlagSet.enumValues <-[Call]- QFlagSet.toEnumValues() <-[Propag]- QFlagSet < ... owItBrackets() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    val enumValues: Array<T> by lazy { enumClass.qEnumValues() }

    // CallChain[size=15] = QFlagSet.toEnumValues() <-[Propag]- QFlagSet <-[Call]- QFlag.none() <-[Call] ... owItBrackets() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QShColorTest.nest2()[Root]
    override fun toEnumValues(): List<T> =
        enumValues.filter { contains(it) }
}