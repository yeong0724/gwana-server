-- =====================================================================
-- 상품 도메인 재설계 - 스키마 재구축 + 샘플 시드
-- DB: gwana-local-db (MySQL 8.0)  /  2026-07-28
-- 방식: 깨끗이 재구축 (기존 상품/옵션/리뷰/이미지 폐기) + 트랜잭션 테이블 초기화 + 샘플 시드
-- 설계: docs/product-redesign.md, docs/redesign.dbml (모델 C, 단일축 variant)
-- =====================================================================

USE `gwana-local-db`;
SET NAMES utf8mb4;   -- 한글 이중인코딩 방지 (파이프로 적용 시 커넥션 charset 고정)
SET FOREIGN_KEY_CHECKS = 0;

-- ---------------------------------------------------------------------
-- 0) 기존 상품 도메인 테이블 제거 + 트랜잭션 테이블 초기화
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `product_review_stats`;
DROP TABLE IF EXISTS `review`;
DROP TABLE IF EXISTS `product_option`;
DROP TABLE IF EXISTS `product`;

TRUNCATE TABLE `cart_item`;
TRUNCATE TABLE `cart`;
TRUNCATE TABLE `order_items`;
TRUNCATE TABLE `orders`;
TRUNCATE TABLE `payment_info`;
TRUNCATE TABLE `payment_session`;
TRUNCATE TABLE `inquiry`;

-- ---------------------------------------------------------------------
-- 1) category (계층)
-- ---------------------------------------------------------------------
CREATE TABLE `category` (
  `category_id` BIGINT       NOT NULL AUTO_INCREMENT,
  `parent_id`   BIGINT       NULL,
  `name`        VARCHAR(100) NOT NULL,
  `slug`        VARCHAR(120) NOT NULL,
  `depth`       TINYINT      NOT NULL DEFAULT 1,
  `sort_order`  INT          NOT NULL DEFAULT 0,
  `is_active`   TINYINT(1)   NOT NULL DEFAULT 1,
  `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `uk_category_slug` (`slug`),
  KEY `idx_category_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ---------------------------------------------------------------------
-- 2) product (SPU)
-- ---------------------------------------------------------------------
CREATE TABLE `product` (
  `product_id`     BIGINT       NOT NULL AUTO_INCREMENT,
  `category_id`    BIGINT       NOT NULL,
  `name`           VARCHAR(255) NOT NULL,
  `summary`        VARCHAR(500) NULL,
  `detail_content` MEDIUMTEXT   NULL,
  `status`         VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',   -- DRAFT/ON_SALE/SOLD_OUT/HIDDEN/DISCONTINUED
  `display_price`  INT          NULL COMMENT '진열용 최저 variant 가격 캐시',
  `shipping_price` INT          NOT NULL DEFAULT 0 COMMENT '0=무료배송',
  `sale_start_at`  DATETIME     NULL,
  `sale_end_at`    DATETIME     NULL,
  `created_at`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by`     VARCHAR(50)  NULL,
  `updated_at`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updated_by`     VARCHAR(50)  NULL,
  `deleted_at`     DATETIME     NULL,
  PRIMARY KEY (`product_id`),
  KEY `idx_product_category` (`category_id`),
  KEY `idx_product_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ---------------------------------------------------------------------
-- 3) product_variant (SKU) - 판매·가격 단위
-- ---------------------------------------------------------------------
CREATE TABLE `product_variant` (
  `product_variant_id` BIGINT       NOT NULL AUTO_INCREMENT,
  `product_id`         BIGINT       NOT NULL,
  `option_label`       VARCHAR(200) NOT NULL COMMENT '예: 세작 80g (관리자 입력)',
  `price`              INT          NOT NULL COMMENT '가격 SSOT',
  `status`             VARCHAR(20)  NOT NULL DEFAULT 'ON_SALE',  -- ON_SALE/SOLD_OUT/HIDDEN
  `sort_order`         INT          NOT NULL DEFAULT 0,
  `created_at`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at`         DATETIME     NULL,
  PRIMARY KEY (`product_variant_id`),
  KEY `idx_variant_product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ---------------------------------------------------------------------
-- 4) product_addon (추가상품) + 매핑
-- ---------------------------------------------------------------------
CREATE TABLE `product_addon` (
  `product_addon_id` BIGINT       NOT NULL AUTO_INCREMENT,
  `name`             VARCHAR(100) NOT NULL,
  `price`            INT          NOT NULL COMMENT '추가금',
  `is_active`        TINYINT(1)   NOT NULL DEFAULT 1,
  `created_at`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`product_addon_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `product_addon_map` (
  `product_id`       BIGINT NOT NULL,
  `product_addon_id` BIGINT NOT NULL,
  PRIMARY KEY (`product_id`, `product_addon_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ---------------------------------------------------------------------
-- 5) product_image (갤러리/디테일 공통 + variant 썸네일)
--    - image_type: THUMBNAIL(variant 전용) / GALLERY / DETAIL
--    - product_variant_id: THUMBNAIL 행에만 지정, variant당 1건 (UNIQUE)
-- ---------------------------------------------------------------------
CREATE TABLE `product_image` (
  `product_image_id`   BIGINT       NOT NULL AUTO_INCREMENT,
  `product_id`         BIGINT       NOT NULL,
  `product_variant_id` BIGINT       NULL,
  `image_type`         VARCHAR(20)  NOT NULL DEFAULT 'GALLERY',
  `url`                VARCHAR(500) NOT NULL,
  `alt_text`           VARCHAR(200) NULL,
  `sort_order`         INT          NOT NULL DEFAULT 0,
  `created_at`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`product_image_id`),
  KEY `idx_image_product` (`product_id`),
  UNIQUE KEY `uk_image_variant_thumb` (`product_variant_id`)  -- variant당 썸네일 1건 (NULL 다중 허용)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ---------------------------------------------------------------------
-- 6) product_review + 이미지 + 통계
-- ---------------------------------------------------------------------
CREATE TABLE `product_review` (
  `product_review_id`  BIGINT        NOT NULL AUTO_INCREMENT,
  `product_id`         BIGINT        NOT NULL,
  `product_variant_id` BIGINT        NULL,
  `order_item_id`      BIGINT        NULL COMMENT '검증구매 연결',
  `user_id`            VARCHAR(100)  NOT NULL,
  `rating`             DECIMAL(2,1)  NOT NULL,
  `content`            VARCHAR(1000) NULL,
  `status`             VARCHAR(20)   NOT NULL DEFAULT 'VISIBLE',  -- VISIBLE/HIDDEN/REPORTED
  `created_at`         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`product_review_id`),
  KEY `idx_review_product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `product_review_image` (
  `product_review_image_id` BIGINT       NOT NULL AUTO_INCREMENT,
  `product_review_id`       BIGINT       NOT NULL,
  `url`                     VARCHAR(500) NOT NULL,
  `sort_order`              INT          NOT NULL DEFAULT 0,
  PRIMARY KEY (`product_review_image_id`),
  KEY `idx_reviewimg_review` (`product_review_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `product_review_stats` (
  `product_review_stats_id` BIGINT       NOT NULL AUTO_INCREMENT,
  `product_id`              BIGINT       NOT NULL,
  `avg_rating`              DECIMAL(2,1) NOT NULL DEFAULT 0.0,
  `review_count`            INT          NOT NULL DEFAULT 0,
  `rating_1_cnt`            INT          NOT NULL DEFAULT 0,
  `rating_2_cnt`            INT          NOT NULL DEFAULT 0,
  `rating_3_cnt`            INT          NOT NULL DEFAULT 0,
  `rating_4_cnt`            INT          NOT NULL DEFAULT 0,
  `rating_5_cnt`            INT          NOT NULL DEFAULT 0,
  PRIMARY KEY (`product_review_stats_id`),
  UNIQUE KEY `uk_reviewstats_product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ---------------------------------------------------------------------
-- 7) 트랜잭션 테이블 참조 컬럼 이관 (option_id -> variant_id)
-- ---------------------------------------------------------------------
ALTER TABLE `cart_item`
  DROP INDEX `uk_cart_item_option`,
  DROP COLUMN `product_option_id`,
  ADD COLUMN `product_variant_id` BIGINT NOT NULL AFTER `cart_id`,
  ADD UNIQUE KEY `uk_cart_item_variant` (`cart_id`, `product_variant_id`);

ALTER TABLE `order_items`
  CHANGE COLUMN `product_option_id` `product_variant_id` BIGINT NOT NULL;

ALTER TABLE `inquiry`
  MODIFY COLUMN `product_id` BIGINT NULL;

ALTER TABLE `payment_session`
  MODIFY COLUMN `product_id` BIGINT NOT NULL;

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================================
-- 8) 샘플 시드
-- =====================================================================

-- 카테고리
INSERT INTO `category` (`category_id`, `parent_id`, `name`, `slug`, `depth`, `sort_order`) VALUES
  (1, NULL, '녹차',   'greenTea',      1, 0),
  (2, NULL, '발효차', 'blackTea',      1, 1),
  (3, NULL, '대용차', 'substituteTea', 1, 2);

-- 상품
INSERT INTO `product` (`product_id`, `category_id`, `name`, `summary`, `status`, `display_price`, `shipping_price`, `created_by`) VALUES
  (1, 1, '관아수제차 세작 유기농 하동녹차', '하동 유기농 첫물 녹차', 'ON_SALE', 70000, 0, 'admin'),
  (2, 1, '관아수제차 우전 유기농 하동녹차', '곡우 이전 수확한 최상급 우전', 'ON_SALE', 110000, 0, 'admin'),
  (3, 3, '관아 무우차',                    '구수한 볶음 대용차',           'ON_SALE', 25000, 3000, 'admin');

-- variant (판매단위)
INSERT INTO `product_variant` (`product_variant_id`, `product_id`, `option_label`, `price`, `status`, `sort_order`) VALUES
  (1, 1, '세작 80g',  70000,  'ON_SALE', 0),
  (2, 1, '세작 150g', 120000, 'ON_SALE', 1),
  (3, 2, '우전 80g',  110000, 'ON_SALE', 0),
  (4, 3, '무우차 100g', 25000, 'ON_SALE', 0);

-- 애드온 (상품별 매핑으로만 노출)
INSERT INTO `product_addon` (`product_addon_id`, `name`, `price`, `is_active`) VALUES
  (1, '선물용 쇼핑백', 1000, 1);

-- 이미지: 갤러리(GALLERY, variant NULL) / 디테일(DETAIL) / variant 썸네일(THUMBNAIL)
INSERT INTO `product_image` (`product_id`, `product_variant_id`, `image_type`, `url`, `alt_text`, `sort_order`) VALUES
  -- 상품1 갤러리
  (1, NULL, 'GALLERY', 'images/product/thumbnail/0Q5AWH8VFZCYR.png', '세작 대표', 0),
  (1, NULL, 'GALLERY', 'images/product/thumbnail/0Q5AWHW3FZCXM.png', '세작 2',   1),
  (1, NULL, 'GALLERY', 'images/product/thumbnail/0Q5AWJ8F3ZCZT.png', '세작 3',   2),
  -- 상품1 디테일
  (1, NULL, 'DETAIL',  'images/product/info/0QA3ZZAN9BWTM.webp', NULL, 0),
  -- 상품1 variant 썸네일
  (1, 1,    'THUMBNAIL', 'images/product/variant/sejak-80.png',  '세작 80g',  0),
  (1, 2,    'THUMBNAIL', 'images/product/variant/sejak-150.png', '세작 150g', 0),
  -- 상품2 갤러리 + variant 썸네일
  (2, NULL, 'GALLERY', 'images/product/thumbnail/ujeon-main.png', '우전 대표', 0),
  (2, 3,    'THUMBNAIL', 'images/product/variant/ujeon-80.png',  '우전 80g',  0),
  -- 상품3 갤러리 + variant 썸네일
  (3, NULL, 'GALLERY', 'images/product/thumbnail/muwoo-main.png', '무우차 대표', 0),
  (3, 4,    'THUMBNAIL', 'images/product/variant/muwoo-100.png', '무우차 100g', 0);

-- 상품별 애드온 매핑 (쇼핑백을 세 상품에 명시적으로 연결)
INSERT INTO `product_addon_map` (`product_id`, `product_addon_id`) VALUES
  (1, 1), (2, 1), (3, 1);

-- 리뷰 (users 시드 사용자 기준; 존재하는 user_id 로 교체 필요할 수 있음)
INSERT INTO `product_review` (`product_id`, `product_variant_id`, `user_id`, `rating`, `content`, `status`)
SELECT 1, 1, u.user_id, 5.0, '향이 정말 좋아요. 재구매 의사 있습니다.', 'VISIBLE'
FROM `users` u LIMIT 1;

INSERT INTO `product_review` (`product_id`, `product_variant_id`, `user_id`, `rating`, `content`, `status`)
SELECT 1, 2, u.user_id, 4.0, '150g 넉넉하고 좋네요.', 'VISIBLE'
FROM `users` u LIMIT 1;

-- 리뷰 통계 재집계
INSERT INTO `product_review_stats` (`product_id`, `avg_rating`, `review_count`,
    `rating_1_cnt`, `rating_2_cnt`, `rating_3_cnt`, `rating_4_cnt`, `rating_5_cnt`)
SELECT r.product_id,
       ROUND(AVG(r.rating), 1),
       COUNT(*),
       SUM(r.rating >= 1 AND r.rating < 2),
       SUM(r.rating >= 2 AND r.rating < 3),
       SUM(r.rating >= 3 AND r.rating < 4),
       SUM(r.rating >= 4 AND r.rating < 5),
       SUM(r.rating >= 5)
FROM `product_review` r
WHERE r.status = 'VISIBLE'
GROUP BY r.product_id;
