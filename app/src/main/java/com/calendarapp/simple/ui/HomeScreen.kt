package com.calendarapp.simple.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.calendarapp.simple.data.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: TodoViewModel) {
    val pendingItems by viewModel.pendingItems.collectAsState()
    val completedItems by viewModel.completedItems.collectAsState()
    val selectedCategories by viewModel.selectedCategories.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("行事曆", color = Color.Black) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "新增代辦事項")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White)
        ) {

            CategoryFilterRow(
                selected = selectedCategories,
                onToggle = { viewModel.toggleCategory(it) },
                onSelectAll = { viewModel.selectAllCategories() },
                onClearAll = { viewModel.clearCategoryFilter() }
            )

            TabRow(selectedTabIndex = selectedTab, containerColor = Color.White) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("待辦 (${pendingItems.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("已完成 (${completedItems.size})") }
                )
            }

            val listToShow = if (selectedTab == 0) pendingItems else completedItems

            if (listToShow.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        if (selectedTab == 0) "目前沒有待辦事項" else "尚未有完成的事項",
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(listToShow, key = { it.id }) { item ->
                        TodoItemRow(
                            item = item,
                            onToggleHighlight = { viewModel.toggleHighlight(item) },
                            onToggleCompleted = { viewModel.toggleCompleted(item) },
                            onDelete = { viewModel.deleteTodo(item) }
                        )
                        Divider(color = Color(0xFFE0E0E0))
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddEditTodoDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, category, year, month, day, recurrence ->
                viewModel.addTodo(title, category, year, month, day, recurrence)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun CategoryFilterRow(
    selected: Set<Category>,
    onToggle: (Category) -> Unit,
    onSelectAll: () -> Unit,
    onClearAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        val allSelected = selected.size == Category.entries.size
        FilterChip(
            selected = allSelected,
            onClick = { if (allSelected) onClearAll() else onSelectAll() },
            label = { Text("全選") }
        )
        Category.entries.forEach { category ->
            FilterChip(
                selected = category in selected,
                onClick = { onToggle(category) },
                label = { Text(category.displayName) }
            )
        }
    }
}
