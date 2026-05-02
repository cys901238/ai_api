package com.cys901238.aiapi.jco;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoMetaData;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoRepository;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.Environment;
import java.util.Properties;

public class JcoFunctionCheckTemplate {
    public static void main(String[] args) {
        String functionName = args.length > 0 ? args[0] : "YOUR_FUNCTION_NAME";
        Properties properties = new Properties();
        properties.setProperty(DestinationDataProvider.JCO_ASHOST, "YOUR_SAP_HOST");
        properties.setProperty(DestinationDataProvider.JCO_SYSNR, "00");
        properties.setProperty(DestinationDataProvider.JCO_CLIENT, "000");
        properties.setProperty(DestinationDataProvider.JCO_USER, "YOUR_USER");
        properties.setProperty(DestinationDataProvider.JCO_PASSWD, "YOUR_PASSWORD");
        properties.setProperty(DestinationDataProvider.JCO_LANG, "EN");
        properties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, "2");
        properties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, "1");

        try {
            new InMemoryDestinationDataProvider("TMP_DEST", properties);
            JCoDestination destination = JCoDestinationManager.getDestination("TMP_DEST");
            destination.ping();
            System.out.println("PING_OK");
            JCoRepository repository = destination.getRepository();
            JCoFunction function = repository.getFunction(functionName);
            if (function == null) {
                System.out.println("FUNCTION_NULL");
                return;
            }
            dumpList("IMPORT", function.getImportParameterList());
            dumpList("EXPORT", function.getExportParameterList());
            dumpList("CHANGING", function.getChangingParameterList());
            dumpList("TABLE", function.getTableParameterList());
        } catch (Throwable e) {
            e.printStackTrace(System.out);
        }
    }

    private static void dumpList(String label, JCoParameterList list) {
        System.out.println(label + "_COUNT=" + (list == null ? 0 : list.getFieldCount()));
        if (list == null) {
            return;
        }
        JCoMetaData metaData = list.getMetaData();
        for (int i = 0; i < list.getFieldCount(); i++) {
            System.out.println(label + "_PARAM name=" + metaData.getName(i)
                + " type=" + metaData.getTypeAsString(i)
                + " len=" + metaData.getLength(i)
                + " decimals=" + metaData.getDecimals(i));
        }
        for (JCoField field : list) {
            if (field.isTable()) {
                JCoTable table = field.getTable();
                System.out.println(label + "_TABLE " + field.getName() + " rows=" + table.getNumRows());
            }
        }
    }

    static class InMemoryDestinationDataProvider implements DestinationDataProvider {
        private final String name;
        private final Properties props;

        InMemoryDestinationDataProvider(String name, Properties props) {
            this.name = name;
            this.props = props;
            Environment.registerDestinationDataProvider(this);
        }

        @Override
        public Properties getDestinationProperties(String destinationName) {
            return name.equals(destinationName) ? props : null;
        }

        @Override
        public void setDestinationDataEventListener(DestinationDataEventListener listener) {}

        @Override
        public boolean supportsEvents() {
            return false;
        }
    }
}
