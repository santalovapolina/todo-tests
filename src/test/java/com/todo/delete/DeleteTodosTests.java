package com.todo.delete;

import com.todo.BaseTest;

import com.todo.assertions.Assert;
import com.todo.requests.TodoRequester;
import com.todo.specs.request.RequestSpec;
import com.todo.specs.response.IncorrectDataResponse;
import io.qameta.allure.Description;
import org.junit.jupiter.api.Test;

import static com.todo.generators.TestDataGeneratorFaker.generateFakerTestData;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.todo.models.Todo;

import java.util.List;
import java.util.Random;

public class DeleteTodosTests extends BaseTest {

    @Test
    @Description("Успешное удаление существующего TODO с корректной авторизацией")
    public void testDeleteExistingTodoWithValidAuth() {
        Todo todo = generateFakerTestData(Todo.class);
        todoRequester.getRequest().create(todo);

        String deleteResponse = todoRequester.getValidatedRequest().delete(todo.getId());

        List<Todo> readResponse = todoRequester.getValidatedRequest().readAll();

        assertAll("Проверки после удаления TODO",
                () -> Assert.assertEmptyBody(deleteResponse),
                () -> Assert.assertTodoNotExist(todo.getId(), readResponse)
        );
    }

    @Test
    @Description("Ошибка удаления TODO без заголовка Authorization")
    public void testDeleteTodoWithoutAuthHeader() {
        Todo todo = generateFakerTestData(Todo.class);
        todoRequester.getRequest().create(todo);

        todoRequester = new TodoRequester(RequestSpec.unauthSpec());
        todoRequester.getRequest().delete(todo.getId()).then().spec(IncorrectDataResponse.STATUS_401);

        List<Todo> readResponse = todoRequester.getValidatedRequest().readAll();
        Assert.assertTodoExists(todo.getId(), readResponse);
    }

    @Test
    @Description("Ошибка удаления TODO с некорректными учетными данными")
    public void testDeleteTodoWithInvalidAuth() {
        Todo todo = generateFakerTestData(Todo.class);
        todoRequester.getRequest().create(todo);

        todoRequester = new TodoRequester(RequestSpec.incorrectAuthSpec());
        todoRequester.getRequest().delete(todo.getId()).then().spec(IncorrectDataResponse.STATUS_401);

        List<Todo> readResponse = todoRequester.getValidatedRequest().readAll();
        Assert.assertTodoExists(todo.getId(), readResponse);
    }

    @Test
    @Description("Удаление TODO с несуществующим id")
    public void testDeleteNonExistentTodo() {
        var nonExistingId = new Random().nextInt();
        todoRequester.getRequest().delete(nonExistingId).then().spec(IncorrectDataResponse.STATUS_404);

        List<Todo> readResponse = todoRequester.getValidatedRequest().readAll();
        Assert.assertTodosSize(0, readResponse);
    }

}
