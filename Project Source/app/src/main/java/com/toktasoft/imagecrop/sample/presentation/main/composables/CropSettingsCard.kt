package com.toktasoft.imagecrop.sample.presentation.main.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.toktasoft.imagecrop.CropShape
import com.toktasoft.imagecrop.sample.R
import com.toktasoft.imagecrop.sample.presentation.main.MainContract.CropSettings
import kotlin.math.roundToInt

@Composable
fun CropSettingsCard(
    settings: CropSettings,
    onSettingsChange: (CropSettings) -> Unit,
    onReset: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        // No horizontal padding on the card: sections pad themselves so switch ripples span the full width.
        Column(
            modifier = Modifier.padding(vertical = 6.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .padding(start = 12.dp, end = 4.dp),
            ) {
                Text(
                    text = stringResource(R.string.settings_title),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                // Only offered when something differs from the defaults.
                AnimatedVisibility(
                    visible = settings != CropSettings(),
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    TextButton(
                        onClick = onReset,
                    ) {
                        Text(stringResource(R.string.settings_reset))
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 12.dp),
            ) {
                ChipRow(
                    title = stringResource(R.string.settings_offered_shapes),
                    entries = CropShape.entries,
                    isSelected = { it in settings.cropShapes },
                    label = { shapeLabel(it) },
                    onClick = { onSettingsChange(settings.toggleShape(it)) },
                )
                ChipRow(
                    title = stringResource(R.string.settings_initial_shape),
                    entries = settings.cropShapes,
                    isSelected = { it == settings.cropShape },
                    label = { shapeLabel(it) },
                    onClick = { onSettingsChange(settings.copy(cropShape = it)) },
                )
            }

            RowDivider()
            SwitchRow(
                title = stringResource(R.string.settings_show_shape_card),
                checked = settings.showCropShapeCard && settings.cropShapes.size > 1,
                enabled = settings.cropShapes.size > 1,
                onCheckedChange = { onSettingsChange(settings.copy(showCropShapeCard = it)) },
            )
            RowDivider()
            SwitchRow(
                title = stringResource(R.string.settings_show_guide_lines),
                checked = settings.showGuideLines,
                onCheckedChange = { onSettingsChange(settings.copy(showGuideLines = it)) },
            )
            RowDivider()
            SwitchRow(
                title = stringResource(R.string.settings_show_guide_lines_card),
                checked = settings.showGuideLinesCard,
                onCheckedChange = { onSettingsChange(settings.copy(showGuideLinesCard = it)) },
            )
            RowDivider()
            SwitchRow(
                title = stringResource(R.string.settings_show_use_full_image),
                checked = settings.showUseFullImage,
                onCheckedChange = { onSettingsChange(settings.copy(showUseFullImage = it)) },
            )
            RowDivider()

            Column(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 4.dp),
            ) {
                SliderRow(
                    title = stringResource(R.string.settings_max_output_size),
                    valueLabel = "${settings.maxOutputSize} px",
                    value = settings.maxOutputSize.toFloat(),
                    valueRange = 256f..4096f,
                    // 256 px steps between 256 and 4096
                    steps = 14,
                    onValueChange = { onSettingsChange(settings.copy(maxOutputSize = it.roundToInt())) },
                )
            }
        }
    }
}

@Composable
private fun RowDivider() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant,
        modifier = Modifier.padding(horizontal = 12.dp),
    )
}

@Composable
private fun shapeLabel(
    shape: CropShape,
): String = stringResource(
    when (shape) {
        CropShape.FREE -> R.string.shape_free
        CropShape.SQUARE -> R.string.shape_square
        CropShape.CIRCLE -> R.string.shape_circle
    }
)

@Composable
private fun <T> ChipRow(
    title: String,
    entries: List<T>,
    isSelected: (T) -> Boolean,
    label: @Composable (T) -> String,
    onClick: (T) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState()),
        ) {
            entries.forEach { entry ->
                FilterChip(
                    selected = isSelected(entry),
                    onClick = { onClick(entry) },
                    label = { Text(label(entry)) },
                )
            }
        }
    }
}

@Composable
private fun SwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
) {
    // The whole row toggles the switch; padding comes after toggleable so the ripple fills the row.
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Switch,
                onValueChange = onCheckedChange,
            )
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .heightIn(min = 48.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = checked,
            enabled = enabled,
            onCheckedChange = null,
        )
    }
}

@Composable
private fun SliderRow(
    title: String,
    valueLabel: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    steps: Int = 0,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = valueLabel,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Slider(
            value = value,
            valueRange = valueRange,
            steps = steps,
            onValueChange = onValueChange,
        )
    }
}
