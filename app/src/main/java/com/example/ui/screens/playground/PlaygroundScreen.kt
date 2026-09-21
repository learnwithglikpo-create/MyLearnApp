package com.example.ui.screens.playground

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.webkit.WebView
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.SnippetEntity
import com.example.engine.ExecutionResult
import com.example.ui.components.CodeCraftButton
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaygroundScreen(
    language: String,
    code: String,
    title: String,
    executionResult: ExecutionResult?,
    isExecuting: Boolean,
    savedSnippets: List<SnippetEntity>,
    onLanguageChange: (String) -> Unit,
    onCodeChange: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onRun: () -> Unit,
    onSave: () -> Unit,
    onLoadSnippet: (SnippetEntity) -> Unit,
    onDeleteSnippet: (SnippetEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showSnippetsSheet by remember { mutableStateOf(false) }
    var activeOutputTab by remember { mutableIntStateOf(0) } // 0: Console, 1: HTML Preview

    val quickSymbols = listOf("(", ")", "{", "}", "[", "]", "=", ";", "\"", "'", "<", ">", "/", ":", "$", "+", "-", "*")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalBg)
            .statusBarsPadding()
    ) {
        // 1. Top Controls Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Snippet Title
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                textStyle = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricTeal,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color(0xFF1E2230),
                    unfocusedContainerColor = Color(0xFF1A1D27)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Action Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Snippets Drawer Button
                IconButton(
                    onClick = { showSnippetsSheet = true },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF262C3E))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FolderOpen,
                        contentDescription = "Saved Snippets",
                        tint = ElectricTeal,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Save Button
                IconButton(
                    onClick = {
                        onSave()
                        Toast.makeText(context, "Saved to Snippets", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF262C3E))
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "Save",
                        tint = AmberStreak,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Share Button
                IconButton(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "// $title ($language)\n$code")
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share Snippet"))
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF262C3E))
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Run Pill Button
                Button(
                    onClick = onRun,
                    enabled = !isExecuting,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricTeal,
                        contentColor = CharcoalBg
                    ),
                    modifier = Modifier.height(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isExecuting) "..." else "Run",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // 2. Language Selector Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("javascript", "python", "html").forEach { lang ->
                val isSelected = language.equals(lang, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) DeepIndigo else Color(0xFF1E2232))
                        .border(1.dp, if (isSelected) ElectricTeal else SurfaceBorderDark, RoundedCornerShape(10.dp))
                        .clickable { onLanguageChange(lang) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = when (lang) {
                            "javascript" -> "JavaScript"
                            "python" -> "Python"
                            else -> "HTML/CSS"
                        },
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) ElectricTeal else TextSecondaryDark
                        )
                    )
                }
            }
        }

        // 3. Code Editor Area
        OutlinedTextField(
            value = code,
            onValueChange = onCodeChange,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 14.dp, vertical = 6.dp),
            textStyle = CodeTextStyle.copy(
                fontSize = 13.sp,
                color = ElectricTeal
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CodeEditorBg,
                unfocusedContainerColor = CodeEditorBg,
                focusedBorderColor = SurfaceBorderDark,
                unfocusedBorderColor = SurfaceBorderDark
            ),
            shape = RoundedCornerShape(14.dp)
        )

        // 4. Quick Symbols Accessory Strip
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF161924))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(quickSymbols) { sym ->
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF262C3E))
                        .clickable { onCodeChange(code + sym) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = sym,
                        style = CodeTextStyle.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }

        // 5. Output Console / HTML Preview Bottom Panel
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Color(0xFF0F1118))
                .border(1.dp, SurfaceBorderDark)
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "CONSOLE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (activeOutputTab == 0) ElectricTeal else TextSecondaryDark,
                            letterSpacing = 1.sp
                        ),
                        modifier = Modifier.clickable { activeOutputTab = 0 }
                    )

                    if (language.equals("html", true)) {
                        Text(
                            text = "WEB PREVIEW",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (activeOutputTab == 1) ElectricTeal else TextSecondaryDark,
                                letterSpacing = 1.sp
                            ),
                            modifier = Modifier.clickable { activeOutputTab = 1 }
                        )
                    }
                }

                if (executionResult != null) {
                    Text(
                        text = "${executionResult.executionTimeMs} ms",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondaryDark,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (activeOutputTab == 1 && executionResult?.htmlContent != null) {
                // HTML/CSS Live Viewport
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            loadDataWithBaseURL(null, executionResult.htmlContent, "text/html", "UTF-8", null)
                        }
                    },
                    update = { webView ->
                        webView.loadDataWithBaseURL(null, executionResult.htmlContent, "text/html", "UTF-8", null)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Standard Output / Error Text Console
                val scroll = rememberScrollState()
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scroll)
                ) {
                    Text(
                        text = executionResult?.output ?: "Press 'Run' to compile and execute snippet.",
                        style = CodeTextStyle.copy(
                            fontSize = 12.sp,
                            color = if (executionResult?.isError == true) CoralRed else Color(0xFFC7CBD8)
                        )
                    )
                }
            }
        }
    }

    // Saved Snippets Bottom Sheet
    if (showSnippetsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSnippetsSheet = false },
            containerColor = Color(0xFF191C28),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Saved Snippets",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (savedSnippets.isEmpty()) {
                    Text(
                        text = "No saved snippets yet. Tap the bookmark icon to save your current work.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondaryDark)
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(savedSnippets) { snip ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceDark)
                                    .clickable {
                                        onLoadSnippet(snip)
                                        showSnippetsSheet = false
                                    }
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = snip.title,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                    Text(
                                        text = snip.language.uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = ElectricTeal,
                                            fontSize = 11.sp
                                        )
                                    )
                                }

                                IconButton(onClick = { onDeleteSnippet(snip) }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Delete,
                                        contentDescription = "Delete",
                                        tint = CoralRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
