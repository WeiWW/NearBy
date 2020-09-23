package com.ann.nearby

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.ann.nearby.ui.main.MainViewModel
import io.mockk.mockk
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest : KoinTest {
    @get:Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)

    @Rule
    @JvmField
    val grantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

    lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        viewModel = mockk(relaxed = true)
        loadKoinModules(module {
            viewModel {
                viewModel
            }
        })
    }

    @After
    fun tearDown() {
        stopKoin()
        activityTestRule.finishActivity()
    }

    @Test
    fun useAppContext() {
        activityTestRule.launchActivity(null)
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.ann.nearby", appContext.packageName)
    }

    @Test
    fun defaultView() {
        activityTestRule.launchActivity(null)
        onView(withId(R.id.venueCard)).check(
            matches(not(isDisplayed()))
        )
    }
}