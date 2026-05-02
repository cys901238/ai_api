package com.cys901238.aiapi.workingday;

import java.util.List;

public record WorkingDayAiResponse(
    String status,
    String message,
    String traceId,
    String workingDay,
    Double confidence,
    List<ReturnMessage> returnMessages
) {
    public record ReturnMessage(
        String type,
        String id,
        String number,
        String message,
        String messageV1,
        String messageV2,
        String messageV3,
        String messageV4
    ) {}
}
