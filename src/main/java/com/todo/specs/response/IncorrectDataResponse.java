package com.todo.specs.response;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;

public class IncorrectDataResponse {

    public static ResponseSpecification STATUS_400 = new ResponseSpecBuilder()
            .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
            .build();

    public static ResponseSpecification STATUS_401 = new ResponseSpecBuilder()
            .expectStatusCode(HttpStatus.SC_UNAUTHORIZED)
            .build();

    public static ResponseSpecification STATUS_404 = new ResponseSpecBuilder()
            .expectStatusCode(HttpStatus.SC_NOT_FOUND)
            .build();

    public static ResponseSpecification sameId(long id) {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody(Matchers.containsString("You are trying to use the same id:" + id))
                .build();
    }

    private IncorrectDataResponse() {
    }
}
