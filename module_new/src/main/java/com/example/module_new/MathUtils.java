package com.example.utils;

public class MathUtils {

    // Пример: сложение двух чисел
    public static int add(int a, int b) {
        return a + b;
    }

    // Пример: вычисление индекса массы тела (ИМТ)
    public static double calculateBmi(double weightKg, double heightMeters) {
        if (heightMeters == 0) {
            return 0;
        }
        return weightKg / (heightMeters * heightMeters);
    }
}

