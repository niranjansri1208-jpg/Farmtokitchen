package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.BatchStatus
import com.example.data.model.DemoMarketStats
import com.example.data.model.SimulatedPurchase
import com.example.data.model.VegetableBatch
import com.example.data.model.VegetableType
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DeepGreen
import com.example.ui.theme.EarthBrown
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.LimeGreen
import com.example.ui.theme.MutedGray
import kotlin.math.roundToInt

@Composable
fun MarketActivitySection(
    batches: List<VegetableBatch>,
    livePurchases: List<SimulatedPurchase>,
    marketStats: DemoMarketStats,
    isSimulationRunning: Boolean,
    onToggleSimulation: () -> Unit,
    onSimulateUserPurchase: (batchId: String, quantityKg: Double) -> Unit,
    onResetDemoInventory: () -> Unit,
    onClearSimulatedPurchases: () -> Unit,
    onNavigateToTrack: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedBatchForPurchase by remember { mutableStateOf<VegetableBatch?>(null) }
    var selectedFilterVeg by remember { mutableStateOf<String?>(null) }
    var batchSearchQuery by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("market_activity_section")
    ) {
        // Section Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ForestGreen),
            border = androidx.compose.foundation.BorderStroke(1.dp, LimeGreen.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = LimeGreen.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LimeGreen.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "SIMULATED DEMO ACTIVITY",
                            color = LimeGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Simulation Play/Pause Button
                    Button(
                        onClick = onToggleSimulation,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSimulationRunning) LimeGreen else Color.White.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("toggle_market_simulation_btn")
                    ) {
                        Icon(
                            imageVector = if (isSimulationRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = if (isSimulationRunning) ForestGreen else CreamWhite,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isSimulationRunning) "Active (4s)" else "Paused",
                            color = if (isSimulationRunning) ForestGreen else CreamWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "FRESH FROM THE FARM. MOVING TO YOUR TABLE.",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = (-0.3).sp
                )

                Text(
                    text = "Watch registered vegetable batches move through the FarmTrace marketplace and get bought by simulated shoppers.",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.82f),
                    lineHeight = 16.sp
                )
            }
        }

        // Live Market Statistics Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MarketStatCard(
                label = "Simulated Sales",
                value = "₹${marketStats.totalSalesRupees.roundToInt()}",
                subtitle = "${marketStats.simulatedPurchasesCount} orders",
                icon = Icons.Default.LocalAtm,
                color = LimeGreen,
                modifier = Modifier.weight(1f)
            )
            MarketStatCard(
                label = "Volume Sold",
                value = "${marketStats.totalKgSold.roundToInt()} kg",
                subtitle = "From stock",
                icon = Icons.Default.Scale,
                color = Color(0xFF64B5F6),
                modifier = Modifier.weight(1f)
            )
            MarketStatCard(
                label = "Customers",
                value = "${marketStats.totalCustomersCount}",
                subtitle = "Top: ${marketStats.topSellingVegetable}",
                icon = Icons.Default.People,
                color = Color(0xFFFFB74D),
                modifier = Modifier.weight(1f)
            )
        }

        // Mini Sales Distribution Chart
        SalesBarVisualizer(
            purchases = livePurchases,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        )

        // Live Purchases Feed
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isSimulationRunning) FreshGreen else Color.Gray)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Live Customer Activity Feed",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                }

                if (livePurchases.isNotEmpty()) {
                    Text(
                        text = "Clear",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F),
                        modifier = Modifier
                            .clickable { onClearSimulatedPurchases() }
                            .padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (livePurchases.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2EBE2)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No simulated purchases yet.",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MutedGray
                        )
                        Text(
                            text = "Toggle simulation above or click 'Simulate Purchase' on any batch below.",
                            fontSize = 11.sp,
                            color = MutedGray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    livePurchases.take(4).forEach { purchase ->
                        LivePurchaseItem(
                            purchase = purchase,
                            onBatchClick = { onNavigateToTrack(purchase.batchId) }
                        )
                    }
                }
            }
        }

        // Interactive Vegetable Stalls (Batches with "Simulate Purchase")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Interactive Vegetable Market",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                    Text(
                        text = "Explore registered produce and simulate shopper buying",
                        fontSize = 11.sp,
                        color = MutedGray
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = LimeGreen.copy(alpha = 0.2f),
                    modifier = Modifier.clickable { onResetDemoInventory() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = DeepGreen, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Reset Stock",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Produce Filter Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ProduceFilterPill(
                    label = "All (${batches.size})",
                    isSelected = selectedFilterVeg == null,
                    onClick = { selectedFilterVeg = null }
                )
                val uniqueVegs = batches.map { it.vegetableName }.distinct()
                uniqueVegs.forEach { veg ->
                    val vegType = VegetableType.fromString(veg)
                    ProduceFilterPill(
                        label = "${vegType.emoji} $veg",
                        isSelected = selectedFilterVeg == veg,
                        onClick = { selectedFilterVeg = veg }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Vegetable Product Cards
            val displayedBatches = batches.filter { b ->
                selectedFilterVeg == null || b.vegetableName.equals(selectedFilterVeg, ignoreCase = true)
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                displayedBatches.forEach { batch ->
                    MarketBatchCard(
                        batch = batch,
                        onBuyClick = { selectedBatchForPurchase = batch },
                        onTrackClick = { onNavigateToTrack(batch.batchId) }
                    )
                }
            }
        }
    }

    // Modal: User Manual Purchase Simulation
    if (selectedBatchForPurchase != null) {
        SimulatePurchaseDialog(
            batch = selectedBatchForPurchase!!,
            onDismiss = { selectedBatchForPurchase = null },
            onConfirm = { qty ->
                onSimulateUserPurchase(selectedBatchForPurchase!!.batchId, qty)
                selectedBatchForPurchase = null
                Toast.makeText(context, "Simulated purchase confirmed! Inventory updated.", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun MarketStatCard(
    label: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2EBE2))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label.uppercase(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = MutedGray,
                    letterSpacing = 0.5.sp
                )
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                color = ForestGreen
            )
            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = FreshGreen,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SalesBarVisualizer(
    purchases: List<SimulatedPurchase>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5EDE4))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Market Sales Activity By Produce",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                )
                Text(
                    text = "Live Demo Ledger",
                    fontSize = 10.sp,
                    color = LimeGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val purchasesByVeg = purchases.groupBy { it.vegetableName }
            if (purchasesByVeg.isEmpty()) {
                Text(
                    text = "Waiting for customer purchases...",
                    fontSize = 10.sp,
                    color = MutedGray
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    purchasesByVeg.entries.take(4).forEach { (veg, list) ->
                        val totalKg = list.sumOf { it.quantityKg }
                        val vegType = VegetableType.fromString(veg)
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${totalKg.roundToInt()}kg",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreen
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(18.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(vegType.iconColorHex).copy(alpha = 0.85f))
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = vegType.emoji,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LivePurchaseItem(
    purchase: SimulatedPurchase,
    onBatchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2EBE2))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Customer Avatar Circle
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(purchase.customerAvatarColor)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = purchase.customerName.take(1),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = purchase.customerName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "bought ${purchase.quantityKg.toInt()} kg ${purchase.vegetableName}",
                            fontSize = 12.sp,
                            color = DeepGreen
                        )
                    }
                    Text(
                        text = "Batch ${purchase.batchId} · ${purchase.shopName}",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MutedGray,
                        modifier = Modifier.clickable { onBatchClick() }
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${purchase.totalPrice.roundToInt()}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = FreshGreen
                )
                Text(
                    text = purchase.formattedTime,
                    fontSize = 9.sp,
                    color = MutedGray
                )
            }
        }
    }
}

@Composable
private fun ProduceFilterPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) ForestGreen else Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) ForestGreen else Color(0xFFD8E2D5)),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) CreamWhite else ForestGreen,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun MarketBatchCard(
    batch: VegetableBatch,
    onBuyClick: () -> Unit,
    onTrackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSoldOut = batch.remainingDemoStock <= 0.0

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSoldOut) Color(0xFFF9FAFB) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSoldOut) Color(0xFFE5E7EB) else Color(0xFFE2EBE2)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
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
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = ForestGreen
                    )
                }

                if (isSoldOut) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFEE2E2)
                    ) {
                        Text(
                            text = "SOLD OUT",
                            color = Color(0xFFDC2626),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = FreshGreen.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "${batch.remainingDemoStock.roundToInt()} ${batch.unit} left",
                            color = DeepGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Farm: ${batch.farmName}",
                        fontSize = 12.sp,
                        color = MutedGray
                    )
                    Text(
                        text = "Price: ₹${batch.pricePerKg.roundToInt()} / kg · ${batch.qualityGrade}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EarthBrown
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onTrackClick,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Track", fontSize = 11.sp)
                    }

                    Button(
                        onClick = onBuyClick,
                        enabled = !isSoldOut,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FreshGreen)
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Simulate Buy", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SimulatePurchaseDialog(
    batch: VegetableBatch,
    onDismiss: () -> Unit,
    onConfirm: (quantityKg: Double) -> Unit
) {
    val maxBuy = minOf(10.0, batch.remainingDemoStock)
    var purchaseQty by remember { mutableDoubleStateOf(minOf(2.0, maxBuy)) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = FreshGreen.copy(alpha = 0.15f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = FreshGreen, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Simulate Customer Purchase", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                        Text("Batch ${batch.batchId} (${batch.vegetableName})", fontSize = 11.sp, color = MutedGray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF6F8F4),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Available Demo Stock: ${batch.remainingDemoStock.roundToInt()} ${batch.unit}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeepGreen)
                        Text("Unit Price: ₹${batch.pricePerKg.roundToInt()} per kg", fontSize = 11.sp, color = MutedGray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Select Quantity to Buy: ${purchaseQty.toInt()} kg", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ForestGreen)

                Slider(
                    value = purchaseQty.toFloat(),
                    onValueChange = { purchaseQty = it.toDouble().coerceIn(1.0, maxBuy) },
                    valueRange = 1f..maxBuy.toFloat().coerceAtLeast(1f),
                    steps = (maxBuy.toInt() - 2).coerceAtLeast(0),
                    colors = SliderDefaults.colors(thumbColor = FreshGreen, activeTrackColor = FreshGreen)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("1 kg", fontSize = 10.sp, color = MutedGray)
                    Text("${maxBuy.toInt()} kg", fontSize = 10.sp, color = MutedGray)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFEF3C7),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "ℹ️ This is a simulated demo transaction for educational evaluation. No real payment or delivery is initiated.",
                        fontSize = 10.sp,
                        color = Color(0xFF92400E),
                        modifier = Modifier.padding(10.dp),
                        lineHeight = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = { onConfirm(purchaseQty) },
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FreshGreen)
                    ) {
                        Text("Confirm (₹${(purchaseQty * batch.pricePerKg).roundToInt()})", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
