package com.example.todolistapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CustomAdapterListener{

    private Button addButton;
    private ListView taskListView;
    private CustomAdapter customAdapter;
    private HashMap<Integer, Task> taskPosition = new HashMap<Integer, Task>();
    private List<Task> taskList = new ArrayList<>();
    private TaskDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addButton = findViewById(R.id.addBtn);
        taskListView = findViewById(R.id.taskListView);
        setUpList();

        /* New Task */
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                someActivityResultLauncher.launch(intent);
            }
        });
    }
    public void setUpList(){
        loadTaskList();
        for(int i=0;i<taskList.size(); i++){
            taskPosition.put(i, taskList.get(i));
        }
        customAdapter = new CustomAdapter(this, taskList, taskPosition, this);
        taskListView.setAdapter(customAdapter);
        loadTaskList();
        customAdapter.notifyDataSetChanged();
    }

    @SuppressLint("Range")
    private void loadTaskList() {
        taskList.clear();
        dbHelper = new TaskDatabaseHelper(this);
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    @Override
    public void onItemClicked(HashMap<Integer, Task> taskPosition) {
        setUpList();
    }
    @Override
    public void showBtnClicked(Task task) {
        Intent intent = new Intent(MainActivity.this, ShowActivity.class);
        intent.putExtra("task", String.valueOf(task.getId()));
        startActivity(intent);
    }
    @Override
    public void editBtnClicked(Task task) {
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        intent.putExtra("task", String.valueOf(task.getId()));
        someActivityResultLauncher.launch(intent);
    }
    @Override
    public void reLoadList(){
        setUpList();
    }
    private ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    setUpList();                }
            });
}
