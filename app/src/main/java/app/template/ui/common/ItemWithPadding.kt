package app.template.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ItemWithPadding(
	content: @Composable () -> Unit = {}
) {
	Box(
		modifier = Modifier
			.padding(horizontal = 16.dp)
			.padding(top = 12.dp)
	) {
		content()
	}
}