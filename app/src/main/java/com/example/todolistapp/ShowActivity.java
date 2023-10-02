package com.example.todolistapp;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class ShowActivity extends AppCompatActivity {
    private TextView showTaskNameTextView;
    private TextView showTaskDescTextView;
    private TextView showTaskDateTextView;
    private TextView showTaskStatusTextView;
    private Button returnBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        List<Task> tasks = getTaskList();

        this.returnBtn = findViewById(R.id.returnBtn);

        Intent intent = getIntent();
        int taskId = Integer.parseInt(intent.getStringExtra("task"));
        for(Task task: tasks){
            if((int)task.getId() == taskId){
                this.showTaskNameTextView = findViewById(R.id.showTaskNameTextView);
                this.showTaskDescTextView = findViewById(R.id.showTaskDescTextView);
                this.showTaskDateTextView = findViewById(R.id.showTaskDateTextView);
                this.showTaskStatusTextView = findViewById(R.id.showTaskStatusTextView);

                this.showTaskNameTextView.setText(task.getTaskName());
                this.showTaskDescTextView.setText(task.getTaskDescription());
                this.showTaskDateTextView.setText(task.getTaskDate());
                if(task.getTaskStatus() == 0){
                    this.showTaskStatusTextView.setText("To do");
                }else{
                    this.showTaskStatusTextView.setText("Done");
                }
            }
        }

        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    @SuppressLint("Range")
    public List<Task>  getTaskList(){
        TaskDatabaseHelper dbHelper = new TaskDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        List<Task> taskList = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.query(TaskDatabaseHelper.TASK_TABLE_NAME, null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                Task task = new Task();
                task.setId(cursor.getLong(cursor.getColumnIndex(TaskDatabaseHelper.COLUMN_ID)));
                task.setTaskName(cursor.getString(cursor.getColumnIndex(TaskDatabaseHelper.COLUMN_TASK)));
                task.setTaskDescription(cursor.getString(cursor.getColumnIndex(TaskDatabaseHelper.COLUMN_DES)));
                task.setTaskDate(cursor.getString(cursor.getColumnIndex(TaskDatabaseHelper.COLUMN_DATE)));
                task.setTaskStatus(cursor.getInt(cursor.getColumnIndex(TaskDatabaseHelper.COLUMN_STATUS)));
                taskList.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return taskList;
    }
}