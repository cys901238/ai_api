package com.cys901238.aiapi.workingday;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.cys901238.aiapi.jco.JcoFunctionIo;
import com.cys901238.aiapi.jco.JcoTableIo;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class WorkingDayJcoRequestMapperTest {
    private final WorkingDayJcoRequestMapper mapper = new WorkingDayJcoRequestMapper();

    @Test
    void mapsJcoImportsIntoWorkingDayRequest() {
        FakeJcoFunctionIo functionIo = new FakeJcoFunctionIo();
        functionIo.imports.put("IV_REQUEST_ID", " req-1\n");
        functionIo.imports.put("IV_WERKS", "IS6");
        functionIo.imports.put("IV_ARBPL", "WC-10");
        functionIo.imports.put("IV_MATNR", "MAT-01");
        functionIo.imports.put("IV_TARGET_DATE", "20260502");
        functionIo.imports.put("IV_LANGUAGE", "KO");
        functionIo.imports.put("IV_SHIFT", "B");
        functionIo.imports.put("IV_ORDER_QTY", "123.45");
        functionIo.imports.put("IV_MODEL", "gpt-4.1-mini");

        WorkingDayAiRequest request = mapper.map(functionIo);

        assertThat(request.requestId()).isEqualTo("req-1");
        assertThat(request.orderQty()).isEqualTo(123.45);
        assertThat(request.shift()).isEqualTo("B");
    }

    @Test
    void rejectsNonNumericOrderQuantity() {
        FakeJcoFunctionIo functionIo = new FakeJcoFunctionIo();
        functionIo.imports.put("IV_ORDER_QTY", "abc");

        assertThatThrownBy(() -> mapper.map(functionIo))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("IV_ORDER_QTY must be numeric.");
    }

    private static final class FakeJcoFunctionIo implements JcoFunctionIo {
        private final Map<String, String> imports = new HashMap<>();

        @Override
        public String getImportString(String name) {
            return imports.get(name);
        }

        @Override
        public void setExportString(String name, String value) {}

        @Override
        public void setExportDouble(String name, Double value) {}

        @Override
        public JcoTableIo table(String name) {
            throw new UnsupportedOperationException();
        }
    }
}
