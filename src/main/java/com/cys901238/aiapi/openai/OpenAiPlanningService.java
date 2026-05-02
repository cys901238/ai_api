package com.cys901238.aiapi.openai;

import com.cys901238.aiapi.domain.PlanningAssistRequest;
import com.cys901238.aiapi.domain.PlanningAssistResponse;

public interface OpenAiPlanningService {
    PlanningAssistResponse generateRecommendation(PlanningAssistRequest request);
}
