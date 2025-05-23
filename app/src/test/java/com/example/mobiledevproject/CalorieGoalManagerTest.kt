package com.example.mobiledevproject

import android.content.SharedPreferences
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class CalorieGoalManagerTest {

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var goalManager: CalorieGoalManager

    @Before
    fun setup() {
        sharedPrefs = mock(SharedPreferences::class.java)
        editor = mock(SharedPreferences.Editor::class.java)

        `when`(sharedPrefs.edit()).thenReturn(editor)
        `when`(editor.putInt(anyString(), anyInt())).thenReturn(editor)

        goalManager = CalorieGoalManager(sharedPrefs)
    }

    @Test
    fun `setGoal saves value to sharedPrefs`() {
        goalManager.setGoal(2000)
        verify(editor).putInt("dailyGoal", 2000)
        verify(editor).apply()
    }

    @Test
    fun `getGoal retrieves value from sharedPrefs`() {
        `when`(sharedPrefs.getInt("dailyGoal", 0)).thenReturn(1800)
        val result = goalManager.getGoal()
        assert(result == 1800)
    }
}
