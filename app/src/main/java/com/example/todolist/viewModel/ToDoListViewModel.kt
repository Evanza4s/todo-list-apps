package com.example.todolist.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.DataModel
import com.example.todolist.data.ToDoListRepository
import com.example.todolist.notifications.AndroidAlarmScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ToDoListViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = ToDoListRepository(application)
    private val alarmScheduler = AndroidAlarmScheduler(application)
    private var recentlyDeletedItem: DataModel? = null

    private var currentSearchQuery = ""

    private val _activityList = MutableStateFlow<List<DataModel>>(emptyList())
    val activityList: StateFlow<List<DataModel>> = _activityList.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                if (currentSearchQuery.isEmpty()) {
                    repo.getAllActivity()
                } else {
                    repo.searchActivity(currentSearchQuery)
                }
            }
            _activityList.value = result
        }
    }

    fun insert(t: DataModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val newId = repo.insertActivity(t)
            val savedTask = t.copy(id = newId.toInt())
            if (!savedTask.isDone) {
                alarmScheduler.schedule(savedTask)
            }
            loadData()
        }
    }

    fun delete(task: DataModel) {
        viewModelScope.launch(Dispatchers.IO) {
            recentlyDeletedItem = task
            repo.deleteActivity(task.id)
            alarmScheduler.cancel(task)
            loadData()
        }
    }

    fun update(t: DataModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateDataActivity(t)
            if (t.isDone) {
                alarmScheduler.cancel(t)
            } else {
                alarmScheduler.cancel(t)
                alarmScheduler.schedule(t)
            }
            loadData()
        }
    }

    fun search(newQuery: String) {
        currentSearchQuery = newQuery
        loadData()
    }

    suspend fun getTaskById(taskId: Int): DataModel? {
        return withContext(Dispatchers.IO) {
            repo.getTaskById(taskId)
        }
    }

    fun undoDelete() {
        recentlyDeletedItem?.let { task ->
            viewModelScope.launch(Dispatchers.IO) {
                val restoredId = repo.insertActivity(task)
                val restoredTask = task.copy(id = restoredId.toInt())
                if (!restoredTask.isDone) {
                    alarmScheduler.schedule(restoredTask)
                }
                recentlyDeletedItem = null
                loadData()
            }
        }
    }
}
