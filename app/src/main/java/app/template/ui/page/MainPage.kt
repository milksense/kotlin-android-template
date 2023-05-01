package app.template.ui.page

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.template.R
import app.template.ui.common.ItemWithPadding
import app.template.ui.theme.ShapeMedium

private const val TAG = "MainPage"

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MainPage(navController: NavController) {
	val context = LocalContext.current
	val scope = rememberCoroutineScope()

	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
	val fraction =
		CubicBezierEasing(1f, 0f, .8f, .4f).transform(scrollBehavior.state.overlappedFraction)

	Scaffold(
		modifier = Modifier
			.fillMaxSize()
			.nestedScroll(scrollBehavior.nestedScrollConnection),
		topBar = {
			TopAppBar(
				title = {
					Text(
						stringResource(id = R.string.app_name),
						maxLines = 1,
						overflow = TextOverflow.Ellipsis
					)
				},
				navigationIcon = {
					IconButton(onClick = { /* doSomething() */ }) {
						Icon(
							imageVector = Icons.Filled.Menu,
							contentDescription = "Localized description"
						)
					}
				},
				actions = {
					IconButton(onClick = { /* doSomething() */ }) {
						Icon(
							imageVector = Icons.Filled.Favorite,
							contentDescription = "Localized description"
						)
					}
				}
			)
		},
		floatingActionButton = {
			FloatingActionButton(onClick = { /*TODO*/ }) {
				Icon(Icons.Filled.Add, contentDescription = "Localized description")
			}
		}
	) {
		LazyColumn(
			modifier = Modifier.padding(it),
		) {
			items(10) {
				ItemWithPadding {
					SimpleCard()
				}
			}
		}
	}
}

// TODO: Remove me
@Composable
fun SimpleCard() {
	ElevatedCard(
		modifier = Modifier
			.clip(ShapeMedium),
		shape = MaterialTheme.shapes.small,
		onClick = { }
	) {
		Column(
			verticalArrangement = Arrangement.spacedBy(2.dp),
			modifier = Modifier
				.requiredHeightIn(min = 100.dp),
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically
			) {
				Column(
					verticalArrangement = Arrangement.spacedBy(4.dp),
					modifier = Modifier
						.weight(1f, fill = true),
				) {
					Text(
						modifier = Modifier
							.padding(start = 8.dp, end = 8.dp, top = 8.dp)
							.fillMaxWidth(),
						text = "Title",
						style = MaterialTheme.typography.titleMedium,
						color = MaterialTheme.colorScheme.onSurface,
						maxLines = 2,
						overflow = TextOverflow.Ellipsis,
					)
					Box {
						Row(
							horizontalArrangement = Arrangement.SpaceBetween,
							modifier = Modifier
								.fillMaxWidth(),
						) {
							Text(
								modifier = Modifier
									.fillMaxWidth()
									.padding(start = 8.dp, end = 8.dp),
								text = "Awesome description",
								style = MaterialTheme.typography.bodyMedium,
								color = MaterialTheme.colorScheme.onSurface,
								maxLines = 1,
								overflow = TextOverflow.Ellipsis,
							)
						}
						Spacer(
							modifier = Modifier
								.fillMaxWidth()
								.height(8.dp)
						)
					}
				}
			}
		}
	}
}