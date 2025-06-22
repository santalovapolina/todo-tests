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
    @Description("Успешное обновление существующего TODO")
    public void testUpdateExistingTodoWithValidData() {
        Todo originalTodo = new TodoBuilder()
                .setId(9).setText("New todo").setCompleted(false).build();
        todoRequester.getRequest().create(originalTodo);

        Todo updatedTodo = new TodoBuilder()
                .setId(originalTodo.getId()).setText("Updated todo").setCompleted(true).build();
        todoRequester.getValidatedRequest().update(originalTodo.getId(), updatedTodo);

        List<Todo> readResponse = todoRequester.getValidatedRequest().readAll();

        List<Todo> expectedTodo = Arrays.asList(updatedTodo);

        assertAll("Проверка обновлённого TODO",
                () -> Assert.assertTodosSize(1, readResponse),
                () -> Assert.assertTodoMatches(readResponse, expectedTodo),
                () -> Assert.assertTodoExists(originalTodo.getId(), readResponse)
        );
    }

    @Test
    @Description("Ошибка при обновлении TODO с несуществующим id")
    public void testUpdateNonExistentTodo() {
        Todo updateTodo = generateFakerTestData(Todo.class);
        todoRequester.getRequest().update(updateTodo.getId(), updateTodo)
                .then().spec(IncorrectDataResponse.STATUS_404);

        List<Todo> readResponse = todoRequester.getValidatedRequest().readAll();
        Assert.assertTodosSize(0, readResponse);
    }

    @Test
    @Description("Успешное обновление TODO без изменения данных (передача тех же значений)")
    public void testUpdateTodoWithoutChangingData() {
        Todo originalTodo = generateFakerTestData(Todo.class);
        todoRequester.getRequest().create(originalTodo);

        todoRequester.getValidatedRequest().update(originalTodo.getId(), originalTodo);

        List<Todo> readResponse = todoRequester.getValidatedRequest().readAll();

        List<Todo> expectedTodo = Arrays.asList(originalTodo);

        assertAll("Проверка обновлённого TODO",
                () -> Assert.assertTodosSize(1, readResponse),
                () -> Assert.assertTodoMatches(readResponse, expectedTodo)
        );
    }
}
