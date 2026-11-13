package com.example

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import com.example.ui.AppTab
import com.example.ui.FinanceApp
import com.example.ui.MainViewModel
import com.example.ui.theme.MyApplicationTheme
import java.io.File
import java.time.LocalDate
import java.util.Locale
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Opt in with GENERATE_STORE_SCREENSHOTS=1; captures the real Compose UI without an emulator. */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w360dp-h800dp-xxhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class StoreScreenshotsTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun captureLocalizedStoreScreenshots() {
        assumeTrue(System.getenv("GENERATE_STORE_SCREENSHOTS") == "1")
        val root = generateSequence(File(requireNotNull(System.getProperty("user.dir")))) { it.parentFile }
            .first { File(it, "settings.gradle.kts").isFile }
        val app = ApplicationProvider.getApplicationContext<Application>()
        app.getSharedPreferences("user_finance_data", Context.MODE_PRIVATE).edit().clear().commit()
        val viewModel = MainViewModel(app) { LocalDate.of(2026, 11, 2) }
        viewModel.dismissSampleDataNotice()
        var selectedLocale by mutableStateOf("ru-RU")
        var contentVersion by mutableStateOf(0)
        composeRule.setContent {
            val configuration = Configuration(LocalConfiguration.current).apply {
                setLocale(Locale.forLanguageTag(selectedLocale))
            }
            CompositionLocalProvider(LocalConfiguration provides configuration) {
                key(contentVersion) {
                    MyApplicationTheme(darkTheme = false) {
                        FinanceApp(viewModel)
                    }
                }
            }
        }
        composeRule.waitForIdle()

        val locales = listOf("ru-RU", "en-US", "de-DE", "es-ES", "fr-FR", "it-IT")
        val tabs = listOf(
            AppTab.INPUT to "1-input.png",
            AppTab.METRICS to "2-metrics.png",
            AppTab.SCORES to "3-audit.png",
            AppTab.FIRE to "4-fire.png"
        )
        try {
            for (locale in locales) {
                composeRule.runOnIdle {
                    selectedLocale = locale
                    viewModel.updateLanguageFromSystem(locale.substringBefore('-'))
                }
                val destination = File(root, "fastlane/metadata/android/$locale/images/phoneScreenshots")
                assertTrue(destination.isDirectory || destination.mkdirs())
                for ((tab, filename) in tabs) {
                    composeRule.runOnIdle {
                        viewModel.selectTab(tab)
                        contentVersion++
                    }
                    composeRule.mainClock.advanceTimeBy(1_000)
                    composeRule.waitForIdle()
                    val bitmap = composeRule.runOnIdle {
                        val view = composeRule.activity.findViewById<View>(android.R.id.content)
                        Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888).also {
                            view.draw(Canvas(it))
                        }
                    }
                    assertTrue(bitmap.width >= 320 && bitmap.height > bitmap.width)
                    File(destination, filename).outputStream().use {
                        assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it))
                    }
                }
            }
        } finally {
            composeRule.runOnIdle { viewModel.flushPendingSave() }
        }
    }
}
