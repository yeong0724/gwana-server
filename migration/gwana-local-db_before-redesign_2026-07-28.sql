-- MySQL dump 10.13  Distrib 8.0.35, for Linux (aarch64)
--
-- Host: localhost    Database: gwana-local-db
-- ------------------------------------------------------
-- Server version	8.0.35

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `gwana-local-db`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `gwana-local-db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `gwana-local-db`;

--
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart` (
  `cart_id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `user_id` varchar(50) NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `created_by` varchar(50) DEFAULT NULL,
  `modified_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modified_by` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`cart_id`),
  UNIQUE KEY `uk_cart_user_product_option` (`user_id`,`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=406 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
INSERT INTO `cart` VALUES (400,3,'0P94CQNA4C4SP','2026-04-21 22:38:55','0P94CQNA4C4SP','2026-04-21 22:38:54',NULL),(401,4,'0P94CQNA4C4SP','2026-04-21 22:39:02','0P94CQNA4C4SP','2026-04-21 22:39:01',NULL),(402,1,'0P94CQNA4C4SP','2026-04-22 20:20:26','0P94CQNA4C4SP','2026-04-22 20:20:25',NULL),(403,1,'0P927VHWN78QN','2026-05-07 13:08:09','0P927VHWN78QN','2026-05-07 13:08:09',NULL),(404,2,'0P94CQNA4C4SP','2026-05-07 13:30:23','0P94CQNA4C4SP','2026-05-07 13:30:22',NULL),(405,4,'0P927VHWN78QN','2026-05-08 14:11:43','0P927VHWN78QN','2026-05-08 14:11:42',NULL);
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart_item`
--

DROP TABLE IF EXISTS `cart_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_item` (
  `cart_item_id` bigint NOT NULL AUTO_INCREMENT,
  `cart_id` bigint NOT NULL,
  `product_option_id` varchar(50) NOT NULL,
  `quantity` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`cart_item_id`),
  UNIQUE KEY `uk_cart_item_option` (`cart_id`,`product_option_id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_item`
--

LOCK TABLES `cart_item` WRITE;
/*!40000 ALTER TABLE `cart_item` DISABLE KEYS */;
INSERT INTO `cart_item` VALUES (91,400,'4',1),(92,401,'5',1),(93,402,'2',2),(95,403,'2',1),(96,404,'3',1),(98,405,'6',1),(99,405,'5',1),(100,405,'1',1);
/*!40000 ALTER TABLE `cart_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inquiry`
--

DROP TABLE IF EXISTS `inquiry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inquiry` (
  `inquiry_id` bigint NOT NULL AUTO_INCREMENT,
  `upper_inquiry_id` bigint DEFAULT NULL COMMENT '답변의 대상이 되는 문의글의 ID',
  `product_id` varchar(50) DEFAULT NULL,
  `title` varchar(200) NOT NULL,
  `content` text NOT NULL,
  `is_secret` char(1) NOT NULL DEFAULT 'N',
  `is_answered` char(1) DEFAULT 'N',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `created_by` varchar(50) DEFAULT NULL,
  `modified_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modified_by` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`inquiry_id`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inquiry`
--

LOCK TABLES `inquiry` WRITE;
/*!40000 ALTER TABLE `inquiry` DISABLE KEYS */;
INSERT INTO `inquiry` VALUES (1,NULL,NULL,'태스트','<img src=\"https://gwana-images.s3.ap-northeast-2.amazonaws.com/images/inquiry/0P9CVV16GQCQK.png\" alt=\"KakaoTalk_20250613_183158404.png\" title=\"KakaoTalk_20250613_183158404.png\" wrapperstyle=\"display: flex\" style=\"max-width: 100%; height: auto; display: block; margin: 12px 0px;\"><p>테스트</p>','Y','Y','2026-01-25 20:26:45','0P927VHWN78QN','2026-01-26 23:07:13',NULL),(2,NULL,NULL,'문의하기 등록 테스트입니다.','<p>문의하기 등록 테스트입니다.<br>냉무입니다.<br>^^</p>','Y','N','2026-01-25 22:57:55','0P927VHWN78QN','2026-01-25 22:57:55',NULL),(3,NULL,'1','일이삼사오육칠팔구십일이삼사오육칠팔구십','<p>등록테스트</p><img src=\"https://gwana-images.s3.ap-northeast-2.amazonaws.com/images/inquiry/0P9EYBHE1Z0PP.png\" alt=\"신제품 카페 메뉴 인스타그램 포스트 (1).png\" title=\"신제품 카페 메뉴 인스타그램 포스트 (1).png\" wrapperstyle=\"display: flex\" style=\"max-width: 100%; height: auto; display: block; margin: 12px 0px;\"><p></p>','N','Y','2026-01-26 00:58:02','0P927VHWN78QN','2026-05-12 21:18:41',NULL),(4,1,NULL,'안녕하세요 고객님. 답변 드립니다.','<p>답변 테스트 입니다.</p>','Y','N','2026-01-26 23:07:13','0P927VHWN78QN','2026-01-26 23:07:13',NULL),(5,NULL,NULL,'테스트입니다','<img src=\"https://gwana-images.s3.ap-northeast-2.amazonaws.com/images/inquiry/0PA1MBN8YDWKJ.png\" alt=\"베이지 블랙 미니멀리스트 제품소개 상세페이지.png\" width=\"95\">\n<p>이미지 사이즈 테스트</p>','Y','N','2026-01-27 20:32:31','0P94CQNA4C4SP','2026-01-27 20:32:30',NULL),(6,NULL,NULL,'문의 테스트','<img src=\"https://gwana-images.s3.ap-northeast-2.amazonaws.com/images/inquiry/0PA1MYA9TQMSP.png\" alt=\"Design (5).png\" title=\"Design (5).png\" width=\"87\" style=\"max-width: 100%; height: auto; display: block; margin: 12px 0px;\">\n<p>이미지 정렬 테스트</p>','Y','Y','2026-01-27 20:33:29','0P94CQNA4C4SP','2026-01-28 20:12:34',NULL),(7,NULL,NULL,'테스트','<p>문의 등록 테스트</p>\n<p></p><img src=\"https://gwana-images.s3.ap-northeast-2.amazonaws.com/images/inquiry/0PA1N683FVRRZ.png\" alt=\"Design (6).png\" title=\"Design (6).png\" width=\"287\" containerstyle=\"position: relative; width: 287px; margin: 0px auto;\" wrapperstyle=\"display: flex;\" style=\"max-width: 100%; height: auto; display: block; margin: 12px 0px;\">\n<p></p>','Y','Y','2026-01-27 20:34:37','0P94CQNA4C4SP','2026-01-30 16:03:12',NULL),(8,NULL,NULL,'각종 에디터 속성적용','<p><span><strong><em><u>테스트입니다</u></em></strong></span></p>\n<p></p><img src=\"https://gwana-images.s3.ap-northeast-2.amazonaws.com/images/inquiry/0PA1P935QVRXX.png\" alt=\"브라운 모던한 뷰티 제품 추천 체크리스트 인스타그램 포스트.png\" title=\"브라운 모던한 뷰티 제품 추천 체크리스트 인스타그램 포스트.png\" width=\"314\" containerstyle=\"position: relative; width: 314px; margin: 0px auto;\" wrapperstyle=\"display: flex;\" style=\"max-width: 100%; height: auto; display: block; margin: 12px 0px;\">\n<p></p>','Y','Y','2026-01-27 20:39:26','0P94CQNA4C4SP','2026-01-30 16:01:24',NULL),(9,NULL,'1','에디터 속성 테스트','<p><span><strong><em><u>에디터 속성 테스트</u></em></strong></span></p>\n<p></p><img src=\"https://gwana-images.s3.ap-northeast-2.amazonaws.com/images/inquiry/0PA1PGPFVVRQD.png\" alt=\"Revision.png\" title=\"Revision.png\" width=\"287\" containerstyle=\"position: relative; width: 287px; margin: 0px auto;\" wrapperstyle=\"display: flex;\" style=\"max-width: 100%; height: auto; display: block; margin: 12px 0px;\">\n<p></p>','N','Y','2026-01-27 20:42:01','0P94CQNA4C4SP','2026-05-12 22:02:01',NULL),(10,NULL,'1','에디터 속성 테스트 2','<p><span style=\"font-size: 24px; color: rgb(22, 163, 74);\"><strong><em><u>에디터 속성 테스트 2</u></em></strong></span></p>\n<p></p><img src=\"https://gwana-images.s3.ap-northeast-2.amazonaws.com/images/inquiry/0PA1Q3QSX78VT.png\" alt=\"2.png\" title=\"2.png\" width=\"177\" style=\"max-width: 100%; height: auto; display: block; margin: 12px 0px; margin-left: auto; margin-right: auto;\">\n<p></p>','Y','Y','2026-01-27 20:42:50','0P94CQNA4C4SP','2026-05-12 21:18:29',NULL),(11,3,NULL,'안녕하세요 고객님. 답변 드립니다.','<p>답편하기 스크롤 테스트</p>\n<p>답편하기 스크롤 테스트</p>\n<p>답편하기 스크롤 테스트</p>\n<p>답편하기 스크롤 테스트</p>\n<p>답편하기 스크롤 테스트</p>\n<p>답편하기 스크롤 테스트</p>\n<p>답편하기 스크롤 테스트</p>\n<p>답편하기 스크롤 테스트</p>\n<p>답편하기 스크롤 테스트</p>\n<p>답편하기 스크롤 테스트</p>','Y','N','2026-01-27 22:13:15','0P927VHWN78QN','2026-01-27 22:13:14',NULL),(12,10,NULL,'안녕하세요 고객님. 답변 드립니다.','<p>답변 드립니다.</p>\n<p>오래 기다리셨습니다. ^^</p>\n<p>답변 테스트입니다.</p>\n<p>수고하세요.</p>\n<p></p>\n<p>그럼 20000</p>','Y','N','2026-01-27 22:44:44','0P927VHWN78QN','2026-01-27 22:44:43',NULL),(13,6,NULL,'안녕하세요 고객님. 답변 드립니다.','<p><span style=\"font-size: 24px; color: rgb(220, 38, 38);\"><strong><em><u>테스트는 성공적</u></em></strong></span></p>\n<p><span style=\"font-size: 24px; color: rgb(220, 38, 38);\"><strong><em><u><br>\n      감사합니다.</u></em></strong></span></p>','Y','N','2026-01-28 20:12:34','0P927VHWN78QN','2026-01-28 20:12:34',NULL),(14,9,NULL,'안녕하세요 고객님. 답변 드립니다.','<p>이상하다</p>\n<p>줄바꿈하면</p>\n<p>밀리는 느낌인데</p>','Y','N','2026-01-28 20:15:20','0P927VHWN78QN','2026-01-28 20:15:20',NULL),(15,8,NULL,'안녕하세요 고객님. 답변 드립니다.','<img src=\"https://gwana-images.s3.ap-northeast-2.amazonaws.com/images/inquiry/0PAYK72KZ18S5.png\" alt=\"4.png\" title=\"4.png\" style=\"max-width: 100%; height: auto; display: block; margin: 12px 0px;\">\n<p></p>','Y','N','2026-01-30 16:01:25','0P927VHWN78QN','2026-01-30 16:01:24',NULL),(16,7,NULL,'안녕하세요 고객님. 답변 드립니다.','<img src=\"https://gwana-images.s3.ap-northeast-2.amazonaws.com/images/inquiry/0PAYKV6FZ18TV.webp\" alt=\"KakaoTalk_20250708_192020234_08.png\" title=\"KakaoTalk_20250708_192020234_08.png\" width=\"189.727\" style=\"max-width: 100%; height: auto; display: block; margin: 12px 0px;\">\n<p>이미지 압축 테스트</p>','Y','N','2026-01-30 16:03:13','0P927VHWN78QN','2026-01-30 16:03:12',NULL),(17,NULL,NULL,'문의하기 데이터 추가 1','<p>문의하기 데이터 추가 1</p>','Y','N','2026-01-31 00:00:22','0P94CQNA4C4SP','2026-01-31 00:00:21',NULL),(18,NULL,NULL,'문의하기 데이터 추가 2','<p>문의하기 데이터 추가 2</p>','Y','N','2026-01-31 00:00:29','0P94CQNA4C4SP','2026-01-31 00:00:29',NULL),(19,NULL,NULL,'문의하기 데이터 추가 3','<p>문의하기 데이터 추가 3</p>','Y','N','2026-01-31 00:00:36','0P94CQNA4C4SP','2026-01-31 00:00:35',NULL),(20,NULL,NULL,'문의하기 데이터 추가 4','<p>문의하기 데이터 추가 4</p>','Y','N','2026-01-31 00:00:47','0P94CQNA4C4SP','2026-01-31 00:00:46',NULL),(21,NULL,NULL,'문의하기 데이터 추가 5','<p>문의하기 데이터 추가 5</p>','Y','N','2026-01-31 00:00:59','0P94CQNA4C4SP','2026-01-31 00:00:58',NULL),(22,NULL,NULL,'문의하기 데이터 추가 6','<p>문의하기 데이터 추가 6</p>','Y','N','2026-01-31 00:01:07','0P94CQNA4C4SP','2026-01-31 00:01:07',NULL),(23,NULL,NULL,'문의하기 데이터 추가 7','<p>문의하기 데이터 추가 7</p>','Y','N','2026-01-31 00:02:34','0P94CQNA4C4SP','2026-01-31 00:02:34',NULL),(24,NULL,NULL,'문의하기 데이터 추가 8','<p>문의하기 데이터 추가 8</p>','Y','N','2026-01-31 00:02:43','0P94CQNA4C4SP','2026-01-31 00:02:42',NULL),(25,NULL,NULL,'문의하기 데이터 추가 9','<p>문의하기 데이터 추가 9</p>','Y','N','2026-01-31 00:02:51','0P94CQNA4C4SP','2026-01-31 00:02:51',NULL),(26,NULL,NULL,'문의하기 데이터 추가 10','<p>문의하기 데이터 추가 10</p>','Y','N','2026-01-31 00:02:56','0P94CQNA4C4SP','2026-01-31 00:02:56',NULL),(27,NULL,NULL,'문의하기 데이터 추가 11','<p>문의하기 데이터 추가 10</p>','Y','N','2026-01-31 00:02:57','0P94CQNA4C4SP','2026-01-31 00:02:57',NULL),(28,NULL,NULL,'문의하기 데이터 추가 12','<p>문의하기 데이터 추가 10</p>','Y','N','2026-01-31 00:02:58','0P94CQNA4C4SP','2026-01-31 00:02:58',NULL),(29,NULL,NULL,'문의하기 데이터 추가 13','<p>문의하기 데이터 추가 10</p>','Y','N','2026-01-31 00:02:59','0P94CQNA4C4SP','2026-01-31 00:02:59',NULL),(30,NULL,NULL,'문의하기 데이터 추가 14','<p>문의하기 데이터 추가 10</p>','Y','N','2026-01-31 00:12:56','0P94CQNA4C4SP','2026-01-31 00:12:56',NULL),(31,NULL,NULL,'문의하기 데이터 추가 15','<p>문의하기 데이터 추가 10</p>','Y','N','2026-01-31 00:12:57','0P94CQNA4C4SP','2026-01-31 00:12:57',NULL),(32,NULL,NULL,'문의하기 데이터 추가 16','<p>문의하기 데이터 추가 10</p>','Y','N','2026-01-31 00:12:58','0P94CQNA4C4SP','2026-01-31 00:12:58',NULL),(33,NULL,NULL,'문의하기 데이터 추가 17','<p>문의하기 데이터 추가 10</p>','Y','N','2026-01-31 00:12:59','0P94CQNA4C4SP','2026-01-31 00:12:59',NULL),(34,NULL,NULL,'문의하기','<p>문의 등록 내용</p>','Y','N','2026-05-12 12:13:44','0P94CQNA4C4SP','2026-05-12 12:13:44',NULL),(35,NULL,NULL,'문의등록테스트','<p>문의등록테스트</p>','Y','N','2026-05-12 12:15:00','0P94CQNA4C4SP','2026-05-12 12:15:00',NULL),(36,NULL,NULL,'문의후 재조회 테스트','<p>문의후 재조회 테스트</p>','Y','N','2026-05-12 12:16:20','0P94CQNA4C4SP','2026-05-12 12:16:19',NULL),(37,NULL,NULL,'등록테스트','<p>등록테스트</p>','Y','N','2026-05-12 12:34:32','0P94CQNA4C4SP','2026-05-12 12:34:32',NULL),(38,NULL,NULL,'문의드려요','<p>문의드려요</p>','Y','N','2026-05-12 12:44:00','0P94CQNA4C4SP','2026-05-12 12:44:00',NULL),(39,NULL,NULL,'문의드려요 2','<p>문의드려요 2</p>','Y','N','2026-05-12 12:44:15','0P94CQNA4C4SP','2026-05-12 12:44:14',NULL),(40,NULL,NULL,'문의드려요3','<p>문의드려요3</p>','Y','N','2026-05-12 12:44:28','0P94CQNA4C4SP','2026-05-12 12:44:28',NULL),(41,NULL,NULL,'문의해요 4','<p>문의해요 4</p>','Y','N','2026-05-12 12:45:34','0P94CQNA4C4SP','2026-05-12 12:45:34',NULL),(42,NULL,NULL,'문의해요 5','<p>문의해요 5</p>','Y','N','2026-05-12 12:45:44','0P94CQNA4C4SP','2026-05-12 12:45:43',NULL),(43,NULL,NULL,'문의합니다 6','<p>문의합니다 6</p>','Y','N','2026-05-12 12:50:41','0P94CQNA4C4SP','2026-05-12 12:50:40',NULL);
/*!40000 ALTER TABLE `inquiry` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `order_item_id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` varchar(100) NOT NULL,
  `product_id` bigint NOT NULL,
  `product_name` varchar(255) NOT NULL,
  `product_thumbnail_url` varchar(255) NOT NULL,
  `category_name` varchar(50) NOT NULL,
  `product_option_id` bigint NOT NULL,
  `option_name` varchar(100) NOT NULL,
  `option_price` int NOT NULL DEFAULT '0',
  `quantity` int NOT NULL DEFAULT '0',
  `is_required` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`order_item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (21,'0QAFK7BMVJRXQ',1,'관아수제차 세작 유기농 하동녹차','images/product/thumbnail/0Q5AWH8VFZCYR.png','녹차',2,'세작 (80g)',70000,1,1),(22,'0QAFK7BMVJRXQ',4,'관아수제차 드립백','images/product/thumbnail/0Q5AYG7M3ZCVR.png','발효차',5,'녹차 드립백 (1백 3개입)',10000,1,1),(23,'0QAFK7BMVJRXQ',4,'관아수제차 드립백','images/product/thumbnail/0Q5AYG7M3ZCVR.png','발효차',6,'홍차 드립백 (1팩 3개입)',10000,1,1),(24,'0QAFK7BMVJRXQ',4,'관아수제차 드립백','images/product/thumbnail/0Q5AYG7M3ZCVR.png','발효차',1,'선물용 쇼핑백',1000,1,0),(25,'0QBPYB8HK6MZP',1,'관아수제차 세작 유기농 하동녹차','images/product/thumbnail/0Q5AWH8VFZCYR.png','녹차',2,'세작 (80g)',70000,2,1),(26,'0QBPYB8HK6MZP',2,'관아수제차 우전 유기농 하동녹차','images/product/thumbnail/0Q5AXFD9BZCZQ.png','녹차',3,'우전 (80g)',110000,2,1),(27,'0QBPYB8HK6MZP',3,'관아수제차 발효차 유기농 하동홍차','images/product/thumbnail/0Q5AY0JDQZCZA.png','발효차',4,'홍차 (80g)',60000,1,1),(28,'0QBPYB8HK6MZP',4,'관아수제차 드립백','images/product/thumbnail/0Q5AYG7M3ZCVR.png','발효차',5,'녹차 드립백 (1백 3개입)',10000,1,1),(29,'0QBPYGP836MXT',1,'관아수제차 세작 유기농 하동녹차','images/product/thumbnail/0Q5AWH8VFZCYR.png','녹차',2,'세작 (80g)',70000,2,1),(30,'0QBPYGP836MXT',2,'관아수제차 우전 유기농 하동녹차','images/product/thumbnail/0Q5AXFD9BZCZQ.png','녹차',3,'우전 (80g)',110000,1,1),(31,'0QBPYGP836MXT',3,'관아수제차 발효차 유기농 하동홍차','images/product/thumbnail/0Q5AY0JDQZCZA.png','발효차',4,'홍차 (80g)',60000,1,1),(32,'0QBPYGP836MXT',4,'관아수제차 드립백','images/product/thumbnail/0Q5AYG7M3ZCVR.png','발효차',5,'녹차 드립백 (1백 3개입)',10000,1,1);
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `order_id` varchar(100) NOT NULL,
  `order_status` varchar(20) NOT NULL,
  `product_amount` int NOT NULL DEFAULT '0',
  `shipping_fee` int NOT NULL DEFAULT '0',
  `discount_amount` int NOT NULL DEFAULT '0',
  `total_amount` int NOT NULL DEFAULT '0',
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `sender_name` varchar(100) DEFAULT NULL,
  `sender_phone` varchar(20) DEFAULT NULL,
  `recipient_name` varchar(100) DEFAULT NULL,
  `recipient_phone` varchar(20) DEFAULT NULL,
  `zonecode` varchar(10) DEFAULT NULL,
  `road_address` varchar(255) DEFAULT NULL,
  `detail_address` varchar(255) DEFAULT NULL,
  `delivery_request` varchar(500) DEFAULT NULL,
  `delivery_request_detail` varchar(500) DEFAULT NULL,
  `ordered_at` datetime DEFAULT NULL,
  `paid_at` datetime DEFAULT NULL,
  `cancelled_at` datetime DEFAULT NULL,
  `refunded_at` datetime DEFAULT NULL,
  `modified_at` datetime DEFAULT NULL,
  `modified_by` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES ('0QAFK7BMVJRXQ','PENDING_PAYMENT',91000,0,0,91000,'2026-05-08 15:08:40','0P927VHWN78QN',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),('0QBPYB8HK6MZP','PENDING_PAYMENT',430000,0,0,430000,'2026-05-12 10:49:52','0P94CQNA4C4SP',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),('0QBPYGP836MXT','PENDING_PAYMENT',320000,0,0,320000,'2026-05-12 10:50:36','0P94CQNA4C4SP',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_info`
--

DROP TABLE IF EXISTS `payment_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_info` (
  `session_id` varchar(50) NOT NULL,
  `order_id` varchar(50) NOT NULL,
  `user_id` varchar(50) NOT NULL,
  `total_price` int NOT NULL,
  `total_shipping_price` int NOT NULL,
  `total_amount` int NOT NULL,
  `sender_name` varchar(100) NOT NULL,
  `sender_phone` varchar(20) NOT NULL,
  `recipient_name` varchar(100) NOT NULL,
  `recipient_phone` varchar(20) NOT NULL,
  `zonecode` varchar(10) NOT NULL,
  `road_address` varchar(255) NOT NULL,
  `detail_address` varchar(255) NOT NULL,
  `delivery_request` varchar(500) NOT NULL,
  `delivery_request_detail` varchar(500) DEFAULT NULL,
  `expires_at` datetime NOT NULL,
  PRIMARY KEY (`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_info`
--

LOCK TABLES `payment_info` WRITE;
/*!40000 ALTER TABLE `payment_info` DISABLE KEYS */;
INSERT INTO `payment_info` VALUES ('','f0088bbe-4e7e-43c9-8f37-3d16070096a3','0P927VHWN78QN',0,0,0,'김진영','01035618411','이은경','01030777785','52302','경남 하동군 화개면 목압길 24-2','관아수제차 1층','LEAVE_AT_GUARD','','2026-05-08 15:43:39'),('0P4Z71SVKG8VJ','1736c6c7-fea4-40bf-9b9a-ab76b82910ee','0P4W4F2YG7WQQ',180000,0,180000,'김진영','01035618411','이은경','01030777785','52302','경남 하동군 화개면 목압길 24-2','관아수제차 1층','LEAVE_AT_GUARD','','2026-01-12 02:33:26'),('0P4ZHH2V744QW','78ceb273-0e6b-4036-ba29-3c537005b6fe','0P4W4F2YG7WQQ',180000,0,180000,'김진영','01035618411','이은경','01030777785','52302','경남 하동군 화개면 목압길 24-2','관아수제차 1층','LEAVE_AT_GUARD','','2026-01-12 03:19:13'),('0P73HZNXFHRRD','9862b8eb-9a9d-4184-a744-69e8f96b0ff0','0P4W4F2YG7WQQ',155000,0,0,'김진영','01035618411','이은경','01030777785','52302','경남 하동군 화개면 목압길 24-2','관아수제차 1층','LEAVE_AT_GUARD','','2026-01-18 17:48:20');
/*!40000 ALTER TABLE `payment_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_session`
--

DROP TABLE IF EXISTS `payment_session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_session` (
  `payment_session_id` bigint NOT NULL AUTO_INCREMENT,
  `session_id` varchar(50) NOT NULL,
  `product_id` varchar(50) NOT NULL,
  `quantity` int NOT NULL DEFAULT '0',
  `user_id` varchar(50) NOT NULL,
  `expires_at` datetime NOT NULL,
  PRIMARY KEY (`payment_session_id`)
) ENGINE=InnoDB AUTO_INCREMENT=335 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_session`
--

LOCK TABLES `payment_session` WRITE;
/*!40000 ALTER TABLE `payment_session` DISABLE KEYS */;
INSERT INTO `payment_session` VALUES (327,'0P73HZNXFHRRD','1',1,'0P4W4F2YG7WQQ','2026-01-18 17:48:11'),(328,'0P73HZNXFHRRD','3',1,'0P4W4F2YG7WQQ','2026-01-18 17:48:11'),(329,'0P73HZNXFHRRD','4',0,'0P4W4F2YG7WQQ','2026-01-18 17:48:11'),(330,'0P73HZNXFHRRD','5',1,'0P4W4F2YG7WQQ','2026-01-18 17:48:11'),(333,'0PD6JSQX6N8TY','1',1,'0P927VHWN78QN','2026-02-06 16:14:43'),(334,'0PD6JSQX6N8TY','4',0,'0P927VHWN78QN','2026-02-06 16:14:43');
/*!40000 ALTER TABLE `payment_session` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `product_id` bigint NOT NULL AUTO_INCREMENT,
  `product_name` varchar(255) NOT NULL,
  `category_id` varchar(100) NOT NULL,
  `category_name` varchar(50) NOT NULL,
  `images` json DEFAULT NULL,
  `infos` json DEFAULT NULL,
  `price` int NOT NULL,
  `shipping_price` int DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `created_by` varchar(50) DEFAULT NULL,
  `modified_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `modified_by` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,'관아수제차 세작 유기농 하동녹차','greenTea','녹차','[\"images/product/thumbnail/0Q5AWH8VFZCYR.png\", \"images/product/thumbnail/0Q5AWHW3FZCXM.png\", \"images/product/thumbnail/0Q5AWJ8F3ZCZT.png\"]','[\"images/product/info/0QA3ZZAN9BWTM.webp\"]',70000,0,'2025-12-05 14:26:16',NULL,'2026-05-07 12:06:39','0P927VHWN78QN'),(2,'관아수제차 우전 유기농 하동녹차','greenTea','녹차','[\"images/product/thumbnail/0Q5AXFD9BZCZQ.png\", \"images/product/thumbnail/0Q5AXFWBBZCV8.png\", \"images/product/thumbnail/0Q5AXGCJ7ZCXH.png\", \"images/product/thumbnail/0Q5AXGWM3ZCZZ.png\"]','[\"/images/product/greenTea/info/greenTeaUjeon_1.webp\", \"/images/product/greenTea/info/greenTeaUjeon_2.webp\", \"/images/product/greenTea/info/greenTeaUjeon_3.webp\", \"/images/product/greenTea/info/greenTeaUjeon_4.webp\", \"/images/product/greenTea/info/greenTeaUjeon_5.webp\", \"/images/product/greenTea/info/greenTeaUjeon_6.webp\", \"/images/product/greenTea/info/greenTeaUjeon_7.webp\"]',110000,0,'2025-12-05 14:26:16',NULL,'2026-04-22 15:25:00',NULL),(3,'관아수제차 발효차 유기농 하동홍차','blackTea','발효차','[\"images/product/thumbnail/0Q5AY0JDQZCZA.png\", \"images/product/thumbnail/0Q5AY12DQZCZ0.png\", \"images/product/thumbnail/0Q5AY1F1FZCTN.png\"]','[\"/images/product/blackTea/info/fermentedTea_1.webp\", \"/images/product/blackTea/info/fermentedTea_2.webp\", \"/images/product/blackTea/info/fermentedTea_3.webp\", \"/images/product/blackTea/info/fermentedTea_4.webp\", \"/images/product/blackTea/info/fermentedTea_5.webp\", \"/images/product/blackTea/info/fermentedTea_6.webp\", \"/images/product/blackTea/info/fermentedTea_7.webp\"]',60000,0,'2025-12-05 14:26:16',NULL,'2026-04-22 15:27:16',NULL),(4,'관아수제차 드립백','blackTea','발효차','[\"images/product/thumbnail/0Q5AYG7M3ZCVR.png\", \"images/product/thumbnail/0Q5AYGPRKZCZX.png\", \"images/product/thumbnail/0Q5AYH29BZCYS.png\"]','[\"/images/product/blackTea/info/fermentedTeaDripBag_1.webp\", \"/images/product/blackTea/info/fermentedTeaDripBag_2.webp\", \"/images/product/blackTea/info/fermentedTeaDripBag_3.webp\", \"/images/product/blackTea/info/fermentedTeaDripBag_4.webp\", \"/images/product/blackTea/info/fermentedTeaDripBag_5.webp\", \"/images/product/blackTea/info/fermentedTeaDripBag_6.webp\", \"/images/product/blackTea/info/fermentedTeaDripBag_7.webp\", \"/images/product/blackTea/info/fermentedTeaDripBag_8.webp\", \"/images/product/blackTea/info/fermentedTeaDripBag_9.webp\", \"/images/product/blackTea/info/fermentedTeaDripBag_10.webp\", \"/images/product/blackTea/info/fermentedTeaDripBag_11.webp\", \"/images/product/blackTea/info/fermentedTeaDripBag_12.webp\", \"/images/product/blackTea/info/fermentedTeaDripBag_13.webp\", \"/images/product/blackTea/info/fermentedTeaDripBag_14.webp\"]',10000,4000,'2025-12-05 14:26:16',NULL,'2026-04-22 15:29:23',NULL),(5,'관아수제차 가을 무우차 무카페인 전통차','substituteTea','대용차','[\"images/product/thumbnail/0Q5AZ1SMVZCV4.png\", \"images/product/thumbnail/0Q5AZ36J7ZCZZ.png\"]','[\"/images/product/substituteTea/info/autumnRadishTea_1.webp\", \"/images/product/substituteTea/info/autumnRadishTea_2.webp\", \"/images/product/substituteTea/info/autumnRadishTea_3.webp\", \"/images/product/substituteTea/info/autumnRadishTea_4.webp\", \"/images/product/substituteTea/info/autumnRadishTea_5.webp\", \"/images/product/substituteTea/info/autumnRadishTea_6.webp\"]',25000,4000,'2025-12-05 14:26:16',NULL,'2026-04-22 15:31:52',NULL),(6,'관아수제차 야생 쑥차 무카페인 건강차','substituteTea','대용차','[\"images/product/thumbnail/0Q5AZ8CDFZCV7.png\", \"images/product/thumbnail/0Q5AZ8Y23ZCXP.png\"]','[\"/images/product/substituteTea/info/wildMugwortTea_1.webp\", \"/images/product/substituteTea/info/wildMugwortTea_2.webp\", \"/images/product/substituteTea/info/wildMugwortTea_3.webp\", \"/images/product/substituteTea/info/wildMugwortTea_4.webp\", \"/images/product/substituteTea/info/wildMugwortTea_5.webp\", \"/images/product/substituteTea/info/wildMugwortTea_6.webp\", \"/images/product/substituteTea/info/wildMugwortTea_7.webp\"]',30000,4000,'2025-12-05 14:26:16',NULL,'2026-04-22 15:32:39',NULL),(7,'관아수제차 구중구포 구기자차 무카페인 전통차','substituteTea','대용차','[\"images/product/thumbnail/0Q5AZJN1VZCVW.png\", \"images/product/thumbnail/0Q5AZK10VZCZX.png\", \"images/product/thumbnail/0Q5AZKDQQZCY0.png\"]','[\"/images/product/substituteTea/info/gugijaTea_1.webp\", \"/images/product/substituteTea/info/gugijaTea_2.webp\", \"/images/product/substituteTea/info/gugijaTea_3.webp\", \"/images/product/substituteTea/info/gugijaTea_4.webp\", \"/images/product/substituteTea/info/gugijaTea_5.webp\", \"/images/product/substituteTea/info/gugijaTea_6.webp\"]',70000,0,'2025-12-05 14:26:16',NULL,'2026-04-22 15:34:05',NULL),(8,'관아수제차 지리산 목련꽃차 무카페인 전통차','substituteTea','대용차','[\"images/product/thumbnail/0Q5AZWJFQZCZJ.png\", \"images/product/thumbnail/0Q5AZWZEVZCZJ.png\", \"images/product/thumbnail/0Q5AZXC0QZCX2.png\"]','[\"/images/product/substituteTea/info/magnoliaFlowerTea_1.webp\", \"/images/product/substituteTea/info/magnoliaFlowerTea_2.webp\", \"/images/product/substituteTea/info/magnoliaFlowerTea_3.webp\", \"/images/product/substituteTea/info/magnoliaFlowerTea_4.webp\", \"/images/product/substituteTea/info/magnoliaFlowerTea_5.webp\", \"/images/product/substituteTea/info/magnoliaFlowerTea_6.webp\", \"/images/product/substituteTea/info/magnoliaFlowerTea_7.webp\"]',30000,4000,'2025-12-05 14:26:16',NULL,'2026-04-22 15:35:26',NULL),(9,'관아수제차 매화꽃차 매화차 무카페인 전통 꽃차 봄 향기','substituteTea','대용차','[\"images/product/thumbnail/0Q5BAY32J5RVJ.png\", \"images/product/thumbnail/0Q5BAYXK25RX0.png\", \"images/product/thumbnail/0Q5BAZFP65RZX.png\"]','[\"images/product/info/0Q5BBP72J5RZJ.png\", \"images/product/info/0Q5BE9VM25RZ3.png\", \"images/product/info/0Q5BEAHBA5RVZ.png\", \"images/product/info/0Q5BED7VE5RTW.png\", \"images/product/info/0Q5BEDRMP5RSV.png\", \"images/product/info/0Q5BEFEHE5RZX.png\", \"images/product/info/0Q5BEG4BE5RZF.png\", \"images/product/info/0Q5BEGJEJ5RXS.png\", \"images/product/info/0Q5BEGYZJ5RZX.png\", \"images/product/info/0Q5BEH9ZT5RVS.png\"]',20000,4000,'2026-04-22 16:22:27',NULL,'2026-04-22 16:39:20',NULL);
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_option`
--

DROP TABLE IF EXISTS `product_option`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_option` (
  `product_option_id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint DEFAULT NULL,
  `option_name` varchar(100) NOT NULL,
  `option_price` int DEFAULT '0',
  `is_required` tinyint(1) DEFAULT '1',
  `is_quantity_adjustable` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`product_option_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_option`
--

LOCK TABLES `product_option` WRITE;
/*!40000 ALTER TABLE `product_option` DISABLE KEYS */;
INSERT INTO `product_option` VALUES (1,NULL,'선물용 쇼핑백',1000,0,1),(2,1,'세작 (80g)',70000,1,1),(3,2,'우전 (80g)',110000,1,1),(4,3,'홍차 (80g)',60000,1,1),(5,4,'녹차 드립백 (1백 3개입)',10000,1,1),(6,4,'홍차 드립백 (1팩 3개입)',10000,1,1),(7,5,'무우차 (100g)',25000,1,1),(8,6,'쑥차 (40g)',30000,1,1),(9,7,'구기자차 (100g)',70000,1,1),(10,8,'목련 꽃차 (20 ~ 25송이)',30000,1,1),(13,9,'매화차 (60ml)',20000,1,1);
/*!40000 ALTER TABLE `product_option` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_review_stats`
--

DROP TABLE IF EXISTS `product_review_stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_review_stats` (
  `product_id` bigint NOT NULL,
  `avg_rating` decimal(2,1) NOT NULL DEFAULT '0.0',
  `review_count` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_review_stats`
--

LOCK TABLES `product_review_stats` WRITE;
/*!40000 ALTER TABLE `product_review_stats` DISABLE KEYS */;
INSERT INTO `product_review_stats` VALUES (1,3.7,7),(2,5.0,1),(5,4.5,1);
/*!40000 ALTER TABLE `product_review_stats` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refresh_token`
--

DROP TABLE IF EXISTS `refresh_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `refresh_token` (
  `refresh_token_id` varchar(100) NOT NULL,
  `user_id` varchar(100) NOT NULL COMMENT 'ì‚¬ìš©ìž ID',
  `token_hash` char(64) NOT NULL COMMENT 'Refresh Token ì›ë¬¸ì˜ SHA-256 hex',
  `expires_at` datetime NOT NULL COMMENT 'Refresh Token ë§Œë£Œì‹œê°',
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `modified_at` datetime DEFAULT NULL,
  `modified_by` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`refresh_token_id`),
  UNIQUE KEY `uk_refresh_token_user` (`user_id`),
  KEY `idx_refresh_token_hash` (`token_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ë¦¬í”„ë ˆì‹œ í† í°(í•´ì‹œ) ì €ìž¥';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refresh_token`
--

LOCK TABLES `refresh_token` WRITE;
/*!40000 ALTER TABLE `refresh_token` DISABLE KEYS */;
INSERT INTO `refresh_token` VALUES ('0R4H8FZSTSGXK','0P927VHWN78QN','b17e8875a41ca0844487c48381b4c3a806dafd4b01955a8838255b22895122dc','2026-07-29 14:44:34','2026-07-28 13:43:27',NULL,'2026-07-28 14:44:33',NULL);
/*!40000 ALTER TABLE `refresh_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review`
--

DROP TABLE IF EXISTS `review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review` (
  `review_id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` varchar(50) NOT NULL,
  `content` varchar(500) NOT NULL,
  `review_images` json DEFAULT NULL,
  `rating` decimal(2,1) NOT NULL DEFAULT '0.0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `created_by` varchar(50) DEFAULT NULL,
  `modified_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modified_by` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`review_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review`
--

LOCK TABLES `review` WRITE;
/*!40000 ALTER TABLE `review` DISABLE KEYS */;
INSERT INTO `review` VALUES (1,'5','가을무우차 아주 좋아요.\n또 사먹고 싶어요!!','[\"images/review/0PB1VC01S6RST.webp\", \"images/review/0PB1VC01N6RNH.webp\"]',4.5,'2026-01-30 23:35:16','0P94CQNA4C4SP','2026-01-30 23:35:16',NULL),(2,'1','세작 유기농 하동녹차 \n\nVery Good ~ !','[\"images/review/0PB93N25MF4ZM.webp\", \"images/review/0PB93N25MF4ZK.webp\", \"images/review/0PB93N25MF4ZJ.webp\", \"images/review/0PB93N25MF4ZN.webp\", \"images/review/0PB93N25MF4ZH.webp\"]',5.0,'2026-01-31 16:30:08','0P94CQNA4C4SP','2026-01-31 16:30:08',NULL),(3,'1','세작의 맛이 아주 좋습니다.\n\n재구매의사 100%\n\n감사합니다.\n\n(1)','[\"images/review/0PB990PZGF4VP.webp\"]',4.5,'2026-01-31 16:53:34','0P94CQNA4C4SP','2026-02-11 21:40:17',NULL),(4,'1','세작의 맛이 아주 좋습니다.\n\n재구매의사 100%\n\n감사합니다.\n\n(2)\n','[\"images/review/0PB1VC01S6RST.webp\", \"images/review/0PB1VC01N6RNH.webp\"]',4.5,'2026-02-01 14:53:34','0P94CQNA4C4SP','2026-02-11 21:40:53',NULL),(5,'1','세작의 맛이 아주 좋습니다.\n\n재구매의사 100%\n\n감사합니다.\n\n(3)','[\"images/review/0PB990PZGF4VP.webp\"]',4.0,'2026-02-02 04:53:34','0P94CQNA4C4SP','2026-02-02 04:53:34',NULL),(6,'1','세작의 맛이 아주 좋습니다.\n\n재구매의사 100%\n\n감사합니다.\n\n(4)\n',NULL,1.5,'2026-02-03 19:53:34','0P94CQNA4C4SP','2026-02-11 22:25:28',NULL),(7,'1','세작의 맛이 아주 좋습니다.\n\n재구매의사 100%\n\n감사합니다.\n\n(5)\n','[\"images/review/0PB93N25MF4ZM.webp\", \"images/review/0PB93N25MF4ZK.webp\", \"images/review/0PB93N25MF4ZJ.webp\", \"images/review/0PB93N25MF4ZN.webp\", \"images/review/0PB93N25MF4ZH.webp\"]',3.5,'2026-02-04 08:53:34','0P94CQNA4C4SP','2026-02-11 22:25:28',NULL),(8,'1','세작의 맛이 아주 좋습니다.\n\n재구매의사 100%\n\n감사합니다.\n\n(6)\n',NULL,3.0,'2026-02-05 21:53:34','0P94CQNA4C4SP','2026-02-11 22:25:28',NULL),(9,'2','리뷰 통계 갱신 테스트를 위한 리뷰 등록\n','[\"images/review/0Q3BB7GTJGRZT.webp\"]',5.0,'2026-04-16 11:17:02','0P94CQNA4C4SP','2026-04-16 11:17:02',NULL);
/*!40000 ALTER TABLE `review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `social_account`
--

DROP TABLE IF EXISTS `social_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `social_account` (
  `social_account_id` varchar(100) NOT NULL COMMENT '소셜 계정 ID',
  `user_id` varchar(100) NOT NULL COMMENT '사용자 ID (FK)',
  `provider` varchar(50) NOT NULL COMMENT '소셜 로그인 제공자 (예: kakao, google)',
  `provider_id` bigint NOT NULL COMMENT '소셜 계정의 고유 ID',
  `access_token` text COMMENT 'ì†Œì…œ provider access token (ë¡œê·¸ì•„ì›ƒ/ì—°ë™í•´ì œìš©)',
  `access_token_expires_at` datetime DEFAULT NULL COMMENT 'ì†Œì…œ access token ë§Œë£Œì‹œê°',
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `modified_at` datetime DEFAULT NULL,
  `modified_by` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`social_account_id`),
  UNIQUE KEY `uk_provider_social_id` (`provider`,`provider_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='소셜로그인 사용자 관리 테이블';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `social_account`
--

LOCK TABLES `social_account` WRITE;
/*!40000 ALTER TABLE `social_account` DISABLE KEYS */;
INSERT INTO `social_account` VALUES ('0P927VJ0N78QX','0P927VHWN78QN','kakao',3754082803,'FA-dsf4BjdWC8_e5oP7Xr6K5pWFaP6XTAAAAAQoNFZsAAAGfp0Db1CJyl_dg0lnq',NULL,'2026-01-24 19:21:58',NULL,'2026-07-28 14:44:33',NULL),('0P94CQNBRC4SN','0P94CQNA4C4SP','kakao',4465641748,'vphui5tpvhoIJwphnBxeGRhNP3veLuqeAAAAAQoNDV8AAAGfpwayU63XznpenZPe',NULL,'2026-01-25 00:22:54',NULL,'2026-07-28 13:41:01',NULL);
/*!40000 ALTER TABLE `social_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` varchar(100) NOT NULL,
  `customer_key` varchar(100) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `profile_image` varchar(100) DEFAULT NULL,
  `zonecode` varchar(20) DEFAULT NULL,
  `road_address` varchar(100) DEFAULT NULL,
  `detail_address` varchar(300) DEFAULT NULL,
  `role` enum('ADMIN','GENERAL') NOT NULL DEFAULT 'GENERAL',
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `modified_at` datetime DEFAULT NULL,
  `modified_by` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_users_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('0P927VHWN78QN','517d5b4e-4a3e-4e7d-800e-536bf61b4d41','김진영',NULL,'kid-4211@daum.net','01035618411','images/profile/0P9EGF82HYRQR.png','04808','서울 성동구 자동차시장3길 93','824호 와이하우스','ADMIN','2026-01-24 19:21:58',NULL,'2026-01-25 23:57:20','0P927VHWN78QN'),('0P94CQNA4C4SP','17f7c04d-a1f8-478e-8373-4452afe1392a','김진영',NULL,'kid4211@kakao.com','01035618411','images/profile/0PABRBHNKFWXY.webp','04808','서울 성동구 자동차시장3길 93','824호 와이하우스','GENERAL','2026-01-25 00:22:54',NULL,'2026-01-28 20:06:17','0P94CQNA4C4SP');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-28 14:54:51
