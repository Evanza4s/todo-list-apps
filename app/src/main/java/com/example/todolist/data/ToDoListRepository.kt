package com.example.todolist.data

import android.content.Context

class ToDoListRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun insertActivity(t: DataModel): Long {
        return dbHelper.insertTask(t)
    }

    fun getAllActivity(): List<DataModel> {
        return dbHelper.fetchAllData()
    }

    fun getTaskById(taskId: Int): DataModel? {
        return dbHelper.fetchTaskById(taskId)
    }

    fun deleteActivity(id: Int) {
        dbHelper.deleteTask(id)
    }

    fun updateDataActivity(t: DataModel) {
        dbHelper.updateTask(t)
    }

    fun searchActivity(queryText: String): List<DataModel> {
        if (queryText.isEmpty()) return getAllActivity()
        return dbHelper.fetchSearchData(queryText)
    }
}
