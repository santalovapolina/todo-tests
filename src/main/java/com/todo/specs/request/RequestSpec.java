package com.todo.specs.request;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.List;

public class RequestSpec {


    private static RequestSpecBuilder baseSpecBuilder() {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.addFilters(List.of(
                new RequestLoggingFilter(), new
                        ResponseLoggingFilter(),
                new AllureRestAssured()));
        requestSpecBuilder.setContentType(ContentType.JSON);
        requestSpecBuilder.setAccept(ContentType.JSON);
        return requestSpecBuilder;
    }

    public static RequestSpecification unauthSpec() {
        return baseSpecBuilder().build();
    }

    public static RequestSpecification authSpec() {
        return baseSpecBuilder()
                .build()
                .auth()
                .preemptive()
                .basic("admin", "admin");
    }
}
