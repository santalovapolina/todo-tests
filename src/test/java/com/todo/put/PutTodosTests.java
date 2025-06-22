package com.todo.put;

import com.todo.BaseTest;
import com.todo.assertions.Assert;
import com.todo.models.TodoBuilder;
import com.todo.specs.response.IncorrectDataResponse;
import io.qameta.allure.Description;
import org.junit.jupiter.api.Test;

import static com.todo.generators.TestDataGeneratorFaker.generateFakerTestData;
import static org.junit.jupiter.api.Assertions.*;

import com.todo.models.Todo;

import java.util.Arrays;
import java.util.List;

public class PutTodosTests extends BaseTest {

    @Test
    @Description("Авторизованный пользователь может обновлять TODO")
    public void testUpdateExistingTodoWithValidData() {
        Todo originalTodo = new TodoBuilder()
                .setId(9).setText("New todo").setCompleted(false).build();
        todoRequester.getRequest().create(originalTodo);

        Todo updatedTodo = new TodoBuilder()
                .setId(originalTodo.getId()).setText("Updated todo").setCompleted(true).build();
        todoRequester.getValidatedRequest().update(originalTodo.getId(), updatedTodo);

        List<Todo> actualTodo = todoRequester.getValidatedRequest().readAll();

        List<Todo> expectedTodo = Arrays.asList(updatedTodo);

        assertAll("Проверка обновлённого TODO",
                () -> Assert.assertTodosSize(1, actualTodo),
                () -> Assert.assertTodoMatches(actualTodo, expectedTodo),
                () -> Assert.assertTodoExists(originalTodo.getId(), actualTodo)
        );
    }

    @Test
    @Description("Пользователь не может обновить несуществующее TODO")
    public void testUpdateNonExistentTodo() {
        Todo updateTodo = generateFakerTestData(Todo.class);
        todoRequester.getRequest().update(updateTodo.getId(), updateTodo)
                .then().spec(IncorrectDataResponse.STATUS_404);

        List<Todo> actualTodo = todoRequester.getValidatedRequest().readAll();
        Assert.assertTodosSize(0, actualTodo);
    }

    @Test
    @Description("Авторизованный пользователь может обновить TODO уже существующими значениями полей")
    public void testUpdateTodoWithoutChangingData() {
        Todo originalTodo = generateFakerTestData(Todo.class);
        todoRequester.getRequest().create(originalTodo);

        todoRequester.getValidatedRequest().update(originalTodo.getId(), originalTodo);

        List<Todo> actualTodo = todoRequester.getValidatedRequest().readAll();

        List<Todo> expectedTodo = Arrays.asList(originalTodo);

        assertAll("Проверка обновлённого TODO",
                () -> Assert.assertTodosSize(1, actualTodo),
                () -> Assert.assertTodoMatches(actualTodo, expectedTodo)
        );
    }
}
