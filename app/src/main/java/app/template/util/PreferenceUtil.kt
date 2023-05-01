package app.template.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.template.App.Companion.applicationScope
import app.template.R
import app.template.ui.theme.ColorScheme.DEFAULT_SEED_COLOR
import com.google.android.material.color.DynamicColors
import com.kyant.monet.PaletteStyle
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val TEST = "palette_style"
const val AUTO_UPDATE = "palette_style"

const val PALETTE_STYLE = "palette_style"
const val DARK_THEME_VALUE = "dark_theme_value"
private const val DYNAMIC_COLOR = "dynamic_color"
private const val HIGH_CONTRAST = "high_contrast"
private const val THEME_COLOR = "theme_color"

val palettesMap = mapOf(
	0 to PaletteStyle.TonalSpot,
	1 to PaletteStyle.Spritz,
	2 to PaletteStyle.FruitSalad,
	3 to PaletteStyle.Vibrant,
)

private val IntPreferenceDefaults = mapOf(
	PALETTE_STYLE to 0,
	DARK_THEME_VALUE to DarkThemePreference.FOLLOW_SYSTEM,
)

private val BooleanPreferenceDefaults = mapOf(
	AUTO_UPDATE to true,
)

private val StringPreferenceDefaults = mapOf(
	TEST to "default",
)

object PreferenceUtil  {
	private val kv = MMKV.defaultMMKV()

	fun String.getInt(default: Int = IntPreferenceDefaults.getOrElse(this) { 0 }): Int =
		kv.decodeInt(this, default)

	fun String.getString(default: String = StringPreferenceDefaults.getOrElse(this) { "" }): String =
		kv.decodeString(this) ?: default

	fun String.getBoolean(default: Boolean = BooleanPreferenceDefaults.getOrElse(this) { false }): Boolean =
		kv.decodeBool(this, default)

	fun String.updateString(newString: String) = kv.encode(this, newString)

	fun String.updateInt(newInt: Int) = kv.encode(this, newInt)

	fun String.updateBoolean(newValue: Boolean) = kv.encode(this, newValue)
	fun updateValue(key: String, b: Boolean) = key.updateBoolean(b)
	fun encodeInt(key: String, int: Int) = key.updateInt(int)
	fun getValue(key: String): Boolean = key.getBoolean()
	fun encodeString(key: String, string: String) = key.updateString(string)
	fun containsKey(key: String) = kv.containsKey(key)

	data class AppSettings(
		val darkTheme: DarkThemePreference = DarkThemePreference(),
		val isDynamicColorEnabled: Boolean = false,
		val seedColor: Int = DEFAULT_SEED_COLOR,
		val paletteStyleIndex: Int = 0
	)

	private val mutableAppSettingsStateFlow = MutableStateFlow(
		AppSettings(
			DarkThemePreference(
				darkThemeValue = kv.decodeInt(
					DARK_THEME_VALUE,
					DarkThemePreference.FOLLOW_SYSTEM
				), isHighContrastModeEnabled = kv.decodeBool(HIGH_CONTRAST, false)
			),
			isDynamicColorEnabled = kv.decodeBool(
				DYNAMIC_COLOR,
				DynamicColors.isDynamicColorAvailable()
			),
			seedColor = kv.decodeInt(THEME_COLOR, DEFAULT_SEED_COLOR),
			paletteStyleIndex = kv.decodeInt(PALETTE_STYLE, 0)
		)
	)
	val AppSettingsStateFlow = mutableAppSettingsStateFlow.asStateFlow()

	fun modifyDarkThemePreference(
		darkThemeValue: Int = AppSettingsStateFlow.value.darkTheme.darkThemeValue,
		isHighContrastModeEnabled: Boolean = AppSettingsStateFlow.value.darkTheme.isHighContrastModeEnabled
	) {
		applicationScope.launch(Dispatchers.IO) {
			mutableAppSettingsStateFlow.update {
				it.copy(
					darkTheme = AppSettingsStateFlow.value.darkTheme.copy(
						darkThemeValue = darkThemeValue,
						isHighContrastModeEnabled = isHighContrastModeEnabled
					)
				)
			}
			kv.encode(DARK_THEME_VALUE, darkThemeValue)
			kv.encode(HIGH_CONTRAST, isHighContrastModeEnabled)
		}
	}

	fun modifyThemeSeedColor(colorArgb: Int, paletteStyleIndex: Int) {
		applicationScope.launch(Dispatchers.IO) {
			mutableAppSettingsStateFlow.update {
				it.copy(seedColor = colorArgb, paletteStyleIndex = paletteStyleIndex)
			}
			kv.encode(THEME_COLOR, colorArgb)
			kv.encode(PALETTE_STYLE, paletteStyleIndex)
		}
	}

	fun switchDynamicColor(enabled: Boolean = !mutableAppSettingsStateFlow.value.isDynamicColorEnabled) {
		applicationScope.launch(Dispatchers.IO) {
			mutableAppSettingsStateFlow.update {
				it.copy(isDynamicColorEnabled = enabled)
			}
			kv.encode(DYNAMIC_COLOR, enabled)
		}
	}

	private const val TAG = "PreferenceUtil"
}

data class DarkThemePreference(
	val darkThemeValue: Int = FOLLOW_SYSTEM,
	val isHighContrastModeEnabled: Boolean = false
) {
	companion object {
		const val FOLLOW_SYSTEM = 1
		const val ON = 2
		const val OFF = 3
	}

	@Composable
	fun isDarkTheme(): Boolean {
		return if (darkThemeValue == FOLLOW_SYSTEM)
			isSystemInDarkTheme()
		else darkThemeValue == ON
	}

	@Composable
	fun getDarkThemeDesc(): String {
		return when (darkThemeValue) {
			FOLLOW_SYSTEM -> stringResource(R.string.follow_system)
			ON -> stringResource(R.string.on)
			else -> stringResource(R.string.off)
		}
	}
}