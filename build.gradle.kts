plugins {
    java
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.reactivex.rxjava3:rxjava:3.1.8")
}

application {
    mainClass = "Sequential"
}