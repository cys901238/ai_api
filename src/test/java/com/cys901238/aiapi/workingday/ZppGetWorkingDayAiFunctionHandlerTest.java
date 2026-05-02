package com.cys901238.aiapi.workingday;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ZppGetWorkingDayAiFunctionHandlerTest {
    private final StubWorkingDayAiPort port = new StubWorkingDayAiPort();
    private final ZppGetWorkingDayAiFunctionHandler handler = new ZppGetWorkingDayAiFunctionHandler(
        new WorkingDayJcoRequestMapper(),
        new WorkingDayAiService(port, new WorkingDayPromptComposer(), new WorkingDayResponseMapper()),
        new WorkingDayJcoResponseWriter(),
        new WorkingDayResponseMapper()
    );

    @Test
    void writesExportAndReturnValuesViaReflectiveJcoFunction() {
        port.result = new WorkingDayAiPortResult("20260507", 0.83, "근무일로 판단했습니다.");
        FakeJcoFunction function = new FakeJcoFunction();
        function.imports.values.put("IV_REQUEST_ID", "req-7");
        function.imports.values.put("IV_WERKS", "IS6");
        function.imports.values.put("IV_ARBPL", "WC-10");
        function.imports.values.put("IV_MATNR", "MAT-01");
        function.imports.values.put("IV_TARGET_DATE", "20260502");

        handler.handle(function);

        assertThat(function.exports.values.get("EV_STATUS")).isEqualTo("S");
        assertThat(function.exports.values.get("EV_WORKING_DAY")).isEqualTo("20260507");
        assertThat(function.exports.values.get("EV_TRACE_ID")).isEqualTo("req-7");
        assertThat(function.exports.values.get("EV_CONFIDENCE")).isEqualTo(0.83);
        assertThat(function.returnTable.rows).isEmpty();
    }

    @Test
    void writesWarningToReturnTableForInvalidNumericImport() {
        FakeJcoFunction function = new FakeJcoFunction();
        function.imports.values.put("IV_ORDER_QTY", "bad");

        handler.handle(function);

        assertThat(function.exports.values.get("EV_STATUS")).isEqualTo("W");
        assertThat(function.exports.values.get("EV_MESSAGE")).isEqualTo("IV_ORDER_QTY must be numeric.");
        assertThat(function.returnTable.rows).hasSize(1);
        assertThat(function.returnTable.rows.getFirst()).containsEntry("TYPE", "W");
        assertThat(function.returnTable.rows.getFirst()).containsEntry("MESSAGE", "IV_ORDER_QTY must be numeric.");
    }

    private static final class StubWorkingDayAiPort implements WorkingDayAiPort {
        private WorkingDayAiPortResult result;

        @Override
        public WorkingDayAiPortResult determineWorkingDay(WorkingDayAiRequest request, String systemPrompt, String userPrompt) {
            return result;
        }
    }

    public static final class FakeJcoFunction {
        private final FakeParameterList imports = new FakeParameterList();
        private final FakeParameterList exports = new FakeParameterList();
        private final FakeTableParameterList tables = new FakeTableParameterList();
        private final FakeTable returnTable = new FakeTable();

        public FakeJcoFunction() {
            tables.tables.put(ZppGetWorkingDayAiContract.RETURN_TABLE_NAME, returnTable);
        }

        public FakeParameterList getImportParameterList() {
            return imports;
        }

        public FakeParameterList getExportParameterList() {
            return exports;
        }

        public FakeTableParameterList getTableParameterList() {
            return tables;
        }
    }

    public static final class FakeParameterList {
        private final Map<String, Object> values = new HashMap<>();

        public String getString(String name) {
            Object value = values.get(name);
            return value == null ? null : value.toString();
        }

        public void setValue(String name, Object value) {
            values.put(name, value);
        }
    }

    public static final class FakeTableParameterList {
        private final Map<String, FakeTable> tables = new HashMap<>();

        public FakeTable getTable(String name) {
            return tables.get(name);
        }
    }

    public static final class FakeTable {
        private final List<Map<String, String>> rows = new ArrayList<>();
        private Map<String, String> currentRow;

        public void deleteAllRows() {
            rows.clear();
            currentRow = null;
        }

        public void appendRow() {
            currentRow = new LinkedHashMap<>();
            rows.add(currentRow);
        }

        public void setValue(Object value, String column) {
            currentRow.put(column, value == null ? null : value.toString());
        }
    }
}
