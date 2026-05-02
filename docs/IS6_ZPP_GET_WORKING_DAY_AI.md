# IS6 `ZPP_GET_WORKING_DAY_AI` 통합 초안

## 목적
IS6 생산계획 시나리오에서 SAP RFC `ZPP_GET_WORKING_DAY_AI`가 Java 미들웨어를 통해 OpenAI에 질의하고, 결과를 다시 SAP 형식으로 돌려주는 최소 계약을 정리합니다.

## 현재 코드 반영 내용
- RFC 이름과 파라미터 이름을 `ZppGetWorkingDayAiContract` 상수로 고정
- RFC 입력을 OpenAI 프롬프트로 바꾸는 `WorkingDayPromptComposer` 추가
- OpenAI 판단 결과를 SAP export 및 `ET_RETURN` 스타일로 정리하는 `WorkingDayResponseMapper` 추가

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
1. 실제 JCo function handler에서 import 값을 `WorkingDayAiRequest`로 매핑
2. OpenAI 구조화 출력 스키마를 근무일 전용으로 추가
3. `WorkingDayAiResponse`를 JCo export/table 파라미터에 기록
