package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.AuditIndicatorStatus
import com.example.ui.theme.StatusColors

/**
 * Компактная карточка результата (балл, срок FIRE, статус). Цвет значения берётся
 * из [StatusColors]/[ScoreColorEvaluator] через переданный [status].
 */
@Composable
fun CompactResultCard(
    title: String,
    value: String,
    status: AuditIndicatorStatus? = null,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    testTag: String = "compact_result_card"
) {
    val valueColor = status?.let { StatusColors.getTextColor(it) } ?: MaterialTheme.colorScheme.onSurface
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag(testTag)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = valueColor,
                    fontWeight = FontWeight.Bold
                )
            )
            if (status != null) {
                StatusChip(status = status, label = status.name, testTag = "${testTag}_status")
            }
        }
    }
}
