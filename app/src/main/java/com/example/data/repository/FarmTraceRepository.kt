package com.example.data.repository

import com.example.data.local.BatchDao
import com.example.data.local.BatchEntity
import com.example.data.local.JourneyEventDao
import com.example.data.local.JourneyEventEntity
import com.example.data.model.BatchStatus
import com.example.data.model.EventType
import com.example.data.model.JourneyEvent
import com.example.data.model.JourneyStage
import com.example.data.model.VegetableBatch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class FarmTraceRepository(
    private val batchDao: BatchDao,
    private val journeyEventDao: JourneyEventDao
) {
    val allBatchesFlow: Flow<List<VegetableBatch>> = batchDao.getAllBatchesFlow().map { entities ->
        entities.map { it.toDomain() }
    }

    val allEventsFlow: Flow<List<JourneyEvent>> = journeyEventDao.getAllEventsFlow().map { entities ->
        entities.map { it.toDomain() }
    }

    fun getEventsForBatchFlow(batchId: String): Flow<List<JourneyEvent>> {
        return journeyEventDao.getEventsForBatchFlow(batchId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getBatchById(batchId: String): VegetableBatch? {
        return batchDao.getBatchById(batchId)?.toDomain()
    }

    suspend fun getEventsForBatch(batchId: String): List<JourneyEvent> {
        return journeyEventDao.getEventsForBatch(batchId).map { it.toDomain() }
    }

    suspend fun registerBatch(
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
    ): VegetableBatch {
        val existing = batchDao.getAllBatches()
        val nextNumber = 123 + existing.size
        val batchId = "FT-2026-${String.format(Locale.US, "%06d", nextNumber)}"

        val batch = VegetableBatch(
            batchId = batchId,
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
            qrPayloadUrl = "https://farmtrace.app/track/$batchId",
            currentStatus = BatchStatus.REGISTERED,
            createdAt = System.currentTimeMillis(),
            farmerName = farmerName,
            farmerContact = farmerContact
        )

        batchDao.insertBatch(BatchEntity.fromDomain(batch))

        // Record initial harvest event
        val harvestEvent = JourneyEvent(
            id = UUID.randomUUID().toString(),
            batchId = batchId,
            eventType = EventType.HARVESTED,
            stage = JourneyStage.FARM,
            locationName = farmName,
            cityRegion = farmLocation,
            distanceFromPrevKm = 0.0,
            handlerRole = "Farm Production Lead",
            description = "Harvested at peak ripeness under Good Agricultural Practices (GAP). Batch $batchId registered in FarmTrace ledger.",
            temperatureC = 18.5,
            humidityPercent = 75,
            recordedTimestamp = getCurrentIsoTimestamp(),
            timestampMillis = System.currentTimeMillis()
        )
        journeyEventDao.insertEvent(JourneyEventEntity.fromDomain(harvestEvent))

        return batch
    }

    suspend fun addJourneyEvent(
        batchId: String,
        eventType: EventType,
        locationName: String,
        cityRegion: String,
        distanceFromPrevKm: Double,
        handlerRole: String,
        description: String,
        temperatureC: Double?,
        humidityPercent: Int?
    ): JourneyEvent {
        val stage = eventType.stage
        val event = JourneyEvent(
            id = UUID.randomUUID().toString(),
            batchId = batchId,
            eventType = eventType,
            stage = stage,
            locationName = locationName,
            cityRegion = cityRegion,
            distanceFromPrevKm = distanceFromPrevKm,
            handlerRole = handlerRole,
            description = description,
            temperatureC = temperatureC,
            humidityPercent = humidityPercent,
            recordedTimestamp = getCurrentIsoTimestamp(),
            timestampMillis = System.currentTimeMillis()
        )

        journeyEventDao.insertEvent(JourneyEventEntity.fromDomain(event))

        // Automatically update the batch's current status based on the latest recorded milestone
        val newStatus = when (stage) {
            JourneyStage.FARM -> BatchStatus.REGISTERED
            JourneyStage.WHOLESALE_MARKET -> BatchStatus.AT_MARKET
            JourneyStage.TRANSPORTATION -> BatchStatus.IN_TRANSIT
            JourneyStage.RETAIL_SHOP -> BatchStatus.AT_RETAIL_SHOP
            JourneyStage.CONSUMER_KITCHEN -> BatchStatus.DELIVERED
        }
        batchDao.updateBatchStatus(batchId, newStatus.name)

        return event
    }

    suspend fun seedDemoDataIfEmpty() {
        val count = batchDao.getAllBatches().size
        if (count == 0) {
            seedSampleRecords()
        }
    }

    suspend fun resetToDemoRecords() {
        val existing = batchDao.getAllBatches()
        existing.forEach {
            journeyEventDao.deleteEventsForBatch(it.batchId)
            batchDao.deleteBatch(it.batchId)
        }
        seedSampleRecords()
    }

    private suspend fun seedSampleRecords() {
        val baseTime = System.currentTimeMillis() - (48 * 3600 * 1000L) // 2 days ago

        // Batch 1: Tomatoes
        val b1 = VegetableBatch(
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
            createdAt = baseTime,
            farmerName = "Rajesh Patel",
            farmerContact = "rajesh@greenvalley.ag"
        )
        batchDao.insertBatch(BatchEntity.fromDomain(b1))

        val b1Events = listOf(
            JourneyEvent(
                id = "b1-ev1",
                batchId = "FT-2026-000123",
                eventType = EventType.HARVESTED,
                stage = JourneyStage.FARM,
                locationName = "Field #4, Green Valley Organics",
                cityRegion = "Salinas Valley, CA",
                distanceFromPrevKm = 0.0,
                handlerRole = "Farm Manager (Rajesh Patel)",
                description = "Hand-picked into aerated crates at sunrise (Brix level: 5.4%). Passed optical defect sorting.",
                temperatureC = 16.2,
                humidityPercent = 70,
                recordedTimestamp = "2026-10-31 06:45 AM",
                timestampMillis = baseTime
            ),
            JourneyEvent(
                id = "b1-ev2",
                batchId = "FT-2026-000123",
                eventType = EventType.RECEIVED_AT_MARKET,
                stage = JourneyStage.WHOLESALE_MARKET,
                locationName = "Bay Area Central Produce Terminal",
                cityRegion = "South San Francisco, CA",
                distanceFromPrevKm = 142.0,
                handlerRole = "Wholesale Receiver (Marcus Vance)",
                description = "Consignment received, weighed, and quality inspected. Cleaned, graded, and staged for cold chain distribution.",
                temperatureC = 13.5,
                humidityPercent = 65,
                recordedTimestamp = "2026-10-31 02:15 PM",
                timestampMillis = baseTime + (8 * 3600 * 1000L)
            ),
            JourneyEvent(
                id = "b1-ev3",
                batchId = "FT-2026-000123",
                eventType = EventType.COLD_CHAIN_TRANSIT,
                stage = JourneyStage.TRANSPORTATION,
                locationName = "EcoLogistics Reefer Fleet #308",
                cityRegion = "Interstate 280 Corridor",
                distanceFromPrevKm = 38.0,
                handlerRole = "Certified Fleet Driver (Dave Jenkins)",
                description = "Loaded onto climate-controlled electric reefer van with continuous temperature telemetry active.",
                temperatureC = 12.8,
                humidityPercent = 68,
                recordedTimestamp = "2026-11-01 07:30 AM",
                timestampMillis = baseTime + (25 * 3600 * 1000L)
            ),
            JourneyEvent(
                id = "b1-ev4",
                batchId = "FT-2026-000123",
                eventType = EventType.SHELVED_FOR_SALE,
                stage = JourneyStage.RETAIL_SHOP,
                locationName = "FreshMart Supermarket - Produce Aisle 2",
                cityRegion = "Downtown Oakland, CA",
                distanceFromPrevKm = 24.0,
                handlerRole = "Store Produce Lead (Elena Rostova)",
                description = "Batch inspected at receiving dock, scanned into inventory, and displayed on misted wooden stands with QR traceability tags.",
                temperatureC = 14.0,
                humidityPercent = 72,
                recordedTimestamp = "2026-11-01 11:20 AM",
                timestampMillis = baseTime + (29 * 3600 * 1000L)
            )
        )
        b1Events.forEach { journeyEventDao.insertEvent(JourneyEventEntity.fromDomain(it)) }

        // Batch 2: Potatoes
        val b2 = VegetableBatch(
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
            createdAt = baseTime - (24 * 3600 * 1000L),
            farmerName = "Sarah Jenkins",
            farmerContact = "sarah@highlandroots.ag"
        )
        batchDao.insertBatch(BatchEntity.fromDomain(b2))

        val b2Events = listOf(
            JourneyEvent(
                id = "b2-ev1",
                batchId = "FT-2026-000124",
                eventType = EventType.HARVESTED,
                stage = JourneyStage.FARM,
                locationName = "Highland Farm Packing Shed",
                cityRegion = "Klamath Basin, OR",
                distanceFromPrevKm = 0.0,
                handlerRole = "Field Supervisor (Sarah Jenkins)",
                description = "Mechanically harvested, dry-brushed, and graded into 50kg burlap sacks.",
                temperatureC = 9.0,
                humidityPercent = 85,
                recordedTimestamp = "2026-10-29 08:00 AM",
                timestampMillis = baseTime - (24 * 3600 * 1000L)
            ),
            JourneyEvent(
                id = "b2-ev2",
                batchId = "FT-2026-000124",
                eventType = EventType.RECEIVED_AT_MARKET,
                stage = JourneyStage.WHOLESALE_MARKET,
                locationName = "Northern Agri-Logistics Hub",
                cityRegion = "Redding, CA",
                distanceFromPrevKm = 210.0,
                handlerRole = "Logistics Coordinator (Tim Ross)",
                description = "Arrived at regional aggregation center. Moisture tested and prepared for distribution dispatch.",
                temperatureC = 8.5,
                humidityPercent = 80,
                recordedTimestamp = "2026-10-30 04:30 PM",
                timestampMillis = baseTime - (10 * 3600 * 1000L)
            ),
            JourneyEvent(
                id = "b2-ev3",
                batchId = "FT-2026-000124",
                eventType = EventType.COLD_CHAIN_TRANSIT,
                stage = JourneyStage.TRANSPORTATION,
                locationName = "Pacific Freight Route 5 Truck #12",
                cityRegion = "Sacramento Valley Corridor",
                distanceFromPrevKm = 260.0,
                handlerRole = "Hauler Driver (Carlos Rivera)",
                description = "In transit to Metro Distribution Center. Monitored ventilation active.",
                temperatureC = 8.0,
                humidityPercent = 82,
                recordedTimestamp = "2026-11-01 03:00 PM",
                timestampMillis = baseTime + (33 * 3600 * 1000L)
            )
        )
        b2Events.forEach { journeyEventDao.insertEvent(JourneyEventEntity.fromDomain(it)) }

        // Batch 3: Onions
        val b3 = VegetableBatch(
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
            createdAt = baseTime + (12 * 3600 * 1000L),
            farmerName = "Anil Shinde",
            farmerContact = "anil@sahyadriagro.in"
        )
        batchDao.insertBatch(BatchEntity.fromDomain(b3))

        val b3Events = listOf(
            JourneyEvent(
                id = "b3-ev1",
                batchId = "FT-2026-000125",
                eventType = EventType.HARVESTED,
                stage = JourneyStage.FARM,
                locationName = "Dindori Sun-Curing Yards",
                cityRegion = "Nashik, MH",
                distanceFromPrevKm = 0.0,
                handlerRole = "Farmer Lead (Anil Shinde)",
                description = "Sun-cured for 5 days post-uprooting. Outer skins tightened and cleaned.",
                temperatureC = 22.0,
                humidityPercent = 55,
                recordedTimestamp = "2026-11-01 07:00 AM",
                timestampMillis = baseTime + (12 * 3600 * 1000L)
            ),
            JourneyEvent(
                id = "b3-ev2",
                batchId = "FT-2026-000125",
                eventType = EventType.RECEIVED_AT_MARKET,
                stage = JourneyStage.WHOLESALE_MARKET,
                locationName = "Lasalgaon APMC Wholesale Yard",
                cityRegion = "Lasalgaon, MH",
                distanceFromPrevKm = 48.0,
                handlerRole = "APMC Inspector (Sunil More)",
                description = "Weighed, verified for zero rot, lot registered under APMC traceability protocol.",
                temperatureC = 21.0,
                humidityPercent = 50,
                recordedTimestamp = "2026-11-01 01:45 PM",
                timestampMillis = baseTime + (18 * 3600 * 1000L)
            )
        )
        b3Events.forEach { journeyEventDao.insertEvent(JourneyEventEntity.fromDomain(it)) }

        // Batch 4: Carrots (Delivered to Table)
        val b4 = VegetableBatch(
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
            createdAt = baseTime - (48 * 3600 * 1000L),
            farmerName = "Maya Lin",
            farmerContact = "maya@sunnyslope.ag"
        )
        batchDao.insertBatch(BatchEntity.fromDomain(b4))

        val b4Events = listOf(
            JourneyEvent(
                id = "b4-ev1",
                batchId = "FT-2026-000126",
                eventType = EventType.HARVESTED,
                stage = JourneyStage.FARM,
                locationName = "Plot 9, Sunny Slope Farm",
                cityRegion = "Bakersfield, CA",
                distanceFromPrevKm = 0.0,
                handlerRole = "Farm Owner (Maya Lin)",
                description = "Hydro-cooled immediately after harvest to preserve sugar and crunch.",
                temperatureC = 3.5,
                humidityPercent = 95,
                recordedTimestamp = "2026-10-28 07:15 AM",
                timestampMillis = baseTime - (48 * 3600 * 1000L)
            ),
            JourneyEvent(
                id = "b4-ev2",
                batchId = "FT-2026-000126",
                eventType = EventType.COLD_CHAIN_TRANSIT,
                stage = JourneyStage.TRANSPORTATION,
                locationName = "Central California Express Reefer",
                cityRegion = "CA-99 Corridor",
                distanceFromPrevKm = 180.0,
                handlerRole = "Driver (Sam Lee)",
                description = "Constant 3°C reefer setting maintained throughout transit.",
                temperatureC = 3.2,
                humidityPercent = 92,
                recordedTimestamp = "2026-10-29 11:30 AM",
                timestampMillis = baseTime - (22 * 3600 * 1000L)
            ),
            JourneyEvent(
                id = "b4-ev3",
                batchId = "FT-2026-000126",
                eventType = EventType.SHELVED_FOR_SALE,
                stage = JourneyStage.RETAIL_SHOP,
                locationName = "Green Leaf Grocers",
                cityRegion = "San Francisco, CA",
                distanceFromPrevKm = 95.0,
                handlerRole = "Store Manager (Chloe Martin)",
                description = "Washed and bunched with greens intact for consumer display.",
                temperatureC = 4.0,
                humidityPercent = 90,
                recordedTimestamp = "2026-10-30 09:00 AM",
                timestampMillis = baseTime - (1 * 3600 * 1000L)
            ),
            JourneyEvent(
                id = "b4-ev4",
                batchId = "FT-2026-000126",
                eventType = EventType.PURCHASED_BY_SHOPPER,
                stage = JourneyStage.CONSUMER_KITCHEN,
                locationName = "Home Kitchen - Salad Bowl",
                cityRegion = "San Francisco, CA",
                distanceFromPrevKm = 3.5,
                handlerRole = "Shopper / Home Chef",
                description = "Purchased by shopper, scanned via FarmTrace QR code, and enjoyed fresh in dinner salad!",
                temperatureC = 20.0,
                humidityPercent = 50,
                recordedTimestamp = "2026-10-31 07:30 PM",
                timestampMillis = baseTime + (10 * 3600 * 1000L)
            )
        )
        b4Events.forEach { journeyEventDao.insertEvent(JourneyEventEntity.fromDomain(it)) }
    }

    fun exportToCsv(batches: List<VegetableBatch>, eventsByBatch: Map<String, List<JourneyEvent>>): String {
        val sb = StringBuilder()
        sb.append("Batch ID,Vegetable,Variety,Quantity,Unit,Farm Name,Farm Location,Harvest Date,Status,Organic,Quality Grade,Events Count,Total Distance (km)\n")
        batches.forEach { b ->
            val events = eventsByBatch[b.batchId] ?: emptyList()
            val totalDist = events.sumOf { it.distanceFromPrevKm }
            sb.append("\"${b.batchId}\",\"${b.vegetableName}\",\"${b.variety}\",${b.quantity},\"${b.unit}\",\"${b.farmName}\",\"${b.farmLocation}\",\"${b.harvestDate}\",\"${b.currentStatus.displayName}\",${b.organicCertified},\"${b.qualityGrade}\",${events.size},${totalDist}\n")
        }
        sb.append("\n--- JOURNEY EVENT LOGS ---\n")
        sb.append("Batch ID,Timestamp,Stage,Event Type,Location,City/Region,Distance (km),Handler Role,Temperature (°C),Description\n")
        batches.forEach { b ->
            val events = eventsByBatch[b.batchId] ?: emptyList()
            events.forEach { ev ->
                sb.append("\"${ev.batchId}\",\"${ev.recordedTimestamp}\",\"${ev.stage.title}\",\"${ev.eventType.title}\",\"${ev.locationName}\",\"${ev.cityRegion}\",${ev.distanceFromPrevKm},\"${ev.handlerRole}\",${ev.temperatureC ?: "N/A"},\"${ev.description.replace("\"", "\"\"")}\"\n")
            }
        }
        return sb.toString()
    }

    private fun getCurrentIsoTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US)
        return sdf.format(Date())
    }
}
