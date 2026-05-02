package com.cys901238.aiapi.workingday;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class WorkingDayResponseMapper {
    public WorkingDayAiResponse success(String traceId, String workingDay, Double confidence, String message) {
        return new WorkingDayAiResponse(
            "S",
            message,
            traceId,
            workingDay,
            confidence,
            List.of()
        );
    }

    public WorkingDayAiResponse insufficientInput(String traceId, String message) {
        return new WorkingDayAiResponse(
            "W",
            message,
            traceId,
            null,
            0.0,
            List.of(new WorkingDayAiResponse.ReturnMessage(
                "W",
                "ZPP_AI",
                "001",
                message,
                null,
                null,
                null,
                null
            ))
        );
    }

    public WorkingDayAiResponse failure(String traceId, String message) {
        return new WorkingDayAiResponse(
            "E",
            message,
            traceId,
            null,
            0.0,
            List.of(new WorkingDayAiResponse.ReturnMessage(
                "E",
                "ZPP_AI",
                "999",
                message,
                null,
                null,
                null,
                null
            ))
        );
    }
}
