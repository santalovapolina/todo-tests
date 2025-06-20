package com.todo.put;

import com.todo.BaseTest;
import com.todo.assertions.Assert;
import com.todo.models.TodoBuilder;
import com.todo.specs.response.IncorrectDataResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.todo.generators.TestDataGeneratorFaker.generateFakerTestData;
import static org.junit.jupiter.api.Assertions.*;

import com.todo.models.Todo;

import java.util.Arrays;
import java.util.List;

public class PutTodosTests extends BaseTest {

    /**
     * TC1: Обновление существующего TODO корректными данными.
     */
    @Test
    public void testUpdateExistingTodoWithValidData() {
        // Создаем TODO для обновления
        Todo originalTodo = new TodoBuilder()
                .setId(9).setText("New todo").setCompleted(false).build();
        todoRequester.getRequest().create(originalTodo);

        Todo updatedTodo = new TodoBuilder()
                .setId(originalTodo.getId()).setText("Updated todo").setCompleted(true).build();
        // Отправляем PUT запрос для обновления
        todoRequester.getValidatedRequest().update(originalTodo.getId(), updatedTodo);

        Response readResponse = todoRequester.getRequest().readAll();

        Todo[] todos = readResponse.getBody().as(Todo[].class);
        List<Todo> actualTodo   = Arrays.asList(todos);
        List<Todo> expectedTodo = Arrays.asList(updatedTodo);

        assertAll("Проверка обновлённого TODO",
                () -> Assert.assertResponseSize(1, readResponse),
                () -> Assert.assertTodoMatches(actualTodo, expectedTodo),
                () ->  Assert.assertTodoExists(originalTodo.getId(), readResponse)
        );
    }

    /**
     * TC2: Попытка обновления TODO с несуществующим id.
     */
    @Test
    public void testUpdateNonExistentTodo() {
        // Обновленные данные для несуществующего TODO
        Todo updateTodo = generateFakerTestData(Todo.class);
        todoRequester.getRequest().update(updateTodo.getId(), updateTodo)
                .then().spec(new IncorrectDataResponse().checkStatus404());

    }

    /**
     * TC3: Обновление TODO с отсутствием обязательных полей.
     */
    @Tag("request schema")
    @Test
    public void testUpdateTodoWithMissingFields() {
        // Создаем TODO для обновления
        Todo originalTodo = generateFakerTestData(Todo.class);
        todoRequester.getRequest().create(originalTodo);
        // Обновленные данные с отсутствующим полем 'text'
        String invalidTodoJson = "{ \"id\": 2, \"completed\": true }";

//        todoRequester.getRequest().update().then().spec(new IncorrectDataResponse().checkStatus400());

//        given()
//                .filter(new AllureRestAssured())
//                .contentType(ContentType.JSON)
//                .body(invalidTodoJson)
//                .when()
//                .put("/todos/2")
//                .then()
//                .statusCode(401);
                //.contentType(ContentType.JSON)
                //.body("error", containsString("Missing required field 'text'"));
    }

    /**
     * TC4: Передача некорректных типов данных при обновлении.
     */
    @Tag("request schema")
    @Test
    public void testUpdateTodoWithInvalidDataTypes() {
        // Создаем TODO для обновления
        Todo originalTodo = generateFakerTestData(Todo.class);
        todoRequester.getRequest().create(originalTodo);

        // Обновленные данные с некорректным типом поля 'completed'
        String invalidTodoJson = "{ \"id\": 3, \"text\": \"Updated Task\", \"completed\": \"notBoolean\" }";

//        todoRequester.getRequest().update().then().spec(new IncorrectDataResponse().checkStatus400());

//        given()
//                .filter(new AllureRestAssured())
//                .contentType(ContentType.JSON)
//                .body(invalidTodoJson)
//                .when()
//                .put("/todos/3")
//                .then()
//                .statusCode(401);
    }

    /**
     * TC5: Обновление TODO без изменения данных (передача тех же значений).
     */
    @Test
    public void testUpdateTodoWithoutChangingData() {
        // Создаем TODO для обновления
        Todo originalTodo = generateFakerTestData(Todo.class);
        todoRequester.getRequest().create(originalTodo);

        // Отправляем PUT запрос с теми же данными
        todoRequester.getValidatedRequest().update(originalTodo.getId(),originalTodo);

        Response readResponse = todoRequester.getRequest().readAll();

        Todo[] todos = readResponse.getBody().as(Todo[].class);
        List<Todo> actualTodo   = Arrays.asList(todos);
        List<Todo> expectedTodo = Arrays.asList(originalTodo);

        assertAll("Проверка обновлённого TODO",
                () -> Assert.assertResponseSize(1, readResponse),
                () -> Assert.assertTodoMatches(actualTodo, expectedTodo)
        );
    }
}
