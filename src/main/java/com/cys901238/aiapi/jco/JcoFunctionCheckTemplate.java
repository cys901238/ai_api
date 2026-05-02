package com.cys901238.aiapi.jco;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

public class JcoFunctionCheckTemplate {
    public static void main(String[] args) {
        String functionName = args.length > 0 ? args[0] : "YOUR_FUNCTION_NAME";
        Properties properties = new Properties();
        properties.setProperty("jco.client.ashost", "YOUR_SAP_HOST");
        properties.setProperty("jco.client.sysnr", "00");
        properties.setProperty("jco.client.client", "000");
        properties.setProperty("jco.client.user", "YOUR_USER");
        properties.setProperty("jco.client.passwd", "YOUR_PASSWORD");
        properties.setProperty("jco.client.lang", "EN");
        properties.setProperty("jco.destination.peak_limit", "2");
        properties.setProperty("jco.destination.pool_capacity", "1");

        try {
            Class<?> destinationDataProvider = Class.forName("com.sap.conn.jco.ext.DestinationDataProvider");
            Class<?> environment = Class.forName("com.sap.conn.jco.ext.Environment");
            new InMemoryDestinationDataProvider(destinationDataProvider, environment, "TMP_DEST", properties);

            Class<?> destinationManager = Class.forName("com.sap.conn.jco.JCoDestinationManager");
            Object destination = destinationManager.getMethod("getDestination", String.class).invoke(null, "TMP_DEST");
            destination.getClass().getMethod("ping").invoke(destination);
            System.out.println("PING_OK");

            Object repository = destination.getClass().getMethod("getRepository").invoke(destination);
            Object function = repository.getClass().getMethod("getFunction", String.class).invoke(repository, functionName);
            if (function == null) {
                System.out.println("FUNCTION_NULL");
                return;
            }

            dumpList("IMPORT", function.getClass().getMethod("getImportParameterList").invoke(function));
            dumpList("EXPORT", function.getClass().getMethod("getExportParameterList").invoke(function));
            dumpList("CHANGING", function.getClass().getMethod("getChangingParameterList").invoke(function));
            dumpList("TABLE", function.getClass().getMethod("getTableParameterList").invoke(function));
        } catch (Throwable e) {
            e.printStackTrace(System.out);
        }
    }

    private static void dumpList(String label, Object list) throws ReflectiveOperationException {
        if (list == null) {
            System.out.println(label + "_COUNT=0");
            return;
        }

        Method getFieldCount = list.getClass().getMethod("getFieldCount");
        int fieldCount = (Integer) getFieldCount.invoke(list);
        System.out.println(label + "_COUNT=" + fieldCount);

        Object metaData = list.getClass().getMethod("getMetaData").invoke(list);
        for (int i = 0; i < fieldCount; i++) {
            System.out.println(label + "_PARAM name=" + invoke(metaData, "getName", i)
                + " type=" + invoke(metaData, "getTypeAsString", i)
                + " len=" + invoke(metaData, "getLength", i)
                + " decimals=" + invoke(metaData, "getDecimals", i));
        }

        for (Object field : toIterable(list)) {
            boolean isTable = (Boolean) field.getClass().getMethod("isTable").invoke(field);
            if (isTable) {
                Object table = field.getClass().getMethod("getTable").invoke(field);
                System.out.println(label + "_TABLE " + field.getClass().getMethod("getName").invoke(field)
                    + " rows=" + table.getClass().getMethod("getNumRows").invoke(table));
            }
        }
    }

    private static Object invoke(Object target, String methodName, int index)
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return target.getClass().getMethod(methodName, int.class).invoke(target, index);
    }

    private static Iterable<Object> toIterable(Object target) {
        if (target instanceof Iterable<?> iterable) {
            return () -> (java.util.Iterator<Object>) iterable.iterator();
        }

        if (target.getClass().isArray()) {
            return () -> new java.util.Iterator<>() {
                private int index;

                @Override
                public boolean hasNext() {
                    return index < Array.getLength(target);
                }

                @Override
                public Object next() {
                    return Array.get(target, index++);
                }
            };
        }

        throw new IllegalStateException("Parameter list is not iterable: " + target.getClass().getName());
    }

    static class InMemoryDestinationDataProvider {
        private final String name;
        private final Properties props;
        private final Object proxyInstance;

        InMemoryDestinationDataProvider(
            Class<?> destinationDataProvider,
            Class<?> environment,
            String name,
            Properties props
        ) throws ReflectiveOperationException {
            this.name = name;
            this.props = props;
            this.proxyInstance = java.lang.reflect.Proxy.newProxyInstance(
                destinationDataProvider.getClassLoader(),
                new Class<?>[] {destinationDataProvider},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getDestinationProperties" -> name.equals(args[0]) ? props : null;
                    case "setDestinationDataEventListener" -> null;
                    case "supportsEvents" -> false;
                    default -> throw new UnsupportedOperationException(method.getName());
                }
            );
            environment.getMethod("registerDestinationDataProvider", destinationDataProvider).invoke(null, proxyInstance);
        }
    }
}
