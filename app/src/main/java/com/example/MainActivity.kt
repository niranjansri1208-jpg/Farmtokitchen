package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DataMode
import com.example.ui.AppScreen
import com.example.ui.FarmTraceViewModel
import com.example.ui.components.CinematicSplashScreen
import com.example.ui.screens.BatchRegistrationScreen
import com.example.ui.screens.FarmerAuthScreen
import com.example.ui.screens.FarmerDashboardScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.HowItWorksScreen
import com.example.ui.screens.ScanScreen
import com.example.ui.screens.TrackBatchScreen
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.DeepGreen
import com.example.ui.theme.FarmTraceTheme
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.LimeGreen
import com.example.ui.theme.MutedGray

class MainActivity : ComponentActivity() {
    private val viewModel: FarmTraceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FarmTraceTheme {
                FarmTraceApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun FarmTraceApp(viewModel: FarmTraceViewModel) {
    var showSplashScreen by remember { mutableStateOf(true) }

    val currentScreen by viewModel.currentScreen.collectAsState()
    val dataMode by viewModel.dataMode.collectAsState()
    val isLoadingModeSwitch by viewModel.isLoadingModeSwitch.collectAsState()
    val batches by viewModel.batches.collectAsState()
    val allEventsByBatch by viewModel.allEventsByBatch.collectAsState()
    val selectedBatchId by viewModel.selectedBatchId.collectAsState()
    val selectedBatchEvents by viewModel.selectedBatchEvents.collectAsState()
    val currentFarmerName by viewModel.currentFarmerName.collectAsState()
    val registeredBatchSuccess by viewModel.registeredBatchSuccess.collectAsState()
    val livePurchases by viewModel.livePurchasesFeed.collectAsState()
    val marketStats by viewModel.marketStats.collectAsState()
    val isSimulationRunning by viewModel.isSimulationRunning.collectAsState()
    val currentToast by viewModel.currentToast.collectAsState()

    val selectedBatch = batches.find { it.batchId == selectedBatchId }
        ?: batches.firstOrNull()

    // Handle Hardware / Predictive Back
    BackHandler(enabled = currentScreen != AppScreen.HOME && !showSplashScreen) {
        when (currentScreen) {
            AppScreen.REGISTER_BATCH -> viewModel.navigateTo(AppScreen.DASHBOARD)
            AppScreen.TRACK, AppScreen.SCAN, AppScreen.DASHBOARD, AppScreen.HOW_IT_WORKS, AppScreen.AUTH -> viewModel.navigateTo(AppScreen.HOME)
            else -> {}
        }
    }

    if (showSplashScreen) {
        CinematicSplashScreen(
            onSplashFinished = { showSplashScreen = false }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Column(modifier = Modifier.background(ForestGreen)) {
                    // Global Header Bar with Data Mode Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Brand Logo & Title
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { viewModel.navigateTo(AppScreen.HOME) }
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = LimeGreen,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Agriculture,
                                        contentDescription = "FarmTrace",
                                        tint = ForestGreen,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "FarmTrace",
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "SMART TRACEABILITY",
                                    color = LimeGreen,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        // Data Mode Switch: Original | Demo
                        DataModeSelector(
                            currentMode = dataMode,
                            onSelectMode = { viewModel.setDataMode(it) }
                        )
                    }

                    // Colored Banner in Demo Mode
                    if (dataMode == DataMode.DEMO) {
                        Surface(
                            color = Color(0xFFD97706), // Warm Amber
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "⚠️ DEMO MODE: Viewing simulated sample batches & live shoppers",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "Switch to Real",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier
                                        .clickable { viewModel.setDataMode(DataMode.ORIGINAL) }
                                        .background(Color.Black.copy(alpha = 0.25f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    } else {
                        // Original Mode Banner: Small clean indicator
                        Surface(
                            color = Color(0xFF133624),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(LimeGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "LIVE DATA: Real user registered batches & authenticated records only",
                                    color = CreamWhite.copy(alpha = 0.85f),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            },
            bottomBar = {
                NavigationBar(
                    containerColor = ForestGreen,
                    contentColor = CreamWhite,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("main_navigation_bar")
                ) {
                    // Home
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.HOME,
                        onClick = { viewModel.navigateTo(AppScreen.HOME) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = navigationItemColors()
                    )

                    // Track
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.TRACK,
                        onClick = { viewModel.navigateTo(AppScreen.TRACK) },
                        icon = { Icon(Icons.Default.Timeline, contentDescription = "Track") },
                        label = { Text("Track", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = navigationItemColors()
                    )

                    // Scan QR (Center Highlight)
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.SCAN,
                        onClick = { viewModel.navigateTo(AppScreen.SCAN) },
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(if (currentScreen == AppScreen.SCAN) LimeGreen else Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.QrCodeScanner,
                                    contentDescription = "Scan",
                                    tint = if (currentScreen == AppScreen.SCAN) ForestGreen else CreamWhite,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        label = { Text("Scan QR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LimeGreen) },
                        colors = navigationItemColors()
                    )

                    // Farmer Dashboard
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.DASHBOARD || currentScreen == AppScreen.REGISTER_BATCH,
                        onClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Farmer") },
                        label = { Text("Farmer", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = navigationItemColors()
                    )

                    // Explainer / About
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.HOW_IT_WORKS,
                        onClick = { viewModel.navigateTo(AppScreen.HOW_IT_WORKS) },
                        icon = { Icon(Icons.Default.Info, contentDescription = "How It Works") },
                        label = { Text("About", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = navigationItemColors()
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Main Screen Content Transition
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "ScreenTransition"
                ) { targetScreen ->
                    when (targetScreen) {
                        AppScreen.HOME -> HomeScreen(
                            batches = batches,
                            dataMode = dataMode,
                            livePurchases = livePurchases,
                            marketStats = marketStats,
                            isSimulationRunning = isSimulationRunning,
                            onToggleSimulation = { viewModel.toggleSimulation() },
                            onSimulateUserPurchase = { batchId, qty -> viewModel.simulateUserPurchase(batchId, qty) },
                            onResetDemoInventory = { viewModel.resetDemoInventory() },
                            onClearSimulatedPurchases = { viewModel.clearSimulatedPurchases() },
                            onSwitchToDemoMode = { viewModel.setDataMode(DataMode.DEMO) },
                            onNavigateToTrack = { batchId -> viewModel.selectBatch(batchId) },
                            onNavigateToScan = { viewModel.navigateTo(AppScreen.SCAN) },
                            onNavigateToDashboard = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                            onNavigateToHowItWorks = { viewModel.navigateTo(AppScreen.HOW_IT_WORKS) }
                        )

                        AppScreen.TRACK -> TrackBatchScreen(
                            batch = selectedBatch,
                            events = selectedBatchEvents,
                            onSearchBatch = { batchId -> viewModel.selectBatch(batchId) },
                            onNavigateBack = { viewModel.navigateTo(AppScreen.HOME) },
                            onNavigateToScan = { viewModel.navigateTo(AppScreen.SCAN) },
                            allBatches = batches
                        )

                        AppScreen.SCAN -> ScanScreen(
                            batches = batches,
                            onBatchScanned = { batchId -> viewModel.selectBatch(batchId) },
                            onNavigateBack = { viewModel.navigateTo(AppScreen.HOME) }
                        )

                        AppScreen.DASHBOARD -> FarmerDashboardScreen(
                            batches = batches,
                            eventsByBatch = allEventsByBatch,
                            dataMode = dataMode,
                            marketStats = marketStats,
                            livePurchases = livePurchases,
                            onNavigateToRegister = { viewModel.navigateTo(AppScreen.REGISTER_BATCH) },
                            onNavigateToTrack = { batchId -> viewModel.selectBatch(batchId) },
                            onAddJourneyStop = { batchId, eventType, location, city, dist, role, desc, temp, humidity ->
                                viewModel.addJourneyStop(batchId, eventType, location, city, dist, role, desc, temp, humidity)
                            },
                            onResetDemoRecords = { viewModel.resetDemoRecords() },
                            onExportCsv = { viewModel.exportLedgerCsv() },
                            onSwitchToOriginalMode = { viewModel.setDataMode(DataMode.ORIGINAL) }
                        )

                        AppScreen.REGISTER_BATCH -> BatchRegistrationScreen(
                            onRegisterBatch = { veg, variety, qty, unit, farm, loc, date, organic, grade, desc, farmer, contact ->
                                viewModel.registerBatch(veg, variety, qty, unit, farm, loc, date, organic, grade, desc, farmer, contact)
                            },
                            onNavigateBack = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                            onViewBatchTrack = { batchId -> viewModel.selectBatch(batchId) },
                            registeredBatchSuccess = registeredBatchSuccess
                        )

                        AppScreen.HOW_IT_WORKS -> HowItWorksScreen(
                            onNavigateBack = { viewModel.navigateTo(AppScreen.HOME) },
                            onNavigateToScan = { viewModel.navigateTo(AppScreen.SCAN) },
                            onNavigateToDashboard = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                            onReplayIntro = { showSplashScreen = true }
                        )

                        AppScreen.AUTH -> FarmerAuthScreen(
                            currentFarmerName = currentFarmerName,
                            onLoginSuccess = { name, email, role -> viewModel.login(name, email, role) },
                            onLogout = { viewModel.logout() },
                            onNavigateBack = { viewModel.navigateTo(AppScreen.HOME) },
                            onContinueToDashboard = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                        )
                    }
                }

                // Floating Purchase Notification Toast at the Top
                AnimatedVisibility(
                    visible = currentToast != null,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(14.dp)
                ) {
                    currentToast?.let { toast ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = ForestGreen.copy(alpha = 0.96f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, LimeGreen),
                            shadowElevation = 8.dp,
                            modifier = Modifier
                                .clickable {
                                    viewModel.selectBatch(toast.batchId)
                                    viewModel.dismissToast()
                                }
                                .testTag("purchase_notification_toast")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = LimeGreen,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(15.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = toast.title,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = LimeGreen
                                    )
                                    Text(
                                        text = toast.message,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = { viewModel.dismissToast() },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = CreamWhite.copy(alpha = 0.7f), modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }

                // Loading Overlay when switching Data Modes
                if (isLoadingModeSwitch) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = ForestGreen,
                            border = androidx.compose.foundation.BorderStroke(1.dp, LimeGreen)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(color = LimeGreen, modifier = Modifier.size(24.dp), strokeWidth = 3.dp)
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = if (dataMode == DataMode.DEMO) "Loading demo data..." else "Restoring original data...",
                                    color = Color.White,
                                    fontSize = 13.sp,
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

@Composable
private fun DataModeSelector(
    currentMode: DataMode,
    onSelectMode: (DataMode) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF0F261B),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF264E38)),
        modifier = Modifier.testTag("data_mode_switch")
    ) {
        Row(
            modifier = Modifier.padding(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Original Option
            val isOriginal = currentMode == DataMode.ORIGINAL
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isOriginal) LimeGreen else Color.Transparent)
                    .clickable { onSelectMode(DataMode.ORIGINAL) }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "Original",
                    color = if (isOriginal) ForestGreen else CreamWhite.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    fontWeight = if (isOriginal) FontWeight.ExtraBold else FontWeight.Medium
                )
            }

            // Demo Option
            val isDemo = currentMode == DataMode.DEMO
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isDemo) LimeGreen else Color.Transparent)
                    .clickable { onSelectMode(DataMode.DEMO) }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "Demo",
                    color = if (isDemo) ForestGreen else CreamWhite.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    fontWeight = if (isDemo) FontWeight.ExtraBold else FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun navigationItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = ForestGreen,
    selectedTextColor = LimeGreen,
    unselectedIconColor = CreamWhite.copy(alpha = 0.65f),
    unselectedTextColor = CreamWhite.copy(alpha = 0.65f),
    indicatorColor = LimeGreen
)
