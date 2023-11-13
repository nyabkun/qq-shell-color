// 2023. nyabkun  MIT LICENSE

package nyab.util

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=6] = QLoopPos <-[Ref]- List<String>.qJoinStringNicely() <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal enum class QLoopPos {
    // CallChain[size=6] = QLoopPos.First <-[Call]- List<String>.qJoinStringNicely() <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    First,
    // CallChain[size=6] = QLoopPos.Middle <-[Call]- List<String>.qJoinStringNicely() <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    Middle,
    // CallChain[size=6] = QLoopPos.Last <-[Call]- List<String>.qJoinStringNicely() <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    Last,
    // CallChain[size=6] = QLoopPos.OnlyOneItem <-[Call]- List<String>.qJoinStringNicely() <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    OnlyOneItem;

    
}

// CallChain[size=6] = Collection<T>.qMap() <-[Call]- List<String>.qJoinStringNicely() <-[Call]- qBracketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal inline fun <T : Any, U : Any?> Collection<T>.qMap(
    crossinline block: (item: T, idx: Int, pos: QLoopPos, previous: T?, next: T?) -> U
): MutableList<U> {
    val list = mutableListOf<U>()

    var prePre: T? = null
    var pre: T? = null
    for ((i, next) in withIndex()) {
        if (i == 0) {
            pre = next
            continue
        }

        // non-last item
        list += block(
            pre!!, i - 1,
            if (i == 1) {
                QLoopPos.First
            } else {
                QLoopPos.Middle
            }, prePre, next
        )

        prePre = pre
        pre = next
    }

    if (isNotEmpty()) {
        // last item
        list += block(
            pre!!,
            size - 1,
            if (size == 1) {
                QLoopPos.OnlyOneItem
            } else {
                QLoopPos.Last
            },
            prePre,
            null
        )
    }

    return list
}