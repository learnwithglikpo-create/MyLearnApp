package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUser(id: String = "user_default"): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserDirect(id: String = "user_default"): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: UserEntity)

    @Query("UPDATE users SET xp = xp + :amount, todayXp = todayXp + :amount WHERE id = :id")
    suspend fun addXp(amount: Int, id: String = "user_default")

    @Query("UPDATE users SET streakDays = streakDays + 1 WHERE id = :id")
    suspend fun incrementStreak(id: String = "user_default")

    @Query("UPDATE users SET hearts = :hearts WHERE id = :id")
    suspend fun updateHearts(hearts: Int, id: String = "user_default")

    @Query("UPDATE users SET darkMode = :isDark WHERE id = :id")
    suspend fun setDarkMode(isDark: Boolean, id: String = "user_default")

    @Query("UPDATE users SET dailyXpGoal = :goal WHERE id = :id")
    suspend fun setDailyGoal(goal: Int, id: String = "user_default")

    @Query("UPDATE users SET onboardingCompleted = 1 WHERE id = :id")
    suspend fun completeOnboarding(id: String = "user_default")
}

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses ORDER BY orderIndex ASC")
    fun getAllCourses(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE id = :courseId LIMIT 1")
    fun getCourseById(courseId: String): Flow<CourseEntity?>

    @Query("SELECT * FROM courses WHERE category = :category ORDER BY orderIndex ASC")
    fun getCoursesByCategory(category: String): Flow<List<CourseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(courses: List<CourseEntity>)

    @Query("UPDATE courses SET completedLessons = completedLessons + 1 WHERE id = :courseId")
    suspend fun incrementCompletedLesson(courseId: String)
}

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons WHERE courseId = :courseId ORDER BY stepIndex ASC")
    fun getLessonsForCourse(courseId: String): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE id = :lessonId LIMIT 1")
    suspend fun getLessonById(lessonId: String): LessonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(lessons: List<LessonEntity>)

    @Query("UPDATE lessons SET isCompleted = 1 WHERE id = :lessonId")
    suspend fun markCompleted(lessonId: String)
}

@Dao
interface SnippetDao {
    @Query("SELECT * FROM snippets ORDER BY createdAt DESC")
    fun getAllSnippets(): Flow<List<SnippetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(snippet: SnippetEntity)

    @Delete
    suspend fun delete(snippet: SnippetEntity)

    @Query("DELETE FROM snippets WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Dao
interface CommunityDao {
    @Query("SELECT * FROM community_posts ORDER BY createdAt DESC")
    fun getAllPosts(): Flow<List<CommunityPostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: CommunityPostEntity)

    @Query("UPDATE community_posts SET likeCount = likeCount + (CASE WHEN isLiked = 1 THEN -1 ELSE 1 END), isLiked = (CASE WHEN isLiked = 1 THEN 0 ELSE 1 END) WHERE id = :postId")
    suspend fun toggleLike(postId: String)
}

@Dao
interface BadgeDao {
    @Query("SELECT * FROM badges")
    fun getAllBadges(): Flow<List<BadgeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(badges: List<BadgeEntity>)

    @Query("UPDATE badges SET isUnlocked = 1, unlockedAt = :date WHERE id = :badgeId")
    suspend fun unlockBadge(badgeId: String, date: String)
}
