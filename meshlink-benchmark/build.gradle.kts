plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":meshlink"))
    implementation(libs.kotlinx.coroutines.core)
}