import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

println("This Gradle script starts with -Dfile.encoding=" + System.getProperty("file.encoding"))

val qMavenArtifactId = "qq-shell-color"
val qKotlinVersion = "1.8.20"
val qJvmSourceCompatibility = "17"
val qJvmTargetCompatibility = "17"

plugins {
    val kotlinVersion = "1.8.20"
    
    application
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("maven-publish")
    
}

group = "com.github.nyabkun"

version = "v2023-11-13-bc02"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

java {
    toolchain {
        sourceCompatibility = JavaVersion.toVersion(qJvmSourceCompatibility)
        targetCompatibility = JavaVersion.toVersion(qJvmTargetCompatibility)
    }
}


sourceSets.main {
    java.srcDirs("src-split")

    resources.srcDirs("rsc")
}

sourceSets.test {
    java.srcDirs("src-test-split")

    resources.srcDirs("rsc-test")
}

sourceSets.register("example") {
    java.srcDirs("src-example")
    val jarFile = "$buildDir/libs/$qMavenArtifactId-$version.jar"
    compileClasspath += files(jarFile)
    runtimeClasspath += files(jarFile)

    resources.srcDirs("rsc")
}

tasks.getByName("compileExampleKotlin").dependsOn("jar")

val exampleImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

val exampleRuntimeOnly: Configuration by configurations.getting {
    extendsFrom(configurations.runtimeOnly.get())
}

sourceSets.register("single") {
    java.srcDirs("src-single")
}

val singleImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

val singleRuntimeOnly: Configuration by configurations.getting {
    extendsFrom(configurations.runtimeOnly.get())
}

sourceSets.register("testSingle") {
    java.srcDirs("src-test-single")

    resources.srcDirs("rsc-test")
}

val testSingleImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

val testSingleRuntimeOnly: Configuration by configurations.getting {
    extendsFrom(configurations.testRuntimeOnly.get())
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.20")
    testImplementation("org.jetbrains.kotlin:kotlin-reflect:1.8.20")
}

tasks {
    jar {
        enabled = true
        
        archiveBaseName.set(qMavenArtifactId)
    
        from(sourceSets.main.get().output)
    
        manifest {
            attributes(
                "Implementation-Title" to qMavenArtifactId,
                "Implementation-Version" to project.version
            )
        }
    }
    
    val qSrcJar by registering(Jar::class) {
        archiveBaseName.set(qMavenArtifactId)
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }
    
    artifacts {
        archives(qSrcJar)
        archives(jar)
    }
    
    distZip {
        enabled = false
    }
    
    distTar {
        enabled = false
    }
    
    startScripts {
        enabled = false
    }

    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    withType<JavaExec>().configureEach {
        jvmArgs("-Dfile.encoding=UTF-8")
    }

    withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = JavaVersion.toVersion(qJvmTargetCompatibility).toString()
    }

    register<JavaExec>("qRunTestSplit") {
        mainClass.set("nyab.util.QColorTestKt")
        classpath = sourceSets.get("test").runtimeClasspath
    }

    register<JavaExec>("qRunTestSingle") {
        mainClass.set("QColorTestKt")
        classpath = sourceSets.get("testSingle").runtimeClasspath
    }

    register<JavaExec>("qRunExample") {
        mainClass.set("shcolor.QColorExampleKt")
        classpath = sourceSets.get("example").runtimeClasspath
    }
    
    getByName<Test>("test") {
        dependsOn("qRunTestSingle")
        dependsOn("qRunTestSplit")
        dependsOn("qRunExample")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = qMavenArtifactId
           
            from(components["kotlin"])

            artifact(tasks.getByName("qSrcJar")) {
                classifier = "sources"
            }
        }
    }
//      // GitHub Packages
//      repositories {
//          maven {
//              url = uri("https://maven.pkg.github.com/nyabkun/qq-shell-color")
//              credentials {
//                  username = "nyabkun"
//                  password = File("../../.q_gpr.key").readText(Charsets.UTF_8).trim()
//              }
//          }
//      }
}