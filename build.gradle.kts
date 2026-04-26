plugins {
    java
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.reactivex.rxjava3:rxjava:3.1.8")
    implementation("io.vertx:vertx-core:5.0.11")
}

application {
    mainClass = "Sequential"
}