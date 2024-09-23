plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(libs.javax.inject)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0") // 최신 버전으로 변경 가능
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0") // 최신 버전으로 변경 가능

}