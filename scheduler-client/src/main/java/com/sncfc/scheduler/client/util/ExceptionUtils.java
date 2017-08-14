package com.sncfc.scheduler.client.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public abstract class ExceptionUtils {

    private static final String[] CAUSE_METHOD_NAMES = {
            "getCause",
            "getNextException",
            "getTargetException",
            "getException",
            "getSourceException",
            "getRootCause",
            "getCausedByException",
            "getNested",
            "getLinkedException",
            "getNestedException",
            "getLinkedCause",
            "getThrowable",
    };

    public static Throwable getRootCause(Throwable throwable) {
        List<Throwable> list = getThrowableList(throwable);
        return list.get(list.size() - 1);
    }

    public static List<Throwable> getThrowableList(Throwable throwable) {
        List<Throwable> list = new ArrayList<Throwable>();
        while (throwable != null && list.contains(throwable) == false) {
            list.add(throwable);
            throwable = ExceptionUtils.getCause(throwable);
        }
        return list;
    }

    public static Throwable getCause(Throwable throwable) {
        return getCause(throwable, CAUSE_METHOD_NAMES);
    }

    public static Throwable getCause(Throwable throwable, String[] methodNames) {
        if (throwable == null) {
            return null;
        }

        if (methodNames == null) {
            methodNames = CAUSE_METHOD_NAMES;
        }

        for (String methodName : methodNames) {
            if (methodName != null) {
                Throwable cause = getCauseUsingMethodName(throwable, methodName);
                if (cause != null) {
                    return cause;
                }
            }
        }

        return null;
    }

    private static Throwable getCauseUsingMethodName(Throwable throwable, String methodName) {
        Method method = null;
        try {
            method = throwable.getClass().getMethod(methodName);
        } catch (NoSuchMethodException ignored) { // NOPMD
            // exception ignored
        } catch (SecurityException ignored) { // NOPMD
            // exception ignored
        }

        if (method != null && Throwable.class.isAssignableFrom(method.getReturnType())) {
            try {
                return (Throwable) method.invoke(throwable);
            } catch (IllegalAccessException ignored) { // NOPMD
                // exception ignored
            } catch (IllegalArgumentException ignored) { // NOPMD
                // exception ignored
            } catch (InvocationTargetException ignored) { // NOPMD
                // exception ignored
            }
        }
        return null;
    }

}
