package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.BatchStatus
import com.example.data.model.DataMode
import com.example.data.model.DemoMarketStats
import com.example.data.model.EventType
import com.example.data.model.JourneyEvent
import com.example.data.model.SimulatedPurchase
import com.example.data.model.VegetableBatch
import com.example.ui.components.BatchStatusChip
import com.example.ui.components.LivePurchaseItem
import com.example.ui.components.PrintableLabelDialog
import com.example.ui.components.VegetableBadge
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.DeepGreen
import com.example.ui.theme.EarthBrown
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.LimeGreen
import com.example.ui.theme.MutedGray
import com.example.ui.theme.StatusDelivered
import com.example.ui.theme.StatusMarket
import com.example.ui.theme.StatusTransit
import kotlin.math.roundToInt

@Composable
fun FarmerDashboardScreen(
    batches: List<VegetableBatch>,
    eventsByBatch: Map<String, List<JourneyEvent>>,
    dataMode: DataMode,
    marketStats: DemoMarketStats,
    livePurchases: List<SimulatedPurchase>,
    onNavigateToRegister: () -> Unit,
    onNavigateToTrack: (String) -> Unit,
    onAddJourneyStop: (
        batchId: String,
        eventType: EventType,
        locationName: String,
        cityRegion: String,
        distanceFromPrevKm: Double,
        handlerRole: String,
        description: String,
        temperatureC: Double?,
        humidityPercent: Int?
    ) -> Unit,
    onResetDemoRecords: () -> Unit,
    onExportCsv: () -> String,
    onSwitchToOriginalMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterStatus by remember { mutableStateOf<BatchStatus?>(null) }

    // Dialog state for Adding a Stop
    var activeBatchForStop by remember { mutableStateOf<VegetableBatch?>(null) }
    // Dialog state for QR Code Label
    var activeBatchForLabel by remember { mutableStateOf<VegetableBatch?>(null) }
    // Safety dialog when trying to register in Demo Mode
    var showDemoRegistrationSafetyDialog by remember { mutableStateOf(false) }

    // Computations
    val totalBatches = batches.size
    val totalEvents = eventsByBatch.values.sumOf { it.size }
    val inTransitCount = batches.count { it.currentStatus == BatchStatus.IN_TRANSIT }
    val deliveredCount = batches.count { it.currentStatus == BatchStatus.DELIVERED || it.currentStatus == BatchStatus.AT_RETAIL_SHOP }
    val totalDistance = eventsByBatch.values.flatten().sumOf { it.distanceFromPrevKm }

    val filteredBatches = batches.filter { b ->
        val matchesSearch = b.batchId.contains(searchQuery, ignoreCase = true) ||
                b.vegetableName.contains(searchQuery, ignoreCase = true) ||
                b.farmName.contains(searchQuery, ignoreCase = true)
        val matchesFilter = selectedFilterStatus == null || b.currentStatus == selectedFilterStatus
        matchesSearch && matchesFilter
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (dataMode == DataMode.DEMO) {
                        showDemoRegistrationSafetyDialog = true
                    } else {
                        onNavigateToRegister()
                    }
                },
                containerColor = FreshGreen,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("fab_register_batch")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Register Batch")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Batch", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CreamWhite)
                .testTag("farmer_dashboard_column")
        ) {
            // Dashboard Header Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ForestGreen)
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "FARMER MANAGEMENT SUITE",
                                        color = LimeGreen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (dataMode == DataMode.DEMO) Color(0xFFF59E0B) else FreshGreen
                                    ) {
                                        Text(
                                            text = if (dataMode == DataMode.DEMO) "DEMO" else "LIVE",
                                            color = ForestGreen,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = if (dataMode == DataMode.DEMO) "Simulated Batch Ledger" else "Verified Farm Ledger",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Row {
                                IconButton(
                                    onClick = {
                                        val csvData = onExportCsv()
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("FarmTrace CSV Export", csvData))

                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/csv"
                                            putExtra(Intent.EXTRA_SUBJECT, "FarmTrace Supply Chain Ledger Export")
                                            putExtra(Intent.EXTRA_TEXT, csvData)
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "Export FarmTrace CSV"))
                                        Toast.makeText(context, "CSV copied & opened share sheet!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.15f))
                                ) {
                                    Icon(Icons.Default.CloudDownload, contentDescription = "Export CSV", tint = LimeGreen)
                                }

                                if (dataMode == DataMode.DEMO) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = {
                                            onResetDemoRecords()
                                            Toast.makeText(context, "Reset demo data & inventory!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.15f))
                                    ) {
                                        Icon(Icons.Default.Refresh, contentDescription = "Reset Demo Data", tint = Color.White)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Metric Stat Cards Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DashboardStatCard(
                                title = "Batches",
                                value = "$totalBatches",
                                subtitle = "Active lots",
                                icon = Icons.Default.Agriculture,
                                tint = LimeGreen,
                                modifier = Modifier.weight(1f)
                            )
                            DashboardStatCard(
                                title = "In Transit",
                                value = "$inTransitCount",
                                subtitle = "Cold chain",
                                icon = Icons.Default.LocalShipping,
                                tint = StatusTransit,
                                modifier = Modifier.weight(1f)
                            )
                            DashboardStatCard(
                                title = "Stops Logged",
                                value = "$totalEvents",
                                subtitle = "${totalDistance.toInt()} km traced",
                                icon = Icons.Default.Route,
                                tint = Color(0xFF64B5F6),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Demo Mode: Separate Market Simulation Statistics Card
            if (dataMode == DataMode.DEMO) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LimeGreen.copy(alpha = 0.6f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Storefront, contentDescription = null, tint = DeepGreen, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "DEMO MARKETPLACE TELEMETRY",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        color = ForestGreen,
                                        letterSpacing = 0.8.sp
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFFEF3C7)
                                ) {
                                    Text(
                                        text = "NOT REAL SALES",
                                        color = Color(0xFF92400E),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Simulated Sales", fontSize = 10.sp, color = MutedGray)
                                    Text("₹${marketStats.totalSalesRupees.roundToInt()}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = FreshGreen)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Quantity Sold", fontSize = 10.sp, color = MutedGray)
                                    Text("${marketStats.totalKgSold.roundToInt()} kg", fontSize = 16.sp, fontWeight = FontWeight.Black, color = ForestGreen)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Available Stock", fontSize = 10.sp, color = MutedGray)
                                    Text("${marketStats.availableDemoInventoryKg.roundToInt()} kg", fontSize = 16.sp, fontWeight = FontWeight.Black, color = StatusTransit)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Customers", fontSize = 10.sp, color = MutedGray)
                                    Text("${marketStats.totalCustomersCount}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = DeepGreen)
                                }
                            }
                        }
                    }
                }
            }

            // Quick Actions Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            if (dataMode == DataMode.DEMO) {
                                showDemoRegistrationSafetyDialog = true
                            } else {
                                onNavigateToRegister()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("dashboard_register_batch_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Register Batch", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            val csvData = onExportCsv()
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("FarmTrace CSV Export", csvData))
                            Toast.makeText(context, "Copied ledger CSV to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export CSV", fontSize = 12.sp)
                    }
                }
            }

            // Search Bar & Status Filter Row
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search by Batch ID, Farm, or Produce...", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = FreshGreen) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .testTag("dashboard_search_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Horizontal Filter Pills
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterPill(
                            label = "All (${batches.size})",
                            isSelected = selectedFilterStatus == null,
                            onClick = { selectedFilterStatus = null }
                        )
                        BatchStatus.entries.forEach { status ->
                            val count = batches.count { it.currentStatus == status }
                            FilterPill(
                                label = "${status.displayName} ($count)",
                                isSelected = selectedFilterStatus == status,
                                onClick = { selectedFilterStatus = status }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            // Batches List
            if (filteredBatches.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No batches found matching filter",
                            color = MutedGray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(filteredBatches) { batch ->
                    val events = eventsByBatch[batch.batchId] ?: emptyList()
                    FarmerBatchCard(
                        batch = batch,
                        eventsCount = events.size,
                        latestEvent = events.lastOrNull(),
                        onViewJourney = { onNavigateToTrack(batch.batchId) },
                        onViewQr = { activeBatchForLabel = batch },
                        onAddStop = { activeBatchForStop = batch },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Modal: Safety dialog when user registers in Demo mode
    if (showDemoRegistrationSafetyDialog) {
        Dialog(onDismissRequest = { showDemoRegistrationSafetyDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Demo Mode Active", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = ForestGreen)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "You are currently viewing simulated demo data. Do you want to switch to Original mode to record real farmer data, or continue creating a demo batch for the simulated marketplace?",
                        fontSize = 12.sp,
                        color = Color(0xFF455A64),
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                showDemoRegistrationSafetyDialog = false
                                onNavigateToRegister()
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Stay in Demo", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                showDemoRegistrationSafetyDialog = false
                                onSwitchToOriginalMode()
                                onNavigateToRegister()
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Text("Switch to Original", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Modal: Add Journey Stop Dialog
    if (activeBatchForStop != null) {
        AddJourneyStopDialog(
            batch = activeBatchForStop!!,
            onDismiss = { activeBatchForStop = null },
            onSubmit = { eventType, location, city, dist, role, desc, temp, humidity ->
                onAddJourneyStop(
                    activeBatchForStop!!.batchId,
                    eventType,
                    location,
                    city,
                    dist,
                    role,
                    desc,
                    temp,
                    humidity
                )
                activeBatchForStop = null
                Toast.makeText(context, "Added milestone to batch ${activeBatchForStop?.batchId}!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Modal: Printable QR Label
    if (activeBatchForLabel != null) {
        PrintableLabelDialog(
            batch = activeBatchForLabel!!,
            onDismiss = { activeBatchForLabel = null }
        )
    }
}

@Composable
private fun DashboardStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = LimeGreen,
                    letterSpacing = 0.5.sp
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(15.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun FilterPill(
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
fun FarmerBatchCard(
    batch: VegetableBatch,
    eventsCount: Int,
    latestEvent: JourneyEvent?,
    onViewJourney: () -> Unit,
    onViewQr: () -> Unit,
    onAddStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${batch.quantity} ${batch.unit} · ${batch.variety.ifEmpty { "Fresh Harvest" }}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreen
            )

            Text(
                text = "Farm: ${batch.farmName} (${batch.farmLocation})",
                fontSize = 12.sp,
                color = MutedGray
            )

            if (latestEvent != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = Color(0xFFF1F6EE),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Route, contentDescription = null, tint = FreshGreen, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Last stop: ${latestEvent.locationName} (${latestEvent.recordedTimestamp})",
                            fontSize = 11.sp,
                            color = DeepGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewJourney,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Timeline, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Journey", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onViewQr,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("QR Label", fontSize = 11.sp)
                }

                Button(
                    onClick = onAddStop,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                    modifier = Modifier.weight(1.2f)
                ) {
                    Icon(Icons.Default.AddLocation, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Stop", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddJourneyStopDialog(
    batch: VegetableBatch,
    onDismiss: () -> Unit,
    onSubmit: (
        eventType: EventType,
        locationName: String,
        cityRegion: String,
        distanceFromPrevKm: Double,
        handlerRole: String,
        description: String,
        temperatureC: Double?,
        humidityPercent: Int?
    ) -> Unit
) {
    var selectedEventType by remember { mutableStateOf(EventType.COLD_CHAIN_TRANSIT) }
    var eventTypeExpanded by remember { mutableStateOf(false) }

    var locationName by remember { mutableStateOf("Bay Area Cold Reefer Logistics #308") }
    var cityRegion by remember { mutableStateOf("Interstate 280 Corridor, CA") }
    var distanceStr by remember { mutableStateOf("45.0") }
    var handlerRole by remember { mutableStateOf("Certified Fleet Driver (Dave J.)") }
    var temperatureStr by remember { mutableStateOf("12.5") }
    var description by remember { mutableStateOf("Transferred under continuous monitored climate control.") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                item {
                    Text(
                        text = "Record Journey Stop",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                    Text(
                        text = "Add verified milestone for Batch ${batch.batchId} (${batch.vegetableName})",
                        fontSize = 12.sp,
                        color = MutedGray
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Event Type Dropdown
                    Text("Milestone Stage", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                    Spacer(modifier = Modifier.height(4.dp))
                    ExposedDropdownMenuBox(
                        expanded = eventTypeExpanded,
                        onExpandedChange = { eventTypeExpanded = !eventTypeExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedEventType.title,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = eventTypeExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = eventTypeExpanded,
                            onDismissRequest = { eventTypeExpanded = false }
                        ) {
                            EventType.entries.forEach { ev ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(ev.title, fontWeight = FontWeight.Bold)
                                            Text(ev.stage.title, fontSize = 10.sp, color = MutedGray)
                                        }
                                    },
                                    onClick = {
                                        selectedEventType = ev
                                        eventTypeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Stop / Destination Name", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = locationName,
                        onValueChange = { locationName = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("City / Geographic Area", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = cityRegion,
                        onValueChange = { cityRegion = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Distance (km)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = distanceStr,
                                onValueChange = { distanceStr = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Temp (°C)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = temperatureStr,
                                onValueChange = { temperatureStr = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Handler Role & Name", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = handlerRole,
                        onValueChange = { handlerRole = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Event Description / Telemetry Notes", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                val dist = distanceStr.toDoubleOrNull() ?: 0.0
                                val temp = temperatureStr.toDoubleOrNull()
                                onSubmit(
                                    selectedEventType,
                                    locationName,
                                    cityRegion,
                                    dist,
                                    handlerRole,
                                    description,
                                    temp,
                                    65
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FreshGreen)
                        ) {
                            Text("Commit Stop", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
