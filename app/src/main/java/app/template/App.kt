package app.template

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.StrictMode
import com.google.android.material.color.DynamicColors
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class App : Application() {
	override fun onCreate() {
		super.onCreate()
		/**
		 * Initialize MMKV Store
		 */
		MMKV.initialize(this)

		/**
		 * Bypassing `fileUriExposedException`, see https://stackoverflow.com/questions/38200282/android-os-fileuriexposedexception-file-storage-emulated-0-test-txt-exposed
		 */
		val builder = StrictMode.VmPolicy.Builder()
		StrictMode.setVmPolicy(builder.build())

		context = applicationContext
		packageInfo = packageManager.run {
			if (Build.VERSION.SDK_INT >= 33) getPackageInfo(
				packageName, PackageManager.PackageInfoFlags.of(0)
			) else
				getPackageInfo(packageName, 0)
		}
		applicationScope = CoroutineScope(SupervisorJob())
		DynamicColors.applyToActivitiesIfAvailable(this)
	}

	/**
	 * Release memory when the UI becomes hidden or when system resources become low.
	 * @param level the memory-related event that was raised.
	 */
	override fun onTrimMemory(level: Int) {
		super.onTrimMemory(level)
		println("Trim Memory $level")
	}

	companion object {
		lateinit var applicationScope: CoroutineScope
		lateinit var packageInfo: PackageInfo

		@SuppressLint("StaticFieldLeak")
		lateinit var context: Context
	}
}