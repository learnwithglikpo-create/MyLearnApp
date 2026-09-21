package com.example.ui.screens.learn

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CourseEntity
import com.example.data.model.UserEntity
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun LearnScreen(
    user: UserEntity?,
    courses: List<CourseEntity>,
    onResumeLesson: () -> Unit,
    onSelectCourse: (CourseEntity) -> Unit,
    onOpenDailyGoal: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeCourse = courses.firstOrNull { it.id == "course_python_101" } ?: courses.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Top Header with Greeting, Streak, XP, and Hearts
        item {
            HeaderSection(
                user = user,
                onStreakClick = onOpenDailyGoal
            )
        }

        // 2. Daily Goal Ring Banner
        item {
            DailyGoalBanner(
                user = user,
                onClick = onOpenDailyGoal
            )
        }

        // 3. Continue Learning Hero Card
        if (activeCourse != null) {
            item {
                ContinueLearningCard(
                    course = activeCourse,
                    onResume = onResumeLesson
                )
            }
        }

        // 4. Career Paths Section
        item {
            CareerPathsSection(courses = courses)
        }

        // 5. Course Catalog grouped by Category
        item {
            Text(
                text = "Course Catalog",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        val categories = listOf("Programming Basics", "Web Development", "Data Science")
        categories.forEach { category ->
            val categoryCourses = courses.filter { it.category == category }
            if (categoryCourses.isNotEmpty()) {
                item {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = ElectricTeal
                        ),
                        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                    )
                }

                items(categoryCourses) { course ->
                    CourseCard(
                        course = course,
                        onClick = { onSelectCourse(course) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(
    user: UserEntity?,
    onStreakClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // User Greeting
        Column {
            Text(
                text = "Welcome back,",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Text(
                text = user?.name ?: "Learner",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
        }

        // Stats Badges (Streak, XP, Hearts)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Streak Flame Badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF26211C))
                    .border(1.dp, AmberStreak.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .clickable(onClick = onStreakClick)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                FlameStreakIcon(size = 18.dp)
                Text(
                    text = "${user?.streakDays ?: 1}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = AmberStreak
                    )
                )
            }

            // XP Badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF192534))
                    .border(1.dp, ElectricTeal.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.EmojiEvents,
                    contentDescription = null,
                    tint = ElectricTeal,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${user?.xp ?: 0}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = ElectricTeal
                    )
                )
            }

            // Hearts Badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF2D1B22))
                    .border(1.dp, CoralRed.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Hearts",
                    tint = CoralRed,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = "${user?.hearts ?: 5}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = CoralRed
                    )
                )
            }
        }
    }
}

@Composable
private fun DailyGoalBanner(
    user: UserEntity?,
    onClick: () -> Unit
) {
    val today = user?.todayXp ?: 35
    val goal = user?.dailyXpGoal ?: 50

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF1E2436), Color(0xFF171A27))
                )
            )
            .border(1.dp, SurfaceBorderDark, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Daily Goal",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.width(6.dp))
                if (today >= goal) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = "Completed",
                        tint = LimeGreen,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$today / $goal XP earned today",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
            )
        }

        DailyGoalRing(
            current = today,
            target = goal,
            size = 52.dp,
            strokeWidth = 6.dp
        )
    }
}

@Composable
private fun ContinueLearningCard(
    course: CourseEntity,
    onResume: () -> Unit
) {
    CodeCraftCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color(0xFF1E2132),
        borderColor = DeepIndigo.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "CONTINUE LEARNING",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = ElectricTeal
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = course.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "Lesson ${course.completedLessons + 1} of ${course.totalLessons} • Next: Conditional Logic",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            ByteMascot(size = 54.dp, mood = MascotMood.HAPPY)
        }

        Spacer(modifier = Modifier.height(14.dp))

        val progress = if (course.totalLessons > 0) course.completedLessons.toFloat() / course.totalLessons else 0f
        CodeCraftProgressBar(progress = progress, height = 6.dp)

        Spacer(modifier = Modifier.height(16.dp))

        CodeCraftButton(
            text = "Resume Lesson",
            onClick = onResume,
            icon = Icons.Default.PlayArrow,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun CareerPathsSection(courses: List<CourseEntity>) {
    Column {
        Text(
            text = "Career Paths",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        val paths = listOf(
            Triple("Full-Stack Developer", "Web & Server Apps", 4),
            Triple("Python Specialist", "Automation & Data", 3),
            Triple("Data Analyst", "SQL & Analytics", 3)
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            items(paths) { (name, desc, total) ->
                val completed = if (name.contains("Python")) 1 else 0
                Column(
                    modifier = Modifier
                        .width(220.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1B1E2B))
                        .border(1.dp, SurfaceBorderDark, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark),
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    // Step Progress Dots
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (i in 0 until total) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (i < completed) ElectricTeal else Color(0xFF2C3246))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$completed/$total courses completed",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (completed > 0) ElectricTeal else TextSecondaryDark,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun CourseCard(
    course: CourseEntity,
    onClick: () -> Unit
) {
    CodeCraftCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colorScheme.surface,
        borderColor = MaterialTheme.colorScheme.outline,
        onClick = if (!course.isPremium) onClick else null
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Language Monogram Badge
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when (course.language) {
                                "Python" -> Color(0xFF1F2F4A)
                                "JavaScript" -> Color(0xFF3B381F)
                                "HTML/CSS" -> Color(0xFF3B2421)
                                else -> DeepIndigo
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = course.language.take(2).uppercase(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = when (course.language) {
                                "Python" -> ElectricTeal
                                "JavaScript" -> AmberStreak
                                "HTML/CSS" -> CoralRed
                                else -> Color.White
                            }
                        )
                    )
                }

                Column {
                    Text(
                        text = course.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "${course.totalLessons} lessons • ${course.language}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            if (course.isPremium) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = TextSecondaryDark,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        if (course.completedLessons > 0) {
            Spacer(modifier = Modifier.height(10.dp))
            val progress = course.completedLessons.toFloat() / course.totalLessons
            CodeCraftProgressBar(progress = progress, height = 4.dp)
        }
    }
}
