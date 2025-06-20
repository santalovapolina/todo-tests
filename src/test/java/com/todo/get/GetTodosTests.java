package com.todo.get;

import com.todo.BaseTest;
import com.todo.annotations.DataPreparationExtension;
import com.todo.annotations.MobileExecutionExtension;
import com.todo.annotations.PrepareTodo;
import com.todo.assertions.Assert;
import io.qameta.allure.*;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static com.todo.generators.TestDataGeneratorFaker.generateFakerTestData;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
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
    @Description("Получение пустого списка TODO, когда база данных пуста")
    public void testGetTodosWhenDatabaseIsEmpty() {

        List<Todo> readResponse = todoRequester.getValidatedRequest().readAll();
        Assert.assertEmptyBody(readResponse);

    }

    @Test
    @Description("Получение списка TODO с существующими записями")
    public void testGetTodosWithExistingEntries() {
        // Предварительно создать несколько TODO
        Todo todo1 = generateFakerTestData(Todo.class);
        Todo todo2 = generateFakerTestData(Todo.class);

        todoRequester.getRequest().create(todo1);
        todoRequester.getRequest().create(todo2);

        Response readResponse = todoRequester.getRequest().readAll();

        Todo[] todos = readResponse.getBody().as(Todo[].class);
        List<Todo> actualTodo   = Arrays.asList(todos);
        List<Todo> expectedTodo = Arrays.asList(todo1, todo2);

        assertAll("Проверка полученного TODO",
                () -> Assert.assertResponseSize(2, readResponse),
                () -> Assert.assertTodoMatches(actualTodo, expectedTodo)
        );
    }

    @PrepareTodo(5)
    @Test
    @Description("Использование параметров offset и limit для пагинации")
    public void testGetTodosWithOffsetAndLimit() {
        Response readResponse = todoRequester.getRequest().readAll(2, 2);
        Assert.assertResponseSize(2, readResponse);
    }

    @Tag("request schema")
    @Test
    @DisplayName("Передача некорректных значений в offset и limit")
    public void testGetTodosWithInvalidOffsetAndLimit() {
        // Тест с отрицательным offset
        given()
                .filter(new AllureRestAssured())
                .queryParam("offset", -1)
                .queryParam("limit", 2)
                .when()
                .get("/todos")
                .then()
                .statusCode(400)
                .contentType("text/plain")
                .body(containsString("Invalid query string"));

        // Тест с нечисловым limit
        given()
                .filter(new AllureRestAssured())
                .queryParam("offset", 0)
                .queryParam("limit", "abc")
                .when()
                .get("/todos")
                .then()
                .statusCode(400)
                .contentType("text/plain")
                .body(containsString("Invalid query string"));

        // Тест с отсутствующим значением offset
        given()
                .filter(new AllureRestAssured())
                .queryParam("offset", "")
                .queryParam("limit", 2)
                .when()
                .get("/todos")
                .then()
                .statusCode(400)
                .contentType("text/plain")
                .body(containsString("Invalid query string"));
    }


    @PrepareTodo(10)
    @Test
    @DisplayName("Проверка ответа при превышении максимально допустимого значения limit")
    public void testGetTodosWithExcessiveLimit() {

        Response readResponse = todoRequester.getRequest().readAll(0, 1000);

       // Проверяем, что вернулось 10 задач
        Assert.assertResponseSize(10, readResponse);
    }
}
