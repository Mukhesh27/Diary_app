package com.example.diary;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> {
    private final ArrayList<DiaryModel> diaries;
    private final OnDiaryClickListener listener;
    public interface OnDiaryClickListener {
        void onDiaryClick(DiaryModel diary);
    }
    public DiaryAdapter(ArrayList<DiaryModel> diaries, OnDiaryClickListener listener) {
        this.diaries = diaries;
        this.listener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_diary, parent, false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DiaryModel diary = diaries.get(position);
        holder.txtTitle.setText("Diary Entry");
        holder.txtDate.setText(diary.getDate());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DiaryDetailsActivity.class);
            intent.putExtra("date", diary.getDate());
            v.getContext().startActivity(intent);
        });
    }
    @Override
    public int getItemCount() {
        return diaries.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDate;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDate = itemView.findViewById(R.id.txtDate);
        }
    }
}
