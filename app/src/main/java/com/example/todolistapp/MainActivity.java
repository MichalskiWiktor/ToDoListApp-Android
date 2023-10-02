package com.example.todolistapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button addButton, showButton, editButton, deleteButton, doneBtn;
    private ListView taskListView;
    private ArrayAdapter<String> adapter;
    private List<Task> taskList = new ArrayList<>();
    private TaskDatabaseHelper dbHelper;
    private static final int REQUEST_CODE = 123;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addButton = findViewById(R.id.addBtn);
        doneBtn = findViewById(R.id.doneBtn);
        taskListView = findViewById(R.id.taskListView);
        dbHelper = new TaskDatabaseHelper(this);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        taskListView.setAdapter(adapter);
        taskListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for(int i=0; i<taskListView.getChildCount(); i++) {
                    taskListView.getChildAt(i).setBackgroundColor(Color.WHITE);
                }
                taskListView.getChildAt(position).setBackgroundColor(Color.LTGRAY);
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedPosition = taskListView.getCheckedItemPosition();

                if (selectedPosition != AdapterView.INVALID_POSITION) {

                }
            }
        });

        loadTaskList();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                loadTaskList();
            }
        }
    }



    // Usuwanie zadania z bazy danych
    private void deleteTask(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.delete(TaskDatabaseHelper.TASK_TABLE_NAME,
                    TaskDatabaseHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(task.getId())});
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    // Wczytywanie listy zadań z bazy danych i aktualizacja interfejsu użytkownika
    @SuppressLint("Range")
    private void loadTaskList() {
        taskList.clear();
        adapter.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
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
                adapter.add(task.getTaskName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        adapter.notifyDataSetChanged();
    }
}
