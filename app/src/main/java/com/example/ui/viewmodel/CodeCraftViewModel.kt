package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CodeCraftDatabase
import com.example.data.model.*
import com.example.data.repository.CodeCraftRepository
import com.example.engine.CodeSandboxEngine
import com.example.engine.ExecutionResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppTab {
    LEARN,
    PRACTICE,
    PLAYGROUND,
    COMMUNITY,
    PROFILE
}

class CodeCraftViewModel(application: Application) : AndroidViewModel(application) {
    private val database = CodeCraftDatabase.getDatabase(application, viewModelScope)
    val repository = CodeCraftRepository(database)
    private val sandboxEngine = CodeSandboxEngine(application)

    // Current Navigation Tab
    private val _currentTab = MutableStateFlow(AppTab.LEARN)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    // User State from Room
    val user: StateFlow<UserEntity?> = repository.user.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    // Courses & Badges from Room
    val courses: StateFlow<List<CourseEntity>> = repository.courses.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val snippets: StateFlow<List<SnippetEntity>> = repository.snippets.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val posts: StateFlow<List<CommunityPostEntity>> = repository.posts.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val badges: StateFlow<List<BadgeEntity>> = repository.badges.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Active Lesson Flow State
    private val _activeLesson = MutableStateFlow<LessonEntity?>(null)
    val activeLesson: StateFlow<LessonEntity?> = _activeLesson.asStateFlow()

    private val _selectedLessonOption = MutableStateFlow("")
    val selectedLessonOption: StateFlow<String> = _selectedLessonOption.asStateFlow()

    private val _reorderedLines = MutableStateFlow<List<String>>(emptyList())
    val reorderedLines: StateFlow<List<String>> = _reorderedLines.asStateFlow()

    private val _typedAnswer = MutableStateFlow("")
    val typedAnswer: StateFlow<String> = _typedAnswer.asStateFlow()

    private val _lessonAnswerState = MutableStateFlow<AnswerStatus>(AnswerStatus.IDLE)
    val lessonAnswerState: StateFlow<AnswerStatus> = _lessonAnswerState.asStateFlow()

    private val _wrongAttemptsCount = MutableStateFlow(0)
    val wrongAttemptsCount: StateFlow<Int> = _wrongAttemptsCount.asStateFlow()

    private val _showByteHintSheet = MutableStateFlow(false)
    val showByteHintSheet: StateFlow<Boolean> = _showByteHintSheet.asStateFlow()

    // Confetti celebration
    private val _showConfetti = MutableStateFlow(false)
    val showConfetti: StateFlow<Boolean> = _showConfetti.asStateFlow()

    // Bug Hunt Game State
    private val _activeBugHunt = MutableStateFlow<BugHuntGame?>(null)
    val activeBugHunt: StateFlow<BugHuntGame?> = _activeBugHunt.asStateFlow()

    // Playground State
    private val _playgroundLang = MutableStateFlow("javascript")
    val playgroundLang: StateFlow<String> = _playgroundLang.asStateFlow()

    private val _playgroundCode = MutableStateFlow(
        "// Welcome to CodeCraft Playground!\nfunction calculateStreak(days) {\n  return `Awesome! You have a \${days}-day coding streak! 🚀`;\n}\n\nconsole.log(calculateStreak(7));"
    )
    val playgroundCode: StateFlow<String> = _playgroundCode.asStateFlow()

    private val _playgroundTitle = MutableStateFlow("Snippet #1")
    val playgroundTitle: StateFlow<String> = _playgroundTitle.asStateFlow()

    private val _executionResult = MutableStateFlow<ExecutionResult?>(null)
    val executionResult: StateFlow<ExecutionResult?> = _executionResult.asStateFlow()

    private val _isExecuting = MutableStateFlow(false)
    val isExecuting: StateFlow<Boolean> = _isExecuting.asStateFlow()

    // Certificate preview modal
    private val _selectedCertificate = MutableStateFlow<CertificateData?>(null)
    val selectedCertificate: StateFlow<CertificateData?> = _selectedCertificate.asStateFlow()

    // Daily Goal sheet
    private val _showDailyGoalSheet = MutableStateFlow(false)
    val showDailyGoalSheet: StateFlow<Boolean> = _showDailyGoalSheet.asStateFlow()

    // Onboarding visible check
    private val _showOnboardingFlow = MutableStateFlow(false)
    val showOnboardingFlow: StateFlow<Boolean> = _showOnboardingFlow.asStateFlow()

    fun switchTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun openOnboarding() {
        _showOnboardingFlow.value = true
    }

    fun closeOnboarding() {
        _showOnboardingFlow.value = false
    }

    fun finishOnboarding(goal: String, language: String, level: String) {
        viewModelScope.launch {
            repository.completeOnboarding(goal, language, level)
            _showOnboardingFlow.value = false
        }
    }

    // Lesson Flow
    fun startLesson(lesson: LessonEntity) {
        _activeLesson.value = lesson
        _selectedLessonOption.value = ""
        _typedAnswer.value = ""
        _lessonAnswerState.value = AnswerStatus.IDLE
        _wrongAttemptsCount.value = 0
        _showByteHintSheet.value = false

        if (lesson.interactionType == InteractionType.REORDER_LINES) {
            val lines = lesson.optionsJson.split("|").shuffled()
            _reorderedLines.value = lines
        }
    }

    fun closeLesson() {
        _activeLesson.value = null
    }

    fun selectOption(option: String) {
        _selectedLessonOption.value = option
    }

    fun setTypedAnswer(text: String) {
        _typedAnswer.value = text
    }

    fun swapReorderedLines(fromIdx: Int, toIdx: Int) {
        val current = _reorderedLines.value.toMutableList()
        if (fromIdx in current.indices && toIdx in current.indices) {
            val item = current.removeAt(fromIdx)
            current.add(toIdx, item)
            _reorderedLines.value = current
        }
    }

    fun checkAnswer() {
        val lesson = _activeLesson.value ?: return

        val isCorrect = when (lesson.interactionType) {
            InteractionType.MULTIPLE_CHOICE, InteractionType.PREDICT_OUTPUT -> {
                _selectedLessonOption.value == lesson.correctAnswer
            }
            InteractionType.FILL_IN_THE_BLANK -> {
                val filled = lesson.blankSnippet.replace("___", _selectedLessonOption.value)
                filled == lesson.correctAnswer || _selectedLessonOption.value.contains(lesson.correctAnswer)
            }
            InteractionType.REORDER_LINES -> {
                _reorderedLines.value.joinToString("|") == lesson.correctAnswer
            }
            InteractionType.FREE_TYPE_CODE -> {
                val cleaned = _typedAnswer.value.replace(" ", "").trim()
                val targetClean = lesson.correctAnswer.replace(" ", "").trim()
                cleaned.contains("x*x") || cleaned == targetClean
            }
        }

        if (isCorrect) {
            _lessonAnswerState.value = AnswerStatus.CORRECT
            _showConfetti.value = true
            viewModelScope.launch {
                repository.completeLesson(lesson.id, lesson.courseId, lesson.xpReward)
            }
        } else {
            _lessonAnswerState.value = AnswerStatus.INCORRECT
            _wrongAttemptsCount.value += 1
            if (_wrongAttemptsCount.value >= 2) {
                _showByteHintSheet.value = true
            }
        }
    }

    fun dismissByteHint() {
        _showByteHintSheet.value = false
    }

    fun dismissConfetti() {
        _showConfetti.value = false
    }

    // Practice / Bug Hunt
    fun startBugHuntGame() {
        _activeBugHunt.value = BugHuntGame(
            title = "Python Indentation Trap",
            description = "Spot the syntax error causing the function to fail.",
            language = "python",
            codeLines = listOf(
                "def calculate_tax(amount):",
                "    rate = 0.15",
                "   total = amount * (1 + rate)", // Buggy: 3 spaces instead of 4
                "    return total",
                "",
                "print(calculate_tax(100))"
            ),
            buggyLineIndex = 2,
            explanation = "Line 3 had inconsistent indentation (3 spaces instead of 4). In Python, indentation defines block scope strictly!"
        )
    }

    fun closeBugHunt() {
        _activeBugHunt.value = null
    }

    // Playground
    fun setPlaygroundLanguage(lang: String) {
        _playgroundLang.value = lang
        _playgroundCode.value = when (lang) {
            "python" -> "# Python CodeCraft Sandbox\nname = \"Developer\"\nprint(\"Hello, \" + name)\n\nfor i in [1, 2, 3]:\n    print(\"Step: \" + str(i))"
            "html" -> "<!DOCTYPE html>\n<html>\n<head>\n<style>\n  body { background: #12141C; color: #00E5C7; font-family: sans-serif; padding: 20px; }\n  h1 { color: #8172FF; }\n</style>\n</head>\n<body>\n  <h1>CodeCraft Web View</h1>\n  <p>Live HTML/CSS rendering on device.</p>\n</body>\n</html>"
            else -> "// JavaScript CodeCraft Sandbox\nconst greet = (user) => `Hello, \${user}!`;\nconsole.log(greet(\"World\"));\n\nconst numbers = [10, 20, 30];\nconsole.log(\"Sum:\", numbers.reduce((a, b) => a + b, 0));"
        }
        _executionResult.value = null
    }

    fun updatePlaygroundCode(code: String) {
        _playgroundCode.value = code
    }

    fun updatePlaygroundTitle(title: String) {
        _playgroundTitle.value = title
    }

    fun runPlaygroundCode() {
        viewModelScope.launch {
            _isExecuting.value = true
            val result = sandboxEngine.execute(_playgroundCode.value, _playgroundLang.value)
            _executionResult.value = result
            _isExecuting.value = false
        }
    }

    fun saveCurrentSnippet() {
        viewModelScope.launch {
            val snippet = SnippetEntity(
                id = "snip_${System.currentTimeMillis()}",
                title = _playgroundTitle.value.ifBlank { "Snippet #${System.currentTimeMillis() % 1000}" },
                language = _playgroundLang.value,
                code = _playgroundCode.value
            )
            repository.saveSnippet(snippet)
        }
    }

    fun loadSnippet(snippet: SnippetEntity) {
        _playgroundTitle.value = snippet.title
        _playgroundLang.value = snippet.language
        _playgroundCode.value = snippet.code
        _executionResult.value = null
    }

    fun deleteSnippet(snippet: SnippetEntity) {
        viewModelScope.launch {
            repository.deleteSnippet(snippet)
        }
    }

    // Community
    fun togglePostLike(postId: String) {
        viewModelScope.launch {
            repository.toggleLike(postId)
        }
    }

    fun createPost(content: String, code: String?, tags: String, type: String) {
        viewModelScope.launch {
            val post = CommunityPostEntity(
                id = "post_${System.currentTimeMillis()}",
                authorName = user.value?.name ?: "Alex Rivera",
                authorHandle = "@${user.value?.username ?: "alex_coder"}",
                type = type,
                content = content,
                codeSnippet = code,
                language = _playgroundLang.value,
                tags = tags,
                likeCount = 1,
                commentCount = 0,
                isLiked = true
            )
            repository.addCommunityPost(post)
        }
    }

    // Settings & Profile
    fun toggleDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            repository.setDarkMode(isDark)
        }
    }

    fun setDailyGoal(goal: Int) {
        viewModelScope.launch {
            repository.setDailyGoal(goal)
            _showDailyGoalSheet.value = false
        }
    }

    fun toggleDailyGoalSheet(show: Boolean) {
        _showDailyGoalSheet.value = show
    }

    fun showCertificate(courseTitle: String) {
        _selectedCertificate.value = CertificateData(
            recipientName = user.value?.name ?: "Alex Rivera",
            courseName = courseTitle,
            issueDate = "September 2026",
            certificateId = "CC-${(100000..999999).random()}"
        )
    }

    fun dismissCertificate() {
        _selectedCertificate.value = null
    }
}

enum class AnswerStatus {
    IDLE,
    CORRECT,
    INCORRECT
}

data class BugHuntGame(
    val title: String,
    val description: String,
    val language: String,
    val codeLines: List<String>,
    val buggyLineIndex: Int,
    val explanation: String
)

data class CertificateData(
    val recipientName: String,
    val courseName: String,
    val issueDate: String,
    val certificateId: String
)
