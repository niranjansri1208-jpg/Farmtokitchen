package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VegetableBatch
import com.example.ui.components.VegetableBadge
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.DeepGreen
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.LimeGreen
import com.example.ui.theme.MutedGray

@Composable
fun ScanScreen(
    batches: List<VegetableBatch>,
    onBatchScanned: (String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var manualBatchInput by remember { mutableStateOf("") }
    var isFlashOn by remember { mutableStateOf(false) }
    var scanSuccessFeedback by remember { mutableStateOf<String?>(null) }

    // Animated Laser Scan Line
    val infiniteTransition = rememberInfiniteTransition(label = "LaserMotion")
    val laserYRatio by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LaserY"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A1C12))
            .testTag("scan_screen_column"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SCAN. DISCOVER. TRACE.",
                        color = LimeGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Scan Batch QR Code",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                IconButton(
                    onClick = {
                        isFlashOn = !isFlashOn
                        Toast.makeText(context, if (isFlashOn) "Torch ON" else "Torch OFF", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isFlashOn) LimeGreen else Color.White.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = "Torch",
                        tint = if (isFlashOn) ForestGreen else Color.White
                    )
                }
            }
        }

        // 3D-styled Smartphone Viewfinder Frame
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Align the FarmTrace QR code inside the viewfinder frame",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                // The Viewfinder Box
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF142B1E))
                        .border(2.dp, LimeGreen.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                ) {
                    // Corner targeting brackets
                    TargetingBrackets()

                    // Simulated Camera Sensor Background Pattern
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color(0xFF1B3D2A), Color(0xFF0F2217))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = LimeGreen.copy(alpha = 0.35f),
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Optical Scanner Active",
                                color = LimeGreen.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Animated Glowing Laser Beam
                    val laserHeight = 260.dp
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .offset(y = laserHeight * laserYRatio)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        LimeGreen,
                                        Color.White,
                                        LimeGreen,
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }

                if (scanSuccessFeedback != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = LimeGreen,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Detected: $scanSuccessFeedback",
                                color = ForestGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Quick Scan Demo Batches Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Surface(
                    color = Color.White.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.QrCode,
                                contentDescription = null,
                                tint = LimeGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SIMULATE SCAN (TEST BATCHES)",
                                color = LimeGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }
                        Text(
                            text = "Tap any verified batch below to simulate an instant QR camera detection:",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            batches.take(4).forEach { b ->
                                Card(
                                    onClick = {
                                        scanSuccessFeedback = b.batchId
                                        onBatchScanned(b.batchId)
                                    },
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E382A)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("test_scan_batch_${b.batchId}")
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            VegetableBadge(vegetableName = b.vegetableName)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = b.batchId,
                                                    color = Color.White,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                                Text(
                                                    text = b.farmName,
                                                    color = Color.White.copy(alpha = 0.6f),
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }

                                        Surface(
                                            color = LimeGreen,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "SCAN NOW",
                                                color = ForestGreen,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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

        // Manual Batch Code Entry Fallback
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Can't Scan? Enter Code Manually",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                    Text(
                        text = "Find the batch ID printed directly beneath the QR code (e.g. FT-2026-000123)",
                        fontSize = 11.sp,
                        color = MutedGray,
                        modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = manualBatchInput,
                            onValueChange = { manualBatchInput = it },
                            placeholder = { Text("FT-2026-...", fontSize = 13.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("manual_scan_input"),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (manualBatchInput.isNotBlank()) {
                                    val cleaned = manualBatchInput.trim()
                                    val found = batches.find { it.batchId.equals(cleaned, ignoreCase = true) }
                                    if (found != null) {
                                        onBatchScanned(found.batchId)
                                    } else {
                                        // Still pass to track screen which has friendly not found state
                                        onBatchScanned(cleaned)
                                    }
                                } else {
                                    Toast.makeText(context, "Please enter a batch code", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                            modifier = Modifier.testTag("manual_scan_submit_button")
                        ) {
                            Text("Trace", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TargetingBrackets() {
    Box(modifier = Modifier.fillMaxSize()) {
        val bracketSize = 24.dp
        val stroke = 3.dp

        // Top Left
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(14.dp)
                .size(bracketSize)
                .border(
                    width = stroke,
                    color = LimeGreen,
                    shape = RoundedCornerShape(topStart = 8.dp)
                )
        )
        // Top Right
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(14.dp)
                .size(bracketSize)
                .border(
                    width = stroke,
                    color = LimeGreen,
                    shape = RoundedCornerShape(topEnd = 8.dp)
                )
        )
        // Bottom Left
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(14.dp)
                .size(bracketSize)
                .border(
                    width = stroke,
                    color = LimeGreen,
                    shape = RoundedCornerShape(bottomStart = 8.dp)
                )
        )
        // Bottom Right
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(14.dp)
                .size(bracketSize)
                .border(
                    width = stroke,
                    color = LimeGreen,
                    shape = RoundedCornerShape(bottomEnd = 8.dp)
                )
        )
    }
}
