-- DB 생성 및 선택
CREATE DATABASE IF NOT EXISTS capstone;
USE capstone;

-- 1. 회원 테이블
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,           -- 사용자 고유 ID
    uname VARCHAR(50) NOT NULL UNIQUE,              -- 사용자 이름 (아이디)
    email VARCHAR(100) NOT NULL UNIQUE,             -- 이메일
    pw VARCHAR(255) NOT NULL,                       -- 비밀번호 해시
    name VARCHAR(100),                              -- 전체 이름 또는 닉네임
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,    -- 생성일시
    updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  -- 수정일시
);

-- 2. 날씨/옷차림 테이블
CREATE TABLE IF NOT EXISTS outfit (
    id INT AUTO_INCREMENT PRIMARY KEY,
    city ENUM('Daegu', 'Hayang') NOT NULL,          -- 지역명 (대구/하양 고정)
    nation VARCHAR(50) DEFAULT 'South Korea',       -- 국가명
    temp FLOAT,                                     -- 현재 기온
    t_min FLOAT NOT NULL,                           -- 온도 하한
    t_max FLOAT NOT NULL,                           -- 온도 상한
    hum INT,                                        -- 습도
    w_desc VARCHAR(255),                            -- 날씨 설명
    wind FLOAT,                                     -- 풍속
    rain FLOAT DEFAULT 0,                           -- 강수량
    pm INT,                                         -- 미세먼지 수치
    mask VARCHAR(100),                              -- 마스크 착용 문구
    umbrella BOOL DEFAULT FALSE,                    -- 우산 필요 여부
    outfit_txt VARCHAR(255),                        -- 옷차림 추천 문구
    time DATETIME DEFAULT CURRENT_TIMESTAMP         -- 기록 시간
);

-- 3. 대중교통 테이블 (버스/지하철 공통)
CREATE TABLE IF NOT EXISTS transport (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type ENUM('bus', 'subway') NOT NULL,            -- 교통수단 종류
    route VARCHAR(50),                              -- 버스 노선 / 지하철 호선
    station VARCHAR(255) NOT NULL,                  -- 정류장/역 이름
    arr TIME,                                       -- 도착 시간
    dep TIME,                                       -- 출발 시간
    dest VARCHAR(255),                              -- 도착지
    dir VARCHAR(100),                               -- 방향 (지하철용)
    time DATETIME DEFAULT CURRENT_TIMESTAMP         -- 저장 시간
);

-- 4. 대구 지하철 스케줄 테이블
CREATE TABLE IF NOT EXISTS subway (
    id INT AUTO_INCREMENT PRIMARY KEY,
    line ENUM('1', '2') NOT NULL,                   -- ✅ CSV에서 직접 지정된 호선
    station VARCHAR(100) NOT NULL,                  -- 역 이름
    timecode VARCHAR(10) NOT NULL,                  -- 열차 시간 코드 (ex: 1002)
    time_val TIME NOT NULL,                         -- 실제 시간 (ex: 06:13:50)
    type ENUM('도착', '출발') NOT NULL,             -- 도착/출발 구분
    day ENUM('평일', '매일') DEFAULT '매일',        -- ✅ 주말 제외
    to_dest ENUM('상', '하') DEFAULT NULL,          -- 상행/하행 방향
    ground BOOL DEFAULT FALSE,                      -- 지상 여부 (추후 사용 가능)
    updated DATETIME DEFAULT CURRENT_TIMESTAMP      -- 레코드 생성/수정 시간
);


-- 5. 루틴 테이블
CREATE TABLE IF NOT EXISTS routine (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uid BIGINT NOT NULL,                            -- 사용자 ID (FK)
    r_name VARCHAR(100) NOT NULL,                   -- 루틴 이름
    r_time TIME NOT NULL,                           -- 루틴 시간
    r_days SET('Mon','Tue','Wed','Thu','Fri','Sat','Sun') NOT NULL,  -- 요일 셋
    created DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (uid) REFERENCES users(id) ON DELETE CASCADE
);

-- 6. 일정 테이블
CREATE TABLE IF NOT EXISTS schedule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uid BIGINT NOT NULL,                            -- 사용자 ID (FK)
    title VARCHAR(255) NOT NULL,                    -- 일정 제목
    tag ENUM('#직장', '#학교', '#공부', '#운동', '#약속') NOT NULL, -- 태그 분류
    s_date DATE NOT NULL,                           -- 일정 날짜
    s_time TIME NOT NULL,                           -- 일정 시간
    alarm BOOL DEFAULT TRUE,                        -- 알림 여부 (전날 알림)
    created DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (uid) REFERENCES users(id) ON DELETE CASCADE
);

-- 7. 루틴 후보 테이블
CREATE TABLE IF NOT EXISTS routine_tmp (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uid BIGINT NOT NULL,                            -- 사용자 ID (FK)
    qTime TIME NOT NULL,                           -- 질문 시간
    qDate DATE NOT NULL,                           -- 질문 날짜
    qCnt INT DEFAULT 1,                            -- 질문 누적 횟수
    auto_routine BOOL DEFAULT FALSE,                -- 자동 루틴 등록 여부
    content VARCHAR(255),
    FOREIGN KEY (uid) REFERENCES users(id) ON DELETE CASCADE
);


-- outfit 데이터 삽입 (중복 없이 적절한 범위 지정)
INSERT INTO outfit (city, t_min, t_max, outfit_txt) VALUES ('Daegu', 28, 99, '민소매, 반팔, 반바지, 원피스');
INSERT INTO outfit (city, t_min, t_max, outfit_txt) VALUES ('Daegu', 24, 27.99, '반팔, 얇은 셔츠, 반바지, 면바지');
INSERT INTO outfit (city, t_min, t_max, outfit_txt) VALUES ('Daegu', 21, 23.99, '얇은 가디건, 긴팔, 면바지, 청바지');
INSERT INTO outfit (city, t_min, t_max, outfit_txt) VALUES ('Daegu', 17, 20.99, '얇은 니트, 맨투맨, 가디건, 청바지');
INSERT INTO outfit (city, t_min, t_max, outfit_txt) VALUES ('Daegu', 12, 16.99, '자켓, 가디건, 야상, 스타킹, 청바지, 면바지');
INSERT INTO outfit (city, t_min, t_max, outfit_txt) VALUES ('Daegu', 9, 11.99, '자켓, 트렌치코트, 야상, 니트, 청바지, 스타킹');
INSERT INTO outfit (city, t_min, t_max, outfit_txt) VALUES ('Daegu', 5, 8.99, '코트, 가죽자켓, 히트텍, 니트, 레깅스');
INSERT INTO outfit (city, t_min, t_max, outfit_txt) VALUES ('Daegu', -99, 4.99, '패딩, 두꺼운 코트, 기모제품');

INSERT INTO outfit (city, t_min, t_max, outfit_txt) VALUES ('Hayang', 28, 99, '민소매, 반팔, 반바지, 원피스');
INSERT INTO outfit (city, t_min, t_max, outfit_txt) VALUES ('Hayang', 24, 27.99, '반팔, 얇은 셔츠, 반바지, 면바지');
INSERT INTO outfit (city, t_min, t_max, outfit_txt) VALUES ('Hayang', 21, 23.99, '얇은 가디건, 긴팔, 면바지, 청바지');
INSERT INTO outfit (city, t_min, t_max, outfit_txt) VALUES ('Hayang', 17, 20.99, '얇은 니트, 맨투맨, 가디건, 청바지');
INSERT INTO outfit (city, t_min, t_max, outfit_txt) VALUES ('Hayang', 12, 16.99, '자켓, 가디건, 야상, 스타킹, 청바지, 면바지');
INSERT INTO outfit (city, t_min, t_max, outfit_txt) VALUES ('Hayang', 9, 11.99, '자켓, 트렌치코트, 야상, 니트, 청바지, 스타킹');
INSERT INTO outfit (city, t_min, t_max, outfit_txt) VALUES ('Hayang', 5, 8.99, '코트, 가죽자켓, 히트텍, 니트, 레깅스');
INSERT INTO outfit (city, t_min, t_max, outfit_txt) VALUES ('Hayang', -99, 4.99, '패딩, 두꺼운 코트, 기모제품');

-- 데이터 확인 쿼리 (예시)
SELECT * FROM outfit WHERE city = 'Daegu' AND t_min <= 27.99 AND t_max >= 26.2;

INSERT INTO outfit (city, t_min, t_max, outfit_txt) VALUES ('Daegu', 20, 25, '테스트 옷차림');
SELECT * FROM outfit;
