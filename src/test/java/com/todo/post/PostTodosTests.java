package com.todo.post;

import com.todo.BaseTest;
import com.todo.annotations.DataPreparationExtension;
import com.todo.annotations.DeterminedEnvExtension;
import com.todo.models.Todo;
import com.todo.requests.TodoRequest;
import com.todo.requests.ValidatedTodoRequest;
import com.todo.specs.request.RequestSpec;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("TODO Management")
@Feature("Create Todos API")
@ExtendWith(DeterminedEnvExtension.class)
@ExtendWith(DataPreparationExtension.class)
public class PostTodosTests extends BaseTest {

    @Tag("functional")
    @Test
    public void testCreateTodoWithValidData() {
        Todo newTodo = new Todo(1, "New Task", false);

        // Отправляем POST запрос для создания нового TODO
        ValidatedTodoRequest unauthRequest = new ValidatedTodoRequest(RequestSpec.unauthSpec());

        String response = unauthRequest.create(newTodo);
        assertTrue(response.isEmpty()); // Проверяем, что тело ответа пустое

        // Проверяем, что TODO было успешно создано
        List<Todo> todos = unauthRequest.readAll();

        // Ищем созданную задачу в списке
        boolean found = false;
        for (Todo todo : todos) {
            if (todo.getId() == newTodo.getId()) {
                assertEquals(newTodo.getText(), todo.getText());
                assertEquals(newTodo.isCompleted(), todo.isCompleted());
                found = true;
                break;
            }
        }
        assertTrue(found, "Созданная задача не найдена в списке TODO");
    }

    /**
     * TC2: Попытка создания TODO с отсутствующими обязательными полями.
     */

    @Tag("request schema")
    @Test
    public void testCreateTodoWithMissingFields() {
        // Создаем JSON без обязательного поля 'text'
        String invalidTodoJson = "{ \"id\": 2, \"completed\": true }";

        TodoRequest unauthRequest = new TodoRequest(RequestSpec.unauthSpec());

//        String response = unauthRequest.create(invalidTodoJson)
//                .then()
//                .statusCode(400);
//
//        Assertions.assertFalse(response.isEmpty());
    }

    /**
     * TC3: Создание TODO с максимально допустимой длиной поля 'text'.
     */
    @Tag("functional")
    @Test
    public void testCreateTodoWithMaxLengthText() {
        // Предполагаем, что максимальная длина поля 'text' составляет 255 символов
        String maxLengthText = "A".repeat(255);
        Todo newTodo = new Todo(3, maxLengthText, false);

        ValidatedTodoRequest unauthRequest = new ValidatedTodoRequest(RequestSpec.unauthSpec());

        // Отправляем POST запрос для создания нового TODO
        String response = unauthRequest.create(newTodo);

        assertTrue(response.isEmpty()); // Проверяем, что тело ответа пустое

        // Проверяем, что TODO было успешно создано
        List<Todo> todos = unauthRequest.readAll();

        // Ищем созданную задачу в списке
        boolean found = false;
        for (Todo todo : todos) {
            if (todo.getId() == newTodo.getId()) {
                assertEquals(newTodo.getText(), todo.getText());
                assertEquals(newTodo.isCompleted(), todo.isCompleted());
                found = true;
                break;
            }
        }
        assertTrue(found, "Созданная задача не найдена в списке TODO");
    }

    /**
     * TC4: Передача некорректных типов данных в полях.
     */
    @Tag("request schema")
    @Test
    public void testCreateTodoWithInvalidDataTypes() {
        // Поле 'completed' содержит строку вместо булевого значения
        String invalidTodoJson = "{ \"id\": 3, \"text\": \"Updated Task\", \"completed\": \"notBoolean\" }";

        TodoRequest unauthRequest = new TodoRequest(RequestSpec.unauthSpec());

//        String response = unauthRequest.create(invalidTodoJson);
        //  assertFalse(response.isEmpty());
    }

    /**
     * TC5: Создание TODO с уже существующим 'id' (если 'id' задается клиентом).
     */

    @Tag("functional")
    @Test
    public void testCreateTodoWithExistingId() {

        Todo duplicateTodo = new Todo(5, "Duplicate Task", true);

        TodoRequest unauthRequest = new TodoRequest(RequestSpec.unauthSpec());

        unauthRequest.create(duplicateTodo);
        unauthRequest.create(duplicateTodo)
                .then()
                .statusCode(400); // Конфликт при дублировании 'id'
        //.contentType(ContentType.TEXT)
        //    .body(is(notNullValue()));
    }

}
