package com.example.diary;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
public class DiaryDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "diary.db";
    private static final int DATABASE_VERSION = 4;
    private static final String TABLE_DIARY = "diary";
    private static final String COL_ID = "id";
    private static final String COL_DATE = "date";
    private static final String COL_CONTENT = "content";
    private static final String COL_TAGS = "tags";
    private static final String TABLE_TASKS = "tasks";
    private static final String COL_TASK_ID = "id";
    private static final String COL_TASK_DATE = "date";
    private static final String COL_START = "start";
    private static final String COL_END = "end";
    private static final String COL_TASK = "task";
    private static final String COL_NOTE = "note";
    public DiaryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createDiaryTable = "CREATE TABLE " + TABLE_DIARY + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DATE + " TEXT UNIQUE, " +
                COL_CONTENT + " TEXT, " +
                COL_TAGS + " TEXT)";
        db.execSQL(createDiaryTable);
        String createTasksTable = "CREATE TABLE " + TABLE_TASKS + " (" +
                COL_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TASK_DATE + " TEXT, " +
                COL_START + " TEXT, " +
                COL_END + " TEXT, " +
                COL_TASK + " TEXT, " +
                COL_NOTE + " TEXT)";
        db.execSQL(createTasksTable);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE " + TABLE_DIARY + " ADD COLUMN " + COL_TAGS + " TEXT");
        }
    }
    public void insertTask(String date, String start, String end, String task, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TASK_DATE, date);
        values.put(COL_START, start);
        values.put(COL_END, end);
        values.put(COL_TASK, task);
        values.put(COL_NOTE, note);
        db.insert(TABLE_TASKS, null, values);
        db.close();
    }
    public void updateTask(int id, String date, String start, String end, String task, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TASK_DATE, date);
        values.put(COL_START, start);
        values.put(COL_END, end);
        values.put(COL_TASK, task);
        values.put(COL_NOTE, note);
        db.update(TABLE_TASKS, values, COL_TASK_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }
    public TaskModel getTaskById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TASKS + " WHERE id = ?", new String[]{String.valueOf(id)});
        TaskModel task = null;
        if (cursor != null && cursor.moveToFirst()) {
            String date = cursor.getString(cursor.getColumnIndexOrThrow(COL_TASK_DATE));
            String start = cursor.getString(cursor.getColumnIndexOrThrow(COL_START));
            String end = cursor.getString(cursor.getColumnIndexOrThrow(COL_END));
            String taskText = cursor.getString(cursor.getColumnIndexOrThrow(COL_TASK));
            String note = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTE));
            task = new TaskModel(id, date, start, end, taskText, note);
            cursor.close();
        }
        db.close();
        return task;
    }
    public ArrayList<TaskModel> getTasksByDate(String date) {
        ArrayList<TaskModel> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tasks WHERE date = ?", new String[]{date});
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String taskDate = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String start = cursor.getString(cursor.getColumnIndexOrThrow("start"));
                String end = cursor.getString(cursor.getColumnIndexOrThrow("end"));
                String taskText = cursor.getString(cursor.getColumnIndexOrThrow("task"));
                String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));

                TaskModel task = new TaskModel(id, taskDate, start, end, taskText, note);
                taskList.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return taskList;
    }
    public void deleteTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("tasks", "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }
    public void updateTaskDetails(int taskId, String updatedDetails) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("note", updatedDetails);
        db.update("tasks", values, "id = ?", new String[]{String.valueOf(taskId)});
        db.close();
    }
    public String normalizeDate(String date) {
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date parsed = input.parse(date);
            return input.format(parsed);
        } catch (Exception e) {
            return date.trim();
        }
    }
    public String getTagsByDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder tagsBuilder = new StringBuilder();
        Cursor cursor = db.rawQuery("SELECT tags FROM diary WHERE date = ?", new String[]{date});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String tags = cursor.getString(cursor.getColumnIndexOrThrow("tags"));
                if (tags != null && !tags.isEmpty()) {
                    tagsBuilder.append(tags).append(",");
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        if (tagsBuilder.length() > 0) {
            tagsBuilder.setLength(tagsBuilder.length() - 1);
        }
        return tagsBuilder.toString();
    }
    public void saveTagsForDate(String date, String tags) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tags", tags);
        int rows = db.update("diary", values, "date = ?", new String[]{date});
        if (rows == 0) {
            values.put("date", date);
            db.insert("diary", null, values);
        }
        db.close();
    }
    public ArrayList<TaskModel> getTasksByTag(String tag) {
        ArrayList<TaskModel> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT t.id, t.date, t.start, t.end, t.task, t.note, d.tags " +
                "FROM tasks t LEFT JOIN diary d ON t.date = d.date " +
                "WHERE LOWER(d.tags) LIKE ? " +
                "ORDER BY t.date DESC";
        Cursor cursor = db.rawQuery(query, new String[]{"%" + tag.toLowerCase() + "%"});
        if (cursor.moveToFirst()) {
            do {
                TaskModel task = new TaskModel();
                task.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                task.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                task.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow("start")));
                task.setEndTime(cursor.getString(cursor.getColumnIndexOrThrow("end")));
                task.setTask(cursor.getString(cursor.getColumnIndexOrThrow("task")));
                task.setNote(cursor.getString(cursor.getColumnIndexOrThrow("note")));
                task.setTags(cursor.getString(cursor.getColumnIndexOrThrow("tags")));
                taskList.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return taskList;
    }
    public void saveDiaryContent(String date, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM diary WHERE date = ?", new String[]{date});
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("content", content);
        if (cursor.moveToFirst()) {
            db.update("diary", values, "date = ?", new String[]{date});
        } else {
            db.insert("diary", null, values);
        }
        cursor.close();
        db.close();
    }
    public String getDiaryContent(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String content = null;
        Cursor cursor = db.rawQuery("SELECT content FROM diary WHERE date = ?", new String[]{date});
        if (cursor.moveToFirst()) {
            content = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return content;
    }

}
