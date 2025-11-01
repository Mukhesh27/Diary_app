package com.example.diary;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.view.GravityCompat;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
public class MainActivity extends AppCompatActivity {
    TextView txtDate, txtNoLogs;
    RecyclerView recyclerView;
    FloatingActionButton fabAdd;
    Button btnPastLogs;
    DiaryDatabaseHelper dbHelper;
    ArrayList<TaskModel> todayTasks;
    TaskAdapter taskAdapter;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageButton btnMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            drawerLayout.closeDrawers();
            if (id == R.id.nav_tasks) {
                return true;
            } else if (id == R.id.nav_diary) {
                startActivity(new Intent(MainActivity.this, DiaryActivity.class));
                return true;
            } else if (id == R.id.nav_search_tags) {
                startActivity(new Intent(MainActivity.this, TagSearchActivity.class));
                return true;
            }
            return false;
        });
        txtDate = findViewById(R.id.txtDate);
        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fabAdd);
        btnPastLogs = findViewById(R.id.btnPastLogs);
        txtNoLogs = findViewById(R.id.txtNoLogs);
        int nightModeFlags = getResources().getConfiguration().uiMode &
                android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            fabAdd.setColorFilter(getResources().getColor(android.R.color.white));
        } else {
            fabAdd.setColorFilter(getResources().getColor(android.R.color.black));
        }
        dbHelper = new DiaryDatabaseHelper(this);
        txtDate.setText(DateUtils.getTodayWithDay());
        todayTasks = dbHelper.getTasksByDate(dbHelper.normalizeDate(DateUtils.getTodayDate()));
        Collections.sort(todayTasks, new Comparator<TaskModel>() {
            @Override
            public int compare(TaskModel t1, TaskModel t2) {
                return t1.getStartTime().compareTo(t2.getStartTime());
            }
        });
        taskAdapter = new TaskAdapter(this, todayTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(taskAdapter);
        updateEmptyState();
        fabAdd.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            intent.putExtra("selectedDate", dbHelper.normalizeDate(DateUtils.getTodayDate()));
            startActivityForResult(intent, 1);
        });
        btnPastLogs.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PastLogsActivity.class);
            startActivity(intent);
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        refreshTaskList();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            refreshTaskList();
        }
    }
    private void refreshTaskList() {
        String today = dbHelper.normalizeDate(DateUtils.getTodayDate());
        todayTasks.clear();
        todayTasks.addAll(dbHelper.getTasksByDate(today));
        Collections.sort(todayTasks, new Comparator<TaskModel>() {
            @Override
            public int compare(TaskModel t1, TaskModel t2) {
                return t1.getStartTime().compareTo(t2.getStartTime());
            }
        });
        taskAdapter.notifyDataSetChanged();
        updateEmptyState();
    }
    private void updateEmptyState() {
        if (todayTasks.isEmpty()) {
            txtNoLogs.setVisibility(TextView.VISIBLE);
            recyclerView.setVisibility(RecyclerView.GONE);
        } else {
            txtNoLogs.setVisibility(TextView.GONE);
            recyclerView.setVisibility(RecyclerView.VISIBLE);
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
