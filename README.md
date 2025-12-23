# 운명(Destiny) 💜 - 블랙프라이데이 피크 타임 백엔드 솔루션

![운명_메인_이미지](https://r2.community.canvaslms.com/texts/14420-destiny-main-concept)

---

## 💜 서비스 소개
![메인이미지.png](docs/%E1%84%86%E1%85%A6%E1%84%8B%E1%85%B5%E1%86%AB%E1%84%8B%E1%85%B5%E1%84%86%E1%85%B5%E1%84%8C%E1%85%B5.png)
> **"혼란스러운 피크 타임 속에서 정해진 질서를 만드는 것, 그것이 '운명'이 설계하는 백엔드입니다."**
>
> **운명**은 블랙프라이데이와 같은 초고부하 트래픽 상황에서도 안정적인 이커머스 경험을 제공하기 위해 설계된 MSA 기반 플랫폼입니다. 최적화된 캐싱 전략과 Saga 오케스트레이션을 통해 수만 명의 동시 접속자 앞에서도 끊김 없는 서비스 흐름을 구현했습니다. 🚀

---

## 🌸 개발 환경 및 기술 스택
- **Language**: ![Java 17](https://img.shields.io/badge/Java-17-red?style=flat-square)
- **Framework**: ![SpringBoot 3.5.8](https://img.shields.io/badge/SpringBoot-3.5.8-green?style=flat-square) ![SpringCloud](https://img.shields.io/badge/SpringCloud-MSA-6DB33F?style=flat-square)
- **Database**: ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-336791?style=flat&logo=postgresql&logoColor=white) ![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat&logo=redis&logoColor=white) ![Elasticsearch](https://img.shields.io/badge/Elasticsearch-005571?style=flat&logo=elasticsearch&logoColor=white)
- **Messaging**: ![Kafka](https://img.shields.io/badge/Kafka-231F20?style=flat&logo=apachekafka&logoColor=white)
- **Infrastructure**: ![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white) ![Docker_Compose](https://img.shields.io/badge/Docker_Compose-2496ED?style=flat&logo=docker&logoColor=white)
- **Test**: ![JUnit5](https://img.shields.io/badge/JUnit5-25A162?style=flat&logo=junit5&logoColor=white) ![Jmeter](https://img.shields.io/badge/JMeter-D22128?style=flat&logo=apachejmeter&logoColor=white)

---

## 🏛️ 아키텍쳐
![아키텍쳐.png](docs/%E1%84%8B%E1%85%A1%E1%84%8F%E1%85%B5%E1%84%90%E1%85%A6%E1%86%A8%E1%84%8E%E1%85%A7.png)

---

## 💿 컨텍스트 구조
![컨텍스트구조.png](docs/%EC%BB%A8%ED%85%8D%EC%8A%A4%ED%8A%B8%EA%B5%AC%EC%A1%B0.png)

---

## 🎯 MSA 구성 및 포트 정보
본 프로젝트는 총 14개의 마이크로서비스 및 인프라 서비스로 유기적으로 연결되어 있습니다. 💫

| 분류 | 서비스 명 | 포트 | 설명 |
|:---:|:---|:---:|:---|
| **Infra** | **Eureka Server** | `8761` | 서비스 디스커버리 및 레지스트리 |
| **Infra** | **Config Server** | `8888` | 중앙 집중식 설정 관리 |
| **Infra** | **Gateway** | `8080` | API Gateway 및 라우팅 |
| **Infra** | **Saga Orchestrator** | `8000` | 분산 트랜잭션 제어 총괄 |
| **Business** | **User_Service** | `18080` | 사용자 관리 및 JWT 인증/인가 👤 |
| **Business** | **Product_Service** | `18090` | 상품 메타데이터 관리 🛒 |
| **Business** | **Stock_Service** | `18100` | CQRS 기반 재고 예약 관리 📊 |
| **Business** | **Order_Service** | `18110` | 주문 생성 및 흐름 관리 📦 |
| **Business** | **Brand_Service** | `18120` | 입점 브랜드 정보 관리 🏢 |
| **Business** | **Payment_Service** | `18130` | Multi-PG(Toss, PortOne, Bootpay) 결제 💳 |
| **Business** | **Notification_Service** | `18140` | 실시간 알림 전송 (Slack 연동) 🔔 |
| **Business** | **Coupon_Service** | `18150` | 선착순 쿠폰 발급 및 동시성 제어 🎫 |
| **Business** | **Review_Service** | `18160` | 상품 리뷰 및 평점 관리 ⭐ |
| **Business** | **Cart_Service** | `18170` | 장바구니 서비스 🛒 |

---

## 🛠 주요 기능 요약
- 🎡 **Saga 오케스트레이션**: Kafka 기반 이벤트 드리븐 설계로 분산 환경의 데이터 일관성 유지
- 🎫 **선착순 쿠폰 발급**: 원자적 DB UPDATE를 통한 동시성 제어 및 Redis 캐싱 성능 최적화
- 📊 **재고 예약 시스템**: 단순 차감 방식 탈피, 예약 방식을 도입하여 결제 중 재고 정합성 완벽 보장
- 💳 **고가용성 결제**: 토스페이먼츠, 포트원, 부트페이 3중 연동으로 장애 발생 시 자동 Failover 구현
- 🔔 **알림 라우팅**: Redis와 Retry 로직을 활용한 안정적인 알림 전송 시스템

---

## ▶️ 실행 방법

### 1. 인프라 실행 (Database & Redis)
데이터베이스 환경 구축을 위해 `database` 폴더로 이동하여 실행합니다. 💾
```bash
cd database
docker-compose up -d
```

### 2. 서비스 실행 환경 정리
중복 실행된 서버 프로세스를 정리하기 위해 최상위 디렉토리의 스크립트를 사용합니다. ✨
```bash
# 최상단(destiny) 디렉토리에서 실행
chmod +x kill_duplicate_servers.sh
./kill_duplicate_servers.sh
```

---

## 🔗 관련 문서 및 링크

> **프로젝트의 상세한 기록과 설계 과정을 아래 링크에서 확인하실 수 있습니다.** 💫

| 📂 분류 | 📄 문서 명                       | 🔗 바로가기 |
|:---:|:------------------------------|:---:|
| **Notion** | **운명(Destiny) 프로젝트 기획서 & 일지** | [👉 노션 링크 바로가기](https://www.notion.so/teamsparta/16-2b52dc3ef514801eaaded4347dac0360) |
| **Canva** | **발표 자료**                     | [👉 캔바 링크 바로가기](https://www.canva.com/design/DAG8Mk-yUlg/UHkLMyrHqc4d0qAWl49KzA/edit) |

---