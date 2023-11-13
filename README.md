<!--- version = v2023-11-13 --->

# 🐕 qq-shell-color

**qq-shell-color** is a Kotlin library that can color your console output.

## How to use
- Just copy and paste Single-File version [QColor.kt](src-single/QColor.kt) into your project.
- Or you can use Jar version. See [Maven Dependency Section](#jar-version-maven-dependency).
- Feel free to fork or copy to your own codebase 😍



## Public API

- [`String.qColor()`](src-split/nyab/util/QColor.kt#L23-L32) *extension function*
- [`String.qDeco()`](src-split/nyab/util/QColor.kt#L33-L42) *extension function*
- [`String.qColorAndDecoDebug()`](src-split/nyab/util/QColor.kt#L168-L180) *extension function*
- [`String.qColorTarget()`](src-split/nyab/util/QColor.kt#L181-L184) *extension function*
- [`String.qDecoTarget()`](src-split/nyab/util/QColor.kt#L185-L188) *extension function*
- [`String.qColorRandom()`](src-split/nyab/util/QColor.kt#L189-L190) *extension function*
- [`String.bold`](src-split/nyab/util/QColor.kt#L191-L193) *extension property*
- [`String.italic`](src-split/nyab/util/QColor.kt#L194-L196) *extension property*
- [`String.underline`](src-split/nyab/util/QColor.kt#L197-L199) *extension property*
- [`String.black`](src-split/nyab/util/QColor.kt#L200-L202) *extension property*
- [`String.red`](src-split/nyab/util/QColor.kt#L203-L205) *extension property*
- [`String.green`](src-split/nyab/util/QColor.kt#L206-L208) *extension property*
- [`String.yellow`](src-split/nyab/util/QColor.kt#L209-L211) *extension property*
- [`String.blue`](src-split/nyab/util/QColor.kt#L212-L214) *extension property*
- [`String.purple`](src-split/nyab/util/QColor.kt#L215-L217) *extension property*
- [`String.cyan`](src-split/nyab/util/QColor.kt#L218-L220) *extension property*
- [`String.light_gray`](src-split/nyab/util/QColor.kt#L221-L223) *extension property*
- [`String.dark_gray`](src-split/nyab/util/QColor.kt#L224-L226) *extension property*
- [`String.light_red`](src-split/nyab/util/QColor.kt#L227-L229) *extension property*
- [`String.light_green`](src-split/nyab/util/QColor.kt#L230-L232) *extension property*
- [`String.light_yellow`](src-split/nyab/util/QColor.kt#L233-L235) *extension property*
- [`String.light_blue`](src-split/nyab/util/QColor.kt#L236-L238) *extension property*
- [`String.light_magenta`](src-split/nyab/util/QColor.kt#L239-L241) *extension property*
- [`String.light_cyan`](src-split/nyab/util/QColor.kt#L242-L244) *extension property*
- [`String.white`](src-split/nyab/util/QColor.kt#L245-L247) *extension property*
- [`String.noStyle`](src-split/nyab/util/QColor.kt#L248-L252) *extension property*
- [`QDeco`](src-split/nyab/util/QColor.kt#L66-L85) *enum class*
- [`QColor`](src-split/nyab/util/QColor.kt#L86-L167) *enum class*

## Single-File version Dependency

If you copy & paste [QColor.kt](src-single/QColor.kt),
refer to [build.gradle.kts](build.gradle.kts) to directly check project settings.



```kotlin
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.20")
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.8.20")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.20")
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
    implementation 'com.github.nyabkun:qq-shell-color:v2023-06-01-bc03'
}
```

### build.gradle.kts ( Kotlin )
```kotlin
repositories {
    ...
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.nyabkun:qq-shell-color:v2023-06-01-bc03")
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
        <version>v2023-06-01-bc03</version>
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