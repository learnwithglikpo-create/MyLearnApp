package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("CodeCraft", appName)
  }

  @Test
  fun `verify initial course catalog loading`() {
    val courses = com.example.data.local.InitialData.defaultCourses
    assertEquals(4, courses.size)
    assertEquals("Python Fundamentals", courses.first().title)
  }
}
