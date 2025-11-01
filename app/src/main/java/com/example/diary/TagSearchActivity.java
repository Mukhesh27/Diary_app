package com.example.diary;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
public class TagSearchActivity extends AppCompatActivity {
    private EditText tagInput;
    private Button searchButton;
    private LinearLayout resultsContainer;
    private DiaryDatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Search by Tag");
        }
        tagInput = findViewById(R.id.tagInput);
        searchButton = findViewById(R.id.searchButton);
        resultsContainer = findViewById(R.id.resultsContainer);
        dbHelper = new DiaryDatabaseHelper(this);
        searchButton.setOnClickListener(v -> {
            String tag = tagInput.getText().toString().trim();
            if (tag.isEmpty()) {
                Toast.makeText(this, "Please enter a tag", Toast.LENGTH_SHORT).show();
                return;
            }
            List<TaskModel> tasks = dbHelper.getTasksByTag(tag);
            if (tasks.isEmpty()) {
                Toast.makeText(this, "No diary pages found for tag: " + tag, Toast.LENGTH_SHORT).show();
            }
            displayDiaryLinks(tasks);
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void displayDiaryLinks(List<TaskModel> tasks) {
        resultsContainer.removeAllViews();
        Map<String, ArrayList<TaskModel>> groupedTasks = new LinkedHashMap<>();
        for (TaskModel task : tasks) {
            String date = task.getDate();
            if (!groupedTasks.containsKey(date)) {
                groupedTasks.put(date, new ArrayList<>());
            }
            groupedTasks.get(date).add(task);
        }
        for (String date : groupedTasks.keySet()) {
            TextView dateHeader = new TextView(this);
            dateHeader.setText(date);
            dateHeader.setTextSize(18);
            dateHeader.setPadding(20, 30, 10, 10);
            resultsContainer.addView(dateHeader);
            TextView diaryItem = new TextView(this);
            diaryItem.setText("📝 View diary page");
            diaryItem.setTextSize(16);
            diaryItem.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            diaryItem.setPadding(40, 10, 10, 30);
            diaryItem.setClickable(true);
            diaryItem.setBackgroundResource(android.R.drawable.list_selector_background);
            diaryItem.setOnClickListener(v -> {
                Intent intent = new Intent(TagSearchActivity.this, DiaryActivity.class);
                intent.putExtra("selectedDate", date);
                startActivity(intent);
            });
            resultsContainer.addView(diaryItem);
        }
        if (groupedTasks.isEmpty()) {
            TextView noResults = new TextView(this);
            noResults.setText("No diary pages found.");
            noResults.setTextSize(16);
            noResults.setPadding(20, 40, 10, 20);
            resultsContainer.addView(noResults);
        }
    }
}
