package com.example.ui.screens.profile

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.components.ByteMascot
import com.example.ui.components.CodeCraftButton
import com.example.ui.components.MascotMood
import com.example.ui.theme.*
import com.example.ui.viewmodel.CertificateData

@Composable
fun CertificateDialog(
    certificate: CertificateData,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF141724))
                .border(2.dp, ElectricTeal, RoundedCornerShape(20.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondaryDark
                    )
                }
            }

            // Certificate Seal / Mascot
            ByteMascot(size = 70.dp, mood = MascotMood.CHEERING)

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "CERTIFICATE OF COMPLETION",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 2.sp,
                    color = ElectricTeal,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "CodeCraft Academy",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "This verified credential is proudly presented to",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = certificate.recipientName,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = AmberStreak
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "for successfully mastering all modules and practice challenges in",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondaryDark,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = certificate.courseName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Certificate ID & Date Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1E2336))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ID: ${certificate.certificateId}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = TextSecondaryDark
                    )
                )
                Text(
                    text = certificate.issueDate,
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            CodeCraftButton(
                text = "Share Credential",
                onClick = {
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "🎓 I just earned my ${certificate.courseName} certificate on CodeCraft! Verified ID: ${certificate.certificateId}"
                        )
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Certificate"))
                },
                icon = Icons.Outlined.Share,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
