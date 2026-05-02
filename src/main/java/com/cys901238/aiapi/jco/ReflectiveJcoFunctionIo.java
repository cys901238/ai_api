package com.cys901238.aiapi.jco;

import java.lang.reflect.InvocationTargetException;

public class ReflectiveJcoFunctionIo implements JcoFunctionIo {
    private final Object function;

    public ReflectiveJcoFunctionIo(Object function) {
        if (function == null) {
            throw new IllegalArgumentException("JCo function must not be null.");
        }
        this.function = function;
    }

    @Override
    public String getImportString(String name) {
        Object importList = invoke(function, "getImportParameterList");
        if (importList == null) {
            return null;
        }
        Object value = invoke(importList, "getString", String.class, name);
        return value == null ? null : value.toString();
    }

    @Override
    public void setExportString(String name, String value) {
        Object exportList = requireList("getExportParameterList", name);
        invoke(exportList, "setValue", String.class, Object.class, name, value);
    }

    @Override
    public void setExportDouble(String name, Double value) {
        Object exportList = requireList("getExportParameterList", name);
        invoke(exportList, "setValue", String.class, Object.class, name, value);
    }

    @Override
    public JcoTableIo table(String name) {
        Object tableList = requireList("getTableParameterList", name);
        Object table = invoke(tableList, "getTable", String.class, name);
        return new ReflectiveJcoTableIo(table, name);
    }

    private Object requireList(String methodName, String parameterName) {
        Object list = invoke(function, methodName);
        if (list == null) {
            throw new IllegalArgumentException("Missing JCo parameter list for " + parameterName + ".");
        }
        return list;
    }

    private Object invoke(Object target, String methodName) {
        try {
            return target.getClass().getMethod(methodName).invoke(target);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to call " + methodName + " on " + target.getClass().getName() + ".", unwrap(e));
        }
    }

    private Object invoke(Object target, String methodName, Class<?> argType, Object arg) {
        try {
            return target.getClass().getMethod(methodName, argType).invoke(target, arg);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to call " + methodName + " on " + target.getClass().getName() + ".", unwrap(e));
        }
    }

    private void invoke(Object target, String methodName, Class<?> arg1Type, Class<?> arg2Type, Object arg1, Object arg2) {
        try {
            target.getClass().getMethod(methodName, arg1Type, arg2Type).invoke(target, arg1, arg2);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to call " + methodName + " on " + target.getClass().getName() + ".", unwrap(e));
        }
    }

    private Throwable unwrap(ReflectiveOperationException exception) {
        if (exception instanceof InvocationTargetException invocationTargetException
            && invocationTargetException.getTargetException() != null) {
            return invocationTargetException.getTargetException();
        }
        return exception;
    }

    private static final class ReflectiveJcoTableIo implements JcoTableIo {
        private final Object table;
        private final String tableName;

        private ReflectiveJcoTableIo(Object table, String tableName) {
            if (table == null) {
                throw new IllegalArgumentException("Missing JCo table parameter " + tableName + ".");
            }
            this.table = table;
            this.tableName = tableName;
        }

        @Override
        public void clear() {
            invoke("deleteAllRows");
        }

        @Override
        public void appendRow() {
            invoke("appendRow");
        }

        @Override
        public void setValue(String column, String value) {
            if (trySetValue(String.class, Object.class, column, value)) {
                return;
            }
            if (trySetValue(Object.class, String.class, value, column)) {
                return;
            }
            throw new IllegalStateException("Failed to find a compatible setValue method on table " + tableName + ".");
        }

        private void invoke(String methodName) {
            try {
                table.getClass().getMethod(methodName).invoke(table);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Failed to call " + methodName + " on table " + tableName + ".", unwrap(e));
            }
        }

        private boolean trySetValue(Class<?> arg1Type, Class<?> arg2Type, Object arg1, Object arg2) {
            try {
                table.getClass().getMethod("setValue", arg1Type, arg2Type).invoke(table, arg1, arg2);
                return true;
            } catch (NoSuchMethodException exception) {
                return false;
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Failed to set table value on " + tableName + ".", unwrap(e));
            }
        }

        private Throwable unwrap(ReflectiveOperationException exception) {
            if (exception instanceof InvocationTargetException invocationTargetException
                && invocationTargetException.getTargetException() != null) {
                return invocationTargetException.getTargetException();
            }
            return exception;
        }
    }
}
