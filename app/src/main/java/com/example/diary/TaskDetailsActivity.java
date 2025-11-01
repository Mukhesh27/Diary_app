package com.example.diary;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
public class TaskDetailsActivity extends AppCompatActivity {
    TextView txtTitle, txtTime;
    EditText edtDetails;
    ImageButton btnSave;
    int taskId = -1;
    DiaryDatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        txtTitle = findViewById(R.id.txtTitle);
        txtTime = findViewById(R.id.txtTime);
        edtDetails = findViewById(R.id.edtDetails);
        btnSave = findViewById(R.id.btnSave);
        dbHelper = new DiaryDatabaseHelper(this);
        taskId = getIntent().getIntExtra("taskId", -1);
        String taskName = getIntent().getStringExtra("task");
        String startTime = getIntent().getStringExtra("startTime");
        String endTime = getIntent().getStringExtra("endTime");
        String details = getIntent().getStringExtra("details");
        txtTitle.setText(taskName);
        if (endTime == null || endTime.equals("--") || endTime.trim().isEmpty()) {
            txtTime.setText(startTime);
        } else {
            txtTime.setText(startTime + " - " + endTime);
        }
        edtDetails.setText(details != null ? details : "");
        btnSave.setOnClickListener(v -> saveTaskDetails());
    }
    private void saveTaskDetails() {
        String updatedDetails = edtDetails.getText().toString().trim();
        if (taskId != -1) {
            dbHelper.updateTaskDetails(taskId, updatedDetails);
            Toast.makeText(this, "Details saved!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error: Task not found!", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        String updatedDetails = edtDetails.getText().toString().trim();
        if (taskId != -1) {
            dbHelper.updateTaskDetails(taskId, updatedDetails);
        }
    }
}
