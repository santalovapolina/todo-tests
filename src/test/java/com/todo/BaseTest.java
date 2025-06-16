package com.todo;

import com.todo.config.Config;
import com.todo.requests.TodoRequest;
import com.todo.requests.TodoRequester;
import com.todo.specs.request.RequestSpec;
import com.todo.storages.TestDataStorage;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public class BaseTest {
    protected TodoRequester todoRequester;

    @BeforeAll
    public static void setup() {
        RestAssured.defaultParser = Parser.JSON;
        RestAssured.baseURI = Config.getInstance().get("baseUrl");
    }

    @BeforeEach
    public void setupTest() {
        todoRequester = new TodoRequester(RequestSpec.authSpec());
    }


    @AfterEach
    public void clean() {
        TestDataStorage.getInstance().getStorage()
                .forEach((k, v) ->
                        new TodoRequest(RequestSpec.authSpec())
                                .delete(k));

        TestDataStorage.getInstance().clean();
    }
}
