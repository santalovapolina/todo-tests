package com.todo.post;

import com.todo.BaseTest;
import com.todo.assertions.Assert;
import com.todo.models.Todo;
import com.todo.models.TodoBuilder;
import com.todo.specs.response.IncorrectDataResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.todo.generators.TestDataGeneratorFaker.generateFakerTestData;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class PostTodosTests extends BaseTest {

    @Test
    public void testCreateTodoWithValidData() {
        // Создаем TODO
        Todo todo = generateFakerTestData(Todo.class);

        todoRequester.getValidatedRequest().create(todo);

        //Проверяем что todo есть в списке
        Response readResponse = todoRequester.getRequest().readAll();
        Assert.assertTodoExists(todo.getId(), readResponse);
    }

    /**
     * TC2: Попытка создания TODO с отсутствующими обязательными полями.
     */
    @Tag("request schema")
    @Test
    public void testCreateTodoWithMissingFields() {
        // Создаем JSON без обязательного поля 'text'
        String invalidTodoJson = "{ \"id\": 2, \"completed\": true }";

//        todoRequester.getRequest().create().then()
//                .spec(new IncorrectDataResponse().checkStatus400()).body(notNullValue()); // Проверяем, что есть сообщение об ошибке
    }

    /**
     * TC3: Создание TODO с максимально допустимой длиной поля 'text'.
     */
    @Test
    public void testCreateTodoWithMaxLengthText() {
        // Предполагаем, что максимальная длина поля 'text' составляет 255 символов
        String maxLengthText = "A".repeat(255);
        Todo todo = new TodoBuilder().setText(maxLengthText).build();

        // Отправляем POST запрос для создания нового TODO
        todoRequester.getValidatedRequest().create(todo);

        // Проверяем, что TODO было успешно создано
        Response readResponse = todoRequester.getRequest().readAll();

        Todo[] todosArray = readResponse.getBody().as(Todo[].class);
        List<Todo> actualTodo   = Arrays.asList(todosArray);
        List<Todo> expectedTodo = Arrays.asList(todo);

        assertAll("Проверка созданного TODO",
                () -> Assert.assertTodoMatches(actualTodo, expectedTodo),
                () ->  Assert.assertTodoExists(todo.getId(), readResponse)
        );
    }

    /**
     * TC4: Передача некорректных типов данных в полях.
     */
    @Tag("request schema")
    @Test
    public void testCreateTodoWithInvalidDataTypes() {
        // Поле 'completed' содержит строку вместо булевого значения
        Todo newTodo = new TodoBuilder()
//                .setCompleted(("text"))
                .build();

        todoRequester.getRequest().create(newTodo)
                .then()
                .spec(new IncorrectDataResponse().checkStatus400())
                .body(notNullValue()); // Проверяем, что есть сообщение об ошибке
    }

    /**
     * TC5: Создание TODO с уже существующим 'id' (если 'id' задается клиентом).
     */
    @Test
    public void testCreateTodoWithExistingId() {
        // Сначала создаем TODO
        Todo firstTodo = generateFakerTestData(Todo.class);
        todoRequester.getRequest().create(firstTodo);

        // Пытаемся создать другую TODO перезаписывая id
        Todo duplicateTodo = generateFakerTestData(Todo.class);
        duplicateTodo.setId(firstTodo.getId());

        todoRequester.getRequest()
                .create(duplicateTodo)
                .then()
                .spec(new IncorrectDataResponse().checkStatus400());
    }
}
