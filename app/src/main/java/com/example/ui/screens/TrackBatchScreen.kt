package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JourneyEvent
import com.example.data.model.JourneyStage
import com.example.data.model.VegetableBatch
import com.example.ui.components.BatchStatusChip
import com.example.ui.components.OrganicCertificationBadge
import com.example.ui.components.QrCodeCard
import com.example.ui.components.VegetableBadge
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.DeepGreen
import com.example.ui.theme.EarthBrown
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.LimeGreen
import com.example.ui.theme.MutedGray
import com.example.ui.theme.StatusDelivered

@Composable
fun TrackBatchScreen(
    batch: VegetableBatch?,
    events: List<JourneyEvent>,
    onSearchBatch: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToScan: () -> Unit,
    allBatches: List<VegetableBatch>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchInput by remember { mutableStateOf(batch?.batchId ?: "") }
    var showQrSection by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CreamWhite)
            .testTag("track_batch_screen_column")
    ) {
        // Top Bar & Batch Lookup Field
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ForestGreen)
                    .padding(16.dp)
            ) {
                Column {
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
                                tint = CreamWhite
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "PUBLIC SHOPPER PORTAL",
                                color = LimeGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "YOUR FOOD HAS A STORY.",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        IconButton(
                            onClick = onNavigateToScan,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(LimeGreen)
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "Scan QR",
                                tint = ForestGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Lookup Input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchInput,
                            onValueChange = { searchInput = it },
                            placeholder = { Text("Enter Batch ID (e.g. FT-2026-000123)", fontSize = 13.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.White, RoundedCornerShape(12.dp))
                                .testTag("batch_search_input"),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (searchInput.isNotBlank()) {
                                    onSearchBatch(searchInput.trim())
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LimeGreen),
                            modifier = Modifier.testTag("batch_search_submit_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Lookup",
                                tint = ForestGreen
                            )
                        }
                    }

                    // Quick Switch Demo Pills
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        allBatches.take(4).forEach { b ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (b.batchId == batch?.batchId) LimeGreen else Color.White.copy(alpha = 0.15f),
                                modifier = Modifier.clickable {
                                    searchInput = b.batchId
                                    onSearchBatch(b.batchId)
                                }
                            ) {
                                Text(
                                    text = b.vegetableName,
                                    fontSize = 11.sp,
                                    color = if (b.batchId == batch?.batchId) ForestGreen else CreamWhite,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (batch == null) {
            // Not Found State
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MutedGray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Batch Not Found",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "We couldn't locate records for ID: \"$searchInput\". Please verify the code or scan the QR on the produce pack.",
                        fontSize = 13.sp,
                        color = MutedGray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onNavigateToScan,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                    ) {
                        Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null, tint = LimeGreen)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Open QR Scanner", color = CreamWhite)
                    }
                }
            }
        } else {
            // Batch Detail Hero Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2EBE2))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            VegetableBadge(vegetableName = batch.vegetableName)
                            BatchStatusChip(status = batch.currentStatus)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "${batch.vegetableName} · ${batch.variety.ifEmpty { "Fresh Harvest" }}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = ForestGreen
                        )

                        Text(
                            text = "Batch ${batch.batchId}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = FreshGreen
                        )

                        if (batch.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = batch.description,
                                fontSize = 12.sp,
                                color = MutedGray,
                                lineHeight = 17.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Produce Metrics Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MetricBox(
                                label = "Origin Farm",
                                value = batch.farmName,
                                sub = batch.farmLocation,
                                modifier = Modifier.weight(1.2f)
                            )
                            MetricBox(
                                label = "Harvest Date",
                                value = batch.harvestDate,
                                sub = batch.qualityGrade,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MetricBox(
                                label = "Registered Batch",
                                value = "${batch.quantity} ${batch.unit}",
                                sub = "Verified Lot",
                                modifier = Modifier.weight(1f)
                            )
                            MetricBox(
                                label = "Traceability",
                                value = "${events.size} Milestones",
                                sub = "Cryptographic Record",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        OrganicCertificationBadge(isOrganic = batch.organicCertified)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Interactive Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("FarmTrace ID", batch.batchId)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Copied Batch ID: ${batch.batchId}", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Copy ID", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, "FarmTrace: ${batch.vegetableName}")
                                        putExtra(Intent.EXTRA_TEXT, "Trace the verified journey of ${batch.vegetableName} from ${batch.farmName}:\n${batch.qrPayloadUrl}")
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share Produce Journey"))
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Share", fontSize = 12.sp)
                            }

                            Button(
                                onClick = { showQrSection = !showQrSection },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.QrCode, contentDescription = null, tint = LimeGreen, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (showQrSection) "Hide QR" else "View QR", fontSize = 12.sp, color = CreamWhite)
                            }
                        }
                    }
                }
            }

            // Collapsible QR Code Card
            item {
                AnimatedVisibility(visible = showQrSection) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        QrCodeCard(batch = batch, showActions = true)
                    }
                }
            }

            // Timeline Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "RECORDED JOURNEY TIMELINE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = FreshGreen,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "From Field Harvest to Table",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                    Text(
                        text = "Chronological event logs recorded by authorized handlers at each supply stop.",
                        fontSize = 12.sp,
                        color = MutedGray
                    )
                }
            }

            // Vertical Timeline Items
            itemsIndexed(events) { index, event ->
                val isLatest = index == events.lastIndex
                TimelineEventCard(
                    event = event,
                    isFirst = index == 0,
                    isLatest = isLatest,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Transparency & Consumer Trust Notice
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F6EE)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FreshGreen.copy(alpha = 0.35f))
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
                                text = "Transparency Notice",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
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

                // Scan Another Batch CTA
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = onNavigateToScan,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = LimeGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Scan Another Produce Batch", color = CreamWhite, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun MetricBox(
    label: String,
    value: String,
    sub: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF6F8F4),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5EDE4))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = label.uppercase(),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = MutedGray,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreen
            )
            Text(
                text = sub,
                fontSize = 10.sp,
                color = FreshGreen
            )
        }
    }
}

@Composable
fun TimelineEventCard(
    event: JourneyEvent,
    isFirst: Boolean,
    isLatest: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Vertical Timeline Column (Node + Connecting Line)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(36.dp)
        ) {
            // Node
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (isLatest) LimeGreen else FreshGreen)
                    .border(2.dp, if (isLatest) ForestGreen else FreshGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (event.stage) {
                        JourneyStage.FARM -> Icons.Default.Agriculture
                        JourneyStage.WHOLESALE_MARKET -> Icons.Default.Store
                        JourneyStage.TRANSPORTATION -> Icons.Default.LocalShipping
                        JourneyStage.RETAIL_SHOP -> Icons.Default.ShoppingCart
                        JourneyStage.CONSUMER_KITCHEN -> Icons.Default.Kitchen
                    },
                    contentDescription = null,
                    tint = if (isLatest) ForestGreen else Color.White,
                    modifier = Modifier.size(15.dp)
                )
            }

            // Connecting vertical line
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(90.dp)
                    .background(if (isLatest) Color(0xFFCBDAD1) else FreshGreen.copy(alpha = 0.6f))
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Event Card Content
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isLatest) Color(0xFFF1F9EE) else Color.White
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isLatest) FreshGreen else Color(0xFFE2EBE2)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isLatest) 2.dp else 1.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = event.eventType.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                    if (isLatest) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = LimeGreen
                        ) {
                            Text(
                                text = "LATEST STAGE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = ForestGreen,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = FreshGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${event.locationName} (${event.cityRegion})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepGreen
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = event.description,
                    fontSize = 12.sp,
                    color = Color(0xFF37474F),
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Telemetry Row: Distance, Temp, Handler, Timestamp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Handler: ${event.handlerRole}",
                            fontSize = 10.sp,
                            color = MutedGray,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = event.recordedTimestamp,
                            fontSize = 10.sp,
                            color = EarthBrown,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (event.distanceFromPrevKm > 0) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFECEFF1)
                            ) {
                                Text(
                                    text = "+${event.distanceFromPrevKm} km",
                                    fontSize = 10.sp,
                                    color = Color(0xFF455A64),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }

                        if (event.temperatureC != null) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFE8F5E9)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Thermostat,
                                        contentDescription = null,
                                        tint = FreshGreen,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = "${event.temperatureC}°C",
                                        fontSize = 10.sp,
                                        color = DeepGreen,
                                        fontWeight = FontWeight.Bold
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
