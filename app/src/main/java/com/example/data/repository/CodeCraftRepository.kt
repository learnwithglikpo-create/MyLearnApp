package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class CodeCraftRepository(private val database: CodeCraftDatabase) {
    private val userDao = database.userDao()
    private val courseDao = database.courseDao()
    private val lessonDao = database.lessonDao()
    private val snippetDao = database.snippetDao()
    private val communityDao = database.communityDao()
    private val badgeDao = database.badgeDao()

    val user: Flow<UserEntity?> = userDao.getUser()
    val courses: Flow<List<CourseEntity>> = courseDao.getAllCourses()
    val snippets: Flow<List<SnippetEntity>> = snippetDao.getAllSnippets()
    val posts: Flow<List<CommunityPostEntity>> = communityDao.getAllPosts()
    val badges: Flow<List<BadgeEntity>> = badgeDao.getAllBadges()

    fun getCourseById(courseId: String): Flow<CourseEntity?> = courseDao.getCourseById(courseId)
    fun getLessonsForCourse(courseId: String): Flow<List<LessonEntity>> = lessonDao.getLessonsForCourse(courseId)
    suspend fun getLessonById(lessonId: String): LessonEntity? = lessonDao.getLessonById(lessonId)

    suspend fun completeLesson(lessonId: String, courseId: String, xpReward: Int) {
        lessonDao.markCompleted(lessonId)
        courseDao.incrementCompletedLesson(courseId)
        userDao.addXp(xpReward)
    }

    suspend fun addXp(amount: Int) = userDao.addXp(amount)
    suspend fun incrementStreak() = userDao.incrementStreak()
    suspend fun updateHearts(hearts: Int) = userDao.updateHearts(hearts)
    suspend fun setDarkMode(isDark: Boolean) = userDao.setDarkMode(isDark)
    suspend fun setDailyGoal(goal: Int) = userDao.setDailyGoal(goal)
    suspend fun completeOnboarding(goal: String, language: String, level: String) {
        val currentUser = userDao.getUserDirect() ?: InitialData.defaultUser
        val updated = currentUser.copy(
            selectedGoal = goal,
            selectedLanguage = language,
            experienceLevel = level,
            onboardingCompleted = true
        )
        userDao.insertOrUpdate(updated)
    }

    suspend fun saveSnippet(snippet: SnippetEntity) = snippetDao.insert(snippet)
    suspend fun deleteSnippet(snippet: SnippetEntity) = snippetDao.delete(snippet)
    suspend fun deleteSnippetById(id: String) = snippetDao.deleteById(id)

    suspend fun addCommunityPost(post: CommunityPostEntity) = communityDao.insert(post)
    suspend fun toggleLike(postId: String) = communityDao.toggleLike(postId)

    suspend fun unlockBadge(badgeId: String, date: String) = badgeDao.unlockBadge(badgeId, date)
}
