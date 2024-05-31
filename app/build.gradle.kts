import java.util.Properties

plugins {

    alias(libs.plugins.androidApplication)
}
val properties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
localPropertiesFile.inputStream().use { properties.load(it) }
extra.set("OPENAI_API_KEY", properties.getProperty("OPENAI_API_KEY"))
android {
    namespace = "com.example.appprogramming"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.appprogramming"

        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField ("String", "OPENAI_API_KEY", properties.getProperty("OPENAI_API_KEY"))
    }
    buildFeatures {
        buildConfig = true
    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(files("libs/jsoup-1.17.2.jar"))
    implementation("org.json:json:20210307")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:11.1.0")
}