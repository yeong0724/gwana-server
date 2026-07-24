-- =====================================================================
-- 카카오 로그인 리팩터링 - 스키마 변경 (DOWN / 롤백)
-- DB: social-login (MySQL 8.0)
--
-- UP 을 원래 구조로 되돌린다. (역순으로 실행)
-- 주의: refresh_token 및 social_account.access_token 데이터는 롤백 시 유실된다.
--       token 테이블은 구조만 복원되며 과거 데이터는 복구되지 않는다.
-- =====================================================================

-- 4') token 테이블 원복 (UP 4 의 역)
CREATE TABLE `token` (
    `TOKEN_ID`                 VARCHAR(100) COLLATE utf8mb4_unicode_ci NOT NULL,
    `USER_ID`                  VARCHAR(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '사용자 ID',
    `ACCESS_TOKEN`             TEXT COLLATE utf8mb4_unicode_ci         NOT NULL COMMENT 'Access Token (JWT)',
    `REFRESH_TOKEN`            TEXT COLLATE utf8mb4_unicode_ci         NOT NULL COMMENT 'Refresh Token (JWT)',
    `auth_access_token`        VARCHAR(100) COLLATE utf8mb4_unicode_ci NOT NULL,
    `ACCESS_TOKEN_EXPIRES_AT`  DATETIME                               NOT NULL COMMENT 'Access Token 만료시각',
    `REFRESH_TOKEN_EXPIRES_AT` DATETIME                               NOT NULL COMMENT 'Refresh Token 만료시각',
    `created_at`               DATETIME     DEFAULT NULL,
    `created_by`               VARCHAR(50) COLLATE utf8mb4_unicode_ci  DEFAULT NULL,
    `modified_at`              DATETIME     DEFAULT NULL,
    `modified_by`              VARCHAR(50) COLLATE utf8mb4_unicode_ci  DEFAULT NULL,
    PRIMARY KEY (`TOKEN_ID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='사용자 토큰 저장 테이블';

-- 3') refresh_token 테이블 제거 (UP 3 의 역)
DROP TABLE `refresh_token`;

-- 2') social_account 컬럼 제거 (UP 2 의 역)
ALTER TABLE `social_account`
    DROP COLUMN `access_token_expires_at`,
    DROP COLUMN `access_token`;

-- 1') users.email UNIQUE 제거 (UP 1 의 역)
ALTER TABLE `users`
    DROP KEY `uk_users_email`;
