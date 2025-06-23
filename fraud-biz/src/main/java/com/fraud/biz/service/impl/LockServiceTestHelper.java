package com.fraud.biz.service.impl;

import java.lang.reflect.Field;

public class LockServiceTestHelper {
    public static String getCurrentThreadUUID(LockService lockService) {
        try {
            Field field = LockService.class.getDeclaredField("LOCK_ID_HOLDER");
            field.setAccessible(true);
            ThreadLocal<String> local = (ThreadLocal<String>) field.get(lockService);
            return local.get();
        } catch (Exception e) {
            throw new RuntimeException("反射读取 LOCK_ID_HOLDER 失败", e);
        }
    }

    public static void setThreadLocalValue(LockService lockService, String value) {
        try {
            Field threadLocalField = LockService.class.getDeclaredField("LOCK_ID_HOLDER");
            threadLocalField.setAccessible(true);
            ThreadLocal<String> lockIdHolder = (ThreadLocal<String>) threadLocalField.get(lockService);
            lockIdHolder.set(value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ThreadLocal in test", e);
        }
    }
}
