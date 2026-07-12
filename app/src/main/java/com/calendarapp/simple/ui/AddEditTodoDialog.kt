package com.calendarapp.simple.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calendarapp.simple.data.Category
import com.calendarapp.simple.data.RecurrenceType
import java.time.LocalDate

@Composable
fun AddEditTodoDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, category: Category, year: Int, month: Int, day: Int, recurrence: RecurrenceType) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(Category.HOMEWORK) }
    var selectedRecurrence by remember { mutableStateOf(RecurrenceType.NONE) }
    val today = remember { LocalDate.now() }
    var yearText by remember { mutableStateOf(today.year.toString()) }
    var monthText by remember { mutableStateOf(today.monthValue.toString()) }
    var dayText by remember { mutableStateOf(today.dayOfMonth.toString()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("新增代辦事項") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("事項名稱") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text("類別")
                Row(modifier = Modifier.fillMaxWidth()) {
                    Category.entries.forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category.displayName) },
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("日期（年 / 月 / 日）")
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = yearText,
                        onValueChange = { yearText = it.filter(Char::isDigit) },
                        label = { Text("年") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    OutlinedTextField(
                        value = monthText,
                        onValueChange = { monthText = it.filter(Char::isDigit) },
                        label = { Text("月") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    OutlinedTextField(
                        value = dayText,
                        onValueChange = { dayText = it.filter(Char::isDigit) },
                        label = { Text("日") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(it, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("重複")
                Row(modifier = Modifier.fillMaxWidth()) {
                    RecurrenceType.entries.forEach { recurrence ->
                        FilterChip(
                            selected = selectedRecurrence == recurrence,
                            onClick = { selectedRecurrence = recurrence },
                            label = { Text(recurrence.displayName) },
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val year = yearText.toIntOrNull()
                val month = monthText.toIntOrNull()
                val day = dayText.toIntOrNull()

                if (title.isBlank()) {
                    errorMessage = "請輸入事項名稱"
                    return@TextButton
                }
                if (year == null || month == null || day == null) {
                    errorMessage = "請輸入完整日期"
                    return@TextButton
                }
                try {
                    LocalDate.of(year, month, day)
                } catch (e: Exception) {
                    errorMessage = "日期不合法，請確認月份(1-12)與日期是否正確"
                    return@TextButton
                }
                onConfirm(title.trim(), selectedCategory, year, month, day, selectedRecurrence)
            }) {
                Text("儲存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
