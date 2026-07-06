package com.calendarapp.simple.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo_items ORDER BY year ASC, month ASC, day ASC, id ASC")
    fun getAll(): Flow<List<TodoItem>>

    @Insert
    suspend fun insert(item: TodoItem): Long

    @Update
    suspend fun update(item: TodoItem)

    @Delete
    suspend fun delete(item: TodoItem)
}
