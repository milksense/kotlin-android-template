package app.template

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import app.template.App.Companion.context
import app.template.ui.common.LocalDarkTheme
import app.template.ui.common.LocalDynamicColorSwitch
import app.template.ui.common.LocalSeedColor
import app.template.ui.common.SettingsProvider
import app.template.ui.page.MainEntry
import app.template.ui.theme.GlobalTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

	@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		WindowCompat.setDecorFitsSystemWindows(window, false)
		ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { v, insets ->
			v.setPadding(0, 0, 0, 0)
			insets
		}
		// Private mode ;)
		window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
		context = this.baseContext

		setContent {
			val windowSizeClass = calculateWindowSizeClass(this)
			SettingsProvider(windowSizeClass.widthSizeClass) {
				GlobalTheme(
					darkTheme = LocalDarkTheme.current.isDarkTheme(),
					isHighContrastModeEnabled = LocalDarkTheme.current.isHighContrastModeEnabled,
					seedColor = LocalSeedColor.current,
					isDynamicColorEnabled = LocalDynamicColorSwitch.current,
				) {
					MainEntry()
				}
			}
		}
	}
}