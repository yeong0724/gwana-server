# DB 스키마 변경 정리 — 카카오 로그인 리팩터링 (2026-07-24)

DB: `social-login` (MySQL 8.0)
관련 파일: `2026-07-24_kakao_login_refactor_up.sql` (적용), `2026-07-24_kakao_login_refactor_down.sql` (롤백)

> ⚠️ 이 변경은 기존 `token` 데이터를 폐기한다. 적용 후 **모든 사용자는 재로그인**이 필요하다.
> 적용 전 전체 덤프 백업을 권장한다: `mysqldump -uroot -p --databases social-login > backup.sql`

---

## 요약

| # | 대상 | 변경 | 이유 |
|---|------|------|------|
| 1 | `users.email` | UNIQUE 제약 추가 | 이메일 중복 계정 방지 |
| 2 | `social_account` | `access_token`, `access_token_expires_at` 컬럼 추가 | 로그아웃/연동해제용 소셜 토큰을 소셜계정 단위로 보관 |
| 3 | `refresh_token` | 신규 테이블 | Access는 무상태, Refresh는 **해시**로 사용자당 1행 저장·회전 |
| 4 | `token` | 테이블 제거 | access/refresh/카카오 토큰 **평문 저장** 구조 폐기 |

---

## 1) `users`

**Before**
```
PRIMARY KEY (user_id)
-- email: varchar(100) NULL, 제약 없음  ← 중복 이메일 가입 가능
```
**After**
```
PRIMARY KEY (user_id)
UNIQUE KEY uk_users_email (email)   ← 추가
```
- email 은 여전히 nullable. MySQL UNIQUE 인덱스는 다중 NULL 을 허용하므로 이메일 미동의(카카오) 사용자는 영향 없음.

---

## 2) `social_account`

**Before**
```
social_account_id (PK), user_id, provider, provider_id,
UNIQUE KEY uk_provider_social_id (provider, provider_id),
created_at, created_by, modified_at, modified_by
```
**After** (+2 컬럼)
```
... provider_id,
access_token             TEXT     NULL,   ← 추가 (소셜 access token)
access_token_expires_at  DATETIME NULL,   ← 추가
...
```
- 기존 `uk_provider_social_id (provider, provider_id)` 를 계정 식별 기준으로 활용(코드에서 `findUserByProvider`).

---

## 3) `refresh_token` (신규)

```
refresh_token_id VARCHAR(100) PK
user_id          VARCHAR(100) NOT NULL   UNIQUE KEY uk_refresh_token_user (user_id)
token_hash       CHAR(64)     NOT NULL   -- Refresh Token 원문의 SHA-256 hex
expires_at       DATETIME     NOT NULL
created_at/created_by/modified_at/modified_by
KEY idx_refresh_token_hash (token_hash)
```
- **Access Token 은 저장하지 않는다** (서명 검증만으로 무상태 인증).
- Refresh Token 은 원문 대신 해시를 저장 → DB 유출 시에도 토큰 자체는 노출되지 않음.
- `user_id` UNIQUE → 사용자당 1세션 행. 재발급 시 해시를 교체(회전).

---

## 4) `token` (제거)

**Before (제거된 구조)**
```
TOKEN_ID (PK), USER_ID,
ACCESS_TOKEN  TEXT NOT NULL,        -- 평문 저장 (불필요, 무상태 검증이면 저장 안 함)
REFRESH_TOKEN TEXT NOT NULL,        -- 평문 저장 (해시 저장이 정석)
auth_access_token VARCHAR(100) NOT NULL,  -- 카카오 토큰 평문 저장
ACCESS_TOKEN_EXPIRES_AT, REFRESH_TOKEN_EXPIRES_AT, audit...
```
- 문제점: 3종 토큰을 평문 저장했고, access_token 으로 세션 행을 조회하는 안티패턴이 있었음.
- 대체: 앱 세션 → `refresh_token`(해시), 카카오 토큰 → `social_account.access_token`.

---

## 롤백

```bash
docker exec -i social-login-mysql sh -c "mysql -uroot -p'****' 'social-login'" \
  < 2026-07-24_kakao_login_refactor_down.sql
```
- `refresh_token`, `social_account.access_token*` 데이터는 롤백 시 유실.
- `token` 은 구조만 복원(과거 데이터 복구 불가). 롤백 후에도 전 사용자 재로그인 필요.
