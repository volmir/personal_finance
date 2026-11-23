package com.example

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.view.ContextThemeWrapper
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.compose.ui.graphics.toArgb
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.example.ui.theme.SlateDarkSurface
import com.example.ui.theme.SlateLightBackground
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35, 36])
class LaunchThemeRobolectricTest {
    @Test
    @Config(sdk = [24, 26, 27, 35, 36])
    fun `manifest launch theme matches Compose before activity creation in both modes`() {
        val app = ApplicationProvider.getApplicationContext<Context>()
        val info = app.packageManager.getActivityInfo(
            android.content.ComponentName(app, MainActivity::class.java), 0
        )
        assertEquals(app.applicationInfo.theme, info.themeResource)
        for (dark in listOf(false, true)) {
            val configuration = Configuration(app.resources.configuration).apply {
                uiMode = (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or
                    if (dark) Configuration.UI_MODE_NIGHT_YES else Configuration.UI_MODE_NIGHT_NO
            }
            val context = ContextThemeWrapper(app.createConfigurationContext(configuration), info.themeResource)
            val attributes = context.obtainStyledAttributes(intArrayOf(
                android.R.attr.windowBackground,
                android.R.attr.colorBackground,
                android.R.attr.statusBarColor,
                android.R.attr.navigationBarColor,
                android.R.attr.windowLightStatusBar,
                android.R.attr.windowLightNavigationBar,
                android.R.attr.windowActionBar
            ))
            try {
                val background = if (dark) SlateDarkSurface.toArgb() else SlateLightBackground.toArgb()
                assertEquals("Launch background, dark=$dark", background, attributes.getColor(0, 0))
                assertEquals(background, attributes.getColor(1, 0))
                assertEquals(Color.TRANSPARENT, attributes.getColor(2, Color.MAGENTA))
                assertEquals(Color.TRANSPARENT, attributes.getColor(3, Color.MAGENTA))
                assertEquals(!dark, attributes.getBoolean(4, dark))
                if (Build.VERSION.SDK_INT >= 27) {
                    assertEquals(!dark, attributes.getBoolean(5, dark))
                }
                assertEquals(false, attributes.getBoolean(6, true))
            } finally {
                attributes.recycle()
            }
        }
    }

    @Test
    fun `system bars follow system mode at launch and after recreation`() {
        for (dark in listOf(false, true)) {
            RuntimeEnvironment.setQualifiers(if (dark) "+night" else "+notnight")
            ActivityScenario.launch(MainActivity::class.java).use { scenario ->
                scenario.onActivity { assertSystemBars(it, dark) }
                RuntimeEnvironment.setQualifiers(if (dark) "+notnight" else "+night")
                scenario.recreate()
                scenario.onActivity { assertSystemBars(it, !dark) }
            }
        }
    }

    private fun assertSystemBars(activity: MainActivity, dark: Boolean) {
        val window = activity.window
        val appearance = window.insetsController!!.systemBarsAppearance
        assertEquals(!dark, appearance and WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS != 0)
        assertEquals(!dark, appearance and WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS != 0)
        assertEquals(Color.TRANSPARENT, window.statusBarColor)
        assertEquals(Color.TRANSPARENT, window.navigationBarColor)
        assertEquals(0, window.attributes.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN)
        if (Build.VERSION.SDK_INT >= 35) {
            assertEquals(WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS,
                window.attributes.layoutInDisplayCutoutMode)
        }
    }
}
