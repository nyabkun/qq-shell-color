// 2023. nyabkun  MIT LICENSE

@file:Suppress("SpellCheckingInspection", "UNCHECKED_CAST")

package nyab.util

import kotlin.reflect.KClass

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=14] = QFlag <-[Ref]- Path.qReader() <-[Call]- Path.qFetchLinesAround() <-[Call]- q ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
/**
 * Only Enum or QFlagMut can implement this interface.
 */
internal sealed interface QFlag<T> where T : QFlag<T>, T : Enum<T> {
    // CallChain[size=16] = QFlag.bits <-[Propag]- QFlag.flagTrueValues() <-[Call]- QFlag<QOpenOpt>.toOp ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val bits: Int

//    fun contains(flags: T): Boolean {
//        return (bits and flags.bits) == flags.bits
//    }

    // CallChain[size=16] = QFlag.notContains() <-[Propag]- QFlag.flagTrueValues() <-[Call]- QFlag<QOpen ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun notContains(flags: QFlag<T>): Boolean {
        return !contains(flags)
    }

    // CallChain[size=16] = QFlag.contains() <-[Propag]- QFlag.flagTrueValues() <-[Call]- QFlag<QOpenOpt ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun contains(flags: QFlag<T>): Boolean {
        return (bits and flags.bits) == flags.bits
    }

    // CallChain[size=16] = QFlag.containsAny() <-[Propag]- QFlag.flagTrueValues() <-[Call]- QFlag<QOpen ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun containsAny(vararg flags: QFlag<T>): Boolean {
        return flags.any { flag -> (bits and flag.bits) == flag.bits }
    }

    // CallChain[size=16] = QFlag.notContainsAny() <-[Propag]- QFlag.flagTrueValues() <-[Call]- QFlag<QO ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun notContainsAny(vararg flags: QFlag<T>): Boolean {
        return !containsAny(*flags)
    }

    // CallChain[size=16] = QFlag.containsAll() <-[Propag]- QFlag.flagTrueValues() <-[Call]- QFlag<QOpen ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun containsAll(vararg flags: QFlag<T>): Boolean {
        return flags.all { flag -> (bits and flag.bits) == flag.bits }
    }

//    fun containsAll(flags: Array<QFlag<T>>): Boolean {
//        return flags.all { flag -> (bits and flag.bits) == flag.bits }
//    }

//    fun matches(flags: T): Boolean {
//        return bits == flags.bits
//    }

    // CallChain[size=16] = QFlag.matches() <-[Propag]- QFlag.flagTrueValues() <-[Call]- QFlag<QOpenOpt> ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun matches(flags: QFlag<T>): Boolean {
        return bits == flags.bits
    }

    // CallChain[size=16] = QFlag.matchesAll() <-[Propag]- QFlag.flagTrueValues() <-[Call]- QFlag<QOpenO ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun matchesAll(vararg flags: QFlag<T>): Boolean {
        return flags.all { flag -> bits == flag.bits }
    }

//    fun matchesAll(flags: Array<QFlag<T>>): Boolean {
//        return flags.all { flag -> bits == flag.bits }
//    }

    // CallChain[size=15] = QFlag.flagTrueValues() <-[Call]- QFlag<QOpenOpt>.toOptEnums() <-[Call]- Path ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun flagTrueValues(): List<T>

    // CallChain[size=16] = QFlag.str() <-[Propag]- QFlag.flagTrueValues() <-[Call]- QFlag<QOpenOpt>.toO ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun str(): String {
        return flagTrueValues().joinToString(", ") { it.name }
    }

    // CallChain[size=16] = QFlag.copy() <-[Propag]- QFlag.flagTrueValues() <-[Call]- QFlag<QOpenOpt>.to ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun copy(): QFlagMut<T>


    // CallChain[size=16] = QFlag.isNone() <-[Propag]- QFlag.flagTrueValues() <-[Call]- QFlag<QOpenOpt>. ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun isNone(): Boolean {
        return bits == 0
    }

    // CallChain[size=16] = QFlag.isNotNone() <-[Propag]- QFlag.flagTrueValues() <-[Call]- QFlag<QOpenOp ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun isNotNone(): Boolean {
        return bits != 0
    }

    companion object {
        // https://discuss.kotlinlang.org/t/reified-generics-on-class-level/16711/2
        // But, can't make constructor private ...

        // CallChain[size=14] = QFlag.none() <-[Call]- Path.qReader() <-[Call]- Path.qFetchLinesAround() <-[ ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
        inline fun <reified T> none(): QFlagMut<T> where T : QFlag<T>, T : Enum<T> {
            return QFlagMut<T>(T::class, 0)
        }
    }
}

// CallChain[size=15] = QFlagEnum <-[Ref]- QOpenOpt <-[Ref]- Path.qReader() <-[Call]- Path.qFetchLin ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal interface QFlagEnum<T> : QFlag<T> where T : QFlag<T>, T : Enum<T> {
    // CallChain[size=16] = QFlagEnum.bits <-[Propag]- QFlagEnum <-[Ref]- QOpenOpt <-[Ref]- Path.qReader ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override val bits: Int
        get() = 1 shl (this as T).ordinal

    // CallChain[size=16] = QFlagEnum.flagTrueValues() <-[Propag]- QFlagEnum <-[Ref]- QOpenOpt <-[Ref]-  ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun flagTrueValues(): List<T> = listOf(this) as List<T>

    // CallChain[size=16] = QFlagEnum.copy() <-[Propag]- QFlagEnum <-[Ref]- QOpenOpt <-[Ref]- Path.qRead ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun copy(): QFlagMut<T> {
        return QFlagMut(this::class as KClass<T>, bits)
    }
}

// CallChain[size=17] = QFlagMut <-[Ref]- QFlag.copy() <-[Propag]- QFlag.flagTrueValues() <-[Call]-  ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
/**
 * Mutable bit flag
 */
internal open class QFlagMut<T>(val enumClass: KClass<T>, override var bits: Int) : QFlag<T> where T : QFlag<T>, T : Enum<T> {
    // CallChain[size=18] = QFlagMut.allEnumValues <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Propag] ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val allEnumValues: Array<T> by lazy { enumClass.qEnumValues() }

    // CallChain[size=18] = QFlagMut.QFlagMut() <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Propag]- Q ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    constructor(enumClass: KClass<T>, vararg flags: T) : this(
        enumClass,
        bits = if (flags.isEmpty()) 0 else flags.map { it.bits }.reduce { a, b -> a or b }
    )

    // CallChain[size=18] = QFlagMut.add() <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Propag]- QFlag. ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun add(flag: T) {
        add(flag.bits)
    }

    // CallChain[size=18] = QFlagMut.add() <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Propag]- QFlag. ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun add(flagBits: Int) {
        bits = bits or flagBits
    }

//    fun addAll(flags: Array<T>) {
//        for (fl in flags) {
//            add(fl.bits)
//        }
//    }

    // CallChain[size=18] = QFlagMut.addAll() <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Propag]- QFl ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun addAll(vararg flags: T) {
        for(fl in flags) {
            add(fl.bits)
        }
    }

    // CallChain[size=18] = QFlagMut.addAll() <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Propag]- QFl ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun addAll(flags: Iterable<T>) {
        for (fl in flags) {
            add(fl.bits)
        }
    }

    // CallChain[size=18] = QFlagMut.allTrue() <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Propag]- QF ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun allTrue() {
        this.addAll(*allEnumValues)
    }

    // CallChain[size=18] = QFlagMut.allFalse() <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Propag]- Q ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun allFalse() {
        this.bits = 0
    }

    // CallChain[size=18] = QFlagMut.removeAll() <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Propag]-  ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun removeAll(vararg flags: T) {
        for(fl in flags) {
            remove(fl.bits)
        }
    }

//    fun removeAll(flags: Array<T>) {
//        for(fl in flags) {
//            remove(fl.bits)
//        }
//    }

    // CallChain[size=18] = QFlagMut.removeAll() <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Propag]-  ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun removeAll(flags: Iterable<T>) {
        for(fl in flags) {
            remove(fl.bits)
        }
    }

    // CallChain[size=18] = QFlagMut.remove() <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Propag]- QFl ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun remove(flag: T) {
        remove(flag.bits)
    }

    // CallChain[size=18] = QFlagMut.remove() <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Propag]- QFl ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun remove(flagBits: Int) {
        bits = bits and flagBits.inv()
    }

    // CallChain[size=18] = QFlagMut.eq() <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Propag]- QFlag.f ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    infix fun eq(other: QFlag<T>) = this.bits == other.bits

    // CallChain[size=18] = QFlagMut.print() <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Propag]- QFla ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun print(out: QOut = QOut.CONSOLE, end: String = "\n") {
        out.print(str() + end)
    }

    // CallChain[size=18] = QFlagMut.flagTrueValues() <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Prop ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun flagTrueValues(): List<T> =
        allEnumValues.filter { contains(it) }

    // CallChain[size=18] = QFlagMut.copy() <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Propag]- QFlag ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun copy(): QFlagMut<T> {
        return QFlagMut(enumClass, bits)
    }

    // CallChain[size=18] = QFlagMut.toString() <-[Propag]- QFlagMut <-[Ref]- QFlag.copy() <-[Propag]- Q ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    override fun toString(): String {
        return flagTrueValues().joinToString(", ")
    }
}