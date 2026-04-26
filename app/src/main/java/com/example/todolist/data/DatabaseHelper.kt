package com.example.todolist.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "todo_app.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
            CREATE TABLE todolist(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT,
                description TEXT,
                is_done INTEGER,
                due_date TEXT,
                priority VARCHAR
            )
        """.trimIndent()
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS todolist")
        onCreate(db)
    }

    fun insertTask(t: DataModel): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", t.title)
            put("description", t.description)
            put("is_done", if (t.isDone) 1 else 0)
            put("due_date", t.dueDateMillis)
            put("priority", t.priority.name)
        }
        return db.insert("todolist", null, values)
    }

    fun updateTask(t: DataModel) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", t.title)
            put("description", t.description)
            put("is_done", if (t.isDone) 1 else 0)
            put("due_date", t.dueDateMillis)
            put("priority", t.priority.name)
        }
        db.update("todolist", values, "id=?", arrayOf(t.id.toString()))
    }

    fun deleteTask(id: Int) {
        val db = writableDatabase
        db.delete("todolist", "id=?", arrayOf(id.toString()))
    }

    fun fetchAllData(): List<DataModel> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM todolist ORDER BY is_done ASC, due_date ASC", null)
        val list = mutableListOf<DataModel>()

        while (cursor.moveToNext()) {
            list.add(
                DataModel(
                    id = cursor.getInt(0),
                    title = cursor.getString(1),
                    description = cursor.getString(2),
                    isDone = cursor.getInt(3) == 1,
                    dueDateMillis = cursor.getLong(4),
                    priority = Priority.valueOf(cursor.getString(5))
                )
            )
        }
        cursor.close()
        return list
    }

    fun fetchTaskById(taskId: Int): DataModel? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM todolist WHERE id = ? LIMIT 1",
            arrayOf(taskId.toString())
        )

        val task = if (cursor.moveToFirst()) {
            DataModel(
                id = cursor.getInt(0),
                title = cursor.getString(1),
                description = cursor.getString(2),
                isDone = cursor.getInt(3) == 1,
                dueDateMillis = cursor.getLong(4),
                priority = Priority.valueOf(cursor.getString(5))
            )
        } else {
            null
        }

        cursor.close()
        return task
    }

    fun fetchSearchData(queryText: String): List<DataModel> {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM todolist WHERE title LIKE ? OR description LIKE ? ORDER BY is_done ASC, due_date ASC",
            arrayOf("%$queryText%", "%$queryText%")
        )
        val list = mutableListOf<DataModel>()

        while (cursor.moveToNext()) {
            list.add(
                DataModel(
                    id = cursor.getInt(0),
                    title = cursor.getString(1),
                    description = cursor.getString(2),
                    isDone = cursor.getInt(3) == 1,
                    dueDateMillis = cursor.getLong(4),
                    priority = Priority.valueOf(cursor.getString(5))
                )
            )
        }
        cursor.close()
        return list
    }
}
