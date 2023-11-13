<!--- version = v2023-11-13-bc02 --->

# 🐕 qq-shell-color

**qq-shell-color** is a Kotlin library that can color your console output.

## How to use
- Just copy and paste Single-File version [QColor.kt](src-single/QColor.kt) into your project.
- Or you can use Jar version. See [Maven Dependency Section](#jar-version-maven-dependency).
- Feel free to fork or copy to your own codebase 😍

## Example

### output
<p align="center">
    
</p>
<p align="center">
    <img src="img/result.png" width="587" alt="result.png">
</p>

### code example

Full Source : [QColorExample.kt](src-example/QColorExample.kt)

```kotlin
fun colorful() {
    println("c".yellow + "o".blue + "l".red + "o".purple + "u".green + "r".cyan + "f".yellow + "u".blue + "l".red)
}

fun regex() {
    val txt = """val color = "you can use regex to color targeted text"""".qColorTarget(
        ptn = """val(?!\S)""".toRegex(),
        fg = QColor.Purple
    ).qColorTarget(
        ptn = """".*?"""".toRegex(),
        fg = QColor.Green
    )

    println(txt)
}

fun background() {
    println("background".qColor(fg = QColor.Red, bg = QColor.Blue))
}

fun decorate() {
    println("underlined".underline)
    println("italic".italic)
    println("bold".bold)
}

fun nest() {
    println("ne${"stab".blue.underline}le".yellow)
}

fun multiline() {
    val txt = """
        multiline
        multiline
    """.trimIndent().blue.underline

    println(txt)
}
```

Please see [QColorTest.kt](src-test-split/nyab/util/QColorTest.kt) for more code examples.
Single-File version [src-test-single/QColorTest.kt](src-test-single/QColorTest.kt) is a self-contained source code that includes a runnable main function.
You can easily copy and paste it into your codebase.        

## Public API

- [`String.qColor()`](src-split/nyab/util/QColor.kt#L23-L32) *Extension Function*
- [`String.qDeco()`](src-split/nyab/util/QColor.kt#L33-L42) *Extension Function*
- [`String.qColorAndDecoDebug()`](src-split/nyab/util/QColor.kt#L168-L180) *Extension Function*
- [`String.qColorTarget()`](src-split/nyab/util/QColor.kt#L181-L184) *Extension Function*
- [`String.qDecoTarget()`](src-split/nyab/util/QColor.kt#L185-L188) *Extension Function*
- [`String.qColorRandom()`](src-split/nyab/util/QColor.kt#L189-L190) *Extension Function*
- [`String.bold`](src-split/nyab/util/QColor.kt#L191-L193) *Extension Property*
- [`String.italic`](src-split/nyab/util/QColor.kt#L194-L196) *Extension Property*
- [`String.underline`](src-split/nyab/util/QColor.kt#L197-L199) *Extension Property*
- [`String.black`](src-split/nyab/util/QColor.kt#L200-L202) *Extension Property*
- [`String.red`](src-split/nyab/util/QColor.kt#L203-L205) *Extension Property*
- [`String.green`](src-split/nyab/util/QColor.kt#L206-L208) *Extension Property*
- [`String.yellow`](src-split/nyab/util/QColor.kt#L209-L211) *Extension Property*
- [`String.blue`](src-split/nyab/util/QColor.kt#L212-L214) *Extension Property*
- [`String.purple`](src-split/nyab/util/QColor.kt#L215-L217) *Extension Property*
- [`String.cyan`](src-split/nyab/util/QColor.kt#L218-L220) *Extension Property*
- [`String.light_gray`](src-split/nyab/util/QColor.kt#L221-L223) *Extension Property*
- [`String.dark_gray`](src-split/nyab/util/QColor.kt#L224-L226) *Extension Property*
- [`String.light_red`](src-split/nyab/util/QColor.kt#L227-L229) *Extension Property*
- [`String.light_green`](src-split/nyab/util/QColor.kt#L230-L232) *Extension Property*
- [`String.light_yellow`](src-split/nyab/util/QColor.kt#L233-L235) *Extension Property*
- [`String.light_blue`](src-split/nyab/util/QColor.kt#L236-L238) *Extension Property*
- [`String.light_magenta`](src-split/nyab/util/QColor.kt#L239-L241) *Extension Property*
- [`String.light_cyan`](src-split/nyab/util/QColor.kt#L242-L244) *Extension Property*
- [`String.white`](src-split/nyab/util/QColor.kt#L245-L247) *Extension Property*
- [`String.noStyle`](src-split/nyab/util/QColor.kt#L248-L252) *Extension Property*
- [`QDeco`](src-split/nyab/util/QColor.kt#L66-L85) *Enum Class*
- [`QColor`](src-split/nyab/util/QColor.kt#L86-L167) *Enum Class*

## Single-File version Dependency

If you copy & paste [QColor.kt](src-single/QColor.kt),
refer to [build.gradle.kts](build.gradle.kts) to directly check project settings.



```kotlin
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.20")
    testImplementation("org.jetbrains.kotlin:kotlin-reflect:1.8.20")
}
```

## Jar version Maven Dependency

If you prefer a jar library,
you can use [jitpack.io](https://jitpack.io/#nyabkun/qq-shell-color) repository.

### build.gradle ( Groovy )
```groovy
repositories {
    ...
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.nyabkun:qq-shell-color:v2023-11-13'
}
```

### build.gradle.kts ( Kotlin )
```kotlin
repositories {
    ...
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.nyabkun:qq-shell-color:v2023-11-13")
}
```

### pom.xml
```xml
<repositories>
    ...
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    ...
    <dependency>
        <groupId>com.github.nyabkun</groupId>
        <artifactId>qq-shell-color</artifactId>
        <version>v2023-11-13</version>
    </dependency>
</dependencies>
```

## How did I create this library

- This library was created using [qq-compact-lib](https://github.com/nyabkun/qq-compact-lib) to generates a compact, self-contained library.
- **qq-compact-lib** is a Kotlin library that can extract code elements from your codebase and make a compact library.
- It utilizes [PSI](https://plugins.jetbrains.com/docs/intellij/psi.html) to resolve function calls and class references.
- The original repository is currently being organized, and I'm gradually extracting and publishing smaller libraries.

## References

- [kolor](https://github.com/ziggy42/kolor)            