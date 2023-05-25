-- MySQL dump 10.13  Distrib 8.0.33, for Linux (x86_64)
--
-- Host: localhost    Database: DropMate
-- ------------------------------------------------------
-- Server version	8.0.33

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
-- Table structure for table `acp_operational_details`
--

DROP TABLE IF EXISTS `acp_operational_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `acp_operational_details` (
  `acp_id` int NOT NULL,
  `statistic_value` int DEFAULT NULL,
  `statistic_name` varchar(255) NOT NULL,
  PRIMARY KEY (`acp_id`,`statistic_name`),
  CONSTRAINT `FKll8udc1o5yc1fohtoy1gnnsr0` FOREIGN KEY (`acp_id`) REFERENCES `associated_collection_points` (`acp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `acp_operational_details`
--

LOCK TABLES `acp_operational_details` WRITE;
/*!40000 ALTER TABLE `acp_operational_details` DISABLE KEYS */;
INSERT INTO `acp_operational_details` VALUES (1,1,'parcels_in_delivery'),(1,1,'parcels_waiting_pickup'),(1,3,'total_parcels'),(2,5,'parcels_in_delivery'),(2,7,'parcels_waiting_pickup'),(2,15,'total_parcels'),(3,12,'parcels_in_delivery'),(3,0,'parcels_waiting_pickup'),(3,24,'total_parcels'),(4,0,'parcels_in_delivery'),(4,0,'parcels_waiting_pickup'),(4,0,'total_parcels'),(5,13,'parcels_in_delivery'),(5,2,'parcels_waiting_pickup'),(5,123,'total_parcels'),(6,2,'parcels_in_delivery'),(6,2,'parcels_waiting_pickup'),(6,45,'total_parcels'),(7,14,'parcels_in_delivery'),(7,2,'parcels_waiting_pickup'),(7,23,'total_parcels'),(8,3,'parcels_in_delivery'),(8,2,'parcels_waiting_pickup'),(8,9,'total_parcels'),(9,0,'parcels_in_delivery'),(9,0,'parcels_waiting_pickup'),(9,0,'total_parcels');
/*!40000 ALTER TABLE `acp_operational_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `associated_collection_points`
--

DROP TABLE IF EXISTS `associated_collection_points`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `associated_collection_points` (
  `acp_id` int NOT NULL AUTO_INCREMENT,
  `address` varchar(255) NOT NULL,
  `city` varchar(255) NOT NULL,
  `delivery_limit` int NOT NULL DEFAULT '0',
  `email` varchar(255) NOT NULL,
  `manager_contact` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `telephone_number` varchar(255) NOT NULL,
  PRIMARY KEY (`acp_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `associated_collection_points`
--

LOCK TABLES `associated_collection_points` WRITE;
/*!40000 ALTER TABLE `associated_collection_points` DISABLE KEYS */;
INSERT INTO `associated_collection_points` VALUES (1,'Av. Dom Frei Miguel de Bulhões e Sousa, 3810-164','Aveiro',10,'flores@mail.pt','234 195 919','Pracinha das Flores','234 195 919'),(2,'R. do Buragal 216, 3810-382 Aradas','Aveiro',22,'mercadao@mail.pt','234 427 229','Mercadão das Flores','234 427 229'),(3,'Praça do Marquês de Pombal, 3810-164 Aveiro','Aveiro',30,'dart@mail.pt','234 427 229','D\'Art E Flor','234 427 229'),(4,'Av. Cap. Silva Pereira 151, 3500-102 Viseu','Viseu',30,'crava@mail.pt','273 583 894','Crava E Canela','273 583 894'),(5,'Largo da Lapa 38, 4050-069 Porto','Porto',30,'porto@mail.pt','225 508 873','Porto Flores','225 508 873'),(6,'R. Dr. António José de Almeida 327 A, 3000-045 Coimbra','Coimbra',30,'tina@mail.pt','914 423 374','Florista Tina','914 423 374'),(7,'Urbanização Nova Leiria, R. Porto de Mós 19 loja c, 2415-784 Leiria','Leiria',30,'floraria@mail.pt','916 261 025','Floraria','916 261 025'),(8,'Av. António Macedo, Edifício Lyons Club nº242 - Loja 3, 4700-413 Braga','Braga',30,'celeste@mail.pt','253 271 534','Jardim da Celeste','253 271 534'),(9,'3740-255 Sever do Vouga','Sever do Vouga',30,'petals@mail.pt','938 412 334','Petalas da Rosa','938 412 334');
/*!40000 ALTER TABLE `associated_collection_points` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `candidate_associated_collection_points`
--

DROP TABLE IF EXISTS `candidate_associated_collection_points`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `candidate_associated_collection_points` (
  `acp_id` int NOT NULL AUTO_INCREMENT,
  `address` varchar(255) NOT NULL,
  `city` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `status` int NOT NULL DEFAULT '0',
  `telephone_number` varchar(255) NOT NULL,
  PRIMARY KEY (`acp_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `candidate_associated_collection_points`
--

LOCK TABLES `candidate_associated_collection_points` WRITE;
/*!40000 ALTER TABLE `candidate_associated_collection_points` DISABLE KEYS */;
INSERT INTO `candidate_associated_collection_points` VALUES (1,'R. Dr. Celestino Gomes 34A, 3830-187 Ilhavo','Ilhavo','PLS LET ME ENTEEEEEEEEEER','florarte@mail.pt','Florarte',0,'374729373'),(2,'Rua Gago Coutinho-Armazém A4, Nº36, Santa Joana, 3810-269 Aveiro','Aveiro','Quero fazer mais dinheiro','tivaldi@mail.pt','Tivaldiflor',0,'234785335'),(3,'R. das Cardadeiras 2, 3800-125 Aveiro','Aveiro','Somos uma florista muito conceituada e profissional e vamos entregar todas as vossas encomendas com todo o respeito','multiflores@mail.pt','Multiflores',0,'234882993');
/*!40000 ALTER TABLE `candidate_associated_collection_points` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `operators`
--

DROP TABLE IF EXISTS `operators`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `operators` (
  `operatorid` int NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `acp_id` int NOT NULL,
  PRIMARY KEY (`operatorid`),
  KEY `FK4snppth03fm6u68xg0vr804fa` (`acp_id`),
  CONSTRAINT `FK4snppth03fm6u68xg0vr804fa` FOREIGN KEY (`acp_id`) REFERENCES `associated_collection_points` (`acp_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `operators`
--

LOCK TABLES `operators` WRITE;
/*!40000 ALTER TABLE `operators` DISABLE KEYS */;
INSERT INTO `operators` VALUES (1,'azevedo@mail.pt','João Azevedo','password123',1);
/*!40000 ALTER TABLE `operators` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `parcels`
--

DROP TABLE IF EXISTS `parcels`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `parcels` (
  `parcel_id` int NOT NULL AUTO_INCREMENT,
  `delivery_code` varchar(255) NOT NULL,
  `delivery_date` date DEFAULT NULL,
  `parcel_status` int DEFAULT NULL,
  `pickup_code` varchar(255) NOT NULL,
  `pickup_date` date DEFAULT NULL,
  `weight` double NOT NULL,
  `acp_id` int NOT NULL,
  `store_id` int NOT NULL,
  PRIMARY KEY (`parcel_id`),
  UNIQUE KEY `UK_44c0drdqc4q4bbk81vhqmlddy` (`delivery_code`),
  UNIQUE KEY `UK_tiyqfimfv23d7y3yx5ts5mnq8` (`pickup_code`),
  KEY `FK2orccd344k6vsxqpad5gcew4m` (`acp_id`),
  KEY `FKu20j636jg5jcvryspi6ubl1x` (`store_id`),
  CONSTRAINT `FK2orccd344k6vsxqpad5gcew4m` FOREIGN KEY (`acp_id`) REFERENCES `associated_collection_points` (`acp_id`),
  CONSTRAINT `FKu20j636jg5jcvryspi6ubl1x` FOREIGN KEY (`store_id`) REFERENCES `stores` (`store_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `parcels`
--

LOCK TABLES `parcels` WRITE;
/*!40000 ALTER TABLE `parcels` DISABLE KEYS */;
INSERT INTO `parcels` VALUES (1,'DEL_123','2023-06-06',1,'PIC_123',NULL,4.3,1,1),(2,'DEL_456','2023-06-08',3,'PIC_456','2023-06-08',7.2,1,1),(3,'DEL_789','2023-06-07',2,'PIC_789',NULL,2.3,1,1);
/*!40000 ALTER TABLE `parcels` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stores`
--

DROP TABLE IF EXISTS `stores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stores` (
  `store_id` int NOT NULL AUTO_INCREMENT,
  `address` varchar(255) NOT NULL,
  `city` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `field` varchar(255) DEFAULT NULL,
  `manager_contact` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `orders_delivered` int DEFAULT '0',
  `telephone_number` varchar(255) NOT NULL,
  PRIMARY KEY (`store_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stores`
--

LOCK TABLES `stores` WRITE;
/*!40000 ALTER TABLE `stores` DISABLE KEYS */;
INSERT INTO `stores` VALUES (1,'3810-193 Aveiro','Aveiro','fiesta@mail.com','Flower Shop','dl.carvalho@ua.pt','Floral Fiesta',0,'234948209');
/*!40000 ALTER TABLE `stores` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_administrators`
--

DROP TABLE IF EXISTS `system_administrators`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_administrators` (
  `adminid` int NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`adminid`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_administrators`
--

LOCK TABLES `system_administrators` WRITE;
/*!40000 ALTER TABLE `system_administrators` DISABLE KEYS */;
INSERT INTO `system_administrators` VALUES (1,'zezinho@mail.pt','Zé Esteves','password');
/*!40000 ALTER TABLE `system_administrators` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-05-25 17:11:31
