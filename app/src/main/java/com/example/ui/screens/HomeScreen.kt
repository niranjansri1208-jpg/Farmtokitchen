package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DataMode
import com.example.data.model.DemoMarketStats
import com.example.data.model.SimulatedPurchase
import com.example.data.model.VegetableBatch
import com.example.ui.components.BatchStatusChip
import com.example.ui.components.IsometricSupplyChainCanvas
import com.example.ui.components.MarketActivitySection
import com.example.ui.components.VegetableBadge
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.DeepGreen
import com.example.ui.theme.EarthBrown
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.LimeGreen
import com.example.ui.theme.MutedGray

@Composable
fun HomeScreen(
    batches: List<VegetableBatch>,
    dataMode: DataMode,
    livePurchases: List<SimulatedPurchase>,
    marketStats: DemoMarketStats,
    isSimulationRunning: Boolean,
    onToggleSimulation: () -> Unit,
    onSimulateUserPurchase: (batchId: String, quantityKg: Double) -> Unit,
    onResetDemoInventory: () -> Unit,
    onClearSimulatedPurchases: () -> Unit,
    onSwitchToDemoMode: () -> Unit,
    onNavigateToTrack: (String) -> Unit,
    onNavigateToScan: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToHowItWorks: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CreamWhite)
            .testTag("home_screen_column")
    ) {
        // 1. Hero Banner with Deep Forest Green Aesthetic
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(ForestGreen, DeepGreen)
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    // Category / Tagline Pill
                    Surface(
                        color = Color.White.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LimeGreen.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(LimeGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SMART AGRICULTURE · SUPPLY CHAIN TRANSPARENCY",
                                color = LimeGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "FROM FARM TO TABLE.\nEVERY STEP MATTERS.",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 32.sp,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Discover where your vegetables come from. Follow their journey from the farm to your kitchen with transparent, QR-powered tracking.",
                        color = Color.White.copy(alpha = 0.88f),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Hero CTA Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onNavigateToScan,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LimeGreen),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("hero_scan_qr_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                tint = ForestGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Track Produce",
                                color = ForestGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        OutlinedButton(
                            onClick = onNavigateToDashboard,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("hero_farmer_portal_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Dashboard,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Farmer Portal",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // 2. Interactive 3D Miniature Agricultural Simulation Showcase
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Interactive Journey Map",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                        Text(
                            text = "Real-time 5-stage farm-to-table simulation",
                            fontSize = 12.sp,
                            color = MutedGray
                        )
                    }

                    Surface(
                        color = FreshGreen.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.clickable { onNavigateToHowItWorks() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "How it Works",
                                color = DeepGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = DeepGreen,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // The 3D Canvas
                IsometricSupplyChainCanvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(290.dp),
                    onStationClick = { stage ->
                        val match = batches.firstOrNull { it.currentStatus.name.contains(stage.name, ignoreCase = true) } ?: batches.firstOrNull()
                        if (match != null) {
                            onNavigateToTrack(match.batchId)
                        }
                    }
                )
            }
        }

        // 3. Live Customer Marketplace Activity (When in DEMO Mode)
        item {
            if (dataMode == DataMode.DEMO) {
                MarketActivitySection(
                    batches = batches,
                    livePurchases = livePurchases,
                    marketStats = marketStats,
                    isSimulationRunning = isSimulationRunning,
                    onToggleSimulation = onToggleSimulation,
                    onSimulateUserPurchase = onSimulateUserPurchase,
                    onResetDemoInventory = onResetDemoInventory,
                    onClearSimulatedPurchases = onClearSimulatedPurchases,
                    onNavigateToTrack = onNavigateToTrack
                )
            } else {
                // If in Original mode, show an invitation card to test Demo mode
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2EBE2))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = LimeGreen.copy(alpha = 0.25f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.PlayCircleFilled, contentDescription = null, tint = DeepGreen)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Market Simulation Available",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = ForestGreen
                            )
                            Text(
                                text = "Switch to Demo mode in the header to watch fake customers purchase produce in real time.",
                                fontSize = 11.sp,
                                color = MutedGray,
                                lineHeight = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onSwitchToDemoMode,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FreshGreen)
                        ) {
                            Text("Demo", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 4. Three Core Pillars: TRACE, TRACK, DISCOVER
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "TRANSPARENCY FROM FARM TO TABLE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = FreshGreen,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Built for Honest Food Systems",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ForestGreen
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FeatureMiniCard(
                        title = "TRACE",
                        subtitle = "Digital Batch ID",
                        desc = "Every harvest gets a tamper-evident QR identity.",
                        color = FreshGreen,
                        modifier = Modifier.weight(1f)
                    )
                    FeatureMiniCard(
                        title = "TRACK",
                        subtitle = "Cold Chain Events",
                        desc = "Telemetry recorded at farm, mandi, reefer & store.",
                        color = DeepGreen,
                        modifier = Modifier.weight(1f)
                    )
                    FeatureMiniCard(
                        title = "DISCOVER",
                        subtitle = "Shopper Portal",
                        desc = "Scan to explore origin, harvest date, and farmer.",
                        color = EarthBrown,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 5. Track Live Batches (Instant Access)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (dataMode == DataMode.DEMO) "Demonstration Produce Batches" else "Your Registered Batches",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                        Text(
                            text = "Tap any batch to view its recorded timeline",
                            fontSize = 12.sp,
                            color = MutedGray
                        )
                    }

                    Surface(
                        color = ForestGreen.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${batches.size} Active",
                            color = ForestGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Batches List
        if (batches.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No batches registered in Original mode yet.", fontWeight = FontWeight.Bold, color = ForestGreen)
                        Text("Register a batch in Farmer Portal or switch to Demo mode.", fontSize = 12.sp, color = MutedGray)
                    }
                }
            }
        } else {
            items(batches) { batch ->
                BatchHomeCard(
                    batch = batch,
                    onClick = { onNavigateToTrack(batch.batchId) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }

        // 6. Trust & Transparency Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F6EE)),
                border = androidx.compose.foundation.BorderStroke(1.dp, FreshGreen.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = FreshGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Supply-Chain Trust & Integrity",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = ForestGreen
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "FarmTrace shows recorded supply-chain information. It does not independently verify the origin, freshness, safety, or organic status of the produce.",
                            fontSize = 11.sp,
                            color = MutedGray,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun FeatureMiniCard(
    title: String,
    subtitle: String,
    desc: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5EDE4))
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = color.copy(alpha = 0.15f)
            ) {
                Text(
                    text = title,
                    color = color,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreen
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = desc,
                fontSize = 10.sp,
                color = MutedGray,
                lineHeight = 13.sp
            )
        }
    }
}

@Composable
fun BatchHomeCard(
    batch: VegetableBatch,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("batch_card_${batch.batchId}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2EBE2))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    VegetableBadge(vegetableName = batch.vegetableName)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = batch.batchId,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = ForestGreen
                    )
                }
                BatchStatusChip(status = batch.currentStatus)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${batch.quantity} ${batch.unit} · ${batch.variety.ifEmpty { "Fresh Harvest" }}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreen
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Origin: ${batch.farmName}",
                        fontSize = 12.sp,
                        color = MutedGray
                    )
                    Text(
                        text = "Harvest: ${batch.harvestDate} · ${batch.qualityGrade}",
                        fontSize = 11.sp,
                        color = EarthBrown,
                        fontWeight = FontWeight.Medium
                    )
                }

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "View Journey",
                    tint = FreshGreen,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
