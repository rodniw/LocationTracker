package dev.example.ru.locationtracker

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [LocationModel::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun locationDao() : LocationDao

    companion object {

        /**
         * var for singleton
         */
        private var INSTANCE: AppDatabase? = null
        /**
         * val lock for safety threading
         */
        private val lock = Any()

        /**
         * classic getInstance function with singleton pattern
         *
         * this function uses synchronized(lock) for threading safety reason
         */
        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(lock) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.applicationContext,
                            AppDatabase::class.java, "app.db")
                            .build()
                    }
                    return INSTANCE as AppDatabase
                }
            }
            return INSTANCE as AppDatabase
        }
    }
}