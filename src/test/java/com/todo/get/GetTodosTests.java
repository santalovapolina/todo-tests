package com.todo.get;

import com.todo.BaseTest;
import com.todo.annotations.DataPreparationExtension;
import com.todo.annotations.MobileExecutionExtension;
import com.todo.annotations.PrepareTodo;
import com.todo.assertions.Assert;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;

import static com.todo.generators.TestDataGeneratorFaker.generateFakerTestData;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.todo.models.Todo;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.List;

@Epic("TODO Management")
@Feature("Get Todos API")
@ExtendWith(DataPreparationExtension.class)
@ExtendWith(MobileExecutionExtension.class)
public class GetTodosTests extends BaseTest {


    @Test
    @Description("Успешное получение списка TODO с существующими записями")
    public void testGetTodosWithExistingEntries() {
        Todo todo1 = generateFakerTestData(Todo.class);
        Todo todo2 = generateFakerTestData(Todo.class);

        todoRequester.getRequest().create(todo1);
        todoRequester.getRequest().create(todo2);

        List<Todo> readResponse = todoRequester.getValidatedRequest().readAll();

        List<Todo> expectedTodo = Arrays.asList(todo1, todo2);

        assertAll("Проверка полученного TODO",
                () -> Assert.assertTodosSize(2, readResponse),
                () -> Assert.assertTodoMatches(readResponse, expectedTodo)
        );
    }

    @PrepareTodo(5)
    @Test
    @Description("Проверка параметров offset и limit для пагинации")
    public void testGetTodosWithOffsetAndLimit() {
        List<Todo> readResponse = todoRequester.getValidatedRequest().readAll(2, 2);
        Assert.assertTodosSize(2, readResponse);
    }


    @PrepareTodo(10)
    @Test
    @Description("Проверка ответа при превышении максимально допустимого значения limit")
    public void testGetTodosWithExcessiveLimit() {
        List<Todo> readResponse = todoRequester.getValidatedRequest().readAll(0, 1000);
        Assert.assertTodosSize(10, readResponse);
    }
}
