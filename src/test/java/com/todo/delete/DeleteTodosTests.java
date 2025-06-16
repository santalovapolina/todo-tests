package com.todo.delete;

import com.todo.BaseTest;
import com.todo.annotations.DataPreparationExtension;
import com.todo.annotations.DeterminedEnvExtension;
import com.todo.requests.TodoRequest;
import com.todo.requests.ValidatedTodoRequest;
import com.todo.specs.request.RequestSpec;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import com.todo.models.Todo;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

@Epic("TODO Management")
@Feature("Delete Todos API")
@ExtendWith(DeterminedEnvExtension.class)
@ExtendWith(DataPreparationExtension.class)
public class DeleteTodosTests extends BaseTest {

    /**
     * TC1: Успешное удаление существующего TODO с корректной авторизацией.
     */
    @Tag("functional")
    @Test
    public void testDeleteExistingTodoWithValidAuth() {
        // Создаем TODO для удаления
        Todo todo = new Todo(1, "Task to Delete", false);
        ValidatedTodoRequest authRequest = new ValidatedTodoRequest((RequestSpec.authSpec()));
        authRequest.create(todo);

        // Отправляем DELETE запрос с корректной авторизацией
        String response = authRequest.delete(1);

        // Проверяем, что тело ответа пустое
        Assertions.assertTrue(response.isEmpty());

        // Получаем список всех TODO и проверяем, что удаленная задача отсутствует
        List<Todo> todos = authRequest.readAll();

        // Проверяем, что удаленная задача отсутствует в списке
        boolean found = false;
        for (Todo t : todos) {
            if (t.getId() == todo.getId()) {
                found = true;
                break;
            }
        }
        Assertions.assertFalse(found, "Удаленная задача все еще присутствует в списке TODO");
    }

    /**
     * TC2: Попытка удаления TODO без заголовка Authorization.
     */

    @Tag("functional")
    @Test
    public void testDeleteTodoWithoutAuthHeader() {
        // Создаем TODO для удаления
        Todo todo = new Todo(2, "Task to Delete", false);
        TodoRequest unauthRequest = new TodoRequest((RequestSpec.unauthSpec()));
        unauthRequest.create(todo);

        // Отправляем DELETE запрос без заголовка Authorization
        unauthRequest.delete(2)
                .then()
                .statusCode(401);
        //.contentType(ContentType.JSON)
        //.body("error", notNullValue()); // Проверяем наличие сообщения об ошибке

        // Проверяем, что TODO не было удалено
        Response response = unauthRequest.readAll();
        List<Todo> todos = response.jsonPath().getList(".", Todo.class);

        // Проверяем, что задача все еще присутствует в списке
        boolean found = false;
        for (Todo t : todos) {
            if (t.getId() == todo.getId()) {
                found = true;
                break;
            }
        }
        Assertions.assertTrue(found, "Задача отсутствует в списке TODO, хотя не должна была быть удалена");
    }

    /**
     * TC3: Попытка удаления TODO с некорректными учетными данными.
     */

    @Tag("functional")
    @Test
    public void testDeleteTodoWithInvalidAuth() {
        // Создаем TODO для удаления
        Todo todo = new Todo(3, "Task to Delete", false);
        TodoRequest unauthRequest = (new TodoRequest(RequestSpec.unauthSpec()));
        unauthRequest.create(todo);

        // Отправляем DELETE запрос с некорректной авторизацией
        unauthRequest.delete(3)
                .then()
                .statusCode(401);
//                .contentType(ContentType.JSON)
//                .body("error", notNullValue());

        // Проверяем, что TODO не было удалено
        Response response = unauthRequest.readAll();
        List<Todo> todos = response.jsonPath().getList(".", Todo.class);

        // Проверяем, что задача все еще присутствует в списке


        boolean found = false;
        for (Todo t : todos) {
            if (t.getId() == todo.getId()) {
                found = true;
                break;
            }
        }
        Assertions.assertTrue(found, "Задача отсутствует в списке TODO, хотя не должна была быть удалена");
    }

    /**
     * TC4: Удаление TODO с несуществующим id.
     */

    @Tag("functional")
    @Test
    public void testDeleteNonExistentTodo() {
        // Отправляем DELETE запрос для несуществующего TODO с корректной авторизацией
        TodoRequest authRequest = new TodoRequest(RequestSpec.authSpec());

        authRequest.delete(999)
                .then()
                .statusCode(404);
//                .contentType(ContentType.JSON)
//                .body("error", notNullValue());

        // Дополнительно можем проверить, что список TODO не изменился
        Response response = authRequest.readAll();

        List<Todo> todos = response.jsonPath().getList(".", Todo.class);
        Assertions.assertTrue(todos.isEmpty());
        // В данном случае, поскольку мы не добавляли задач с id 999, список должен быть пуст или содержать только ранее добавленные задачи
    }

    /**
     * TC5: Попытка удаления с некорректным форматом id (например, строка вместо числа).
     */

    @Tag("request schema")
    @Test
    public void testDeleteTodoWithInvalidIdFormat() {

        long invalidId = 2222222222222222222L;

        // Отправляем DELETE запрос с некорректным id
        TodoRequest authRequest = new TodoRequest(RequestSpec.authSpec());
        authRequest.delete(invalidId)
                .then()
                .statusCode(404);
//                .contentType(ContentType.JSON);
//                .body("error", notNullValue());
    }
}
