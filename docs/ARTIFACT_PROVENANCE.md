# 산출물 출처

이 저장소는 최근의 로컬 SAP RFC, JCo, OpenAI 개발 작업물을 바탕으로 구성했으며, 공개를 위해 민감 정보를 정리한 버전입니다.

## 포함된 원본 자료
- `sjg_abap_planning_webo/docs/SAP_OPENAI_JCO_ARCHITECTURE.md`
- `sjg_abap_planning_webo/middleware/openai-jco-bridge/README.md`
- `sjg_abap_planning_webo/middleware/openai-jco-bridge/pom.xml`
- `sjg_abap_planning_webo/middleware/openai-jco-bridge/src/main/resources/planning-assistant-response.schema.json`
- `sjg_abap_planning_webo/middleware/openai-jco-bridge/src/main/resources/application.yml` (정제 후 템플릿으로 전환)
- `zpp-plan-seq/zpp-plan-seq/docs/openai-ping-adt-sicf-deployment-ds4-client400.md` (일반화 및 정제 적용)
- `JcoFunctionCheck.java` (비밀정보가 없는 템플릿으로 재작성)

## 적용한 정제 기준
- 환경 종속적인 실제 SAP 호스트, 사용자, 비밀번호, 클라이언트, 대상 시스템명은 제거했습니다.
- 트랜스포트 번호와 내부 URL은 제거했습니다.
- 아키텍처, 인터페이스 계약, 스타터 구조는 유지했습니다.
- 로컬 실행 설정은 공개 가능한 플레이스홀더만 남겼습니다.
