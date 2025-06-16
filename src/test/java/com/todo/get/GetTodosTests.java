package com.todo.get;

import com.todo.BaseTest;
import com.todo.annotations.DataPreparationExtension;
import com.todo.annotations.DeterminedEnvExtension;

import com.todo.requests.ValidatedTodoRequest;
import com.todo.specs.request.RequestSpec;
import io.qameta.allure.*;
import io.qameta.allure.restassured.AllureRestAssured;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;

import com.todo.models.Todo;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

@Epic("TODO Management")
@Feature("Search Todos API")
@ExtendWith(DeterminedEnvExtension.class)
@ExtendWith(DataPreparationExtension.class)
public class GetTodosTests extends BaseTest {

    @Tag("functional")
    @Test
    @Description("Получение пустого списка TODO, когда база данных пуста")
    public void testGetTodosWhenDatabaseIsEmpty() {
        ValidatedTodoRequest unauthRequest = new ValidatedTodoRequest((RequestSpec.unauthSpec()));
        List<Todo> todos = unauthRequest.readAll();
        assertTrue(todos.isEmpty());

    }

    @Tag("functional")
    @Test
    @Description("Получение списка TODO с существующими записями")
    public void testGetTodosWithExistingEntries() {
        // Предварительно создать несколько TODO
        Todo todo1 = new Todo(1, "Task 1", false);
        Todo todo2 = new Todo(2, "Task 2", true);
        ValidatedTodoRequest unauthRequest = new ValidatedTodoRequest((RequestSpec.unauthSpec()));

        unauthRequest.create(todo1);
        unauthRequest.create(todo2);
        List<Todo> todos = unauthRequest.readAll();

        // Дополнительная проверка содержимого
        assertEquals(2, todos.size());

        assertEquals(1, todos.get(0).getId());
        assertEquals("Task 1", todos.get(0).getText());
        assertFalse(todos.get(0).isCompleted());

        assertEquals(2, todos.get(1).getId());
        assertEquals("Task 2", todos.get(1).getText());
        assertTrue(todos.get(1).isCompleted());
    }

    @Tag("functional")
    @Test
    @Description("Использование параметров offset и limit для пагинации")
    public void testGetTodosWithOffsetAndLimit() {

        ValidatedTodoRequest unauthRequest = new ValidatedTodoRequest(RequestSpec.unauthSpec());
        // Создаем 5 TODO
        for (int i = 1; i <= 5; i++) {
            unauthRequest.create(new Todo(i, "Task " + i, i % 2 == 0));
        }

        List<Todo> todos = unauthRequest.readAll(2, 2);

        assertEquals(2, todos.size());

        // Проверяем, что получили задачи с id 3 и 4
        assertEquals(3, todos.get(0).getId());
        assertEquals("Task 3", todos.get(0).getText());

        assertEquals(4, todos.get(1).getId());
        assertEquals("Task 4", todos.get(1).getText());
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

    @Tag("functional")
    @Test
    @DisplayName("Проверка ответа при превышении максимально допустимого значения limit")
    public void testGetTodosWithExcessiveLimit() {

        ValidatedTodoRequest unauthRequest = new ValidatedTodoRequest((RequestSpec.unauthSpec()));
        // Создаем 10 TODO
        for (int i = 1; i <= 10; i++) {
            unauthRequest.create(new Todo(i, "Task " + i, i % 2 == 0));
        }
        List<Todo> todos = unauthRequest.readAll(0, 1000);

        // Проверяем, что вернулось 10 задач
        assertEquals(10, todos.size());
    }
}
