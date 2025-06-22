package com.todo.annotations;

import com.todo.conf.Configuration;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class MobileExecutionExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        var testMethod = extensionContext.getRequiredTestMethod();

        var mobile = testMethod.getAnnotation(Mobile.class);

        if (mobile != null) {
            Configuration.setProperty("version", "mobile");
        }
    }
}
