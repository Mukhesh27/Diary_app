package com.example.diary;
public class TaskModel {
    private int id;
    private String date;
    private String startTime;
    private String endTime;
    private String task;
    private String note;
    private String tags;
    public TaskModel() { }
    public TaskModel(int id, String date, String startTime, String endTime, String task, String note) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.task = task;
        this.note = note;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public String getTask() { return task; }
    public void setTask(String task) { this.task = task; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public void setTags(String tags) { this.tags = tags; }
}
