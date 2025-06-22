package com.todo.assertions;

import com.todo.models.Todo;
import io.qameta.allure.Step;
import org.hamcrest.Matcher;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class Assert {

    @Step("Проверка что TODO существует")
    public static void assertTodoExists(long id, List<Todo> actualTodo) {
        boolean isFound = actualTodo.stream()
                .map(item -> ((Number) item.getId()).longValue())
                .anyMatch(todoId -> todoId == id);

        assertTrue(isFound, "TODO c id=" + id + " не найдено");
    }

    @Step("Проверка что TODO не существует")
    public static void assertTodoNotExist(long id, List<Todo> actualTodo) {
        boolean isFound = actualTodo.stream()
                .map(item -> ((Number) item.getId()).longValue())
                .anyMatch(todoId -> todoId == id);

        assertFalse(isFound, "TODO c id=" + id + " найдено");
    }

    @Step("Проверка что тело ответа пустое")
    public static void assertEmptyBody(String response) {
        assertThat(response, isEmptyOrNullString());
    }


    @Step("Проверка размерности списка TODO")
    public static void assertTodosSize(int expectedSize, List<Todo> actualTodo) {
        assertEquals(expectedSize, actualTodo.size());
    }

    @Step("Сравнение TODO по каждому атрибуту")
    public static void assertTodoMatches(List<Todo> actualTodo, List<Todo> expectedTodo) {
        Matcher[] matchers = expectedTodo.stream()
                .map(todo -> allOf(
                        hasProperty("id", equalTo(todo.getId())),
                        hasProperty("text", equalTo(todo.getText())),
                        hasProperty("completed", equalTo(todo.isCompleted()))
                ))
                .toArray(Matcher[]::new);

        assertThat(actualTodo, contains(matchers));
    }
}
