package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.CalculatedMetrics
import com.example.ui.theme.StatusColors
import com.example.util.Formatters
import com.example.util.localization.LocalAppLanguage
import com.example.util.localization.LocalAppStrings

@Composable
internal fun FireSimulationSummaryPanel(
    metrics: CalculatedMetrics,
    simulatedMetrics: CalculatedMetrics,
    activeChanges: Int,
    onResetSimulation: () -> Unit
) {
    val strings = LocalAppStrings.current
    val display = Formatters.formatFireSimulationComparison(
        metrics, simulatedMetrics, activeChanges, LocalAppLanguage.current
    )
    val statusColor = StatusColors.getColor(display.status)
    val textColor = StatusColors.getTextColor(display.status)
    // Подложка покрывает также поля, зазоры и скруглённые углы внутренней панели.
    Surface(
        color = MaterialTheme.colorScheme.background.copy(alpha = 1f),
        modifier = Modifier.fillMaxWidth().testTag("fire_sim_sticky_summary")
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = statusColor.copy(alpha = 0.08f),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                .fillMaxWidth().testTag("fire_simulation_summary")
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = strings.fireSimSummaryTitle,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.semantics { heading() }
                )
                SummaryRow(strings.fireSimTimeLabel, display.years, "fire_sim_years")
                SummaryRow(strings.fireSimAgeLabel, display.age, "fire_sim_age")
                SummaryRow(strings.fireSimDeltaLabel, display.delta, "fire_sim_delta", textColor, statusColor)
                HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = strings.fireSimChangesCount(activeChanges),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(0.45f).alignByBaseline()
                            .padding(end = 8.dp).testTag("fire_sim_changes_count")
                    )
                    OutlinedButton(
                        onClick = onResetSimulation,
                        enabled = activeChanges > 0,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.weight(0.55f)
                            .heightIn(min = 48.dp)
                            .alignByBaseline()
                            .testTag("fire_sim_reset_button")
                    ) {
                        Text(strings.fireSimReset, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    tag: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    badgeColor: Color? = null
) {
    // Каждая пара растёт по высоте совместно, включая переносы при крупном шрифте.
    Row(modifier = Modifier.fillMaxWidth().semantics(mergeDescendants = true) {}) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(0.45f).alignByBaseline().padding(end = 8.dp).testTag("${tag}_label")
        )
        Surface(
            color = badgeColor?.copy(alpha = 0.12f) ?: Color.Transparent,
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.weight(0.55f).alignByBaseline()
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = valueColor,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 3.dp).testTag("${tag}_value")
            )
        }
    }
}
