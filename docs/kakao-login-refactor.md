# 카카오 로그인 전면 리팩터링 — 변경 정리

작성일: 2026-07-24
범위: 백엔드(인증/토큰/HTTP 클라이언트/보안) + DB 스키마 + 프론트엔드 동기화

이 문서는 **무엇을 / 어떻게 / 왜** 바꿨는지 정리한 자료다. DB 스키마 상세는
[`migration/SCHEMA_CHANGES.md`](../migration/SCHEMA_CHANGES.md) 참고.

---

## 0. TL;DR

| 영역 | Before | After | 핵심 이유 |
|------|--------|-------|-----------|
| 계정 식별 | 카카오 로그인을 **email 로 매칭** → 같은 email 이면 남의 계정에 로그인될 위험 | **(provider, providerId)** 로 재방문 식별 + **email 전역 유일** + 최초 로그인 시 email 중복이면 "기존 계정 로그인 유도" | 계정 탈취·중복가입 방지 |
| 이메일 | 선택 | **필수** (미동의 시 재동의 안내) | email 을 유일 키로 사용 |
| Access Token | 의미 없이 **DB 평문 저장** | **미저장**(서명 검증 무상태) | 저장 불필요·유출면 축소 |
| Refresh Token | DB **평문 저장** | **SHA-256 해시** 저장 + 사용 시 **회전(rotation)** | 유출 대비 |
| 카카오 토큰 | `token.auth_access_token` 평문 | `social_account.access_token` 으로 이동 | 세션과 분리 |
| 로그아웃 | **버그로 실제 무효화 안 됨** | Refresh 폐기 + 카카오 로그아웃(best-effort) + 쿠키 삭제 | 진짜 로그아웃 |
| HTTP 클라이언트 | 손으로 감싼 `RestClient` 래퍼 3개 | **선언형 HTTP Interface(`@HttpExchange`)** | 스프링6 표준·간결·타입안전 |
| 엔드포인트 | 인증 API 가 `/user/**` 에 혼재 | `/auth/**` 로 분리 | 책임 분리 |

검증: 백엔드 `./gradlew build` 성공 + **실제 부팅(컨텍스트 로드) 성공** + 엔드포인트 스모크테스트 통과, 프론트 `tsc --noEmit` 통과, DB 마이그레이션 적용/검증 완료.

---

## 1. 로그인 플로우 (Before → After)

기본 골격(프론트가 카카오 인가코드를 받아 백엔드로 전달)은 유지하되, 백엔드 처리를 재작성했다.

**After — 카카오 로그인**
```
[FE] 카카오 인가 → code 획득
  → POST /auth/kakao/login { code }
[BE] KakaoAuthService.login(code)
     1. (선언형) KakaoAuthClient.issueToken  → 카카오 access token
     2. (선언형) KakaoApiClient.getUser       → providerId, email ...
     3. email 없음        → 2003 KAKAO_EMAIL_REQUIRED (재동의 안내)
     4. (provider,providerId) 존재 → 로그인(토큰 최신화)
     5. 없고 email 이미 존재 → 2004 EMAIL_ALREADY_REGISTERED (기존 계정 로그인 유도)
     6. 없고 email 신규     → users + social_account 생성 후 로그인
     7. TokenService.issueTokens → Access(JSON) + Refresh(HttpOnly 쿠키)
```

**After — 재발급 / 로그아웃**
```
POST /auth/token/refresh   (Refresh 쿠키만) → 서명·만료·해시 검증 → 회전 후 재발급
POST /auth/logout          (Bearer 필요)   → Refresh 폐기 + 카카오 로그아웃(best-effort) + 쿠키 삭제
GET  /auth/oauth2/logout/kakao             → 카카오 SSO 로그아웃 페이지로 302
```

---

## 2. 엔드포인트 변경 (프론트 동기화 완료)

| 기능 | Before | After |
|------|--------|-------|
| 카카오 로그인 | `POST /user/callback` | `POST /auth/kakao/login` |
| 토큰 재발급 | `POST /user/refresh/token` | `POST /auth/token/refresh` |
| 로그아웃(REST) | `POST /user/logout/kakao` | `POST /auth/logout` |
| 카카오 SSO 로그아웃 | `GET /user/oauth2/logout/kakao` | `GET /auth/oauth2/logout/kakao` |
| 로컬 로그인/가입 | `POST /user/signin`, `/user/signup` | (유지) — signin 은 이제 Refresh 쿠키도 설정 |

프론트 반영: `src/api/login.ts`, `src/components/layout/Header/MainHeader.tsx`,
`src/components/layout/Navigation.tsx`, `src/components/features/login/KakaoRedirectContainer.tsx`.

---

## 3. 백엔드 변경 상세 (무엇을 / 왜)

### 3-1. 계정 식별 정책 — `KakaoAuthService`(신규)
- **무엇**: 이메일 기반 매칭(`createUserByKakao` = `findUserByEmail`) 제거. `(provider, providerId)` 로 재방문 식별, email 은 전역 유일 키, 최초 로그인 email 충돌 시 `EmailAlreadyRegisteredException`.
- **왜**: 기존 방식은 카카오 email 이 기존(예: 로컬가입) 계정과 같으면 **그 계정으로 로그인**되어 계정 탈취가 가능했다. 정책상 email 을 유일 키로 쓰되 자동 병합 대신 **기존 계정 로그인 유도**로 안전하게 처리.

### 3-2. 토큰 계층 — `TokenService`(재작성), `RefreshToken`/`RefreshTokenMapper`(신규)
- **무엇**: `issueTokens` / `reissue`(회전) / `deleteRefreshToken`. Access 는 저장 안 함. Refresh 는 **SHA-256 해시**를 `user_id` 1행으로 저장. 재발급은 **쿠키의 Refresh 만**으로 검증(서명+만료+해시 일치), 성공 시 회전. 해시 불일치/만료면 해당 사용자 Refresh 폐기.
- **왜**: 기존엔 access·refresh 를 **평문 저장**하고 access_token 으로 세션을 조회하는 안티패턴이었다. 무상태 JWT + Refresh 해시/회전이 표준이며 유출 위험을 크게 줄인다.

### 3-3. 로그아웃 — `AuthService`(재작성)
- **무엇**: `logout(userId)` = `social_account.access_token` 으로 카카오 로그아웃 시도(실패 무시) + Refresh 폐기. 컨트롤러는 Bearer 로 사용자 식별 후 쿠키 삭제.
- **왜**: 기존 `deleteTokenByAccessToken(authAccessToken)` 은 **앱 토큰이 아니라 카카오 토큰**을 넘겨 DB 행이 안 지워졌다 → 로그아웃 후에도 세션 유효. 실제 무효화되도록 수정.

### 3-4. HTTP 클라이언트 — 선언형 `@HttpExchange` 로 리뉴얼
- **무엇**: `RestClient` 를 손으로 감싼 `KakaoTokenHttpClient`/`KakaoUserHttpClient`/`TossPaymentClient` 를 제거하고, 인터페이스 기반 선언형 클라이언트로 교체.
  - `KakaoAuthClient`(토큰 발급), `KakaoApiClient`(사용자정보·로그아웃), `TossPaymentApi`(결제 승인)
  - `HttpClientConfig` 에서 `RestClientAdapter` + `HttpServiceProxyFactory` 로 프록시 빈 생성
  - 응답은 타입 레코드(`KakaoTokenResponse`, `KakaoUserResponse`)로 매핑
  - 토스는 인증 헤더(Basic)·에러(→`CustomException`) 매핑을 RestClient 설정으로 유지
- **왜**: 스프링6 기준 가장 트렌디한 방식. 보일러플레이트 제거, 타입 안전, 테스트 용이.

### 3-5. 보안/필터
- `WebSecurityConfig`: `/auth/**` 일괄 permitAll 제거 → 로그인/재발급/카카오SSO로그아웃만 permitAll, **`/auth/logout` 은 인증 필요**.
- `JwtAuthenticationFilter`: `shouldNotFilter` 에서 `/auth/logout` 제외(인증 필요), 삭제된 `findUserByAccessToken` 대신 `parseUserId` + `UserService` 사용.
- `TokenCookieManager`: `clearRefreshTokenCookie`(maxAge=0) 추가.

### 3-6. 로컬 로그인/가입
- `/user/signin` 이 이제 `issueTokens` 로 발급하고 **Refresh 쿠키를 설정**(기존엔 미설정이라 재발급 불가 버그가 있었음).

### 삭제된 파일
`KakaoTokenHttpClient`, `KakaoUserHttpClient`, `TossPaymentClient`, `Token`, `RefreshTokenRequest`, `LogoutRequest`, `UserFromKakao`, `SocialAccountResponse`, `TokenMapper(.java/.xml)`.

---

## 4. DB 변경 (적용 완료)

상세: [`migration/SCHEMA_CHANGES.md`](../migration/SCHEMA_CHANGES.md), up/down SQL 동봉.

1. `users.email` **UNIQUE**(`uk_users_email`)
2. `social_account` + `access_token`, `access_token_expires_at`
3. `refresh_token` **신규**(해시 저장, user_id UNIQUE)
4. `token` **제거**

> 적용 시 기존 세션 전부 무효화 → **전 사용자 재로그인 필요**. 적용 전 덤프 백업 권장.

---

## 5. 신규 API 스펙

### POST `/auth/kakao/login`
- Req: `{ "code": "<카카오 인가코드>" }`
- Res 200(성공): `{ success:true, code:"0000", data: LoginResponse }` + `Set-Cookie: refreshToken=…; HttpOnly`
- Res(정책): `2003`(이메일 미동의), `2004`(이미 가입된 이메일 → 기존 계정 로그인 유도)

### POST `/auth/token/refresh`
- Req: 본문 불필요. `refreshToken` 쿠키 필요(`withCredentials`)
- Res 200: `LoginResponse`(새 Access) + 회전된 Refresh 쿠키 / 실패: `4004`

### POST `/auth/logout`
- Header: `Authorization: Bearer <accessToken>`
- Res 200: `{ success:true }` + Refresh 쿠키 삭제 / 미인증: `401`(4003)

### GET `/auth/oauth2/logout/kakao`
- 302 → 카카오 SSO 로그아웃 → 완료 후 프론트 로그아웃 페이지 복귀

`LoginResponse` = `{ accessToken, provider, customerKey, username, email, phone, profileImage, zonecode, roadAddress, detailAddress, role }`

---

## 6. 검증 내역
- `./gradlew build -x test` 성공, **애플리케이션 부팅 성공**(컨텍스트 로드 = 전 빈/매퍼/프로퍼티 와이어링 정상).
- 스모크테스트: `/auth/token/refresh`(쿠키X)→4004, `/auth/logout`(BearerX)→401, `/auth/oauth2/logout/kakao`→302, `/auth/kakao/login`(더미)→선언형 클라이언트가 실제 카카오 호출.
- 프론트 `tsc --noEmit` 통과. 옛 엔드포인트 참조 0건.
- DB 마이그레이션 적용 후 스키마 검증 완료.

---

## 7. 후속 권장(이번 범위 밖)
- **시크릿 폐기/회수**: `application.yml` 에 JWT/AWS/카카오 시크릿이 기본값으로 커밋되어 있음(별도 보고). 회수·환경변수화 필요.
- **결제 금액 검증 부재**(`PaymentService.paymentVerification`)는 이번 리팩터링과 별개로 남아 있음 — 승인 전 저장금액 대비 검증 추가 필요.
- 카카오 `social_account.access_token` 은 현재 평문 저장 — 장기적으로 암호화 저장 권장.
- 전역 예외 처리기가 다수 에러를 HTTP 200 으로 반환하는 점은 유지(프론트 계약과 결합) — 추후 상태코드 정합화 검토.
