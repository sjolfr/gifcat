plugins {
    //trick: for the same plugin versions in all sub-modules
    id("com.android.application").version("7.3.0").apply(false)
    id("com.android.library").version("7.3.0").apply(false)
    kotlin("android").version("1.7.10").apply(false)
    kotlin("multiplatform").version("1.7.10").apply(false)
    kotlin("plugin.serialization").version("1.7.10").apply(false)
    id("io.kotest.multiplatform").version("5.4.2").apply(false)
    id("org.jlleitschuh.gradle.ktlint").version("11.0.0").apply(false)
    id("io.gitlab.arturbosch.detekt").version("1.21.0").apply(false)
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
