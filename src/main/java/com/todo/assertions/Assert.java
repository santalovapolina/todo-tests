package com.todo.assertions;

import com.todo.models.Todo;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.hamcrest.Matcher;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class Assert {

    @Step("Проверка что TODO существует")
    public static void assertTodoExists(long id, Response response) {
        List<Map<String, Object>> todos = response.jsonPath().getList("$");

        boolean isFound = todos.stream()
                .map(item -> ((Number) item.get("id")).longValue())
                .anyMatch(todoId -> todoId == id);

        assertTrue(isFound, "TODO c id=" + id + " не найдено");
    }

    @Step("Проверка что TODO не существует")
    public static void assertTodoNotExist(long id, Response response) {
        List<Map<String, Object>> todos = response.jsonPath().getList("$");

        boolean isFound = todos.stream()
                .map(item -> ((Number) item.get("id")).longValue())
                .anyMatch(todoId -> todoId == id);

        assertFalse(isFound, "TODO c id=" + id + " найдено");
    }

    @Step("Проверка что тело ответа пустое")
    public static void assertEmptyBody(String response) {
        assertThat(response, isEmptyOrNullString());
    }

    @Step("Проверка что тело ответа пустое")
    public static <T> void assertEmptyBody(Collection<T> response) {
        assertThat(response, empty());
    }

    @Step("Проверка что размерности тела ответа")
    public static void assertResponseSize(int expectedSize, Response response) {
        List<Todo> todos = response
                .jsonPath()
                .getList("$", Todo.class);

        assertEquals(expectedSize, todos.size());
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
