# 🏃PaceMaker🏃

<b> 개인화 맞춤 러닝 코칭 서비스 페이스 메이커입니다.

<b> LLM을 활용하여 개인 정보에 맞게 러닝 플랜을 생성 해줍니다.

<b> LLM과 TTS를 활용해 실시간 AI 러닝 코치가 러닝 피드백을 생성합니다.

<b> 러닝 완료 후 LLM이 러닝 결과와 AI 코치의 러닝 후기를 생성합니다.

## 팀원 구성

<div align="center">

|               **김재훈**                |                 **김연욱**                 |                 **허유정**                  |               **박준식**                |                **정다혜**                |                 **최승준**                 |
|:------------------------------------:|:---------------------------------------:|:----------------------------------------:|:------------------------------------:|:-------------------------------------:|:---------------------------------------:|
|                <b>백엔드                |                 <b>백엔드                  |                  <b>백엔드                  |               <b>안드로이드               |               <b>안드로이드                |                <b>안드로이드                 
</div>


## 1. 개발 환경

- Front-end: Android Studio, Kotlin, MVVM
- Back-end : IntelliJ, Spring boot
- GPU Server: Python, FastAPI
- 버전 및 이슈관리 : Gitlab, Jira
- 협업 툴 : Matter Most, Discord, Notion
- 서비스 배포 환경 : AWS EC2, Docker, Jenkins
- 디자인 : [Figma](https://www.figma.com/design/sMLjgI5OwHFt8tIS5ZyDBD/Yoga-Navi?node-id=0-1&t=nj03qnrp0J5vai0o-0)
- [커밋 컨벤션](https://aluminum-timpani-a63.notion.site/a1fab9b04fb24b3ebaf4034745971f3d?pvs=74)
- [코드 컨벤션](https://google.github.io/styleguide/javaguide.html)
<br>

## 2. 채택한 개발 기술과 브랜치 전략

### Android

- Hilt
	- 의존성 주입을 통해 객체의 생성을 관리함으로써 코드의 모듈화와 재사용성을 극대화할 수 있습니다.
- Retrofit2
	- 백앤드 서버와의 HTTP 요청을 인터페이스 메서드로 매핑하여, 네트워크 요청 로직을 직관적이고 쉽게 작성할 수 있습니다.
- Jetpack Navigation
	- Navigation Graph를 통해 앱 내 모든 탐색을 하나의 그래프로 관리할 수 있게 해주며, 이를 통해 UI 흐름을 시각적으로 설계할 수 있습니다.
- Clean Architecture
	- 관심사 분리와 개발의 유지 보수를 위하여 클린 아키텍쳐를 도입하였습니다.


### Backend

 - JPA 
    - 객체 지향적인 방식으로 데이터베이스를 조작할 수 있어, 사용자 정보나 강의 정보 등을 효율적으로 관리할 수 있습니다.
    - 복잡한 SQL 쿼리 없이도 간단하게 데이터를 조회하고 조작할 수 있습니다.
    - 엔티티 간의 관계를 쉽게 표현하고 관리할 수 있어, 강의와 사용자 간의 관계 등을 효과적으로 모델링할 수 있습니다.

## 4. 기능

## 5. 트러블 슈팅

- [서버보안](https://aluminum-timpani-a63.notion.site/b4014a88311f42d882789f39422f3ef3?pvs=74)

- [프롬프트 엔지니어링](https://aluminum-timpani-a63.notion.site/e67849e6e25445ec9f8633f867fcfa0b?pvs=74)


