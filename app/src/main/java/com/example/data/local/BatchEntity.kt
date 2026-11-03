package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.BatchStatus
import com.example.data.model.EventType
import com.example.data.model.JourneyEvent
import com.example.data.model.JourneyStage
import com.example.data.model.VegetableBatch

@Entity(tableName = "batches")
data class BatchEntity(
    @PrimaryKey
    val batchId: String,
    val vegetableName: String,
    val variety: String,
    val quantity: Double,
    val unit: String,
    val farmName: String,
    val farmLocation: String,
    val harvestDate: String,
    val organicCertified: Boolean,
    val qualityGrade: String,
    val description: String,
    val qrPayloadUrl: String,
    val currentStatus: String,
    val createdAt: Long,
    val farmerName: String,
    val farmerContact: String,
    val pricePerKg: Double = 40.0,
    val isDemoBatch: Boolean = false
) {
    fun toDomain(): VegetableBatch {
        return VegetableBatch(
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
            qrPayloadUrl = qrPayloadUrl,
            currentStatus = BatchStatus.fromString(currentStatus),
            createdAt = createdAt,
            farmerName = farmerName,
            farmerContact = farmerContact,
            pricePerKg = pricePerKg,
            remainingDemoStock = quantity,
            isDemoBatch = isDemoBatch
        )
    }

    companion object {
        fun fromDomain(domain: VegetableBatch): BatchEntity {
            return BatchEntity(
                batchId = domain.batchId,
                vegetableName = domain.vegetableName,
                variety = domain.variety,
                quantity = domain.quantity,
                unit = domain.unit,
                farmName = domain.farmName,
                farmLocation = domain.farmLocation,
                harvestDate = domain.harvestDate,
                organicCertified = domain.organicCertified,
                qualityGrade = domain.qualityGrade,
                description = domain.description,
                qrPayloadUrl = domain.qrPayloadUrl,
                currentStatus = domain.currentStatus.name,
                createdAt = domain.createdAt,
                farmerName = domain.farmerName,
                farmerContact = domain.farmerContact,
                pricePerKg = domain.pricePerKg,
                isDemoBatch = domain.isDemoBatch
            )
        }
    }
}

@Entity(tableName = "journey_events")
data class JourneyEventEntity(
    @PrimaryKey
    val id: String,
    val batchId: String,
    val eventTypeName: String,
    val stageName: String,
    val locationName: String,
    val cityRegion: String,
    val distanceFromPrevKm: Double,
    val handlerRole: String,
    val description: String,
    val temperatureC: Double?,
    val humidityPercent: Int?,
    val recordedTimestamp: String,
    val timestampMillis: Long,
    val isVerified: Boolean
) {
    fun toDomain(): JourneyEvent {
        return JourneyEvent(
            id = id,
            batchId = batchId,
            eventType = EventType.fromString(eventTypeName),
            stage = JourneyStage.fromString(stageName),
            locationName = locationName,
            cityRegion = cityRegion,
            distanceFromPrevKm = distanceFromPrevKm,
            handlerRole = handlerRole,
            description = description,
            temperatureC = temperatureC,
            humidityPercent = humidityPercent,
            recordedTimestamp = recordedTimestamp,
            timestampMillis = timestampMillis,
            isVerified = isVerified
        )
    }

    companion object {
        fun fromDomain(domain: JourneyEvent): JourneyEventEntity {
            return JourneyEventEntity(
                id = domain.id,
                batchId = domain.batchId,
                eventTypeName = domain.eventType.name,
                stageName = domain.stage.name,
                locationName = domain.locationName,
                cityRegion = domain.cityRegion,
                distanceFromPrevKm = domain.distanceFromPrevKm,
                handlerRole = domain.handlerRole,
                description = domain.description,
                temperatureC = domain.temperatureC,
                humidityPercent = domain.humidityPercent,
                recordedTimestamp = domain.recordedTimestamp,
                timestampMillis = domain.timestampMillis,
                isVerified = domain.isVerified
            )
        }
    }
}
