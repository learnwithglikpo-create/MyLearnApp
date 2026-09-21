package com.example.ui.screens.practice

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.BugHuntGame
import kotlinx.coroutines.delay

@Composable
fun BugHuntGameScreen(
    game: BugHuntGame,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedLineIndex by remember { mutableStateOf<Int?>(null) }
    var isSuccess by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(100) }
    var timeLeftSeconds by remember { mutableIntStateOf(30) }
    var showConfetti by remember { mutableStateOf(false) }

    // Countdown Timer
    LaunchedEffect(isSuccess) {
        while (!isSuccess && timeLeftSeconds > 0) {
            delay(1000)
            timeLeftSeconds--
            if (score > 10) score -= 2
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Bar: Close, Title, Score, Timer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Exit",
                        tint = Color.White
                    )
                }

                Text(
                    text = "BUG HUNT",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = CoralRed
                    )
                )

                // Score pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF231E35))
                        .border(1.dp, DeepIndigo, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "$score pts",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ElectricTeal
                        )
                    )
                }
            }

            // Timer bar
            val timerProgress = (timeLeftSeconds.toFloat() / 30f).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { timerProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (timeLeftSeconds < 10) CoralRed else ElectricTeal,
                trackColor = Color(0xFF262A3B)
            )

            // Game instructions
            Text(
                text = "Tap the single line containing the bug:",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            )

            // Code Block with interactive clickable lines
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(CodeEditorBg)
                    .border(1.dp, SurfaceBorderDark, RoundedCornerShape(14.dp))
                    .padding(vertical = 12.dp)
            ) {
                game.codeLines.forEachIndexed { index, line ->
                    val isSelected = selectedLineIndex == index
                    val isThisBug = isSelected && index == game.buggyLineIndex
                    val isFalsePick = isSelected && index != game.buggyLineIndex

                    val lineBg = when {
                        isThisBug -> LimeGreen.copy(alpha = 0.25f)
                        isFalsePick -> CoralRed.copy(alpha = 0.25f)
                        else -> Color.Transparent
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(lineBg)
                            .clickable(enabled = !isSuccess) {
                                selectedLineIndex = index
                                if (index == game.buggyLineIndex) {
                                    isSuccess = true
                                    showConfetti = true
                                } else {
                                    score = (score - 15).coerceAtLeast(10)
                                }
                            }
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${index + 1}",
                            style = CodeTextStyle.copy(
                                color = if (isSelected) ElectricTeal else Color(0xFF4B5565),
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            modifier = Modifier.width(28.dp)
                        )

                        Text(
                            text = if (line.isEmpty()) " " else line,
                            style = CodeTextStyle.copy(
                                color = if (isThisBug) LimeGreen else if (isFalsePick) CoralRed else Color.White,
                                fontSize = 13.5.sp
                            )
                        )
                    }
                }
            }

            // Success or Hint Feedback
            AnimatedVisibility(visible = isSuccess) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF142E1F))
                        .border(1.dp, LimeGreen, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ByteMascot(size = 70.dp, mood = MascotMood.CHEERING)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Found it! 🎉",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = LimeGreen
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = game.explanation,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            lineHeight = 20.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CodeCraftButton(
                        text = "Claim +$score XP",
                        onClick = onClose,
                        containerColor = LimeGreen,
                        contentColor = CharcoalBg,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        ConfettiBurst(
            trigger = showConfetti,
            onComplete = { showConfetti = false }
        )
    }
}
