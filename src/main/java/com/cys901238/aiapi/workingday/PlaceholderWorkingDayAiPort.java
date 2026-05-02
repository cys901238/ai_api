package com.cys901238.aiapi.workingday;

import org.springframework.stereotype.Component;

@Component
public class PlaceholderWorkingDayAiPort implements WorkingDayAiPort {
    @Override
    public WorkingDayAiPortResult determineWorkingDay(WorkingDayAiRequest request, String systemPrompt, String userPrompt) {
        throw new UnsupportedOperationException("No OpenAI adapter is configured for ZPP_GET_WORKING_DAY_AI.");
    }
}
