package com.example.diary;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.navigation.NavigationView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class DiaryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private String selectedDate;
    private CalendarView calendarView;
    private RecyclerView recyclerView;
    private TextView txtNoEntries, diaryTitle;
    private DiaryDatabaseHelper dbHelper;
    private ArrayList<TaskModel> dayTasks;
    private TaskAdapter taskAdapter;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private ChipGroup chipGroupTags;
    private EditText editTag;
    private Button btnAddTag, btnAddTask;
    private ImageView icForwardPage, icBackPage;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        calendarView = findViewById(R.id.calendarView);
        recyclerView = findViewById(R.id.recyclerViewDiary);
        txtNoEntries = findViewById(R.id.txtNoEntries);
        diaryTitle = findViewById(R.id.diaryTitle);
        chipGroupTags = findViewById(R.id.chipGroupTags);
        editTag = findViewById(R.id.editTag);
        btnAddTag = findViewById(R.id.btnAddTag);
        btnAddTask = findViewById(R.id.btnAddTask);
        icForwardPage = findViewById(R.id.icForwardPage);
        dbHelper = new DiaryDatabaseHelper(this);
        dayTasks = new ArrayList<>();
        taskAdapter = new TaskAdapter(this, dayTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(taskAdapter);
        calendarView.setMaxDate(System.currentTimeMillis());
        String intentDate = getIntent().getStringExtra("selectedDate");
        if (intentDate != null && !intentDate.isEmpty()) {
            selectedDate = intentDate;
            setCalendarToDate(selectedDate);
        } else {
            selectedDate = sdf.format(new Date());
            setCalendarToDate(selectedDate);
        }
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            refreshDiaryPage();
        });
        btnAddTag.setOnClickListener(v -> {
            String newTag = editTag.getText().toString().trim();
            if (!newTag.isEmpty()) {
                addTagChip(newTag);
                saveTagsToDatabase(selectedDate);
                editTag.setText("");
            }
        });
        btnAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(DiaryActivity.this, AddTaskActivity.class);
            intent.putExtra("selectedDate", selectedDate);
            startActivity(intent);
        });
        View.OnClickListener openDiaryDetails = v -> {
            if (selectedDate == null || selectedDate.isEmpty()) {
                selectedDate = sdf.format(new Date());
            }
            Intent intent = new Intent(DiaryActivity.this, DiaryDetailsActivity.class);
            intent.putExtra("selectedDate", selectedDate);
            startActivity(intent);
        };
        if (icForwardPage != null) icForwardPage.setOnClickListener(openDiaryDetails);
        if (icBackPage != null) icBackPage.setOnClickListener(openDiaryDetails);
        diaryTitle.setOnClickListener(openDiaryDetails);
    }
    private void setCalendarToDate(String date) {
        try {
            Date parsed = sdf.parse(date);
            if (parsed != null) {
                calendarView.post(() -> calendarView.setDate(parsed.getTime(), true, true));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        refreshDiaryPage();
    }
    private void refreshDiaryPage() {
        updateDiaryHeader(selectedDate);
        loadTasksForDate(selectedDate);
        loadTagsForDate(selectedDate);
    }
    private void updateDiaryHeader(String date) {
        diaryTitle.setText("Page of " + date);
    }
    private void loadTasksForDate(String date) {
        dayTasks.clear();
        dayTasks.addAll(dbHelper.getTasksByDate(dbHelper.normalizeDate(date)));
        Collections.sort(dayTasks, (t1, t2) -> t1.getStartTime().compareTo(t2.getStartTime()));
        taskAdapter.notifyDataSetChanged();
        if (dayTasks.isEmpty()) {
            txtNoEntries.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            txtNoEntries.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
    private void loadTagsForDate(String date) {
        chipGroupTags.removeAllViews();
        String tagString = dbHelper.getTagsByDate(date);
        if (tagString != null && !tagString.isEmpty()) {
            List<String> tags = Arrays.asList(tagString.split(","));
            for (String tag : tags) {
                addTagChip(tag.trim());
            }
        }
    }
    private void saveTagsToDatabase(String date) {
        List<String> tags = new ArrayList<>();
        for (int i = 0; i < chipGroupTags.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupTags.getChildAt(i);
            tags.add(chip.getText().toString());
        }
        dbHelper.saveTagsForDate(date, String.join(",", tags));
    }
    private void addTagChip(String tagText) {
        Chip chip = new Chip(this, null, com.google.android.material.R.style.Widget_MaterialComponents_Chip_Entry);
        chip.setText(tagText);
        chip.setCloseIconVisible(true);
        chip.setChipBackgroundColorResource(R.color.chip_bg_color);
        chip.setTextColor(getResources().getColor(R.color.chip_text_color));
        chip.setRippleColorResource(R.color.chip_ripple_color);
        chip.setOnCloseIconClickListener(v -> {
            chipGroupTags.removeView(chip);
            saveTagsToDatabase(selectedDate);
        });
        chipGroupTags.addView(chip);
    }
    @Override
    protected void onResume() {
        super.onResume();
        refreshDiaryPage();
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        drawerLayout.closeDrawer(GravityCompat.START);
        if (id == R.id.nav_tasks) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.nav_diary) {
            return true;
        } else if (id == R.id.nav_search_tags) {
            drawerLayout.postDelayed(() -> {
                Intent intent = new Intent(DiaryActivity.this, TagSearchActivity.class);
                startActivity(intent);
            }, 250);
            return true;
        }
        return false;
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
