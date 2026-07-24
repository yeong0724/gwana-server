-- =====================================================================
-- 카카오 로그인 리팩터링 - 스키마 변경 (UP / 정방향)
-- DB: social-login (MySQL 8.0)
-- 적용일: 2026-07-24
--
-- 롤백은 2026-07-24_kakao_login_refactor_down.sql 참고
-- 변경 배경/근거는 SCHEMA_CHANGES.md 참고
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1) users.email 에 UNIQUE 제약 추가
--    - 이유: 이메일 중복 가입 방지. (이전엔 제약이 없어 같은 이메일 계정이
--            여러 개 생길 수 있었음)
--    - email 은 nullable 이며 MySQL 은 UNIQUE 인덱스에서 다중 NULL 을 허용하므로
--      이메일 미제공(카카오 미동의) 사용자는 영향 없음.
-- ---------------------------------------------------------------------
ALTER TABLE `users`
    ADD UNIQUE KEY `uk_users_email` (`email`);

-- ---------------------------------------------------------------------
-- 2) social_account 에 소셜 access token 컬럼 추가
--    - 이유: 로그아웃/연동해제 시 필요한 카카오 access token 을
--            (세션 테이블이 아니라) 소셜 계정 단위로 보관.
-- ---------------------------------------------------------------------
ALTER TABLE `social_account`
    ADD COLUMN `access_token` TEXT NULL
        COMMENT '소셜 provider access token (로그아웃/연동해제용)' AFTER `provider_id`,
    ADD COLUMN `access_token_expires_at` DATETIME NULL
        COMMENT '소셜 access token 만료시각' AFTER `access_token`;

-- ---------------------------------------------------------------------
-- 3) refresh_token 테이블 신설
--    - 이유: Access Token 은 무상태(서명검증)로만 인증하므로 DB 저장하지 않음.
--            Refresh Token 은 원문 대신 SHA-256 해시를 사용자당 1행으로 저장하고
--            재발급 시 회전(rotation)한다. (user_id UNIQUE)
-- ---------------------------------------------------------------------
CREATE TABLE `refresh_token` (
    `refresh_token_id` VARCHAR(100) NOT NULL,
    `user_id`          VARCHAR(100) NOT NULL COMMENT '사용자 ID',
    `token_hash`       CHAR(64)     NOT NULL COMMENT 'Refresh Token 원문의 SHA-256 hex',
    `expires_at`       DATETIME     NOT NULL COMMENT 'Refresh Token 만료시각',
    `created_at`       DATETIME     DEFAULT NULL,
    `created_by`       VARCHAR(50)  DEFAULT NULL,
    `modified_at`      DATETIME     DEFAULT NULL,
    `modified_by`      VARCHAR(50)  DEFAULT NULL,
    PRIMARY KEY (`refresh_token_id`),
    UNIQUE KEY `uk_refresh_token_user` (`user_id`),
    KEY `idx_refresh_token_hash` (`token_hash`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='리프레시 토큰(해시) 저장';

-- ---------------------------------------------------------------------
-- 4) 기존 token 테이블 제거
--    - 이유: access/refresh 원문 및 카카오 토큰을 평문 저장하던 구조를 폐기.
--    - 주의: 적용 시 기존 세션은 모두 무효화되어 사용자는 재로그인이 필요하다.
-- ---------------------------------------------------------------------
DROP TABLE `token`;
