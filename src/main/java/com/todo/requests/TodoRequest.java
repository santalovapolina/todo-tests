package com.todo.requests;

import com.todo.models.Todo;
import com.todo.requests.interfaces.CrudInterface;
import com.todo.requests.interfaces.SearchInterface;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class TodoRequest extends Request implements CrudInterface<Todo>, SearchInterface<Todo> {

    public TodoRequest(RequestSpecification reqSpec) {
        super(reqSpec);
    }

    @Override
    public Response create(Todo entity) {
        return given()
                .spec(reqSpec)
                .body(entity)
                .when()
                .post();
    }

    @Override
    public Response update(long id, Todo entity) {
        return given()
                .spec(reqSpec)
                .body(entity)
                .put("/" + id);
    }

    @Override
    public Response delete(long id) {
        return given()
                .spec(reqSpec)
                .delete("/" + id);
    }

    @Override
    public Response readAll(int offset, int limit) {
        return given()
                .spec(reqSpec)
                .queryParam("offset", offset)
                .queryParam("limit", limit)
                .when()
                .get();
    }

    public Response readAll() {
        return given()
                .spec(reqSpec)
                .when()
                .get();
    }
}
