package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TrajectoryPoint
import com.example.ui.theme.AccentGold
import com.example.ui.theme.SuccessGreen
import com.example.util.Formatters

@Composable
fun FireTrajectoryChart(
    trajectory: List<TrajectoryPoint>,
    yearsToRetire: Double,
    currentAge: Int,
    currencySymbol: String = "₽",
    modifier: Modifier = Modifier
) {
    if (trajectory.isEmpty()) return

    val maxCapital = trajectory.maxOfOrNull { maxOf(it.accumulatedCapital, it.requiredCapital) } ?: 1.0
    val minAge = trajectory.first().age
    val maxAge = trajectory.last().age
    val retireAge = currentAge + yearsToRetire

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("fire_trajectory_chart")
    ) {
        // Legend
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(SuccessGreen, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Накопленный капитал",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(AccentGold, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Необходимый капитал",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
            val textColor = MaterialTheme.colorScheme.onSurfaceVariant

            Canvas(modifier = Modifier.matchParentSize()) {
                val w = size.width
                val h = size.height

                // Draw horizontal grid lines
                for (step in 1..3) {
                    val y = h * (step / 4f)
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                fun getX(age: Double): Float {
                    val ageRange = (maxAge - minAge).coerceAtLeast(1.0)
                    return (((age - minAge) / ageRange) * w).toFloat().coerceIn(0f, w)
                }

                fun getY(capital: Double): Float {
                    val capNorm = (capital / maxCapital).coerceIn(0.0, 1.0)
                    return (h - (capNorm * (h - 10f))).toFloat()
                }

                // Accumulated capital path
                val accPath = Path()
                trajectory.forEachIndexed { index, point ->
                    val x = getX(point.age)
                    val y = getY(point.accumulatedCapital)
                    if (index == 0) accPath.moveTo(x, y) else accPath.lineTo(x, y)
                }

                // Draw filled gradient under accumulated capital
                val fillPath = Path().apply {
                    addPath(accPath)
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                drawPath(
                    path = fillPath,
                    color = SuccessGreen.copy(alpha = 0.12f)
                )

                // Draw accumulated stroke
                drawPath(
                    path = accPath,
                    color = SuccessGreen,
                    style = Stroke(width = 3.dp.toPx())
                )

                // Required capital path
                val reqPath = Path()
                trajectory.forEachIndexed { index, point ->
                    val x = getX(point.age)
                    val y = getY(point.requiredCapital)
                    if (index == 0) reqPath.moveTo(x, y) else reqPath.lineTo(x, y)
                }
                drawPath(
                    path = reqPath,
                    color = AccentGold,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                    )
                )

                // Draw retirement vertical mark
                val retX = getX(retireAge)
                drawLine(
                    color = Color.White.copy(alpha = 0.6f),
                    start = Offset(retX, 0f),
                    end = Offset(retX, h),
                    strokeWidth = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                )

                // Highlight retirement point circle
                val retPoint = trajectory.minByOrNull { kotlin.math.abs(it.age - retireAge) }
                if (retPoint != null) {
                    val px = getX(retireAge)
                    val py = getY(retPoint.accumulatedCapital)
                    drawCircle(
                        color = SuccessGreen,
                        radius = 6.dp.toPx(),
                        center = Offset(px, py)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 3.dp.toPx(),
                        center = Offset(px, py)
                    )
                }
            }
        }

        // Axis labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, start = 4.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${minAge.toInt()} лет",
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "FIRE: ${Formatters.formatRatio(retireAge)} лет",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${maxAge.toInt()} лет",
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }
    }
}
