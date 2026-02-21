package com.example.myfirstapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DetailsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText etTitle, etDesc;
    private Button btnSave, btnDelete;
    private int taskId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        dbHelper = new DatabaseHelper(this);
        etTitle = findViewById(R.id.etDetailsTitle);
        etDesc = findViewById(R.id.etDetailsDesc);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        taskId = getIntent().getIntExtra("TASK_ID", -1);

        if (taskId != -1) {
            loadTask();
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTask();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTask();
            }
        });
    }

    private void loadTask() {
        Task task = dbHelper.getTask(taskId);
        if (task != null) {
            etTitle.setText(task.getTitle());
            etDesc.setText(task.getDescription());
        }
    }

    private void saveTask() {
        String title = etTitle.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Введите название", Toast.LENGTH_SHORT).show();
            return;
        }

        if (taskId == -1) {
            if (dbHelper.addTask(title, desc)) {
                Toast.makeText(this, "Задача добавлена", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Task task = new Task();
            task.setId(taskId);
            task.setTitle(title);
            task.setDescription(desc);
            if (dbHelper.updateTask(task)) {
                Toast.makeText(this, "Задача обновлена", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void deleteTask() {
        if (taskId != -1) {
            if (dbHelper.deleteTask(taskId)) {
                Toast.makeText(this, "Задача удалена", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}