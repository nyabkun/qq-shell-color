# 🐕 qq-shell-color

**qq-shell-color** is a Kotlin library that can color your console output.
- Just copy and paste 🟦 Single-File version [QShColor.kt](src-single/QShColor.kt) into your project. 
- Or you can use 🟩 Split-File Jar version. See [Maven Dependency Section](#-split-file-jar-version-maven-dependency).
- Feel free to fork or copy to your own codebase.

## Example

### output
<p align="center">
    
</p>
<p align="center">
    <img src="img/result.png" width="611" alt="result.png">
</p>

### code

Full Source  [QShColorExample.kt](src-example/QShColorExample.kt)

```kotlin
fun main() {
    var txt = "c".yellow + "o".blue + "l".red + "o".magenta + "u".green + "r".cyan + "f".yellow + "u".blue + "l".red

    println(txt)

    txt = "you can set background".qColor(fg = QShColor.RED, bg = QShColor.BLUE)

    println(txt)

    txt = """val color = "you can use regex to color targeted text"""".qColorTarget(
        ptn = """val(?!\S)""".toRegex(),
        color = QShColor.MAGENTA
    ).qColorTarget(
        ptn = """".*?"""".toRegex(),
        color = QShColor.GREEN
    )

    println(txt)
}
```

Please see [QShColorTest.kt](src-test-split/nyab/util/QShColorTest.kt) for more code examples.
Single-File version [src-test-single/QShColorTest.kt](src-test-single/QShColorTest.kt) is a self-contained source code that includes a runnable main function.
You can easily copy and paste it into your codebase.        

## 🟦 Single-File version Dependency

If you copy & paste [QShColor.kt](src-single/QShColor.kt).

Refer to [build.gradle.kts](build.gradle.kts) to directly check project settings.



```kotlin
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.20")
    testImplementation(kotlin("test"))
}
```

## 🟩 Split-File Jar version Maven Dependency

If you prefer a jar library. Add [jitpack.io](https://jitpack.io/#nyabkun/qq-shell-color) repository to the build script.

### build.gradle ( Groovy )
```groovy
repositories {
    ...
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.nyabkun:qq-shell-color:v2023-05-21'
}
```

### build.gradle.kts ( Kotlin )
```kotlin
repositories {
    ...
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.nyabkun:qq-shell-color:v2023-05-21")
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
        <version>v2023-05-21</version>
    </dependency>
</dependencies>
```

## How did I create this library

I created this library by developing a program within my own codebase that automatically resolves dependencies at the method or property level, extracts necessary code elements, and generates a compact, self-contained, single-file library.

The program uses [PSI](https://plugins.jetbrains.com/docs/intellij/psi.html) to resolve dependencies for function calls and references to classes.

Although my original repository is currently disorganized, I have been gradually extracting and publishing small libraries. I also plan to prepare the original repository for publication in the future

## References

- [kolor](https://github.com/ziggy42/kolor)            