<!--- version = v2023-06-01-bc02 --->

# 🐕 qq-shell-color

**qq-shell-color** is a Kotlin library that can color your console output.

## How to use
- Just copy and paste Single-File version [QShColor.kt](src-single/QShColor.kt) into your project.
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

Full Source : [QShColorExample.kt](src-example/QShColorExample.kt)

```kotlin
fun colorful() {
    println("c".yellow + "o".blue + "l".red + "o".purple + "u".green + "r".cyan + "f".yellow + "u".blue + "l".red)
}

fun regex() {
    val txt = """val color = "you can use regex to color targeted text"""".qColorTarget(
        ptn = """val(?!\S)""".toRegex(),
        fg = QShColor.Purple
    ).qColorTarget(
        ptn = """".*?"""".toRegex(),
        fg = QShColor.Green
    )

    println(txt)
}

fun background() {
    println("background".qColor(fg = QShColor.Red, bg = QShColor.Blue))
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

Please see [QShColorTest.kt](src-test-split/nyab/util/QShColorTest.kt) for more code examples.
Single-File version [src-test-single/QShColorTest.kt](src-test-single/QShColorTest.kt) is a self-contained source code that includes a runnable main function.
You can easily copy and paste it into your codebase.        

## Public API

- `String.qColor()` *ExtensionFunction* [📄 jump to code](src-split/nyab/util/QShColor.kt#L38-L47)
- `String.qDeco()` *ExtensionFunction* [📄 jump to code](src-split/nyab/util/QShColor.kt#L49-L58)
- `String.qColorAndDecoDebug()` *ExtensionFunction* [📄 jump to code](src-split/nyab/util/QShColor.kt#L174-L188)
- `String.qColorTarget()` *ExtensionFunction* [📄 jump to code](src-split/nyab/util/QShColor.kt#L190-L193)
- `String.qDecoTarget()` *ExtensionFunction* [📄 jump to code](src-split/nyab/util/QShColor.kt#L195-L198)
- `String.qColorRandom()` *ExtensionFunction* [📄 jump to code](src-split/nyab/util/QShColor.kt#L200-L201)
- `String.bold` *ExtensionProperty* [📄 jump to code](src-split/nyab/util/QShColor.kt#L203-L205)
- `String.italic` *ExtensionProperty* [📄 jump to code](src-split/nyab/util/QShColor.kt#L207-L209)
- `String.underline` *ExtensionProperty* [📄 jump to code](src-split/nyab/util/QShColor.kt#L211-L213)
- `String.black` *ExtensionProperty* [📄 jump to code](src-split/nyab/util/QShColor.kt#L215-L217)
- `String.red` *ExtensionProperty* [📄 jump to code](src-split/nyab/util/QShColor.kt#L219-L221)
- `String.green` *ExtensionProperty* [📄 jump to code](src-split/nyab/util/QShColor.kt#L223-L225)
- `String.yellow` *ExtensionProperty* [📄 jump to code](src-split/nyab/util/QShColor.kt#L227-L229)
- `String.blue` *ExtensionProperty* [📄 jump to code](src-split/nyab/util/QShColor.kt#L231-L233)
- `String.purple` *ExtensionProperty* [📄 jump to code](src-split/nyab/util/QShColor.kt#L235-L237)
- `String.cyan` *ExtensionProperty* [📄 jump to code](src-split/nyab/util/QShColor.kt#L239-L241)
- `String.light_gray` *ExtensionProperty* [📄 jump to code](src-split/nyab/util/QShColor.kt#L243-L245)
- `String.dark_gray` *ExtensionProperty* [📄 jump to code](src-split/nyab/util/QShColor.kt#L247-L249)
- `String.light_red` *ExtensionProperty* [📄 jump to code](src-split/nyab/util/QShColor.kt#L251-L253)
- `String.light_green` *ExtensionProperty* [📄 jump to code](src-split/nyab/util/QShColor.kt#L255-L257)
- `String.light_yellow` *ExtensionProperty* [📄 jump to code](src-split/nyab/util/QShColor.kt#L259-L261)
- `String.light_blue` *ExtensionProperty* [📄 jump to code](src-split/nyab/util/QShColor.kt#L263-L265)
- `String.light_magenta` *ExtensionProperty* [📄 jump to code](src-split/nyab/util/QShColor.kt#L267-L269)
- `String.light_cyan` *ExtensionProperty* [📄 jump to code](src-split/nyab/util/QShColor.kt#L271-L273)
- `String.white` *ExtensionProperty* [📄 jump to code](src-split/nyab/util/QShColor.kt#L275-L277)
- `String.noStyle` *ExtensionProperty* [📄 jump to code](src-split/nyab/util/QShColor.kt#L279-L283)
- `QShDeco` *EnumClass* [📄 jump to code](src-split/nyab/util/QShColor.kt#L88-L109)
- `QShColor` *EnumClass* [📄 jump to code](src-split/nyab/util/QShColor.kt#L111-L172)

## Single-File version Dependency

If you copy & paste [QShColor.kt](src-single/QShColor.kt),
Refer to [build.gradle.kts](build.gradle.kts) to directly check project settings.



```kotlin
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.20")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.20")
}
```

## Jar version Maven Dependency

If you prefer a jar library,
add [jitpack.io](https://jitpack.io/#nyabkun/qq-shell-color) repository to your build settings.

### build.gradle ( Groovy )
```groovy
repositories {
    ...
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.nyabkun:qq-shell-color:v2023-06-01-bc02'
}
```

### build.gradle.kts ( Kotlin )
```kotlin
repositories {
    ...
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.nyabkun:qq-shell-color:v2023-06-01-bc02")
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
        <version>v2023-06-01-bc02</version>
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