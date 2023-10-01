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
import android.widget.EditText;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AddActivity extends AppCompatActivity {
    private EditText textName;
    private EditText textDescription;
    private Button addBtn;
    private TaskDatabaseHelper dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        textName = findViewById(R.id.textName);
        textDescription = findViewById(R.id.textDesc);
        addBtn = findViewById(R.id.addNewTaskBtn);
        dbHelper = new TaskDatabaseHelper(this);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String taskName = textName.getText().toString().trim();
                String textDesc = textDescription.getText().toString().trim();
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                String textDate = dtf.format(now).toString();

                if (!taskName.isEmpty()) {
                    insertTask(taskName, textDesc, textDate);
                    Toast.makeText(AddActivity.this, taskName, Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AddActivity.this, "Wprowadź nazwę zadania", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void insertTask(String taskName, String textDesc, String textDate) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TaskDatabaseHelper.COLUMN_TASK, taskName);
        values.put(TaskDatabaseHelper.COLUMN_DES, textDesc);
        values.put(TaskDatabaseHelper.COLUMN_DATE, textDate);

        try {
            db.insert(TaskDatabaseHelper.TASK_TABLE_NAME, null, values);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
}