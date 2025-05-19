drop database if exists DBTEST;
create database if not exists DBTEST;
USE DBTEST;

-- user1 계정을 암호 user1 으로 만들고 모든 권한을 부여한다.
DROP USER IF EXISTS 'user1'@'localhost';
create user user1@localhost identified by 'user1';
GRANT SELECT, INSERT, UPDATE, DELETE ON DBTEST.* TO 'user1'@'localhost';

-- user1 권한 부여하는거 다시 설정하자


-- 테이블 삭제
DROP TABLE IF EXISTS `외부정비정보`;
DROP TABLE IF EXISTS `외부캠핑카정비소`;
DROP TABLE IF EXISTS `캠핑카대여`;
DROP TABLE IF EXISTS `고객`;
DROP TABLE IF EXISTS `자체정비기록`;
DROP TABLE IF EXISTS `직원`;
DROP TABLE IF EXISTS `부품재고`;
DROP TABLE IF EXISTS `캠핑카`;
DROP TABLE IF EXISTS `캠핑카대여회사`;

-- 테이블 생성
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema DBTEST
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema DBTEST
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `DBTEST` DEFAULT CHARACTER SET utf8 ;
USE `DBTEST` ;

-- -----------------------------------------------------
-- Table `DBTEST`.`캠핑카대여회사`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DBTEST`.`캠핑카대여회사` (
  `캠핑카대여회사ID` VARCHAR(40) NOT NULL,
  `회사명` VARCHAR(45) NULL,
  `주소` VARCHAR(45) NULL,
  `전화번호` VARCHAR(45) NULL,
  `담당자이름` VARCHAR(10) NULL,
  `담당자이메일` VARCHAR(45) NULL,
  PRIMARY KEY (`캠핑카대여회사ID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `DBTEST`.`캠핑카`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DBTEST`.`캠핑카` (
  `캠핑카등록ID` VARCHAR(40) NOT NULL,
  `캠핑카이름` VARCHAR(45) NULL,
  `차량번호` VARCHAR(45) NULL,
  `승차인원수` INT NULL,
  `캠핑카이미지` VARCHAR(255) NULL,
  `캠핑카상세정보` VARCHAR(100) NULL,
  `캠핑카대여비용` INT NULL,
  `대여한회사ID` VARCHAR(40) NULL,
  `등록일자` DATE NULL,
  PRIMARY KEY (`캠핑카등록ID`),
  INDEX `캠핑카 대여회사 아이디 외래키_idx` (`대여한회사ID` ASC) VISIBLE,
  CONSTRAINT `캠핑카 대여회사 아이디 외래키`
    FOREIGN KEY (`대여한회사ID`)
    REFERENCES `DBTEST`.`캠핑카대여회사` (`캠핑카대여회사ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `DBTEST`.`부품재고`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DBTEST`.`부품재고` (
  `부품등록ID` VARCHAR(40) NOT NULL,
  `부품명` VARCHAR(45) NULL,
  `부품단가` INT NULL,
  `재고수량` INT NULL,
  `입고날짜` DATE NULL,
  `공급회사이름` VARCHAR(45) NULL,
  PRIMARY KEY (`부품등록ID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `DBTEST`.`직원`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DBTEST`.`직원` (
  `직원ID` VARCHAR(40) NOT NULL,
  `직원이름` VARCHAR(10) NULL,
  `휴대폰번호` VARCHAR(20) NULL,
  `집주소` VARCHAR(45) NULL,
  `월급여` INT NULL,
  `부양가족수` INT NULL,
  `부서명` VARCHAR(45) NULL,
  `담당업무` VARCHAR(10) NULL,
  PRIMARY KEY (`직원ID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `DBTEST`.`자체정비기록`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DBTEST`.`자체정비기록` (
  `자체정비등록ID` VARCHAR(40) NOT NULL,
  `캠핑카등록ID` VARCHAR(40) NULL,
  `부품등록ID` VARCHAR(40) NULL,
  `정비일자` DATE NULL,
  `정비시간` INT NULL,
  `정비담당자ID` VARCHAR(40) NULL,
  PRIMARY KEY (`자체정비등록ID`),
  INDEX `캠핑카 등록id 외래키_idx` (`캠핑카등록ID` ASC) VISIBLE,
  INDEX `부품등록 id 외래키_idx` (`부품등록ID` ASC) VISIBLE,
  INDEX `정비담당자 id 외래키_idx` (`정비담당자ID` ASC) VISIBLE,
  CONSTRAINT `캠핑카 등록id 외래키`
    FOREIGN KEY (`캠핑카등록ID`)
    REFERENCES `DBTEST`.`캠핑카` (`캠핑카등록ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `부품등록 id 외래키`
    FOREIGN KEY (`부품등록ID`)
    REFERENCES `DBTEST`.`부품재고` (`부품등록ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `정비담당자 id 외래키`
    FOREIGN KEY (`정비담당자ID`)
    REFERENCES `DBTEST`.`직원` (`직원ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `DBTEST`.`고객`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DBTEST`.`고객` (
  `고객ID` VARCHAR(40) NOT NULL,
  `비밀번호` VARCHAR(30) NULL,
  `운전면허증번호` VARCHAR(40) NULL,
  `고객명` VARCHAR(10) NULL,
  `고객주소` VARCHAR(45) NULL,
  `고객전화번호` VARCHAR(20) NULL,
  `고객 이메일` VARCHAR(45) NULL,
  `이전캠핑카사용날짜` DATE NULL,
  `이전캠핑카종류` VARCHAR(45) NULL,
  PRIMARY KEY (`고객ID`),
  UNIQUE INDEX `운전면허증 번호_UNIQUE` (`운전면허증번호` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `DBTEST`.`캠핑카대여`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DBTEST`.`캠핑카대여` (
  `대여번호` INT NOT NULL,
  `캠핑카등록ID` VARCHAR(40) NULL,
  `운전면허증번호` VARCHAR(40) NULL,
  `캠핑카대여회사ID` VARCHAR(40) NULL,
  `대여시작일` DATE NULL,
  `대여기간` INT NULL,
  `청구요금` INT NULL,
  `납입기한` DATE NULL,
  `기타청구내역` VARCHAR(50) NULL,
  `기타청구요금` INT NULL,
  PRIMARY KEY (`대여번호`),
  INDEX `캠핑카 등록 id 외래키_idx` (`캠핑카등록ID` ASC) VISIBLE,
  INDEX `캠핑카 대여회사 id 외래키_idx` (`캠핑카대여회사ID` ASC) VISIBLE,
  INDEX `운전면허증 번호 외래키_idx` (`운전면허증번호` ASC) VISIBLE,
  CONSTRAINT `캠핑카 등록 id 외래키`
    FOREIGN KEY (`캠핑카등록ID`)
    REFERENCES `DBTEST`.`캠핑카` (`캠핑카등록ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `캠핑카 대여회사 id 외래키`
    FOREIGN KEY (`캠핑카대여회사ID`)
    REFERENCES `DBTEST`.`캠핑카대여회사` (`캠핑카대여회사ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `운전면허증 번호 외래키`
    FOREIGN KEY (`운전면허증번호`)
    REFERENCES `DBTEST`.`고객` (`운전면허증번호`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `DBTEST`.`외부캠핑카정비소`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DBTEST`.`외부캠핑카정비소` (
  `캠핑카정비소ID` VARCHAR(40) NOT NULL,
  `정비소명` VARCHAR(45) NULL,
  `정비소주소` VARCHAR(45) NULL,
  `정비소전화번호` VARCHAR(20) NULL,
  `담당자이름` VARCHAR(10) NULL,
  `담당자이메일` VARCHAR(45) NULL,
  PRIMARY KEY (`캠핑카정비소ID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `DBTEST`.`외부정비정보`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DBTEST`.`외부정비정보` (
  `정비번호` INT NOT NULL,
  `캠핑카등록ID` VARCHAR(40) NULL,
  `캠핑카대여회사ID` VARCHAR(40) NULL,
  `캠핑카정비소ID` VARCHAR(40) NULL,
  `고객운전면허증번호` VARCHAR(40) NULL,
  `정비내역` VARCHAR(45) NULL,
  `수리날짜` DATE NULL,
  `수리비용` INT NULL,
  `납입기한` DATE NULL,
  `기타정비내역` VARCHAR(50) NULL,
  PRIMARY KEY (`정비번호`),
  INDEX `캠핑카등록 id 외래키_idx` (`캠핑카등록ID` ASC) VISIBLE,
  INDEX `캠핑카 대여회사 id 외래키_idx` (`캠핑카대여회사ID` ASC) VISIBLE,
  INDEX `외부캠핑카 정비소 외래키_idx` (`캠핑카정비소ID` ASC) VISIBLE,
  CONSTRAINT `캠핑카등록 id 외래키`
    FOREIGN KEY (`캠핑카등록ID`)
    REFERENCES `DBTEST`.`캠핑카` (`캠핑카등록ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `캠핑카 대여회사 id_외부정비정보 외래키`
    FOREIGN KEY (`캠핑카대여회사ID`)
    REFERENCES `DBTEST`.`캠핑카대여회사` (`캠핑카대여회사ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `고객운전면허증 정보 외래키`
    FOREIGN KEY (`고객운전면허증번호`)
    REFERENCES `DBTEST`.`고객` (`운전면허증번호`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `외부캠핑카 정비소 외래키`
    FOREIGN KEY (`캠핑카정비소ID`)
    REFERENCES `DBTEST`.`외부캠핑카정비소` (`캠핑카정비소ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;




-- 샘플 데이터 삽입

-- 캠핑카 대여회사
-- 캠피캉대여회사ID,회사명,주소,전화번호,담당자이름,담당자이메일
INSERT INTO `캠핑카대여회사` VALUES 
('CC001', '캠핑', '서울 강남구', '010-1111-1111', '홍길동', 'hong@example.com'),
('CC002', 'RV코리아', '부산 해운대구', '010-2222-2222', '김철수', 'kim@example.com'),
('CC003', '카라반마을', '대전 중구', '010-3333-3333', '이영희', 'lee@example.com');  


-- 캠핑카
-- 캠핑카등록ID,캠핑카이름,차량번호,승차인원수,캠핑카이미지,캠핑카상세정보,캠핑카대여비용,대여한회사ID,등록일
INSERT INTO `캠핑카` VALUES
('RV001', '럭셔리 캠핑카', '11가1234', 6, 'image1.jpg', '풀옵션 캠핑카', 150000, 'CC001', '2024-05-01'),
('RV002', '패밀리 캠핑카', '22나5678', 4, 'image2.jpg', '가족용 캠핑카', 120000, 'CC002', '2024-05-02'),
('RV003', '솔로 캠핑카', '33다9012', 2, 'image3.jpg', '1인용 소형 캠핑카', 90000, 'CC003', '2024-05-03');

-- 부품재고
-- 부품등록ID,부품명,부품단가,재고수량,입고날짜,공급회사이
INSERT INTO `부품재고` VALUES
('P001', '타이어', 80000, 20, '2024-04-01', '한국타이어'),
('P002', '배터리', 120000, 10, '2024-04-02', 'LG배터리');

-- 직원
-- 직원ID,직원이름,휴대폰번호,집주소,월급여,부양가족수,부서명,담당업무
INSERT INTO `직원` VALUES
('E001', '박민수', '010-5555-6666', '서울 송파구', 3200000, 2, '정비부', '정비'),
('E002', '최지우', '010-6666-7777', '부산 수영구', 3000000, 1, '관리부', '관리');

-- 고객
-- 고객ID,비밀번호,운전면허증번호,고객명,고객주소,고객전화번호,이메일,이전캠핑카사용날짜,이전캠핑카종류
INSERT INTO `고객` VALUES
('U001', 'pass123', 'DL1234567', '서강준', '서울 강북구', '010-7777-8888', 'seo@example.com', '2024-04-10', '럭셔리 캠핑카'),
('U002', 'pass456', 'DL2345678', '한지민', '부산 금정구', '010-8888-9999', 'han@example.com', '2024-04-11', '패밀리 캠핑카');

-- 캠핑카대여
-- 대여번호,캠핑카등록ID,운전면허번호,캠핑카대여회사ID,대여시작일,대여기간,청구요금,납입기한,기타청구내역,기타청구요금 
INSERT INTO `캠핑카대여` VALUES
(1, 'RV001', 'DL1234567', 'CC001', '2025-05-01', 3, 450000, '2025-05-04', '침낭 추가', 30000),
(2, 'RV002', 'DL2345678', 'CC002', '2025-05-05', 5, 600000, '2025-05-10', '자전거 대여', 50000);

-- 외부캠핑카 정비소
-- 캠핑카정비소ID,정비소명,정비소주소,정비소전화번호,담당자이름,담당자이메
INSERT INTO `외부캠핑카정비소` VALUES
('RS001', '정비마스터', '서울 은평구', '02-111-2222', '조세호', 'cho@example.com'),
('RS002', '바른정비', '부산 사상구', '051-333-4444', '유재석', 'yu@example.com');

-- 자체정비기록
-- 자체정비등록ID,캠핑카등록ID,부품등록ID,정비일자,정비시간,정비담당자ID
INSERT INTO `자체정비기록` VALUES
('SR001', 'RV001', 'P001', '2024-04-15', 90, 'E001'),
('SR002', 'RV002', 'P002', '2024-04-20', 120, 'E001');

-- 외부정비정보
-- 정비번호,캠핑카등록ID,캠핑카대여회사ID,캠핑카정비소ID,운전면허번호,정비내역,수리날짜,수리비용,납입기한,기타정비내
INSERT INTO `외부정비정보` VALUES
(1, 'RV001', 'CC001', 'RS001', 'DL1234567', '타이어 교체', '2024-04-25', 150000, '2024-04-30', '추가 점검 포함'),
(2, 'RV002', 'CC002', 'RS002', 'DL2345678', '배터리 교체', '2024-04-28', 200000, '2024-05-03', '추가 부품교체');