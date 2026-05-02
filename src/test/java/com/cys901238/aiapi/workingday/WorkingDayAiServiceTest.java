package com.cys901238.aiapi.workingday;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class WorkingDayAiServiceTest {
    private final CapturingWorkingDayAiPort port = new CapturingWorkingDayAiPort();
    private final WorkingDayAiService service = new WorkingDayAiService(
        port,
        new WorkingDayPromptComposer(),
        new WorkingDayResponseMapper()
    );

    @Test
    void returnsSuccessWhenPortProvidesWorkingDay() {
        port.result = new WorkingDayAiPortResult("20260508", 1.2, "가능한 근무일입니다.");

        WorkingDayAiResponse response = service.handle(new WorkingDayAiRequest(
            "req-1",
            "IS6",
            "WC-10",
            "MAT-01",
            "20260502",
            "KO",
            "A",
            100.0,
            null
        ));

        assertThat(response.status()).isEqualTo("S");
        assertThat(response.traceId()).isEqualTo("req-1");
        assertThat(response.workingDay()).isEqualTo("20260508");
        assertThat(response.confidence()).isEqualTo(1.0);
        assertThat(port.lastSystemPrompt).contains("Determine the most likely working day");
        assertThat(port.lastUserPrompt).contains("function: ZPP_GET_WORKING_DAY_AI");
        assertThat(port.lastRequest.model()).isEqualTo("gpt-4.1-mini");
    }

    @Test
    void returnsWarningForMissingRequiredInput() {
        WorkingDayAiResponse response = service.handle(new WorkingDayAiRequest(
            null,
            " ",
            null,
            "MAT-01",
            "2026-05-02",
            null,
            null,
            null,
            null
        ));

        assertThat(response.status()).isEqualTo("W");
        assertThat(response.message()).contains("IV_WERKS is required.");
        assertThat(response.message()).contains("IV_ARBPL is required.");
        assertThat(response.message()).contains("IV_TARGET_DATE must use YYYYMMDD.");
        assertThat(response.returnMessages()).hasSize(1);
    }

    @Test
    void returnsFailureWhenPlaceholderPortIsUsed() {
        WorkingDayAiService placeholderService = new WorkingDayAiService(
            new PlaceholderWorkingDayAiPort(),
            new WorkingDayPromptComposer(),
            new WorkingDayResponseMapper()
        );

        WorkingDayAiResponse response = placeholderService.handle(new WorkingDayAiRequest(
            "req-2",
            "IS6",
            "WC-10",
            "MAT-01",
            "20260502",
            "KO",
            "A",
            100.0,
            null
        ));

        assertThat(response.status()).isEqualTo("E");
        assertThat(response.message()).contains("OpenAI adapter is not configured");
    }

    private static final class CapturingWorkingDayAiPort implements WorkingDayAiPort {
        private WorkingDayAiPortResult result;
        private WorkingDayAiRequest lastRequest;
        private String lastSystemPrompt;
        private String lastUserPrompt;

        @Override
        public WorkingDayAiPortResult determineWorkingDay(WorkingDayAiRequest request, String systemPrompt, String userPrompt) {
            this.lastRequest = request;
            this.lastSystemPrompt = systemPrompt;
            this.lastUserPrompt = userPrompt;
            return result;
        }
    }
}
