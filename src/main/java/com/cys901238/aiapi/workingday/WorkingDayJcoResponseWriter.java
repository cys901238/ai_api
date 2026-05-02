package com.cys901238.aiapi.workingday;

import com.cys901238.aiapi.jco.JcoFunctionIo;
import com.cys901238.aiapi.jco.JcoTableIo;
import org.springframework.stereotype.Component;

@Component
public class WorkingDayJcoResponseWriter {
    public void write(JcoFunctionIo functionIo, WorkingDayAiResponse response) {
        functionIo.setExportString("EV_STATUS", blankSafe(response.status()));
        functionIo.setExportString("EV_MESSAGE", blankSafe(response.message()));
        functionIo.setExportString("EV_TRACE_ID", blankSafe(response.traceId()));
        functionIo.setExportString("EV_WORKING_DAY", blankSafe(response.workingDay()));
        functionIo.setExportDouble("EV_CONFIDENCE", response.confidence() == null ? 0.0 : response.confidence());

        JcoTableIo returnTable = functionIo.table(ZppGetWorkingDayAiContract.RETURN_TABLE_NAME);
        returnTable.clear();
        for (WorkingDayAiResponse.ReturnMessage message : response.returnMessages()) {
            returnTable.appendRow();
            returnTable.setValue("TYPE", blankSafe(message.type()));
            returnTable.setValue("ID", blankSafe(message.id()));
            returnTable.setValue("NUMBER", blankSafe(message.number()));
            returnTable.setValue("MESSAGE", blankSafe(message.message()));
            returnTable.setValue("MESSAGE_V1", blankSafe(message.messageV1()));
            returnTable.setValue("MESSAGE_V2", blankSafe(message.messageV2()));
            returnTable.setValue("MESSAGE_V3", blankSafe(message.messageV3()));
            returnTable.setValue("MESSAGE_V4", blankSafe(message.messageV4()));
        }
    }

    private String blankSafe(String value) {
        return value == null ? "" : value;
    }
}
