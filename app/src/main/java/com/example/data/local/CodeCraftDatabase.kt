package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        CourseEntity::class,
        LessonEntity::class,
        SnippetEntity::class,
        CommunityPostEntity::class,
        BadgeEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CodeCraftDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun courseDao(): CourseDao
    abstract fun lessonDao(): LessonDao
    abstract fun snippetDao(): SnippetDao
    abstract fun communityDao(): CommunityDao
    abstract fun badgeDao(): BadgeDao

    companion object {
        @Volatile
        private var INSTANCE: CodeCraftDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): CodeCraftDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CodeCraftDatabase::class.java,
                    "codecraft_db"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database)
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // Check if empty and populate
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        if (database.userDao().getUserDirect() == null) {
                            populateDatabase(database)
                        }
                    }
                }
            }

            private suspend fun populateDatabase(db: CodeCraftDatabase) {
                db.userDao().insertOrUpdate(InitialData.defaultUser)
                db.courseDao().insertAll(InitialData.defaultCourses)
                db.lessonDao().insertAll(InitialData.defaultLessons)
                InitialData.defaultSnippets.forEach { db.snippetDao().insert(it) }
                InitialData.defaultPosts.forEach { db.communityDao().insert(it) }
                db.badgeDao().insertAll(InitialData.defaultBadges)
            }
        }
    }
}
