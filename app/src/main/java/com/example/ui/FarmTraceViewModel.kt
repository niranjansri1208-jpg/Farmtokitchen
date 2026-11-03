package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.FarmTraceDatabase
import com.example.data.model.BatchStatus
import com.example.data.model.DataMode
import com.example.data.model.DemoMarketStats
import com.example.data.model.EventType
import com.example.data.model.JourneyEvent
import com.example.data.model.JourneyStage
import com.example.data.model.SimulatedPurchase
import com.example.data.model.VegetableBatch
import com.example.data.repository.FarmTraceRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

enum class AppScreen(val title: String) {
    HOME("Home"),
    TRACK("Track"),
    SCAN("Scan QR"),
    DASHBOARD("Farmer"),
    REGISTER_BATCH("Register"),
    HOW_IT_WORKS("How It Works"),
    AUTH("Account")
}

data class LiveToastNotification(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val message: String,
    val batchId: String
)

class FarmTraceViewModel(application: Application) : AndroidViewModel(application) {

    private val db = FarmTraceDatabase.getDatabase(application)
    private val repository = FarmTraceRepository(db.batchDao(), db.journeyEventDao())

    // 1. Data Mode: ORIGINAL (Real User Data) vs DEMO (Sample Batches & Market Simulation)
    private val _dataMode = MutableStateFlow(DataMode.DEMO) // Default to Demo for interactive exhibition
    val dataMode: StateFlow<DataMode> = _dataMode.asStateFlow()

    private val _isLoadingModeSwitch = MutableStateFlow(false)
    val isLoadingModeSwitch: StateFlow<Boolean> = _isLoadingModeSwitch.asStateFlow()

    // 2. Separate Collections for Original vs Demo
    private val _originalBatches = MutableStateFlow<List<VegetableBatch>>(emptyList())
    private val _demoBatches = MutableStateFlow<List<VegetableBatch>>(emptyList())

    // Active Batches exposed to UI based on current dataMode
    private val _activeBatches = MutableStateFlow<List<VegetableBatch>>(emptyList())
    val batches: StateFlow<List<VegetableBatch>> = _activeBatches.asStateFlow()

    private val _allEventsByBatch = MutableStateFlow<Map<String, List<JourneyEvent>>>(emptyMap())
    val allEventsByBatch: StateFlow<Map<String, List<JourneyEvent>>> = _allEventsByBatch.asStateFlow()

    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _selectedBatchId = MutableStateFlow<String?>("FT-2026-000123")
    val selectedBatchId: StateFlow<String?> = _selectedBatchId.asStateFlow()

    private val _selectedBatchEvents = MutableStateFlow<List<JourneyEvent>>(emptyList())
    val selectedBatchEvents: StateFlow<List<JourneyEvent>> = _selectedBatchEvents.asStateFlow()

    private val _currentFarmerName = MutableStateFlow<String?>("Rajesh Patel")
    val currentFarmerName: StateFlow<String?> = _currentFarmerName.asStateFlow()

    private val _registeredBatchSuccess = MutableStateFlow<VegetableBatch?>(null)
    val registeredBatchSuccess: StateFlow<VegetableBatch?> = _registeredBatchSuccess.asStateFlow()

    // 3. Fake Customer Market Simulation State
    private val _isSimulationRunning = MutableStateFlow(true)
    val isSimulationRunning: StateFlow<Boolean> = _isSimulationRunning.asStateFlow()

    private val _livePurchasesFeed = MutableStateFlow<List<SimulatedPurchase>>(emptyList())
    val livePurchasesFeed: StateFlow<List<SimulatedPurchase>> = _livePurchasesFeed.asStateFlow()

    private val _marketStats = MutableStateFlow(DemoMarketStats())
    val marketStats: StateFlow<DemoMarketStats> = _marketStats.asStateFlow()

    private val _currentToast = MutableStateFlow<LiveToastNotification?>(null)
    val currentToast: StateFlow<LiveToastNotification?> = _currentToast.asStateFlow()

    // 12 Fictional Customer First Names
    private val fakeCustomers = listOf(
        "Priya" to 0xFFE91E63,
        "Arun" to 0xFF2196F3,
        "Meena" to 0xFF9C27B0,
        "Karthik" to 0xFF4CAF50,
        "Fatima" to 0xFFFF9800,
        "Ravi" to 0xFF009688,
        "Ananya" to 0xFFE040FB,
        "Kavin" to 0xFF3F51B5,
        "Arjun" to 0xFF00BCD4,
        "Divya" to 0xFFFF5722,
        "Rahul" to 0xFF795548,
        "Sneha" to 0xFF8BC34A
    )

    private val sampleShops = listOf(
        "FreshMart Supermarket (Aisle 2)",
        "Green Leaf Grocers (Downtown)",
        "Organic Valley Market #4",
        "Metro Agro Produce Stalls",
        "Community Farmers Market"
    )

    private var simulationJob: Job? = null

    init {
        viewModelScope.launch {
            repository.seedDemoDataIfEmpty()
            loadAllEvents()
            initDemoBatches()
            refreshActiveBatches()
            startSimulationLoop()
        }
    }

    private fun loadAllEvents() {
        viewModelScope.launch {
            repository.allEventsFlow.collect { events ->
                _allEventsByBatch.value = events.groupBy { it.batchId }
                val currentId = _selectedBatchId.value
                if (currentId != null) {
                    _selectedBatchEvents.value = events.filter { it.batchId == currentId }
                }
            }
        }
    }

    private fun initDemoBatches() {
        val sampleList = listOf(
            VegetableBatch(
                batchId = "FT-2026-000123",
                vegetableName = "Tomato",
                variety = "Roma Plum Vine",
                quantity = 1200.0,
                unit = "kg",
                farmName = "Green Valley Organic Farm",
                farmLocation = "Salinas Valley, CA",
                harvestDate = "2026-10-31",
                organicCertified = true,
                qualityGrade = "Grade A Premium",
                description = "Vine-ripened, naturally irrigated organic plum tomatoes with vibrant color and high brix sweetness.",
                currentStatus = BatchStatus.AT_RETAIL_SHOP,
                pricePerKg = 40.0,
                remainingDemoStock = 1180.0,
                isDemoBatch = true
            ),
            VegetableBatch(
                batchId = "FT-2026-000124",
                vegetableName = "Potato",
                variety = "Russet Burbank Golden",
                quantity = 3500.0,
                unit = "kg",
                farmName = "Highland Roots Farm",
                farmLocation = "Klamath Basin, OR",
                harvestDate = "2026-10-29",
                organicCertified = false,
                qualityGrade = "US No. 1",
                description = "High-starch, smooth-skinned golden Russet potatoes ideal for baking and roasting.",
                currentStatus = BatchStatus.IN_TRANSIT,
                pricePerKg = 28.0,
                remainingDemoStock = 3450.0,
                isDemoBatch = true
            ),
            VegetableBatch(
                batchId = "FT-2026-000125",
                vegetableName = "Onion",
                variety = "Nashik Red Sweet",
                quantity = 2000.0,
                unit = "kg",
                farmName = "Sahyadri Agro Collective",
                farmLocation = "Nashik Valley, MH",
                harvestDate = "2026-11-01",
                organicCertified = true,
                qualityGrade = "Export Quality A+",
                description = "Naturally sun-cured sweet red onions with crisp concentric layers and rich aroma.",
                currentStatus = BatchStatus.AT_MARKET,
                pricePerKg = 35.0,
                remainingDemoStock = 1970.0,
                isDemoBatch = true
            ),
            VegetableBatch(
                batchId = "FT-2026-000126",
                vegetableName = "Carrot",
                variety = "Nantes Sweet Crunch",
                quantity = 850.0,
                unit = "kg",
                farmName = "Sunny Slope Organics",
                farmLocation = "Bakersfield, CA",
                harvestDate = "2026-10-28",
                organicCertified = true,
                qualityGrade = "Grade A",
                description = "Cylindrical Nantes carrots, crisp with high natural beta-carotene sweetness.",
                currentStatus = BatchStatus.DELIVERED,
                pricePerKg = 50.0,
                remainingDemoStock = 810.0,
                isDemoBatch = true
            )
        )
        _demoBatches.value = sampleList
        updateMarketStats()
    }

    private fun refreshActiveBatches() {
        _activeBatches.value = if (_dataMode.value == DataMode.DEMO) {
            _demoBatches.value
        } else {
            _originalBatches.value
        }
        val currentId = _selectedBatchId.value
        if (_activeBatches.value.none { it.batchId == currentId }) {
            _selectedBatchId.value = _activeBatches.value.firstOrNull()?.batchId
        }
        _selectedBatchId.value?.let { loadSelectedBatchEvents(it) }
    }

    fun setDataMode(mode: DataMode) {
        if (_dataMode.value == mode) return
        viewModelScope.launch {
            _isLoadingModeSwitch.value = true
            delay(500) // Brief smooth loading transition
            _dataMode.value = mode
            refreshActiveBatches()
            if (mode == DataMode.DEMO) {
                _isSimulationRunning.value = true
                startSimulationLoop()
            } else {
                _isSimulationRunning.value = false
                simulationJob?.cancel()
                _currentToast.value = null
            }
            _isLoadingModeSwitch.value = false
        }
    }

    fun toggleSimulation() {
        val next = !_isSimulationRunning.value
        _isSimulationRunning.value = next
        if (next) {
            startSimulationLoop()
        } else {
            simulationJob?.cancel()
        }
    }

    private fun startSimulationLoop() {
        simulationJob?.cancel()
        if (_dataMode.value != DataMode.DEMO) return

        simulationJob = viewModelScope.launch {
            while (isActive) {
                delay(Random.nextLong(4000, 7500)) // Every 4-7 seconds
                if (!_isSimulationRunning.value || _dataMode.value != DataMode.DEMO) continue

                val eligibleBatches = _demoBatches.value.filter { it.remainingDemoStock > 0 }
                if (eligibleBatches.isEmpty()) continue

                val targetBatch = eligibleBatches.random()
                val (custName, avatarColor) = fakeCustomers.random()
                val shop = sampleShops.random()
                val qtyToBuy = minOf(Random.nextDouble(1.0, 4.0).toInt().toDouble(), targetBatch.remainingDemoStock)
                if (qtyToBuy <= 0.0) continue

                val price = qtyToBuy * targetBatch.pricePerKg

                // Deduct stock
                val updatedRemaining = targetBatch.remainingDemoStock - qtyToBuy
                val newStatus = if (updatedRemaining <= 0.0) BatchStatus.SOLD_OUT else targetBatch.currentStatus

                _demoBatches.value = _demoBatches.value.map { b ->
                    if (b.batchId == targetBatch.batchId) {
                        b.copy(remainingDemoStock = updatedRemaining, currentStatus = newStatus)
                    } else b
                }
                refreshActiveBatches()

                // Create Purchase record
                val purchase = SimulatedPurchase(
                    customerName = custName,
                    batchId = targetBatch.batchId,
                    vegetableName = targetBatch.vegetableName,
                    quantityKg = qtyToBuy,
                    totalPrice = price,
                    shopName = shop,
                    customerAvatarColor = avatarColor
                )

                _livePurchasesFeed.value = listOf(purchase) + _livePurchasesFeed.value.take(19)
                updateMarketStats()

                // Show soft Toast notification
                _currentToast.value = LiveToastNotification(
                    title = "🛒 Demo Purchase",
                    message = "$custName bought ${qtyToBuy.toInt()} kg ${targetBatch.vegetableName} (Batch ${targetBatch.batchId})",
                    batchId = targetBatch.batchId
                )

                // Dismiss toast after 3.5 seconds
                launch {
                    delay(3500)
                    if (_currentToast.value?.batchId == targetBatch.batchId) {
                        _currentToast.value = null
                    }
                }
            }
        }
    }

    fun simulateUserPurchase(batchId: String, quantityKg: Double) {
        val batch = _demoBatches.value.find { it.batchId == batchId } ?: return
        val qty = minOf(quantityKg, batch.remainingDemoStock)
        if (qty <= 0.0) return

        val price = qty * batch.pricePerKg
        val updatedStock = batch.remainingDemoStock - qty
        val newStatus = if (updatedStock <= 0.0) BatchStatus.SOLD_OUT else batch.currentStatus

        _demoBatches.value = _demoBatches.value.map { b ->
            if (b.batchId == batchId) {
                b.copy(remainingDemoStock = updatedStock, currentStatus = newStatus)
            } else b
        }
        refreshActiveBatches()

        val purchase = SimulatedPurchase(
            customerName = "Shopper (You)",
            batchId = batch.batchId,
            vegetableName = batch.vegetableName,
            quantityKg = qty,
            totalPrice = price,
            shopName = "Direct Verified Checkout",
            customerAvatarColor = 0xFF2E7D32
        )
        _livePurchasesFeed.value = listOf(purchase) + _livePurchasesFeed.value.take(19)
        updateMarketStats()

        _currentToast.value = LiveToastNotification(
            title = "✅ Purchase Confirmed",
            message = "You purchased ${qty.toInt()} kg ${batch.vegetableName} · ₹${price.toInt()}",
            batchId = batch.batchId
        )
    }

    private fun updateMarketStats() {
        val purchases = _livePurchasesFeed.value
        val totalSales = purchases.sumOf { it.totalPrice }
        val totalKg = purchases.sumOf { it.quantityKg }
        val uniqueCustomers = purchases.map { it.customerName }.distinct().size
        val topVeg = purchases.groupBy { it.vegetableName }
            .maxByOrNull { it.value.sumOf { p -> p.quantityKg } }?.key ?: "Tomato"
        val remainingStock = _demoBatches.value.sumOf { it.remainingDemoStock }

        _marketStats.value = DemoMarketStats(
            totalSalesRupees = totalSales,
            totalKgSold = totalKg,
            totalCustomersCount = uniqueCustomers,
            topSellingVegetable = topVeg,
            availableDemoInventoryKg = remainingStock,
            simulatedPurchasesCount = purchases.size
        )
    }

    fun dismissToast() {
        _currentToast.value = null
    }

    fun resetDemoInventory() {
        initDemoBatches()
        _livePurchasesFeed.value = emptyList()
        refreshActiveBatches()
        updateMarketStats()
    }

    fun clearSimulatedPurchases() {
        _livePurchasesFeed.value = emptyList()
        updateMarketStats()
    }

    fun selectBatch(batchId: String) {
        _selectedBatchId.value = batchId
        loadSelectedBatchEvents(batchId)
        _currentScreen.value = AppScreen.TRACK
    }

    private fun loadSelectedBatchEvents(batchId: String) {
        viewModelScope.launch {
            val events = repository.getEventsForBatch(batchId)
            _selectedBatchEvents.value = events
        }
    }

    fun navigateTo(screen: AppScreen) {
        if (screen == AppScreen.REGISTER_BATCH) {
            _registeredBatchSuccess.value = null
        }
        _currentScreen.value = screen
    }

    fun registerBatch(
        vegetableName: String,
        variety: String,
        quantity: Double,
        unit: String,
        farmName: String,
        farmLocation: String,
        harvestDate: String,
        organicCertified: Boolean,
        qualityGrade: String,
        description: String,
        farmerName: String,
        farmerContact: String
    ) {
        viewModelScope.launch {
            val batch = repository.registerBatch(
                vegetableName = vegetableName,
                variety = variety,
                quantity = quantity,
                unit = unit,
                farmName = farmName,
                farmLocation = farmLocation,
                harvestDate = harvestDate,
                organicCertified = organicCertified,
                qualityGrade = qualityGrade,
                description = description,
                farmerName = farmerName,
                farmerContact = farmerContact
            )

            // If in Demo mode, also add into demo batches so students immediately see fake shoppers buy from it!
            if (_dataMode.value == DataMode.DEMO) {
                _demoBatches.value = listOf(batch.copy(isDemoBatch = true)) + _demoBatches.value
            } else {
                _originalBatches.value = listOf(batch) + _originalBatches.value
            }

            refreshActiveBatches()
            _registeredBatchSuccess.value = batch
            selectBatch(batch.batchId)
            _currentScreen.value = AppScreen.REGISTER_BATCH
        }
    }

    fun addJourneyStop(
        batchId: String,
        eventType: EventType,
        locationName: String,
        cityRegion: String,
        distanceFromPrevKm: Double,
        handlerRole: String,
        description: String,
        temperatureC: Double?,
        humidityPercent: Int?
    ) {
        viewModelScope.launch {
            repository.addJourneyEvent(
                batchId = batchId,
                eventType = eventType,
                locationName = locationName,
                cityRegion = cityRegion,
                distanceFromPrevKm = distanceFromPrevKm,
                handlerRole = handlerRole,
                description = description,
                temperatureC = temperatureC,
                humidityPercent = humidityPercent
            )
            loadSelectedBatchEvents(batchId)
        }
    }

    fun resetDemoRecords() {
        viewModelScope.launch {
            repository.resetToDemoRecords()
            initDemoBatches()
            refreshActiveBatches()
            selectBatch("FT-2026-000123")
        }
    }

    fun exportLedgerCsv(): String {
        return repository.exportToCsv(_activeBatches.value, _allEventsByBatch.value)
    }

    fun login(name: String, email: String, role: String) {
        _currentFarmerName.value = name
    }

    fun logout() {
        _currentFarmerName.value = null
    }
}
