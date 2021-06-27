import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.mpp.*
import org.jetbrains.kotlin.util.capitalizeDecapitalize.capitalizeFirstWord

buildscript {
    val dokkaVersion: String by project
    val bintrayPluginVersion: String by project
    dependencies {
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:$dokkaVersion")
        classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:$bintrayPluginVersion")
    }
    repositories {
        jcenter()
        mavenCentral()
        maven { setUrl("https://dl.bintray.com/jetbrains/kotlin-native-dependencies") }
    }
}

// Required to make statically typed accessors available in sub-projects.
// Not used directly.
plugins {
    kotlin("multiplatform") version "1.3.20"
}

val defaultRepositories: RepositoryHandler.() -> Unit = {
    mavenCentral()
    jcenter()
}

repositories(defaultRepositories)

subprojects {
    apply(plugin="org.jetbrains.kotlin.multiplatform")
    repositories(defaultRepositories)
}

// Workaround: `by project` form is broken on gradle 4.7
val publishingMode = findProperty("publishKoma") as? String != null ||
        gradle.startParameter.taskNames.any { it.contains("publish") }



project("koma-core") {
    base.archivesBaseName = "koma-core"

    kotlin {
        jvm("ejml") {
            main {
                defaultSourceSet {
                    kotlin.srcDir("jvm-ejml/src")
                    dependencies {
                        implementation("org.ejml:ejml-all:0.37")
                    }
                }
            }
        }
        jvm("mtj") {
            main {
                defaultSourceSet {
                    kotlin.srcDir("jvm-mtj/src")
                    dependencies {
                        implementation("com.googlecode.matrix-toolkits-java:mtj:1.0.4")
                    }
                }

            }
        }
        jvm("jblas") {

            main {
                defaultSourceSet {
                    kotlin.srcDir("jvm-jblas/src")
                    dependencies {
                        implementation("org.jblas:jblas:1.2.3")
                    }
                }
            }
        }
        native(prefix="cblas-") {
            main {
                buildTypes = mutableListOf(NativeBuildType.DEBUG, NativeBuildType.RELEASE)
                defaultSourceSet {
                    kotlin.srcDir("native-cblas/src")
                }
                addCBlasInterop()
                addLapackeInterop()
            }
        }
        if (!publishingMode) {
            native(suffix="Example") {
                main {
                    outputKinds = mutableListOf(NativeOutputKind.EXECUTABLE)
                    buildTypes = mutableListOf(NativeBuildType.DEBUG, NativeBuildType.RELEASE)
                    defaultSourceSet {
                        val platform = this@native.name.dropLast(7)
                        dependsOn(sourceSets["cblas-${platform}Main"])
                        kotlin.srcDir("../examples/native")
                    }
                    addCBlasInterop()
                    addLapackeInterop()
                }
            }
        }
        js {
            main {
                kotlinOptions {
                    outputFile = "node_modules/koma_core_implementation.js"
                    moduleKind = "umd"
                    sourceMap = true
                    sourceMapEmbedSources = "always"
                    metaInfo = true
                }
                defaultSourceSet {
                    kotlin.srcDir("js-default/src")
                }
            }
        }
        commonMainSourceSet {
            dependencies {
                implementation(project(":koma-core-api"))
            }
        }
    }
    apply(from = rootProject.file("buildscripts/publishing.gradle"))
}
project("koma-core-api") {
    kotlin {
        native {
            main {
                buildTypes = mutableListOf(NativeBuildType.DEBUG, NativeBuildType.RELEASE)
                defaultSourceSet {
                    kotlin.srcDir("native/src")
                }
            }
        }
        jvm {
            main {
                defaultSourceSet {
                    kotlin.srcDir("jvm/src")
                }
            }
        }
        js {
            main {
                defaultSourceSet {
                    kotlin.srcDir("js/src")
                }
            }
        }
        commonMainSourceSet {
            kotlin.srcDir("common/src")
        }
    }
    apply(from = rootProject.file("buildscripts/publishing.gradle"))
}

// A project to generate koma shared/static libraries for native.
//
// Must be a separate project because currently konan only includes symbols
// from the current project (not dependent projects), so the cblas/api
// projects would result in incomplete headers.
//
// Must be named `koma` because of KT-28313. Disabled in intellij
// to prevent clashes with the ordinary koma-native-cblas project.

project(":koma") {
    val cmdLine = gradle.startParameter.taskNames.any { rq ->
        rq.contains("build") ||
        rq.toLowerCase().contains("native") ||
        rq.contains("tasks")
    }
    if (cmdLine)
        kotlin {
            native {
                main {
                    outputKinds = mutableListOf(NativeOutputKind.STATIC,
                                                NativeOutputKind.DYNAMIC)
                    buildTypes = mutableListOf(NativeBuildType.DEBUG,
                                               NativeBuildType.RELEASE)
                    defaultSourceSet {
                        kotlin.srcDir("../koma-core-api/native/src")
                        kotlin.srcDir("../koma-core/native-cblas/src")
                    }
                    addCBlasInterop()
                    addLapackeInterop()
                }
                afterEvaluate {
                    binaries {
                        forEach {
                            it.baseName = "koma"
                        }
                    }
                }
            }
            commonMainSourceSet {
                kotlin.srcDir("../koma-core-api/common/src")
            }
        }
}

project(":koma-logging") {
    kotlin {
        jvm {
            main {
                defaultSourceSet {
                    kotlin.srcDir("src")
                    dependencies {
                        implementation(project(":koma-core-api"))
                        implementation("org.slf4j:slf4j-api:1.7.21")
                        implementation("ch.qos.logback:logback-classic:1.1.7")
                    }
                }
            }
        }
    }
    apply(from = rootProject.file("buildscripts/publishing.gradle"))
}
project(":koma-plotting") {
    kotlin {
        jvm {
            main {
                defaultSourceSet {
                    kotlin.srcDir("src")
                    dependencies {
                        implementation(project(":koma-core-api"))
                        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
                        api("org.knowm.xchart:xchart:3.5.1")
                    }
                }
            }
        }
    }
    apply(from = rootProject.file("buildscripts/publishing.gradle"))
}
project(":koma-tests") {
    kotlin {
        jvm {
            test {
                defaultSourceSet {
                    kotlin.srcDir("test")
                    dependencies {
                        implementation(project(":koma-core", configuration="ejmlDefault"))
                        implementation(project(":koma-core", configuration="mtjDefault"))
                        implementation(project(":koma-core", configuration="jblasDefault"))
                        implementation("org.jetbrains.kotlin:kotlin-test")
                        implementation("org.jetbrains.kotlin:kotlin-test-junit")
                    }
                }
            }
        }
        js {
            test {
                defaultSourceSet {
                    dependencies {
                        implementation(project(":koma-core"))
                        implementation("org.jetbrains.kotlin:kotlin-test-js")
                    }
                }
            }
        }
        commonTestSourceSet {
            dependencies {
                implementation(project(":koma-core-api"))
                implementation("org.jetbrains.kotlin:kotlin-test-common")
                implementation("org.jetbrains.kotlin:kotlin-test-annotations-common")
            }
        }
    }
}

// Must run after all project targets loaded
allprojects {
    fun KotlinCompilation<*>.addDep(depName: String) {
        this.defaultSourceSet {
            dependencies {
                implementation(depName)
            }
        }
    }

    // Inject the standard library into targets automatically
    this.kotlin.targets.forEach {
        it.compilations
            .forEach {
                if (it is KotlinJvmCompilation) {
                    it.addDep("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
                }
                else if (it is KotlinJsCompilation) {
                    it.addDep("org.jetbrains.kotlin:kotlin-stdlib-js")
                }
                else if (it is KotlinCommonCompilation) {
                    it.addDep("org.jetbrains.kotlin:kotlin-stdlib-common")
                }
                else if (it is KotlinNativeCompilation) {
                }
            }
    }
}
tasks["build"].dependsOn(":buildJvm", ":buildNative", ":buildJs")

val buildJs by tasks.creating {
    dependsOn(":koma-core:jsJar")
    doLast {
        copy {
            from("koma-core-api/build/libs")
            from("koma-core/build/libs")
            include("*js*.jar")
            into(rootDir.toString() + "/build/js")
        }
    }
}
val buildJvm by tasks.creating {
    dependsOn(":koma-core:ejmlJar")
    dependsOn(":koma-core:mtjJar")
    dependsOn(":koma-core:jblasJar")
    dependsOn(":koma-core-api:jvmJar")
    dependsOn(":koma-plotting:build")
    dependsOn(":koma-logging:build")

    doLast {
        copy {
            from("koma-core-api/build/libs")
            from("koma-core/build/libs")
            from("koma-plotting/build/libs")
            from("koma-logging/build/libs")
            include("*.jar")
            into(rootDir.toString() + "/build/jvm")
        }
    }
}
val os = org.gradle.internal.os.OperatingSystem.current()
val currentDesktopPlatform =
        if(os.isWindows)
            "mingwX64"
        else if (os.isMacOsX)
            "macosX64"
        else
            "linuxX64"


val buildNative by tasks.creating {
    dependsOn(":koma-core-api:${currentDesktopPlatform}Binaries")
    dependsOn(":koma-core:cblas-${currentDesktopPlatform}Binaries")
    dependsOn(":koma:build")
    dependsOn(":koma-core:linkMainReleaseExecutable${currentDesktopPlatform.capitalize()}Example")
    doLast {
        copy {
            from("koma-core-api/build/classes/kotlin/${currentDesktopPlatform}/main")
            from("koma-core/build/classes/kotlin/cblas-${currentDesktopPlatform}/main")
            include("*.klib")
            into(rootDir.toString() + "/build/native/klib")
        }
        copy {
            from("koma/build/bin/${currentDesktopPlatform}")
            include("mainReleaseStatic/**")
            include("mainReleaseShared/**")
            into(getRootDir().toString() + "/build/native")
        }
        copy {
            from("koma-core/build/bin/${currentDesktopPlatform}Example")
            include("mainReleaseExecutable/**")
            into(rootDir.toString() + "/build/native")
        }
    }
}

//TODO: Reenable dokka
/*
// Workaround via https://discuss.kotlinlang.org/t/how-to-configure-dokka-for-kotlin-multiplatform/9834
dokka {
    impliedPlatforms = ["Common"]
    dependsOn(":codegen")
    outputFormat = "gfm"
    outputDirectory = "docs/markdown"
    moduleName = 'api'
    kotlinTasks {
        []
    }
    sourceRoot {
        path = project(":koma-core-api").kotlin.sourceSets.commonMain.kotlin.srcDirs[0]
    }
    sourceDirs = files([
            "koma-core/native-cblas/src"
    ])

    jdkVersion = 8
    doLast {
        // Dokka uses moduleName for the final folder name, and mangles upper case letters
        file("docs/markdown/api").renameTo(file("docs/markdown/Reference_API_Docs"))
    }
}
 */

apply(from="buildscripts/codegen.gradle")

// Utility Functions

fun <T: KotlinCompilation<*>> KotlinOnlyTarget<T>.main(f: T.()->Unit) {
    compilations["main"].f()
}
fun <T: KotlinCompilation<*>> KotlinOnlyTarget<T>.test(f: T.()->Unit) {
    compilations["test"].f()
}

fun KotlinMultiplatformExtension.commonMainSourceSet(f: KotlinSourceSet.()->Unit) {
    (sourceSets) {
        "commonMain" {
            f()
        }
    }
}
fun KotlinMultiplatformExtension.commonTestSourceSet(f: KotlinSourceSet.()->Unit) {
    (sourceSets) {
        "commonTest" {
            f()
        }
    }
}

// This is a workaround for kotlin-native#2372.
//
// When publishing, we need to expose all platforms to gradle. When importing
// to the IDE, we need to expose only one. This function runs the [configBlock]
// on one or more of the set of platforms specified, depending on whether
// or not it detects we are in publishing mode.
fun KotlinMultiplatformExtension.native(prefix: String = "",
                                        suffix: String = "",
                                        vararg extraPlatforms: (KotlinNativeTarget.() -> Unit)->Unit,
                    configBlock: KotlinNativeTarget.()->Unit) {
    val os = org.gradle.internal.os.OperatingSystem.current()
    if (os.isMacOsX || publishingMode) {
        macosX64("${prefix}macosX64$suffix") { configBlock() }
    }
    if (os.isWindows || publishingMode) {
        mingwX64("${prefix}mingwX64$suffix") { configBlock() }
    }
    if (os.isLinux || publishingMode){
        linuxX64("${prefix}linuxX64$suffix") { configBlock() }
    }
    if (publishingMode) {
        extraPlatforms.forEach {
            it(configBlock)
        }
    }
}

fun KotlinNativeCompilation.addCBlasInterop() {
    val cblasIncludeDir: String? = findProperty("cblasIncludeDir") as? String
    val cblasLibDir: String? = findProperty("cblasLibDir") as? String

    addInterop("cblas",
               "../koma-core/native-cblas/defs/cblas.def",
               cblasIncludeDir,
               cblasLibDir)

}
fun KotlinNativeCompilation.addLapackeInterop() {
    val lapackeIncludeDir: String? = findProperty("lapackeIncludeDir") as? String
    val lapackeLibDir: String? = findProperty("lapackeLibDir") as? String

    addInterop("lapacke",
               "../koma-core/native-cblas/defs/lapacke.def",
               lapackeIncludeDir,
               lapackeLibDir)
}

fun KotlinNativeCompilation.addInterop(name: String,
                                       defFile: String,
                                       includeDir: String? = null,
                                       libDir: String? = null) {
    // See k/n#1807 and kotlin-dsl#1138
    cinterops.apply {
        create(name) {
            defFile(defFile)
            includeDir?.let { includeDirs(it) }
        }
    }
    // Workaround for bug: this wont run inside the cinterops.create{...} scope
    libDir?.let {
        linkerOpts("-L$libDir")
    }
}

