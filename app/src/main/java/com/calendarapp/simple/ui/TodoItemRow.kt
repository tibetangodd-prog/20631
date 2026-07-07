package com.calendarapp.simple.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calendarapp.simple.data.TodoItem

@Composable
fun TodoItemRow(
    item: TodoItem,
    onToggleHighlight: () -> Unit,
    onToggleCompleted: () -> Unit,
    onDelete: () -> Unit
) {
    // 顏色規則：完成 -> 黑；逾期未完成 -> 紅；有標記 -> 藍(發光)；預設 -> 黑
    val textColor = when {
        item.isCompleted -> Color.Black
        item.isOverdue() -> Color.Red
        item.isHighlighted -> Color(0xFF1565FF)
        else -> Color.Black
    }

    val showGlow = item.isHighlighted && !item.isCompleted && !item.isOverdue()

    val textStyle = if (showGlow) {
        TextStyle(
            color = textColor,
            fontSize = 16.sp,
            shadow = Shadow(color = Color(0xFF4DA3FF), blurRadius = 18f)
        )
    } else {
        TextStyle(color = textColor, fontSize = 16.sp)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.isCompleted,
            onCheckedChange = { onToggleCompleted() }
        )

        IconButton(onClick = onToggleHighlight) {
            Icon(
                Icons.Filled.Star,
                contentDescription = if (item.isHighlighted) "取消標記" else "標記",
                tint = if (item.isHighlighted) Color(0xFF1565FF) else Color.LightGray
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.title, style = textStyle)
            Row {
                Text(
                    text = "%04d/%02d/%02d".format(item.year, item.month, item.day),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "【${item.category.displayName}】",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }
        }

        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "刪除", tint = Color.Gray)
        }
    }
}
