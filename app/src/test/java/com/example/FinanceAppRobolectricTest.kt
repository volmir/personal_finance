package com.example

import android.app.Application
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ApplicationProvider
import com.example.ui.FinanceApp
import com.example.ui.MainViewModel
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FinanceAppRobolectricTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `resuming FinanceApp recalculates age after birthday without editing input`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        app.getSharedPreferences("user_finance_data", Context.MODE_PRIVATE).edit().clear().commit()
        var currentDate = LocalDate.of(2026, 10, 31)
        val viewModel = MainViewModel(app) { currentDate }
        viewModel.updateInputData { it.copy(birthDate = "1988-11-01") }
        viewModel.flushPendingSave()
        val input = viewModel.inputData.value
        composeRule.setContent { FinanceApp(viewModel) }
        composeRule.runOnIdle { assertEquals(37, viewModel.metrics.value.currentAge) }

        composeRule.activityRule.scenario.moveToState(Lifecycle.State.CREATED)
        currentDate = currentDate.plusDays(1)
        composeRule.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)

        composeRule.runOnIdle {
            assertEquals(38, viewModel.metrics.value.currentAge)
            assertEquals(38, viewModel.simulatedMetrics.value.currentAge)
            assertEquals(input, viewModel.inputData.value)
        }
    }
}
