package com.example

import androidx.test.core.app.ActivityScenario
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.example.data.repository.LocalPreferencesManager
import com.example.ui.MainViewModel
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.util.ReflectionHelpers

@RunWith(RobolectricTestRunner::class)
class MainActivityRobolectricTest {
    @Before
    fun resetViewModelFactory() {
        // AndroidX caches Application, while Robolectric creates a new one for each test.
        ReflectionHelpers.setStaticField(ViewModelProvider.AndroidViewModelFactory::class.java, "_instance", null)
    }

    @Test
    fun `stopping activity saves pending input immediately`() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            lateinit var viewModel: MainViewModel
            lateinit var app: android.app.Application
            scenario.onActivity { activity ->
                app = activity.application
                viewModel = ViewModelProvider(activity)[MainViewModel::class.java]
                org.junit.Assert.assertSame(app, viewModel.getApplication<android.app.Application>())
                viewModel.updateInputData { it.copy(monthlyIncome = 12_000.0) }
            }

            scenario.moveToState(Lifecycle.State.CREATED)

            assertEquals(viewModel.inputData.value, LocalPreferencesManager(app).loadData())
        }
    }

    @Test
    fun `main activity launches without crash`() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                org.junit.Assert.assertNotNull(activity)
            }
        }
    }
}
