# 1단계: 빌드 환경
FROM openjdk:21-jdk-slim AS builder

# 애플리케이션 소스 코드 복사
COPY . /app
WORKDIR /app

# Gradle Wrapper에 실행 권한 부여
RUN chmod 755 gradlew

# Gradle Wrapper를 사용하여 애플리케이션 빌드
# RUN ./gradlew build
RUN ./gradlew clean build -x test

# 2단계: 실행 환경
FROM openjdk:21-slim

# 빌드된 JAR 파일을 실행 환경으로 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# EXPOSE는 문서화로 있어도 없어도 상관 없음
# EXPOSE 8080

# JAR 파일을 실행
CMD ["java", "-jar", "app.jar"]

