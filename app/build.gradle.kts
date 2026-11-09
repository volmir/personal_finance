plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
}

android {
  namespace = "com.example"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "io.github.volmir.personalfinance"
    minSdk = 24
    targetSdk = 36
    versionCode = 2
    versionName = "1.0.1"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  val keystorePath = System.getenv("KEYSTORE_PATH")
  val storePassword = System.getenv("STORE_PASSWORD")
  val keyPassword = System.getenv("KEY_PASSWORD")
  val keyAlias = System.getenv("KEY_ALIAS") ?: "upload"
  val keystoreFile = keystorePath?.let { file(it) }
  val hasReleaseSigning = !keystorePath.isNullOrBlank() &&
      !storePassword.isNullOrBlank() &&
      !keyPassword.isNullOrBlank() &&
      keystoreFile != null &&
      keystoreFile.exists()

  // стабильный debug-ключ для локальных и CI-сборок, на публикацию не влияет
  val customDebugKeystore = file("${rootDir}/debug.keystore")
  val hasDebugKeystore = customDebugKeystore.exists()

  signingConfigs {
    create("release") {
      if (hasReleaseSigning) {
        storeFile = keystoreFile
        this.storePassword = storePassword
        this.keyAlias = keyAlias
        this.keyPassword = keyPassword
      }
    }
    create("debugConfig") {
      if (hasDebugKeystore) {
        storeFile = customDebugKeystore
        this.storePassword = "android"
        this.keyAlias = "androiddebugkey"
        this.keyPassword = "android"
      }
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      if (hasReleaseSigning) {
        signingConfig = signingConfigs.getByName("release")
      }
    }
    debug {
      if (hasDebugKeystore) {
        signingConfig = signingConfigs.getByName("debugConfig")
      }
    }
  }
  compileOptions {
    isCoreLibraryDesugaringEnabled = true
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions {
    unitTests {
      isIncludeAndroidResources = true
      all {
        // Robolectric resolves Android SDK jars separately from Gradle repositories.
        it.systemProperty("robolectric.dependency.repo.url", "https://repo.maven.apache.org/maven2")
      }
    }
  }
  dependenciesInfo {
    includeInApk = false
    includeInBundle = true
  }
}

dependencies {
  coreLibraryDesugaring(libs.desugar.jdk.libs)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)

  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
}
