package com.example.data.model

import java.util.UUID

enum class DataMode {
    ORIGINAL,
    DEMO
}

enum class BatchStatus(val displayName: String, val stepIndex: Int) {
    REGISTERED("Registered at Farm", 0),
    AT_MARKET("At Wholesale Market", 1),
    IN_TRANSIT("In Cold Transit", 2),
    AT_RETAIL_SHOP("At Supermarket", 3),
    DELIVERED("Delivered to Table", 4),
    SOLD_OUT("Sold Out (Demo Market)", 5);

    companion object {
        fun fromString(value: String): BatchStatus {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: REGISTERED
        }
    }
}

enum class JourneyStage(val title: String, val defaultIcon: String) {
    FARM("Farm Origin", "Agriculture"),
    WHOLESALE_MARKET("Wholesale Market", "Store"),
    TRANSPORTATION("Logistics Transit", "LocalShipping"),
    RETAIL_SHOP("Supermarket Shelf", "ShoppingCart"),
    CONSUMER_KITCHEN("Consumer Table", "Kitchen");

    companion object {
        fun fromString(value: String): JourneyStage {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: FARM
        }
    }
}

enum class EventType(val title: String, val stage: JourneyStage) {
    HARVESTED("Batch Harvested", JourneyStage.FARM),
    SORTED_PACKED("Sorted & Packed", JourneyStage.FARM),
    QUALITY_CHECK_PASSED("Quality Check Passed", JourneyStage.FARM),
    DISPATCHED_TO_MANDI("Dispatched to Mandi", JourneyStage.TRANSPORTATION),
    RECEIVED_AT_MARKET("Received at Wholesale Market", JourneyStage.WHOLESALE_MARKET),
    AUCTIONED_DISPATCHED("Wholesale Dispatched", JourneyStage.WHOLESALE_MARKET),
    COLD_CHAIN_TRANSIT("Cold Chain Reefer Transit", JourneyStage.TRANSPORTATION),
    RECEIVED_AT_STORE("Received at Supermarket", JourneyStage.RETAIL_SHOP),
    SHELVED_FOR_SALE("Shelved on Produce Aisle", JourneyStage.RETAIL_SHOP),
    PURCHASED_BY_SHOPPER("Purchased & Placed on Table", JourneyStage.CONSUMER_KITCHEN);

    companion object {
        fun fromString(value: String): EventType {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: HARVESTED
        }
    }
}

enum class VegetableType(
    val commonName: String,
    val scientificName: String,
    val iconColorHex: Long,
    val optimalTempRange: String,
    val emoji: String,
    val defaultPricePerKg: Double
) {
    TOMATO("Tomato", "Solanum lycopersicum", 0xFFE53935, "12°C - 15°C", "🍅", 40.0),
    POTATO("Potato", "Solanum tuberosum", 0xFFC0A060, "7°C - 10°C", "🥔", 28.0),
    ONION("Onion", "Allium cepa", 0xFF8E24AA, "15°C - 20°C", "🧅", 35.0),
    CARROT("Carrot", "Daucus carota", 0xFFFF8F00, "1°C - 4°C", "🥕", 50.0),
    SPINACH("Spinach", "Spinacia oleracea", 0xFF2E7D32, "2°C - 5°C", "🥬", 30.0),
    BRINJAL("Brinjal / Eggplant", "Solanum melongena", 0xFF512DA8, "10°C - 12°C", "🍆", 45.0),
    CABBAGE("Cabbage", "Brassica oleracea", 0xFF43A047, "0°C - 2°C", "🥬", 25.0),
    OTHER("Fresh Produce", "Agricultural Commodity", 0xFF35A853, "8°C - 12°C", "🌱", 35.0);

    companion object {
        fun fromString(name: String): VegetableType {
            return entries.find {
                it.commonName.contains(name, ignoreCase = true) ||
                it.name.equals(name, ignoreCase = true)
            } ?: OTHER
        }
    }
}

data class VegetableBatch(
    val batchId: String,
    val vegetableName: String,
    val variety: String = "",
    val quantity: Double,
    val unit: String = "kg",
    val farmName: String,
    val farmLocation: String,
    val harvestDate: String,
    val organicCertified: Boolean = true,
    val qualityGrade: String = "Grade A",
    val description: String = "",
    val qrPayloadUrl: String = "https://farmtrace.app/track/$batchId",
    val currentStatus: BatchStatus = BatchStatus.REGISTERED,
    val createdAt: Long = System.currentTimeMillis(),
    val farmerName: String = "Rajesh Patel",
    val farmerContact: String = "farmer@greenvalley.ag",
    val pricePerKg: Double = 40.0,
    val remainingDemoStock: Double = quantity,
    val isDemoBatch: Boolean = false
)

data class JourneyEvent(
    val id: String,
    val batchId: String,
    val eventType: EventType,
    val stage: JourneyStage,
    val locationName: String,
    val cityRegion: String,
    val distanceFromPrevKm: Double,
    val handlerRole: String,
    val description: String,
    val temperatureC: Double? = null,
    val humidityPercent: Int? = null,
    val recordedTimestamp: String,
    val timestampMillis: Long = System.currentTimeMillis(),
    val isVerified: Boolean = true
)

data class SimulatedPurchase(
    val id: String = UUID.randomUUID().toString(),
    val customerName: String,
    val batchId: String,
    val vegetableName: String,
    val quantityKg: Double,
    val totalPrice: Double,
    val shopName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val formattedTime: String = "Just now",
    val customerAvatarColor: Long = 0xFF35A853
)

data class DemoMarketStats(
    val totalSalesRupees: Double = 0.0,
    val totalKgSold: Double = 0.0,
    val totalCustomersCount: Int = 0,
    val topSellingVegetable: String = "Tomato",
    val availableDemoInventoryKg: Double = 0.0,
    val simulatedPurchasesCount: Int = 0
)
