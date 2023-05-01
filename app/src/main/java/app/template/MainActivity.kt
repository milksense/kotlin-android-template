package app.template

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import app.template.App.Companion.context
import app.template.ui.theme.GlobalTheme
import app.template.ui.common.LocalDarkTheme
import app.template.ui.common.LocalDynamicColorSwitch
import app.template.ui.common.LocalSeedColor
import app.template.ui.common.SettingsProvider
import app.template.ui.page.MainEntry
import com.alessiocameroni.pixely_components.PixelyListItem
import com.alessiocameroni.pixely_components.PixelyListItemDefaults
import com.alessiocameroni.pixely_components.PixelySectionTitle
import com.alessiocameroni.pixely_components.PixelySegmentedRow
import com.alessiocameroni.pixely_components.PixelySupportInfoText
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
				)  {
					MainEntry()
				}
			}
		}
	}
}