package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.FlameStreakIcon
import com.example.ui.screens.community.CommunityScreen
import com.example.ui.screens.learn.LearnScreen
import com.example.ui.screens.learn.LessonScreen
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.screens.playground.PlaygroundScreen
import com.example.ui.screens.practice.BugHuntGameScreen
import com.example.ui.screens.practice.PracticeScreen
import com.example.ui.screens.profile.CertificateDialog
import com.example.ui.screens.profile.DailyGoalBottomSheet
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.CodeCraftViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: CodeCraftViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val user by viewModel.user.collectAsStateWithLifecycle()
            val darkMode = user?.darkMode != false

            CodeCraftTheme(darkTheme = darkMode) {
                CodeCraftApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CodeCraftApp(viewModel: CodeCraftViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val user by viewModel.user.collectAsStateWithLifecycle()
    val courses by viewModel.courses.collectAsStateWithLifecycle()
    val snippets by viewModel.snippets.collectAsStateWithLifecycle()
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val badges by viewModel.badges.collectAsStateWithLifecycle()

    val activeLesson by viewModel.activeLesson.collectAsStateWithLifecycle()
    val selectedOption by viewModel.selectedLessonOption.collectAsStateWithLifecycle()
    val reorderedLines by viewModel.reorderedLines.collectAsStateWithLifecycle()
    val typedAnswer by viewModel.typedAnswer.collectAsStateWithLifecycle()
    val answerStatus by viewModel.lessonAnswerState.collectAsStateWithLifecycle()
    val showByteHint by viewModel.showByteHintSheet.collectAsStateWithLifecycle()
    val showConfetti by viewModel.showConfetti.collectAsStateWithLifecycle()

    val activeBugHunt by viewModel.activeBugHunt.collectAsStateWithLifecycle()
    val showOnboardingFlow by viewModel.showOnboardingFlow.collectAsStateWithLifecycle()
    val showDailyGoalSheet by viewModel.showDailyGoalSheet.collectAsStateWithLifecycle()
    val selectedCertificate by viewModel.selectedCertificate.collectAsStateWithLifecycle()

    val playgroundLang by viewModel.playgroundLang.collectAsStateWithLifecycle()
    val playgroundCode by viewModel.playgroundCode.collectAsStateWithLifecycle()
    val playgroundTitle by viewModel.playgroundTitle.collectAsStateWithLifecycle()
    val executionResult by viewModel.executionResult.collectAsStateWithLifecycle()
    val isExecuting by viewModel.isExecuting.collectAsStateWithLifecycle()

    // Handle back button for modals
    if (activeLesson != null) {
        BackHandler { viewModel.closeLesson() }
    } else if (activeBugHunt != null) {
        BackHandler { viewModel.closeBugHunt() }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 600.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (!isWideScreen && activeLesson == null && activeBugHunt == null && !showOnboardingFlow) {
                    CodeCraftBottomNav(
                        currentTab = currentTab,
                        onSelectTab = { viewModel.switchTab(it) }
                    )
                }
            }
        ) { paddingValues ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        bottom = if (isWideScreen) paddingValues.calculateBottomPadding() else 0.dp
                    )
            ) {
                // Adaptive Navigation Rail for Expanded/Tablet screens
                if (isWideScreen && activeLesson == null && activeBugHunt == null && !showOnboardingFlow) {
                    CodeCraftNavRail(
                        currentTab = currentTab,
                        onSelectTab = { viewModel.switchTab(it) }
                    )
                }

                // Primary Content Viewport
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    when (currentTab) {
                        AppTab.LEARN -> {
                            LearnScreen(
                                user = user,
                                courses = courses,
                                onResumeLesson = {
                                    // Start next uncompleted lesson or first lesson
                                    val course = courses.firstOrNull { it.id == "course_python_101" } ?: courses.firstOrNull()
                                    course?.let {
                                        // Pick lesson 3 (conditional logic) or uncompleted
                                        val defaultLesson = com.example.data.local.InitialData.defaultLessons.find { l -> l.id == "py_lesson_3" }
                                            ?: com.example.data.local.InitialData.defaultLessons.first()
                                        viewModel.startLesson(defaultLesson)
                                    }
                                },
                                onSelectCourse = { course ->
                                    val lesson = com.example.data.local.InitialData.defaultLessons.find { it.courseId == course.id && !it.isCompleted }
                                        ?: com.example.data.local.InitialData.defaultLessons.firstOrNull { it.courseId == course.id }
                                    if (lesson != null) {
                                        viewModel.startLesson(lesson)
                                    }
                                },
                                onOpenDailyGoal = { viewModel.toggleDailyGoalSheet(true) }
                            )
                        }
                        AppTab.PRACTICE -> {
                            PracticeScreen(
                                user = user,
                                onStartBugHunt = { viewModel.startBugHuntGame() }
                            )
                        }
                        AppTab.PLAYGROUND -> {
                            PlaygroundScreen(
                                language = playgroundLang,
                                code = playgroundCode,
                                title = playgroundTitle,
                                executionResult = executionResult,
                                isExecuting = isExecuting,
                                savedSnippets = snippets,
                                onLanguageChange = { viewModel.setPlaygroundLanguage(it) },
                                onCodeChange = { viewModel.updatePlaygroundCode(it) },
                                onTitleChange = { viewModel.updatePlaygroundTitle(it) },
                                onRun = { viewModel.runPlaygroundCode() },
                                onSave = { viewModel.saveCurrentSnippet() },
                                onLoadSnippet = { viewModel.loadSnippet(it) },
                                onDeleteSnippet = { viewModel.deleteSnippet(it) }
                            )
                        }
                        AppTab.COMMUNITY -> {
                            CommunityScreen(
                                posts = posts,
                                onToggleLike = { viewModel.togglePostLike(it) },
                                onCreatePost = { content, code, tags, type ->
                                    viewModel.createPost(content, code, tags, type)
                                }
                            )
                        }
                        AppTab.PROFILE -> {
                            ProfileScreen(
                                user = user,
                                badges = badges,
                                onToggleDarkMode = { viewModel.toggleDarkMode(it) },
                                onOpenDailyGoalDialog = { viewModel.toggleDailyGoalSheet(true) },
                                onViewCertificate = { viewModel.showCertificate(it) }
                            )
                        }
                    }
                }
            }
        }

        // Full-screen Modal Overlays

        // 1. Active Interactive Lesson Modal
        if (activeLesson != null) {
            LessonScreen(
                lesson = activeLesson!!,
                selectedOption = selectedOption,
                reorderedLines = reorderedLines,
                typedAnswer = typedAnswer,
                answerStatus = answerStatus,
                showByteHint = showByteHint,
                showConfetti = showConfetti,
                hearts = user?.hearts ?: 5,
                onSelectOption = { viewModel.selectOption(it) },
                onTypedAnswerChange = { viewModel.setTypedAnswer(it) },
                onSwapLines = { from, to -> viewModel.swapReorderedLines(from, to) },
                onCheck = {
                    if (answerStatus == com.example.ui.viewmodel.AnswerStatus.CORRECT) {
                        viewModel.closeLesson()
                    } else {
                        viewModel.checkAnswer()
                    }
                },
                onClose = { viewModel.closeLesson() },
                onDismissHint = { viewModel.dismissByteHint() },
                onDismissConfetti = { viewModel.dismissConfetti() }
            )
        }

        // 2. Active Bug Hunt Game Modal
        if (activeBugHunt != null) {
            BugHuntGameScreen(
                game = activeBugHunt!!,
                onClose = { viewModel.closeBugHunt() }
            )
        }

        // 3. Onboarding Flow Modal
        if (showOnboardingFlow) {
            OnboardingScreen(
                onFinish = { goal, lang, level ->
                    viewModel.finishOnboarding(goal, lang, level)
                },
                onSkip = { viewModel.closeOnboarding() }
            )
        }

        // 4. Daily Goal XP Bottom Sheet
        if (showDailyGoalSheet) {
            DailyGoalBottomSheet(
                currentGoal = user?.dailyXpGoal ?: 50,
                onSelectGoal = { viewModel.setDailyGoal(it) },
                onDismiss = { viewModel.toggleDailyGoalSheet(false) }
            )
        }

        // 5. Certificate of Completion Dialog
        if (selectedCertificate != null) {
            CertificateDialog(
                certificate = selectedCertificate!!,
                onDismiss = { viewModel.dismissCertificate() }
            )
        }
    }
}

@Composable
fun CodeCraftBottomNav(
    currentTab: AppTab,
    onSelectTab: (AppTab) -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF13151F),
        tonalElevation = 8.dp,
        modifier = Modifier.border(1.dp, SurfaceBorderDark)
    ) {
        val items = listOf(
            Triple(AppTab.LEARN, "Learn", Icons.Outlined.School),
            Triple(AppTab.PRACTICE, "Practice", Icons.Outlined.SportsEsports),
            Triple(AppTab.PLAYGROUND, "Playground", Icons.Outlined.Terminal),
            Triple(AppTab.COMMUNITY, "Community", Icons.Outlined.People),
            Triple(AppTab.PROFILE, "Profile", Icons.Outlined.Person)
        )

        items.forEach { (tab, label, icon) ->
            val isSelected = currentTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onSelectTab(tab) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.5.sp,
                            fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                        )
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ElectricTeal,
                    selectedTextColor = ElectricTeal,
                    indicatorColor = Color(0xFF1E2638),
                    unselectedIconColor = TextSecondaryDark,
                    unselectedTextColor = TextSecondaryDark
                )
            )
        }
    }
}

@Composable
fun CodeCraftNavRail(
    currentTab: AppTab,
    onSelectTab: (AppTab) -> Unit
) {
    NavigationRail(
        containerColor = Color(0xFF13151F),
        modifier = Modifier.border(1.dp, SurfaceBorderDark)
    ) {
        val items = listOf(
            Triple(AppTab.LEARN, "Learn", Icons.Outlined.School),
            Triple(AppTab.PRACTICE, "Practice", Icons.Outlined.SportsEsports),
            Triple(AppTab.PLAYGROUND, "Playground", Icons.Outlined.Terminal),
            Triple(AppTab.COMMUNITY, "Community", Icons.Outlined.People),
            Triple(AppTab.PROFILE, "Profile", Icons.Outlined.Person)
        )

        Spacer(modifier = Modifier.height(16.dp))

        items.forEach { (tab, label, icon) ->
            val isSelected = currentTab == tab
            NavigationRailItem(
                selected = isSelected,
                onClick = { onSelectTab(tab) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label
                    )
                },
                label = { Text(text = label) },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = ElectricTeal,
                    selectedTextColor = ElectricTeal,
                    indicatorColor = Color(0xFF1E2638),
                    unselectedIconColor = TextSecondaryDark,
                    unselectedTextColor = TextSecondaryDark
                )
            )
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
