import java.io.FileInputStream
import java.util.*

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
	alias(libs.plugins.androidApplication)
	alias(libs.plugins.kotlinAndroid)
	id("com.google.devtools.ksp")
	kotlin("kapt")
}

sealed class Version(
	open val versionMajor: Int,
	val versionMinor: Int,
	val versionPatch: Int,
	val versionBuild: Int = 0,
) {
	abstract fun toVersionName(): String
	class Beta(versionMajor: Int, versionMinor: Int, versionPatch: Int, versionBuild: Int) :
		Version(versionMajor, versionMinor, versionPatch, versionBuild) {
		override fun toVersionName(): String =
			"${versionMajor}.${versionMinor}.${versionPatch}-beta.$versionBuild"
	}

	class Stable(versionMajor: Int, versionMinor: Int, versionPatch: Int) :
		Version(versionMajor, versionMinor, versionPatch) {
		override fun toVersionName(): String =
			"${versionMajor}.${versionMinor}.${versionPatch}"
	}

	class ReleaseCandidate(
		versionMajor: Int,
		versionMinor: Int,
		versionPatch: Int,
		versionBuild: Int,
	) :
		Version(versionMajor, versionMinor, versionPatch, versionBuild) {
		override fun toVersionName(): String =
			"${versionMajor}.${versionMinor}.${versionPatch}-rc.$versionBuild"
	}
}

val currentVersion: Version = Version.Beta(
	versionMajor = 1,
	versionMinor = 0,
	versionPatch = 0,
	versionBuild = 1
)

val keystorePropertiesFile = rootProject.file("keystore.properties")

val splitApks = !project.hasProperty("noSplits")

android {
	if (keystorePropertiesFile.exists()) {
		val keystoreProperties = Properties()
		keystoreProperties.load(FileInputStream(keystorePropertiesFile))
		signingConfigs {
			getByName("debug")
			{
				keyAlias = keystoreProperties["keyAlias"].toString()
				keyPassword = keystoreProperties["keyPassword"].toString()
				storeFile = file(keystoreProperties["storeFile"]!!)
				storePassword = keystoreProperties["storePassword"].toString()
			}
		}
	}

	namespace = "app.template"
	compileSdk = 33

	defaultConfig {
		// TODO: Change me
		applicationId = "app.template"
		minSdk = 26
		targetSdk = 33
		versionCode = 1
		versionName = currentVersion.toVersionName().run {
			if (!splitApks) "$this-(F-Droid)"
			else this
		}

		if (!splitApks)
			ndk {
				(properties["ABI_FILTERS"] as String).split(';').forEach {
					abiFilters.add(it)
				}
			}

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables {
			useSupportLibrary = true
		}
	}

	if (splitApks)
		splits {
			abi {
				isEnable = !project.hasProperty("noSplits")
				reset()
				include("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
				isUniversalApk = false
			}
		}

	buildTypes {
		release {
			isMinifyEnabled = true
			isShrinkResources = true
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
			)
			if (keystorePropertiesFile.exists())
				signingConfig = signingConfigs.getByName("debug")
		}
		debug {
			if (keystorePropertiesFile.exists())
				signingConfig = signingConfigs.getByName("debug")
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}
	kotlinOptions {
		jvmTarget = JavaVersion.VERSION_1_8.toString()
	}
	kotlin {
		jvmToolchain(8)
	}
	buildFeatures {
		compose = true
	}

	applicationVariants.all {
		outputs.all {
			(this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName =
				"Kotlin-Template-${defaultConfig.versionName}-${name}.apk"
		}
	}

	composeOptions {
		kotlinCompilerExtensionVersion = "1.4.6"
		// TODO: Alternative
		// kotlinCompilerExtensionVersion = libs.versions.**
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
}

dependencies {
	implementation(project(":color"))

	implementation(libs.mmkv)

	implementation(libs.pixely.components)
	implementation(libs.androidx.compose.material3.windowSizeClass)

	ksp(libs.hilt.ext.compiler)
	implementation(libs.hilt.android)
	ksp(libs.hilt.compiler)

	// Room
	implementation("androidx.room:room-runtime:2.5.1")
	implementation("androidx.room:room-paging:2.5.1")
	implementation("androidx.room:room-ktx:2.5.1")
	implementation(libs.material)
	ksp("androidx.room:room-compiler:2.5.1")

	implementation(libs.accompanist.systemuicontroller)
	implementation(libs.accompanist.permissions)
	implementation(libs.accompanist.navigation.animation)
	implementation(libs.accompanist.webview)
	implementation(libs.accompanist.pager.layouts)
	implementation(libs.accompanist.pager.indicators)
	implementation(libs.accompanist.flowlayout)

	implementation(libs.core.ktx)
	implementation(libs.lifecycle.runtime.ktx)
	implementation(libs.activity.compose)
	implementation(platform(libs.compose.bom))
	implementation(libs.ui)
	implementation(libs.ui.graphics)
	implementation(libs.ui.tooling.preview)
	implementation(libs.material3)
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.test.ext.junit)
	androidTestImplementation(libs.espresso.core)
	androidTestImplementation(platform(libs.compose.bom))
	androidTestImplementation(libs.ui.test.junit4)
	debugImplementation(libs.ui.tooling)
	debugImplementation(libs.ui.test.manifest)
}

tasks {
	withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
		kotlinOptions {
			freeCompilerArgs += "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
			freeCompilerArgs += "-opt-in=androidx.compose.material.ExperimentalMaterialApi"
			freeCompilerArgs += "-opt-in=androidx.compose.animation.ExperimentalAnimationApi"
			freeCompilerArgs += "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi"
			freeCompilerArgs += "-opt-in=com.google.accompanist.pager.ExperimentalPagerApi"
			freeCompilerArgs += "-opt-in=coil.annotation.ExperimentalCoilApi"
			freeCompilerArgs += "-opt-in=androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi"
		}
	}
}

ksp {
	arg("room.schemaLocation", "$projectDir/schemas".toString())
}