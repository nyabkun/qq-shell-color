// 2023. nyabkun  MIT LICENSE

package nyab.util

import org.intellij.lang.annotations.Language

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=12] = RO <-[Ref]- qRe() <-[Call]- @receiver:Language("RegExp") String.re <-[Call]- ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal typealias RO = RegexOption

// CallChain[size=11] = qRe() <-[Call]- @receiver:Language("RegExp") String.re <-[Call]- String.qCam ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal fun qRe(@Language("RegExp") regex: String, vararg opts: RO): Regex {
    return qCacheItOneSecThreadLocal(regex + opts.contentToString()) {
        Regex(regex, setOf(*opts))
    }
}

// CallChain[size=10] = @receiver:Language("RegExp") String.re <-[Call]- String.qCamelCaseToSpaceSep ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
// https://youtrack.jetbrains.com/issue/KTIJ-5643
internal val @receiver:Language("RegExp") String.re: Regex
    get() = qRe(this)