plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")

    // Apply the Application plugin to add support for building an executable JVM application.
    application
}

dependencies {
    // Project "app" depends on project "utils". (Project paths are separated with ":", so ":utils" refers to the top-level "utils" project.)
    implementation(project(":utils"))
}

application {
    // Define the Fully Qualified Name for the application main class
    // (Note that Kotlin compiles `Annotaion.kt` to a class with FQN `org.charlie.app.AnnotaionKt`.)
    mainClass = "org.charlie.app.MainKt"
}
