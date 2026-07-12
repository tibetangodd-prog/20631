package com.calendarapp.simple.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromCategory(category: Category): String = category.name

    @TypeConverter
    fun toCategory(value: String): Category = Category.valueOf(value)

    @TypeConverter
    fun fromRecurrence(recurrence: RecurrenceType): String = recurrence.name

    @TypeConverter
    fun toRecurrence(value: String): RecurrenceType = RecurrenceType.valueOf(value)
}
