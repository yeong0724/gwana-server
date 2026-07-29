-- =====================================================================
-- 2026-07-29  soft delete 컬럼 통일: is_deleted (deleted_at 기반 생성컬럼)
-- ---------------------------------------------------------------------
-- product / product_variant / product_addon 모두 is_deleted 로 조회 통일.
-- is_deleted 는 (deleted_at IS NOT NULL) 로부터 자동 생성(STORED)되어
-- deleted_at 이 세팅되면 자동으로 1 이 된다 (드리프트 불가).
-- addon 은 기존 is_active(삭제 플래그)를 deleted_at 으로 이관 후 컬럼 제거.
-- 실행: mysql -uroot -p1234 gwana-local-db < 2026-07-29_soft_delete_is_deleted.sql
-- =====================================================================

-- 1) product : deleted_at 이미 존재 → is_deleted 생성컬럼 + 인덱스
ALTER TABLE product
    ADD COLUMN is_deleted TINYINT(1) AS (deleted_at IS NOT NULL) STORED AFTER deleted_at;
ALTER TABLE product
    ADD INDEX idx_product_is_deleted (is_deleted);

-- 2) product_variant : 동일
ALTER TABLE product_variant
    ADD COLUMN is_deleted TINYINT(1) AS (deleted_at IS NOT NULL) STORED AFTER deleted_at;
ALTER TABLE product_variant
    ADD INDEX idx_variant_is_deleted (is_deleted);

-- 3) product_addon : is_active(삭제 플래그) → deleted_at + is_deleted 이관
ALTER TABLE product_addon
    ADD COLUMN deleted_at DATETIME NULL;
UPDATE product_addon SET deleted_at = CURRENT_TIMESTAMP WHERE is_active = 0;
ALTER TABLE product_addon
    ADD COLUMN is_deleted TINYINT(1) AS (deleted_at IS NOT NULL) STORED AFTER deleted_at;
ALTER TABLE product_addon
    ADD INDEX idx_addon_is_deleted (is_deleted);
ALTER TABLE product_addon
    DROP COLUMN is_active;

-- =====================================================================
-- 롤백(참고)
-- ALTER TABLE product_addon ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT TRUE;
-- UPDATE product_addon SET is_active = (deleted_at IS NULL);
-- ALTER TABLE product_addon DROP COLUMN is_deleted, DROP COLUMN deleted_at;
-- ALTER TABLE product_variant DROP COLUMN is_deleted;
-- ALTER TABLE product DROP COLUMN is_deleted;
-- =====================================================================
