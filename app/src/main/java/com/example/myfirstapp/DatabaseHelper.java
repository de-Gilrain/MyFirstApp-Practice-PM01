package com.example.myfirstapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TasksDB.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_TASKS = "tasks";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESC = "description";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_TASKS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT NOT NULL, " +
                    COLUMN_DESC + " TEXT NOT NULL)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    public boolean addTask(String title, String desc) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_DESC, desc);
        long result = db.insert(TABLE_TASKS, null, cv);
        db.close();
        return result != -1;
    }

    public List<Task> getAllTasks() {
        return getTasksSortedByTitle(null);
    }

    public List<Task> getTasksSortedByTitle(String searchQuery) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = null;
        String[] selectionArgs = null;

        if (searchQuery != null && !searchQuery.isEmpty()) {
            selection = COLUMN_TITLE + " LIKE ?";
            selectionArgs = new String[]{"%" + searchQuery + "%"};
        }

        Cursor cursor = db.query(TABLE_TASKS, null, selection, selectionArgs, null, null,
                COLUMN_TITLE + " ASC");

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task();
                task.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                task.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
                task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESC)));
                tasks.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tasks;
    }

    public boolean updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, task.getTitle());
        cv.put(COLUMN_DESC, task.getDescription());
        int result = db.update(TABLE_TASKS, cv, COLUMN_ID + "=?",
                new String[]{String.valueOf(task.getId())});
        db.close();
        return result > 0;
    }

    public boolean deleteTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_TASKS, COLUMN_ID + "=?",
                new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    public Task getTask(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, null, COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        Task task = null;
        if (cursor.moveToFirst()) {
            task = new Task();
            task.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            task.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
            task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESC)));
        }
        cursor.close();
        db.close();
        return task;
    }

    public List<Task> searchTasks(String query) {
        List<Task> tasks = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String selection = COLUMN_TITLE + " LIKE ?";
            String[] selectionArgs = {"%" + query + "%"};
            Cursor cursor = db.query(TABLE_TASKS, null, selection, selectionArgs, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    Task task = new Task();
                    task.setId(cursor.getInt(0));
                    task.setTitle(cursor.getString(1));
                    task.setDescription(cursor.getString(2));
                    tasks.add(task);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            android.util.Log.e("DatabaseHelper", "Search error: " + e.getMessage());
        }
        return tasks;
    }

    public int getTasksCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_TASKS, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    public int getTodayTasksCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_TASKS + " WHERE date(" + COLUMN_ID + ") = date('now')",
                null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }
}