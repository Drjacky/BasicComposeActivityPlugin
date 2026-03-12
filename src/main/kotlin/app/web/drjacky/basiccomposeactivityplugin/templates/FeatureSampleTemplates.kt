package app.web.drjacky.basiccomposeactivityplugin.templates

fun featureSampleBuildGradleKts(packageName: String): String = """
plugins {
    alias(libs.plugins.app.android.library)
    alias(libs.plugins.app.android.library.compose)
    alias(libs.plugins.app.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.app.kotlin.serialization)
}

android {
    namespace = "$packageName.feature.sample"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:common"))
    implementation(project(":core:ui"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.java.inject)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.text)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.animation.graphics)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.coil.compose)

    testImplementation(libs.arch.core.testing)
    androidTestImplementation(libs.test.runner)
    androidTestImplementation(libs.test.rules)
    androidTestImplementation(libs.test.core)
    androidTestImplementation(libs.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
""".trimIndent()

fun sampleDestination(packageName: String): String = """
package $packageName.feature.sample.navigation

import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Immutable
sealed interface SampleDestination : NavKey {
    @Serializable
    data object SampleList : SampleDestination

    @Serializable
    data object AddSample : SampleDestination
}
""".trimIndent()

fun sampleNavigator(packageName: String): String = """
package $packageName.feature.sample.navigation

interface SampleNavigator {
    fun navigateToAddSample()
    fun navigateBack()
}
""".trimIndent()

fun sampleGraph(packageName: String): String = """
package $packageName.feature.sample.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import $packageName.feature.sample.SampleViewModel
import $packageName.feature.sample.add.AddSampleRoute
import $packageName.feature.sample.list.SampleListRoute
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

fun EntryProviderScope<NavKey>.sampleGraph(
    navigator: SampleNavigator,
    sharedViewModel: @Composable () -> SampleViewModel,
) {
    entry<SampleDestination.SampleList> {
        val viewModel = sharedViewModel()
        val items by viewModel.items.collectAsStateWithLifecycle()
        SampleListRoute(
            items = items,
            onAddClick = { navigator.navigateToAddSample() },
        )
    }
    entry<SampleDestination.AddSample> {
        val viewModel = sharedViewModel()
        AddSampleRoute(
            onAddItem = { title ->
                viewModel.addItem(title)
                navigator.navigateBack()
            },
            onBackClick = { navigator.navigateBack() },
        )
    }
}
""".trimIndent()

fun sampleListScreen(packageName: String): String = """
package $packageName.feature.sample.list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import $packageName.core.domain.entity.SampleItem
import $packageName.feature.sample.R

@Composable
fun SampleListRoute(
    items: List<SampleItem>,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SampleListScreen(
        items = items,
        onAddClick = onAddClick,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleListScreen(
    items: List<SampleItem>,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.sample_list_title),
                        modifier = Modifier.semantics { heading() },
                    )
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.sample_add_item),
                )
            }
        },
        modifier = modifier,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            items(
                items = items,
                key = { it.id },
            ) { item ->
                SampleItemRow(item = item)
            }
        }
    }
}

@Preview
@Composable
private fun SampleListScreenPreview() {
    SampleListScreen(
        items = listOf(
            SampleItem(id = "1", title = "First item"),
            SampleItem(id = "2", title = "Second item"),
        ),
        onAddClick = {},
    )
}
""".trimIndent()

fun sampleItemRow(packageName: String): String = """
package $packageName.feature.sample.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import $packageName.core.domain.entity.SampleItem

@Composable
fun SampleItemRow(
    item: SampleItem,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .semantics(mergeDescendants = true) {},
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = item.id,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Preview
@Composable
private fun SampleItemRowPreview() {
    SampleItemRow(
        item = SampleItem(id = "1", title = "Sample item"),
    )
}
""".trimIndent()

fun addSampleScreen(packageName: String): String = """
package $packageName.feature.sample.add

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import $packageName.feature.sample.R

@Composable
fun AddSampleRoute(
    onAddItem: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AddSampleScreen(
        onAddItem = onAddItem,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSampleScreen(
    onAddItem: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var text by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.sample_add_title),
                        modifier = Modifier.semantics { heading() },
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(
                                $packageName.core.ui.R.string.content_description_back,
                            ),
                        )
                    }
                },
            )
        },
        modifier = modifier,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(stringResource(R.string.sample_item_title_hint)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (text.isNotBlank()) {
                        onAddItem(text.trim())
                    }
                },
                enabled = text.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.sample_add_button))
            }
        }
    }
}

@Preview
@Composable
private fun AddSampleScreenPreview() {
    AddSampleScreen(
        onAddItem = {},
        onBackClick = {},
    )
}
""".trimIndent()

fun sampleViewModel(packageName: String): String = """
package $packageName.feature.sample

import androidx.lifecycle.ViewModel
import $packageName.core.domain.entity.SampleItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SampleViewModel @Inject constructor() : ViewModel() {

    private val _items = MutableStateFlow(defaultItems())
    val items: StateFlow<List<SampleItem>> = _items.asStateFlow()

    fun addItem(title: String) {
        _items.update { currentItems ->
            currentItems + SampleItem(
                id = UUID.randomUUID().toString(),
                title = title,
            )
        }
    }

    private fun defaultItems(): List<SampleItem> = listOf(
        SampleItem(id = UUID.randomUUID().toString(), title = "Learn Jetpack Compose"),
        SampleItem(id = UUID.randomUUID().toString(), title = "Set up Hilt DI"),
        SampleItem(id = UUID.randomUUID().toString(), title = "Explore Navigation 3"),
        SampleItem(id = UUID.randomUUID().toString(), title = "Write unit tests"),
        SampleItem(id = UUID.randomUUID().toString(), title = "Configure detekt"),
    )
}
""".trimIndent()

fun featureSampleStringsXml(): String = """
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="sample_list_title">Samples</string>
    <string name="sample_add_title">Add Sample</string>
    <string name="sample_add_item">Add item</string>
    <string name="sample_add_button">Add</string>
    <string name="sample_item_title_hint">Title</string>
</resources>
""".trimIndent()
