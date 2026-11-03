package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.IsometricSupplyChainCanvas
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.DeepGreen
import com.example.ui.theme.EarthBrown
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.LimeGreen
import com.example.ui.theme.MutedGray

@Composable
fun HowItWorksScreen(
    onNavigateBack: () -> Unit,
    onNavigateToScan: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onReplayIntro: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CreamWhite)
            .testTag("how_it_works_column")
    ) {
        // Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ForestGreen)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
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
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "SMART SUPPLY CHAIN EXPLAINER",
                            color = LimeGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "How FarmTrace Works",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // Project Headline & Mission
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "TRANSPARENCY FROM FARM TO TABLE.",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = ForestGreen,
                    lineHeight = 28.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "FarmTrace is a smart agriculture tracking platform that uses unique batch identities, QR codes, and recorded journey events to make vegetable supply chains easier to understand.",
                    fontSize = 14.sp,
                    color = Color(0xFF37474F),
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Interactive 3D Canvas
                IsometricSupplyChainCanvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Three Core Cards
                SectionPillarCard(
                    title = "1. TRACE",
                    subtitle = "Give each batch a unique digital identity",
                    description = "When vegetables are harvested, farmers register the lot in FarmTrace. An immutable Batch ID (e.g. FT-2026-000123) and high-density QR code are generated immediately.",
                    icon = Icons.Default.QrCode,
                    color = FreshGreen
                )

                Spacer(modifier = Modifier.height(12.dp))

                SectionPillarCard(
                    title = "2. TRACK",
                    subtitle = "Record the journey at every supply-chain stop",
                    description = "Authorized handlers at the farm, wholesale market mandi, cold reefer truck, and supermarket scan and append verifiable timestamped event logs with GPS coordinates and temperature readings.",
                    icon = Icons.Default.LocalShipping,
                    color = DeepGreen
                )

                Spacer(modifier = Modifier.height(12.dp))

                SectionPillarCard(
                    title = "3. DISCOVER",
                    subtitle = "Let shoppers explore the recorded history by scanning",
                    description = "Shoppers in grocery stores scan the crate QR tag with any camera phone without downloading an app to discover the harvest date, farmer story, and complete travel route.",
                    icon = Icons.Default.Kitchen,
                    color = EarthBrown
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Robotics & AgriTech Educational Summary
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2EBE2))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = FreshGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Supply Chain Integrity Model",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = ForestGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "• Cryptographic Batch Codes: Prevent counterfeit produce labeling.\n• Cold Chain Telemetry: Real-time temperature & humidity logging ensures safe food transit.\n• Open Access: Consumer transparency builds direct trust between farmers and consumers.",
                            fontSize = 12.sp,
                            color = Color(0xFF455A64),
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // CTA Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onNavigateToScan,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Try Scanner", color = CreamWhite, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onNavigateToDashboard,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LimeGreen),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Farmer Dashboard", color = ForestGreen, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onReplayIntro,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Replay Intro Logo Animation", color = ForestGreen, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
private fun SectionPillarCard(
    title: String,
    subtitle: String,
    description: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = color.copy(alpha = 0.15f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = color,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = subtitle,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                fontSize = 12.sp,
                color = Color(0xFF455A64),
                lineHeight = 17.sp
            )
        }
    }
}
