package com.convergencelabstfx.smartdrone.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.convergencelabstfx.keyfinder.harmony.VoicingTemplate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber


@Database(entities = [VoicingTemplateEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class DroneDatabase : RoomDatabase() {

    abstract fun voicingTemplateDao(): VoicingTemplateDao

    companion object {
        @Volatile
        private var INSTANCE: DroneDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): DroneDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                Timber.i("made it past synchronized")
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        DroneDatabase::class.java,
                        "drone_database")
                        .fallbackToDestructiveMigration() // todo: look into what this call does
                        .addCallback(DroneDatabaseCallback(scope))
                        .build()
                Timber.i("made it past instance initialization")
                INSTANCE = instance
                return instance
            }
        }
    }

    private class DroneDatabaseCallback(private val scope: CoroutineScope)
        : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Timber.i("made it here")
            Timber.i("onCreate database called")
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    initDatabase(database.voicingTemplateDao())
                }
            }
        }

//        override fun onOpen(db: SupportSQLiteDatabase) {
//            super.onOpen(db)
//            Timber.i("onOpen called")
//            INSTANCE?.let { database ->
//                scope.launch(Dispatchers.IO) {
//                    initDatabase(database.voicingTemplateDao())
//                }
//            }
//        }


        fun initDatabase(voicingTemplateDao: VoicingTemplateDao) {

            val template = VoicingTemplate()
            template.addBassTone(0)
            template.addChordTone(0)
            template.addChordTone(4)
            template.addChordTone(9)
            voicingTemplateDao.insertTemplate(VoicingTemplateEntity(template))

            val template2 = VoicingTemplate()
            template2.addBassTone(0)
            template2.addChordTone(6)
            template2.addChordTone(9)
            template2.addChordTone(11)
            voicingTemplateDao.insertTemplate(VoicingTemplateEntity(template2))

            val template3 = VoicingTemplate()
            template3.addBassTone(0)
            template3.addChordTone(3)
            template3.addChordTone(6)
            template3.addChordTone(9)
            voicingTemplateDao.insertTemplate(VoicingTemplateEntity(template3))
        }


    }


}