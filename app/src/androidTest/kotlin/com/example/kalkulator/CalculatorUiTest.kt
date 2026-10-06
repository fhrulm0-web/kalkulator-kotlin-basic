package com.example.kalkulator

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CalculatorUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testAdditionUiFlow() {
        composeTestRule.onNodeWithText("1").performClick()
        composeTestRule.onNodeWithText("5").performClick()
        composeTestRule.onNodeWithContentDescription("Plus").performClick()
        composeTestRule.onNodeWithText("2").performClick()
        composeTestRule.onNodeWithText("7").performClick()
        composeTestRule.onNodeWithContentDescription("Equals").performClick()
        composeTestRule.onNodeWithText("42").assertIsDisplayed()
    }

    @Test
    fun testClearResetsDisplay() {
        composeTestRule.onNodeWithText("9").performClick()
        composeTestRule.onNodeWithContentDescription("Clear all").performClick()
        composeTestRule.onNodeWithText("0").assertIsDisplayed()
    }

    @Test
    fun testParenthesesExpression() {
        composeTestRule.onNodeWithContentDescription("Open parenthesis").performClick()
        composeTestRule.onNodeWithText("2").performClick()
        composeTestRule.onNodeWithContentDescription("Plus").performClick()
        composeTestRule.onNodeWithText("3").performClick()
        composeTestRule.onNodeWithContentDescription("Close parenthesis").performClick()
        composeTestRule.onNodeWithContentDescription("Multiply").performClick()
        composeTestRule.onNodeWithText("4").performClick()
        composeTestRule.onNodeWithContentDescription("Equals").performClick()
        composeTestRule.onNodeWithText("20").assertIsDisplayed()
    }
}
