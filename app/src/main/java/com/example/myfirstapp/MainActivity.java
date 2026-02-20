package com.example.myfirstapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;  // ИЗМЕНЕНО: LinearLayout вместо ConstraintLayout
import android.widget.Toast;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // Массив цветов для смены фона
    private final int[] colors = {
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.YELLOW,
            Color.CYAN,
            Color.MAGENTA,
            Color.parseColor("#FF9800"),  // Оранжевый
            Color.parseColor("#9C27B0"),  // Фиолетовый
            Color.parseColor("#009688")   // Бирюзовый
    };

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

        // === КНОПКИ ИЗ ЧАСТИ 1 ===
        Button buttonStart = findViewById(R.id.button_start);
        Button buttonStop = findViewById(R.id.button_stop);

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

        // === НОВАЯ КНОПКА ДЛЯ СМЕНЫ ЦВЕТА (ЧАСТЬ 2) ===
        Button changeColorBtn = findViewById(R.id.btn_change_color);
        LinearLayout mainLayout = findViewById(R.id.main_layout);  // Корневой контейнер

        changeColorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Случайный выбор цвета из массива
                Random random = new Random();
                int colorIndex = random.nextInt(colors.length);
                int selectedColor = colors[colorIndex];

                // Меняем цвет фона
                mainLayout.setBackgroundColor(selectedColor);

                // Показываем какой цвет выпал (для наглядности)
                String colorName = getColorName(selectedColor);
                Toast.makeText(getApplicationContext(),
                        "Цвет изменён на: " + colorName,
                        Toast.LENGTH_SHORT).show();
            }
        });

        // === СУЩЕСТВУЮЩИЙ КОД ДЛЯ LISTVIEW ===
        ListView lvScreens = findViewById(R.id.lvScreens);

        String[] screens = {
                "Открыть профиль",
                "Открыть экран с расчётом",
                "Открыть экран настроек"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                screens
        );

        lvScreens.setAdapter(adapter);

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
    }

    // Вспомогательный метод для получения названия цвета
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
}