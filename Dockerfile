# 使用 Maven 官方映像作為基礎映像
FROM maven:3.8.4-openjdk-17 AS build

# 設定工作目錄
WORKDIR /app

# 複製專案檔案到容器中
COPY . /app

# 執行 Maven 指令
RUN mvn clean package

# # 使用官方 OpenJDK 映像作為基礎映像
# FROM openjdk:17

FROM debian:bullseye-slim

# 安裝 OpenJDK 17 和 Python 3
RUN apt-get update && apt-get install -y openjdk-17-jdk python3

# 複製建置好的 jar 檔案
COPY --from=build /app/target/leetcodeCloneAPI-0.0.1-SNAPSHOT.jar /app/

# 開啟 Java 應用程式
CMD ["java", "-jar", "/app/leetcodeCloneAPI-0.0.1-SNAPSHOT.jar"]
