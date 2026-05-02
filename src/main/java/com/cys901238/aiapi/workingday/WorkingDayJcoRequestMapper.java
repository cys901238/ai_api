package com.cys901238.aiapi.workingday;

import com.cys901238.aiapi.jco.JcoFunctionIo;
import org.springframework.stereotype.Component;

@Component
public class WorkingDayJcoRequestMapper {
    public WorkingDayAiRequest map(JcoFunctionIo functionIo) {
        return new WorkingDayAiRequest(
            normalize(functionIo.getImportString("IV_REQUEST_ID")),
            normalize(functionIo.getImportString("IV_WERKS")),
            normalize(functionIo.getImportString("IV_ARBPL")),
            normalize(functionIo.getImportString("IV_MATNR")),
            normalize(functionIo.getImportString("IV_TARGET_DATE")),
            normalize(functionIo.getImportString("IV_LANGUAGE")),
            normalize(functionIo.getImportString("IV_SHIFT")),
            parseOrderQty(functionIo.getImportString("IV_ORDER_QTY")),
            normalize(functionIo.getImportString("IV_MODEL"))
        );
    }

    private Double parseOrderQty(String rawValue) {
        String value = normalize(rawValue);
        if (value == null) {
            return null;
        }

        try {
            return Double.valueOf(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("IV_ORDER_QTY must be numeric.");
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.replace('\n', ' ').trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
