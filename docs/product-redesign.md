# 상품 도메인 재설계 (실이커머스 기준)

> 목적: 현업 커머스(카페24 / 쿠팡 / Shopify / Broadleaf 계열) 수준의 상품 카탈로그 스키마를
> **학습 + 실제 구현 목표**로 재설계한다. 아직 소스/DB에는 반영하지 않는다(설계 단계).
> 통화는 단일(KRW), 금액은 원 단위 정수. 작성일 2026-07-28.

---

## 0. 설계 원칙 (먼저 합의할 것)

| 원칙 | 내용 | 이유 |
|------|------|------|
| **단일 진실원천(SSOT)** | 가격은 **오직 SKU(variant)** 에만 존재. 상품(SPU)은 가격을 갖지 않는다 | 현재 `product.price`가 죽은 값이고 `option_price`와 이중화된 문제 제거 |
| **SPU / SKU 분리** | 상품(SPU=진열 단위) 과 판매단위(SKU=결제 단위) 를 나눈다 | 옵션·가격을 SKU 단위로 일관 관리 (관아수제차는 단일 축이라 옵션값을 variant에 직접 라벨링) |
| **옵션 ≠ 애드온** | 구성옵션(variant를 결정)과 추가상품(애드온)을 다른 테이블로 분리 | 현재 한 테이블에 섞여 `option_price` 의미가 행마다 다른 문제 제거 |
| **스냅샷 불변성** | 주문/결제에 담긴 상품정보는 **주문 시점 스냅샷** 으로 고정 | 상품이 바뀌어도 과거 주문 내역은 불변(현재 order_items가 이미 이 패턴 — 유지) |
| **정규화 + 명시적 FK** | 카테고리/이미지 정규화, 관계는 물리 FK로 무결성 보장, 타입 통일(bigint) | 현재 문자열 category, JSON 이미지, product_id 타입불일치 문제 제거 |
| **상태·소프트삭제** | 마스터 테이블은 status enum + deleted_at | 판매중지/임시저장/이력 보존 |
| **감사(audit) 표준화** | created_at/by, updated_at/by 통일 | 운영 추적성 |

**표기 규약**
- PK: 카탈로그 엔터티는 `bigint AUTO_INCREMENT`(내부 식별자). 외부 노출 식별자가 필요하면 별도 `*_code`(unique).
- 금액: `int` (원). 국제화 시 `DECIMAL(12,2)` + `currency` 로 확장.
- 상태: 가독성을 위해 `ENUM` 사용(운영 중 값 추가가 잦으면 코드 lookup 테이블로 승격).

---

## 1. 도메인 개요

```
category ──< product (SPU) ─┬─< product_variant (SKU, option_label + price)
                            ├─< product_image (variant_id nullable)
                            ├─< product_addon_map >── product_addon
                            ├─< product_review ─< product_review_image
                            └─  product_review_stats (1:1 집계 캐시)

cart ─< cart_item ─< cart_item_addon        (cart_item → variant)
orders ─< order_item ─< order_item_addon    (order_item → variant, 스냅샷)
```
(배송비는 별도 정책 테이블 없이 `product.shipping_price` 로 관리. 브랜드 테이블 없음 — 단일 브랜드)
(옵션은 단일 축이라 option_group/value/variant_option_value 없이 `product_variant.option_label` 로 관리 — 아래 2.3 참고)

핵심 축: **product(SPU) → product_variant(SKU)** 가 뼈대. 나머지는 SPU 또는 SKU에 매달린다.

---

## 2. 테이블 상세 + 설계 의도

### 2.1 category — 카테고리 (계층)

```sql
CREATE TABLE category (
  category_id  BIGINT       NOT NULL AUTO_INCREMENT,
  parent_id    BIGINT       NULL,                    -- 루트면 NULL (self FK)
  name         VARCHAR(100) NOT NULL,
  slug         VARCHAR(120) NOT NULL,                -- URL/식별용 (greenTea 등)
  depth        TINYINT      NOT NULL DEFAULT 1,
  sort_order   INT          NOT NULL DEFAULT 0,
  is_active    TINYINT(1)   NOT NULL DEFAULT 1,
  created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (category_id),
  UNIQUE KEY uk_category_slug (slug),
  KEY idx_category_parent (parent_id),
  CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES category(category_id)
);
```
- **왜**: 현재는 `product.category_id='greenTea'`, `category_name='녹차'` 를 상품마다 중복 저장 → 카테고리명 변경 시 전상품 갱신, 계층/정렬 불가. 별도 테이블 + self FK로 트리 구성.
- 대량 서브트리 탐색이 잦으면 `path`(materialized path) 또는 closure table 추가 고려. 지금 규모(3개)엔 parent_id로 충분.

### 2.2 product — 상품 (SPU, 진열 단위)

```sql
CREATE TABLE product (
  product_id       BIGINT       NOT NULL AUTO_INCREMENT,
  category_id      BIGINT       NOT NULL,
  name             VARCHAR(255) NOT NULL,
  summary          VARCHAR(500) NULL,                 -- 짧은 소개
  detail_content   MEDIUMTEXT   NULL,                 -- 상세페이지 본문(HTML/MD) 또는 이미지참조
  status           ENUM('ON_SALE','SOLD_OUT','HIDDEN','DISCONTINUED')
                                NOT NULL DEFAULT 'ON_SALE',
  display_price     INT         NULL,                 -- 진열용 대표가(최저 variant가) = 캐시, SSOT 아님
  shipping_price    INT         NOT NULL DEFAULT 0,   -- 0=무료배송, 그 외 배송비(원)
  sale_start_at    DATETIME     NULL,
  sale_end_at      DATETIME     NULL,
  created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by       VARCHAR(50)  NULL,
  updated_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by       VARCHAR(50)  NULL,
  deleted_at       DATETIME     NULL,                 -- 소프트 삭제
  PRIMARY KEY (product_id),
  KEY idx_product_category (category_id),
  KEY idx_product_status (status),
  CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category(category_id)
);
```
- **가격 제거가 핵심**: `product.price`(죽은 값) 삭제. 대신 **진열용 최저가 캐시**로 `display_price`만 둔다(정렬·목록 표시용, 결제 계산엔 절대 사용 안 함). variant 저장/수정 시 트리거·앱로직으로 갱신.
- **배송비**: 별도 배송정책 테이블 없이 상품 단위 `shipping_price` 로 관리. `0` 이면 무료배송, 그 외 값이면 해당 금액(원) 부과. (현재 `OrderService` 의 5만원 무료배송 하드코딩은 제거하고 이 컬럼 기준으로 계산)
- **브랜드 없음**: 단일 브랜드(관아수제차) 샵이라 brand 테이블은 두지 않는다.
- `status` 로 품절·숨김·단종 관리 → 현재 불가능하던 운영 제어.
- `detail_content` 는 상세 이미지 나열(현재 `infos`)을 대체. 이미지 순서 제어가 필요하면 아래 product_image(type=DETAIL)로 관리.

### 2.3 product_variant — SKU (판매·가격의 단위)

```sql
CREATE TABLE product_variant (
  variant_id   BIGINT       NOT NULL AUTO_INCREMENT,
  product_id   BIGINT       NOT NULL,
  option_label VARCHAR(200) NOT NULL,                 -- 예: '세작 80g' (관리자 직접 입력)
  price        INT          NOT NULL,                 -- ★ 가격의 단일 진실원천
  status       ENUM('ON_SALE','SOLD_OUT','HIDDEN') NOT NULL DEFAULT 'ON_SALE',
  sort_order   INT          NOT NULL DEFAULT 0,
  created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at   DATETIME     NULL,
  PRIMARY KEY (variant_id),
  KEY idx_variant_product (product_id),
  CONSTRAINT fk_variant_product FOREIGN KEY (product_id) REFERENCES product(product_id)
);
```
- **왜**: 결제의 실제 대상은 SKU(판매단위). `price` 를 여기 두어 SSOT 확립. 현재 `is_required` 옵션(=판매단위)들이 여기로 이동하고, `option_price`(전체가) → `variant.price` 로 의미가 명확해짐.
- **옵션 구조 단순화 (모델 C)**: 관아수제차는 옵션 축이 하나(용량/종류)뿐이라 `product_option_group` / `product_option_value` / `variant_option_value` 를 두지 않는다. 대신 옵션 표기를 `option_label`(예: '세작 80g') 로 **관리자가 직접 입력**한다.
  - 이유: 축이 하나면 "색상 고르고 → 사이즈 고르기" 같은 축별 교차 선택이 없으므로, 옵션값을 정규화해도 이점(다축 조합·교차 필터)이 살지 않는다. 상세 화면은 variant 목록을 셀렉트에 그대로 나열하면 된다.
  - **다축(색상×용량 등)이 필요해지면** 그때 option_group/value/variant_option_value 를 도입한다. (이번 범위 밖)
- **SKU 코드 없음**: 재고/물류를 시스템으로 관리하지 않으므로 `sku_code` 는 두지 않는다. 결제·주문·장바구니는 모두 `variant_id` 로 동작.

> 재고(inventory) / 재고변동이력(inventory_transaction) 은 이번 서비스 범위에서 제외한다.
> 품절 표기가 필요하면 `product_variant.status = SOLD_OUT` 으로 수동 처리한다.

### 2.4 product_addon / product_addon_map — 추가상품(애드온)

```sql
CREATE TABLE product_addon (
  addon_id   BIGINT       NOT NULL AUTO_INCREMENT,
  name       VARCHAR(100) NOT NULL,                   -- 예: '선물용 쇼핑백'
  price      INT          NOT NULL,                   -- 추가 금액
  is_active  TINYINT(1)   NOT NULL DEFAULT 1,
  PRIMARY KEY (addon_id)
);

CREATE TABLE product_addon_map (
  product_id BIGINT NOT NULL,
  addon_id   BIGINT NOT NULL,
  PRIMARY KEY (product_id, addon_id),
  CONSTRAINT fk_addonmap_product FOREIGN KEY (product_id) REFERENCES product(product_id),
  CONSTRAINT fk_addonmap_addon   FOREIGN KEY (addon_id)   REFERENCES product_addon(addon_id)
);
```
- **왜**: 현재 `product_id=NULL` 로 매단 전역 애드온(쇼핑백) 안티패턴 제거. 애드온은 자체 카탈로그로 두고, 상품별 노출은 **오직 `product_addon_map` 매핑으로만** 제어한다(현업 정석: 상품별 명시 큐레이션). "전 상품 자동노출" 전역 플래그(`is_global`)는 예측 불가능성 때문에 두지 않는다. 샵 전체 공통 애드온(쇼핑백 등)이 필요하면 향후 장바구니/주문 레벨 애드온으로 승격한다(이번 범위 밖).
- `option_price`(추가금 의미) → `addon.price` 로 의미 분리. 이제 variant.price(전체가)와 헷갈리지 않는다.

### 2.5 product_image — 이미지 (JSON 배열 대체)

```sql
CREATE TABLE product_image (
  image_id    BIGINT       NOT NULL AUTO_INCREMENT,
  product_id  BIGINT       NOT NULL,
  variant_id  BIGINT       NULL,                      -- 옵션별 이미지면 지정, 대표는 NULL
  image_type  ENUM('THUMBNAIL','GALLERY','DETAIL') NOT NULL DEFAULT 'GALLERY',
  url         VARCHAR(500) NOT NULL,
  alt_text    VARCHAR(200) NULL,
  sort_order  INT          NOT NULL DEFAULT 0,
  PRIMARY KEY (image_id),
  KEY idx_image_product (product_id),
  CONSTRAINT fk_image_product FOREIGN KEY (product_id) REFERENCES product(product_id),
  CONSTRAINT fk_image_variant FOREIGN KEY (variant_id) REFERENCES product_variant(variant_id)
);
```
- **왜**: 현재 `images`/`infos` JSON 배열은 정렬·대표·alt·옵션별 이미지가 불가. 테이블로 분리해 `image_type`(썸네일/갤러리/상세) + `sort_order` + `variant_id`(옵션별 이미지) 제어.

### 2.6 product_review / product_review_image / product_review_stats — 리뷰

```sql
CREATE TABLE product_review (
  review_id     BIGINT       NOT NULL AUTO_INCREMENT,
  product_id    BIGINT       NOT NULL,                -- ★ bigint 로 통일 (현재 varchar)
  variant_id    BIGINT       NULL,
  order_item_id BIGINT       NULL,                    -- 검증구매 연결
  user_id       VARCHAR(100) NOT NULL,
  rating        DECIMAL(2,1) NOT NULL,
  content       VARCHAR(1000) NULL,
  status        ENUM('VISIBLE','HIDDEN','REPORTED') NOT NULL DEFAULT 'VISIBLE',
  created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (review_id),
  KEY idx_review_product (product_id),
  CONSTRAINT fk_review_product FOREIGN KEY (product_id) REFERENCES product(product_id),
  CONSTRAINT fk_review_user    FOREIGN KEY (user_id)    REFERENCES users(user_id)
);

CREATE TABLE product_review_image (
  review_image_id BIGINT NOT NULL AUTO_INCREMENT,
  review_id  BIGINT NOT NULL,
  url        VARCHAR(500) NOT NULL,
  sort_order INT NOT NULL DEFAULT 0,
  PRIMARY KEY (review_image_id),
  CONSTRAINT fk_reviewimg_review FOREIGN KEY (review_id) REFERENCES product_review(review_id)
);

CREATE TABLE product_review_stats (
  product_id   BIGINT       NOT NULL,
  avg_rating   DECIMAL(2,1) NOT NULL DEFAULT 0.0,
  review_count INT          NOT NULL DEFAULT 0,
  rating_1_cnt INT NOT NULL DEFAULT 0,
  rating_2_cnt INT NOT NULL DEFAULT 0,
  rating_3_cnt INT NOT NULL DEFAULT 0,
  rating_4_cnt INT NOT NULL DEFAULT 0,
  rating_5_cnt INT NOT NULL DEFAULT 0,
  PRIMARY KEY (product_id),
  CONSTRAINT fk_reviewstats_product FOREIGN KEY (product_id) REFERENCES product(product_id)
);
```
- **왜**: `product_id` 타입 통일(bigint) + FK. `order_item_id` 로 **검증구매(구매자 리뷰)** 표시 가능. `review_images`(JSON) → 이미지 테이블. 통계 캐시엔 평점 분포까지 담아 별점 히스토그램 표시.

### 2.7 결제·장바구니 연동 (참조 변경 + 애드온 스냅샷)

- `cart_item`: `product_option_id(varchar)` → **`product_variant_id(bigint)`** 참조로 변경(컬럼명·타입 모두 통일). 선택 애드온은 `cart_item_addon(cart_item_id, product_addon_id, quantity)`.
- `order_item`: `product_option_id` → **`product_variant_id`** 로 변경 + 기존 스냅샷(상품명·옵션명·단가) 유지. 애드온은 `order_item_addon(order_item_id, addon_name, price, quantity)` 로 스냅샷.
- **왜**: 결제 대상이 SKU로 명확해지고, 애드온이 주문에 정확히 기록됨. 스냅샷 유지로 과거 주문 불변성 보존.

---

## 3. 현재 → 신규 매핑 (마이그레이션 관점)

| 현재 | 신규 | 비고 |
|------|------|------|
| `product.category_id/category_name` | `category` 행 + `product.category_id` FK | slug=기존 category_id, name=기존 category_name |
| `product.price` | 삭제 (→ `product.display_price` 캐시) | 결제엔 variant.price 사용 |
| `product.images`(JSON) | `product_image`(type=THUMBNAIL/GALLERY) | 배열 index → sort_order |
| `product.infos`(JSON) | `product_image`(type=DETAIL) 또는 `detail_content` | |
| `product_option` where `is_required=1` | `product_variant` (`option_label`) | `option_price`→`variant.price`, 옵션명→`option_label` |
| `product_option` where `is_required=0` | `product_addon` (+ map) | 상품별 노출은 `product_addon_map` 로만 (전역 플래그 없음) |
| `review.product_id`(varchar) | `product_review.product_id`(bigint) FK | 타입 변환 필요 |
| 무료배송 5만원 하드코딩 | `product.shipping_price`(0=무료) 기준 계산 | OrderService 하드코딩 제거 |

---

## 4. dbdiagram.io 용 DBML

> DBML은 별도 파일로 분리했다: **[`redesign.dbml`](./redesign.dbml)**
> dbdiagram.io 왼쪽 에디터에 파일 내용을 통째로 붙여넣으면 전체 ERD가 그려진다.

---

## 5. 학습 포인트 & 다음 단계

- **SPU/SKU 분리**가 커머스 데이터모델의 근간. "상품 = 진열, SKU = 판매·가격" 을 몸에 익힐 것.
- **SSOT**: 가격은 한 곳(variant)에만. 진열가는 캐시로만.
- **스냅샷 vs 참조**: 카탈로그는 변하지만 주문은 불변 → 주문에는 스냅샷, 정합 추적용으로 참조(variant_id)도 함께.
- **재고 동시성**: version(낙관적 락) 또는 SELECT ... FOR UPDATE(비관적 락) — 오버셀 방지 학습 주제.
- **다음 단계 후보**: (1) 이 스키마로 마이그레이션 SQL 작성, (2) 도메인/매퍼 코드 재작성, (3) 쿠폰/프로모션 도메인 확장, (4) 검색·필터(카테고리/옵션 기반) 인덱스 설계.

> 반영 지시가 있기 전까지는 문서 단계로만 유지.
