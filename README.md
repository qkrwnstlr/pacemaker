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

## 4. 설치 방법

## 앱 설치
### 모바일 앱 설치
1. 모바일에서 위의 파일을 다운받고, 압축을 해제해 주세요.
    - 예시
          
        ![title](/image/install_app_1.png)
        ![title](/image/install_app_2.png)

2. app-debug.apk 파일을 설치해 주세요.
    - 예시
          
        ![title](/image/install_app_3.png) 
        ![title](/image/install_app_4.png) 
        
3. 설치가 완료된 후 앱의 모든 권한을 허용합니다. 권한은 총 2개 입니다.
    - 예시
          
        ![title](/image/install_app_5.png) 
        ![title](/image/install_app_6.png) 
        
4. 구글 로그인 후 앱을 사용할 수 있습니다.
    - 예시
          
        ![title](/image/install_app_7.png) 
        ![title](/image/install_app_8.png) 
        

### 워치 앱 설치

1. 모바일에서 위의 파일을 다운받고, 압축을 해제합니다.
    - 예시
          
        ![title](/image/install_watch_1.png) 
        ![title](/image/install_watch_2.png) 
        
2. 그 후 아래 링크를 따라 워치와 폰을 연결하고,  wath-debug.apk를 설치해 주세요.
    [워치 앱 설치](https://menofpassion.tistory.com/332)
    
3. 설치가 완료된 후 앱의 모든 권한을 허용합니다. 권한은 총 4개 입니다.
    - 예시
          
        ![title](/image/install_watch_3.png) 
        ![title](/image/install_watch_4.png) 
        ![title](/image/install_watch_5.png) 
        ![title](/image/install_watch_6.png) 
        ![title](/image/install_watch_7.png) 
        ![title](/image/install_watch_8.png) 
        ![title](/image/install_watch_9.png) 

### 주의 사항
1. 실시간 러닝은 워치와 모바일이 서로 연결되어있지 않으면 실행이 되지 않습니다.
2. 반드시 모든 권한이 있어야 실시간 러닝이 정상적으로 동작합니다.
3. 헬스 커넥트의 경우 권한 재설정이 필요하시면 마이페이지의 권한 설정에서 하실 수 있습니다.
4. 긴 시간(현재는 30분까지) 사용하면 워치에서 ANR이 발생할 수 있습니다.
5. 워치 화면이 어두워지면(AOD) 센서 데이터가 수집되지 않습니다. 기본적으로는 손으로 워치 화면을 덮지 않으면 항상 화면이 켜지는 것으로 설정되어 있습니다. 혹시 워치 화면이 어두워지면 앱을 왼쪽에서 오른쪽으로 슬라이드(뒤로 가기) 하여 종료한 후 재 실행하시면 됩니다.

## 5. 트러블 슈팅

- [서버보안](https://aluminum-timpani-a63.notion.site/b4014a88311f42d882789f39422f3ef3?pvs=74)

- [프롬프트 엔지니어링](https://aluminum-timpani-a63.notion.site/e67849e6e25445ec9f8633f867fcfa0b?pvs=74)


