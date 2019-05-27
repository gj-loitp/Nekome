package com.chesire.malime.flow.login

import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.chesire.malime.R
import com.chesire.malime.TestApplication
import com.chesire.malime.core.Resource
import com.chesire.malime.core.api.AuthApi
import com.chesire.malime.helpers.ToastMatcher.Companion.onToast
import com.schibsted.spain.barista.assertion.BaristaErrorAssertions.assertError
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo
import io.mockk.coEvery
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class LoginTests {
    @get:Rule
    val loginActivity = ActivityTestRule(LoginActivity::class.java, false, false)

    @Inject
    lateinit var auth: AuthApi

    @Before
    fun setUp() {
        val app =
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as TestApplication
        app.component.inject(this)
    }

    @Test
    fun emptyUsernameShowsError() {
        loginActivity.launchActivity(null)

        writeTo(R.id.fragmentDetailsUsernameText, "")
        writeTo(R.id.fragmentDetailsPasswordText, "Password")
        clickOn(R.id.fragmentDetailsLoginButton)

        assertError(R.id.fragmentDetailsUsernameLayout, R.string.login_error_empty_username)
    }

    @Test
    fun emptyPasswordShowsError() {
        loginActivity.launchActivity(null)

        writeTo(R.id.fragmentDetailsUsernameText, "Username")
        writeTo(R.id.fragmentDetailsPasswordText, "")
        clickOn(R.id.fragmentDetailsLoginButton)

        assertError(R.id.fragmentDetailsPasswordLayout, R.string.login_error_empty_password)
    }

    @Test
    fun invalidCredentialsShowsError() {
        coEvery {
            auth.login("Username", "Password")
        } coAnswers {
            Resource.Error("Unauthorized error", 401)
        }

        loginActivity.launchActivity(null)

        writeTo(R.id.fragmentDetailsUsernameText, "Username")
        writeTo(R.id.fragmentDetailsPasswordText, "Password")
        clickOn(R.id.fragmentDetailsLoginButton)

        onToast(R.string.login_error_credentials).check(matches(isDisplayed()))
    }

    @Test
    fun failureToLoginShowsError() {
        coEvery {
            auth.login("Username", "Password")
        } coAnswers {
            Resource.Error("Generic error", 0)
        }

        loginActivity.launchActivity(null)

        writeTo(R.id.fragmentDetailsUsernameText, "Username")
        writeTo(R.id.fragmentDetailsPasswordText, "Password")
        clickOn(R.id.fragmentDetailsLoginButton)

        onToast(R.string.login_error_generic).check(matches(isDisplayed()))
    }
}
