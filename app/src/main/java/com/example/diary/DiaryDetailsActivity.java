package com.example.diary;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
public class DiaryDetailsActivity extends AppCompatActivity {
    private TextView textDate, textWeekday;
    private EditText editDiary, editTagInput;
    private Button btnAddTag, btnSave;
    private ChipGroup chipGroupTags;
    private DiaryDatabaseHelper dbHelper;
    private String selectedDate;
    private ArrayList<String> tagList = new ArrayList<>();
    private boolean textChanged = false;
    private boolean tagsChanged = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_details);
        dbHelper = new DiaryDatabaseHelper(this);
        selectedDate = getIntent().getStringExtra("selectedDate");
        if (selectedDate == null || selectedDate.isEmpty()) {
            selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Calendar.getInstance().getTime());
        }
        textDate = findViewById(R.id.textDate);
        textWeekday = findViewById(R.id.textWeekday);
        editDiary = findViewById(R.id.editDiary);
        editTagInput = findViewById(R.id.editTagInput);
        btnAddTag = findViewById(R.id.btnAddTag);
        btnSave = findViewById(R.id.btnSave);
        chipGroupTags = findViewById(R.id.chipGroupTags);
        textDate.setText("Diary for " + selectedDate);
        Calendar cal = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            cal.setTime(sdf.parse(selectedDate));
            textWeekday.setText(new SimpleDateFormat("EEEE", Locale.getDefault()).format(cal.getTime()));
        } catch (Exception e) {
            textWeekday.setText("");
        }
        loadDiaryData();
        btnAddTag.setOnClickListener(v -> {
            String newTag = editTagInput.getText().toString().trim();
            if (!newTag.isEmpty() && !tagList.contains(newTag)) {
                tagList.add(newTag);
                updateTagChips();
                tagsChanged = true; // 🧠 Auto-save trigger
                autoSaveDiary();
                editTagInput.setText("");
            }
        });
        btnSave.setOnClickListener(v -> saveDiary());
        editDiary.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                textChanged = true;
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }
    private void loadDiaryData() {
        String content = dbHelper.getDiaryContent(selectedDate);
        if (content != null) editDiary.setText(content);
        String tags = dbHelper.getTagsByDate(selectedDate);
        if (tags != null && !tags.isEmpty()) {
            tagList.clear();
            tagList.addAll(Arrays.asList(tags.split(",")));
            updateTagChips();
        }
    }
    private void updateTagChips() {
        chipGroupTags.removeAllViews();
        for (String tag : tagList) {
            Chip chip = new Chip(this);
            chip.setText(tag);
            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(v -> {
                tagList.remove(tag);
                updateTagChips();
                tagsChanged = true;
                autoSaveDiary();
            });
            chipGroupTags.addView(chip);
        }
    }
    private void saveDiary() {
        String content = editDiary.getText().toString().trim();
        String tags = String.join(",", tagList);
        dbHelper.saveDiaryContent(selectedDate, content);
        dbHelper.saveTagsForDate(selectedDate, tags);
        Toast.makeText(this, "Diary saved successfully!", Toast.LENGTH_SHORT).show();
    }
    private void autoSaveDiary() {
        if (textChanged || tagsChanged) {
            String content = editDiary.getText().toString().trim();
            String tags = String.join(",", tagList);
            dbHelper.saveDiaryContent(selectedDate, content);
            dbHelper.saveTagsForDate(selectedDate, tags);
            textChanged = false;
            tagsChanged = false;
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        autoSaveDiary();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}
