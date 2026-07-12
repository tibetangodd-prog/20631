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
    val recurrence: RecurrenceType = RecurrenceType.NONE,
    val createdAt: Long = System.currentTimeMillis()
) {
    val date: LocalDate
        get() = LocalDate.of(year, month, day)

    // 期限已到（今天之後）且尚未完成 -> 逾期
    fun isOverdue(): Boolean = !isCompleted && date.isBefore(LocalDate.now())

    /** 依重複規則算出下一次發生的日期，NONE 回傳 null */
    fun nextOccurrenceDate(): LocalDate? = when (recurrence) {
        RecurrenceType.NONE -> null
        RecurrenceType.MONTHLY -> date.plusMonths(1)
        RecurrenceType.YEARLY -> date.plusYears(1)
    }
}
