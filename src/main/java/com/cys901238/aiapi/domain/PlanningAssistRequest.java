package com.cys901238.aiapi.domain;

import java.util.List;

public record PlanningAssistRequest(
    String requestId,
    String scenario,
    String werks,
    String arbpl,
    String psttr,
    String language,
    Integer maxSuggestions,
    String model,
    List<PlanItem> planItems,
    List<CapacityItem> capacityItems
) {
    public record PlanItem(
        Integer seqnr,
        String matnr,
        String maktx,
        Double gsmng,
        Double uph,
        Double zuph,
        String shift,
        String obtype,
        String plnum,
        String berid,
        String verid,
        String lgpro
    ) {}

    public record CapacityItem(
        String shift,
        Double availableHours,
        Double loadHours,
        Double loadRate,
        Double toolChangeHours
    ) {}
}
