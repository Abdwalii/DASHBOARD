package com.example.dashboard;

public class Category {
    private String name;
    private int iconResId;
    private int taskCount;

    public Category(String name, int iconResId, int taskCount) {
        this.name = name;
        this.iconResId = iconResId;
        this.taskCount = taskCount;
    }

    public String getName() {
        return name;
    }

    public int getIconResId() {
        return iconResId;
    }

    public int getTaskCount() {
        return taskCount;
    }
}