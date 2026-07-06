package com.calendarapp.simple.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "todo_items")
data class TodoItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: Category,
    val year: Int,
    val month: Int,
    val day: Int,
    val isHighlighted: Boolean = false,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    val date: LocalDate
        get() = LocalDate.of(year, month, day)

    // 期限已到（今天之後）且尚未完成 -> 逾期
    fun isOverdue(): Boolean = !isCompleted && date.isBefore(LocalDate.now())
}
