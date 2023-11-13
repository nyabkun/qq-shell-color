// 2023. nyabkun  MIT LICENSE

package nyab.util

import java.nio.file.Path
import java.nio.file.Paths
import nyab.conf.QMyPath

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=11] = String.path <-[Call]- QMyPath.src_root <-[Call]- qLogStackFrames() <-[Call]- ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal val String.path: Path
    get() = Paths.get(this.trim()).toAbsolutePath().normalize()