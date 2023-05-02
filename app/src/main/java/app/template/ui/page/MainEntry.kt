package app.template.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import app.template.ui.common.LocalWindowWidthState
import app.template.ui.common.Route
import app.template.ui.common.animatedComposable
import app.template.ui.page.main.MainPage
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.Job

private const val TAG = "MainEntry"

@Composable
fun MainEntry() {
	val navController = rememberAnimatedNavController()
	val context = LocalContext.current
	val scope = rememberCoroutineScope()
	var updateJob: Job? = null

	val onBackPressed = { navController.popBackStack() }

	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.background)
	) {
		AnimatedNavHost(
			modifier = Modifier
				.fillMaxWidth(
					when (LocalWindowWidthState.current) {
						WindowWidthSizeClass.Compact -> 1f
						WindowWidthSizeClass.Expanded -> 0.5f
						else -> 0.8f
					}
				)
				.align(Alignment.Center),
			navController = navController,
			startDestination = Route.MAIN
		) {
			mainGraph(navController) { onBackPressed() }
		}
	}
}

fun NavGraphBuilder.mainGraph(
	navController: NavHostController,
	onBackPressed: () -> Unit = { navController.popBackStack() }
) {
	navigation(startDestination = Route.MAIN_PAGE, route = Route.MAIN) {
		animatedComposable(Route.MAIN_PAGE) {
			MainPage(navController)
		}
	}
}
