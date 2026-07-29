-- =====================================================================
-- Gwana ERD 생성용 SQL (dbdiagram.io Import > From MySQL 용)
-- 대상: gwana-local-db (MySQL 8.0)  /  생성일: 2026-07-28
--
-- 주의(실제 스키마와 다른 점 = dbdiagram 관계선을 위해 조정한 부분):
--  * orders 는 실제로 PK가 없음 → 여기선 PRIMARY KEY(order_id) 추가 [검토 대상]
--  * FK 5곳은 부모/자식 타입이 달라 실제 물리 FK로는 성립하지 않음 [TYPE MISMATCH 주석]
--  * 실제 DB에는 물리 FK가 전혀 없음. 아래 FK는 관계 시각화 용도.
-- =====================================================================

CREATE TABLE `users` (
  `user_id`         varchar(100) NOT NULL,
  `customer_key`    varchar(100) NOT NULL,
  `username`        varchar(50)  NOT NULL,
  `password`        varchar(255) DEFAULT NULL,
  `email`           varchar(100) DEFAULT NULL,
  `phone`           varchar(20)  DEFAULT NULL,
  `profile_image`   varchar(100) DEFAULT NULL,
  `zonecode`        varchar(20)  DEFAULT NULL,
  `road_address`    varchar(100) DEFAULT NULL,
  `detail_address`  varchar(300) DEFAULT NULL,
  `role`            enum('ADMIN','GENERAL') NOT NULL DEFAULT 'GENERAL',
  `created_at`      datetime     DEFAULT NULL,
  `created_by`      varchar(50)  DEFAULT NULL,
  `modified_at`     datetime     DEFAULT NULL,
  `modified_by`     varchar(50)  DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_users_email` (`email`)
);

CREATE TABLE `social_account` (
  `social_account_id`       varchar(100) NOT NULL,
  `user_id`                 varchar(100) NOT NULL,
  `provider`                varchar(50)  NOT NULL,
  `provider_id`             bigint       NOT NULL,
  `access_token`            text,
  `access_token_expires_at` datetime     DEFAULT NULL,
  `created_at`              datetime     DEFAULT NULL,
  `created_by`              varchar(50)  DEFAULT NULL,
  `modified_at`             datetime     DEFAULT NULL,
  `modified_by`             varchar(50)  DEFAULT NULL,
  PRIMARY KEY (`social_account_id`),
  UNIQUE KEY `uk_provider_social_id` (`provider`,`provider_id`)
);

CREATE TABLE `refresh_token` (
  `refresh_token_id` varchar(100) NOT NULL,
  `user_id`          varchar(100) NOT NULL,
  `token_hash`       char(64)     NOT NULL,
  `expires_at`       datetime     NOT NULL,
  `created_at`       datetime     DEFAULT NULL,
  `created_by`       varchar(50)  DEFAULT NULL,
  `modified_at`      datetime     DEFAULT NULL,
  `modified_by`      varchar(50)  DEFAULT NULL,
  PRIMARY KEY (`refresh_token_id`),
  UNIQUE KEY `uk_refresh_token_user` (`user_id`)
);

CREATE TABLE `product` (
  `product_id`     bigint       NOT NULL AUTO_INCREMENT,
  `product_name`   varchar(255) NOT NULL,
  `category_id`    varchar(100) NOT NULL,
  `category_name`  varchar(50)  NOT NULL,
  `images`         json         DEFAULT NULL,
  `infos`          json         DEFAULT NULL,
  `price`          int          NOT NULL,
  `shipping_price` int          DEFAULT NULL,
  `created_at`     datetime     DEFAULT CURRENT_TIMESTAMP,
  `created_by`     varchar(50)  DEFAULT NULL,
  `modified_at`    datetime     DEFAULT CURRENT_TIMESTAMP,
  `modified_by`    varchar(50)  DEFAULT NULL,
  PRIMARY KEY (`product_id`)
);

CREATE TABLE `product_option` (
  `product_option_id`      bigint       NOT NULL AUTO_INCREMENT,
  `product_id`             bigint       DEFAULT NULL,
  `option_name`            varchar(100) NOT NULL,
  `option_price`           int          DEFAULT '0',
  `is_required`            tinyint(1)   DEFAULT '1',
  `is_quantity_adjustable` tinyint(1)   DEFAULT '1',
  PRIMARY KEY (`product_option_id`)
);

CREATE TABLE `product_review_stats` (
  `product_id`   bigint       NOT NULL,
  `avg_rating`   decimal(2,1) NOT NULL DEFAULT '0.0',
  `review_count` int          NOT NULL DEFAULT '0',
  PRIMARY KEY (`product_id`)
);

CREATE TABLE `review` (
  `review_id`     bigint       NOT NULL AUTO_INCREMENT,
  `product_id`    varchar(50)  NOT NULL,   -- TYPE MISMATCH: product.product_id 는 bigint
  `content`       varchar(500) NOT NULL,
  `review_images` json         DEFAULT NULL,
  `rating`        decimal(2,1) NOT NULL DEFAULT '0.0',
  `created_at`    datetime     DEFAULT CURRENT_TIMESTAMP,
  `created_by`    varchar(50)  DEFAULT NULL,
  `modified_at`   datetime     DEFAULT CURRENT_TIMESTAMP,
  `modified_by`   varchar(50)  DEFAULT NULL,
  PRIMARY KEY (`review_id`)
);

CREATE TABLE `inquiry` (
  `inquiry_id`       bigint       NOT NULL AUTO_INCREMENT,
  `upper_inquiry_id` bigint       DEFAULT NULL,   -- self: 답변 대상 문의글
  `product_id`       varchar(50)  DEFAULT NULL,   -- TYPE MISMATCH: product.product_id 는 bigint
  `title`            varchar(200) NOT NULL,
  `content`          text         NOT NULL,
  `is_secret`        char(1)      NOT NULL DEFAULT 'N',
  `is_answered`      char(1)      DEFAULT 'N',
  `created_at`       datetime     DEFAULT CURRENT_TIMESTAMP,
  `created_by`       varchar(50)  DEFAULT NULL,
  `modified_at`      datetime     DEFAULT CURRENT_TIMESTAMP,
  `modified_by`      varchar(50)  DEFAULT NULL,
  PRIMARY KEY (`inquiry_id`)
);

CREATE TABLE `cart` (
  `cart_id`     bigint      NOT NULL AUTO_INCREMENT,
  `product_id`  bigint      NOT NULL,
  `user_id`     varchar(50) NOT NULL,
  `created_at`  datetime    DEFAULT CURRENT_TIMESTAMP,
  `created_by`  varchar(50) DEFAULT NULL,
  `modified_at` datetime    DEFAULT CURRENT_TIMESTAMP,
  `modified_by` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`cart_id`),
  UNIQUE KEY `uk_cart_user_product` (`user_id`,`product_id`)
);

CREATE TABLE `cart_item` (
  `cart_item_id`      bigint      NOT NULL AUTO_INCREMENT,
  `cart_id`           bigint      NOT NULL,
  `product_option_id` varchar(50) NOT NULL,   -- TYPE MISMATCH: product_option.product_option_id 는 bigint
  `quantity`          int         NOT NULL DEFAULT '1',
  PRIMARY KEY (`cart_item_id`),
  UNIQUE KEY `uk_cart_item_option` (`cart_id`,`product_option_id`)
);

-- 주의: 실제 orders 테이블에는 PRIMARY KEY 가 없음. dbdiagram 관계선을 위해 PK 추가 [검토 대상]
CREATE TABLE `orders` (
  `order_id`                varchar(100) NOT NULL,
  `order_status`            varchar(20)  NOT NULL,
  `product_amount`          int          NOT NULL DEFAULT '0',
  `shipping_fee`            int          NOT NULL DEFAULT '0',
  `discount_amount`         int          NOT NULL DEFAULT '0',
  `total_amount`            int          NOT NULL DEFAULT '0',
  `created_at`              datetime     DEFAULT NULL,
  `created_by`              varchar(50)  DEFAULT NULL,   -- 주문자 user_id (전용 user_id 컬럼 없음)
  `sender_name`             varchar(100) DEFAULT NULL,
  `sender_phone`            varchar(20)  DEFAULT NULL,
  `recipient_name`          varchar(100) DEFAULT NULL,
  `recipient_phone`         varchar(20)  DEFAULT NULL,
  `zonecode`                varchar(10)  DEFAULT NULL,
  `road_address`            varchar(255) DEFAULT NULL,
  `detail_address`          varchar(255) DEFAULT NULL,
  `delivery_request`        varchar(500) DEFAULT NULL,
  `delivery_request_detail` varchar(500) DEFAULT NULL,
  `ordered_at`              datetime     DEFAULT NULL,
  `paid_at`                 datetime     DEFAULT NULL,
  `cancelled_at`            datetime     DEFAULT NULL,
  `refunded_at`             datetime     DEFAULT NULL,
  `modified_at`             datetime     DEFAULT NULL,
  `modified_by`             varchar(50)  DEFAULT NULL,
  PRIMARY KEY (`order_id`)
);

CREATE TABLE `order_items` (
  `order_item_id`         bigint       NOT NULL AUTO_INCREMENT,
  `order_id`              varchar(100) NOT NULL,
  `product_id`            bigint       NOT NULL,
  `product_name`          varchar(255) NOT NULL,
  `product_thumbnail_url` varchar(255) NOT NULL,
  `category_name`         varchar(50)  NOT NULL,
  `product_option_id`     bigint       NOT NULL,
  `option_name`           varchar(100) NOT NULL,
  `option_price`          int          NOT NULL DEFAULT '0',
  `quantity`              int          NOT NULL DEFAULT '0',
  `is_required`           tinyint(1)   DEFAULT '1',
  PRIMARY KEY (`order_item_id`)
);

CREATE TABLE `payment_info` (
  `session_id`              varchar(50)  NOT NULL,
  `order_id`                varchar(50)  NOT NULL,   -- TYPE MISMATCH: orders.order_id 는 varchar(100)
  `user_id`                 varchar(50)  NOT NULL,
  `total_price`             int          NOT NULL,
  `total_shipping_price`    int          NOT NULL,
  `total_amount`            int          NOT NULL,
  `sender_name`             varchar(100) NOT NULL,
  `sender_phone`            varchar(20)  NOT NULL,
  `recipient_name`          varchar(100) NOT NULL,
  `recipient_phone`         varchar(20)  NOT NULL,
  `zonecode`                varchar(10)  NOT NULL,
  `road_address`            varchar(255) NOT NULL,
  `detail_address`          varchar(255) NOT NULL,
  `delivery_request`        varchar(500) NOT NULL,
  `delivery_request_detail` varchar(500) DEFAULT NULL,
  `expires_at`              datetime     NOT NULL,
  PRIMARY KEY (`session_id`)
);

CREATE TABLE `payment_session` (
  `payment_session_id` bigint      NOT NULL AUTO_INCREMENT,
  `session_id`         varchar(50) NOT NULL,
  `product_id`         varchar(50) NOT NULL,   -- TYPE MISMATCH: product.product_id 는 bigint
  `quantity`           int         NOT NULL DEFAULT '0',
  `user_id`            varchar(50) NOT NULL,
  `expires_at`         datetime    NOT NULL,
  PRIMARY KEY (`payment_session_id`)
);

-- =====================================================================
-- 관계(FK) — 실제 DB엔 없는 논리 관계. dbdiagram 관계선 생성용.
-- =====================================================================

ALTER TABLE `social_account`  ADD FOREIGN KEY (`user_id`)           REFERENCES `users` (`user_id`);
ALTER TABLE `refresh_token`   ADD FOREIGN KEY (`user_id`)           REFERENCES `users` (`user_id`);

ALTER TABLE `product_option`  ADD FOREIGN KEY (`product_id`)        REFERENCES `product` (`product_id`);
ALTER TABLE `product_review_stats` ADD FOREIGN KEY (`product_id`)   REFERENCES `product` (`product_id`);

ALTER TABLE `cart`            ADD FOREIGN KEY (`user_id`)           REFERENCES `users` (`user_id`);
ALTER TABLE `cart`            ADD FOREIGN KEY (`product_id`)        REFERENCES `product` (`product_id`);
ALTER TABLE `cart_item`       ADD FOREIGN KEY (`cart_id`)           REFERENCES `cart` (`cart_id`);
ALTER TABLE `cart_item`       ADD FOREIGN KEY (`product_option_id`) REFERENCES `product_option` (`product_option_id`); -- TYPE MISMATCH

ALTER TABLE `review`          ADD FOREIGN KEY (`product_id`)        REFERENCES `product` (`product_id`); -- TYPE MISMATCH
ALTER TABLE `inquiry`         ADD FOREIGN KEY (`product_id`)        REFERENCES `product` (`product_id`); -- TYPE MISMATCH
ALTER TABLE `inquiry`         ADD FOREIGN KEY (`upper_inquiry_id`)  REFERENCES `inquiry` (`inquiry_id`); -- self

ALTER TABLE `orders`          ADD FOREIGN KEY (`created_by`)        REFERENCES `users` (`user_id`); -- 주문자(감사컬럼)
ALTER TABLE `order_items`     ADD FOREIGN KEY (`order_id`)          REFERENCES `orders` (`order_id`);
ALTER TABLE `order_items`     ADD FOREIGN KEY (`product_id`)        REFERENCES `product` (`product_id`);
ALTER TABLE `order_items`     ADD FOREIGN KEY (`product_option_id`) REFERENCES `product_option` (`product_option_id`);

ALTER TABLE `payment_info`    ADD FOREIGN KEY (`user_id`)           REFERENCES `users` (`user_id`);
ALTER TABLE `payment_info`    ADD FOREIGN KEY (`order_id`)          REFERENCES `orders` (`order_id`); -- TYPE MISMATCH
ALTER TABLE `payment_session` ADD FOREIGN KEY (`session_id`)        REFERENCES `payment_info` (`session_id`);
ALTER TABLE `payment_session` ADD FOREIGN KEY (`user_id`)           REFERENCES `users` (`user_id`);
ALTER TABLE `payment_session` ADD FOREIGN KEY (`product_id`)        REFERENCES `product` (`product_id`); -- TYPE MISMATCH
