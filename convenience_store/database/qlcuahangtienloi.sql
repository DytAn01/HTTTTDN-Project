CREATE DATABASE  IF NOT EXISTS `qlcuahangtienloi` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `qlcuahangtienloi`;
-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: qlcuahangtienloi
-- ------------------------------------------------------
-- Server version	8.0.39

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `chamcong`
--

DROP TABLE IF EXISTS `chamcong`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chamcong` (
  `maChamCong` int NOT NULL AUTO_INCREMENT,
  `maNhanVien` int NOT NULL,
  `soGioLamViec` int DEFAULT NULL,
  `soNgayLamViec` int DEFAULT NULL,
  `soGioLamThem` int DEFAULT NULL,
  `chiTiet` varchar(500) DEFAULT NULL,
  `thangChamCong` int DEFAULT NULL,
  `namChamCong` int DEFAULT NULL,
  PRIMARY KEY (`maChamCong`),
  KEY `fk_chamcong_nhanvien_idx` (`maNhanVien`),
  CONSTRAINT `fk_chamcong_nhanvien` FOREIGN KEY (`maNhanVien`) REFERENCES `nhanvien` (`maNhanVien`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chamcong`
--

LOCK TABLES `chamcong` WRITE;
/*!40000 ALTER TABLE `chamcong` DISABLE KEYS */;
INSERT INTO `chamcong` VALUES (1,1,1,10,1,NULL,9,2024),(2,2,2,12,2,NULL,9,2024),(3,3,3,13,3,NULL,9,2024),(4,4,4,14,4,NULL,9,2024),(5,5,5,15,5,NULL,9,2024),(6,6,6,26,6,NULL,9,2024),(7,7,7,17,7,NULL,9,2024),(8,8,8,18,8,NULL,9,2024),(9,9,9,16,9,NULL,9,2024),(10,10,10,13,10,NULL,9,2024),(11,1,0,0,0,'',12,2024),(12,2,0,0,0,'',12,2024),(13,3,0,0,0,'',12,2024),(14,4,0,0,0,'',12,2024),(15,5,0,0,0,'',12,2024),(16,6,0,0,0,'',12,2024),(17,7,0,0,0,'',12,2024),(18,9,0,0,0,'',12,2024),(19,10,0,0,0,'',12,2024),(20,1,0,0,0,'',2,2025),(21,2,0,0,0,'',2,2025),(22,3,0,0,0,'',2,2025),(23,4,0,0,0,'',2,2025),(24,5,0,0,0,'',2,2025),(25,6,0,0,0,'',2,2025),(26,7,0,0,0,'',2,2025),(27,9,0,0,0,'',2,2025),(28,10,0,0,0,'',2,2025),(29,1,0,0,0,'',3,2025),(30,2,0,0,0,'',3,2025),(31,3,0,0,0,'',3,2025),(32,4,0,0,0,'',3,2025),(33,5,0,0,0,'',3,2025),(34,6,0,0,0,'',3,2025),(35,7,0,0,0,'',3,2025),(36,9,0,0,0,'',3,2025),(37,10,0,0,0,'',3,2025),(40,1,0,1,0,'',1,2026),(41,2,0,0,0,'',1,2026),(42,3,0,0,0,'',1,2026),(43,4,0,0,0,'',1,2026),(44,5,0,0,0,'',1,2026),(45,6,0,0,0,'',1,2026),(46,7,0,0,0,'',1,2026),(47,9,0,0,0,'',1,2026),(48,10,0,0,0,'',1,2026);
/*!40000 ALTER TABLE `chamcong` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chitietchamcong`
--

DROP TABLE IF EXISTS `chitietchamcong`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chitietchamcong` (
  `maChiTietChamCong` int NOT NULL AUTO_INCREMENT,
  `ngayChamCong` date DEFAULT NULL,
  `loaiChamCong` varchar(45) DEFAULT NULL,
  `soGioLam` int DEFAULT NULL,
  `maChamCong` int DEFAULT NULL,
  `gioBatDau` timestamp NULL DEFAULT NULL,
  `gioKetThuc` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`maChiTietChamCong`),
  KEY `fk_chamcong_idx` (`maChamCong`),
  CONSTRAINT `fk_chamcong` FOREIGN KEY (`maChamCong`) REFERENCES `chamcong` (`maChamCong`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chitietchamcong`
--

LOCK TABLES `chitietchamcong` WRITE;
/*!40000 ALTER TABLE `chitietchamcong` DISABLE KEYS */;
INSERT INTO `chitietchamcong` VALUES (1,'2024-12-11','Bình thường',0,11,'2024-12-10 17:12:15',NULL),(2,'2026-01-25','Bình thường',0,40,'2026-01-25 12:53:34','2026-01-25 12:53:35');
/*!40000 ALTER TABLE `chitietchamcong` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chitiethoadon`
--

DROP TABLE IF EXISTS `chitiethoadon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chitiethoadon` (
  `maCTHoaDon` int NOT NULL AUTO_INCREMENT,
  `maSanPham` int NOT NULL,
  `soLuong` int NOT NULL,
  `donGia` double NOT NULL,
  `maHoaDon` int NOT NULL,
  PRIMARY KEY (`maCTHoaDon`),
  KEY `fk_CTHoaDon_hoaDon` (`maHoaDon`),
  KEY `maSanPham` (`maSanPham`,`maHoaDon`),
  CONSTRAINT `chitiethoadon_ibfk_1` FOREIGN KEY (`maSanPham`) REFERENCES `sanpham` (`maSanPham`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chitiethoadon_ibfk_2` FOREIGN KEY (`maHoaDon`) REFERENCES `hoadon` (`maHoaDon`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=231 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chitiethoadon`
--

LOCK TABLES `chitiethoadon` WRITE;
/*!40000 ALTER TABLE `chitiethoadon` DISABLE KEYS */;
INSERT INTO `chitiethoadon` VALUES (223,108,1,5000,1),(224,108,2,5000,2),(225,113,1,22000,2),(226,109,1,31000,2),(227,108,1,5000,3),(228,118,1,29000,3),(229,108,4,5000,4),(230,108,5,5000,5);
/*!40000 ALTER TABLE `chitiethoadon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chitietphieunhap`
--

DROP TABLE IF EXISTS `chitietphieunhap`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chitietphieunhap` (
  `maCTPhieuNhap` int NOT NULL AUTO_INCREMENT,
  `soLuong` int NOT NULL,
  `giaNhap` double NOT NULL,
  `maPhieuNhap` int NOT NULL,
  `maSanPham` int NOT NULL,
  `ngayHetHan` date NOT NULL,
  `ishidden` tinyint(1) NOT NULL,
  `ghiChu` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `soLuongTonKho` int NOT NULL,
  `giaBan` double DEFAULT NULL,
  PRIMARY KEY (`maCTPhieuNhap`),
  KEY `maPhieuNhap` (`maPhieuNhap`,`maSanPham`),
  KEY `fk_CTPhieuNhap_sanPham` (`maSanPham`),
  CONSTRAINT `chitietphieunhap_ibfk_1` FOREIGN KEY (`maSanPham`) REFERENCES `sanpham` (`maSanPham`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chitietphieunhap_ibfk_2` FOREIGN KEY (`maPhieuNhap`) REFERENCES `phieunhap` (`maPhieuNhap`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=98 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chitietphieunhap`
--

LOCK TABLES `chitietphieunhap` WRITE;
/*!40000 ALTER TABLE `chitietphieunhap` DISABLE KEYS */;
INSERT INTO `chitietphieunhap` VALUES (76,9,24000,1,109,'2024-12-24',1,'',8,31000),(77,9,4000,2,138,'2024-12-17',1,'',9,5500),(78,15,6000,3,139,'2024-12-09',1,'',15,8000),(79,5,5000,3,140,'2024-12-28',1,'',5,5500),(80,20,4000,4,108,'2027-12-21',0,'',7,5000),(81,1,25000,4,118,'2025-12-23',1,'',0,29000),(82,30,6000,4,125,'2024-12-31',1,'',30,6900),(83,1,15000,4,115,'2024-12-11',1,'',1,15600),(84,1,20000,4,104,'2024-12-09',1,'',1,22000),(85,8,55000,5,158,'2024-12-28',1,'',8,61000),(86,5,9000,6,155,'2024-12-20',1,'',5,10000),(87,10,4000,6,152,'2024-12-13',1,'',10,4500),(88,1,8000,7,131,'2025-12-17',1,'',1,10000),(89,1,6000,7,121,'2024-12-25',1,'',1,7500),(90,15,12000,7,122,'2024-12-25',1,'',15,13300),(91,1,9000,8,156,'2024-12-19',1,'',1,12000),(92,10,6000,9,142,'2024-12-27',1,'',10,7500),(93,1,5000,10,134,'2025-12-23',1,'',1,5500),(94,20,9000,10,121,'2024-12-27',1,'',20,10200),(95,9,20000,10,113,'2024-12-31',1,'',8,22000),(96,1,6000,10,128,'2025-12-24',1,'',1,8000),(97,1,9000,10,124,'2024-12-28',1,'',1,10500);
/*!40000 ALTER TABLE `chitietphieunhap` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chucnang`
--

DROP TABLE IF EXISTS `chucnang`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chucnang` (
  `maChucNang` int NOT NULL AUTO_INCREMENT,
  `tenChucNang` varchar(45) NOT NULL,
  `maDanhMuc` int NOT NULL,
  `isDelete` int DEFAULT NULL,
  PRIMARY KEY (`maChucNang`),
  KEY `fk_group_idx` (`maDanhMuc`),
  CONSTRAINT `fk_group` FOREIGN KEY (`maDanhMuc`) REFERENCES `danhmuc` (`maDanhMuc`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chucnang`
--

LOCK TABLES `chucnang` WRITE;
/*!40000 ALTER TABLE `chucnang` DISABLE KEYS */;
INSERT INTO `chucnang` VALUES (1,'Menu',1,0),(2,'Hóa đơn',1,0),(3,'Sản phẩm',2,0),(4,'Nhà cung cấp',2,0),(5,'Phiếu nhập',2,0),(6,'Thông tin nhân viên',3,0),(7,'Tài khoản',3,0),(8,'Chức vụ',3,0),(9,'Chấm công',3,0),(10,'Lương',3,0),(11,'Hợp đồng lao động',3,0),(12,'Thông tin khách hàng',4,0),(13,'Ưu đãi & khuyến mãi',4,0),(15,'Tổng quan',5,0),(16,'Thống kê sản phẩm',5,0),(20,'Phân loại',6,0),(21,'Chấm công hàng ngày',6,0);
/*!40000 ALTER TABLE `chucnang` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chucvu`
--

DROP TABLE IF EXISTS `chucvu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chucvu` (
  `maChucVu` int NOT NULL AUTO_INCREMENT,
  `tenChucVu` varchar(45) NOT NULL,
  `isDelete` int DEFAULT NULL,
  PRIMARY KEY (`maChucVu`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chucvu`
--

LOCK TABLES `chucvu` WRITE;
/*!40000 ALTER TABLE `chucvu` DISABLE KEYS */;
INSERT INTO `chucvu` VALUES (1,'admin',0),(2,'thủ kho',0),(3,'quầy',0),(4,'quản lí',0),(5,'kế toán',0);
/*!40000 ALTER TABLE `chucvu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `danhmuc`
--

DROP TABLE IF EXISTS `danhmuc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `danhmuc` (
  `maDanhMuc` int NOT NULL AUTO_INCREMENT,
  `tenDanhMuc` varchar(45) NOT NULL,
  `icon` varchar(255) NOT NULL,
  `isDelete` int DEFAULT NULL,
  PRIMARY KEY (`maDanhMuc`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `danhmuc`
--

LOCK TABLES `danhmuc` WRITE;
/*!40000 ALTER TABLE `danhmuc` DISABLE KEYS */;
INSERT INTO `danhmuc` VALUES (1,'Order','/source/image/icon/order.png',0),(2,'Kho','/source/image/icon/warehouse.png',0),(3,'Nhân Viên','/source/image/icon/employees.png',0),(4,'Khách hàng','/source/image/icon/customer.png',0),(5,'Thống kê','/source/image/icon/analysis.png',0),(6,'Cài đặt','/source/image/icon/setting.png',0);
/*!40000 ALTER TABLE `danhmuc` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hoadon`
--

DROP TABLE IF EXISTS `hoadon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hoadon` (
  `maHoaDon` int NOT NULL AUTO_INCREMENT,
  `maNhanVien` int NOT NULL,
  `tongTien` double(12,2) NOT NULL,
  `maKhachHang` int DEFAULT NULL,
  `maKhuyenMai` int DEFAULT NULL,
  `ngayMua` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ghiChu` varchar(100) DEFAULT NULL,
  `maTichDiem` int DEFAULT NULL,
  `isDelete` tinyint(1) NOT NULL,
  PRIMARY KEY (`maHoaDon`),
  KEY `maNhanVien` (`maNhanVien`,`maKhachHang`,`maKhuyenMai`),
  KEY `fk_hoaDon_khachHang` (`maKhachHang`),
  KEY `fk_haoDon_KhuyenMai` (`maKhuyenMai`),
  KEY `hoadon_tichdiem_idx` (`maTichDiem`),
  CONSTRAINT `hoadon_ibfk_1` FOREIGN KEY (`maKhachHang`) REFERENCES `khachhang` (`maKhachHang`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `hoadon_ibfk_2` FOREIGN KEY (`maKhuyenMai`) REFERENCES `khuyenmai` (`maKhuyenMai`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `hoadon_ibfk_3` FOREIGN KEY (`maNhanVien`) REFERENCES `nhanvien` (`maNhanVien`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `hoadon_tichdiem` FOREIGN KEY (`maTichDiem`) REFERENCES `tichdiem` (`matichdiem`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hoadon`
--

LOCK TABLES `hoadon` WRITE;
/*!40000 ALTER TABLE `hoadon` DISABLE KEYS */;
INSERT INTO `hoadon` VALUES (1,1,5000.00,NULL,NULL,'2024-12-10 23:53:28','',NULL,0),(2,1,63000.00,NULL,NULL,'2024-12-10 23:54:44','',NULL,0),(3,1,34000.00,NULL,NULL,'2025-01-07 20:41:46','',NULL,0),(4,9,20000.00,NULL,NULL,'2026-01-13 13:56:39','',NULL,0),(5,1,25000.00,NULL,NULL,'2026-01-25 19:25:47','',NULL,0);
/*!40000 ALTER TABLE `hoadon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hopdonglaodong`
--

DROP TABLE IF EXISTS `hopdonglaodong`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hopdonglaodong` (
  `mahopdong` int NOT NULL,
  `tungay` date NOT NULL,
  `denngay` date NOT NULL,
  `luongcoban` float NOT NULL,
  `maNhanVien` int DEFAULT NULL,
  `isDelete` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`mahopdong`),
  KEY `FK_HopDong_MaNhanVien_idx` (`maNhanVien`),
  CONSTRAINT `FK_HopDong_MaNhanVien` FOREIGN KEY (`maNhanVien`) REFERENCES `nhanvien` (`maNhanVien`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hopdonglaodong`
--

LOCK TABLES `hopdonglaodong` WRITE;
/*!40000 ALTER TABLE `hopdonglaodong` DISABLE KEYS */;
INSERT INTO `hopdonglaodong` VALUES (1,'2024-01-10','2024-01-30',10000,1,0),(2,'2024-01-09','2024-01-30',20000,2,0),(3,'2024-01-08','2024-01-30',30000,3,0),(4,'2024-01-07','2024-01-30',40000,4,0),(5,'2024-01-06','2024-01-30',50000,5,0),(6,'2024-01-05','2024-01-30',60000,6,0),(7,'2024-01-04','2024-01-30',70000,7,0),(8,'2024-01-03','2024-01-30',80000,8,0),(9,'2024-01-02','2024-01-30',90000,9,0),(10,'2024-01-01','2024-01-30',100000,10,0);
/*!40000 ALTER TABLE `hopdonglaodong` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `khachhang`
--

DROP TABLE IF EXISTS `khachhang`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `khachhang` (
  `maKhachHang` int NOT NULL AUTO_INCREMENT,
  `SDT` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `tenKhachHang` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `diemTichLuy` int NOT NULL,
  `maUuDai` int NOT NULL,
  PRIMARY KEY (`maKhachHang`),
  KEY `fk_khachhang_uudai_idx` (`maUuDai`),
  CONSTRAINT `fk_khachhang_uudai` FOREIGN KEY (`maUuDai`) REFERENCES `uudai` (`maUuDai`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `khachhang`
--

LOCK TABLES `khachhang` WRITE;
/*!40000 ALTER TABLE `khachhang` DISABLE KEYS */;
INSERT INTO `khachhang` VALUES (1,'0908898765','Nguyễn Minh Thắng',0,1),(2,'0976898735','Lê Trung',0,1),(3,'0955444333','Hoàng Minh Tiến',0,1),(4,'0945663712','Lê Hoàng An',0,1),(5,'0917661222','Hà Xuân Anh',0,1),(6,'0963714255','Lê Anh Nam',0,1),(7,'0917685441','Lê Hoàng Minh',0,1),(8,'0944111517','Nguyễn Ngọc Tuấn',0,1),(13,'0843109787','Tanle1298',0,1);
/*!40000 ALTER TABLE `khachhang` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `khuyenmai`
--

DROP TABLE IF EXISTS `khuyenmai`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `khuyenmai` (
  `maKhuyenMai` int NOT NULL AUTO_INCREMENT,
  `tenKhuyenMai` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `ngayBatDau` datetime NOT NULL,
  `ngayHetHan` datetime NOT NULL,
  `soLuong` int NOT NULL,
  `phanTram` double(10,2) NOT NULL,
  `ishidden` tinyint(1) NOT NULL,
  `soLuongDaDung` int NOT NULL,
  PRIMARY KEY (`maKhuyenMai`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `khuyenmai`
--

LOCK TABLES `khuyenmai` WRITE;
/*!40000 ALTER TABLE `khuyenmai` DISABLE KEYS */;
INSERT INTO `khuyenmai` VALUES (11,'Ngày 13/12','2023-12-13 00:00:00','2023-12-14 00:00:00',50,8.00,0,1),(12,'Giáng sinh 2023','2023-12-25 00:00:00','2023-12-27 00:00:00',100,12.00,0,0),(13,'Năm mới 2024','2023-12-31 00:00:00','2024-01-04 00:00:00',200,15.00,0,0),(14,'Ngày nhà giáo Việt Nam 2023','2023-11-20 00:00:00','2023-11-21 00:00:00',200,8.00,0,0),(15,'Black Friday','2024-09-20 00:00:00','2024-09-21 00:00:00',300,20.00,0,0),(16,'7/7','2025-07-07 00:00:00','2025-07-08 00:00:00',400,21.00,0,0),(17,'8/8','2025-08-08 00:00:00','2025-08-09 00:00:00',500,22.00,0,0),(18,'9/9','2025-09-09 00:00:00','2025-09-10 00:00:00',600,23.00,0,0),(19,'10/10','2025-10-10 00:00:00','2025-10-11 00:00:00',700,24.00,0,0),(20,'11/11','2025-11-11 00:00:00','2025-11-12 00:00:00',800,25.00,0,0);
/*!40000 ALTER TABLE `khuyenmai` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `luong`
--

DROP TABLE IF EXISTS `luong`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `luong` (
  `maLuong` int NOT NULL AUTO_INCREMENT,
  `maChamCong` int NOT NULL,
  `phuCap` float DEFAULT NULL,
  `luongThucTe` float DEFAULT NULL,
  `luongThuong` float DEFAULT NULL,
  `KhoanBaoHiem` float DEFAULT NULL,
  `khoanTru` float DEFAULT NULL,
  `khoanThue` float DEFAULT NULL,
  `thucLanh` float DEFAULT NULL,
  `luongLamThem` float DEFAULT NULL,
  `ngayNhanLuong` date DEFAULT NULL,
  `maNhanVien` int NOT NULL,
  PRIMARY KEY (`maLuong`),
  KEY `fk_chamcong_idx` (`maChamCong`),
  KEY `fk_luong_nhanvien_idx` (`maNhanVien`),
  CONSTRAINT `fk_chamcong_luong` FOREIGN KEY (`maChamCong`) REFERENCES `chamcong` (`maChamCong`),
  CONSTRAINT `fk_luong_nhanvien` FOREIGN KEY (`maNhanVien`) REFERENCES `nhanvien` (`maNhanVien`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `luong`
--

LOCK TABLES `luong` WRITE;
/*!40000 ALTER TABLE `luong` DISABLE KEYS */;
INSERT INTO `luong` VALUES (11,11,0,0,0,0,NULL,0,0,0,'2024-12-10',1),(12,12,0,0,0,0,NULL,0,0,0,'2024-12-10',2),(13,13,0,0,0,0,NULL,0,0,0,'2024-12-10',3),(14,14,0,0,0,0,NULL,0,0,0,'2024-12-10',4),(15,15,0,0,0,0,NULL,0,0,0,'2024-12-10',5),(16,16,0,0,0,0,NULL,0,0,0,'2024-12-10',6),(17,17,0,0,0,0,NULL,0,0,0,'2024-12-10',7),(18,18,0,0,0,0,NULL,0,0,0,'2024-12-10',9),(19,19,0,0,0,0,NULL,0,0,0,'2024-12-10',10),(20,40,0,0,0,0,NULL,0,0,0,'2026-01-25',1),(21,41,0,0,0,0,NULL,0,0,0,'2026-01-25',2),(22,42,0,0,0,0,NULL,0,0,0,'2026-01-25',3),(23,43,0,0,0,0,NULL,0,0,0,'2026-01-25',4),(24,44,0,0,0,0,NULL,0,0,0,'2026-01-25',5),(25,45,0,0,0,0,NULL,0,0,0,'2026-01-25',6),(26,46,0,0,0,0,NULL,0,0,0,'2026-01-25',7),(27,47,0,0,0,0,NULL,0,0,0,'2026-01-25',9),(28,48,0,0,0,0,NULL,0,0,0,'2026-01-25',10);
/*!40000 ALTER TABLE `luong` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nhacungcap`
--

DROP TABLE IF EXISTS `nhacungcap`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nhacungcap` (
  `maNhaCungCap` int NOT NULL AUTO_INCREMENT,
  `tenNhaCungCap` varchar(255) NOT NULL,
  `SDT` varchar(12) NOT NULL,
  `email` varchar(255) NOT NULL,
  `diaChi` varchar(255) NOT NULL,
  `ngayTao` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` int DEFAULT NULL,
  PRIMARY KEY (`maNhaCungCap`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nhacungcap`
--

LOCK TABLES `nhacungcap` WRITE;
/*!40000 ALTER TABLE `nhacungcap` DISABLE KEYS */;
INSERT INTO `nhacungcap` VALUES (1,'Bánh Tráng 365','0903123456','banhtrang365@gmail.com','123 Đường Lý Thường Kiệt, Quận Tân Bình, TP.HCM','2024-12-02 22:03:47',0),(2,'Ngon Ngon Bakery','0914789123','ngonngonbakery@gmail.com','45A Đường Nguyễn Trãi, Quận 5, TP.HCM','2024-12-02 22:03:47',0),(3,'Hokkaido Delight','0946523611','hokkaidodelight@gmail.com','22 Đường Trần Phú, Quận 1, TP.HCM','2024-12-02 22:03:47',0),(4,'Mì hảo hảo','0932456789','haohaoi@gmail.com','88 Đường Nguyễn Văn Cừ, Quận 10, TP.HCM','2024-12-02 22:05:48',0),(5,'Omusumi House','0943888999','omusumihouse@gmail.com','100 Đường Lê Lợi, Quận 3, TP.HCM','2024-12-02 22:03:47',0),(7,'Oishi','0912111222','Oishi@gmail.com','50 Đường Nguyễn Văn Trỗi, Quận Phú Nhuận, TP.HCM','2024-12-02 22:08:01',0),(8,'Bánh Ngọt Đài Loan','0902222333','banhngotdailoan@gmail.com','67 Đường Hàm Nghi, Quận 1, TP.HCM','2024-12-02 22:03:47',0),(9,'Karo Bakery','0974555666','karobakery@gmail.com','12 Đường Nguyễn Hữu Thọ, Quận 7, TP.HCM','2024-12-02 22:03:47',0),(10,'Sữa và Đồ Uống IDP','0988333444','idp.drinks@gmail.com','23 Đường Phạm Hùng, Quận Bình Chánh, TP.HCM\n','2024-12-02 22:04:13',0),(11,'Cocacola Việt Nam','1800123456','cocacola.vn@gmail.com','01 Đường Pasteur, Quận 1, TP.HCM','2024-12-02 22:04:13',0),(12,'Fuze tea Việt Nam','0945777888','fuzeteavn@gmail.com','78 Đường Võ Thị Sáu, Quận 3, TP.HCM','2024-12-02 22:04:13',0),(13,'Dasani Việt Nam','0915757688','dasanivn@gmail.com','34 Đường Hàm Nghi, Quận 1, TP.HCM','2024-12-02 22:04:13',0),(14,'Aquafina Việt Nam','0902822313','aquafinavn@gmail.com','92 Đường Nguyễn Văn Cừ, Quận 10, TP.HCM','2024-12-02 22:04:13',0),(15,'Cafe Corner','0936999666','cafecorner@gmail.com','34 Đường Trương Định, Quận 1, TP.HCM','2024-12-02 22:04:13',0),(16,'Bánh Bao 888','0988112668','banhbao888@gmail.com','46 Đường Phạm Hùng, Quận Bình Chánh, TP.HCM','2024-12-02 22:04:13',0),(17,'Snack Time','0943888969','snacktime.vn@gmail.com','82 Đường Lý Thường Kiệt, Quận Tân Bình, TP.HCM','2024-12-02 22:04:13',0),(18,'Bánh Xinh','0902112373','bfcake@gmail.com','68 Đường Lê Lợi, Quận 3, TP.HCM','2024-12-02 22:04:13',0),(19,'Sandwich House','0912111332','sandhouse@gmail.com','34 Đường Nguyễn Hữu Thọ, Quận 7, TP.HCM','2024-12-02 22:04:13',0),(20,'Oreo','0945777883','oreovn@gmail.com','04 Đường Tạ Quang Bửu, Quận 8, TP.HCM','2024-12-02 22:10:20',0),(21,'Ostar','0936998666','ostar@gmail.com','57 Đường Phạm Hùng, Quận Bình Chánh, TP.HCM','2024-12-02 22:04:13',0);
/*!40000 ALTER TABLE `nhacungcap` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nhanvien`
--

DROP TABLE IF EXISTS `nhanvien`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nhanvien` (
  `maNhanVien` int NOT NULL AUTO_INCREMENT,
  `tenNhanVien` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `maChucVu` int NOT NULL,
  `gioiTinh` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `ngaySinh` date NOT NULL,
  `diaChi` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `soDienThoai` varchar(12) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `isDelete` tinyint(1) NOT NULL,
  `ngayTao` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `img` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  PRIMARY KEY (`maNhanVien`),
  UNIQUE KEY `maNhanVien_UNIQUE` (`maNhanVien`),
  KEY `fk_chucvu_nhanvien_idx` (`maChucVu`),
  CONSTRAINT `fk_chucvu_nhanvien` FOREIGN KEY (`maChucVu`) REFERENCES `chucvu` (`maChucVu`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nhanvien`
--

LOCK TABLES `nhanvien` WRITE;
/*!40000 ALTER TABLE `nhanvien` DISABLE KEYS */;
INSERT INTO `nhanvien` VALUES (1,'Lê Xuân Anh',1,'1','1994-12-06','Quận 7, TP Hồ Chí Minh','duytan100104@gmail.com','0993566471',0,'2023-11-10 08:33:25','nv1.png'),(2,'Hoàng Minh Huy',2,'1','1996-11-05','Quận 8, TP Hồ Chí Minh','mhuy96@gmail.com','0914523617',0,'2023-11-10 08:35:19','nv2.png'),(3,'Nguyễn Tấn Hiệu',3,'1','2003-11-04','Quận 3, TP Hồ Chí Minh','tuilahiju7@gmail.com','0913517899',0,'2023-11-10 08:37:03','nv3.png'),(4,'Lê Văn Tâm',3,'1','2002-03-05','Quận 1, TP Hồ Chí Minh','tamle22@gmail.com','0935663719',0,'2023-11-10 08:38:37','nv4.png'),(5,'Bùi Lê Bích Nhung',5,'0','2003-06-08','Quận Tân Bình, TP Hồ Chí Minh','bnhun03@gmail.com','0925663781',0,'2023-11-10 08:40:05','nv5.png'),(6,'Nguyễn Ngọc Minh An',4,'0','2000-03-08','Quận Bình Tân, TP Hồ Chí Minh','man2k@gmail.com','0912788372',0,'2023-11-10 08:42:35','nv6.png'),(7,'Nguyễn Thị Xinh',3,'0','2001-03-05','Quận 6, TP Hồ Chí Minh','txinh011@gmail.com','0995342671',0,'2023-11-10 08:47:31','nv7.png'),(8,'Lê Hoàng Minh',2,'1','2002-06-10','Quận 6, TP Hồ Chí Minh','hmin02@gmail.com','0935667112',1,'2023-11-10 08:55:22','nv8.png'),(9,'Nguyễn Ngọc Hoàng',3,'1','1992-03-12','Quận 3, TP Hồ Chí Minh','nhoang92@gmail.com','0356417284',0,'2023-11-10 08:56:45','nv9.png'),(10,'Lê Hoàng Thắng',3,'1','1994-12-02','Quận 3, TP Hồ Chí Minh','hthang7@gmail.com','0944225671',0,'2023-12-02 06:38:20','nv10.png');
/*!40000 ALTER TABLE `nhanvien` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nhomchucnang`
--

DROP TABLE IF EXISTS `nhomchucnang`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nhomchucnang` (
  `manhomchucnang` int NOT NULL AUTO_INCREMENT,
  `tennhomchucnang` varchar(45) NOT NULL,
  `icon` varchar(255) NOT NULL,
  `isDelete` tinyint(1) NOT NULL,
  PRIMARY KEY (`manhomchucnang`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nhomchucnang`
--

LOCK TABLES `nhomchucnang` WRITE;
/*!40000 ALTER TABLE `nhomchucnang` DISABLE KEYS */;
INSERT INTO `nhomchucnang` VALUES (1,'Order','./src/SOURCE/icon/shopping_cart.png',0),(2,'Kho','./src/SOURCE/icon/delivery.png',0),(3,'Nhân Viên','./src/SOURCE/icon/staff.png',0),(4,'Khách hàng','./src/SOURCE/icon/customer.png',0),(5,'Thống kê','./src/SOURCE/icon/trend.png',0),(6,'Cài đặt','./src/SOURCE/icon/setting.png',0);
/*!40000 ALTER TABLE `nhomchucnang` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `phanloai`
--

DROP TABLE IF EXISTS `phanloai`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `phanloai` (
  `maPhanLoai` int NOT NULL AUTO_INCREMENT,
  `tenPhanLoai` varchar(255) NOT NULL,
  `isDelete` int DEFAULT NULL,
  PRIMARY KEY (`maPhanLoai`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `phanloai`
--

LOCK TABLES `phanloai` WRITE;
/*!40000 ALTER TABLE `phanloai` DISABLE KEYS */;
INSERT INTO `phanloai` VALUES (1,'Khác',0),(2,'Nước giải khát',0),(3,'Ăn uống',0),(4,'Đồ đóng hộp',0),(5,'Ăn vặt',0),(6,'Đồ dùng học tập',0),(11,'Đồ dùng sinh hoạt',0),(12,'Trái cây',0),(13,'Kem',0);
/*!40000 ALTER TABLE `phanloai` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `phanquyen`
--

DROP TABLE IF EXISTS `phanquyen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `phanquyen` (
  `maPhanQuyen` int NOT NULL AUTO_INCREMENT,
  `maChucVu` int NOT NULL,
  `maChucNang` int NOT NULL,
  PRIMARY KEY (`maPhanQuyen`),
  KEY `fk_chucnang_idx` (`maChucNang`),
  KEY `fk_chucvu_idx` (`maChucVu`),
  CONSTRAINT `fk_chucnang` FOREIGN KEY (`maChucNang`) REFERENCES `chucnang` (`maChucNang`),
  CONSTRAINT `fk_chucvu` FOREIGN KEY (`maChucVu`) REFERENCES `chucvu` (`maChucVu`)
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `phanquyen`
--

LOCK TABLES `phanquyen` WRITE;
/*!40000 ALTER TABLE `phanquyen` DISABLE KEYS */;
INSERT INTO `phanquyen` VALUES (1,1,1),(2,1,2),(3,1,3),(4,1,4),(6,1,5),(8,1,6),(9,1,7),(10,1,8),(14,1,9),(17,1,10),(25,1,11),(27,1,12),(28,1,13),(35,1,15),(36,1,16),(40,1,20),(42,1,21),(44,2,3),(45,2,4),(46,2,5),(47,2,21),(48,3,1),(49,3,2),(50,3,13),(52,3,21),(53,4,2),(54,4,3),(55,4,4),(56,4,5),(57,4,6),(58,4,9),(59,4,10),(60,4,11),(61,4,13),(63,4,15),(64,4,16),(67,4,20),(69,4,21),(75,5,2),(76,5,5),(77,5,9),(78,5,10),(79,5,11),(80,5,21);
/*!40000 ALTER TABLE `phanquyen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `phieunhap`
--

DROP TABLE IF EXISTS `phieunhap`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `phieunhap` (
  `maPhieuNhap` int NOT NULL AUTO_INCREMENT,
  `ngayNhap` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `tongTien` double NOT NULL,
  `maNhaCungCap` int NOT NULL,
  `maNhanVien` int NOT NULL,
  `ghiChu` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `isDelete` tinyint(1) NOT NULL,
  PRIMARY KEY (`maPhieuNhap`),
  KEY `maNhaCungCap` (`maNhaCungCap`,`maNhanVien`),
  KEY `maNhaCungCap_2` (`maNhaCungCap`,`maNhanVien`),
  KEY `maNhanVien` (`maNhanVien`),
  CONSTRAINT `phieunhap_ibfk_1` FOREIGN KEY (`maNhanVien`) REFERENCES `nhanvien` (`maNhanVien`),
  CONSTRAINT `phieunhap_ibfk_2` FOREIGN KEY (`maNhaCungCap`) REFERENCES `nhacungcap` (`maNhaCungCap`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `phieunhap`
--

LOCK TABLES `phieunhap` WRITE;
/*!40000 ALTER TABLE `phieunhap` DISABLE KEYS */;
INSERT INTO `phieunhap` VALUES (1,'2023-12-08 22:12:54',216000,1,1,'',0),(2,'2024-12-08 22:13:26',36000,11,1,'',0),(3,'2024-12-08 22:14:17',115000,12,1,'',0),(4,'2024-12-08 22:15:51',320000,1,1,'',0),(5,'2024-12-08 22:16:25',440000,21,1,'',0),(6,'2024-12-08 22:17:18',85000,7,1,'',0),(7,'2024-12-08 22:18:24',194000,1,1,'',0),(8,'2024-12-08 22:18:51',9000,20,1,'',0),(9,'2024-12-08 22:19:20',60000,17,1,'',0),(10,'2024-12-08 22:21:07',380000,1,1,'',0);
/*!40000 ALTER TABLE `phieunhap` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sanpham`
--

DROP TABLE IF EXISTS `sanpham`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sanpham` (
  `maSanPham` int NOT NULL AUTO_INCREMENT,
  `tenSanPham` varchar(50) NOT NULL,
  `soLuong` int NOT NULL,
  `ngayThem` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `isHidden` tinyint(1) NOT NULL,
  `img` varchar(255) NOT NULL,
  `maPhanLoai` int NOT NULL,
  `maNhaCungCap` int NOT NULL,
  PRIMARY KEY (`maSanPham`),
  KEY `fk_phanloai_idx` (`maPhanLoai`),
  KEY `fk_nhacungcap_sanpham_idx` (`maNhaCungCap`),
  CONSTRAINT `fk_nhacungcap` FOREIGN KEY (`maNhaCungCap`) REFERENCES `nhacungcap` (`maNhaCungCap`),
  CONSTRAINT `fk_phanloai` FOREIGN KEY (`maPhanLoai`) REFERENCES `phanloai` (`maPhanLoai`)
) ENGINE=InnoDB AUTO_INCREMENT=1049 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sanpham`
--

LOCK TABLES `sanpham` WRITE;
/*!40000 ALTER TABLE `sanpham` DISABLE KEYS */;
INSERT INTO `sanpham` VALUES (1,'Bánh tráng bơ',100,'2023-10-08 12:34:37',0,'Abi-Banh-trang-bo-100g-01-1-400x400.jpg',5,1),(60,'a',0,'2026-01-25 13:05:13',0,'denji-hybrid-form-chainsaw-man-hd-wallpaper-uhdpaper.com-13@1@j.jpg',1,1),(103,'Bánh tráng sa tế cay',1000,'2024-10-12 15:50:34',0,'Abi-banh-trang-sa-te-cay-80g-400x400.jpg',5,1),(104,'Cơm cháy siêu vị',1000,'2024-10-12 15:50:34',0,'Abi-Com-chay-sieu-vi-70g-100.jpg',5,1),(105,'Americano nóng',1000,'2024-10-12 15:50:34',0,'Americano-nong-10oz-01-1-400x400.jpg',2,1),(106,'Aquafina 5L',1000,'2024-10-12 15:50:34',0,'Aquafina-5L.jpg',2,1),(107,'Aquafina 500ml',1000,'2024-10-12 15:50:34',0,'Aquafina-500ml-01-1-800x800.jpg',2,1),(108,'Aquafina 1500ml',1000,'2024-10-12 15:50:34',0,'Aquafina-1500ml.jpg',2,1),(109,'Bánh bao nhân thịt heo trứng muối',1000,'2024-10-12 15:50:34',0,'Banh-bao-nhan-thit-heo-trung-muoi-1-400x400.jpg',3,1),(110,'Bánh crepe tiramisu',1000,'2024-10-12 15:50:34',0,'Banh-crepe-tiramisu-SK-400x400.jpg',3,1),(111,'Bánh đậu đỏ',1000,'2024-10-12 15:50:34',0,'Banh-Dau-do-moi-Y-400x400.jpg',3,1),(112,'Bánh Dorayaki nhân kem sữa',1000,'2024-10-12 15:50:34',0,'Banh-Dorayaki-nhan-kem-sua-Hokkaido-400x400.jpg',3,1),(113,'Bánh gạo An An vị tảo biển',1000,'2024-10-12 15:50:34',0,'Banh-gao-AN-vi-tao-bien-111.3g-400x400.jpg',3,1),(114,'Bánh gio nhân gà 2 trứng cút',1000,'2024-10-12 15:50:34',0,'Banh-gio-nhan-ga-2-trung-cut-2.jpg',3,1),(115,'Bánh giò nhân thịt 2 trứng cút',1000,'2024-10-12 15:50:34',0,'Banh-gio-nhan-thit-2-trung-cut-1-400x400.jpg',3,1),(116,'Bánh Hawaii khoai tây',1000,'2024-10-12 15:50:34',0,'Banh-Hawaii-Khoai-Tay-12-O-Y-400x400.jpg',3,1),(117,'Bánh Karo phô mai hoàng kim',1000,'2024-10-12 15:50:34',0,'Banh-Karo-pho-mai-hoang-kim-26g-x-6-goi-300x300.jpg',3,1),(118,'Bánh Karo trứng tươi chà bông',1000,'2024-10-12 15:50:34',0,'Banh-Karo-trung-tuoi-cha-bong-26g-x-6-goi-402x400.jpg',3,1),(119,'Bánh mì lát mini',1000,'2024-10-12 15:50:34',0,'Banh-mi-lat-mini-nho-A-2-400x400.jpg',3,1),(120,'Bánh mì Việt Nam chả cá',1000,'2024-10-12 15:50:34',0,'Banh-mi-Viet-Nam-cha-ca-soi-01-2-400x400.jpg',3,1),(121,'Bánh ngọt nhân trứng muối Đài Loan',1000,'2024-10-12 15:50:34',0,'Banh-ngot-nhan-trung-muoi-Dai-Loan-180g-401x400.jpg',3,1),(122,'Bánh pillows nhân dừa',1000,'2024-10-12 15:50:34',0,'Banh-pillows-nhan-dua-85g-01-1-400x400.jpg',3,1),(123,'Bánh pillows nhân socola',1000,'2024-10-12 15:50:34',0,'Banh-Pillows-nhan-socola-85g-01-1-400x400.jpg',3,1),(124,'Bánh que Dorkbua thịt xong khối',1000,'2024-10-12 15:50:34',0,'Banh-que-Dorkbua-vi-thit-xong-khoi-50g-300x300.jpg',3,1),(125,'Bánh que Dorkbua vị truyền thống',1000,'2024-10-12 15:50:34',0,'Banh-que-Dorkbua-vi-truyen-thong-50g-300x300.jpg',3,1),(126,'Bánh que Poca nhân kem ổi',0,'2024-10-12 15:50:34',0,'Banh-que-Poca-nhan-kem-oi-hong-115g-300x300.jpg',3,1),(127,'Bát kool spagheltti xốt bò bầm',0,'2024-10-12 15:50:34',0,'bat-kool-spaghetti-xot-bo-bam-105g-400x400.jpg',3,1),(128,'Bento mực vị cay ngọt',0,'2024-10-12 15:50:34',0,'Bento-snack-muc-vi-cay-ngot-18g-01-1-800x800.jpg',3,1),(129,'Bento mực vị cay thái',0,'2024-10-12 15:50:34',0,'Bento-snack-muc-vi-cay-Thai-18g.jpg',3,1),(130,'Betagen SCU cam',0,'2024-10-12 15:50:34',0,'Betagen-SCU-cam-140ml-400x400.jpg',3,1),(131,'Bia 333',0,'2024-10-12 15:50:34',0,'Bia-333-01-1-400x400.jpg',1,1),(132,'Bia Heineken lon cao',0,'2024-10-12 15:50:34',0,'Bia-Heineken-lon-cao-330ml-01-1-400x400.jpg',1,1),(133,'Cafe đen đá L',0,'2024-10-12 15:50:34',0,'Ca-phe-den-da-L-size-400x400.jpg',1,1),(134,'Cafe đen đá S',0,'2024-10-12 15:50:34',0,'Ca-phe-den-da-S-size-400x400.jpg',1,1),(135,'Cafe sữa đá L',0,'2024-10-12 15:50:34',0,'Ca-phe-sua-da-L-size-400x400.jpg',1,1),(136,'Cafe sữa đá S',0,'2024-10-12 15:50:34',0,'Ca-phe-sua-da-S-size-400x400.jpg',1,1),(137,'Coca cola chai 390ml',0,'2024-10-12 15:50:34',0,'Coca-Cola-chai-390ml.jpg',2,11),(138,'Coca cola giảm đường 1.5L',0,'2024-10-12 15:50:34',0,'Coca-Cola-giam-duong-chai-1.5L-800x800.jpg',2,11),(139,'Fruzetea trà đào và hạt chia 450ml',0,'2024-10-12 15:50:34',0,'Fuzetea-tra-dao-va-hat-chia-450ml-01-1-400x400.jpg',2,12),(140,'Hồng trà C2 vị đào 455ml',0,'2024-10-12 15:50:34',0,'Hong-tra-C2-vi-Dao-455ml-1-401x400.jpg',2,12),(141,'Sữa IDP Malto Socola',0,'2024-10-12 15:50:34',0,'IDP-Malto-Socola-180ml-400x400.jpg',1,10),(142,'Kitkat Socola',0,'2024-10-12 15:50:34',0,'KitKat-socola-2F-17g-400x400.jpg',5,17),(143,'Mì Hảo Hảo tôm chua cay',0,'2024-10-12 15:50:34',0,'Mi-Hao-Hao-tom-chua-cay-01-800x800.jpg',3,4),(144,'Mì Hảo Hảo xào khô tôm chua ngọt',0,'2024-10-12 15:50:34',0,'Mi-Hao-Hao-xao-kho-tom-chua-ngot-75g.jpg',3,4),(145,'Mì ly Handy Hảo Hảo tôm chua cay',0,'2024-10-12 15:50:34',0,'Mi-ly-Handy-Hao-Hao-tom-chua-cay-67g-01-800x800.jpg',1,4),(146,'Temaki Omusubi Bacon phô mai',0,'2024-10-12 15:50:34',0,'Temaki-Omusubi-Bacon-Pho-Mai-Hokkaido-401x400.jpg',3,5),(147,'Temaki Omusubi cá hồi nướng mayo',0,'2024-10-12 15:50:34',0,'TEMAKI-OMUSUBI-CA-HOI-NUONG-MAYO-01-1-400x400.jpg',3,5),(148,'Temaki Omusubu cá hồi xốt teriyaki',0,'2024-10-12 15:50:34',0,'Temaki-Omusubi-ca-hoi-xot-teriyaki-01-01-400x400.jpg',3,5),(149,'Mỳ ly CayKay vị bò',0,'2024-10-12 15:50:34',0,'My-ly-CayKay-vi-bo-66g-01-400x400.jpg',4,4),(150,'Nước gạo buỏi sáng 1.5L',0,'2024-10-12 15:50:34',0,'Nuoc-Gao-Buoi-Sang-1.5L.jpg',2,10),(151,'Nước gạo buổi sáng 500ml',0,'2024-10-12 15:50:34',0,'Nuoc-Gao-Buoi-Sang-500ml-01-1-800x800.jpg',2,10),(152,'Oishi bánh phòng mực',0,'2024-10-12 15:50:34',0,'Oishi-banh-phong-muc-Indo-chips-110g-01-400x400.jpg',3,7),(153,'Oishi Snack bí đỏ',0,'2024-10-12 15:50:34',0,'Oishi-Snack-bi-do-110g-01-800x800.jpg',2,7),(154,'Oishi Snack tôm cay đặc biệt',0,'2024-10-12 15:50:34',0,'Oishi-Snack-tom-cay-dac-biet-68g.jpg',2,7),(155,'Oishi Snack tôm cay nóng',0,'2024-10-12 15:50:34',0,'Oishi-snack-tom-cay-nong-70g.jpg',2,7),(156,'Oreo kem vani',0,'2024-10-12 15:50:34',0,'Oreo-kem-vani-119.6g-01-400x400.jpg',13,20),(157,'Ostar Kimchi',0,'2024-10-12 15:50:34',0,'Ostar-Kimchi-108g-399x400.jpg',5,21),(158,'Ostar phô mai trứng muối',0,'2024-10-12 15:50:34',0,'Ostar-pho-mai-trung-muoi-56g-01-400x400.jpg',5,21),(159,'Sandwich cá ngừ Trung Mayonnaise',0,'2024-10-12 15:50:34',0,'SANDWICH-CA-NGU-TRUNG-MAYONNAISE-400x400.jpg',3,19),(160,'Sandwich dằm bông  phô mai',0,'2024-10-12 15:50:34',0,'SANDWICH-DAM-BONG-PHO-MAI-HOKKAIDO-01-1-400x400.jpg',3,19);
/*!40000 ALTER TABLE `sanpham` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `taikhoan`
--

DROP TABLE IF EXISTS `taikhoan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `taikhoan` (
  `tenDangNhap` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `matKhau` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `ngayTao` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `isBlock` tinyint(1) NOT NULL,
  `maNhanVien` int NOT NULL,
  PRIMARY KEY (`tenDangNhap`),
  KEY `maNhanVien` (`maNhanVien`),
  CONSTRAINT `fk_taiKhoan_nhanVien` FOREIGN KEY (`maNhanVien`) REFERENCES `nhanvien` (`maNhanVien`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `taikhoan`
--

LOCK TABLES `taikhoan` WRITE;
/*!40000 ALTER TABLE `taikhoan` DISABLE KEYS */;
INSERT INTO `taikhoan` VALUES ('bnhun03','tuilathungannhung','2023-11-12 06:42:36',1,5),('hiju7','ToilaHiju7','2023-12-04 02:03:51',0,3),('mhuy345','tkhuy','2023-11-12 06:39:22',0,2),('minan2k','tuilaquanliminan','2023-11-11 09:25:22',0,6),('nhoang92','tuilanvquayhoang','2023-11-12 10:39:26',0,9),('vtam','tuilanvquaytam','2023-11-11 06:37:19',0,4),('xunanh37','adman123','2023-11-10 08:57:45',0,1);
/*!40000 ALTER TABLE `taikhoan` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tichdiem`
--

DROP TABLE IF EXISTS `tichdiem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tichdiem` (
  `matichdiem` int NOT NULL AUTO_INCREMENT,
  `Tien` double NOT NULL,
  `diemTichLuy` int NOT NULL,
  `isDelete` tinyint(1) NOT NULL,
  PRIMARY KEY (`matichdiem`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tichdiem`
--

LOCK TABLES `tichdiem` WRITE;
/*!40000 ALTER TABLE `tichdiem` DISABLE KEYS */;
INSERT INTO `tichdiem` VALUES (1,0,1,0),(2,350000,5,0),(3,500000,7,0),(4,750000,10,0),(11,1500000,15,0),(12,950000,11,1),(13,150000,3,0),(14,1700000,17,0),(16,600000,8,0),(18,1900000,19,0);
/*!40000 ALTER TABLE `tichdiem` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `uudai`
--

DROP TABLE IF EXISTS `uudai`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `uudai` (
  `maUuDai` int NOT NULL AUTO_INCREMENT,
  `mocUuDai` int NOT NULL,
  `tiLeGiam` int NOT NULL,
  `isDelete` tinyint(1) NOT NULL,
  PRIMARY KEY (`maUuDai`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `uudai`
--

LOCK TABLES `uudai` WRITE;
/*!40000 ALTER TABLE `uudai` DISABLE KEYS */;
INSERT INTO `uudai` VALUES (1,0,3,0),(2,1000,5,0),(3,2000,8,0),(4,4000,10,0),(5,8000,15,0),(6,6500,12,1),(7,10000,20,1),(8,20000,30,1),(9,30000,40,1),(10,40000,50,1);
/*!40000 ALTER TABLE `uudai` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-25 20:25:42
