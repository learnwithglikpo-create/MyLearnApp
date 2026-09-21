package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun CodeBlockView(
    code: String,
    language: String = "python",
    modifier: Modifier = Modifier,
    showLineNumbers: Boolean = true,
    showCopyButton: Boolean = true
) {
    val context = LocalContext.current
    val lines = code.lines()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CodeEditorBg)
            .border(1.dp, SurfaceBorderDark, RoundedCornerShape(12.dp))
    ) {
        // Top header strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E2232))
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(CoralRed))
                Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(AmberStreak))
                Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(LimeGreen))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = language.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = ElectricTeal,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }

            if (showCopyButton) {
                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("code", code)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Code copied", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = "Copy code",
                        tint = TextSecondaryDark,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Code Area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 12.dp, horizontal = 14.dp)
        ) {
            if (showLineNumbers) {
                Column(
                    modifier = Modifier.padding(end = 12.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    lines.indices.forEach { index ->
                        Text(
                            text = "${index + 1}",
                            style = CodeTextStyle.copy(
                                color = Color(0xFF4B5565),
                                fontSize = 12.5.sp
                            )
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height((lines.size * 20).dp)
                        .background(Color(0xFF2C3246))
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column {
                lines.forEach { line ->
                    Text(
                        text = highlightSyntax(line, language),
                        style = CodeTextStyle.copy(fontSize = 12.5.sp)
                    )
                }
            }
        }
    }
}

/**
 * Highlights keywords, strings, comments, and numbers matching CodeCraft theme tokens.
 */
fun highlightSyntax(line: String, language: String): AnnotatedString {
    return buildAnnotatedString {
        append(line)

        // Comments (# or //)
        val commentIdx = if (language.equals("python", true)) line.indexOf('#') else line.indexOf("//")
        if (commentIdx >= 0) {
            addStyle(SpanStyle(color = SyntaxComment), commentIdx, line.length)
            return@buildAnnotatedString
        }

        // Strings ("..." or '...')
        val stringRegex = Regex("(\"[^\"]*\"|'[^']*')")
        stringRegex.findAll(line).forEach { match ->
            addStyle(SpanStyle(color = SyntaxString), match.range.first, match.range.last + 1)
        }

        // Numbers
        val numberRegex = Regex("\\b\\d+(\\.\\d+)?\\b")
        numberRegex.findAll(line).forEach { match ->
            addStyle(SpanStyle(color = SyntaxNumber), match.range.first, match.range.last + 1)
        }

        // Keywords
        val keywords = listOf(
            "def", "return", "if", "else", "elif", "for", "in", "while", "import", "from",
            "class", "const", "let", "var", "function", "async", "await", "new", "try", "catch",
            "SELECT", "FROM", "WHERE", "JOIN", "GROUP BY", "ORDER BY"
        )
        keywords.forEach { kw ->
            val regex = Regex("\\b$kw\\b")
            regex.findAll(line).forEach { match ->
                addStyle(SpanStyle(color = SyntaxKeyword, fontWeight = FontWeight.Bold), match.range.first, match.range.last + 1)
            }
        }

        // Functions (e.g. print(, console.log(, map()
        val funcRegex = Regex("\\b([a-zA-Z_][a-zA-Z0-9_]*)(?=\\()")
        funcRegex.findAll(line).forEach { match ->
            addStyle(SpanStyle(color = SyntaxFunction), match.range.first, match.range.last + 1)
        }
    }
}
