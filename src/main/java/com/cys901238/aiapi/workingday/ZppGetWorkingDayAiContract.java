package com.cys901238.aiapi.workingday;

import java.util.List;

public final class ZppGetWorkingDayAiContract {
    public static final String FUNCTION_NAME = "ZPP_GET_WORKING_DAY_AI";

    public static final List<String> IMPORT_PARAMETERS = List.of(
        "IV_REQUEST_ID",
        "IV_WERKS",
        "IV_ARBPL",
        "IV_MATNR",
        "IV_TARGET_DATE",
        "IV_LANGUAGE",
        "IV_SHIFT",
        "IV_ORDER_QTY",
        "IV_MODEL"
    );

    public static final List<String> EXPORT_PARAMETERS = List.of(
        "EV_STATUS",
        "EV_MESSAGE",
        "EV_TRACE_ID",
        "EV_WORKING_DAY",
        "EV_CONFIDENCE"
    );

    public static final List<String> RETURN_TABLE_COLUMNS = List.of(
        "TYPE",
        "ID",
        "NUMBER",
        "MESSAGE",
        "MESSAGE_V1",
        "MESSAGE_V2",
        "MESSAGE_V3",
        "MESSAGE_V4"
    );

    private ZppGetWorkingDayAiContract() {}
}
