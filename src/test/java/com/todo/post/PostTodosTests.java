package com.todo.post;

import com.todo.BaseTest;
import com.todo.assertions.Assert;
import com.todo.models.Todo;
import com.todo.models.TodoBuilder;
import com.todo.specs.response.IncorrectDataResponse;
import io.qameta.allure.Description;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.todo.generators.TestDataGeneratorFaker.generateFakerTestData;
import static org.junit.jupiter.api.Assertions.assertAll;

public class PostTodosTests extends BaseTest {

    @Test
    @Description("Авторизованный пользователь может создавать TODO")
    public void testCreateTodoWithValidData() {
        Todo todo = generateFakerTestData(Todo.class);
        todoRequester.getValidatedRequest().create(todo);

        List<Todo> actualTodo = todoRequester.getValidatedRequest().readAll();
        Assert.assertTodoExists(todo.getId(), actualTodo);
    }

    @Test
    @Description("Максимально допустимая длина поля 'text' составляет 255")
    public void testCreateTodoWithMaxLengthText() {
        String maxLengthText = "A".repeat(255);
        Todo todo = new TodoBuilder().setText(maxLengthText).build();
        todoRequester.getValidatedRequest().create(todo);

        List<Todo> actualTodo = todoRequester.getValidatedRequest().readAll();
        List<Todo> expectedTodo = Arrays.asList(todo);

        assertAll("Проверка созданного TODO",
                () -> Assert.assertTodoMatches(actualTodo, expectedTodo),
                () -> Assert.assertTodoExists(todo.getId(), actualTodo)
        );
    }

    @Test
    @Description("Пользователь не может создать TODO c существующим значением поля 'id'")
    public void testCreateTodoWithExistingId() {
        Todo firstTodo = generateFakerTestData(Todo.class);
        todoRequester.getRequest().create(firstTodo);

        Todo duplicateTodo = generateFakerTestData(Todo.class);
        duplicateTodo.setId(firstTodo.getId());

        todoRequester.getRequest()
                .create(duplicateTodo)
                .then()
                .spec(IncorrectDataResponse.STATUS_400);

        List<Todo> actualTodo = todoRequester.getValidatedRequest().readAll();
        Assert.assertTodosSize(1, actualTodo);
    }
}
