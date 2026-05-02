package com.cys901238.aiapi.workingday;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class WorkingDayPromptComposerTest {
    private final WorkingDayPromptComposer composer = new WorkingDayPromptComposer();

    @Test
    void buildsSecretSafePromptForZppGetWorkingDayAi() {
        WorkingDayAiRequest request = new WorkingDayAiRequest(
            "req-123",
            "IS6",
            "WC-10",
            "MAT-001",
            "20260502",
            "KO",
            "A",
            120.0,
            "gpt-4.1-mini"
        );

        String systemPrompt = composer.buildSystemPrompt();
        String userPrompt = composer.buildUserPrompt(request);

        assertThat(systemPrompt).contains("Determine the most likely working day");
        assertThat(userPrompt)
            .contains("function: ZPP_GET_WORKING_DAY_AI")
            .contains("- werks: IS6")
            .contains("- arbpl: WC-10")
            .contains("- targetDate: 20260502")
            .contains("workingDay must be YYYYMMDD or null")
            .doesNotContain("password")
            .doesNotContain("token");
    }

    @Test
    void stripsNewlinesFromPromptValues() {
        WorkingDayAiRequest request = new WorkingDayAiRequest(
            "req-123\nextra",
            "IS6",
            "WC-10",
            "MAT-001",
            "20260502",
            "KO",
            "A",
            120.0,
            "gpt-4.1-mini"
        );

        String userPrompt = composer.buildUserPrompt(request);

        assertThat(userPrompt).contains("- requestId: req-123 extra");
    }
}
