package ru.netology.testing.uiautomator

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


const val SETTINGS_PACKAGE = "com.android.settings"
const val MODEL_PACKAGE = "ru.netology.testing.uiautomator"

const val TIMEOUT = 5000L

@RunWith(AndroidJUnit4::class)
class ChangeTextTest {

    private lateinit var device: UiDevice
    private val textToSet = "Netology"

    private fun waitForPackage(packageName: String) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        context.startActivity(intent)
        device.wait(Until.hasObject(By.pkg(packageName)), TIMEOUT)
    }

    @Before
    fun beforeEachTest() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressHome()

        val launcherPackage = device.launcherPackageName
        device.wait(Until.hasObject(By.pkg(launcherPackage)), TIMEOUT)
    }

    @Test
    fun testInternetSettings() {
        waitForPackage(SETTINGS_PACKAGE)

        device.findObject(
            UiSelector().resourceId("android:id/title").instance(0)
        ).click()
    }

    @Test
    fun testChangeText() {
        val packageName = MODEL_PACKAGE
        waitForPackage(packageName)

        device.findObject(By.res(packageName, "userInput")).text = textToSet
        device.findObject(By.res(packageName, "buttonChange")).click()

        val result = device.findObject(By.res(packageName, "textToBeChanged")).text
        assertEquals(result, textToSet)
    }

    @Test
    fun testEmptyStringInput() {
        val packageName = MODEL_PACKAGE
        waitForPackage(packageName)

        val initialText = device
            .findObject(By.res(packageName, "textToBeChanged"))
            .text ?: ""

        device.findObject(By.res(packageName, "userInput")).text = "   "
        device.findObject(By.res(packageName, "buttonChange")).click()

        val result = device
            .findObject(By.res(packageName, "textToBeChanged"))
            .text ?: ""

        assertEquals(
            "Текст не должен меняться при вводе пустой строки",
            initialText,
            result
        )
    }

    @Test
    fun testOpenTextInNewActivity() {
        val packageName = MODEL_PACKAGE
        waitForPackage(packageName)

        val textToPass = "NetologySecond"

        device.findObject(By.res(packageName, "userInput")).text = textToPass
        device.findObject(By.res(packageName, "buttonActivity")).click()

        val newActivityTextView = device.wait(
            Until.findObject(By.res(packageName, "text")),
            TIMEOUT
        )

        assertEquals(
            "Текст во второй Activity должен совпадать с введённым",
            textToPass,
            newActivityTextView.text
        )
    }
}



