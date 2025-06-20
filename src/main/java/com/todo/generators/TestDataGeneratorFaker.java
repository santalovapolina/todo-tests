package com.todo.generators;

import com.github.javafaker.Faker;
import io.qameta.allure.Step;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Random;

public class TestDataGeneratorFaker {

    private static final Faker latinFaker = new Faker();
    private static final Faker cyrillicFaker = new Faker(new Locale("ru"));
    private static final Random RANDOM = new Random();

    @Step("Generate faker-based data for {clazz}")
    public static <T> T generateFakerTestData(Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                Class<?> type = field.getType();

                if (type == long.class || type == Long.class) {
                    field.set(instance, latinFaker.number().randomNumber(3, true));
                } else if (type == int.class || type == Integer.class) {
                    field.set(instance, latinFaker.number().numberBetween(0, 10000));
                } else if (type == String.class) {
                    field.set(instance, generateTextMixed());
                } else if (type == boolean.class || type == Boolean.class) {
                    field.set(instance, latinFaker.bool().bool());
                }
            }

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate faker-based test data for class: " + clazz.getName(), e);
        }
    }

    private static String generateTextMixed() {
        return RANDOM.nextBoolean()
                ? cyrillicFaker.lorem().sentence(3)
                : latinFaker.lorem().sentence(3);
    }
}
