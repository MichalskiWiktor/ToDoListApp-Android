package com.example.todolistapp;

import java.util.HashMap;

public interface CustomAdapterListener {
    void onItemClicked(HashMap<Integer, Task> taskPosition);
    void showBtnClicked(Task task);
}

