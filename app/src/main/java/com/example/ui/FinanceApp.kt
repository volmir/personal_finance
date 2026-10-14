package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Equalizer
import androidx.compose.material.icons.outlined.Grade
import androidx.compose.material.icons.outlined.Whatshot
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AuditScoreScreen
import com.example.ui.screens.FireRetirementScreen
import com.example.ui.screens.InputDataScreen
import com.example.ui.screens.MetricsScreen
import com.example.ui.theme.AlertRed
import com.example.ui.theme.ScoreColorEvaluator
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningYellow
import com.example.util.Formatters
import com.example.util.localization.LocalAppLanguage
import com.example.util.localization.LocalAppStrings
import com.example.util.localization.getAppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceApp(viewModel: MainViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val inputData by viewModel.inputData.collectAsStateWithLifecycle()
    val metrics by viewModel.metrics.collectAsStateWithLifecycle()
    val simulatedMetrics by viewModel.simulatedMetrics.collectAsStateWithLifecycle()
    val simulationState by viewModel.fireSimulation.collectAsStateWithLifecycle()
    val trajectory by viewModel.trajectory.collectAsStateWithLifecycle()
    val simulatedTrajectory by viewModel.simulatedTrajectory.collectAsStateWithLifecycle()
    val savedScenarios by viewModel.savedScenarios.collectAsStateWithLifecycle()

    val strings = getAppStrings(appLanguage)
    val tabs = AppTab.values()

    CompositionLocalProvider(
        LocalAppLanguage provides appLanguage,
        LocalAppStrings provides strings
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Whatshot,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = strings.appName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    },
                    actions = {
                        // Quick Score Pill
                        val totalScore = metrics.scores.totalScore
                        val totalScoreColor = ScoreColorEvaluator.getTotalScoreColor(totalScore)
                        val totalScoreBg = ScoreColorEvaluator.getTotalScoreContainerColor(totalScore)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = totalScoreBg,
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .testTag("topbar_score_badge")
                        ) {
                            Text(
                                text = "${Formatters.formatScore(totalScore, lang = appLanguage)} / 10",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = totalScoreColor
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    tabs.forEach { tab ->
                        val selected = currentTab == tab
                        val (filledIcon, outlinedIcon) = when (tab) {
                            AppTab.INPUT -> Icons.Filled.Calculate to Icons.Outlined.Calculate
                            AppTab.METRICS -> Icons.Filled.Equalizer to Icons.Outlined.Equalizer
                            AppTab.SCORES -> Icons.Filled.Grade to Icons.Outlined.Grade
                            AppTab.FIRE -> Icons.Filled.Whatshot to Icons.Outlined.Whatshot
                        }

                        NavigationBarItem(
                            selected = selected,
                            onClick = { viewModel.selectTab(tab) },
                            icon = {
                                Icon(
                                    imageVector = if (selected) filledIcon else outlinedIcon,
                                    contentDescription = tab.localizedTitle(strings)
                                )
                            },
                            label = {
                                Text(
                                    text = tab.localizedShortTitle(strings),
                                    fontSize = 11.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Crossfade(
                targetState = currentTab,
                label = "tab_crossfade",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) { activeTab ->
                when (activeTab) {
                    AppTab.INPUT -> InputDataScreen(
                        inputData = inputData,
                        metrics = metrics,
                        onUpdate = { transform -> viewModel.updateInputData(transform) },
                        onLoadRussiaAverage = { viewModel.loadRussiaAverage() },
                        onClear = { viewModel.clearData() },
                        onNavigateToMetrics = { viewModel.selectTab(AppTab.METRICS) }
                    )

                    AppTab.METRICS -> MetricsScreen(
                        inputData = inputData,
                        metrics = metrics
                    )

                    AppTab.SCORES -> AuditScoreScreen(
                        inputData = inputData,
                        metrics = metrics
                    )

                    AppTab.FIRE -> FireRetirementScreen(
                        inputData = inputData,
                        metrics = metrics,
                        simulatedMetrics = simulatedMetrics,
                        simulationState = simulationState,
                        trajectory = if (simulationState.isSimulating) simulatedTrajectory else trajectory,
                        onUpdateSimulation = { extraSavings, returnDelta, expReduction ->
                            viewModel.updateSimulation(extraSavings, returnDelta, expReduction)
                        },
                        onToggleScenario = { scenarioId ->
                            viewModel.toggleScenario(scenarioId)
                        },
                        onResetSimulation = { viewModel.resetSimulation() }
                    )
                }
            }
        }
    }
}
