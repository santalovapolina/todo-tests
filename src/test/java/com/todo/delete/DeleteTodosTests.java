package com.todo.delete;

import com.todo.BaseTest;

import com.todo.assertions.Assert;
import com.todo.requests.TodoRequester;
import com.todo.specs.request.RequestSpec;
import com.todo.specs.response.IncorrectDataResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.todo.generators.TestDataGeneratorFaker.generateFakerTestData;
import static org.junit.jupiter.api.Assertions.assertAll;
import com.todo.models.Todo;

public class DeleteTodosTests extends BaseTest {

    /**
     * TC1: Успешное удаление существующего TODO с корректной авторизацией.
     */
    @Test
    public void testDeleteExistingTodoWithValidAuth() {
        // Создаем TODO для удаления
        Todo todo = generateFakerTestData(Todo.class);
        todoRequester.getRequest().create(todo);

        // Отправляем DELETE запрос с корректной авторизацией
        String deleteResponse = todoRequester.getValidatedRequest().delete(todo.getId());

        Response readResponse = todoRequester.getRequest().readAll();

        assertAll("Проверки после удаления TODO",
                () -> Assert.assertEmptyBody(deleteResponse),
                () -> Assert.assertTodoNotExist(todo.getId(), readResponse)
        );
    }

    /**
     * TC2: Попытка удаления TODO без заголовка Authorization.
     */
    @Test
    public void testDeleteTodoWithoutAuthHeader() {
        // Создаем TODO для удаления
        Todo todo = generateFakerTestData(Todo.class);
        todoRequester.getRequest().create(todo);

        todoRequester = new TodoRequester(RequestSpec.unauthSpec());

        // Отправляем DELETE запрос без заголовка Authorization
        todoRequester.getRequest().delete(todo.getId()).then().spec(new IncorrectDataResponse().checkStatus401());

        Response readResponse = todoRequester.getRequest().readAll();
        Assert.assertTodoExists(todo.getId(), readResponse);
    }


    /**
     * TC3: Попытка удаления TODO с некорректными учетными данными.
     */
    @Test
    public void testDeleteTodoWithInvalidAuth() {
        // Создаем TODO для удаления
        Todo todo = generateFakerTestData(Todo.class);
        todoRequester.getRequest().create(todo);

        todoRequester = new TodoRequester(RequestSpec.incorrectAuthSpec());
        // Отправляем DELETE запрос с некорректной авторизацией
        todoRequester.getRequest().delete(todo.getId()).then().spec(new IncorrectDataResponse().checkStatus401());

        Response readResponse = todoRequester.getRequest().readAll();
        Assert.assertTodoExists(todo.getId(), readResponse);
    }


    /**
     * TC4: Удаление TODO с несуществующим id.
     */
    @Test
    public void testDeleteNonExistentTodo() {
        // Отправляем DELETE запрос для несуществующего TODO с корректной авторизацией
        todoRequester.getRequest().delete(999).then().spec(new IncorrectDataResponse().checkStatus404());

        // Проверить, что список TODO не изменился
        Response readResponse = todoRequester.getRequest().readAll();
        Assert.assertResponseSize(0, readResponse);
    }

    /**
     * TC5: Попытка удаления с некорректным форматом id (например, строка вместо числа).
     */
    @Tag("request schema")
    @Test
    public void testDeleteTodoWithInvalidIdFormat() {
        // Отправляем DELETE запрос с некорректным id
//        todoRequester.getRequest().delete("1222222").then().spec(new IncorrectDataResponse().checkStatus404());


//        given()
//                .filter(new AllureRestAssured())
//                .auth()
//                .preemptive()
//                .basic("admin", "admin")
//                .when()
//                .delete("/todos/invalidId")
//                .then()
//                .statusCode(404);
//                .contentType(ContentType.JSON)
//                .body("error", notNullValue());
    }
}
