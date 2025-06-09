# Capstone - AI 챗봇 비서 프로젝트

개인 맞춤형 생활 지원을 위한 AI 기반 챗봇 비서 시스템입니다.  
아두이노를 이용해 움직이는 음성 챗봇 비서를 구현하였습니다.
날씨 정보, 옷차림 추천, 대중교통 안내, 길찾기, 일정 관리, 루틴 등록 등 다양한 기능을 제공합니다.

---

## 프로젝트 구성
```
Capstone/
├── flutter/ # Flutter 프론트엔드 (앱 UI)
├── spring/ # Spring 백엔드 (API 서버)
├── Database/ # 데이터베이스 CSV 및 SQL 자료, 벡터DB
└── capstone_robot/ # 라즈베리파이 음성 제어 코드
```
---

##  주요 기능

-  자연어 질의 기반 챗봇 응답 (GPT API 활용)
-  실시간 날씨 정보 + 마스크/우산/옷차림 안내
-  버스/지하철 도착 정보 제공
-  길찾기 (출발지/도착지 기반)
-  일정 등록 및 조회
-  동일 시간, 동일 태그로 3회 이상 등록한 경우 그 시간에 맞게 알림
-  음성 인식 기반 라즈베리파이 제어 (VOSK, Google STT/TTS)
-  아두이노를 이용해 바퀴가 달려 플러터로 조종하면 앞/뒤/오/왼으로 조종

---

##  주요 API 명세

| 기능       | 메서드 | 경로                            | 설명                        |
|------------|--------|---------------------------------|-----------------------------|
| 날씨 안내  | GET    | `/chat/weather?message=...`     | 실시간 날씨 및 마스크 안내 |
| 옷차림 추천| GET    | `/api/outfit/recommend`         | 기온 기반 의상 추천        |
| 일정 등록  | POST   | `/chat/schedule`                | 자연어 일정 등록            |
| 일정 조회  | GET    | `/chat/schedule/today`          | 오늘 일정 요약              |
| 버스 도착  | POST   | `/api/bus/arrival`              | 정류장+노선 기반 도착 정보 |
| 지하철 안내| POST   | `/api/subway/info`              | 지하철역+호선 기반 도착 정보 |
| 길찾기     | POST   | `/api/direction/find`           | 출발지-도착지 기반 길안내   |

---

##  기술 스택

| 파트         | 사용 기술 |
|--------------|-----------|
| 프론트엔드   | Flutter, Firebase Authentication |
| 백엔드       | Java Spring (MVC), MySQL, GPT API |
| DB           | MySQL, CSV 수집 (OpenWeather, 공공데이터) |
| 라즈베리파이 | Python, VOSK, Google STT/TTS |
| 기타         | GitHub Actions (예정), Google Calendar API (예정) |

---

## 개발 환경

- Java 17, Spring Framework
- Flutter 3.10+
- Python 3.10
- MySQL 8.0+
- putty - raspberry Pi 4
- arduino
- IntelliJ / VS Code / Android Studio

---

##  시연 영상

- [ Google Drive 시연 영상 보기](https://drive.google.com/drive/folders/1-izkUQsDigxz1Iu3l29-GDDgBEo6sWWg?usp=sharing)

---

## 민감 정보 관리

- 모든 API 키, Firebase 키 등은 `.gitignore`에 포함되어 있으며  
  `application.yml`, `*.json`, `*.py` 등 실제 파일은 업로드되지 않습니다.

