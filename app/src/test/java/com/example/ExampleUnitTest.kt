package com.example

import com.example.data.model.BatchStatus
import com.example.data.model.EventType
import com.example.data.model.JourneyEvent
import com.example.data.model.JourneyStage
import com.example.data.model.VegetableBatch
import com.example.data.model.VegetableType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun batch_qrPayloadUrl_formattedCorrectly() {
        val batch = VegetableBatch(
            batchId = "FT-2026-000123",
            vegetableName = "Tomato",
            variety = "Roma",
            quantity = 1000.0,
            farmName = "Green Valley Organics",
            farmLocation = "Salinas Valley, CA",
            harvestDate = "2026-10-31"
        )

        assertEquals("FT-2026-000123", batch.batchId)
        assertEquals("https://farmtrace.app/track/FT-2026-000123", batch.qrPayloadUrl)
        assertEquals(BatchStatus.REGISTERED, batch.currentStatus)
    }

    @Test
    fun vegetableType_lookup_returnsCorrectEmojiAndColor() {
        val tomato = VegetableType.fromString("Tomato")
        assertEquals("🍅", tomato.emoji)

        val potato = VegetableType.fromString("Potato")
        assertEquals("🥔", potato.emoji)

        val onion = VegetableType.fromString("Onion")
        assertEquals("🧅", onion.emoji)
    }

    @Test
    fun journeyEvent_stage_matchesExpectedEventType() {
        assertEquals(JourneyStage.FARM, EventType.HARVESTED.stage)
        assertEquals(JourneyStage.WHOLESALE_MARKET, EventType.RECEIVED_AT_MARKET.stage)
        assertEquals(JourneyStage.TRANSPORTATION, EventType.COLD_CHAIN_TRANSIT.stage)
        assertEquals(JourneyStage.RETAIL_SHOP, EventType.SHELVED_FOR_SALE.stage)
        assertEquals(JourneyStage.CONSUMER_KITCHEN, EventType.PURCHASED_BY_SHOPPER.stage)
    }
}
