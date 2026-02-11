plugins {
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.android.library)
  alias(libs.plugins.mavenPublish)
}

android {
  namespace = "com.clerk.convex"
  compileSdk = libs.versions.compileSdk.get().toInt()

  defaultConfig {
    minSdk = libs.versions.minSdk.get().toInt()
    consumerProguardFiles("consumer-rules.pro")
  }

  testOptions { unitTests.isIncludeAndroidResources = true }

  buildTypes {
    debug { isMinifyEnabled = false }
    release {
      isMinifyEnabled = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
}

mavenPublishing {
  coordinates("com.clerk", "clerk-convex", property("CLERK_CONVEX_VERSION") as String)
  publishToMavenCentral()
  signAllPublications()
  pom {
    name.set("Clerk Convex")
    description.set("Bridges Clerk authentication with Convex backends for Android")
    inceptionYear.set("2025")
    url.set("https://github.com/clerk/clerk-convex-kotlin")
    licenses {
      license {
        name.set("MIT License")
        url.set("https://github.com/clerk/clerk-convex-kotlin/blob/main/LICENSE")
        distribution.set("https://github.com/clerk/clerk-convex-kotlin/blob/main/LICENSE")
      }
    }
    developers {
      developer {
        id.set("clerk")
        name.set("Clerk")
        url.set("https://clerk.com")
      }
    }
    scm {
      url.set("https://github.com/clerk/clerk-convex-kotlin")
      connection.set("scm:git:git://github.com/clerk/clerk-convex-kotlin.git")
      developerConnection.set("scm:git:ssh://github.com:clerk/clerk-convex-kotlin.git")
    }
  }
}

dependencies {
  api(libs.clerk.api)

  implementation(libs.convex.mobile)
  implementation(libs.kotlinx.coroutines)

  testImplementation(kotlin("test"))
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.mockk)
  testImplementation(libs.robolectric)
}
