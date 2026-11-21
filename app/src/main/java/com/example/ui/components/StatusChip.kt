package com.example.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.domain.model.AuditIndicatorStatus
import com.example.ui.theme.StatusColors

/**
 * Семантический чип статуса. Цвет — только из [StatusColors] по [AuditIndicatorStatus]:
 * позитивный зелёный, нейтральный жёлтый, негативный красный. Текстовый оттенок
 * выбирается под светлую/тёмную тему и соответствует WCAG AA.
 */
@Composable
fun StatusChip(
    status: AuditIndicatorStatus,
    label: String = status.name,
    modifier: Modifier = Modifier,
    containerAlpha: Float = 0.12f,
    testTag: String = "status_chip"
) {
    val accent = StatusColors.getColor(status)
    val textColor = StatusColors.getTextColor(status)
    Surface(
        shape = RoundedCornerShape(50),
        color = accent.copy(alpha = containerAlpha),
        modifier = modifier.testTag(testTag)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = textColor,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
            ),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

/** Перегрузка для явного цвета акцента, если статус не передан (не меняет палитру). */
@Composable
fun StatusChip(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    testTag: String = "status_chip"
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.12f),
        modifier = modifier.testTag(testTag)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = color,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
            ),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
