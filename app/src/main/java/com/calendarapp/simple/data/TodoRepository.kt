package com.calendarapp.simple.data

import kotlinx.coroutines.flow.Flow

class TodoRepository(private val dao: TodoDao) {
    val allItems: Flow<List<TodoItem>> = dao.getAll()

    suspend fun insert(item: TodoItem) = dao.insert(item)
    suspend fun update(item: TodoItem) = dao.update(item)
    suspend fun delete(item: TodoItem) = dao.delete(item)
}
