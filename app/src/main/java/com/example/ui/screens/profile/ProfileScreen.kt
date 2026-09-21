package com.example.ui.screens.profile

import android.content.Intent
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BadgeEntity
import com.example.data.model.UserEntity
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    user: UserEntity?,
    badges: List<BadgeEntity>,
    onToggleDarkMode: (Boolean) -> Unit,
    onOpenDailyGoalDialog: () -> Unit,
    onViewCertificate: (courseTitle: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var reminderEnabled by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. User Header
        item {
            UserHeaderCard(user = user)
        }

        // 2. Stats Grid (4 Cards)
        item {
            StatsGrid(user = user)
        }

        // 3. Weekly Streak Calendar
        item {
            StreakCalendarCard(streakDays = user?.streakDays ?: 5)
        }

        // 4. Certificates & Achievements
        item {
            CertificatesSection(
                onViewCertificate = onViewCertificate
            )
        }

        // 5. Badges & Achievements
        item {
            BadgesSection(badges = badges)
        }

        // 6. Settings List
        item {
            SettingsSection(
                user = user,
                reminderEnabled = reminderEnabled,
                onReminderToggle = { reminderEnabled = it },
                onToggleDarkMode = onToggleDarkMode,
                onOpenDailyGoal = onOpenDailyGoalDialog
            )
        }
    }
}

@Composable
private fun UserHeaderCard(user: UserEntity?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceDark)
            .border(1.dp, SurfaceBorderDark, RoundedCornerShape(18.dp))
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Avatar with Byte Robot emblem
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF202638))
                    .border(2.dp, ElectricTeal, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                ByteMascot(size = 46.dp, mood = MascotMood.HAPPY)
            }

            Column {
                Text(
                    text = user?.name ?: "Alex Rivera",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "@${user?.username ?: "alex_coder"} • Joined Sep 2026",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondaryDark,
                        fontSize = 12.sp
                    )
                )
                Text(
                    text = "${user?.selectedLanguage ?: "Python"} Specialist",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = ElectricTeal,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun StatsGrid(user: UserEntity?) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "Streak",
                value = "${user?.streakDays ?: 5} Days",
                icon = { FlameStreakIcon(size = 20.dp) },
                accent = AmberStreak,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Total XP",
                value = "${user?.xp ?: 420}",
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.EmojiEvents,
                        contentDescription = null,
                        tint = ElectricTeal,
                        modifier = Modifier.size(20.dp)
                    )
                },
                accent = ElectricTeal,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "Completed",
                value = "1 Course",
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.School,
                        contentDescription = null,
                        tint = LimeGreen,
                        modifier = Modifier.size(20.dp)
                    )
                },
                accent = LimeGreen,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "League Rank",
                value = "#14 Amber",
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Shield,
                        contentDescription = null,
                        tint = Color(0xFF8172FF),
                        modifier = Modifier.size(20.dp)
                    )
                },
                accent = Color(0xFF8172FF),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: @Composable () -> Unit,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.dp, SurfaceBorderDark, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextSecondaryDark,
                    fontSize = 11.sp
                )
            )
            icon()
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
    }
}

@Composable
private fun StreakCalendarCard(streakDays: Int) {
    CodeCraftCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = SurfaceDark,
        borderColor = SurfaceBorderDark
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Streak Calendar",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                FlameStreakIcon(size = 16.dp)
                Text(
                    text = "$streakDays Days active",
                    style = MaterialTheme.typography.labelSmall.copy(color = AmberStreak, fontWeight = FontWeight.Bold)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            days.forEachIndexed { index, day ->
                val isActive = index < 5 // 5 days completed this week
                val isToday = index == 4

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isToday) ElectricTeal else TextSecondaryDark,
                            fontSize = 11.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isToday -> AmberStreak
                                    isActive -> ElectricTeal
                                    else -> Color(0xFF262C3E)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isToday) {
                            FlameStreakIcon(size = 18.dp)
                        } else if (isActive) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
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
}

@Composable
private fun CertificatesSection(onViewCertificate: (String) -> Unit) {
    Column {
        Text(
            text = "Certificates of Completion",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        CodeCraftCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0xFF1B2234),
            borderColor = DeepIndigo.copy(alpha = 0.8f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Python Fundamentals",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Issued Sep 2026 • Verified Credential",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondaryDark,
                            fontSize = 11.sp
                        )
                    )
                }

                Button(
                    onClick = { onViewCertificate("Python Fundamentals") },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepIndigo,
                        contentColor = ElectricTeal
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("View", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun BadgesSection(badges: List<BadgeEntity>) {
    Column {
        Text(
            text = "Badges & Achievements",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(badges) { badge ->
                val isUnlocked = badge.isUnlocked
                val border = if (isUnlocked) ElectricTeal else SurfaceBorderDark
                val bg = if (isUnlocked) SurfaceDark else Color(0xFF161922)

                Column(
                    modifier = Modifier
                        .width(130.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(bg)
                        .border(1.dp, border, RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (isUnlocked) DeepIndigo else Color(0xFF232734)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (badge.iconType) {
                                "flame" -> Icons.Outlined.LocalFireDepartment
                                "bug" -> Icons.Outlined.BugReport
                                "spark" -> Icons.Outlined.FlashOn
                                "trophy" -> Icons.Outlined.EmojiEvents
                                else -> Icons.Outlined.Code
                            },
                            contentDescription = null,
                            tint = if (isUnlocked) AmberStreak else Color(0xFF555E75),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = badge.name,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isUnlocked) Color.White else TextSecondaryDark
                        )
                    )

                    Text(
                        text = if (isUnlocked) (badge.unlockedAt ?: "Unlocked") else "Locked",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isUnlocked) ElectricTeal else Color(0xFF555E75),
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    user: UserEntity?,
    reminderEnabled: Boolean,
    onReminderToggle: (Boolean) -> Unit,
    onToggleDarkMode: (Boolean) -> Unit,
    onOpenDailyGoal: () -> Unit
) {
    Column {
        Text(
            text = "Settings & Preferences",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceDark)
                .border(1.dp, SurfaceBorderDark, RoundedCornerShape(16.dp))
        ) {
            // Daily Goal setting
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenDailyGoal)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Daily XP Goal", style = MaterialTheme.typography.bodyLarge.copy(color = Color.White))
                    Text("${user?.dailyXpGoal ?: 50} XP per day", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark))
                }
                Icon(imageVector = Icons.Outlined.ChevronRight, contentDescription = null, tint = TextSecondaryDark)
            }

            HorizontalDivider(color = SurfaceBorderDark)

            // Learning Reminders
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Daily Practice Reminders", style = MaterialTheme.typography.bodyLarge.copy(color = Color.White))
                    Text("Get notified at 8:00 PM to keep your streak", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark))
                }
                Switch(
                    checked = reminderEnabled,
                    onCheckedChange = onReminderToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = ElectricTeal,
                        checkedTrackColor = DeepIndigo
                    )
                )
            }

            HorizontalDivider(color = SurfaceBorderDark)

            // Dark Mode Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Dark Mode First", style = MaterialTheme.typography.bodyLarge.copy(color = Color.White))
                    Text("Charcoal background with high-contrast syntax", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark))
                }
                Switch(
                    checked = user?.darkMode != false,
                    onCheckedChange = onToggleDarkMode,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = ElectricTeal,
                        checkedTrackColor = DeepIndigo
                    )
                )
            }
        }
    }
}
