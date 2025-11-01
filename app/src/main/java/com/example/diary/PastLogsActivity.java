package com.example.diary;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import java.util.ArrayList;
public class PastLogsActivity extends AppCompatActivity {
    private CalendarView calendarView;
    private RecyclerView recyclerView;
    private Button btnToday, btnAddTask;
    private TextView txtNoLogs;
    private DiaryDatabaseHelper dbHelper;
    private ArrayList<TaskModel> tasks;
    private TaskAdapter adapter;
    private String selectedDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_logs);
        calendarView = findViewById(R.id.calendarView);
        recyclerView = findViewById(R.id.recyclerViewLogs);
        btnToday = findViewById(R.id.btnToday);
        btnAddTask = findViewById(R.id.btnAddTask);
        txtNoLogs = findViewById(R.id.txtNoLogs);
        dbHelper = new DiaryDatabaseHelper(this);
        tasks = new ArrayList<>();
        adapter = new TaskAdapter(this, tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        calendarView.setMaxDate(System.currentTimeMillis());
        selectedDate = DateUtils.getTodayDate();
        loadTasksForDate(selectedDate);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
            loadTasksForDate(selectedDate);
        });
        btnToday.setOnClickListener(v -> {
            Intent intent = new Intent(PastLogsActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
        btnAddTask.setOnClickListener(v -> {
            String normalizedDate = dbHelper.normalizeDate(selectedDate);
            Intent intent = new Intent(PastLogsActivity.this, AddTaskActivity.class);
            intent.putExtra("selectedDate", normalizedDate);
            startActivity(intent);
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadTasksForDate(selectedDate);
    }
    private void loadTasksForDate(String date) {
        tasks.clear();
        tasks.addAll(dbHelper.getTasksByDate(date));
        adapter.notifyDataSetChanged();
        if (tasks.isEmpty()) {
            recyclerView.setVisibility(android.view.View.GONE);
            txtNoLogs.setVisibility(android.view.View.VISIBLE);
            txtNoLogs.setText("No tasks found for " + date);
        } else {
            recyclerView.setVisibility(android.view.View.VISIBLE);
            txtNoLogs.setVisibility(android.view.View.GONE);
        }
    }
}
