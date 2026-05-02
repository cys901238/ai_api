# ai_api

정제된 SAP RFC -> Java JCo -> OpenAI 스타터 저장소입니다.

## 포함된 내용
- RFC에서 OpenAI까지 이어지는 아키텍처 노트
- IS6 `ZPP_GET_WORKING_DAY_AI`용 secret-safe 계약 초안과 프롬프트/응답 매핑 코드
- 최소 구성의 Spring Boot + Maven 미들웨어 스켈레톤
- 구조화된 OpenAI 응답용 JSON 스키마
- 독립형 SAP HTTP 핑 엔드포인트를 위한 정제된 배포 메모
- 민감 정보가 제거된 JCo 함수 점검 유틸리티 템플릿

## 의도적으로 제외한 내용
- 실제 SAP 호스트, 사용자, 비밀번호, 클라이언트, 트랜스포트 번호
- 실제 OpenAI API 키 또는 비밀값 조회 코드
- 독점 바이너리인 `sapjco3.jar` 및 네이티브 라이브러리
- 내부 엔드포인트가 포함된 환경별 마크다운 문서

## 권장 디렉터리 구성
- `docs/`: 설계 및 배포 관련 문서
- `src/main/java/`: 미들웨어 스타터 클래스
- `src/main/resources/`: 설정 템플릿과 응답 스키마

## 빠른 시작
1. Java 21과 Maven을 설치합니다.
2. 승인된 내부 절차에 따라 SAP JCo 라이브러리를 준비합니다.
3. `src/main/resources/application.example.yml`을 로컬 실행용 설정 파일로 복사합니다.
4. SAP 게이트웨이와 OpenAI 설정의 플레이스홀더 값을 실제 환경에 맞게 채웁니다.
5. OpenAI 클라이언트와 JCo 서버 연동 코드를 구현합니다.

## 참고
- `docs/IS6_ZPP_GET_WORKING_DAY_AI.md`에 IS6 근무일 판단 RFC 초안을 정리했습니다.
- 이 저장소는 공개를 전제로 안전하게 정리되어 있습니다. 연결 정보나 비밀값은 제거하고, 설계 방향과 스타터 코드는 유지했습니다.
