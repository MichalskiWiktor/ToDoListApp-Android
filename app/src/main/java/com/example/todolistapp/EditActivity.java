package com.example.todolistapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity {
    private EditText textName;
    private EditText textDescription;
    private Button editBtn;
    private CheckBox taskStatus;
    private TaskDatabaseHelper dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        this.textName = findViewById(R.id.taskNameEdit);
        this.textDescription = findViewById(R.id.taskDescEdit);
        this.editBtn = findViewById(R.id.editTaskBtn);
        this.taskStatus = findViewById(R.id.taskStatusEdit);
        this.dbHelper = new TaskDatabaseHelper(this);

        List<Task> tasks = getTaskList();

        Intent intent = getIntent();
        int taskId = Integer.parseInt(intent.getStringExtra("task"));
        for(Task task: tasks){
            if((int)task.getId() == taskId){
                this.textName.setText(task.getTaskName());
                this.textDescription.setText(task.getTaskDescription());
                if(task.getTaskStatus() == 0){
                    this.taskStatus.setChecked(false);
                }else{
                    this.taskStatus.setChecked(true);
                }
                /* Submit Button */
                this.editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        try {

                            ContentValues values = new ContentValues();
                            values.put(TaskDatabaseHelper.COLUMN_TASK, String.valueOf(textName.getText()));
                            values.put(TaskDatabaseHelper.COLUMN_DES, String.valueOf(textDescription.getText()));
                            if(taskStatus.isChecked())
                                values.put(TaskDatabaseHelper.COLUMN_STATUS, 1);
                            else
                                values.put(TaskDatabaseHelper.COLUMN_STATUS, 0);

                            db.update(TaskDatabaseHelper.TASK_TABLE_NAME, values,TaskDatabaseHelper.COLUMN_ID + " = ?",
                                    new String[]{String.valueOf(task.getId())});
                        } finally {
                            db.close();
                        }
                        Intent resultIntent = new Intent();
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                });
            }
        }
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