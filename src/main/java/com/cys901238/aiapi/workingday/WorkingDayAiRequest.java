package com.cys901238.aiapi.workingday;

public record WorkingDayAiRequest(
    String requestId,
    String werks,
    String arbpl,
    String matnr,
    String targetDate,
    String language,
    String shift,
    Double orderQty,
    String model
) {}
