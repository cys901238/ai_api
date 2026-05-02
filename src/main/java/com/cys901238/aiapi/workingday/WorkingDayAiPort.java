package com.cys901238.aiapi.workingday;

public interface WorkingDayAiPort {
    WorkingDayAiPortResult determineWorkingDay(WorkingDayAiRequest request, String systemPrompt, String userPrompt);
}
