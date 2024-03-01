# 예약마켓(yeyak-market) 프로젝트

## 🚀 프로젝트 소개

예약마켓(yeyak-market)은 소셜 미디어 기능과 결합된 혁신적인 eCommerce 플랫폼입니다. 사용자는 기본 기능을 활용해 소셜 미디어 활동을 즐기며, 동시에 다양한 상품을 결제하고 예약 구매할 수 있는 통합 서비스를 경험할 수 있습니다. 이 서비스는 사용자 중심의 웹 사이트로, 소통하면서 원하는 상품을 쉽고 편리하게 예약 구매할 수 있는 환경을 제공합니다.

### 📅 프로젝트 실행 기간

2024년 1월 24일부터 2024년 2월 29일까지

### 👤 프로젝트 호스팅

[최재혁](https://github.com/jaehyuuk)

---

## 📜 프로젝트 설명

예약마켓은 사용자들이 상품 정보를 공유하고, 피드백을 주고받으며, 상품 예약 구매까지 할 수 있는 플랫폼입니다. 사용자들은 상품에 대한 실시간 정보와 리뷰를 소셜 미디어 형식으로 접하며, 이를 기반으로 정보에 입각한 구매 결정을 할 수 있습니다.  

## 💻 Docker-Compose 실행 명령어

```bash
docker-compose up -d
```

## 🗂 ERD

![yeyak-market ERD](https://github.com/jaehyuuk/yeyak-market/assets/108051201/5373a4f6-07dd-4f70-a600-e36fa5b9e406)

---

## 📚 API 문서

### [yeyak-market API (Postman)](https://documenter.getpostman.com/view/29397283/2sA2r9V3S6)

---

## 🌟 주요 기능

- **유저 관리**: 사용자는 프로필 관리와 팔로우 기능을 통해 소통의 폭을 넓힐 수 있습니다.
- **상품 정보 공유 및 리뷰**: 상품에 대한 정보 게시, 댓글 작성 및 좋아요를 통해 다른 사용자와 경험을 공유할 수 있습니다.
- **예약 구매 기능**: 다양한 상품을 예약 구매할 수 있는 기능을 제공합니다.
- **결제 시스템 통합**: 안전하고 편리한 결제 시스템을 통해 사용자는 손쉽게 상품을 구매할 수 있습니다.

---

## 🛠 기술 스택

### 🖥 Backend

- Spring Boot
- Spring Security
- JPA / Hibernate
- MySQL
- Redis
- Docker / Docker Compose

---

## 🚧 프로젝트 아키텍처

프로젝트의 전체적인 아키텍처는 마이크로서비스 아키텍처를 따르며, 각 기능별로 분리된 서비스들이 REST API를 통해 서로 통신합니다. Docker를 사용하여 각 서비스들을 컨테이너화하였으며, MySQL과 Redis를 사용하여 데이터의 지속성을 관리합니다.

---

## 📈 성능 최적화 및 트러블슈팅

프로젝트 개발 과정에서 발생한 주요 성능 최적화 작업과 트러블슈팅 사례를 공유합니다. 이는 프로젝트 진행 중 직면한 기술적 문제를 해결하고, 프로젝트의 전반적인 성능을 향상시킨 경험을 공유하는 자료입니다.

### 성능 최적화 사례

- **MSA(MicroService Architecture) 도입**: 서비스의 확장성과 유지보수성을 향상시키기 위해 마이크로서비스 아키텍처를 도입했습니다. [자세히 보기](https://jaehyuuk.tistory.com/161)
- **API Gateway 추가**: 시스템의 안정성과 서비스 관리의 용이성을 위해 API Gateway를 추가했습니다. [자세히 보기](https://jaehyuuk.tistory.com/165)
- **실시간 재고 관리 서비스 추가**: 대규모 트래픽 처리를 위한 실시간 재고 관리 서비스를 추가했습니다. [자세히 보기](https://jaehyuuk.tistory.com/180)

### 트러블슈팅 경험

- **Redis와 JWT 토큰 만료 시간 동기화 문제**: Redis에 저장된 JWT 토큰과 실제 만료 시간 사이의 동기화 문제를 해결했습니다. [자세히 보기](https://jaehyuuk.tistory.com/160)
- **Rest API 응답 데이터의 Null 값 문제 해결**: 선택적 필드를 포함하는 API 응답에서 Null 값 처리 문제를 해결했습니다. [자세히 보기](https://jaehyuuk.tistory.com/163)
- **예약 구매 상품의 시간 제한 처리**: 예약 구매 상품에 대한 시간 제한 처리 로직을 개선했습니다. [자세히 보기](https://jaehyuuk.tistory.com/172)

전체 프로젝트 관련 글 및 기술적 고민은 [블로그](https://jaehyuuk.tistory.com/category/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8%20%28Java%29/%EC%98%88%EC%95%BD%EB%A7%88%EC%BC%93)에서 확인할 수 있습니다.

---

## 🔗 유용한 링크

- **프로젝트 GitHub 저장소**: [yeyak-market GitHub](https://github.com/jaehyuuk/yeyak-market)
- **프로젝트 문서 및 기술적 의사결정 자료**: [의사결정 문서 (yeyak-market)](https://drive.google.com/file/d/11zDsGOgyGlBBeZabj4n0ZMt-inn5qnrR/view?usp=sharing)
