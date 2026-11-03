package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [BatchEntity::class, JourneyEventEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FarmTraceDatabase : RoomDatabase() {
    abstract fun batchDao(): BatchDao
    abstract fun journeyEventDao(): JourneyEventDao

    companion object {
        @Volatile
        private var INSTANCE: FarmTraceDatabase? = null

        fun getDatabase(context: Context): FarmTraceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FarmTraceDatabase::class.java,
                    "farmtrace_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
