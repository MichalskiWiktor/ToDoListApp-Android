package com.example.todolistapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
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

public class AddActivity extends AppCompatActivity {
    private EditText textName;
    private EditText textDescription;
    private Button addBtn;
    private CheckBox taskStatus;
    private TaskDatabaseHelper dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        textName = findViewById(R.id.taskName);
        textDescription = findViewById(R.id.taskDesc);
        addBtn = findViewById(R.id.addNewTaskBtn);
        taskStatus = findViewById(R.id.taskStatus);
        dbHelper = new TaskDatabaseHelper(this);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String taskName = textName.getText().toString().trim();
                String textDesc = textDescription.getText().toString().trim();
                Boolean taskStat = taskStatus.isChecked();
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                String textDate = dtf.format(now).toString();

                if (!taskName.isEmpty()) {
                    if(taskStat)
                        insertTask(taskName, textDesc, textDate, 1);
                    else
                        insertTask(taskName, textDesc, textDate, 0);
                    Toast.makeText(AddActivity.this, taskName, Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AddActivity.this, "Wprowadź nazwę zadania", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void insertTask(String taskName, String taskDesc, String taskDate, int taskStatus) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TaskDatabaseHelper.COLUMN_TASK, taskName);
        values.put(TaskDatabaseHelper.COLUMN_DES, taskDesc);
        values.put(TaskDatabaseHelper.COLUMN_DATE, taskDate);
        values.put(TaskDatabaseHelper.COLUMN_STATUS, taskStatus);

        try {
            db.insert(TaskDatabaseHelper.TASK_TABLE_NAME, null, values);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
}