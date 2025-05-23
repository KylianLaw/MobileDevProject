package com.example.mobiledevproject

import android.content.SharedPreferences
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class LoginManagerTest {

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var loginManager: LoginManager

    @Before
    fun setup() {
        sharedPrefs = mock(SharedPreferences::class.java)
        editor = mock(SharedPreferences.Editor::class.java)

        `when`(sharedPrefs.getString("email", null)).thenReturn("test@example.com")
        `when`(sharedPrefs.getString("password", null)).thenReturn("password123")

        loginManager = LoginManager(sharedPrefs)
    }

    @Test
    fun `login returns true for correct credentials`() {
        val result = loginManager.login("test@example.com", "password123")
        assertTrue(result)
    }

    @Test
    fun `login returns false for incorrect credentials`() {
        val result = loginManager.login("wrong@example.com", "wrongpass")
        assertFalse(result)
    }
}
