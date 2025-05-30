drop database if exists DBTEST;
create database if not exists DBTEST;
USE DBTEST;

-- user1 계정을 암호 user1 으로 만들고 모든 권한을 부여한다.
DROP USER IF EXISTS 'user1'@'localhost';
create user user1@localhost identified by 'user1';

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
('CC003', '카라반마을', '대전 중구', '010-3333-3333', '이영희', 'lee@example.com'),
('CC004', '오토캠프', '인천 연수구', '010-4444-4444', '박찬호', 'park@example.com'),
('CC005', '캠프존', '광주 북구', '010-5555-5555', '최민수', 'choi@example.com'),
('CC006', '카모텔', '대구 달서구', '010-6666-6666', '장서연', 'jang@example.com'),
('CC007', '탑캠핑카', '울산 남구', '010-7777-7777', '윤동주', 'yoon@example.com'),
('CC008', '고릴라캠핑', '경기도 수원시', '010-8888-8888', '하정우', 'ha@example.com'),
('CC009', '로드트립', '강원도 춘천시', '010-9999-9999', '이재훈', 'leejh@example.com'),
('CC010', '자연속으로', '충북 청주시', '010-0000-0000', '신지우', 'shin@example.com'),
('CC011', '캠핑천국', '전남 여수시', '010-1212-1212', '공유', 'gong@example.com'),
('CC012', '하이캠핑', '제주 제주시', '010-2323-2323', '강한나', 'kang@example.com');


-- 캠핑카
-- 캠핑카등록ID,캠핑카이름,차량번호,승차인원수,캠핑카이미지,캠핑카상세정보,캠핑카대여비용,대여한회사ID,등록일
INSERT INTO `캠핑카` VALUES
('RV001', '럭셔리 캠핑카', '11가1234', 6, 'image1.jpg', '풀옵션 캠핑카', 150000, 'CC001', '2024-05-01'),
('RV002', '패밀리 캠핑카', '22나5678', 4, 'image2.jpg', '가족용 캠핑카', 120000, 'CC002', '2024-05-02'),
('RV003', '솔로 캠핑카', '33다9012', 2, 'image3.jpg', '1인용 소형 캠핑카', 90000, 'CC003', '2024-05-03'),
('RV004', '하이엔드 캠핑카', '44라3456', 8, 'image4.jpg', '고급 사양 캠핑카', 180000, 'CC004', '2024-05-04'),
('RV005', '오프로드 캠핑카', '55마7890', 5, 'image5.jpg', '오프로드 대응 가능', 160000, 'CC005', '2024-05-05'),
('RV006', '컴팩트 캠핑카', '66바4321', 2, 'image6.jpg', '도심형 소형 캠핑카', 95000, 'CC006', '2024-05-06'),
('RV007', 'VIP 캠핑카', '77사8765', 6, 'image7.jpg', 'VIP 전용 옵션 포함', 200000, 'CC007', '2024-05-07'),
('RV008', '클래식 캠핑카', '88아6543', 4, 'image8.jpg', '복고풍 감성 캠핑카', 130000, 'CC008', '2024-05-08'),
('RV009', '익스플로러 캠핑카', '99자3210', 7, 'image9.jpg', '탐험가 스타일 대형', 175000, 'CC009', '2024-05-09'),
('RV010', '미니멀 캠핑카', '10차1098', 2, 'image10.jpg', '미니멀 디자인', 85000, 'CC010', '2024-05-10'),
('RV011', '펫 전용 캠핑카', '12카8765', 4, 'image11.jpg', '반려동물 동반 최적화', 140000, 'CC011', '2024-05-11'),
('RV012', '올인원 캠핑카', '13타6543', 6, 'image12.jpg', '주방, 샤워실 포함', 155000, 'CC012', '2024-05-12');

-- 부품재고
-- 부품등록ID,부품명,부품단가,재고수량,입고날짜,공급회사이
INSERT INTO `부품재고` VALUES
('P001', '타이어', 80000, 20, '2024-04-01', '한국타이어'),
('P002', '배터리', 120000, 10, '2024-04-02', 'LG배터리'),
('P003', '브레이크패드', 45000, 30, '2024-04-03', '현대모비스'),
('P004', '에어필터', 20000, 50, '2024-04-04', '보쉬코리아'),
('P005', '오일필터', 18000, 40, '2024-04-05', '한화정공'),
('P006', '스파크플러그', 30000, 25, '2024-04-06', '덴소'),
('P007', '연료펌프', 85000, 15, '2024-04-07', '현대모비스'),
('P008', '와이퍼블레이드', 15000, 35, '2024-04-08', '불스원'),
('P009', '냉각수', 25000, 40, '2024-04-09', 'GS칼텍스'),
('P010', '전조등', 60000, 18, '2024-04-10', '필립스'),
('P011', '후방카메라', 110000, 8, '2024-04-11', '블랙박스코리아'),
('P012', '히터코어', 130000, 12, '2024-04-12', '대우전자');

-- 직원
-- 직원ID,직원이름,휴대폰번호,집주소,월급여,부양가족수,부서명,담당업무
INSERT INTO `직원` VALUES
('E001', '박민수', '010-5555-6666', '서울 송파구', 3200000, 2, '정비부', '정비'),
('E002', '최지우', '010-6666-7777', '부산 수영구', 3000000, 1, '관리부', '관리'),
('E003', '김도현', '010-7777-8888', '인천 연수구', 3100000, 3, '사무부', '사무'),
('E004', '이서연', '010-8888-9999', '대구 달서구', 2950000, 0, '정비부', '정비'),
('E005', '정우성', '010-9999-0000', '광주 북구', 3300000, 1, '관리부', '관리'),
('E006', '한지민', '010-1111-2222', '대전 서구', 2800000, 2, '사무부', '사무'),
('E007', '서지수', '010-2222-3333', '울산 남구', 2700000, 0, '정비부', '정비'),
('E008', '장동건', '010-3333-4444', '전주 완산구', 3500000, 4, '관리부', '관리'),
('E009', '김연아', '010-4444-5555', '제주 제주시', 3000000, 2, '사무부', '사무'),
('E010', '이준호', '010-5555-7777', '서울 강동구', 2900000, 1, '정비부', '정비'),
('E011', '윤아름', '010-6666-8888', '경기 성남시', 3100000, 3, '사무부', '사무'),
('E012', '박보검', '010-7777-9999', '부산 해운대구', 3400000, 2, '관리부', '관리');

-- 고객
-- 고객ID,비밀번호,운전면허증번호,고객명,고객주소,고객전화번호,이메일,이전캠핑카사용날짜,이전캠핑카종류
INSERT INTO `고객` VALUES
('U001', 'pass123', 'DL1234567', '서강준', '서울 강북구', '010-7777-8888', 'seo@example.com', '2024-04-10', '럭셔리 캠핑카'),
('U002', 'pass456', 'DL2345678', '한지민', '부산 금정구', '010-8888-9999', 'han@example.com', '2024-04-11', '패밀리 캠핑카'),
('U003', 'pass789', 'DL3456789', '정해인', '인천 남동구', '010-9999-1111', 'jung@example.com', '2024-04-12', '솔로 캠핑카'),
('U004', 'pass321', 'DL4567890', '김세정', '대구 북구', '010-1111-2222', 'kim@example.com', '2024-04-13', '럭셔리 캠핑카'),
('U005', 'pass654', 'DL5678901', '차은우', '광주 서구', '010-2222-3333', 'cha@example.com', '2024-04-14', '패밀리 캠핑카'),
('U006', 'pass987', 'DL6789012', '문채원', '대전 유성구', '010-3333-4444', 'moon@example.com', '2024-04-15', '솔로 캠핑카'),
('U007', 'pass111', 'DL7890123', '이동욱', '울산 중구', '010-4444-5555', 'lee@example.com', '2024-04-16', '럭셔리 캠핑카'),
('U008', 'pass222', 'DL8901234', '김유정', '전주 덕진구', '010-5555-6666', 'yoojung@example.com', '2024-04-17', '패밀리 캠핑카'),
('U009', 'pass333', 'DL9012345', '이승기', '경기 고양시', '010-6666-7777', 'seung@example.com', '2024-04-18', '솔로 캠핑카'),
('U010', 'pass444', 'DL0123456', '신세경', '서울 관악구', '010-7777-8888', 'shin@example.com', '2024-04-19', '럭셔리 캠핑카'),
('U011', 'pass555', 'DL1123456', '남주혁', '부산 수영구', '010-8888-9999', 'nam@example.com', '2024-04-20', '패밀리 캠핑카'),
('U012', 'pass666', 'DL2123456', '박은빈', '대전 동구', '010-9999-0000', 'park@example.com', '2024-04-21', '솔로 캠핑카');

-- 캠핑카대여
-- 대여번호,캠핑카등록ID,운전면허번호,캠핑카대여회사ID,대여시작일,대여기간,청구요금,납입기한,기타청구내역,기타청구요금 
INSERT INTO `캠핑카대여` VALUES
(1, 'RV001', 'DL1234567', 'CC001', '2025-05-01', 3, 450000, '2025-05-04', '침낭 추가', 30000),
(2, 'RV002', 'DL2345678', 'CC002', '2025-05-05', 5, 600000, '2025-05-10', '자전거 대여', 50000),
(3, 'RV003', 'DL3456789', 'CC003', '2025-05-07', 2, 180000, '2025-05-09', '전기포트 추가', 10000),
(4, 'RV004', 'DL4567890', 'CC004', '2025-05-10', 4, 500000, '2025-05-14', '텐트 포함', 40000),
(5, 'RV005', 'DL5678901', 'CC005', '2025-05-11', 3, 390000, '2025-05-14', '그릴 대여', 15000),
(6, 'RV006', 'DL6789012', 'CC006', '2025-05-13', 6, 720000, '2025-05-19', '침구 추가', 25000),
(7, 'RV007', 'DL7890123', 'CC007', '2025-05-14', 1, 80000, '2025-05-15', '세탁 서비스', 5000),
(8, 'RV008', 'DL8901234', 'CC008', '2025-05-15', 2, 200000, '2025-05-17', '포터블 스토브', 12000),
(9, 'RV009', 'DL9012345', 'CC009', '2025-05-16', 3, 330000, '2025-05-19', '멀티탭 대여', 5000),
(10, 'RV010', 'DL0123456', 'CC010', '2025-05-17', 7, 1050000, '2025-05-24', '랜턴 추가', 8000),
(11, 'RV011', 'DL1123456', 'CC011', '2025-05-18', 4, 480000, '2025-05-22', '캠핑의자 대여', 10000),
(12, 'RV012', 'DL2123456', 'CC012', '2025-05-19', 2, 180000, '2025-05-21', '전기히터', 15000);

-- 외부캠핑카 정비소
-- 캠핑카정비소ID,정비소명,정비소주소,정비소전화번호,담당자이름,담당자이메
INSERT INTO `외부캠핑카정비소` VALUES
('RS001', '정비마스터', '서울 은평구', '02-111-2222', '조세호', 'cho@example.com'),
('RS002', '바른정비', '부산 사상구', '051-333-4444', '유재석', 'yu@example.com'),
('RS003', '오토닥터', '대전 서구', '042-222-3333', '김영희', 'kimyh@example.com'),
('RS004', '트러블슈터', '광주 북구', '062-555-6666', '박정우', 'parkjw@example.com'),
('RS005', '캠핑정비소', '울산 남구', '052-777-8888', '이하늘', 'leehn@example.com'),
('RS006', '한빛정비', '수원 권선구', '031-123-4567', '최민호', 'choimh@example.com'),
('RS007', '마이카센터', '전주 덕진구', '063-321-6543', '서지수', 'seojs@example.com'),
('RS008', '모터존', '창원 의창구', '055-876-5432', '정다은', 'jungde@example.com'),
('RS009', '프리미엄정비', '인천 연수구', '032-333-2222', '한상우', 'hansw@example.com'),
('RS010', '신속정비', '세종특별자치시', '044-444-5555', '배유리', 'baeyr@example.com'),
('RS011', '오케이카정비', '경기 성남시', '031-998-8877', '문지호', 'moonjh@example.com'),
('RS012', '카닥터', '제주 제주시', '064-111-2222', '장미정', 'jangmj@example.com');

-- 자체정비기록
-- 자체정비등록ID,캠핑카등록ID,부품등록ID,정비일자,정비시간,정비담당자ID
INSERT INTO `자체정비기록` VALUES
('SR001', 'RV001', 'P001', '2024-04-15', 90, 'E001'),
('SR002', 'RV002', 'P002', '2024-04-20', 120, 'E001'),
('SR003', 'RV003', 'P003', '2024-04-22', 60, 'E004'),
('SR004', 'RV004', 'P004', '2024-04-25', 75, 'E004'),
('SR005', 'RV005', 'P005', '2024-04-28', 80, 'E004'),
('SR006', 'RV006', 'P006', '2024-05-01', 95, 'E007'),
('SR007', 'RV007', 'P007', '2024-05-03', 100, 'E007'),
('SR008', 'RV008', 'P008', '2024-05-05', 110, 'E007'),
('SR009', 'RV009', 'P009', '2024-05-07', 70, 'E010'),
('SR010', 'RV010', 'P010', '2024-05-09', 85, 'E010'),
('SR011', 'RV011', 'P011', '2024-05-11', 90, 'E010'),
('SR012', 'RV012', 'P012', '2024-05-13', 105, 'E010');

-- 외부정비정보
-- 정비번호,캠핑카등록ID,캠핑카대여회사ID,캠핑카정비소ID,운전면허번호,정비내역,수리날짜,수리비용,납입기한,기타정비내
INSERT INTO `외부정비정보` VALUES
(1, 'RV001', 'CC001', 'RS001', 'DL1234567', '타이어 교체', '2024-04-25', 150000, '2024-04-30', '추가 점검 포함'),
(2, 'RV002', 'CC002', 'RS002', 'DL2345678', '배터리 교체', '2024-04-28', 200000, '2024-05-03', '추가 부품교체'),
(3, 'RV003', 'CC003', 'RS003', 'DL3456789', '브레이크 수리', '2024-05-01', 180000, '2024-05-06', '패드 교체 포함'),
(4, 'RV004', 'CC004', 'RS004', 'DL4567890', '전조등 교체', '2024-05-03', 90000, '2024-05-08', '우측 등 포함'),
(5, 'RV005', 'CC005', 'RS005', 'DL5678901', '에어컨 점검', '2024-05-05', 130000, '2024-05-10', '냉매 충전 포함'),
(6, 'RV006', 'CC006', 'RS006', 'DL6789012', '오일 교환', '2024-05-07', 70000, '2024-05-12', '엔진오일 포함'),
(7, 'RV007', 'CC007', 'RS007', 'DL7890123', '냉각수 교체', '2024-05-09', 60000, '2024-05-14', '부동액 포함'),
(8, 'RV008', 'CC008', 'RS008', 'DL8901234', '엔진 점검', '2024-05-11', 250000, '2024-05-16', '정밀 진단 포함'),
(9, 'RV009', 'CC009', 'RS009', 'DL9012345', '타이어 밸런스', '2024-05-13', 95000, '2024-05-18', '앞바퀴 중심'),
(10, 'RV010', 'CC010', 'RS010', 'DL0123456', '차체 정렬', '2024-05-15', 220000, '2024-05-20', '휠 얼라인먼트'),
(11, 'RV011', 'CC011', 'RS011', 'DL1123456', '서스펜션 수리', '2024-05-17', 270000, '2024-05-22', '쇼바 포함'),
(12, 'RV012', 'CC012', 'RS012', 'DL2123456', '기어 점검', '2024-05-19', 160000, '2024-05-24', '미션오일 포함');



GRANT SELECT, INSERT, UPDATE, DELETE ON `DBTEST`.`캠핑카` TO 'user1'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON `DBTEST`.`캠핑카대여` TO 'user1'@'localhost';
GRANT SELECT ON `DBTEST`.`캠핑카대여회사` TO 'user1'@'localhost';
GRANT SELECT ON `DBTEST`.`고객` TO 'user1'@'localhost';
GRANT SELECT, INSERT, UPDATE ON `DBTEST`.`외부정비정보` TO 'user1'@'localhost';
GRANT SELECT ON `DBTEST`.`외부캠핑카정비소` TO 'user1'@'localhost';

