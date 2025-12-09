import com.android.build.api.variant.BuildConfigField
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.secrets.gradle.plugin)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.ksp)
}

fun getSecret(key: String): String {
    val secretsFile = rootProject.file("secrets.properties")
    if (!secretsFile.exists()) {
        throw GradleException("secrets.properties file not found at ${secretsFile.absolutePath}")
    }

    val properties = Properties()
    secretsFile.inputStream().use { properties.load(it) }

    return properties.getProperty(key)
        ?: throw GradleException("Key $key not found in secrets.properties")
}

android {
    namespace = "com.bbluecoder.sowittest"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.bbluecoder.sowittest"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("STORE_FILE")?: getSecret("STORE_FILE"))
            storePassword = System.getenv("SIGNING_STORE_PASSWORD") ?: getSecret("KEY_STORE_PASSWORD")
            keyAlias = System.getenv("SIGNING_KEY_ALIAS") ?: getSecret("ALIAS")
            keyPassword = System.getenv("SIGNING_KEY_PASSWORD") ?: getSecret("ALIAS_PASSWORD")
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs["release"]
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

secrets {
    propertiesFileName="secrets.properties"
    defaultPropertiesFileName="local.defaults.properties"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.text.google.fonts)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.gms.play.services.location)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Google Maps SDK
    implementation(libs.play.services.maps)

    // Google Play Services For Location
    implementation(libs.play.services.location)

    // Hilt
    implementation(libs.hilt.android)
    ksp("com.google.dagger:hilt-compiler:2.57.1")

    implementation(libs.maps.compose)

    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")

    //Room
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)
}

