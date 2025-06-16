package com.todo.put;

import com.todo.BaseTest;
import com.todo.annotations.DataPreparationExtension;
import com.todo.annotations.DeterminedEnvExtension;
import com.todo.requests.TodoRequest;
import com.todo.requests.ValidatedTodoRequest;
import com.todo.specs.request.RequestSpec;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.todo.models.Todo;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

@Epic("TODO Management")
@Feature("Update Todos API")
@ExtendWith(DeterminedEnvExtension.class)
@ExtendWith(DataPreparationExtension.class)
public class PutTodosTests extends BaseTest {

    /**
     * TC1: Обновление существующего TODO корректными данными.
     */

    @Tag("functional")
    @Test
    public void testUpdateExistingTodoWithValidData() {
        // Создаем TODO для обновления
        Todo originalTodo = new Todo(1, "Original Task", false);
        ValidatedTodoRequest authRequest = new ValidatedTodoRequest(RequestSpec.authSpec());

        authRequest.create(originalTodo);
        // Обновленные данные
        Todo updatedTodo = new Todo(1, "Updated Task", true);

        // Отправляем PUT запрос для обновления
        authRequest.update(1, updatedTodo);

        // Проверяем, что данные были обновлены
        List<Todo> todos = authRequest.readAll();

        assertEquals(1, todos.size());
        assertEquals("Updated Task", todos.get(0).getText());
        assertTrue(todos.get(0).isCompleted());
    }

    /**
     * TC2: Попытка обновления TODO с несуществующим id.
     */
    @Tag("functional")
    @Test
    public void testUpdateNonExistentTodo() {
        // Обновленные данные для несуществующего TODO
        Todo updatedTodo = new Todo(999, "Non-existent Task", true);

        TodoRequest authRequest = new TodoRequest(RequestSpec.authSpec());

        authRequest.create(updatedTodo);
        authRequest.update(9999, updatedTodo)
                .then()
                .statusCode(404);
    }

    /**
     * TC3: Обновление TODO с отсутствием обязательных полей.
     */
    @Tag("request schema")
    @Test
    public void testUpdateTodoWithMissingFields() {
        // Создаем TODO для обновления
        Todo originalTodo = new Todo(2, "Task to Update", false);

        TodoRequest authRequest = new TodoRequest(RequestSpec.unauthSpec());

        authRequest.create(originalTodo);

        // Обновленные данные с отсутствующим полем 'text'
        String invalidTodoJson = "{ \"id\": 2, \"completed\": true }";

//        authRequest.update(2, invalidTodoJson)
//                .then()
//                .assertThat().body("error", containsString("missing field 'text'"));

    }

    /**
     * TC4: Передача некорректных типов данных при обновлении.
     */

    @Tag("request schema")
    @Test
    public void testUpdateTodoWithInvalidDataTypes() {
        // Создаем TODO для обновления
        Todo originalTodo = new Todo(3, "Another Task", false);
        TodoRequest authRequest = new TodoRequest(RequestSpec.unauthSpec());

        authRequest.create(originalTodo);

        // Обновленные данные с некорректным типом поля 'completed'
//        String invalidTodoJson = "{ \"id\": 3, \"text\": \"Updated Task\", \"completed\": \"notBoolean\" }";

//        authRequest.update(3, invalidTodoJson)
//                        .then()
//                .assertThat().body("error", containsString("invalid type: string 'notBoolean'"));

    }

    /**
     * TC5: Обновление TODO без изменения данных (передача тех же значений).
     */
    @Tag("functional")
    @Test
    public void testUpdateTodoWithoutChangingData() {
        // Создаем TODO для обновления
        Todo originalTodo = new Todo(4, "Task without Changes", false);
        ValidatedTodoRequest authRequest = new ValidatedTodoRequest(RequestSpec.authSpec());
        authRequest.create(originalTodo);

        // Отправляем PUT запрос с теми же данными
        authRequest.update(4, originalTodo);

        // Проверяем, что данные не изменились
        List<Todo> todos = authRequest.readAll();

        assertEquals("Task without Changes", todos.get(0).getText());
        assertFalse(todos.get(0).isCompleted());
    }
}
