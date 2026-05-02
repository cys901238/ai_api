package com.cys901238.aiapi.workingday;

public record WorkingDayAiPortResult(
    String workingDay,
    Double confidence,
    String message
) {}
