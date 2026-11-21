package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.AuditIndicatorStatus
import com.example.ui.theme.StatusColors

/**
 * Компактная строка показателя: название, значение, опциональный статусный чип.
 * Значение окрашивается через [StatusColors] при наличии статуса.
 */
@Composable
fun MetricSummaryRow(
    title: String,
    value: String,
    status: AuditIndicatorStatus? = null,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    testTag: String = "metric_summary_row"
) {
    val valueColor = status?.let { StatusColors.getTextColor(it) } ?: MaterialTheme.colorScheme.onSurface
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(vertical = 6.dp)
            .testTag(testTag)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = valueColor,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        if (status != null) {
            StatusChip(status = status, label = status.name, testTag = "${testTag}_status")
        }
    }
}
