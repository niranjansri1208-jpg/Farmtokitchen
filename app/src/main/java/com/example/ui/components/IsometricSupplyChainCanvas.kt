package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JourneyStage
import com.example.data.model.VegetableType
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.DeepGreen
import com.example.ui.theme.EarthBrown
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.LimeGreen
import com.example.ui.theme.MutedGray
import kotlin.math.cos
import kotlin.math.sin

data class StationInfo(
    val stage: JourneyStage,
    val name: String,
    val subtitle: String,
    val details: String,
    val normX: Float,
    val normY: Float
)

@Composable
fun IsometricSupplyChainCanvas(
    modifier: Modifier = Modifier,
    onStationClick: (JourneyStage) -> Unit = {}
) {
    var isPlaying by remember { mutableStateOf(true) }
    var isDuskMode by remember { mutableStateOf(false) }
    var selectedStationIndex by remember { mutableIntStateOf(0) }

    val infiniteTransition = rememberInfiniteTransition(label = "SupplyChainMotion")

    // Smooth continuous truck progress
    val truckProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "TruckProgress"
    )

    // Floating vegetation bobbing
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FloatOffset"
    )

    // Windmill rotation angle
    val windmillAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WindmillAngle"
    )

    // Pulsing glow for route
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseGlow"
    )

    val stations = remember {
        listOf(
            StationInfo(JourneyStage.FARM, "1. Organic Farm", "Field #4 Salinas Valley", "Harvested at peak Brix ripeness with natural drip irrigation", 0.12f, 0.72f),
            StationInfo(JourneyStage.WHOLESALE_MARKET, "2. Wholesale Mandi", "Central Regional Terminal", "Optical sorting, weight verification & cold buffer staging", 0.35f, 0.38f),
            StationInfo(JourneyStage.TRANSPORTATION, "3. Cold Chain Transit", "Reefer Fleet #308 (4°C)", "Real-time telemetry tracking route with climate control", 0.60f, 0.65f),
            StationInfo(JourneyStage.RETAIL_SHOP, "4. Supermarket Shelf", "FreshMart Store Aisle 2", "Misted display racks with batch QR traceability cards", 0.82f, 0.32f),
            StationInfo(JourneyStage.CONSUMER_KITCHEN, "5. Table / Kitchen", "Farm-to-Fork Salad Bowl", "Scanned by shopper & enjoyed fresh at dinner", 0.90f, 0.78f)
        )
    }

    val activeProgress = if (isPlaying) truckProgress else (selectedStationIndex / 4f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(if (isDuskMode) Color(0xFF0D1B13) else Color(0xFF133624))
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        val canvasW = size.width
                        val canvasH = size.height
                        // Check proximity to stations
                        stations.forEachIndexed { index, station ->
                            val sx = station.normX * canvasW
                            val sy = station.normY * canvasH
                            val distance = kotlin.math.hypot((tapOffset.x - sx).toDouble(), (tapOffset.y - sy).toDouble())
                            if (distance < 50.dp.toPx()) {
                                selectedStationIndex = index
                                onStationClick(station.stage)
                            }
                        }
                    }
                }
        ) {
            val width = size.width
            val height = size.height

            // 1. Draw Environmental Gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = if (isDuskMode) {
                        listOf(Color(0xFF0F172A), Color(0xFF132F20), Color(0xFF0C1F16))
                    } else {
                        listOf(Color(0xFF1A4731), Color(0xFF153D2B), Color(0xFF0E2A1D))
                    }
                )
            )

            // 2. Draw 3D Landscape Contour Grids
            drawLandscapeContours(this, width, height, isDuskMode)

            // 3. Define Supply Chain Route Points
            val p0 = Offset(stations[0].normX * width, stations[0].normY * height)
            val p1 = Offset(stations[1].normX * width, stations[1].normY * height)
            val p2 = Offset(stations[2].normX * width, stations[2].normY * height)
            val p3 = Offset(stations[3].normX * width, stations[3].normY * height)
            val p4 = Offset(stations[4].normX * width, stations[4].normY * height)

            val routePath = Path().apply {
                moveTo(p0.x, p0.y)
                cubicTo(
                    p0.x + (p1.x - p0.x) * 0.3f, p0.y - 40f,
                    p1.x - (p1.x - p0.x) * 0.3f, p1.y + 40f,
                    p1.x, p1.y
                )
                cubicTo(
                    p1.x + (p2.x - p1.x) * 0.4f, p1.y + 60f,
                    p2.x - (p2.x - p1.x) * 0.4f, p2.y - 60f,
                    p2.x, p2.y
                )
                cubicTo(
                    p2.x + (p3.x - p2.x) * 0.3f, p2.y - 50f,
                    p3.x - (p3.x - p2.x) * 0.3f, p3.y + 50f,
                    p3.x, p3.y
                )
                cubicTo(
                    p3.x + (p4.x - p3.x) * 0.4f, p3.y + 60f,
                    p4.x - (p4.x - p3.x) * 0.4f, p4.y - 40f,
                    p4.x, p4.y
                )
            }

            // Draw Route Ambient Glow & Road Bed
            drawPath(
                path = routePath,
                color = FreshGreen.copy(alpha = 0.25f * pulseGlow),
                style = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Round)
            )
            drawPath(
                path = routePath,
                color = Color(0xFF225539),
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )
            // Animated glowing dashes along route
            drawPath(
                path = routePath,
                color = LimeGreen,
                style = Stroke(
                    width = 4.dp.toPx(),
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), activeProgress * 200f)
                )
            )

            // 4. Draw 3D Hubs / Stations
            stations.forEachIndexed { index, station ->
                val pos = Offset(station.normX * width, station.normY * height)
                val isSelected = index == selectedStationIndex

                when (station.stage) {
                    JourneyStage.FARM -> drawFarmHub(this, pos, windmillAngle, isSelected)
                    JourneyStage.WHOLESALE_MARKET -> drawMarketHub(this, pos, isSelected)
                    JourneyStage.TRANSPORTATION -> drawLogisticsHub(this, pos, isSelected)
                    JourneyStage.RETAIL_SHOP -> drawSupermarketHub(this, pos, isSelected)
                    JourneyStage.CONSUMER_KITCHEN -> drawKitchenHub(this, pos, isSelected)
                }
            }

            // 5. Draw Animated Delivery Truck Along Route
            val truckPos = evaluateRoutePoint(activeProgress, p0, p1, p2, p3, p4)
            drawAnimatedDeliveryTruck(this, truckPos, activeProgress, floatOffset)

            // 6. Floating Vegetables in Foreground with gentle bobbing
            drawFloatingVegetable(this, Offset(width * 0.22f, height * 0.20f + floatOffset), VegetableType.TOMATO)
            drawFloatingVegetable(this, Offset(width * 0.50f, height * 0.15f - floatOffset), VegetableType.CARROT)
            drawFloatingVegetable(this, Offset(width * 0.74f, height * 0.72f + floatOffset * 0.7f), VegetableType.POTATO)
            drawFloatingVegetable(this, Offset(width * 0.88f, height * 0.18f - floatOffset * 0.8f), VegetableType.SPINACH)
        }

        // Overlay: Top Navigation Bar inside Canvas
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = ForestGreen.copy(alpha = 0.88f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, LimeGreen.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isPlaying) LimeGreen else Color(0xFFF59E0B))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isPlaying) "LIVE 3D SUPPLY CHAIN" else "PAUSED · INSPECT",
                        color = CreamWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Row {
                IconButton(
                    onClick = { isDuskMode = !isDuskMode },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ForestGreen.copy(alpha = 0.7f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Brightness4,
                        contentDescription = "Toggle Dusk/Day Lighting",
                        tint = LimeGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = { isPlaying = !isPlaying },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ForestGreen.copy(alpha = 0.7f))
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Pause/Play Simulation",
                        tint = LimeGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Overlay: Station Detail Card at Bottom
        val activeStation = stations[selectedStationIndex]
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(12.dp),
            colors = CardDefaults.cardColors(containerColor = ForestGreen.copy(alpha = 0.94f)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, LimeGreen.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = LimeGreen.copy(alpha = 0.2f),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = when (activeStation.stage) {
                                        JourneyStage.FARM -> Icons.Default.Agriculture
                                        JourneyStage.WHOLESALE_MARKET -> Icons.Default.Store
                                        JourneyStage.TRANSPORTATION -> Icons.Default.LocalShipping
                                        JourneyStage.RETAIL_SHOP -> Icons.Default.ShoppingCart
                                        JourneyStage.CONSUMER_KITCHEN -> Icons.Default.Kitchen
                                    },
                                    contentDescription = null,
                                    tint = LimeGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = activeStation.name,
                                color = CreamWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = activeStation.subtitle,
                                color = LimeGreen,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Surface(
                        color = Color.White.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Tap to track",
                            color = CreamWhite,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = activeStation.details,
                    color = CreamWhite.copy(alpha = 0.85f),
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

private fun drawLandscapeContours(drawScope: DrawScope, w: Float, h: Float, isDusk: Boolean) {
    val contourPaint = if (isDusk) Color(0xFF163827) else Color(0xFF1F5238)
    // Organic field contour patches
    val p1 = Path().apply {
        moveTo(0f, h * 0.5f)
        cubicTo(w * 0.2f, h * 0.45f, w * 0.35f, h * 0.65f, w * 0.4f, h)
        lineTo(0f, h)
        close()
    }
    drawScope.drawPath(p1, color = contourPaint.copy(alpha = 0.45f))

    val p2 = Path().apply {
        moveTo(w * 0.55f, 0f)
        cubicTo(w * 0.65f, h * 0.35f, w * 0.85f, h * 0.25f, w, h * 0.45f)
        lineTo(w, 0f)
        close()
    }
    drawScope.drawPath(p2, color = contourPaint.copy(alpha = 0.35f))

    // Crop furrow rows near farm
    for (i in 0..4) {
        val yOffset = h * 0.78f + (i * 12f)
        drawScope.drawLine(
            color = Color(0xFF2E6F4B).copy(alpha = 0.6f),
            start = Offset(15f, yOffset),
            end = Offset(w * 0.25f, yOffset + 18f),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )
    }
}

private fun drawFarmHub(drawScope: DrawScope, pos: Offset, windmillAngle: Float, isSelected: Boolean) {
    // Platform base
    drawScope.drawCircle(
        color = if (isSelected) LimeGreen else Color(0xFF2C6B47),
        radius = 26f,
        center = pos,
        style = if (isSelected) Stroke(width = 4f) else Fill
    )
    drawScope.drawCircle(
        color = Color(0xFF1B4E32),
        radius = 22f,
        center = pos
    )
    // Little red barn
    drawScope.drawRoundRect(
        color = Color(0xFFC0392B),
        topLeft = Offset(pos.x - 14f, pos.y - 12f),
        size = Size(20f, 16f),
        cornerRadius = CornerRadius(3f, 3f)
    )
    // Barn roof
    val roof = Path().apply {
        moveTo(pos.x - 16f, pos.y - 12f)
        lineTo(pos.x - 4f, pos.y - 24f)
        lineTo(pos.x + 8f, pos.y - 12f)
        close()
    }
    drawScope.drawPath(roof, color = Color(0xFFE74C3C))

    // Windmill tower & rotating blades
    val millX = pos.x + 12f
    val millY = pos.y - 8f
    drawScope.drawLine(
        color = Color(0xFFECEFF1),
        start = Offset(millX, millY),
        end = Offset(millX, millY - 20f),
        strokeWidth = 2.5f
    )
    // Windmill rotating sails
    for (i in 0..3) {
        val rad = Math.toRadians((windmillAngle + (i * 90)).toDouble())
        val bladeX = millX + (cos(rad) * 12f).toFloat()
        val bladeY = (millY - 20f) + (sin(rad) * 12f).toFloat()
        drawScope.drawLine(
            color = LimeGreen,
            start = Offset(millX, millY - 20f),
            end = Offset(bladeX, bladeY),
            strokeWidth = 2f
        )
    }
}

private fun drawMarketHub(drawScope: DrawScope, pos: Offset, isSelected: Boolean) {
    drawScope.drawCircle(
        color = if (isSelected) LimeGreen else Color(0xFF2C6B47),
        radius = 24f,
        center = pos,
        style = if (isSelected) Stroke(width = 4f) else Fill
    )
    drawScope.drawCircle(
        color = Color(0xFF1B4E32),
        radius = 20f,
        center = pos
    )
    // Market canopy (Striped tent)
    drawScope.drawRoundRect(
        color = EarthBrown,
        topLeft = Offset(pos.x - 14f, pos.y - 10f),
        size = Size(28f, 18f),
        cornerRadius = CornerRadius(4f, 4f)
    )
    drawScope.drawRoundRect(
        color = Color(0xFFF59E0B),
        topLeft = Offset(pos.x - 16f, pos.y - 18f),
        size = Size(32f, 9f),
        cornerRadius = CornerRadius(3f, 3f)
    )
    // Produce crates (tiny orange & red blocks)
    drawScope.drawRect(Color(0xFFE53935), Offset(pos.x - 8f, pos.y + 10f), Size(6f, 6f))
    drawScope.drawRect(Color(0xFFFF8F00), Offset(pos.x - 1f, pos.y + 10f), Size(6f, 6f))
    drawScope.drawRect(Color(0xFF43A047), Offset(pos.x + 6f, pos.y + 10f), Size(6f, 6f))
}

private fun drawLogisticsHub(drawScope: DrawScope, pos: Offset, isSelected: Boolean) {
    drawScope.drawCircle(
        color = if (isSelected) LimeGreen else Color(0xFF2C6B47),
        radius = 24f,
        center = pos,
        style = if (isSelected) Stroke(width = 4f) else Fill
    )
    drawScope.drawCircle(
        color = Color(0xFF1B4E32),
        radius = 20f,
        center = pos
    )
    // Depot loading dock
    drawScope.drawRoundRect(
        color = Color(0xFF455A64),
        topLeft = Offset(pos.x - 14f, pos.y - 12f),
        size = Size(28f, 22f),
        cornerRadius = CornerRadius(4f, 4f)
    )
    // Cold chain antenna
    drawScope.drawLine(
        color = LimeGreen,
        start = Offset(pos.x, pos.y - 12f),
        end = Offset(pos.x, pos.y - 24f),
        strokeWidth = 2f
    )
    drawScope.drawCircle(LimeGreen, radius = 3f, center = Offset(pos.x, pos.y - 24f))
}

private fun drawSupermarketHub(drawScope: DrawScope, pos: Offset, isSelected: Boolean) {
    drawScope.drawCircle(
        color = if (isSelected) LimeGreen else Color(0xFF2C6B47),
        radius = 24f,
        center = pos,
        style = if (isSelected) Stroke(width = 4f) else Fill
    )
    drawScope.drawCircle(
        color = Color(0xFF1B4E32),
        radius = 20f,
        center = pos
    )
    // Glass retail building
    drawScope.drawRoundRect(
        color = Color(0xFF1E88E5),
        topLeft = Offset(pos.x - 15f, pos.y - 14f),
        size = Size(30f, 24f),
        cornerRadius = CornerRadius(4f, 4f)
    )
    // Store sign
    drawScope.drawRoundRect(
        color = FreshGreen,
        topLeft = Offset(pos.x - 12f, pos.y - 20f),
        size = Size(24f, 7f),
        cornerRadius = CornerRadius(2f, 2f)
    )
}

private fun drawKitchenHub(drawScope: DrawScope, pos: Offset, isSelected: Boolean) {
    drawScope.drawCircle(
        color = if (isSelected) LimeGreen else Color(0xFF2C6B47),
        radius = 24f,
        center = pos,
        style = if (isSelected) Stroke(width = 4f) else Fill
    )
    drawScope.drawCircle(
        color = Color(0xFF1B4E32),
        radius = 20f,
        center = pos
    )
    // Dining table & fresh salad bowl
    drawScope.drawRoundRect(
        color = EarthBrown,
        topLeft = Offset(pos.x - 14f, pos.y - 10f),
        size = Size(28f, 18f),
        cornerRadius = CornerRadius(4f, 4f)
    )
    // Salad bowl (white with green leafage)
    drawScope.drawCircle(Color.White, radius = 7f, center = pos)
    drawScope.drawCircle(LimeGreen, radius = 4.5f, center = pos)
}

private fun evaluateRoutePoint(t: Float, p0: Offset, p1: Offset, p2: Offset, p3: Offset, p4: Offset): Offset {
    val scaledT = t * 4f
    return when {
        scaledT <= 1f -> {
            val localT = scaledT
            val inv = 1f - localT
            Offset(inv * p0.x + localT * p1.x, inv * p0.y + localT * p1.y)
        }
        scaledT <= 2f -> {
            val localT = scaledT - 1f
            val inv = 1f - localT
            Offset(inv * p1.x + localT * p2.x, inv * p1.y + localT * p2.y)
        }
        scaledT <= 3f -> {
            val localT = scaledT - 2f
            val inv = 1f - localT
            Offset(inv * p2.x + localT * p3.x, inv * p2.y + localT * p3.y)
        }
        else -> {
            val localT = (scaledT - 3f).coerceIn(0f, 1f)
            val inv = 1f - localT
            Offset(inv * p3.x + localT * p4.x, inv * p3.y + localT * p4.y)
        }
    }
}

private fun drawAnimatedDeliveryTruck(drawScope: DrawScope, pos: Offset, progress: Float, floatBob: Float) {
    val truckX = pos.x
    val truckY = pos.y

    // Shadow under truck
    drawScope.drawOval(
        color = Color.Black.copy(alpha = 0.35f),
        topLeft = Offset(truckX - 18f, truckY + 6f),
        size = Size(36f, 12f)
    )

    // Truck Refrigerated Cargo Box (Clean White / Lime accent)
    drawScope.drawRoundRect(
        color = Color.White,
        topLeft = Offset(truckX - 16f, truckY - 14f),
        size = Size(24f, 16f),
        cornerRadius = CornerRadius(3f, 3f)
    )
    // FarmTrace logo stripe on truck side
    drawScope.drawRect(
        color = FreshGreen,
        topLeft = Offset(truckX - 16f, truckY - 6f),
        size = Size(24f, 4f)
    )

    // Driver Cab
    drawScope.drawRoundRect(
        color = ForestGreen,
        topLeft = Offset(truckX + 8f, truckY - 9f),
        size = Size(12f, 12f),
        cornerRadius = CornerRadius(3f, 3f)
    )
    // Windshield
    drawScope.drawRoundRect(
        color = Color(0xFF81D4FA),
        topLeft = Offset(truckX + 11f, truckY - 7f),
        size = Size(7f, 5f),
        cornerRadius = CornerRadius(1f, 1f)
    )

    // Wheels
    drawScope.drawCircle(Color(0xFF263238), radius = 3.5f, center = Offset(truckX - 10f, truckY + 3f))
    drawScope.drawCircle(Color(0xFF263238), radius = 3.5f, center = Offset(truckX + 13f, truckY + 3f))

    // Clean energy sparkle exhaust
    drawScope.drawCircle(LimeGreen.copy(alpha = 0.7f), radius = 2f, center = Offset(truckX - 22f, truckY - 1f))
}

private fun drawFloatingVegetable(drawScope: DrawScope, pos: Offset, type: VegetableType) {
    val color = Color(type.iconColorHex)

    // Soft glow
    drawScope.drawCircle(
        color = color.copy(alpha = 0.3f),
        radius = 16f,
        center = pos
    )
    // Vegetable sphere
    drawScope.drawCircle(
        color = color,
        radius = 11f,
        center = pos
    )
    // Sprout / green cap
    drawScope.drawCircle(
        color = LimeGreen,
        radius = 4f,
        center = Offset(pos.x, pos.y - 9f)
    )
}
