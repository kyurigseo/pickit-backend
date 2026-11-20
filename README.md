# PICKIT-BE

멋쟁이사자처럼 대학 14기 4호선톤 22팀 **PICKIT** 백엔드 레포지토리입니다.

## 📌 서비스 소개

**PICKIT**은 팝업스토어 현장에서 굿즈를 바로 교환할 수 있는 위치 기반 굿즈 교환 서비스입니다.

- **팝업 둘러보기**: 진행 중인 팝업을 검색·필터로 찾고 찜하기
- **위치 인증**: 팝업 근처에서 한 번 인증하면 주변 팝업이 모두 인증
- **LIVE 교환**: 현장에서 쓴 교환글은 LIVE로, 현장을 떠나면 일반 교환으로
- **채팅·교환 약속**: 채팅으로 약속을 잡고, 교환 후 후기 남기기

## 🛠 기술 스택

- **Language**: Java 21
- **Framework**: Spring Boot 4.1, Spring Data JPA, Spring Security
- **Auth**: JWT, Google 로그인
- **Realtime**: WebSocket (STOMP)
- **Database**: MySQL 8.0 (테스트: H2)
- **Infra**: AWS EC2 · ECR, Docker, Nginx, GitHub Actions

## 📦 실행 방법

1. MySQL에 `pickit` 데이터베이스와 `pickit_app` 계정을 만듭니다.
2. 환경변수 `DB_PASSWORD`를 설정합니다. (IntelliJ: Run → Edit Configurations → Environment variables)
3. 서버를 실행합니다.

```bash
./gradlew bootRun
```

- 테이블은 실행할 때 자동으로 만들어지고, 팝업 데이터는 `src/main/resources/db/manual/`의 SQL로 넣습니다.
- 팝업 사진은 Git에 올리지 않고 `uploads/popups/`에 둡니다.
- 테스트: `./gradlew test`

## 📁 프로젝트 구조

```
src/main/java/com/pickit/backend
├── auth          # 회원가입, 로그인, 구글 로그인, JWT
├── user          # 마이페이지, 프로필
├── popup         # 팝업 목록·검색·필터·상세·찜
├── verification  # 위치 인증, 위치 갱신
├── category      # 카테고리
├── interest      # 관심사
├── post          # 교환글, 교환글 찜
├── chat          # 채팅방, 메시지
├── appointment   # 교환 약속, 후기
├── common        # 공통 응답, 예외 처리
└── config        # Security, CORS, WebSocket 설정
```

## 🚀 배포

`main` 브랜치에 머지되면 GitHub Actions가 Docker 이미지를 만들어 EC2에 자동 배포합니다.

- API 서버: https://api.kyooo.shop

## 🌿 Git 컨벤션

**브랜치**

- `main`: 배포
- `develop`: 개발 통합
- `feat/{이슈번호}-{기능}`: 기능 개발 (예: `feat/72-popup-like-status`)

이슈 생성 → 브랜치 작업 → `develop`으로 PR → `main` 머지 시 배포

**커밋**

```
feat: 새로운 기능
fix: 버그 수정
refactor: 구조 변경
chore: 설정, 데이터 등 기타 작업
```

예: `feat: 팝업 상세·내 현장 팝업에 찜 여부 추가(#72)`

## 👥 팀원 (BE)

| GitHub | 담당 |
| --- | --- |
| [khxxn](https://github.com/khxxn) | 팝업, 위치 인증, 카테고리, 관심사, 서버 배포 |
| [kyurigseo](https://github.com/kyurigseo) | 회원·로그인, 교환글, 채팅, 교환 약속 |
