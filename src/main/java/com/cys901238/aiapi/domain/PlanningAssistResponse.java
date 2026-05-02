package com.cys901238.aiapi.domain;

import java.util.List;

public record PlanningAssistResponse(
    String summary,
    List<Suggestion> suggestions,
    List<String> risks
) {
    public record Suggestion(
        Integer lineNo,
        String actionType,
        String seqnr,
        String targetShift,
        Double targetQty,
        Double targetUph,
        String reasonCode,
        String reasonText,
        Double confidence
    ) {}
}
