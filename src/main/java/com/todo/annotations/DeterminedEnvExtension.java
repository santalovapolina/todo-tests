package com.todo.annotations;

import com.todo.config.Config;
import io.restassured.RestAssured;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Method;

public class DeterminedEnvExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        Method testMethod = context.getRequiredTestMethod();
        String endpoint;

        if (testMethod.isAnnotationPresent(Mobile.class)) {
            endpoint = Config.getInstance().get("endpointMobile");
        } else {
            endpoint = Config.getInstance().get("endpointBase");
        }

        RestAssured.basePath = endpoint;
    }
}
