package com.example.diary;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private final Context context;
    private final ArrayList<TaskModel> list;
    public TaskAdapter(Context context, ArrayList<TaskModel> list) {
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskModel task = list.get(position);
        String start = task.getStartTime();
        String end = task.getEndTime();
        if (end == null || end.trim().isEmpty() || end.equals("--")) {
            holder.txtTime.setText(start);
            holder.txtDuration.setVisibility(View.GONE);
        } else {
            holder.txtTime.setText(start + " - " + end);
            holder.txtDuration.setVisibility(View.VISIBLE);
            holder.txtDuration.setText(getDuration(start, end));
        }
        holder.txtTask.setText(task.getTask());
        holder.btnArrow.setOnClickListener(v -> openTaskDetails(task));
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddTaskActivity.class);
            intent.putExtra("taskId", task.getId());
            intent.putExtra("startTime", start);
            intent.putExtra("endTime", end);
            intent.putExtra("taskDesc", task.getTask());
            intent.putExtra("taskDetails", task.getNote());
            context.startActivity(intent);
        });
        holder.btnDelete.setOnClickListener(v -> {
            DiaryDatabaseHelper dbHelper = new DiaryDatabaseHelper(context);
            dbHelper.deleteTask(task.getId());
            list.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, list.size());
        });
        holder.itemView.setOnClickListener(v -> openTaskDetails(task));
        holder.itemView.setPadding(20, 10, 10, 10);
        holder.txtTask.setTextColor(context.getResources().getColor(android.R.color.white));
        holder.txtTime.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        holder.txtTask.setTextSize(16);
        holder.txtTime.setTextSize(15);
        holder.itemView.setBackgroundResource(R.drawable.item_background);
    }
    private void openTaskDetails(TaskModel task) {
        Intent intent = new Intent(context, TaskDetailsActivity.class);
        intent.putExtra("taskId", task.getId());
        intent.putExtra("task", task.getTask());
        intent.putExtra("startTime", task.getStartTime());
        intent.putExtra("endTime", task.getEndTime());
        intent.putExtra("details", task.getNote());
        context.startActivity(intent);
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView txtTime, txtTask, txtDuration;
        ImageButton btnEdit, btnDelete, btnArrow;
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtTask = itemView.findViewById(R.id.txtTask);
            txtDuration = itemView.findViewById(R.id.txtDuration);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnArrow = itemView.findViewById(R.id.btnArrow);
        }
    }
    private String getDuration(String start, String end) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Date startDate = sdf.parse(start);
            Date endDate = sdf.parse(end);
            if (startDate == null || endDate == null)
                return "";
            long diff = endDate.getTime() - startDate.getTime();
            if (diff < 0) diff += 24 * 60 * 60 * 1000;
            long hours = diff / (1000 * 60 * 60);
            long minutes = (diff / (1000 * 60)) % 60;
            if (hours == 0 && minutes == 0)
                return "0 min";
            else if (hours == 0)
                return minutes + " min";
            else if (minutes == 0)
                return hours + " hr";
            else
                return hours + " hr " + minutes + " min";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
