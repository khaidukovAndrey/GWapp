package com.example.kotlin_lesson_1.domain.models

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Item::class],
    version = 4,
    autoMigrations = [
        AutoMigration (from = 3, to = 4)
    ]
)
abstract class MainDbClass: RoomDatabase() {
    abstract fun getDao(): Dao
    companion object{
        fun getDb(context: Context): MainDbClass {
            return Room.databaseBuilder(
                context.applicationContext,
                MainDbClass::class.java,
                "skinscaner.db"
            ).build()
        }
    }
}