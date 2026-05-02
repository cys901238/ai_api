# IS6 `ZPP_GET_WORKING_DAY_AI` 통합 초안

## 목적
IS6 생산계획 시나리오에서 SAP RFC `ZPP_GET_WORKING_DAY_AI`가 Java 미들웨어를 통해 OpenAI에 질의하고, 결과를 다시 SAP 형식으로 돌려주는 최소 계약을 정리합니다.

## 현재 코드 반영 내용
- RFC 이름과 파라미터 이름을 `ZppGetWorkingDayAiContract` 상수로 고정
- RFC 입력을 OpenAI 프롬프트로 바꾸는 `WorkingDayPromptComposer` 추가
- OpenAI 판단 결과를 SAP export 및 `ET_RETURN` 스타일로 정리하는 `WorkingDayResponseMapper` 추가
- Spring 관리 서비스 `WorkingDayAiService` 추가
- JCo import 매핑용 `WorkingDayJcoRequestMapper`, export/table 기록용 `WorkingDayJcoResponseWriter` 추가
- 실제 SAP JCo 타입 없이도 테스트 가능한 reflection 기반 `ZppGetWorkingDayAiFunctionHandler` 추가
- 비밀값 없이 동작하는 `PlaceholderWorkingDayAiPort` 추가

## 제안 RFC 계약
### Import
- `IV_REQUEST_ID`: 요청 추적 ID
- `IV_WERKS`: 플랜트, 예: `IS6`
- `IV_ARBPL`: 작업장
- `IV_MATNR`: 자재 코드
- `IV_TARGET_DATE`: 기준일, `YYYYMMDD`
- `IV_LANGUAGE`: 응답 언어
- `IV_SHIFT`: 교대
- `IV_ORDER_QTY`: 생산 수량
- `IV_MODEL`: 선택 모델명, 비우면 미들웨어 기본값 사용

### Export
- `EV_STATUS`: `S`, `W`, `E`
- `EV_MESSAGE`: 짧은 설명
- `EV_TRACE_ID`: 미들웨어 추적 ID
- `EV_WORKING_DAY`: 판단된 근무일, `YYYYMMDD`
- `EV_CONFIDENCE`: 0~1

### Return table
`ET_RETURN`은 SAP 표준 메시지 형식으로 경고나 오류를 전달합니다.

## 프롬프트 안전 원칙
- SAP 접속 정보, 사용자, 비밀번호, 토큰은 프롬프트에 포함하지 않음
- 입력값이 부족하면 근무일을 추정해서 꾸며내지 않고 `W` 상태로 반환
- 응답은 JSON으로 제한해서 후속 매핑을 단순화

## 다음 연결 포인트
1. 실제 JCo 서버 콜백에서 `ZppGetWorkingDayAiFunctionHandler#handle(Object)`를 연결
2. `WorkingDayAiPort`를 내부 OpenAI 어댑터로 대체하고 구조화 응답 파싱을 추가
3. 운영 기준에 맞는 RFC 메시지 ID/번호 체계를 `WorkingDayResponseMapper`에 반영

## 현재 처리 흐름
1. JCo import 파라미터를 `WorkingDayJcoRequestMapper`가 `WorkingDayAiRequest`로 변환
2. `WorkingDayAiService`가 필수 입력을 최소 검증하고 프롬프트를 구성
3. 서비스가 `WorkingDayAiPort`를 호출
4. 결과를 `WorkingDayJcoResponseWriter`가 `EV_*` export와 `ET_RETURN` table에 기록

## 저장소 안전성 메모
- 기본 Maven 빌드는 proprietary `sapjco3` 없이 가능하도록 유지
- 실제 SAP JCo 의존성은 내부 환경에서만 `-Pwith-sapjco` 프로필로 추가
- placeholder OpenAI 포트는 비밀값이나 외부 호출 없이 명시적 오류만 반환
