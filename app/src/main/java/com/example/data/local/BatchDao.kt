package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BatchDao {
    @Query("SELECT * FROM batches ORDER BY createdAt DESC")
    fun getAllBatchesFlow(): Flow<List<BatchEntity>>

    @Query("SELECT * FROM batches ORDER BY createdAt DESC")
    suspend fun getAllBatches(): List<BatchEntity>

    @Query("SELECT * FROM batches WHERE batchId = :batchId LIMIT 1")
    suspend fun getBatchById(batchId: String): BatchEntity?

    @Query("SELECT * FROM batches WHERE batchId = :batchId LIMIT 1")
    fun getBatchByIdFlow(batchId: String): Flow<BatchEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatch(batch: BatchEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatches(batches: List<BatchEntity>)

    @Update
    suspend fun updateBatch(batch: BatchEntity)

    @Query("UPDATE batches SET currentStatus = :status WHERE batchId = :batchId")
    suspend fun updateBatchStatus(batchId: String, status: String)

    @Query("DELETE FROM batches WHERE batchId = :batchId")
    suspend fun deleteBatch(batchId: String)
}

@Dao
interface JourneyEventDao {
    @Query("SELECT * FROM journey_events WHERE batchId = :batchId ORDER BY timestampMillis ASC")
    fun getEventsForBatchFlow(batchId: String): Flow<List<JourneyEventEntity>>

    @Query("SELECT * FROM journey_events WHERE batchId = :batchId ORDER BY timestampMillis ASC")
    suspend fun getEventsForBatch(batchId: String): List<JourneyEventEntity>

    @Query("SELECT * FROM journey_events ORDER BY timestampMillis DESC")
    fun getAllEventsFlow(): Flow<List<JourneyEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: JourneyEventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<JourneyEventEntity>)

    @Query("DELETE FROM journey_events WHERE batchId = :batchId")
    suspend fun deleteEventsForBatch(batchId: String)
}
