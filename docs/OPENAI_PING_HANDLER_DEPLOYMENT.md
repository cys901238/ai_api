# 독립형 OpenAI 핑 핸들러 배포

## 범위
- 생산계획 UI는 변경하지 않음
- 독립형 백엔드 핑 엔드포인트만 대상
- 배포 전까지 모든 환경 식별자는 플레이스홀더로 유지

## 핸들러 계약
### 지원 메서드
- `GET`
- `POST`
- `OPTIONS`

### 요청 예시
```http
GET /sap/bc/zpp_openai_ping?sap-client=<client>
```

### 성공 응답 예시
```json
{
  "ok": true,
  "endpoint": "/sap/bc/zpp_openai_ping",
  "requestMethod": "GET",
  "message": "OpenAI connectivity test succeeded.",
  "httpStatus": 200,
  "openaiStatus": 200,
  "openaiStatusText": "OK",
  "provider": "openai",
  "sapClient": "<client>",
  "responseBody": "{...raw OpenAI response...}",
  "timestampUtc": "2026-04-23T12:30:00.0000000"
}
```

### 실패 응답 예시
```json
{
  "ok": false,
  "message": "OpenAI API key is not configured.",
  "httpStatus": 500
}
```

## ADT 배포 절차
1. 대상 SAP 시스템과 클라이언트에 연결된 ADT를 엽니다.
2. 선택한 패키지에 ABAP 클래스 `ZCL_PP_OPENAI_PING`를 생성합니다.
3. 환경에 맞는 올바른 트랜스포트 요청을 지정합니다.
4. 인터페이스 `IF_HTTP_EXTENSION`를 추가합니다.
5. 클래스 본문에는 로컬 구현 소스를 반영합니다.
6. 클래스를 활성화하고 문법 오류가 없는지 확인합니다.
7. API 키 처리 로직은 안전한 조회 방식으로 교체합니다.

## SICF 배포 절차
1. 트랜잭션 `SICF`를 엽니다.
2. `default_host -> sap -> bc`로 이동합니다.
3. 서비스 `zpp_openai_ping`를 생성합니다.
4. 핸들러 클래스로 `ZCL_PP_OPENAI_PING`를 지정합니다.
5. BSP가 아닌 일반 HTTP 서비스로 유지합니다.
6. 로컬 인증 및 권한 정책을 적용합니다.
7. 서비스를 활성화합니다.
8. 브라우저나 REST 클라이언트에서 엔드포인트를 테스트합니다.

## 검증 체크리스트
- 클래스가 정상적으로 활성화됨
- SICF 노드가 활성 상태임
- `GET` 요청이 JSON을 반환함
- API 키가 없을 때 제어된 `500` JSON을 반환함
- 유효한 키가 있을 때 OpenAI 상위 응답 상태와 본문을 반환함

## 참고
- 외부 호출은 `GET https://api.openai.com/v1/models` 같은 최소 수준의 프로브로 충분합니다.
- 이 문서는 비즈니스 로직 연동이 아니라 연결성 점검을 위한 배포 가이드입니다.
