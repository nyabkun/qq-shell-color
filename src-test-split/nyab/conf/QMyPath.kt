// 2023. nyabkun  MIT LICENSE

package nyab.conf

import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.exists
import nyab.match.QM
import nyab.util.QFType
import nyab.util.path
import nyab.util.qListByMatch

// qq-shell-color is a self-contained single-file library created by nyabkun.
// This is a split-file version of the library, this file is not self-contained.

// CallChain[size=10] = QMyPath <-[Ref]- qLogStackFrames() <-[Call]- QException.mySrcAndStack <-[Cal ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
internal object QMyPath {
    // -- dirs

    // CallChain[size=11] = QMyPath.src <-[Call]- QMyPath.src_root <-[Call]- qLogStackFrames() <-[Call]- ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val src = "src".path
    // CallChain[size=11] = QMyPath.src_java <-[Call]- QMyPath.src_root <-[Call]- qLogStackFrames() <-[C ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val src_java = "src-java".path
    // CallChain[size=11] = QMyPath.src_build <-[Call]- QMyPath.src_root <-[Call]- qLogStackFrames() <-[ ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val src_build = "src-build".path
    // CallChain[size=11] = QMyPath.src_experiment <-[Call]- QMyPath.src_root <-[Call]- qLogStackFrames( ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val src_experiment = "src-experiment".path
    // CallChain[size=11] = QMyPath.src_plugin <-[Call]- QMyPath.src_root <-[Call]- qLogStackFrames() <- ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val src_plugin = "src-plugin".path
    // CallChain[size=11] = QMyPath.src_config <-[Call]- QMyPath.src_root <-[Call]- qLogStackFrames() <- ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val src_config = "src-config".path
    // CallChain[size=11] = QMyPath.src_test <-[Call]- QMyPath.src_root <-[Call]- qLogStackFrames() <-[C ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val src_test = "src-test".path
    // --- dir list
    // CallChain[size=10] = QMyPath.src_root <-[Call]- qLogStackFrames() <-[Call]- QException.mySrcAndSt ... racketsColored() <-[Call]- qBrackets() <-[Call]- Any.shouldBe() <-[Call]- QColorTest.nest2()[Root]
    val src_root: List<Path> by lazy {
        val base = listOf(
            src,
            src_test,
            src_experiment,
            src_config,
            src_plugin,
            src_java,
            src_build,
            "src".path,
            "test".path,
            "src/main/kotlin".path,
            "src/test/kotlin".path,
            "src/main/java".path,
            "src/test/java".path,
//            ".".path,
        ).filter { it.exists() }

        val search = Paths.get(".").qListByMatch(type = QFType.Dir, nameMatch = QM.startsWith("src-"), maxDepth = 1)

        (base + search).distinct()
    }

    // -- files
}