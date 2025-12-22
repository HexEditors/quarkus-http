package com.github.hexeditors.http;

import java.lang.reflect.Field;

public final class TestInjectionUtil {

    private TestInjectionUtil() {
    }

    public static void inject(Object target, Object... deps) {
        for (Object dep : deps) {
            for (Field field : target.getClass().getDeclaredFields()) {
                if (field.getType().isAssignableFrom(dep.getClass())) {
                    field.setAccessible(true);
                    try {
                        field.set(target, dep);
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }
}
