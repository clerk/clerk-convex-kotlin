plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.kotlin.serialization)
}

configurations.configureEach {
  resolutionStrategy.dependencySubstitution {
    // Temporary substitution: Clerk UI 1.0 currently references telemetry 1.0,
    // but the published telemetry artifact line is 0.1.x.
    substitute(module("com.clerk:clerk-android-telemetry:1.0"))
      .using(module("com.clerk:clerk-android-telemetry:0.1.3"))
  }
}

android {
  namespace = "com.clerk.workouttracker"
  compileSdk { version = release(36) }

  defaultConfig {
    applicationId = "com.clerk.workouttracker"
    minSdk = 26
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"

    val clerkPublishableKeyProp = "WORKOUT_CLERK_PUBLISHABLE_KEY"
    val convexDeploymentUrlProp = "WORKOUT_CONVEX_DEPLOYMENT_URL"

    val clerkPublishableKey =
      (project.findProperty(clerkPublishableKeyProp) as String?) ?: "PLACEHOLDER_KEY"
    val convexDeploymentUrl =
      (project.findProperty(convexDeploymentUrlProp) as String?) ?: "PLACEHOLDER_URL"

    buildConfigField("String", clerkPublishableKeyProp, "\"$clerkPublishableKey\"")
    buildConfigField("String", convexDeploymentUrlProp, "\"$convexDeploymentUrl\"")
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  kotlin { jvmToolchain(libs.versions.jvmTarget.get().toInt()) }

  buildFeatures {
    compose = true
    buildConfig = true
  }
}

dependencies {
  implementation(project(":source:clerk-convex-android"))
  implementation(libs.clerk.ui)
  implementation(libs.core.ktx)
  implementation(libs.convex.mobile)
  implementation(libs.lifecycle.runtime.ktx)
  implementation(libs.lifecycle.viewmodel.ktx)
  implementation(libs.activity.compose)
  implementation(libs.kotlinx.coroutines)
  implementation(libs.kotlinx.serialization)
  implementation(platform(libs.compose.bom))
  implementation(libs.navigation.compose)
  implementation(libs.ui)
  implementation(libs.ui.graphics)
  implementation(libs.ui.tooling.preview)
  implementation(libs.material3)
  testImplementation(libs.junit)
  androidTestImplementation(libs.ext.junit)
  androidTestImplementation(libs.espresso.core)
  androidTestImplementation(platform(libs.compose.bom))
  androidTestImplementation(libs.ui.test.junit4)
  debugImplementation(libs.ui.tooling)
  debugImplementation(libs.ui.test.manifest)
}
