// 2023. nyabkun  MIT LICENSE

@file:Suppress("FunctionName", "UNCHECKED_CAST")

package nyab.util

import java.lang.ref.WeakReference
import java.nio.file.Path
import kotlin.reflect.KClass

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=21] = QExProps <-[Call]- Any.qSetExProp() <-[Call]- N.children <-[Call]- N.depthFi ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
/**
 * Minimal Version of IdentityWeakHashMap.
 */
private object QExProps {
    // CallChain[size=22] = QExProps.map <-[Call]- QExProps.put() <-[Call]- Any.qSetExProp() <-[Call]- N ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val map: MutableMap<WeakKey, HashMap<String, Any?>> = HashMap()

    // CallChain[size=22] = QExProps.removeGarbageCollectedEntries() <-[Call]- QExProps.put() <-[Call]-  ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun removeGarbageCollectedEntries() {
        qMinIntervalRun("QExProps.removeGarbageCollectedEntries", 5000) {
            // This takes large time
            map.keys.removeIf { it.get() == null }
        }
    }

    // CallChain[size=21] = QExProps.get() <-[Call]- Any.qSetExProp() <-[Call]- N.children <-[Call]- N.d ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun get(key: Any): HashMap<String, Any?>? {
        removeGarbageCollectedEntries()

        return map[WeakKey(key)]
    }

    // CallChain[size=21] = QExProps.put() <-[Call]- Any.qSetExProp() <-[Call]- N.children <-[Call]- N.d ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    fun put(key: Any, value: HashMap<String, Any?>) {
        removeGarbageCollectedEntries()

        map[WeakKey(key)] = value
    }

    // CallChain[size=22] = QExProps.WeakKey <-[Call]- QExProps.put() <-[Call]- Any.qSetExProp() <-[Call ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    class WeakKey(key: Any) : WeakReference<Any>(key) {
        val hash = System.identityHashCode(key)

        override fun equals(other: Any?): Boolean {
            // If equals, hashCode() must be the same value
            // If both reference objects are null, then the keys are not equals
            val thisValue = this.get() ?: return false

            return thisValue === (other as WeakKey).get()
        }

        override fun hashCode() = hash
    }
}

// CallChain[size=20] = Any.qSetExProp() <-[Call]- N.children <-[Call]- N.depthFirstRecursive() <-[C ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun Any.qSetExProp(key: String, value: Any? = true) = synchronized(QExProps) {
    var props = QExProps.get(this)
    if (props == null) {
        props = HashMap(2)
        QExProps.put(this, props)
    }
    props[key] = value
}

// CallChain[size=21] = Any.qGetExPropOrDefault() <-[Call]- Any.qGetExPropOrNull() <-[Call]- N.child ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun <T> Any.qGetExPropOrDefault(key: String, default: T): T = synchronized(QExProps) {
    val props = QExProps.get(this) ?: return default

    return props.getOrDefault(key, default) as T
}

// CallChain[size=20] = Any.qGetExPropOrNull() <-[Call]- N.children <-[Call]- N.depthFirstRecursive( ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
// TODO remove this function, qGetExProp can already returns null
internal fun Any.qGetExPropOrNull(key: String): Any? = synchronized(QExProps) {
    return qGetExPropOrDefault(key, null)
}