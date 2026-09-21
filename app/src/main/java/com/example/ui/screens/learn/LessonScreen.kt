package com.example.ui.screens.learn

import android.view.HapticFeedbackConstants
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InteractionType
import com.example.data.model.LessonEntity
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AnswerStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    lesson: LessonEntity,
    selectedOption: String,
    reorderedLines: List<String>,
    typedAnswer: String,
    answerStatus: AnswerStatus,
    showByteHint: Boolean,
    showConfetti: Boolean,
    hearts: Int,
    onSelectOption: (String) -> Unit,
    onTypedAnswerChange: (String) -> Unit,
    onSwapLines: (Int, Int) -> Unit,
    onCheck: () -> Unit,
    onClose: () -> Unit,
    onDismissHint: () -> Unit,
    onDismissConfetti: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val scrollState = rememberScrollState()

    // Haptics trigger on status change
    LaunchedEffect(answerStatus) {
        if (answerStatus == AnswerStatus.CORRECT) {
            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
        } else if (answerStatus == AnswerStatus.INCORRECT) {
            view.performHapticFeedback(HapticFeedbackConstants.REJECT)
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
                .padding(bottom = 80.dp)
        ) {
            // 1. Top Bar: Close, Segmented Progress Pill, Hearts
            TopLessonBar(
                currentStep = lesson.stepIndex,
                totalSteps = lesson.totalSteps,
                hearts = hearts,
                onClose = onClose
            )

            // 2. Scrollable Lesson Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Concept Title & Explanation
                Text(
                    text = lesson.conceptTitle,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Text(
                    text = lesson.conceptExplanation,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = TextSecondaryDark,
                        lineHeight = 22.sp
                    )
                )

                // Inline Syntax-Highlighted Code Block
                if (lesson.codeSnippet.isNotBlank()) {
                    CodeBlockView(
                        code = lesson.codeSnippet,
                        language = if (lesson.courseId.contains("js")) "javascript" else "python",
                        showLineNumbers = true
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Question Prompt
                Text(
                    text = lesson.questionPrompt,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                )

                // 3. Dynamic Interaction Area
                when (lesson.interactionType) {
                    InteractionType.MULTIPLE_CHOICE, InteractionType.PREDICT_OUTPUT -> {
                        MultipleChoiceInteraction(
                            options = lesson.optionsJson.split("|"),
                            selected = selectedOption,
                            onSelect = onSelectOption
                        )
                    }
                    InteractionType.FILL_IN_THE_BLANK -> {
                        FillInBlankInteraction(
                            blankSnippet = lesson.blankSnippet,
                            options = lesson.optionsJson.split("|"),
                            selected = selectedOption,
                            onSelect = onSelectOption
                        )
                    }
                    InteractionType.REORDER_LINES -> {
                        ReorderLinesInteraction(
                            lines = reorderedLines,
                            onSwap = onSwapLines
                        )
                    }
                    InteractionType.FREE_TYPE_CODE -> {
                        FreeTypeInteraction(
                            value = typedAnswer,
                            onValueChange = onTypedAnswerChange
                        )
                    }
                }
            }
        }

        // 4. Fixed Bottom Check / Continue Bar
        BottomCheckBar(
            answerStatus = answerStatus,
            onCheck = onCheck,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // 5. Confetti Burst Particle Canvas
        ConfettiBurst(
            trigger = showConfetti,
            onComplete = onDismissConfetti
        )

        // 6. Mascot Byte Floating Assistant Sheet
        if (showByteHint) {
            ByteHintModalSheet(
                hintText = lesson.aiHint,
                onDismiss = onDismissHint
            )
        }
    }
}

@Composable
private fun TopLessonBar(
    currentStep: Int,
    totalSteps: Int,
    hearts: Int,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onClose,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close lesson",
                tint = Color.White
            )
        }

        // Segmented pill progress bar
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            for (i in 1..totalSteps) {
                val isCompleted = i <= currentStep
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isCompleted) ElectricTeal else Color(0xFF262C3E))
                )
            }
        }

        // Hearts count
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Hearts",
                tint = CoralRed,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "$hearts",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = CoralRed
                )
            )
        }
    }
}

@Composable
private fun MultipleChoiceInteraction(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        options.forEach { option ->
            val isSelected = selected == option
            val border = if (isSelected) ElectricTeal else SurfaceBorderDark
            val bg = if (isSelected) Color(0xFF1E2838) else SurfaceDark

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(bg)
                    .border(if (isSelected) 2.dp else 1.dp, border, RoundedCornerShape(14.dp))
                    .clickable { onSelect(option) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = option,
                    style = CodeTextStyle.copy(
                        color = if (isSelected) Color.White else TextPrimaryDark,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                )

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(ElectricTeal),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null,
                            tint = CharcoalBg,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FillInBlankInteraction(
    blankSnippet: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Preview Box with blank filled in
        val filled = blankSnippet.replace("___", selected.ifEmpty { "____" })
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CodeEditorBg)
                .border(1.dp, if (selected.isNotEmpty()) ElectricTeal else SurfaceBorderDark, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text(
                text = filled,
                style = CodeTextStyle.copy(
                    fontSize = 15.sp,
                    color = if (selected.isNotEmpty()) ElectricTeal else Color(0xFF8172FF)
                )
            )
        }

        Text(
            text = "Tap to insert token:",
            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
        )

        // Token chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            options.forEach { token ->
                val isSelected = selected == token
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) DeepIndigo else SurfaceDark)
                        .border(1.dp, if (isSelected) ElectricTeal else SurfaceBorderDark, RoundedCornerShape(12.dp))
                        .clickable { onSelect(token) }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = token,
                        style = CodeTextStyle.copy(
                            color = if (isSelected) ElectricTeal else Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ReorderLinesInteraction(
    lines: List<String>,
    onSwap: (Int, Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        lines.forEachIndexed { index, line ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceDark)
                    .border(1.dp, SurfaceBorderDark, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = line,
                    style = CodeTextStyle.copy(color = Color.White),
                    modifier = Modifier.weight(1f)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    IconButton(
                        onClick = { if (index > 0) onSwap(index, index - 1) },
                        enabled = index > 0,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Move Up",
                            tint = if (index > 0) ElectricTeal else Color(0xFF384058)
                        )
                    }
                    IconButton(
                        onClick = { if (index < lines.size - 1) onSwap(index, index + 1) },
                        enabled = index < lines.size - 1,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Move Down",
                            tint = if (index < lines.size - 1) ElectricTeal else Color(0xFF384058)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FreeTypeInteraction(
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            placeholder = {
                Text(
                    text = "x * x",
                    style = CodeTextStyle.copy(color = TextSecondaryDark)
                )
            },
            textStyle = CodeTextStyle.copy(color = ElectricTeal),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CodeEditorBg,
                unfocusedContainerColor = CodeEditorBg,
                focusedBorderColor = ElectricTeal,
                unfocusedBorderColor = SurfaceBorderDark
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun BottomCheckBar(
    answerStatus: AnswerStatus,
    onCheck: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = when (answerStatus) {
        AnswerStatus.CORRECT -> Color(0xFF142E1F)
        AnswerStatus.INCORRECT -> Color(0xFF32161A)
        else -> SurfaceDark
    }

    val buttonColor = when (answerStatus) {
        AnswerStatus.CORRECT -> LimeGreen
        AnswerStatus.INCORRECT -> CoralRed
        else -> DeepIndigo
    }

    val buttonText = when (answerStatus) {
        AnswerStatus.CORRECT -> "Awesome! Continue"
        AnswerStatus.INCORRECT -> "Try Again"
        else -> "Check Answer"
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = bgColor,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            CodeCraftButton(
                text = buttonText,
                onClick = onCheck,
                containerColor = buttonColor,
                contentColor = if (answerStatus == AnswerStatus.CORRECT) CharcoalBg else Color.White,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ByteHintModalSheet(
    hintText: String,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF181B27),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ByteMascot(size = 80.dp, mood = MascotMood.HINT)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Byte's Quick Hint",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = AmberStreak
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = hintText.ifEmpty { "Double-check the syntax requirements and indentation!" },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White,
                    lineHeight = 22.sp
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Was this helpful?",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("👍 Yes", style = MaterialTheme.typography.labelSmall)
                    }
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("👎 Not quite", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            CodeCraftButton(
                text = "Got it!",
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
