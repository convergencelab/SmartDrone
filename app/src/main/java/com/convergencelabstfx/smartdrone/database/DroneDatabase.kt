package com.convergencelabstfx.smartdrone.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [VoicingTemplateEntity::class], version = 1, exportSchema = false)
abstract class DroneDatabase : RoomDatabase() {

    abstract fun voicingTemplateDao(): VoicingTemplateDao

    companion object {
        @Volatile
        private var INSTANCE: DroneDatabase? = null

        fun getDatabase(context: Context): DroneDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                context.applicationContext,
                DroneDatabase::class.java,
                "drone_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}