package com.cys901238.aiapi.workingday;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;
import org.springframework.stereotype.Component;

@Component
public class WorkingDayPromptComposer {
    public String buildSystemPrompt() {
        return "You are an SAP production planning assistant. Determine the most likely working day for the given plant, work center, material, shift, and target date. "
            + "If the input is insufficient, return a conservative explanation instead of inventing a plant calendar. Respond with a short JSON object that includes workingDay, confidence, and message.";
    }

    public String buildUserPrompt(WorkingDayAiRequest request) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("function", ZppGetWorkingDayAiContract.FUNCTION_NAME);
        payload.put("requestId", request.requestId());
        payload.put("werks", request.werks());
        payload.put("arbpl", request.arbpl());
        payload.put("matnr", request.matnr());
        payload.put("targetDate", request.targetDate());
        payload.put("language", request.language());
        payload.put("shift", request.shift());
        payload.put("orderQty", request.orderQty());
        payload.put("model", request.model());

        StringJoiner lines = new StringJoiner("\n");
        lines.add("Use this SAP RFC payload:");
        payload.forEach((key, value) -> lines.add("- " + key + ": " + sanitize(value)));
        lines.add("Return JSON only.");
        lines.add("workingDay must be YYYYMMDD or null.");
        lines.add("confidence must be a number between 0 and 1.");
        lines.add("message must explain the decision briefly in the user's language when possible.");
        return lines.toString();
    }

    private String sanitize(Object value) {
        if (value == null) {
            return "null";
        }
        return value.toString().replace('\n', ' ').trim();
    }
}
