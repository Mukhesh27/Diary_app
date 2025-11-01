package com.example.diary;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
public class AddTaskActivity extends AppCompatActivity {
    private EditText startTimeInput, endTimeInput, taskInput;
    private Button saveButton;
    private DiaryDatabaseHelper dbHelper;
    private boolean is24HourFormat;
    private int editingTaskId = -1;
    private String taskDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        taskInput = findViewById(R.id.taskInput);
        startTimeInput = findViewById(R.id.startTimeInput);
        endTimeInput = findViewById(R.id.endTimeInput);
        saveButton = findViewById(R.id.saveButton);
        dbHelper = new DiaryDatabaseHelper(this);
        is24HourFormat = DateFormat.is24HourFormat(this);
        startTimeInput.setKeyListener(null);
        endTimeInput.setKeyListener(null);
        startTimeInput.setOnClickListener(v -> showTimePickerDialog(startTimeInput));
        endTimeInput.setOnClickListener(v -> showTimePickerDialog(endTimeInput));
        taskDate = DateUtils.getTodayDate();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("taskId")) {
                editingTaskId = extras.getInt("taskId");
                TaskModel task = dbHelper.getTaskById(editingTaskId);
                if (task != null) {
                    startTimeInput.setText(task.getStartTime());
                    endTimeInput.setText(task.getEndTime());
                    taskInput.setText(task.getTask());
                    taskDate = task.getDate();
                }
            }
            if (extras.containsKey("selectedDate")) {
                String rawDate = extras.getString("selectedDate");
                if (rawDate != null) {
                    taskDate = dbHelper.normalizeDate(rawDate);
                }
            }
        }
        saveButton.setOnClickListener(v -> saveTask());
    }
    private void showTimePickerDialog(EditText targetInput) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (TimePicker view, int hourOfDay, int minute1) -> {
                    String formattedTime;
                    if (is24HourFormat) {
                        formattedTime = String.format("%02d:%02d", hourOfDay, minute1);
                    } else {
                        int hour12 = hourOfDay % 12;
                        if (hour12 == 0) hour12 = 12;
                        String amPm = (hourOfDay < 12) ? "AM" : "PM";
                        formattedTime = String.format("%02d:%02d %s", hour12, minute1, amPm);
                    }
                    targetInput.setText(formattedTime);
                },
                hour,
                minute,
                is24HourFormat
        );
        timePickerDialog.show();
    }
    private void saveTask() {
        String task = taskInput.getText().toString().trim();
        String start = startTimeInput.getText().toString().trim();
        String end = endTimeInput.getText().toString().trim();
        if (task.isEmpty() || start.isEmpty()) {
            Toast.makeText(this, "Please enter task and start time", Toast.LENGTH_SHORT).show();
            return;
        }
        if (end.isEmpty()) {
            end = "--";
        }
        if (editingTaskId == -1) {
            dbHelper.insertTask(taskDate, start, end, task, "");
        } else {
            dbHelper.updateTask(editingTaskId, taskDate, start, end, task, "");
        }
        setResult(RESULT_OK);
        finish();
    }
}
