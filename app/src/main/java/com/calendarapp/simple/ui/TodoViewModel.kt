package com.calendarapp.simple.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.calendarapp.simple.data.Category
import com.calendarapp.simple.data.TodoItem
import com.calendarapp.simple.data.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoViewModel(private val repository: TodoRepository) : ViewModel() {

    private val _selectedCategories = MutableStateFlow(Category.entries.toSet())
    val selectedCategories: StateFlow<Set<Category>> = _selectedCategories

    val allItems: StateFlow<List<TodoItem>> = repository.allItems.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val pendingItems: StateFlow<List<TodoItem>> = combine(allItems, selectedCategories) { items, cats ->
        items.filter { !it.isCompleted && it.category in cats }
            .sortedWith(compareBy({ it.year }, { it.month }, { it.day }))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedItems: StateFlow<List<TodoItem>> = combine(allItems, selectedCategories) { items, cats ->
        items.filter { it.isCompleted && it.category in cats }
            .sortedWith(compareBy({ it.year }, { it.month }, { it.day }))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleCategory(category: Category) {
        val current = _selectedCategories.value.toMutableSet()
        if (category in current) current.remove(category) else current.add(category)
        _selectedCategories.value = current
    }

    fun selectAllCategories() {
        _selectedCategories.value = Category.entries.toSet()
    }

    fun clearCategoryFilter() {
        _selectedCategories.value = emptySet()
    }

    fun addTodo(title: String, category: Category, year: Int, month: Int, day: Int) {
        viewModelScope.launch {
            repository.insert(
                TodoItem(title = title, category = category, year = year, month = month, day = day)
            )
        }
    }

    fun toggleHighlight(item: TodoItem) {
        viewModelScope.launch {
            repository.update(item.copy(isHighlighted = !item.isHighlighted))
        }
    }

    fun toggleCompleted(item: TodoItem) {
        viewModelScope.launch {
            repository.update(item.copy(isCompleted = !item.isCompleted))
        }
    }

    fun deleteTodo(item: TodoItem) {
        viewModelScope.launch {
            repository.delete(item)
        }
    }

    class Factory(private val repository: TodoRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TodoViewModel(repository) as T
        }
    }
}
