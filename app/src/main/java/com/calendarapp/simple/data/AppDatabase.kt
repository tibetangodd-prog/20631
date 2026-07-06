package com.calendarapp.simple.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [TodoItem::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // 這個資料庫檔案會存放在 App 專屬的 databases/ 資料夾（內部儲存空間），
        // 手機系統設定裡的「清除快取(cache)」不會動到這裡，
        // 只有「清除儲存空間(storage)」或移除 App 才會刪除。
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "calendar_app.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
