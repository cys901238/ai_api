# SAP RFC -> Java JCo 미들웨어 -> OpenAI -> SAP RFC

## 목표
SAP가 RFC로 생산계획 컨텍스트를 Java 미들웨어에 전달하고, 미들웨어가 OpenAI를 호출한 뒤 결과를 SAP 응답 구조에 다시 매핑하는 표준 아키텍처를 정의합니다.

## 권장 흐름
1. SAP ABAP RFC 호출부가 `Z_OPENAI_PLAN_REQUEST`를 실행합니다.
2. Java JCo 서버가 RFC import 파라미터와 table 파라미터를 수신합니다.
3. 미들웨어가 SAP 페이로드를 `PlanningAssistRequest`로 정규화합니다.
4. 프롬프트 빌더가 OpenAI 요청을 생성합니다.
5. OpenAI가 구조화된 JSON을 반환합니다.
6. 응답 매퍼가 결과를 SAP export 구조와 table 구조로 변환합니다.
7. JCo 서버가 RFC 응답을 SAP로 반환합니다.
8. 오류는 표준 SAP 반환 테이블 형식으로 정규화합니다.

## 논리 구성 요소
- SAP ABAP 호출부 및 후속 저장 로직
- Java JCo 서버 엔드포인트
- 요청 및 응답 매퍼
- OpenAI 오케스트레이션 서비스
- 검증, 타임아웃, 재시도, 감사 로그

## RFC 계약 초안
### Import
- `IV_REQUEST_ID` CHAR36
- `IV_SCENARIO` CHAR30
- `IV_WERKS` WERKS_D
- `IV_ARBPL` ARBPL
- `IV_PSTTR` DATS
- `IV_LANGUAGE` LANGU
- `IV_MAX_SUGGESTIONS` INT4
- `IV_MODEL` CHAR40

### Tables
#### `IT_PLAN_ITEMS`
- `SEQNR`
- `MATNR`
- `MAKTX`
- `GSMNG`
- `UPH`
- `ZUPH`
- `SHIFT`
- `OBTYPE`
- `PLNUM`
- `BERID`
- `VERID`
- `LGPRO`

#### `IT_CAPACITY`
- `SHIFT`
- `AVAILABLE_HOURS`
- `LOAD_HOURS`
- `LOAD_RATE`
- `TOOL_CHANGE_HOURS`

### Export
- `EV_STATUS` (`S`, `E`, `W`)
- `EV_MESSAGE`
- `EV_TRACE_ID`

### Return tables
#### `ET_SUGGESTIONS`
- `LINE_NO`
- `ACTION_TYPE`
- `SEQNR`
- `TARGET_SHIFT`
- `TARGET_QTY`
- `TARGET_UPH`
- `REASON_CODE`
- `REASON_TEXT`
- `CONFIDENCE`

#### `ET_RETURN`
- `TYPE`
- `ID`
- `NUMBER`
- `MESSAGE`
- `LOG_NO`
- `LOG_MSG_NO`
- `MESSAGE_V1` to `MESSAGE_V4`

## 비기능 가이드
- SAP -> 미들웨어 타임아웃: 약 15초
- 미들웨어 -> OpenAI 타임아웃: 약 10초
- 재시도는 OpenAI 429 및 5xx에만 적용
- 비밀정보는 소스가 아니라 환경변수 또는 Vault에 보관
- 프롬프트에는 불필요한 개인정보를 포함하지 않음

## 권장 개발 순서
1. SAP FM과 DDIC 구조를 확정합니다.
2. Java JCo 서버 스켈레톤을 시작합니다.
3. OpenAI 모의 응답으로 RFC 왕복을 검증합니다.
4. 구조화 출력 스키마를 연결합니다.
5. SAP 결과 처리 로직을 연결합니다.
6. 타임아웃, 처리량, 오류 테스트를 수행합니다.
