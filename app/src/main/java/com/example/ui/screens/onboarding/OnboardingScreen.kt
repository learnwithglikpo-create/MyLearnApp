package com.example.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ByteMascot
import com.example.ui.components.CodeCraftButton
import com.example.ui.components.MascotMood
import com.example.ui.theme.*

@Composable
fun OnboardingScreen(
    onFinish: (goal: String, language: String, level: String) -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(1) }
    var selectedGoal by remember { mutableStateOf("Build Websites") }
    var selectedLanguage by remember { mutableStateOf("Python") }
    var selectedLevel by remember { mutableStateOf("Some experience") }

    val goals = listOf(
        Pair("Build Websites", "HTML, CSS, JS apps"),
        Pair("Analyze Data", "Python & SQL metrics"),
        Pair("Get a Dev Job", "Full portfolio mastery"),
        Pair("Just Curious", "Learn for fun & hobby")
    )

    val languages = listOf("Python", "JavaScript", "HTML/CSS", "SQL", "Java", "C#", "Data Analytics")
    val levels = listOf(
        Pair("Brand new", "Never written a single line of code before"),
        Pair("Some experience", "Know basics like variables and simple loops"),
        Pair("I code regularly", "Looking to sharpen skills or learn a new language")
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(CharcoalBg, SurfaceDark)
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Navigation & Step Indicator
            if (step > 1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { if (step > 1) step-- },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    // Progress Dots
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (i in 1..5) {
                            Box(
                                modifier = Modifier
                                    .size(if (i == step) 18.dp else 8.dp, 8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (i <= step) ElectricTeal else Color(0xFF2C3246))
                            )
                        }
                    }

                    TextButton(onClick = onSkip) {
                        Text(
                            text = "Skip",
                            color = TextSecondaryDark,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Screen Content by Step
            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left) togetherWith
                            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
                },
                modifier = Modifier.weight(1f),
                label = "onboarding_steps"
            ) { targetStep ->
                when (targetStep) {
                    1 -> WelcomeStep(onStart = { step = 2 }, onSignIn = onSkip)
                    2 -> GoalStep(
                        goals = goals,
                        selectedGoal = selectedGoal,
                        onSelect = { selectedGoal = it }
                    )
                    3 -> LanguageStep(
                        languages = languages,
                        selectedLanguage = selectedLanguage,
                        onSelect = { selectedLanguage = it }
                    )
                    4 -> LevelStep(
                        levels = levels,
                        selectedLevel = selectedLevel,
                        onSelect = { selectedLevel = it }
                    )
                    5 -> AccountStep(
                        onComplete = { onFinish(selectedGoal, selectedLanguage, selectedLevel) },
                        onGuest = onSkip
                    )
                }
            }

            // Bottom CTA for Steps 2..4
            if (step in 2..4) {
                CodeCraftButton(
                    text = "Continue",
                    onClick = { step++ },
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun WelcomeStep(onStart: () -> Unit, onSignIn: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ByteMascot(
            size = 130.dp,
            mood = MascotMood.WAVING
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Learn to Code,\nOne Snippet at a Time.",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Interactive bite-sized lessons, live code playground, and gamified practice right in your pocket.",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = TextSecondaryDark,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        CodeCraftButton(
            text = "Get Started",
            onClick = onStart,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onSignIn) {
            Text(
                text = "I already have an account",
                color = ElectricTeal,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun GoalStep(
    goals: List<Pair<String, String>>,
    selectedGoal: String,
    onSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "What is your main goal?",
            style = MaterialTheme.typography.headlineMedium.copy(color = Color.White)
        )
        Text(
            text = "We will tailor your learning path and daily challenges.",
            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondaryDark),
            modifier = Modifier.padding(top = 6.dp, bottom = 24.dp)
        )

        goals.forEach { (title, subtitle) ->
            val isSelected = selectedGoal == title
            val border = if (isSelected) ElectricTeal else SurfaceBorderDark
            val bg = if (isSelected) Color(0xFF1E2838) else SurfaceDark

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(bg)
                    .border(if (isSelected) 2.dp else 1.dp, border, RoundedCornerShape(16.dp))
                    .clickable { onSelect(title) }
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(ElectricTeal),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = CharcoalBg,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageStep(
    languages: List<String>,
    selectedLanguage: String,
    onSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Choose your first language",
            style = MaterialTheme.typography.headlineMedium.copy(color = Color.White)
        )
        Text(
            text = "You can always explore more languages in the course catalog anytime.",
            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondaryDark),
            modifier = Modifier.padding(top = 6.dp, bottom = 24.dp)
        )

        languages.chunked(2).forEach { rowPair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowPair.forEach { lang ->
                    val isSelected = selectedLanguage == lang
                    val border = if (isSelected) ElectricTeal else SurfaceBorderDark
                    val bg = if (isSelected) Color(0xFF1E2838) else SurfaceDark

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(bg)
                            .border(if (isSelected) 2.dp else 1.dp, border, RoundedCornerShape(16.dp))
                            .clickable { onSelect(lang) }
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) DeepIndigo else Color(0xFF262C3E)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = lang.take(2).uppercase(),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) ElectricTeal else Color.White
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = lang,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
                if (rowPair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun LevelStep(
    levels: List<Pair<String, String>>,
    selectedLevel: String,
    onSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Your coding experience",
            style = MaterialTheme.typography.headlineMedium.copy(color = Color.White)
        )
        Text(
            text = "Helps us calibrate lesson pacing and starting point.",
            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondaryDark),
            modifier = Modifier.padding(top = 6.dp, bottom = 24.dp)
        )

        levels.forEach { (title, subtitle) ->
            val isSelected = selectedLevel == title
            val border = if (isSelected) ElectricTeal else SurfaceBorderDark
            val bg = if (isSelected) Color(0xFF1E2838) else SurfaceDark

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(bg)
                    .border(if (isSelected) 2.dp else 1.dp, border, RoundedCornerShape(16.dp))
                    .clickable { onSelect(title) }
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = { onSelect(title) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = ElectricTeal,
                        unselectedColor = TextSecondaryDark
                    )
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AccountStep(onComplete: () -> Unit, onGuest: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ByteMascot(
            size = 100.dp,
            mood = MascotMood.CHEERING
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Create Your Account",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = "Sync your streak, badges, and code snippets across all devices.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextSecondaryDark,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(top = 6.dp, bottom = 28.dp)
        )

        CodeCraftButton(
            text = "Continue with Google",
            onClick = onComplete,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        CodeCraftButton(
            text = "Start in Guest Mode",
            onClick = onGuest,
            isOutlined = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
