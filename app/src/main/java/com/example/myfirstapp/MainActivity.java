package com.example.myfirstapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final int[] colors = {
            Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW,
            Color.CYAN, Color.MAGENTA, Color.parseColor("#FF9800"),
            Color.parseColor("#9C27B0"), Color.parseColor("#009688")
    };

    private DatabaseHelper dbHelper;
    private ListView listViewTasks;
    private ArrayAdapter<String> adapter;
    private List<Task> tasks = new ArrayList<>();
    private int selectedTaskId = -1;
    private TextView tvStats;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);
        listViewTasks = findViewById(R.id.listViewTasks);
        tvStats = findViewById(R.id.tvStats);

        Button buttonStart = findViewById(R.id.button_start);
        Button buttonStop = findViewById(R.id.button_stop);
        Button changeColorBtn = findViewById(R.id.btn_change_color);
        SwitchCompat themeSwitch = findViewById(R.id.themeSwitch);

        LinearLayout mainLayout = findViewById(R.id.main_layout);
        LinearLayout rootLayout = mainLayout;
        if (rootLayout == null) {
            View rootView = findViewById(android.R.id.content).getRootView();
            if (rootView instanceof LinearLayout) {
                rootLayout = (LinearLayout) rootView;
            }
        }
        final LinearLayout finalMainLayout = rootLayout;

        ListView lvScreens = findViewById(R.id.lvScreens);
        EditText etTitle = findViewById(R.id.etTitle);
        EditText etDesc = findViewById(R.id.etDesc);
        EditText etSearch = findViewById(R.id.etSearch);

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);

        if (isDarkMode) {
            themeSwitch.setChecked(true);
        }

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putBoolean("dark_mode", true);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putBoolean("dark_mode", false);
            }
            editor.apply();
        });

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),
                        "Приветствие! Добро пожаловать в приложение",
                        Toast.LENGTH_SHORT).show();
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonStop.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),
                        "Кнопка 'Остановить' скрыта",
                        Toast.LENGTH_SHORT).show();
            }
        });

        changeColorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finalMainLayout != null) {
                    Random random = new Random();
                    int colorIndex = random.nextInt(colors.length);
                    int selectedColor = colors[colorIndex];
                    finalMainLayout.setBackgroundColor(selectedColor);
                    String colorName = getColorName(selectedColor);
                    Toast.makeText(getApplicationContext(),
                            "Цвет изменён на: " + colorName,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Не удалось изменить цвет",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        String[] screens = {
                "Открыть профиль",
                "Открыть экран с расчётом",
                "Открыть экран настроек"
        };

        ArrayAdapter<String> screensAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                screens
        );

        lvScreens.setAdapter(screensAdapter);

        lvScreens.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = null;
                if (position == 0) {
                    intent = new Intent(MainActivity.this, ProfileActivity.class);
                } else if (position == 1) {
                    intent = new Intent(MainActivity.this, CalcActivity.class);
                } else if (position == 2) {
                    intent = new Intent(MainActivity.this, SettingsActivity.class);
                }
                if (intent != null) {
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });

        findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etTitle.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Введите название задачи", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (dbHelper.addTask(etTitle.getText().toString(), etDesc.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Задача добавлена!", Toast.LENGTH_SHORT).show();
                    refreshList(null);
                    etTitle.setText("");
                    etDesc.setText("");
                }
            }
        });

        findViewById(R.id.btnEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTaskId == -1) {
                    Toast.makeText(MainActivity.this, "Сначала выберите задачу из списка", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etTitle.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Введите название задачи", Toast.LENGTH_SHORT).show();
                    return;
                }
                Task task = new Task();
                task.setId(selectedTaskId);
                task.setTitle(etTitle.getText().toString());
                task.setDescription(etDesc.getText().toString());
                if (dbHelper.updateTask(task)) {
                    Toast.makeText(MainActivity.this, "Задача обновлена!", Toast.LENGTH_SHORT).show();
                    refreshList(null);
                    etTitle.setText("");
                    etDesc.setText("");
                    selectedTaskId = -1;
                }
            }
        });

        findViewById(R.id.btnRefresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshList(null);
                etSearch.setText("");
            }
        });

        findViewById(R.id.btnSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = etSearch.getText().toString().trim();
                if (!query.isEmpty()) {
                    try {
                        tasks.clear();
                        tasks.addAll(dbHelper.searchTasks(query));
                        refreshList(query);
                        Toast.makeText(MainActivity.this, "🔍 Найдено: " + tasks.size() + " задач", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "❌ Ошибка поиска: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    refreshList(null);
                }
            }
        });

        findViewById(R.id.btnClearSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setText("");
                refreshList(null);
            }
        });

        listViewTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task selectedTask = tasks.get(position);
                selectedTaskId = selectedTask.getId();
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("TASK_ID", selectedTaskId);
                startActivityForResult(intent, 1);
            }
        });

        findViewById(R.id.btnDeleteSelected).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTaskId == -1) {
                    Toast.makeText(MainActivity.this, "Сначала выберите задачу из списка", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (dbHelper.deleteTask(selectedTaskId)) {
                    Toast.makeText(MainActivity.this, "Задача удалена!", Toast.LENGTH_SHORT).show();
                    refreshList(null);
                    etTitle.setText("");
                    etDesc.setText("");
                    selectedTaskId = -1;
                }
            }
        });

        refreshList(null);
    }

    private String getColorName(int color) {
        if (color == Color.RED) return "Красный";
        if (color == Color.GREEN) return "Зелёный";
        if (color == Color.BLUE) return "Синий";
        if (color == Color.YELLOW) return "Жёлтый";
        if (color == Color.CYAN) return "Голубой";
        if (color == Color.MAGENTA) return "Пурпурный";
        if (color == Color.parseColor("#FF9800")) return "Оранжевый";
        if (color == Color.parseColor("#9C27B0")) return "Фиолетовый";
        if (color == Color.parseColor("#009688")) return "Бирюзовый";
        return "Неизвестный";
    }

    private void animateFadeIn(View view) {
        android.view.animation.Animation fadeIn =
                android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_in);
        view.startAnimation(fadeIn);
    }

    private void refreshList(String searchQuery) {
        tasks.clear();
        tasks.addAll(dbHelper.getTasksSortedByTitle(searchQuery));
        List<String> taskTitles = new ArrayList<>();
        for (Task task : tasks) {
            taskTitles.add(task.getTitle() + " (" + task.getDescription() + ")");
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, taskTitles);
        listViewTasks.setAdapter(adapter);

        int totalTasks = dbHelper.getTasksCount();
        int todayTasks = dbHelper.getTodayTasksCount();
        tvStats.setText("Всего задач: " + totalTasks + " | Сегодня добавлено: " + todayTasks);

        animateFadeIn(listViewTasks);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            refreshList(null);
        }
    }
}