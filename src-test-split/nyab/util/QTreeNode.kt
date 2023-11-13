// 2023. nyabkun  MIT LICENSE

@file:Suppress("UNCHECKED_CAST")

package nyab.util

import kotlin.reflect.jvm.isAccessible
import nyab.conf.QE

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=20] = QLazyTreeNode <-[Ref]- N.children <-[Call]- N.depthFirstRecursive() <-[Call] ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
/**
 * Represents a node in a tree data structure that can fill its child nodes.
 *
 * When the [fillTree] function of the root node is called, it invokes the [fillChildNodes] function
 * of descendant nodes in a breadth-first order to fill the child nodes.
 */
internal interface QLazyTreeNode<V : Any?> : IQTreeNode<V> {
    // CallChain[size=20] = QLazyTreeNode.hasChildNodesToFill() <-[Call]- N.children <-[Call]- N.depthFi ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    /**
     * Checks if the node has child nodes that need to be filled.
     */
    fun hasChildNodesToFill(): Boolean

    // CallChain[size=20] = QLazyTreeNode.fillChildNodes() <-[Call]- N.children <-[Call]- N.depthFirstRe ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    /**
     * Fills and returns the child nodes of the current node.
     */
    fun fillChildNodes(): List<QLazyTreeNode<V>>
}

// CallChain[size=18] = IQTreeNode <-[Ref]- N.walkDescendants() <-[Call]- Path.qSeq() <-[Call]- Path ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
/**
 * Represents a node in a tree data structure. [value] can be of any type, but within a single tree,
 * the type of [value] needs to be consistent.
 */
internal interface IQTreeNode<V : Any?> {
    // CallChain[size=19] = IQTreeNode.value <-[Propag]- IQTreeNode <-[Ref]- N.walkDescendants() <-[Call ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val value: V

    // CallChain[size=19] = IQTreeNode.toTreeNodeString() <-[Propag]- IQTreeNode <-[Ref]- N.walkDescenda ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun toTreeNodeString(): String {
        return value.toString()
    }
}

// CallChain[size=20] = N.parent <-[Call]- N.depthFrom() <-[Call]- N.depthFirst() <-[Call]- N.walkDe ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal var <N : IQTreeNode<*>> N.parent: N?
    get() =
        this.qGetExPropOrNull("#parent") as N?
    set(value) {
        val oldParent = this.parent
        if (oldParent != null) {
            val oldChildren = oldParent.children
            (oldChildren as MutableList<N>).remove(this)
        }

        if (value != null && !value.children.contains(this)) {
            (value.children as MutableList<N>).add(this)
        }

        this.qSetExProp("#parent", value)
    }

// CallChain[size=19] = N.children <-[Call]- N.depthFirstRecursive() <-[Call]- N.walkDescendants() < ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
/**
 * Obtain the child nodes of this node.
 */
internal val <N : IQTreeNode<*>> N.children: List<N>
    get() {
        var result = this.qGetExPropOrNull("#children")

        if (result == null) {
            val newChildren = if (this is QLazyTreeNode<*> && this.hasChildNodesToFill()) {
                val filled = this.fillChildNodes().toMutableList() as List<N>
                filled.forEach {
                    it.qSetExProp("#parent", this)
                }
                filled
            } else {
                mutableListOf<N>()
            }

            this.qSetExProp("#children", newChildren)

            result = newChildren
        }

        return result as List<N>
    }

// CallChain[size=19] = N.depthFrom() <-[Call]- N.depthFirst() <-[Call]- N.walkDescendants() <-[Call ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun <N : IQTreeNode<*>> N.depthFrom(root: IQTreeNode<*>): Int {
    var count = 1
    var parent = this.parent

    while (parent !== root) {
        count++

        if (parent == null) {
            return -1
        }

        parent = parent.parent
    }

    return count
//    return ancestorsList().size
//    return ancestorsSeq().count()
}

// CallChain[size=17] = N.walkDescendants() <-[Call]- Path.qSeq() <-[Call]- Path.qList() <-[Call]- P ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
/**
 * It traverses the descendant nodes in the order specified by the [algorithm].
 * The return value is of type [Sequence].
 */
internal fun <N : IQTreeNode<*>> N.walkDescendants(
    algorithm: QWalkAlgo = QWalkAlgo.BreadthFirst,
    maxDepth: Int = Int.MAX_VALUE,
    includeSelf: Boolean = true,
    filter: (N) -> Boolean = { true }
): Sequence<N> {
    return when (algorithm) {
        QWalkAlgo.BreadthFirst -> breadthFirst(maxDepth = maxDepth, includeSelf = includeSelf, filter = filter)
        QWalkAlgo.DepthFirst -> depthFirst(maxDepth = maxDepth, includeSelf = includeSelf, filter = filter)
        QWalkAlgo.DepthFirstRecursive -> depthFirstRecursive(
            maxDepth = maxDepth,
            includeSelf = includeSelf,
            filter = filter
        )
    }
}

// CallChain[size=19] = N.mark() <-[Call]- N.depthFirstRecursive() <-[Call]- N.walkDescendants() <-[ ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun <N : IQTreeNode<*>> N.mark(marked: HashSet<N>) {
    marked += this
}

// CallChain[size=19] = N.isMarked() <-[Call]- N.depthFirstRecursive() <-[Call]- N.walkDescendants() ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun <N : IQTreeNode<*>> N.isMarked(marked: HashSet<N>): Boolean =
    marked.contains(this)

// CallChain[size=18] = N.breadthFirst() <-[Call]- N.walkDescendants() <-[Call]- Path.qSeq() <-[Call ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun <N : IQTreeNode<*>> N.breadthFirst(
    maxDepth: Int = Int.MAX_VALUE,
    includeSelf: Boolean = true,
    filter: (N) -> Boolean = { true }
): Sequence<N> = sequence {
    val check: HashSet<N> = HashSet()

    var depth = 0

    var curNodes = mutableListOf<N>()
    curNodes += this@breadthFirst
    var nextDepthNodes = mutableListOf<N>()

    while (curNodes.isNotEmpty()) {
        for (node in curNodes) {
            if (node.isMarked(check)) {
                // already visited
                continue
            }

            val okToAdd = filter(node)

            if (includeSelf || node !== this@breadthFirst) {
                if (okToAdd)
                    yield(node)
            }

            node.mark(check)

            if (okToAdd)
                nextDepthNodes += node.children
        }

        curNodes = nextDepthNodes
        nextDepthNodes = mutableListOf()

        depth++

        if (depth > maxDepth) {
            break
        }
    }
}

// CallChain[size=18] = N.depthFirstRecursive() <-[Call]- N.walkDescendants() <-[Call]- Path.qSeq()  ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun <N : IQTreeNode<*>> N.depthFirstRecursive(
    maxDepth: Int = Int.MAX_VALUE,
    includeSelf: Boolean = true,
    filter: (N) -> Boolean = { true },
    check: HashSet<N> = HashSet(),
    curDepth: Int = 0
): Sequence<N> = sequence {
    if (curDepth > maxDepth) {
        return@sequence
    }

    val thisNode = this@depthFirstRecursive

    thisNode.mark(check)

    if (includeSelf && filter(thisNode)) {
        yield(thisNode)
    }

    for (node in thisNode.children) {
        if (!filter(node))
            continue

        if (node.isMarked(check)) {
            // already visited
            continue
        }

        node.mark(check)

        // recursive call
        yieldAll(node.depthFirstRecursive(maxDepth, true, filter, check, curDepth + 1))
    }
}

// CallChain[size=18] = N.depthFirst() <-[Call]- N.walkDescendants() <-[Call]- Path.qSeq() <-[Call]- ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
private fun <N : IQTreeNode<*>> N.depthFirst(
    maxDepth: Int = Int.MAX_VALUE,
    raiseExceptionIfCyclic: Boolean = false,
    includeSelf: Boolean = true,
    filter: (N) -> Boolean = { true }
): Sequence<N> = sequence {
    val check: HashSet<N> = HashSet()

    val stack = mutableListOf<N>()

    stack += this@depthFirst

    this@depthFirst.mark(check)

    while (stack.isNotEmpty()) {
        val node = stack.removeAt(stack.size - 1)

        val okToAdd = filter(node)

        if (includeSelf || node !== this@depthFirst) {
            if (okToAdd)
                yield(node)
        }

        for (n in node.children.reversed()) {
            if (n.isMarked(check)) {
                if (raiseExceptionIfCyclic) {
                    QE.CycleDetected.throwIt(n)
                }

                // already visited
                continue
            }

            if (n.depthFrom(this@depthFirst) <= maxDepth) {
                stack += n
                node.mark(check)
            }
        }
    }
}

// CallChain[size=17] = QWalkAlgo <-[Ref]- Path.qSeq() <-[Call]- Path.qList() <-[Call]- Path.qFind() ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal enum class QWalkAlgo {
    // CallChain[size=17] = QWalkAlgo.BreadthFirst <-[Call]- Path.qSeq() <-[Call]- Path.qList() <-[Call] ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    BreadthFirst,
    // CallChain[size=18] = QWalkAlgo.DepthFirst <-[Propag]- QWalkAlgo.BreadthFirst <-[Call]- Path.qSeq( ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    DepthFirst,
    // CallChain[size=18] = QWalkAlgo.DepthFirstRecursive <-[Propag]- QWalkAlgo.BreadthFirst <-[Call]- P ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    DepthFirstRecursive
}