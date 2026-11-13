package com.example.ui.components

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.data.model.CalculatedMetrics
import com.example.data.model.FinancialInputData
import com.example.domain.AuditorAnalysisGenerator
import com.example.util.localization.LocalAppLanguage
import com.example.util.localization.LocalAppStrings

@Composable
fun ExportResultsButton(
    inputData: FinancialInputData,
    metrics: CalculatedMetrics,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lang = LocalAppLanguage.current
    val strings = LocalAppStrings.current

    SecondaryActionButton(
        text = strings.exportButtonText,
        onClick = {
            val exportText = AuditorAnalysisGenerator.generateExportSummary(inputData, metrics, lang)
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, exportText)
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(sendIntent, strings.exportChooserTitle))
        },
        icon = Icons.Default.Share,
        iconContentDescription = strings.exportButtonText,
        modifier = modifier
            .fillMaxWidth()
            .testTag("export_button")
    )
}
