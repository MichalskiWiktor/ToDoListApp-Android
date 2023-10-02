package com.example.todolistapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import java.util.HashMap;
import java.util.List;

public class CustomAdapter extends BaseAdapter {
    private Context context;
    private List<Task> dataList;
    private TaskDatabaseHelper dbHelper;
    private HashMap<Integer, Task> taskPosition;
    private CustomAdapterListener mListener;

    private CheckBox statusCheckbox;
    private TextView taskNameTextView;
    private Button editBtn;
    private Button deleteBtn;

    public CustomAdapter(Context context, List<Task> dataList, HashMap<Integer, Task> taskPosition, CustomAdapterListener listener) {
        this.context = context;
        this.dataList = dataList;
        this.taskPosition = taskPosition;
        this.mListener = listener;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Task getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        dbHelper = new TaskDatabaseHelper(context);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.activity_list_item, parent, false);

            this.statusCheckbox = convertView.findViewById(R.id.statusCheckbox);
            this.taskNameTextView = convertView.findViewById(R.id.taskNameTextView);
            this.editBtn = convertView.findViewById(R.id.editBtn);
            this.deleteBtn = convertView.findViewById(R.id.deleteBtn);

        } else {
            System.out.println("Error");
        }

        Task item = dataList.get(position);
        if(item.getTaskStatus() == 0)
            this.statusCheckbox.setChecked(false);
        else
            this.statusCheckbox.setChecked(true);
        this.taskNameTextView.setText(item.getTaskName());



        /* On click functions */
        this.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTask(taskPosition.get(position));
                mListener.onItemClicked(taskPosition);
            }
        });

        return convertView;
    }
    private void deleteTask(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.delete(TaskDatabaseHelper.TASK_TABLE_NAME,
                    TaskDatabaseHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(task.getId())});
        } finally {
            db.close();
        }
    }

    static class ViewHolder {
        CheckBox checkbox;
        TextView textView;
        Button button1;
        Button button2;
    }
}


