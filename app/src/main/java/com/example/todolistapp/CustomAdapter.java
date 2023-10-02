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
import java.util.Map;

public class CustomAdapter extends BaseAdapter {
    private Context context;
    private List<Task> dataList;
    private TaskDatabaseHelper dbHelper;
    private HashMap<Integer, Task> taskPosition;
    private CustomAdapterListener mListener;

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
        ViewHolder holder;
        dbHelper = new TaskDatabaseHelper(context);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.activity_list_item, parent, false);

            holder = new ViewHolder();
            holder.checkbox = convertView.findViewById(R.id.checkbox);
            holder.textView = convertView.findViewById(R.id.textView);
            holder.button1 = convertView.findViewById(R.id.button1);
            holder.button2 = convertView.findViewById(R.id.button2);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Task item = dataList.get(position);

        if(item.getTaskStatus() == 0)
            holder.checkbox.setChecked(false);
        else
            holder.checkbox.setChecked(true);
        holder.textView.setText(item.getTaskName());

        holder.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTask(taskPosition.get(position));
                taskPosition.remove(position);
                mListener.onItemClicked(taskPosition);
                ///reload listy
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
            int positionToRemove = -1;
            for (Map.Entry<Integer, Task> entry : taskPosition.entrySet()) {
                if (entry.getValue().getId() == task.getId()) {
                    positionToRemove = entry.getKey();
                    break;
                }
            }
            if (positionToRemove != -1) {
                taskPosition.remove(positionToRemove);
            }
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


