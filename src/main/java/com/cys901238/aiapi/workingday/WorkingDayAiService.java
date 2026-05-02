package com.cys901238.aiapi.workingday;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class WorkingDayAiService {
    static final String DEFAULT_LANGUAGE = "KO";
    static final String DEFAULT_MODEL = "gpt-4.1-mini";

    private static final Pattern SAP_DATE_PATTERN = Pattern.compile("\\d{8}");

    private final WorkingDayAiPort workingDayAiPort;
    private final WorkingDayPromptComposer promptComposer;
    private final WorkingDayResponseMapper responseMapper;

    public WorkingDayAiService(
        WorkingDayAiPort workingDayAiPort,
        WorkingDayPromptComposer promptComposer,
        WorkingDayResponseMapper responseMapper
    ) {
        this.workingDayAiPort = workingDayAiPort;
        this.promptComposer = promptComposer;
        this.responseMapper = responseMapper;
    }

    public WorkingDayAiResponse handle(WorkingDayAiRequest request) {
        WorkingDayAiRequest normalizedRequest = normalize(request);
        String traceId = traceIdFor(normalizedRequest);
        List<String> validationIssues = validate(normalizedRequest);
        if (!validationIssues.isEmpty()) {
            return responseMapper.insufficientInput(traceId, String.join(" ", validationIssues));
        }

        try {
            WorkingDayAiPortResult result = workingDayAiPort.determineWorkingDay(
                normalizedRequest,
                promptComposer.buildSystemPrompt(),
                promptComposer.buildUserPrompt(normalizedRequest)
            );

            if (result == null || !hasText(result.workingDay()) || !SAP_DATE_PATTERN.matcher(result.workingDay().trim()).matches()) {
                return responseMapper.failure(traceId, "OpenAI adapter returned an invalid working-day payload.");
            }

            return responseMapper.success(
                traceId,
                result.workingDay().trim(),
                normalizeConfidence(result.confidence()),
                defaultIfBlank(result.message(), "Working day determined.")
            );
        } catch (UnsupportedOperationException exception) {
            return responseMapper.failure(traceId, "OpenAI adapter is not configured for ZPP_GET_WORKING_DAY_AI.");
        } catch (RuntimeException exception) {
            return responseMapper.failure(traceId, "Working-day AI processing failed.");
        }
    }

    private WorkingDayAiRequest normalize(WorkingDayAiRequest request) {
        return new WorkingDayAiRequest(
            trimToNull(request.requestId()),
            trimToNull(request.werks()),
            trimToNull(request.arbpl()),
            trimToNull(request.matnr()),
            trimToNull(request.targetDate()),
            defaultIfBlank(request.language(), DEFAULT_LANGUAGE),
            trimToNull(request.shift()),
            request.orderQty(),
            defaultIfBlank(request.model(), DEFAULT_MODEL)
        );
    }

    private String traceIdFor(WorkingDayAiRequest request) {
        return hasText(request.requestId()) ? request.requestId() : "wd-" + UUID.randomUUID();
    }

    private List<String> validate(WorkingDayAiRequest request) {
        List<String> issues = new ArrayList<>();
        if (!hasText(request.werks())) {
            issues.add("IV_WERKS is required.");
        }
        if (!hasText(request.arbpl())) {
            issues.add("IV_ARBPL is required.");
        }
        if (!hasText(request.targetDate())) {
            issues.add("IV_TARGET_DATE is required.");
        } else if (!SAP_DATE_PATTERN.matcher(request.targetDate()).matches()) {
            issues.add("IV_TARGET_DATE must use YYYYMMDD.");
        }
        return issues;
    }

    private Double normalizeConfidence(Double confidence) {
        if (confidence == null) {
            return 0.0;
        }
        return Math.max(0.0, Math.min(1.0, confidence));
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return hasText(value) ? value.trim() : defaultValue;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
