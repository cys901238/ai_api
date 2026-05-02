package com.cys901238.aiapi.workingday;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class WorkingDayResponseMapperTest {
    private final WorkingDayResponseMapper mapper = new WorkingDayResponseMapper();

    @Test
    void createsSuccessResponse() {
        WorkingDayAiResponse response = mapper.success("trace-1", "20260505", 0.82, "근무일 가능성이 높습니다.");

        assertThat(response.status()).isEqualTo("S");
        assertThat(response.workingDay()).isEqualTo("20260505");
        assertThat(response.confidence()).isEqualTo(0.82);
        assertThat(response.returnMessages()).isEmpty();
    }

    @Test
    void createsWarningForInsufficientInput() {
        WorkingDayAiResponse response = mapper.insufficientInput("trace-2", "작업장 달력 정보가 부족합니다.");

        assertThat(response.status()).isEqualTo("W");
        assertThat(response.workingDay()).isNull();
        assertThat(response.returnMessages()).singleElement().satisfies(msg -> {
            assertThat(msg.type()).isEqualTo("W");
            assertThat(msg.id()).isEqualTo("ZPP_AI");
        });
    }

    @Test
    void createsErrorResponse() {
        WorkingDayAiResponse response = mapper.failure("trace-3", "OpenAI 응답 파싱 실패");

        assertThat(response.status()).isEqualTo("E");
        assertThat(response.returnMessages()).singleElement().satisfies(msg -> {
            assertThat(msg.type()).isEqualTo("E");
            assertThat(msg.number()).isEqualTo("999");
        });
    }
}
