package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = "user_default",
    val name: String = "Alex Rivera",
    val username: String = "alex_coder",
    val email: String = "alex@codecraft.io",
    val xp: Int = 340,
    val dailyXpGoal: Int = 50,
    val todayXp: Int = 35,
    val streakDays: Int = 5,
    val hearts: Int = 5,
    val maxHearts: Int = 5,
    val selectedGoal: String = "Build Websites",
    val selectedLanguage: String = "Python",
    val experienceLevel: String = "Some experience",
    val onboardingCompleted: Boolean = true,
    val darkMode: Boolean = true
)

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String, // "Programming Basics", "Web Development", "Data Science"
    val language: String, // "Python", "JavaScript", "HTML/CSS", "SQL"
    val totalLessons: Int,
    val completedLessons: Int,
    val description: String,
    val pathTitle: String? = null,
    val isPremium: Boolean = false,
    val orderIndex: Int = 0
)

enum class InteractionType {
    MULTIPLE_CHOICE,
    FILL_IN_THE_BLANK,
    PREDICT_OUTPUT,
    REORDER_LINES,
    FREE_TYPE_CODE
}

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey val id: String,
    val courseId: String,
    val title: String,
    val stepIndex: Int,
    val totalSteps: Int,
    val xpReward: Int,
    val conceptTitle: String,
    val conceptExplanation: String,
    val codeSnippet: String,
    val interactionType: InteractionType,
    val questionPrompt: String,
    val optionsJson: String, // Comma or pipe delimited options
    val correctAnswer: String,
    val blankSnippet: String = "",
    val aiHint: String = "",
    val isCompleted: Boolean = false
)

@Entity(tableName = "snippets")
data class SnippetEntity(
    @PrimaryKey val id: String,
    val title: String,
    val language: String, // "javascript", "python", "html"
    val code: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)

@Entity(tableName = "community_posts")
data class CommunityPostEntity(
    @PrimaryKey val id: String,
    val authorName: String,
    val authorHandle: String,
    val type: String, // "Question", "Snippet", "Achievement"
    val content: String,
    val codeSnippet: String? = null,
    val language: String? = null,
    val tags: String = "#python, #basics",
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val isLiked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "badges")
data class BadgeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val iconType: String, // "spark", "flame", "bug", "code", "trophy", "shield"
    val isUnlocked: Boolean = false,
    val unlockedAt: String? = null
)
