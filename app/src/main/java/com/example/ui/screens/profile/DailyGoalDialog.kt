package com.example.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CodeCraftButton
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyGoalBottomSheet(
    currentGoal: Int,
    onSelectGoal: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selected by remember { mutableIntStateOf(currentGoal) }

    val goals = listOf(
        Pair(20, "Casual • 5 min/day"),
        Pair(50, "Regular • 15 min/day"),
        Pair(100, "Serious • 30 min/day"),
        Pair(150, "Intense • 45+ min/day")
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF191C28),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Set Daily XP Goal",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Text(
                text = "Pick a daily pace that fits your schedule. You can adjust this anytime.",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondaryDark)
            )

            goals.forEach { (xp, label) ->
                val isSelected = selected == xp
                val border = if (isSelected) ElectricTeal else SurfaceBorderDark
                val bg = if (isSelected) Color(0xFF1E2838) else SurfaceDark

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(bg)
                        .border(if (isSelected) 2.dp else 1.dp, border, RoundedCornerShape(14.dp))
                        .clickable { selected = xp }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "$xp XP / day",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) ElectricTeal else Color.White
                            )
                        )
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    RadioButton(
                        selected = isSelected,
                        onClick = { selected = xp },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = ElectricTeal,
                            unselectedColor = TextSecondaryDark
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            CodeCraftButton(
                text = "Save Daily Goal",
                onClick = { onSelectGoal(selected) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
